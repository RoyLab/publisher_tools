<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='html' version='1.0' encoding='UTF-8' indent='yes'/>

<xsl:import href="descript.xslt" />
<xsl:import href="proced.xslt" />



<xsl:template match="/">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <meta http-equiv="PRAGMA" content="no-cache" />
   <link rel="STYLESHEET" type="text/css" href="manual-resources/css/style.css" />
   <link rel="STYLESHEET" type="text/css" href="manual-resources/css/tbStyle.css" />
   
   <script language="JavaScript" src="manual-resources/js/jquery.js"></script>
   <script language="JavaScript" src="manual-resources/js/Dialog.js"></script>
   <script language="JavaScript" src="manual-resources/js/Reference.js"></script>
   <script language="JavaScript" src="manual-resources/js/AssistantInfo.js"></script>
   <script language="JavaScript" src="manual-resources/js/Graphic.js"></script>
   <script language="JavaScript" src="manual-resources/js/Procedure.js"></script>
   <script language="JavaScript" src="manual-resources/js/Fault.js"></script>
   <script language="JavaScript" src="manual-resources/js/Process.js"></script>
   <script language="JavaScript" src="manual-resources/js/Schedule.js"></script>
   <script language="JavaScript" src="manual-resources/js/ApplicRecised.js"></script>
   <script language="JavaScript" src="manual-resources/js/IETM.js"></script>
    
	<title>Content</title>
</head>
<body>
	<div style="height:80%" id="dmContent">
	<div align="center" class="dmodule_title">
		<xsl:value-of select="//techname"/> - <xsl:value-of select="//infoname"/>
	</div>
	<xsl:apply-templates select="dmodule/content/descript | dmodule/content/proced"/>
	
</div>
<br/><br/><br/><br/>
<hr class="DmEnding" width="98%" align="center" />
<div class="DmEnding" align="center" style="color:#6C6C6C;font-family:黑体; font-weight:bold;">数据模块结束</div>
</body>
</html>
</xsl:template>
	
<xsl:template match="dmodule/content/proced">
	<xsl:apply-imports/>
</xsl:template>

<xsl:template match="dmodule/content/descript">
	<xsl:apply-imports/>
</xsl:template>


</xsl:stylesheet>
