package com.linpinger.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;


public class ToolBookJava {

	public static String getFullURL(String baseURL, String subURL) { // ��ȡ����·��
		String allURL = "";
		try {
			allURL = (new URL(new URL(baseURL), subURL)).toString();
		} catch (MalformedURLException e) {
			System.err.println(e.toString());
		}
		return allURL;
	}

	//��ȡ�����ļ���ת�浽outPath�У�outPath��Ҫ���ļ���׺���������ļ���С
	public static int saveHTTPFile(String inURL, String outPath) {
		int oLen = 0 ;
		File toFile = new File(outPath);
		if (toFile.exists())
			toFile.delete();
		try {
			FileOutputStream fos = new FileOutputStream(toFile);
			byte[] hb = downHTTP(inURL, "GET", null);
			oLen = hb.length ;
			fos.write(hb);
			fos.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return oLen;
	}

	public static String downhtml(String inURL) {
		return downhtml(inURL, "");
	}

	public static String downhtml(String inURL, String pageCharSet) {
		return downhtml(inURL, pageCharSet, "GET");
	}

	public static String downhtml(String inURL, String pageCharSet, String PostData) {
		return downhtml(inURL, pageCharSet, PostData, null);
	}

	public static String downhtml(String inURL, String pageCharSet, String PostData, String iCookie) {
		byte[] buf = downHTTP(inURL, PostData, iCookie);
		if (buf == null)
			return "";
		try {
			String html = "";
			if (pageCharSet == "") {
				html = new String(buf, "gbk");
				if (html.matches("(?smi).*<meta[^>]*charset=[\"]?(utf8|utf-8)[\"]?.*")) // ̽�����
					html = new String(buf, "utf-8");
			} else {
				html = new String(buf, pageCharSet);
			}
			return html;
		} catch (Exception e) { // ���� ������
			System.err.println(e.toString());
			return "";
		}
	}

	public static byte[] downHTTP(String inURL, String PostData, String iCookie) {
		byte[] buf = null;
		try {
			URL url = new URL(inURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if ("GET" != PostData) {
//				System.out.println("I am Posting ...");
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			}

			if ( null != iCookie )
				conn.setRequestProperty("Cookie", iCookie);

			if (inURL.contains(".13xs."))
				conn.setRequestProperty("User-Agent", "ZhuiShuShenQi/3.26"); // 2015-10-27: qqxsʹ�ü��ٱ�����Java��ͷ�ᱻ��г
			else
				conn.setRequestProperty("User-Agent", "ZhuiShuShenQi/3.26 Java/1.6.0_55"); // Android�Դ�ͷ����IE8ͷ���ᵼ��yahoo�����������Ϊ׷������

			if (!inURL.contains("files.qidian.com")) // 2015-4-16: qidian txt ʹ��cdn���٣����ͷ����gzip�ͻ᷵�ش����gz����
				conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			else
				conn.setRequestProperty("Accept-Encoding", "*"); // Android ���Զ�����gzip����ӣ�ʹ��*����֮�����CDN������ȷ������

			conn.setRequestProperty("Accept", "*/*");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);	// ��ȡ��ʱ5s
			conn.setUseCaches(false);	 // Cache-Control: no-cache	 Pragma: no-cache

			conn.connect();  // ��ʼ����
			if ("GET" != PostData) {  // ����PostData
				conn.getOutputStream().write(PostData.getBytes("UTF-8"));
				conn.getOutputStream().flush();
				conn.getOutputStream().close();
			}
			
			// ����жϷ���״̬���������жϴ��󣬽���򵥵�����connect���У���������������
			/*
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
//				System.out.println("  Error Happend, responseCode: " + responseCode + "  URL: " + inURL);
				return buf;
			}
			*/

			// �жϷ��ص��Ƿ���gzip����
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int len = 0;
			// ���ص��ֶ�: Content-Encoding: gzip/null �ж��Ƿ���gzip
			if (null == conn.getContentEncoding()) { // ����gzip����
				InputStream in = conn.getInputStream();
				while ((len = in.read(buffer)) != -1)
					outStream.write(buffer, 0, len);
				in.close();
			} else { // gzip ѹ������
				InputStream in = conn.getInputStream();
				GZIPInputStream gzin = new GZIPInputStream(in);
				while ((len = gzin.read(buffer)) != -1)
					outStream.write(buffer, 0, len);
				gzin.close();
				in.close();
			}

			buf = outStream.toByteArray();
			outStream.close();
		} catch (Exception e) { // ���� ������
			System.err.println(e.toString());
		}
		return buf;
	}


} // �����
