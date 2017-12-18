package com.linpinger.novel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.linpinger.tool.ToolJava;

public class StorTxt extends Stor {

	public List<Novel> load(File inFile) {
		// boolean isNew = true;
		//long sTime = System.currentTimeMillis();
		// ��һ�������룬��GBK����UTF-8���������迼��
		String txtEnCoding = ToolJava.detectTxtEncoding(inFile.getPath()) ; // �²������ı����� ����: "GBK" �� "UTF-8"
		String txt = ToolJava.readText(inFile.getPath(), txtEnCoding).replace("\r", "").replace("��", ""); // Ϊ���txtԤ����

		if ( ! txt.contains("����ʱ��") ) // ������ı�
			return importNormalTxt(inFile, txtEnCoding);

		String sQidianid = inFile.getName().replace(".txt", ""); // �ļ���
		String sQidianURL = new SiteQiDian().getTOCURL_Android7(sQidianid); // URL
		String sBookName = sQidianid;

		List<Novel> lst = new ArrayList<Novel>();
		Novel book = new Novel();
		Map<String, Object> info = new HashMap<String, Object>();
		List<Map<String, Object>> chapters = new ArrayList<Map<String, Object>>();
		Map<String, Object> page;

		// �°�Ҫ��ܶ࣬��������ͷ���������½�
		// ��������Ժ����txt�нṹ�䶯�Ļ�����Ӧ�Կ��ܲ���ɰ棬�ʱ����ɰ�
		String line[] = txt.split("\n");
		int lineCount = line.length;
		sBookName = line[0] ;
		int titleNum = 0 ; // base:0 ����
		int headNum = 0 ; // base:0  ����
		int lastEndNum = 0 ;
		for ( int i=3; i<lineCount; i++) { // �ӵ����п�ʼ
			if (line[i].startsWith("����ʱ��")) { // ��һ��Ϊ������
				titleNum = i - 1 ;
				headNum = i + 2 ;
			} else { // �Ǳ�����
				if ( line[i].startsWith("<a href=") ) {
					if ( i - lastEndNum < 5 ) // ��Щtxt�½�β�����������ӣ�����
						continue;
					// �������ȡ�����У�������
					// System.out.println(titleNum + " : " + headNum + " - " + i );
					StringBuilder sbd = new StringBuilder();
					for ( int j=headNum; j<i; j++)
						sbd.append(line[j]).append("\n");
					sbd.append("\n");

					page = new HashMap<String, Object>();
					page.put(NV.PageName, line[titleNum]);
					page.put(NV.PageURL,  "");
					page.put(NV.Content,  sbd.toString());
					page.put(NV.Size,	 sbd.length());
					chapters.add(page);

					lastEndNum = i;
				}
			}
		}
		book.setChapters(chapters);

		info.put(NV.BookName,   sBookName);
		info.put(NV.BookURL,	sQidianURL);
		info.put(NV.DelURL,	 "");
		info.put(NV.BookStatu,  0);
		info.put(NV.QDID,	   sQidianid);
		info.put(NV.BookAuthor, "");
		book.setInfo(info);

		// Log.e("XX", "��ʱ: " + (System.currentTimeMillis() - sTime));
		lst.add(book);
		return lst ;
	}
	
	private List<Novel> importNormalTxt(File inFile, String txtEnCoding) {
//		String txtEnCoding = ToolBookJava.detectTxtEncoding(txtPath) ; // �²������ı����� ����: "GBK" �� "UTF-8"
//		String txt = ToolBookJava.readText(txtPath, txtEnCoding) ;

		String fileName = inFile.getName().replace(".txt", ""); // �ļ���

		List<Novel> lst = new ArrayList<Novel>();
		Novel book = new Novel();
		Map<String, Object> info = new HashMap<String, Object>();
		List<Map<String, Object>> chapters = new ArrayList<Map<String, Object>>();
		Map<String, Object> page;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), txtEnCoding));

			StringBuilder chunkStr = new StringBuilder(65536);
			int chunkLen = 0;
			int chunkCount = 0;
			String line = null;
			int lineLen = 0;
			while ((line = br.readLine()) != null) {
				if ( line.startsWith("����") ) // ȥ����ͷ�Ŀհ�
				line = line.replaceFirst("����*", "");

				lineLen = line.length() ;
				chunkLen = chunkStr.length();
				if ( chunkLen > 2200 && lineLen < 22 && ( line.startsWith("��") || line.contains("��") || line.contains("��") || line.contains("��") || line.contains("��") || line.contains("Ʒ") || lineLen > 2 ) ) {
					++ chunkCount;

					page = new HashMap<String, Object>();
					page.put(NV.PageName, txtEnCoding + "_" + String.valueOf(chunkCount));
					page.put(NV.PageURL,  "");
					page.put(NV.Content,  chunkStr.toString());
					page.put(NV.Size,	 chunkStr.length());
					chapters.add(page);

					chunkStr = new StringBuilder(65536);
				}
				chunkStr.append(line).append("\n");
			}

			if ( chunkStr.length() > 0 ) {
				++ chunkCount;
				page = new HashMap<String, Object>();
				page.put(NV.PageName, txtEnCoding + "_" + String.valueOf(chunkCount));
				page.put(NV.PageURL,  "");
				page.put(NV.Content,  chunkStr.toString());
				page.put(NV.Size,	 chunkStr.length());
				chapters.add(page);
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		book.setChapters(chapters);

		info.put(NV.BookName,   fileName);
		info.put(NV.BookURL,	"");
		info.put(NV.DelURL,	 "");
		info.put(NV.BookStatu,  0);
		info.put(NV.QDID,	   "");
		info.put(NV.BookAuthor, "");
		book.setInfo(info);

		lst.add(book);
		return lst ;
	}

	public void save(List<Novel> inList , File outFile) {
		StringBuilder oo = new StringBuilder();

		Map<String, Object> info;
		for (Novel novel : inList) {
			if ( novel.getChapters().size() == 0 )
				continue ;
			info = novel.getInfo();
			oo.append(info.get(NV.BookName)).append("\n")
			.append(info.get(NV.BookAuthor)).append("\n\n");

			for (Map<String, Object> page : novel.getChapters()) {
				oo.append(page.get(NV.PageName)).append("\n\n")
				.append(page.get(NV.Content)).append("\n\n\n");
			}
		}

		ToolJava.writeText(oo.toString(), outFile.getPath());
	}

}

