<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='html' version='1.0' encoding='UTF-8' indent='yes'/>

<xsl:template match="descript">
	<xsl:apply-templates/>
</xsl:template>


<xsl:template match="para0">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="subpara1">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="subpara2">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="subpara3">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="subpara4">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="para0/title">
	<div class="para0_title">
		<xsl:number count="para0"/>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="."/>
	</div>
</xsl:template>

<xsl:template match="subpara1/title">
	<div class="subpara1_title">
		<xsl:number count="para0"/>
		<xsl:text>.</xsl:text>
		<xsl:number count="subpara1"/>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="."/>
	</div>
</xsl:template>

<xsl:template match="subpara2/title">
	<div class="subpara2_title">
		<xsl:number count="para0"/>
		<xsl:text>.</xsl:text>
		<xsl:number count="subpara1"/>
		<xsl:text>.</xsl:text>
		<xsl:number count="subpara2"/>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="."/>
	</div>
</xsl:template>

<xsl:template match="deflist">
	<table class="lists_table" align="center" width="90%">
  		<tr>
    		<th width="50%" align="center">术语</th>
    		<th width="50%" align="center">解释</th>
  		</tr>
  		<xsl:for-each select="term">
	        <tr valign="top">
	          <td width="50%"><xsl:value-of select="."/></td>
	          <td style="padding-left:5px;table-layout:fixed;word-wrap:break-word;word-break:break-all" width="50%"><xsl:value-of select="following::def/para"/> </td>
	        </tr>
        </xsl:for-each>
  	</table>
</xsl:template>

<xsl:template match="para">
	<p class="para_firstline_indent_text"><xsl:apply-templates/></p>
</xsl:template>


<xsl:template match="randlist[floor(count(ancestor::randlist) div 2) * 2 = count(ancestor::randlist)]">
<ul class="unordered_randlist_hyphen_Odd">
	<xsl:apply-templates select="title | item"/>
</ul>
</xsl:template>

<xsl:template match="randlist/title">
<div style="font-weight:bold;font-size:14.25;text-indent:0mm;">
	<xsl:value-of select="."/>
</div>
</xsl:template>
          
<xsl:template match="randlist/item">
<li style="line-height:1.8;"><div class="para_text" style="text-indent:0mm;">
	<xsl:value-of select="para/text()"/>
	<xsl:apply-templates select="para/randlist"/>
</div></li>
</xsl:template>

<xsl:template match="randlist[floor(count(ancestor::randlist) div 2) * 2 != count(ancestor::randlist)]">
<ul class="unordered_randlist_hyphen_Even">
	<xsl:apply-templates select="item"/>
</ul>
</xsl:template>

<xsl:template match="figure">
	<xsl:variable name="icn"><xsl:value-of select="graphic/@boardno" /></xsl:variable>
	<div class="figure">
    	<p align="center"><img class="figure_min" id="{$icn}.png" src="thumbnails/min_{$icn}.GIF" /></p>
    </div>
	<div class="figure_multimedia_icn"><xsl:value-of select="$icn"/></div>
    <div class="figure_title" >
    	<a name="dir-title-5"></a>
    	<xsl:text>图片</xsl:text>
    	<xsl:number count="//figure"/>
    	<xsl:text> </xsl:text>
		<xsl:value-of select="title"/>
    </div>
</xsl:template>


<xsl:template match="multimedia">
	<xsl:variable name="icn"><xsl:value-of select="multimediaobject/@boardno" /></xsl:variable>
	<p align="center"><img class="figure_min" onclick="" style="width:48px;border-width:2px;border-style:solid;" name="Media.gif" id="{$icn}.mp4" src="manual-resources/images/media.png" alt="多媒体1  视频一"/></p>
	<div class="figure_multimedia_icn"><xsl:value-of select="$icn"/></div>
    <div class="multimedia_title" >
    	<xsl:text>多媒体</xsl:text>
    	<xsl:number count="//multimedia"/>
    	<xsl:text> </xsl:text>
		<xsl:value-of select="title"/>
    </div>
</xsl:template>



<xsl:template match="table">
	<table cellpadding="5" border="0" cellspacing="0" align="center" class="table_class" width="90%"><br />
		<colgroup>
			<xsl:for-each select="tgroup/colspec">
				<xsl:variable name="width"><xsl:value-of select="@colwidth" /></xsl:variable>
				<col colwidth="{$width}"/>
			</xsl:for-each>
	    </colgroup>
    	<xsl:apply-templates select="tgroup/thead | tgroup/tbody"/>
	</table>
</xsl:template>

<xsl:template match="tgroup/thead">
	<xsl:variable name="ws"><xsl:value-of select="sum(../colspec/@colwidth)" /></xsl:variable>
	<tr>
		<xsl:for-each select="row/entry">
			<xsl:variable name="cName"><xsl:value-of select="@colname"/></xsl:variable>
			<xsl:variable name="width"><xsl:value-of select="ancestor::tgroup/colspec[@colname=$cName]/@colwidth" /></xsl:variable>
			<xsl:variable name="widthRatio"><xsl:value-of select="$width div $ws * 100" /></xsl:variable>
			<th class="table_first_thead" width="{$widthRatio}%" align="left" valign="top">
				<xsl:value-of select="." />
			</th>
		</xsl:for-each>
		<td class="table_last_col" style="border-right-width:0pt;border-top-width:0pt;border-bottom-width:0pt;"> </td>
   	</tr>
</xsl:template>


<xsl:template match="tgroup/tbody">
	<xsl:variable name="ws"><xsl:value-of select="sum(../colspec/@colwidth)" /></xsl:variable>
	<xsl:for-each select="row">
       	<tr>
			<xsl:for-each select="entry">
	       		<xsl:variable name="seq"><xsl:number count="row"/></xsl:variable>
		      	<xsl:if test="$seq = '1'">
					<xsl:variable name="cName"><xsl:value-of select="@colname"/></xsl:variable>
					<xsl:variable name="width"><xsl:value-of select="ancestor::tgroup/colspec[@colname=$cName]/@colwidth" /></xsl:variable>
					<xsl:variable name="widthRatio"><xsl:value-of select="$width div $ws * 100" /></xsl:variable>
		          	<td style="border-top-style:solid;border-top-width:1pt;" width="{$widthRatio}%" class="table_cell" align="left" valign="top">
	          		<xsl:call-template name="LFsToBRs">
						<xsl:with-param name="input" select="."/>
					</xsl:call-template>
		          	</td>
				</xsl:if>
				<xsl:if test="$seq != '1'">
					<td class="table_cell" align="left" valign="top">
					<xsl:call-template name="LFsToBRs">
						<xsl:with-param name="input" select="."/>
					</xsl:call-template>
					</td>
				</xsl:if>
			</xsl:for-each>
          	<td class="table_last_col" style="border-right-width:0pt;border-top-width:0pt;border-bottom-width:0pt;"> </td>
        </tr>
	</xsl:for-each>
</xsl:template>

<xsl:template name="LFsToBRs">
	<xsl:param name="input" />
	<xsl:choose>
		<xsl:when test="contains($input, '&#10;')">
			<xsl:value-of select="substring-before($input, '&#10;')" /><br />
			<xsl:call-template name="LFsToBRs">
				<xsl:with-param name="input" select="substring-after($input, '&#10;')" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$input" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
