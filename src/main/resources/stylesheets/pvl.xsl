<?xml version='1.0' encoding='UTF-8'?>
<!--
Copyright © 2019, California Institute of Technology ("Caltech").
U.S. Government sponsorship acknowledged.

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

• Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
• Redistributions must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other
  materials provided with the distribution.
• Neither the name of Caltech nor its operating division, the Jet Propulsion
  Laboratory, nor the names of its contributors may be used to endorse or
  promote products derived from this software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
-->
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
