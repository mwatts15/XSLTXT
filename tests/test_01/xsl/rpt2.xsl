<?xml version="1.0"?>
<!--
  Templates to generate the second report.
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
      <xsl:with-param name="elem" select="sku_prod_type"/>
    </xsl:call-template>
    <xsl:call-template name="columnHeader">
      <xsl:with-param name="text">
        <xsl:text>TOTAL WT</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--
    This template overrides the default name="tableBody" statement
    in common.xsl. It does this so that it can generate rows
    only for unique values of sku_prod_type
  -->
  <xsl:template name="tableBody">
    <!-- Loop over all sku_prod_type elements in the document -->
    <xsl:for-each select="order/sku/sku_prod_type">
      <!-- Sort them by sku_prod_type -->
      <xsl:sort select="."/>
      <!-- Store the value of the sku_prod_type, e.g. DMON -->
      <xsl:variable name="this" select="text()"/>
      <!--
        Set next to be all of the sku_prod_type elements that
        follow this one and have the same text value, e.g DMON
      -->
      <xsl:variable name="next"
        select="following::sku_prod_type[text() = $this]"/>
      <!-- 
        If there are no next nodes then this value is the
        last of a set of equal values so a row should be
        written out for it. Call the tableRow template to
        output the row passing it the sku_prod_type value
        being processed.
      -->
      <xsl:if test="not($next)">
        <xsl:call-template name="tableRow">
          <xsl:with-param name="prod-type" select="$this"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!--
    Generate a single table row. This template sets up a list
    of sku elements that all have an sku_prod_type value
    equal to the prod-type parameter passed in. The tableRowData
    template is passed this list and it uses it to produce the
    output row.
  -->
  <xsl:template name="tableRow">
    <xsl:param name="prod-type"/>
    <tr>
      <xsl:call-template name="tableRowData">
        <xsl:with-param name="prod-type" select="$prod-type"/>
        <!--
          Note // in select value. We are currently on a particular
          sku_prod_type element so we need to start at the root element
          if we're going to find any others.
        -->
        <xsl:with-param name="elem"
          select="//order/sku[sku_prod_type = $prod-type]"/>
      </xsl:call-template>
    </tr>
  </xsl:template>

  <!--
    Generate the td elements for a single table row. The tricky bit
    is to generate the total weight of all of the products with
    the same sku_prod_type value. To do this the template calls
    totalWeightProduct, which is very similar to totalWeight in
    common.xsl.
  -->
  <xsl:template name="tableRowData">
    <xsl:param name="prod-type"/>
    <xsl:param name="elem"/>
    <td>
      <xsl:value-of select="sum($elem/sku_qty)"/>
    </td>
    <td>
      <xsl:value-of select="$elem[1]/sku_prod_type"/>
    </td>
    <td>
      <xsl:call-template name="totalWeightProduct">
        <xsl:with-param name="prod-type" select="$prod-type"/>
        <xsl:with-param name="elem" select="$elem"/>
        <xsl:with-param name="total-so-far" select="0"/>
      </xsl:call-template>
    </td>
  </xsl:template>

  <!--
    Calculate the total weight of all of the entries with the same value of
    sku_prod_type. Similar to totalWeight in common.xsl
  -->
  <xsl:template name="totalWeightProduct">
    <xsl:param name="prod-type"/>
    <xsl:param name="elem"/>
    <xsl:param name="total-so-far"/>
    <xsl:choose>
      <xsl:when test="$elem">
        <xsl:call-template name="totalWeightProduct">
          <xsl:with-param name="prod-type" select="$prod-type"/>
          <xsl:with-param name="elem"
            select="$elem/following::sku[sku_prod_type = $prod-type]"/>
          <xsl:with-param name="total-so-far"
            select="$total-so-far + ($elem[1]/sku_qty * $elem[1]/sku_weight)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$total-so-far"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
