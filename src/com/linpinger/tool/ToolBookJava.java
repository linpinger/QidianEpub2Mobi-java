package com.linpinger.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.linpinger.novel.NV;

public class ToolBookJava {

/*
	public static String simplifyDelList2(String DelList) { // ���� DelList
		int qi = 0;
		int zhi = 0;
		if (DelList.contains("��ֹ=")) {
			Matcher mat = Pattern.compile("(?i)��ֹ=([0-9\\-]+),([0-9\\-]+)").matcher(DelList);
			while (mat.find()) {
				qi = Integer.valueOf(mat.group(1));
				zhi = Integer.valueOf(mat.group(2));
			}
		}
		DelList = DelList.replace("\r", "").replace("\n\n", "\n");
		String[] xx = DelList.split("\n");
		if (xx.length < 15) {
			return DelList;
		}
		int MaxLineCount = xx.length - 9;
	
		StringBuffer newList = new StringBuffer(1024);
		for (int i = 0; i < 9; i++) {
			newList.append(xx[MaxLineCount + i]).append("\n");
		}
		if (zhi > 0) {
			return "��ֹ=" + qi + "," + String.valueOf(zhi + MaxLineCount - 1) + "\n" + newList.toString();
		} else {
			return "��ֹ=" + qi + "," + String.valueOf(zhi + MaxLineCount) + "\n" + newList.toString();
		}
	}
*/
	

/*
	public static List compare2GetNewPages2(List<Map<String, Object>> xx, String existList) {
		existList = existList.toLowerCase();
		int xxSize = xx.size();
		if (existList.contains("��ֹ=")) { // ���� ��ֹ ����һ�� xx
			Matcher mat = Pattern.compile("(?i)��ֹ=([0-9-]+),([0-9-]+)").matcher(existList);
			int qz_1 = 0;
			int qz_2 = 0;
			while (mat.find()) {
				qz_1 = Integer.valueOf(mat.group(1));
				qz_2 = Integer.valueOf(mat.group(2));
			}

			ArrayList<Map<String, Object>> nXX = new ArrayList<Map<String, Object>>(30);
			// ����ĳ�ʼֵ���ж�˳����ò�Ҫ���䶯
			int sIdx = 0;
			int eIdx = xxSize;
			int leftIdx = 0;
			if (qz_2 > 0) {
				sIdx = qz_2;
				leftIdx = eIdx - sIdx;
			}
			if (qz_1 < 0) {
				eIdx = eIdx + qz_1;
				leftIdx = leftIdx + qz_1;
			}
			if (leftIdx > 0) {
				int nSIdx = 0;
				for (int i = 0; i < leftIdx; i++) {
					nSIdx = sIdx + i;
					nXX.add(xx.get(nSIdx));
				}
				xx = nXX;
			} else { // �½�����Ϊ��
				if (55 == xxSize) {
					String jj[] = existList.split("\n");
					if (jj.length > 2) { // ��ȡ��ɾ����¼�е�һ��֮��ļ�¼��������½�>55���ܻᱯ��
						String sToBeComp = jj[jj.length - 2];
						ArrayList<Map<String, Object>> nX2 = new ArrayList<Map<String, Object>>(30);
						Iterator itr = xx.iterator();
						String nowurl = "";
						boolean bFillArray = false;
						while (itr.hasNext()) {
							HashMap<String, Object> mm = (HashMap<String, Object>) itr.next();
							nowurl = mm.get("url").toString().toLowerCase();
							if (sToBeComp.contains(nowurl)) {
								bFillArray = true;
								nX2.add(mm);
							} else {
								if (bFillArray) {
									nX2.add(mm);
								}
							}
						}
						xx = nX2;
					} else {
						System.out.println("error: jj < 2 : " + jj.length);
					}
				} else {  // ����ŵĴ�����û�����½ڵĴ�����
					return new ArrayList<HashMap<String, Object>>();
				}
			}
		}

		// �Ƚϵõ����½�
		String nowURL;
		ArrayList<HashMap<String, Object>> newPages = new ArrayList<HashMap<String, Object>>();
		Iterator<Map<String, Object>> itr = xx.iterator();
		while (itr.hasNext()) {
			HashMap<String, Object> mm = (HashMap<String, Object>) itr.next();
			nowURL = (String) mm.get("url");
			if (!existList.contains(nowURL.toLowerCase() + "|")) { // ���½�
				newPages.add(mm);
			}
		}
		return newPages;
	}
*/
	public static String simplifyDelList(String DelList) { // ���� DelList
		int nLastItem = 9;
		DelList = DelList.replace("\r", "").replace("\n\n", "\n");
		String[] xx = DelList.split("\n");
		if (xx.length < (nLastItem + 2)) {
			return DelList;
		}
		int MaxLineCount = xx.length - nLastItem;

		StringBuilder newList = new StringBuilder(4096);
		for (int i = 0; i < 9; i++) {
			newList.append(xx[MaxLineCount + i]).append("\n");
		}
		return newList.toString();
	}

