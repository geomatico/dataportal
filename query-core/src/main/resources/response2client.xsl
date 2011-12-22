<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml"
	xmlns:geonet="http://www.fao.org/geonetwork" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gmi="http://www.isotc211.org/2005/gmi">

	<xsl:template match="csw:GetRecordsResponse/csw:SearchResults">
		<xsl:element name="response">
			<xsl:attribute name="totalcount"><xsl:value-of
				select="@numberOfRecordsMatched" /></xsl:attribute>
			<xsl:attribute name="success">true</xsl:attribute>
			<xsl:apply-templates select="./gmd:MD_Metadata" />
		</xsl:element>
	</xsl:template>
	
	<!-- Transformation to GetRecordById response  -->
	
	<xsl:template match="csw:GetRecordByIdResponse">
        <xsl:element name="response">
            <xsl:attribute name="totalcount"><xsl:value-of
                select="count(//gmd:MD_Metadata)" /></xsl:attribute>
            <xsl:attribute name="success">true</xsl:attribute>
            <xsl:apply-templates select="./gmd:MD_Metadata" />
        </xsl:element>
    </xsl:template>

	<xsl:template match="gmd:MD_Metadata">
	<xsl:element name="item">
	<xsl:element name="id">
		<xsl:value-of select="gmd:fileIdentifier/gco:CharacterString" /></xsl:element>
		<xsl:apply-templates select="./gmd:identificationInfo/gmd:MD_DataIdentification" />
		<xsl:apply-templates select="./gmd:contentInfo"></xsl:apply-templates>
		<xsl:apply-templates select="./gmd:distributionInfo//gmd:transferOptions"></xsl:apply-templates>
	</xsl:element>
	</xsl:template>

	<xsl:template match="gmd:MD_DataIdentification">
		<xsl:element name="title"><xsl:value-of select="./gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString" /></xsl:element>
		<xsl:element name="summary"><xsl:value-of select="./gmd:abstract/gco:CharacterString" /></xsl:element>
		<xsl:element name="institution"><xsl:value-of select="./gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString" /></xsl:element>
		<xsl:element name="creator_url"><xsl:value-of select="./gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL" /></xsl:element>
		<xsl:element name="data_type"><xsl:value-of select="./gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode" /></xsl:element>
		<xsl:element name="icos_domain"><xsl:value-of select="./gmd:topicCategory/gmd:MD_TopicCategoryCode" /></xsl:element>
		<xsl:apply-templates select="./gmd:extent/gmd:EX_Extent" />
	</xsl:template>

	<xsl:template match="gmd:EX_Extent">
		<xsl:apply-templates select="./gmd:geographicElement/gmd:EX_GeographicBoundingBox" />
		<xsl:apply-templates select="./gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod" />
	</xsl:template>

	<xsl:template match="gmd:EX_GeographicBoundingBox">
		<xsl:variable name="norte" select="./gmd:northBoundLatitude/gco:Decimal" />
		<xsl:variable name="sur" select="./gmd:southBoundLatitude/gco:Decimal" />
		<xsl:variable name="este" select="./gmd:eastBoundLongitude/gco:Decimal" />
		<xsl:variable name="oeste" select="./gmd:westBoundLongitude/gco:Decimal" />
		<xsl:element name="geo_extent">POLYGON((<xsl:value-of select="concat($oeste,' ',$norte,',',$este,' ',$norte,',',$este,' ',$sur,',',$oeste,' ',$sur,',', $oeste,' ',$norte)" />))</xsl:element>
	</xsl:template>

	<xsl:template match="gml:TimePeriod">
		<xsl:element name="start_time"><xsl:value-of select="./gml:beginPosition" /></xsl:element>
		<xsl:element name="end_time"><xsl:value-of select="./gml:endPosition" /></xsl:element>
	</xsl:template>

	<xsl:template match="gmd:transferOptions">
		<xsl:for-each select=".//gmd:URL">
			<xsl:if test="contains(.,'fileServer')">
				<xsl:element name="data_link">
					<xsl:value-of select="(.)" />
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="gmd:contentInfo">
		<xsl:variable name="variables">
			<xsl:for-each select="gmi:MI_CoverageDescription//gmd:dimension">
				<xsl:value-of
					select="concat(',',gmd:MD_Band/gmd:sequenceIdentifier/gco:MemberName/gco:aName/gco:CharacterString)"></xsl:value-of>
			</xsl:for-each>
		</xsl:variable>
		<xsl:element name="variables"><xsl:value-of select="substring($variables, 2)"></xsl:value-of></xsl:element>
	</xsl:template>

</xsl:stylesheet> 
