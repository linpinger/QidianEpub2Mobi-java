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

	public static String getFullURL(String baseURL, String subURL) { // 获取完整路径
		String allURL = "";
		try {
			allURL = (new URL(new URL(baseURL), subURL)).toString();
		} catch (MalformedURLException e) {
			System.err.println(e.toString());
		}
		return allURL;
	}

	//获取网络文件，转存到outPath中，outPath需要带文件后缀名，返回文件大小
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
				if (html.matches("(?smi).*<meta[^>]*charset=[\"]?(utf8|utf-8)[\"]?.*")) // 探测编码
					html = new String(buf, "utf-8");
			} else {
				html = new String(buf, pageCharSet);
			}
			return html;
		} catch (Exception e) { // 错误 是神马
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
				conn.setRequestProperty("User-Agent", "ZhuiShuShenQi/3.26"); // 2015-10-27: qqxs使用加速宝，带Java的头会被和谐
			else
				conn.setRequestProperty("User-Agent", "ZhuiShuShenQi/3.26 Java/1.6.0_55"); // Android自带头部和IE8头部会导致yahoo搜索结果链接为追踪链接

			if (!inURL.contains("files.qidian.com")) // 2015-4-16: qidian txt 使用cdn加速，如果头里有gzip就会返回错误的gz数据
				conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			else
				conn.setRequestProperty("Accept-Encoding", "*"); // Android 会自动加上gzip，真坑，使用*覆盖之，起点CDN就能正确处理了

			conn.setRequestProperty("Accept", "*/*");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);	// 读取超时5s
			conn.setUseCaches(false);	 // Cache-Control: no-cache	 Pragma: no-cache

			conn.connect();  // 开始连接
			if ("GET" != PostData) {  // 发送PostData
				conn.getOutputStream().write(PostData.getBytes("UTF-8"));
				conn.getOutputStream().flush();
				conn.getOutputStream().close();
			}
			
			// 这个判断返回状态，本来想判断错误，结果简单的重新connect不行，不如重新来过吧
			/*
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
//				System.out.println("  Error Happend, responseCode: " + responseCode + "  URL: " + inURL);
				return buf;
			}
			*/

			// 判断返回的是否是gzip数据
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int len = 0;
			// 返回的字段: Content-Encoding: gzip/null 判断是否是gzip
			if (null == conn.getContentEncoding()) { // 不是gzip数据
				InputStream in = conn.getInputStream();
				while ((len = in.read(buffer)) != -1)
					outStream.write(buffer, 0, len);
				in.close();
			} else { // gzip 压缩处理
				InputStream in = conn.getInputStream();
				GZIPInputStream gzin = new GZIPInputStream(in);
				while ((len = gzin.read(buffer)) != -1)
					outStream.write(buffer, 0, len);
				gzin.close();
				in.close();
			}

			buf = outStream.toByteArray();
			outStream.close();
		} catch (Exception e) { // 错误 是神马
			System.err.println(e.toString());
		}
		return buf;
	}


} // 类结束