	// ȡ��������Ԫ�أ��������������
	public static List<Map<String, Object>> getLastNPage(List<Map<String, Object>> inArrayList, int lastNpage) {
		int aSize = inArrayList.size();
		if (aSize <= lastNpage || lastNpage <= 0) {
			return inArrayList;
		}
		List<Map<String, Object>> outList = new ArrayList<Map<String, Object>>(100);
		for (int nowIdx = aSize - lastNpage; nowIdx < aSize; nowIdx++) {
			outList.add((HashMap<String, Object>) (inArrayList.get(nowIdx)));
		}
		return outList;
	}

	public static List<Map<String, Object>> compare2GetNewPages(List<Map<String, Object>> listURLName, String DelList) {
		int linkSize = listURLName.size();
		if ( 0 == linkSize ) // aHTMLΪ��(������ҳ����������)
			return listURLName ;
		if ( ! DelList.contains("|") ) // ��DelListΪ�գ�����ԭ����
			return listURLName ;
		
		// ��ȡ DelList ��һ�е� URL : BaseLineURL
		int fFF = DelList.indexOf("|");
		String BaseLineURL = DelList.substring(1 + DelList.lastIndexOf("\n", fFF), fFF);
		
		// �鵽����aHTML�е���BaseLineURL���кţ���ɾ��1�����кŵ�����Ԫ��
		int EndIdx = 0 ;
		String nowURL ;
		for (int nowIdx = 0; nowIdx < linkSize; nowIdx++) {
			nowURL = listURLName.get(nowIdx).get(NV.PageURL).toString();
			if ( BaseLineURL.equalsIgnoreCase(nowURL) ) {
				EndIdx = nowIdx ;
				break ;
			}
		}
		for (int nowIdx = EndIdx; nowIdx >= 0; nowIdx--) {
			listURLName.remove(nowIdx);
		}
		linkSize = listURLName.size();
		
		// �Ա�ʣ���aHTML��DelList���õ��µ�aNewRet������
		List<Map<String, Object>> aNewRet = new ArrayList<Map<String, Object>>(30);
		for (int nowIdx = 0; nowIdx < linkSize; nowIdx++) {
			nowURL = listURLName.get(nowIdx).get(NV.PageURL).toString();
			if ( ! DelList.contains("\n" + nowURL + "|") )
				aNewRet.add(listURLName.get(nowIdx));
		}
		
		return aNewRet ;
	}


/*
	public static String simplifyDelList2(String DelList) { // ���� DelList
		int qi = 0;
		int zhi = 0;
		if (DelList.contains("��ֹ=")) {
			Matcher mat = Pattern.compile("(?i)��ֹ=([0-9\\-]+),([0-9\\-]+)").matcher(DelList);
			while (mat.find()) {
				qi = Integer.valueOf(mat.group(1));
				zhi = Integer.valueOf(mat.group(2));
			}
		}
		DelList = DelList.replace("\r", "").replace("\n\n", "\n");
		String[] xx = DelList.split("\n");
		if (xx.length < 15) {
			return DelList;
		}
		int MaxLineCount = xx.length - 9;

		StringBuffer newList = new StringBuffer(1024);
		for (int i = 0; i < 9; i++) {
			newList.append(xx[MaxLineCount + i]).append("\n");
		}
		if (zhi > 0) {
			return "��ֹ=" + qi + "," + String.valueOf(zhi + MaxLineCount - 1) + "\n" + newList.toString();
		} else {
			return "��ֹ=" + qi + "," + String.valueOf(zhi + MaxLineCount) + "\n" + newList.toString();
		}
	}
*/

