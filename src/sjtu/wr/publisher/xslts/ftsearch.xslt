<?xml version="1.0" encoding="UTF-8"?>
<!-- Edited with XML Spy v2007 (http://www.altova.com) -->
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='html' version='1.0' encoding='UTF-8' indent='yes'/>

<xsl:template match="/">
  <html>
  <head>
    <script type="text/javascript" src="js/jquery-2.1.4.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/jquery.textSearch-1.0.js" charset="utf-8"></script>
  </head>
  <body>
  <h1>搜索结果</h1>
      <xsl:for-each select="result/dm">
        <a href=""><xsl:value-of select="techname"/> - <xsl:value-of select="infoname"/></a>
        <p><xsl:value-of select="abstract"/></p>
        <div><xsl:value-of select="code"/>, <xsl:value-of select="date"/></div>
        <br/>
      </xsl:for-each>
    <xsl:variable name="keyword"><xsl:value-of select="result/key" /></xsl:variable>
    <h1><xsl:value-of select="$keyword"/></h1>
    <script type="text/javascript">
      $("p").textSearch("<xsl:value-of select="$keyword"/>");
    </script>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
