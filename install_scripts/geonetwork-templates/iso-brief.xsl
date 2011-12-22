<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
										xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
										xmlns:gmd="http://www.isotc211.org/2005/gmd"
										xmlns:gco="http://www.isotc211.org/2005/gco"
										xmlns:srv="http://www.isotc211.org/2005/srv"
										xmlns:ows="http://www.opengis.net/ows"
										xmlns:geonet="http://www.fao.org/geonetwork"
										xmlns:gml="http://www.opengis.net/gml">
	
	<!-- =================================================================== -->

	<xsl:template match="gmd:MD_Metadata|*[@gco:isoType='gmd:MD_Metadata']">
		<xsl:copy>
			<xsl:apply-templates select="gmd:fileIdentifier"/>
			<xsl:apply-templates select="gmd:hierarchyLevel"/>
			<xsl:apply-templates select="gmd:identificationInfo"/>
			<xsl:apply-templates select="gmd:contentInfo" />	
			<xsl:apply-templates select="gmd:distributionInfo" />			
		</xsl:copy>
	</xsl:template>

	<!-- =================================================================== -->

	<xsl:template match="gmd:MD_DataIdentification|
		*[@gco:isoType='gmd:MD_DataIdentification']">
		<xsl:copy>
			<xsl:apply-templates select="gmd:citation"/>
			<xsl:apply-templates select="gmd:abstract"/>	
			<xsl:apply-templates select="gmd:spatialRepresentationType"/>
			<xsl:apply-templates select="gmd:topicCategory"/>		
			<xsl:apply-templates select="gmd:extent[child::gmd:EX_Extent]"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- =================================================================== -->
	
	<xsl:template match="gmd:EX_Extent">
        <xsl:copy>
        	<xsl:apply-templates select="gmd:geographicElement[child::gmd:EX_GeographicBoundingBox]"/>
        	<xsl:apply-templates select="gmd:temporalElement[child::gmd:EX_TemporalExtent[child::gmd:extent[child::gml:TimePeriod]]]" />
        </xsl:copy>
	</xsl:template>
	
	<xsl:template match="gmd:EX_GeographicBoundingBox">
        <xsl:copy>
            <xsl:apply-templates select="gmd:westBoundLongitude"/>
        	<xsl:apply-templates select="gmd:southBoundLatitude"/>
        	<xsl:apply-templates select="gmd:eastBoundLongitude"/>
        	<xsl:apply-templates select="gmd:northBoundLatitude"/>
        </xsl:copy>
	</xsl:template>
	
	<xsl:template match="gml:TimePeriod">
	   <xsl:copy>
	       <xsl:apply-templates select="gml:beginPosition"/>
	       <xsl:apply-templates select="gml:endPosition"/>
	   </xsl:copy>
	</xsl:template>
	
	<!-- =================================================================== -->
	
	<xsl:template match="gmd:CI_Citation">
        <xsl:copy>
        	<xsl:apply-templates select="gmd:title"/>
        	<xsl:apply-templates select="gmd:citedResponsibleParty"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- =================================================================== -->
    
    <xsl:template match="gmd:MD_Distribution">
        <xsl:copy>
            <xsl:apply-templates select="gmd:transferOptions" />
        </xsl:copy>
    </xsl:template>
	
	<!-- === copy template ================================================= -->

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- =================================================================== -->

</xsl:stylesheet>



