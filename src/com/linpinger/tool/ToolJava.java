package com.linpinger.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolJava {

	// { ͨ���ı���ȡ��д��
	// ����ʹ�������ȡ�ı�����㣬������С���Ե���һ���Դﵽ��õ��ٶ�
	public static String readText(String filePath, String inFileEnCoding) {
		// Ϊ���̰߳�ȫ�������滻StringBuilder Ϊ StringBuffer
		StringBuilder retStr = new StringBuilder(174080);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), inFileEnCoding));

			char[] chars = new char[4096]; // �����С��Ӱ���ȡ�ٶ�
			int length = 0;
			while ((length = br.read(chars)) > 0) {
				retStr.append(chars, 0, length);
			}
/*
			// �������Ч���Եͣ������Կ��ƻ��з�
			String line = null;
			while ((line = br.readLine()) != null) {
			retStr.append(line).append("\n");
			}
*/
			br.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return retStr.toString();
	}

	public static void writeText(String iUtf8Str, String filePath) {
		writeText(iUtf8Str, filePath, "UTF-8");
	}
	// д��ָ�����룬�ٶȿ��
	public static void writeText(String iStr, String filePath, String oFileEncoding) {
		boolean bAppend = false;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, bAppend), oFileEncoding));
			bw.write(iStr);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/*
	// ������ڶ����и���ı��ϣ��Ѿ���importQidianTxt��ʹ��
	public static String readTextAndSplit(String filePath, String inFileEnCoding) {
		StringBuilder retStr = new StringBuilder(174080);
		StringBuilder chunkStr = new StringBuilder(65536);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), inFileEnCoding));
			String line = null;
			int chunkLen = 0;
			while ((line = br.readLine()) != null) {
				chunkLen = chunkStr.length();
				if ( chunkLen > 3000 && ( line.length() == 0 || chunkLen > 6000 || line.startsWith("��") || line.contains("��") || line.contains("��") || line.contains("��") ) ) {
					retStr.append(chunkStr).append("\n##################################################\n\n");
					chunkStr = new StringBuilder(65536);
				}
				chunkStr.append(line).append("\n");
			}
			if ( chunkStr.length() > 0 )
				retStr.append(chunkStr).append("\n#####LAST###########\n\n");
			br.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return retStr.toString();
	}
	*/

	public static String detectTxtEncoding(String txtPath) { // �²������ı����� ����: "GBK" �� "UTF-8"
		byte[] b = new byte[256]; // ��ȡ��ô���ֽڣ������ô���ֽڶ���Ӣ���Ǿͱ�����
		int loopTimes = 256 - 6 ; // ѭ������ = �ֽ��� - 6 ����Խ��
		try {
			FileInputStream in= new FileInputStream(txtPath);
			in.read(b);
			in.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		boolean isGBK = false ;
	if (b[0] == -17 && b[1] == -69 && b[2] == -65) { // UTF-8 BOM : EF BB BF
		isGBK = false ;
	} else {
		int aa = 0 ;
		int bb = 0 ;
		int cc = 0 ;
		int dd = 0 ;
		int ee = 0 ;
		int ff = 0 ;
		int i = 0 ;
		while ( i < loopTimes ) { // 2�ֽ�GBK��3�ֽ�UTF8������Ϊ6����ȡ6���ַ��Ƚ�
			aa = b[i] & 0x000000FF ;
			if ( aa == 0 )
				break ;
			if ( aa < 128 ) {
				i = i + 1 ;
				continue ;
			}
			if ( aa < 192 || aa > 239 ) {
				isGBK = true ;
				break ;
			}

			bb = b[i+1] & 0x000000FF ;
			if ( bb < 128 || bb > 191 ) {
				isGBK = true ;
				break ;
			}

			// �����ֽ�:Ӣ��<128��GBK:129-254��UTF8:128-191 192-239
			cc = b[i+2] & 0x000000FF ;
			if ( cc > 239 ) {
				isGBK = true ;
				break ;
			} else if ( cc < 128 ) {
				i = i + 3 ;
				continue ;
			}

			dd = b[i+3] & 0x000000FF ;
			if ( dd < 64 ) {
				i = i + 4 ;
				continue ;
			}

			ee = b[i+4] & 0x000000FF ;
			if ( ee < 64 ) {
				i = i + 5 ;
				continue ;
			}

			ff = b[i+5] & 0x000000FF ;
			i = i + 6 ;
		// GBK: : 2 : 129-254 64-254
		// UTF8 : 2 : 192-223 128-191
		// UTF8 : 3 : 224-239 128-191 128-191
		} // while
	} // if
		if ( isGBK ) {
			return "GBK" ;
		} else {
			return "UTF-8" ;
		}
	}
/*
GBK	 : 1 : 81-FE = 129-254
GBK	 : 2 : 40-FE = 64-254

GBK -> UTF-8 :
UTF-8 2 : 1 : C2-D1 = 194-209
UTF-8 2 : 1 : 80-BF = 128-191

UTF-8 3 : 1 : E2-EF = 224-239
UTF-8 3 : 2 : 80-BF = 128-191
UTF-8 3 : 3 : 80-BF = 128-191
*/
// UTF-8 BOM : EF BB BF

/*
1�ֽ� 0xxxxxxx
2�ֽ� 110xxxxx 10xxxxxx
3�ֽ� 1110xxxx 10xxxxxx 10xxxxxx
4�ֽ� 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
5�ֽ� 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
6�ֽ� 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx

UTF8 : 2 : 192-223 128-191
UTF8 : 3 : 224-239 128-191 128-191
*/

	// } ͨ���ı���ȡ��д��


//	public static void createTxtFile(File txtFile, String cc) { // ����Txt�ļ�
//		try {
//			txtFile.createNewFile();
//			FileOutputStream outImgStream = new FileOutputStream(txtFile);
//			outImgStream.write(cc.getBytes("UTF-8"));
//			outImgStream.close();
//		} catch (Exception e) {
//			System.err.println(e.toString());
//		}
//	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list(); // �ݹ�ɾ��Ŀ¼�е���Ŀ¼��
			for (int i = 0; i < children.length; i++) {
				if (! deleteDir(new File(dir, children[i])) ) {
					return false;
				}
			}
		} // Ŀ¼��ʱΪ�գ�����ɾ��
		boolean bDeleted = false ;
		try {
			bDeleted = dir.delete();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return bDeleted;
	}

	/**
	 * ����·��ɾ��ָ����Ŀ¼���ļ������۴������
	 *
	 * @param sPath Ҫɾ����Ŀ¼���ļ�
	 * @return ɾ���ɹ����� true�����򷵻� false��
	 */
