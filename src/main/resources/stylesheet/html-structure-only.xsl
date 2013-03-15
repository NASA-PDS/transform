<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
 <!-- 
     This is a first pass at an html conversion stylesheet.
  -->
<xsl:output method="text" omit-xml-declaration="yes" indent="yes"/>

<xsl:variable name="tab">
    <xsl:text>&#32;&#32;</xsl:text>
</xsl:variable>

<xsl:template match="/*">
    <xsl:text>&lt;html&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/>
    <xsl:text>&lt;head&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/><xsl:value-of select="$tab"/>
    <xsl:text>&lt;title&gt;</xsl:text>
    <xsl:value-of select="./*[name()='Identification_Area']/*[name()='title']"/>
    <xsl:text>&lt;/title&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/>
    <xsl:text>&lt;/head&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/>
    <xsl:text>&lt;body&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/>
    <xsl:text>&lt;table border="5" cellpadding="2" cellspacing="4" width="100%"&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:value-of select="$tab"/>   
    <xsl:text>&lt;tr&gt;&lt;th align="left" colspan="2" bgcolor="silver"&gt;</xsl:text><xsl:value-of select="name()"/><xsl:text>&lt;/th&gt;&lt;/tr&gt;</xsl:text><xsl:text>
</xsl:text>    
    <xsl:apply-templates select="*"/>
    <xsl:value-of select="$tab"/>
    <xsl:text>&lt;/table&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:text>&lt;/body&gt;</xsl:text><xsl:text>
</xsl:text>
    <xsl:text>&lt;/html&gt;</xsl:text>
</xsl:template>

<xsl:template match="*">
    <xsl:choose>
        <xsl:when test="./*">
            <xsl:for-each select="ancestor::*">
                <xsl:value-of select="$tab"/>
            </xsl:for-each>   
            <xsl:text>&lt;tr&gt;&lt;td colspan="2"&gt;</xsl:text><xsl:text>
</xsl:text>
            <xsl:for-each select="ancestor::*">
                <xsl:value-of select="$tab"/>
            </xsl:for-each>               
            <xsl:text>&lt;table border="5" cellpadding="2" cellspacing="4" width="100%"&gt;</xsl:text><xsl:text>
</xsl:text>
            <xsl:for-each select="ancestor::*">
                <xsl:value-of select="$tab"/>
            </xsl:for-each>   
            <xsl:text>&lt;tr&gt;&lt;th align="left" colspan="2" bgcolor="silver"&gt;</xsl:text><xsl:value-of select="name()"/><xsl:text>&lt;/th&gt;&lt;/tr&gt;</xsl:text><xsl:text>
</xsl:text>
            <xsl:apply-templates select="*"/>
            <xsl:for-each select="ancestor::*">
                <xsl:value-of select="$tab"/>
            </xsl:for-each>   
            <xsl:text>&lt;/table&gt;</xsl:text><xsl:text>
</xsl:text>
            <xsl:for-each select="ancestor::*">
                <xsl:value-of select="$tab"/>
            </xsl:for-each> 
            <xsl:text>&lt;/tr&gt;&lt;/td&gt;</xsl:text><xsl:text>
</xsl:text>            
        </xsl:when><!--
        <xsl:otherwise>
            <xsl:call-template name="row"/>
        </xsl:otherwise>-->
    </xsl:choose>
</xsl:template>

<xsl:template name="row">
    <xsl:for-each select="ancestor::*">
        <xsl:value-of select="$tab"/>
    </xsl:for-each>
    <xsl:text>&lt;tr&gt;&lt;td&gt;</xsl:text><xsl:value-of select="name()"/><xsl:text>&lt;/td&gt;&lt;td&gt;</xsl:text>
    <xsl:choose>
        <xsl:when test="string(number(.))='NaN'"><xsl:value-of select="normalize-space(text())"/>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="normalize-space(text())"/>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&lt;/td&gt;&lt;/tr&gt;</xsl:text><xsl:text>
</xsl:text>
</xsl:template>

</xsl:stylesheet>
