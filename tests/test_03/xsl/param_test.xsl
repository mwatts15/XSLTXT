<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text"/>
  <xsl:template match="resultSet/record">
    <xsl:call-template name="called_tpl">
      <xsl:with-param name="p1" select="column[@name='USER_NAME']"/>
      <xsl:with-param name="p2">
        <xsl:text>Format One</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="called_tpl">
      <xsl:with-param name="p1" select="column[@name='USER_NAME']"/>
      <xsl:with-param name="p2">
        <xsl:text>Format Two</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="called_tpl">
      <xsl:with-param name="p1" select="column[@name='USER_NAME']"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="called_tpl">
    <xsl:param name="p1"/>
    <xsl:param name="p2">
      <xsl:text>Format Three</xsl:text>
    </xsl:param>
    <xsl:text>
</xsl:text>
    <xsl:text>Calling </xsl:text>
    <xsl:value-of select="$p1"/>
    <xsl:text> with format </xsl:text>
    <xsl:value-of select="$p2"/>
    <xsl:text>
</xsl:text>
  </xsl:template>
</xsl:stylesheet>
