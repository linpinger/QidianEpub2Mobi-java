package com.linpinger.novel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Site1024 extends NovelSite {
	public HashMap<String, Object> getContentTitle(String html) { // ����õ�1024�ı��������: title, content
		// Used by: Activity_EBook_Viewer , Activity_ShowPage4Eink 
		HashMap<String, Object> oM = new HashMap<String, Object>();

		// ����
		// <center><b>�鿴�����汾: [-- <a href="read.php?tid=21" target="_blank">[11-14] ���Ǿ��⴫</a> --]</b></center>
		Matcher mat2 = Pattern.compile("(?smi)<center><b>[^>]*?>([^<]*?)</a>").matcher(html);
		while (mat2.find())
			oM.put(NV.PageName, mat2.group(1));

		// ����
		String text = "";
		Matcher mat = Pattern.compile("(?smi)\"tpc_content\">(.*?)</td>").matcher(html);
		while (mat.find())
			text = text + mat.group(1) + "<br>-----#####-----<br>" ;

		text = text.replace("<script src=\"http://u.phpwind.com/src/nc.php\" language=\"JavaScript\"></script><br>", "")
				.replace("\r", "")
				.replace("\n", "")
				.replace("&nbsp;", " ")
				.replace("</span>", "")
				.replaceAll("(?smi)<br>[ ��]*", "\n")
				.replaceAll("(?smi)^[ ��]*", "")
				.replaceAll("(?i)<span[^>]*?>", "")
				.replace("<br>", "\n")
				.replace("\n\n", "\n")
				.replace("\n\n", "\n");

		oM.put(NV.Content, text);

		return oM ;
	}
}
