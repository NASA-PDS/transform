<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

 <!--
     This is a first pass at a PVL conversion stylesheet.
  -->
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="*">
    <xsl:choose>
        <xsl:when test="./*">
            <xsl:for-each select="ancestor::*">
                <xsl:text>&#32;&#32;</xsl:text>
            </xsl:for-each>
            <xsl:text>BEGIN_OBJECT = </xsl:text><xsl:value-of select="translate(name() , $vLowercaseChars_CONST , $vUppercaseChars_CONST)"/><xsl:text>
</xsl:text>
            <xsl:apply-templates select="*"/>
            <xsl:for-each select="ancestor::*">
                <xsl:text>&#32;&#32;</xsl:text>
            </xsl:for-each>
            <xsl:text>END_OBJECT = </xsl:text><xsl:value-of select="translate(name() , $vLowercaseChars_CONST , $vUppercaseChars_CONST)"/>;<xsl:text>
</xsl:text>
        </xsl:when>
        <xsl:otherwise>
            <xsl:for-each select="ancestor::*">
                <xsl:text>&#32;&#32;</xsl:text>
            </xsl:for-each>
            <xsl:value-of select="translate(name() , $vLowercaseChars_CONST , $vUppercaseChars_CONST)"/>
            <xsl:text> = </xsl:text>
            <xsl:choose>
                <xsl:when test="string(number(.))='NaN'">"<xsl:value-of select="normalize-space(text())"/>";<xsl:text>
</xsl:text></xsl:when>
                <xsl:otherwise><xsl:value-of select="normalize-space(text())"/>;<xsl:text>
</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


    <xsl:variable name="vLowercaseChars_CONST" select="'abcdefghijklmnopqrstuvwxyz'"/>
    <xsl:variable name="vUppercaseChars_CONST" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>


</xsl:stylesheet>
