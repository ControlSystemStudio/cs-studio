<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>
                
<!-- Style sheet that translates older (SNS) beamline configuration file
     into scan config file (use double'-', can't show that in XML comment):
     
      xsltproc -output scan_config.xml beamline2scan_config.xsl beamline.xml 
     
     Kay Kasemir
  -->

<xsl:output method="xml" omit-xml-declaration="no"/>

<!-- Translate all devices below a beamline -->
<xsl:template match="beamline">
  <xsl:comment> Scan configuration generated from beamline.xml </xsl:comment>
  <scan_config>
    <xsl:text>&#xa;</xsl:text>
    <xsl:apply-templates select="devices/device"/>
  <xsl:text>&#xa;</xsl:text>
    
  </scan_config>
</xsl:template>

<!-- Handle PVs of one device -->
<xsl:template match="device">  
  <xsl:choose>
    <!-- Skip devices explicitly marked as non-active -->
    <xsl:when test="@active='false'">
      <xsl:text>&#xa;  </xsl:text>
      <xsl:comment> Device '<xsl:value-of select="name"/>': Not active </xsl:comment>
      <xsl:text>&#xa;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&#xa;  </xsl:text>
      <xsl:comment> Device '<xsl:value-of select="name"/>' </xsl:comment>
      <xsl:text>&#xa;</xsl:text>
      <!-- List device's PVs -->
      <xsl:for-each select="pv">
        <xsl:sort select="name"/>
        <xsl:text>  </xsl:text>
        <pv>
          <xsl:text> </xsl:text>
          <name> <xsl:value-of select="name"/> </name>
          <xsl:text> </xsl:text>
          <xsl:if test="alias != ''">
            <alias> <xsl:value-of select="alias"/> </alias>
            <xsl:text> </xsl:text>
          </xsl:if>
        </pv>
        <xsl:text>&#xa;</xsl:text>
      </xsl:for-each>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

</xsl:stylesheet>
