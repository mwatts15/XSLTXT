<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:template match="resultSet">
    <xsl:for-each select="record/column[1]">
      <xsl:sort select="."/>
      <xsl:variable name="this" select="text()"/>
      <xsl:variable name="next" select="following::column[text() = $this]"/>
      <xsl:if test="not($next)">
        <xsl:call-template name="tableRow">
          <xsl:with-param name="user" select="$this"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="tableRow">
    <xsl:param name="user"/>
    <xsl:variable name="id">
      <xsl:value-of select="position()"/>
    </xsl:variable>
    <tr>
      <xsl:attribute name="id">
        <xsl:value-of select="$id"/>
      </xsl:attribute>
      <xsl:attribute name="onClick">
        <xsl:text>SelectRow(</xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>)</xsl:text>
      </xsl:attribute>
      <td class="header1">
        <xsl:value-of select="$user"/>
      </td>
      <xsl:call-template name="stageCell">
        <xsl:with-param name="user" select="$user"/>
        <xsl:with-param name="stage">
          <xsl:text>1</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
      <td class="silver">
        <xsl:value-of select="sum(/resultSet/record[column[1] = $user and column[2] != '']/column[3])"
        />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="stageCell">
    <xsl:param name="user"/>
    <xsl:param name="stage"/>
    <xsl:choose>
      <xsl:when test="$stage &lt; 5">
        <xsl:variable name="count">
          <xsl:value-of select="/resultSet/record[column[1] = $user and column[2] = $stage]/column[3]"
          />
        </xsl:variable>
        <td class="white">
          <xsl:choose>
            <xsl:when test="$count != ''">
              <xsl:value-of select="$count"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>0</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <xsl:call-template name="stageCell">
          <xsl:with-param name="user" select="$user"/>
          <xsl:with-param name="stage" select="$stage + 1"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
