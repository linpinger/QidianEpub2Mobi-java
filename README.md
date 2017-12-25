QidianEpub2Mobi(起点epub转mobi)
-----------------------------------------
**声明:** 本程序调用了 amazon 的 kindlegen, iHarder.net的FileDrop.java类，版权归各被调用程序及库的所有者

**名称:** QidianEpub2Mobi

**功能:** 将起点epub电子书转换为mobi电子书(Kindel电子书)

**作者:** 爱尔兰之狐(linpinger)

**邮箱:** [linpinger@gmail.com](mailto:linpinger@gmail.com)

**主页:** <http://linpinger.github.io?s=Atc_QidianEpub2Mobi_Java>

**缘起:** 2017年起点改版后，原txt格式不提供下载，目前只提供epub下载

**原理:** 解压epub，然后读取内容重新生成html，然后调用kindlegen转为mobi格式

**下载:**
- jar文件: [QidianEpub2Mobi-java.jar](http://linpinger.qiniudn.com/prj/QidianEpub2Mobi-java.jar)
- 源代码: [QidianEpub2Mobi-java](https://github.com/linpinger/qidianepub2mobi-java)

**依赖:**
- amazon 的 kindlegen <https://www.amazon.com/gp/feature.html?ie=UTF8&docId=1000765211>
- iHarder.net的FileDrop.java类 <http://iharder.sourceforge.net/current/java/filedrop/>

**安装与使用:**
- 下载安装 jre / jdk，<https://www.java.com/zh_CN/>
- 下载 适合你平台的 kindlegen，并放入环境变量PATH路径中，例如C:\windows\, ~/bin/
- 命令行:  java -jar QidianEpub2Mobi-java.jar  xxxxx.epub 即可转换
- 图形界面: java -jar QidianEpub2Mobi-java.jar， 按 选择 按钮选择epub文件，或将epub文件拖动到窗口中，然后双击第一章或生成按钮

**更新日志:**
- 2017-12-25: 删除无用的文件
- 2017-12-18: 第一版