	public static List<Map<String, Object>> getSearchEngineHref(String html, String KeyWord) { // String KeyWord = "����Ѫ��" ;
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(64);
		Map<String, Object> item;

		html = html.replace("\t", "");
		html = html.replace("\r", "");
		html = html.replace("\n", "");
		html = html.replaceAll("(?i)<!--[^>]+-->", "");
		html = html.replace("<em>", "");
		html = html.replace("</em>", "");
		html = html.replace("<b>", "");
		html = html.replace("</b>", "");
		html = html.replace("<strong>", "");
		html = html.replace("</strong>", "");

		// ��ȡ���� ������ṹ��
		Matcher mat = Pattern.compile("(?smi)href *= *[\"']?([^>\"']+)[\"']?[^>]*> *([^<]+)<").matcher(html);
		while (mat.find()) {
			if (2 == mat.groupCount()) {
				if (mat.group(1).length() < 5)
					continue;
				if (!mat.group(1).startsWith("http"))
					continue;
				if (mat.group(1).contains("www.sogou.com/web"))
					continue;
				if (!mat.group(2).contains(KeyWord))
					continue;

				item = new HashMap<String, Object>(2);
				item.put(NV.BookURL, mat.group(1));
				item.put(NV.BookName, mat.group(2));
				data.add(item);
			}
		}

		return data;
	}

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
	
	// Wget Cookie תΪHTTPͷ��Cookie�ֶ�
	public static String cookie2Field(String iCookie) {
		String oStr = "" ;
		Matcher mat = Pattern.compile("(?smi)\t[0-9]*\t([^\t]*)\t([^\r\n]*)").matcher(iCookie);
		while (mat.find()) {
			oStr = oStr + mat.group(1) + "=" + mat.group(2) + "; " ;
//			System.out.println(mat.group(1) + "=" + mat.group(2));
		}
		return oStr ;
	}

	/**
	 * �ϴ��ļ�
	 * @param urlStr
	 * @param fileMap
	 * @return
	 */
	// String filepath = "Q:\\zPrj\\fb.zip";
	// String urlStr = "http://linpinger.eicp.net:58080/cgi-bin/ff.lua";
	// Map<String, String> textMap = new HashMap<String, String>();
	// textMap.put("name", "testname");
	// Map<String, String> fileMap = new HashMap<String, String>();
	// fileMap.put("f", filepath);
	// String ret = formUpload(urlStr, fileMap);
	// System.out.println(ret);
	public static String formUpload(String urlStr, Map<String, String> fileMap) {
		// http://blog.csdn.net/wangpeng047/article/details/38303865
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "---------------------------123821742118716"; //boundary����requestͷ���ϴ��ļ����ݵķָ���
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
//			Map<String, String> textMap = null;
//			if (textMap != null) {
//				StringBuffer strBuf = new StringBuffer();
//				Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();
//				while (iter.hasNext()) {
//					Map.Entry<String, String> entry = iter.next();
//					String inputName = (String) entry.getKey();
//					String inputValue = (String) entry.getValue();
//					if (inputValue == null) {
//						continue;
//					}
//					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
//					strBuf.append(inputValue);
//				}
//				out.write(strBuf.toString().getBytes());
//			}

			// file	
			if (fileMap != null) {
				Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null)
						continue;
					File file = new File(inputValue);
					String filename = file.getName();

					StringBuffer strBuf = new StringBuffer();
					//strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("--").append(BOUNDARY).append("\r\n");  // ��һ��\n��ʹnanohttpd��������
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
					strBuf.append("Content-Type: application/octet-stream\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1)
						out.write(bufferOut, 0, bytes);
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// ��ȡ��������
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null)
				strBuf.append(line).append("\n");
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			System.err.println("����POST�������" + urlStr + "\n" + e.toString());
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

} // �����
