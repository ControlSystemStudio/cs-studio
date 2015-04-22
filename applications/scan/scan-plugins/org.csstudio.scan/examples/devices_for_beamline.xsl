<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>
                
<!-- Style sheet that translates a beamline configuration file
     into an SNS translation service device file.
     
     Kay Kasemir
  -->

<xsl:output method="xml" omit-xml-declaration="no"/>

<!-- Translate all devices below a beamline -->
<xsl:template match="beamline">
  <xsl:comment> This device description was translated
     from a scan system beam line configuration file.
  </xsl:comment>
  <xsl:apply-templates select="devices"/>
</xsl:template>

<!-- Translate each device below devices -->
<xsl:template match="devices">
  <devices>
    <xsl:apply-templates select="device"/>
  </devices>
</xsl:template>

<!-- Device: name and PVs -->
<xsl:template match="device">
  <xsl:text>&#xa;  </xsl:text>
  <device>
    <xsl:text>&#xa;    </xsl:text>
    <device_name><xsl:value-of select="name"/></device_name>
    <xsl:apply-templates select="pv"/>
  <xsl:text>&#xa;  </xsl:text>
  </device>
  <xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- PV: "pv_name" is the (optional) alias. Actual PV name is "epics_name" -->
<xsl:template match="pv">
  <xsl:text>&#xa;    </xsl:text>
  <process_variable>
    <xsl:text>&#xa;      </xsl:text>
    <pv_name>
	  <xsl:choose>
        <xsl:when test="alias != ''">
          <xsl:value-of select="alias "/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
	    </xsl:otherwise>
	  </xsl:choose>
    </pv_name>
    <xsl:text>&#xa;      </xsl:text>
    <epics_name><xsl:value-of select="name"/></epics_name>
    <xsl:text>&#xa;    </xsl:text>
  </process_variable>
</xsl:template>

</xsl:stylesheet>
