<?xml version="1.0"?>
<!--
  Templates to generate the first report.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Import the common templates from common.xsl -->
  <xsl:import href="common.xsl"/>

  <!-- Choose to output html -->
  <xsl:output method="html"/>

  <!--
    This generates the table header. It is called from the match="return" 
    template in common.xsl. It uses the name="columnHeader" template
    from common.xsl to output the the elements.
  -->
  <xsl:template match="sku" mode="header">
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="elem" select="sku_qty"/>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="elem" select="sku_id"/>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="elem" select="sku_prod_type"/>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="text">
        <xsl:text>TOTAL WT</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="text">
        <xsl:text>ORDER NUM</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="text">
        <xsl:text>DPS NUM</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--
    This template is called from the name="tableBody" template in 
    common.xsl. It outputs a single row in the table.
  -->
  <xsl:template match="sku" mode="row">
    <tr>
      <td>
        <xsl:value-of select="sku_qty"/>
      </td>
      <td>
        <xsl:value-of select="sku_id"/>
      </td>
      <td>
        <xsl:value-of select="sku_prod_type"/>
      </td>
      <td>
        <xsl:value-of select="sku_qty * sku_weight"/>
      </td>
      <td>
        <xsl:value-of select="../@order_num"/>
      </td>
      <td>
        <xsl:value-of select="../@dps_num"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
