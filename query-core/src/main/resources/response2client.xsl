<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml" xmlns:geonet="http://www.fao.org/geonetwork" 
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 
<xsl:template match="csw:GetRecordsResponse">
	<xsl:apply-templates select="./csw:SearchResults" />
</xsl:template>

<xsl:template match="csw:SearchResults">
	<xsl:element name="response">
		<xsl:attribute name="totalcount"><xsl:value-of select="@numberOfRecordsMatched" /></xsl:attribute>
		<xsl:attribute name="success">true</xsl:attribute>
		<xsl:element name="data">
				<xsl:apply-templates select="./gmd:MD_Metadata" />
		</xsl:element>
    </xsl:element>
</xsl:template>

<xsl:template match="gmd:MD_Metadata">
	<xsl:element name="metadata">
		<xsl:element name="id"><xsl:value-of select="gmd:fileIdentifier/gco:CharacterString" /></xsl:element>
		<xsl:element name="schema"><xsl:value-of select="gmd:metadataStandardName/gco:CharacterString" /></xsl:element>
		<xsl:apply-templates select="./gmd:identificationInfo" />
		<xsl:apply-templates select="./gmd:distributionInfo//gmd:transferOptions"></xsl:apply-templates>
	</xsl:element>
</xsl:template>

<xsl:template match="gmd:identificationInfo">
	<xsl:element name="title"><xsl:value-of select=".//gmd:title/gco:CharacterString" /></xsl:element>
	<xsl:element name="summary"><xsl:value-of select=".//gmd:abstract/gco:CharacterString" /></xsl:element>
	<xsl:apply-templates select="./gmd:MD_DataIdentification//gmd:EX_Extent" />
</xsl:template>

<xsl:template match="gmd:EX_Extent">
	<xsl:variable name="norte" select=".//gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal" />
	<xsl:variable name="sur" select=".//gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal" />
	<xsl:variable name="este" select=".//gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal" />
	<xsl:variable name="oeste" select=".//gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal" />
	<xsl:element name="extent">POLYGON (( <xsl:value-of select="concat($oeste,' ',$norte,',',$este,' ',$norte,',',$este,' ',$sur,',',$oeste,' ',$sur)"/>))</xsl:element>
	<xsl:apply-templates select=".//gmd:EX_TemporalExtent" />
</xsl:template>

<xsl:template match="gmd:EX_TemporalExtent">
	<xsl:element name="temporalextent">
		<xsl:element name="time_coverage_start"><xsl:value-of select="//gml:beginPosition" /></xsl:element>
		<xsl:element name="time_coverage_end"><xsl:value-of select="//gml:endPosition" /></xsl:element>
	</xsl:element>
</xsl:template>

<xsl:template match="gmd:transferOptions">
    <xsl:for-each select=".//gmd:URL">
        <xsl:if test="contains(.,'fileServer')">
            <xsl:element name="URLFileserver"><xsl:value-of select="(.)" /></xsl:element>
        </xsl:if>
    </xsl:for-each>
</xsl:template>
</xsl:stylesheet> 