//	public static boolean DeleteFolder(String sPath) {
//		boolean flag = false;
//		File file = new File(sPath);
//		// �ж�Ŀ¼���ļ��Ƿ����
//		if (!file.exists()) {  // �����ڷ��� false
//			return flag;
//		} else {
//			// �ж��Ƿ�Ϊ�ļ�
//			if (file.isFile()) {  // Ϊ�ļ�ʱ����ɾ���ļ�����
//				return deleteFile(sPath);
//			} else {  // ΪĿ¼ʱ����ɾ��Ŀ¼����
//				return deleteDirectory(sPath);
//			}
//		}
//	}

	/**
	 * ɾ�������ļ�
	 *
	 * @param sPath ��ɾ���ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
//	public static boolean deleteFile(String sPath) {
//		boolean flag = false;
//		File file = new File(sPath);
//		// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
//		if (file.isFile() && file.exists()) {
//			file.delete();
//			flag = true;
//		}
//		return flag;
//	}

	/**
	 * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
	 *
	 * @param sPath ��ɾ��Ŀ¼���ļ�·��
	 * @return Ŀ¼ɾ���ɹ�����true�����򷵻�false
	 */
//	public static boolean deleteDirectory(String sPath) {
//		//���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
//		if (!sPath.endsWith(File.separator)) {
//			sPath = sPath + File.separator;
//		}
//		File dirFile = new File(sPath);
//		//���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
//		if (!dirFile.exists() || !dirFile.isDirectory()) {
//			return false;
//		}
//		boolean flag = true;
//		//ɾ���ļ����µ������ļ�(������Ŀ¼)
//		File[] files = dirFile.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			//ɾ�����ļ�
//			if (files[i].isFile()) {
//				flag = deleteFile(files[i].getAbsolutePath());
//				if (!flag) {
//					break;
//				}
//			} //ɾ����Ŀ¼
//			else {
//				flag = deleteDirectory(files[i].getAbsolutePath());
//				if (!flag) {
//					break;
//				}
//			}
//		}
//		if (!flag) {
//			return false;
//		}
//		//ɾ����ǰĿ¼
//		if (dirFile.delete()) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	public static void copyFileSlow(File fromFile, File toFile) { // ������Ʒ����Ƚ���
//		try {
//			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fromFile));
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toFile));
//			byte[] buf = new byte[1048576]; // 1M
//			int nn ;
//			while ( ( nn = bis.read(buf, 0, 1048576)) != -1 )
//				bos.write(buf, 0, nn);
//			bis.close();
//			bos.close();
//		} catch (Exception e) {
//			System.err.println(e.toString());
//		}
//	}

	public static long copyFile(File fromFile, File toFile) { // ʹ��channel���Ƹ��죬�����Ǵ��ļ�������
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		long cSize = 0;
		try {
			fis = new FileInputStream(fromFile);
			fos = new FileOutputStream(toFile);
			in = fis.getChannel();
			out = fos.getChannel();
			in.transferTo(0, in.size(), out); //��������ͨ�������Ҵ�inͨ����ȡ��Ȼ��д��outͨ��
		} catch (Exception e) {
			System.err.println(e.toString());
		} finally {
			try {
				cSize = out.size();
				fis.close();
				in.close();
				fos.close();
				out.close();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		return cSize;
	}

	public static boolean renameIfExist(File tarFile) {
		return renameIfExist(tarFile, ".old");
	}
	public static boolean renameIfExist(File tarFile, String addSuffix) { // ���Ŀ���ļ����ھ�����������������ļ�Ҳ���ھ�ɾ����
		if ( tarFile.exists() ) {
			File newFile = new File(tarFile.getPath() + addSuffix);
			if ( newFile.exists() )
				newFile.delete();
			return tarFile.renameTo(newFile);
		}
		return true;
	}

	// �� IP תΪ �㲥ip: ����: 192.168.1.22 -> 192.168.1.255
	public static String ip2bip(String iIPStr) {
		String ipHead = "";
		String RE = "^([0-9]*\\.[0-9]*\\.[0-9]*)\\.([0-9]*)$";
		Matcher m = Pattern.compile(RE).matcher(iIPStr);
		while (m.find())
			ipHead = m.group(1);
		if ( ipHead.contains(".") ) {
			return ipHead + ".255" ;
		} else {
			return "";
		}
	}

	/**
	 *
	 * @param file
	 * @param algorithm �������㷨������  for example: MD5, SHA1, SHA-256, SHA-384, SHA-512 etc.
	 * @return
	 */
	public static String getFileHash(File file, String algorithm) {
		if (!file.exists() || !file.isFile())
			return "";

		byte[] buffer = new byte[2048];
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			FileInputStream in = new FileInputStream(file);
			while (true) {
				int len = in.read(buffer, 0, 2048);
				if (len != -1)
					digest.update(buffer, 0, len);
				else
					break;
			}
			in.close();

			byte[] md5Bytes = digest.digest();
			StringBuilder hexValue = new StringBuilder();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			return "";
		}
	}


}
