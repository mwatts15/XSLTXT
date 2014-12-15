<?xml version="1.0"?>
<!--
  A set of common templates/functions that are used by all of the reports.
  As this file is imported into each of the report files any of these templates
  can be overridden in those files by supplying a template with the same name
  or match attribute value.
-->  
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!--
    Default initial template. As this matches on "/" it will be the first one
    called. All it does is output a simple html page with head and body around
    the actual html data output by the template that is selected by the reply.
    If you don't want the html page data then replace this template with
    <xsl:template match="/">
      <xsl:apply-templates select="return"/>
    </xsl:template>
    This will produce an html "fragment" as the output that can be included
    in a coach.
  -->
  <xsl:template match="/">
    <html>
      <head>
        <title>
          <xsl:text>Testing</xsl:text>
        </title>
      </head>
      <body>
        <xsl:apply-templates select="return"/>
      </body>
    </html>
  </xsl:template>

  <!--
    Default template that generates the table. This is not currently
    overridden by any of the report specific xsl files.
  -->
  <xsl:template match="return">
    <table border="1">
      <xsl:apply-templates select="order[1]/sku[1]" mode="header"/>
      <xsl:call-template name="tableBody"/>
    </table>
    <xsl:call-template name="tableFooter"/>
  </xsl:template>

  <!--
    Default template that generates the header for a column. This is
    not currently overridden by any of the report specific xsl files.
    It takes either an "elem" or a "text" parameter. If it receives 
    an "elem" parameter it must be an element. It then uses the "desc"
    attribute on this element to provide the header. If it recieves
    a "text" parameter it just uses its value for the header.
  -->
  <xsl:template name="columnHeader">
    <xsl:param name="elem"/>
    <xsl:param name="text"/>
    <th>
      <xsl:choose>
        <xsl:when test="$elem">
          <xsl:value-of select="$elem/@desc"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$text"/>
        </xsl:otherwise>
      </xsl:choose>
    </th>
  </xsl:template>

  <!--
    Default template that generates the table body. This is overridden
    in rpt2.xsl. It applys the template with mode "row" that
    matches the "sku" element. rpt1.xsl and rpt2.xsl both
    include a template that will match.
  -->
  <xsl:template name="tableBody">
    <xsl:apply-templates select="order/sku" mode="row"/>
  </xsl:template>

  <!-- 
    Default template that generates the "footer" info at the bottom
    of the table. This is not currently overridden by any of the 
    report specific xsl files.
  -->
  <xsl:template name="tableFooter">
    <p>
      <xsl:text>No of Boxes = </xsl:text>
      <!-- Number of Boxes is easy using the sum function -->
      <xsl:value-of select="sum(order/sku/sku_qty)"/>
    </p>
    <p>
      <xsl:text>Total Weight(lbs) = </xsl:text>
      <!--
        Total Weight is trickier because of the need to multiply
        two numbers and then sum the result.
      -->
      <xsl:call-template name="totalWeight">
        <xsl:with-param name="elem" select="order/sku"/>
        <xsl:with-param name="total-so-far">
          <xsl:text>0</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
    </p>
  </xsl:template>

  <!--
    Recursive calculation of the totalWeight. This template is passed
    two parameters, "elem" which is a node set of the nodes still to
    be processed, and "total-so-far" which is the total weight calculated
    so far. It repeatedly calls itself passing in each time a shorter 
    list of nodes still to be processed and a larger total weight.
  -->  
  <xsl:template name="totalWeight">
    <xsl:param name="elem"/>
    <xsl:param name="total-so-far"/>
    <xsl:choose>
      <xsl:when test="$elem">
        <xsl:call-template name="totalWeight">
          <xsl:with-param name="elem" select="$elem/following::sku"/>
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
