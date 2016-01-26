<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='html' version='1.0' encoding='UTF-8' indent='yes'/>

<xsl:template match="proced">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="prelreqs">
<div id="proced_prelreqs">
    <div class="lists_title1">前置要求</div>
    <span class="nullpmd"></span>
    <span class="nullreqpers"></span>
    <span class="ReqcondsContent"/>
	<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="mainfunc">
	<div class="lists_title1">维修操作</div>
	<table cellpadding="0" cellspacing="5" border="0" width="100%">
		<xsl:apply-templates/>
 	</table><br/>
</xsl:template>

<xsl:template match="closereqs">
	<div>
        <div class="lists_title1">结束要求</div>
		<xsl:apply-templates/>
    </div>
</xsl:template>

<xsl:template match="reqconds">
<xsl:variable name="hasConds"><xsl:value-of select="count(reqcond)"></xsl:value-of></xsl:variable>
<xsl:choose>
	<xsl:when test="$hasConds &gt; 0">
		<div class="lists_title2">条件要求</div>
		<table width="100%" class="lists_table" align="center">
	        <tr><th width="100%">动作/条件</th></tr>
			<xsl:for-each select="reqcond">
				<tr><td><xsl:value-of select="."/></td></tr>
			</xsl:for-each>
	    </table>
	</xsl:when>
	<xsl:otherwise>
		<xsl:apply-templates/>
	</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="noconds">
	<span class="para_firstline_indent_text">无</span>
</xsl:template>
        
<xsl:template match="supequip">
<div>
    <div class="lists_title2">保障工具</div>
	<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="nosupeq">
    <span class="nullsupequip">
    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
    <span class="para_firstline_indent_text">无保障设备要求</span>
    </span>
</xsl:template>

<xsl:template match="supplies">
<div>
    <div class="lists_title2">消耗品</div>
	<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="nosupply">
	<span class="nullsupplies">
	<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
	<span class="para_firstline_indent_text">无消耗品</span>
	</span>
</xsl:template>


<xsl:template match="spares">
<div>
    <div class="lists_title2">备件</div>
	<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="nospares">
	<span class="nullspares">
	<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
	<span class="para_firstline_indent_text">无备件要求</span>
	</span>
</xsl:template>

<xsl:template match="safety">
	<div id="proced_safety">
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="safecond">
	<div class="lists_title2">安全条件</div>
	<xsl:apply-templates select="warning | caution"/>
</xsl:template>

<xsl:template match="warning">
	<p xmlns:ibv="http://sbtr.com/namespace">
	<br/>
          <table width="90%" border="0" align="center" background="./manual-resources/images/lines_rw_cut2.gif">
            <tr>
              <td style="border:none" width="10" height="10"></td>
              <td style="border:none" height="10"></td>
              <td style="border:none" width="10" height="10"></td>
            </tr>
            <tr>
              <td style="border:none" width="10"></td>
              <td style="border:none;padding:3px;" bgcolor="#000000">
                <table width="100%" border="0" bgcolor="#FFFFFF">
                  <tr>
                    <td style="border:none" align="center" height="21px">
                      <div class="warning_head">警告</div>
                    </td>
                  </tr>
                  <tr>
                    <td style="border:none;table-layout:fixed;word-wrap:break-word;word-break:break-all">
                      <div><a name=""><p class="warning_text">
                      	<xsl:value-of select="para"></xsl:value-of>
                      </p></a></div>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="10" style="border:none"></td>
            </tr>
            <tr>
              <td style="border:none" width="10" height="10"></td>
              <td style="border:none" height="10"></td>
              <td style="border:none" width="10" height="10"></td>
            </tr>
          </table>
        </p>
</xsl:template>

<xsl:template match="caution">
<p xmlns:ibv="http://sbtr.com/namespace">
	<br/>
  <table width="90%" border="0" align="center" background="./manual-resources/images/lines_by_cut2.gif" class="contentWaringOrCaution contentCaution">
    <tr>
      <td style="border:none" width="10" height="10"></td>
      <td style="border:none" height="10"></td>
      <td style="border:none" width="10" height="10"></td>
    </tr>
    <tr>
      <td style="border:none" width="10"></td>
      <td style="border:none;padding:3px;" bgcolor="#000000">
        <table width="100%" border="0" bgcolor="#FFFFFF">
          <tr>
            <td style="border:none" align="center" height="21px">
              <div class="caution_head">注意</div>
            </td>
          </tr>
          <tr>
            <td style="border:none;table-layout:fixed;word-wrap:break-word;word-break:break-all">
              <div><a name=""><p class="caution_text">
				<xsl:value-of select="para"></xsl:value-of>
			  </p></a></div>
            </td>
          </tr>
        </table>
      </td>
      <td width="10" style="border:none"></td>
    </tr>
    <tr>
      <td style="border:none" width="10" height="10"></td>
      <td style="border:none" height="10"></td>
      <td style="border:none" width="10" height="10"></td>
    </tr>
  </table>
</p>
</xsl:template>


<xsl:template match="step1">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="step2">
	<tr valign="top">
        <td colspan="1" style="border-bottom:none;border-right:none;"></td>
        <td align="left" valign="top" class="auto_adapt_with_cls" style="border-bottom:none;border-right:none;"><span class="step_numbercls"><a name="dir-step2-26"></a>
			<xsl:number count="step1"/>
			<xsl:text>.</xsl:text>
			<xsl:number count="step2"/>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</span></td>
        <td align="left" valign="top" colspan="7" style="border-bottom:none;border-right:none;"><a name="">
        	<xsl:apply-templates />
		</a></td>
    </tr>
</xsl:template>

<xsl:template match="step2/para">
	<span class="para_text">
		<xsl:value-of select="."/><br/>
	</span>
</xsl:template>

<xsl:template match="step3">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="step1/title">
	<tr valign="top">
        <td align="left" valign="top" class="auto_adapt_with_cls" style="border-bottom:none;border-right:none;">
        <span class="step_titlecls"><a name="dir-step1-25"></a>
	        <xsl:number count="step1"/>
		</span></td>
        <td align="left" valign="top" colspan="8" style="border-bottom:none;border-right:none;"><span class="step_titlecls">
        	<xsl:value-of select="."/>
		</span></td>
    </tr>
</xsl:template>

</xsl:stylesheet>
