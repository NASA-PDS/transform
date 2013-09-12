<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
 <!-- 
     This is a first pass at an html conversion stylesheet.
  -->
<xsl:output method="html" indent="yes" version="5.0"/>

<xsl:template match="/*">
<html lang="en">
  <head>
    <title><xsl:value-of select="./*[name()='Identification_Area']/*[name()='title']"/></title>
  </head>
  <body>
    <table  border="5" cellpadding="2" cellspacing="4" width="100%">    
    <xsl:apply-templates select="*"/>
    </table>
  </body>
</html>
</xsl:template>

<xsl:template match="*">
    <xsl:choose>
        <xsl:when test="./*">
      <tr>
        <td colspan="2">
          <table border="5" cellpadding="2" cellspacing="4" width="100%">
            <tr><th align="left" colspan="2" bgcolor="silver"><xsl:value-of select="name()"/></th></tr>
                           
            <xsl:apply-templates select="*"/>
          </table>
        </td>
      </tr>            
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="row"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="row">
    <tr><td><xsl:value-of select="name()"/></td><td>
    <xsl:choose>
        <xsl:when test="string(number(.))='NaN'"><xsl:value-of select="normalize-space(text())"/>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="normalize-space(text())"/>
        </xsl:otherwise>
    </xsl:choose>
    </td></tr>
</xsl:template>

</xsl:stylesheet>
