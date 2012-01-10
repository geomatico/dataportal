==============================
GeoNetwork configuration files
==============================

Disclaimer:
This is only a quick and dirty installation guide for those who need to configure GeoNetwork quickly.
If you want to hack & tweak, we *do* have an excellent documentation elsewhere.

Custom GetRecordById response
-----------------------------

1. Replaced the iso-brief.xsl with this iso-brief.xsl file in folder $TOMCAT_HOME/webapps/geonetwork/xml/schemas/iso19139/present/csw/ 

Now all the *brief* GetRecordById responses be served with the structure defined in iso-brief.xsl.    

Custom Thredds to GeoNetwork harvesting
---------------------------------------

1. Copy the Base Template "thredds-harvester-unidata-data-discovery-geomatico.xml" to:
$TOMCAT_HOME/webapps/geonetwork/xml/schemas/iso19139/templates/thredds-harvester-unidata-data-discovery-geomatico.xml

2. Copy the Fragments Subtemplate "netcdf-attributes-geomatico.xsl" to:
$TOMCAT_HOME/webapps/geonetwork/xml/schemas/iso19139/convert/ThreddsToFragments/netcdf-attributes-geomatico.xsl

3. Go to "Administration" => "Metadata & Template". Select "iso19139" and "Add templates".

4. Go to "Administration" => "Harvesting Management" and add a "Thredds Catalog" harvesting task. The following table specifies the different inputs used in an ICOS configuration:

========================================================== =======================================================
Parameter                                                  Value
========================================================== =======================================================
Name                                                       Icos Data Portal
Catalog URL                                                http://{dataportal_base_url}/thredds/catalog.xml
ISO topic                                                  environment
Create metadata for Atomic Datasets                        checked
Extract Unidata dataset discovery metadata using fragments checked
Select schema for output metadata records                  iso19139
Stylesheet to create metadata fragments                    netcdf-attributes-geomatico
Create subtemplates for fragments...                       HARVESTING TEMPLATE - geomatico
Create thumbnails for any datasets delivered by WMS        checked
Privileges                                                 Add all permissions to "All" group
Category for service                                       Interactive resources
Category for datasets                                      Datasets
========================================================== =======================================================

Further details on how Thredds Harvesting works and how to customize it:
http://{dataportal_base_url}/doc/install.html#creacion-y-configuracion-del-proceso-de-harvesting


Custom Lucene indexing for search services
------------------------------------------

1. Copy the Indexer Configuration "index-fields.xsl" to:
$TOMCAT_HOME/webapps/geonetwork/xml/schemas/iso19139/index-fields.xsl

2. Go to "Administration" => "Index settings" => "Rebuild"

3. Copy the CSW Configuration File "config-csw.xml" to:
$TOMCAT_HOME/webapps/geonetwork/WEB-INF/config-csw.xml

4. Restart Tomcat

5. Do a GetCapabilities http://{host:port}/geonetwork/srv/en/csw?service=CSW&request=GetCapabilities and search for "ContentInfo" to verify it is working.


Further details on how Lucene Indexation works and how to customize it:
http://{dataportal_base_url}/doc/search-service.html#configuracion-de-gn-para-busqueda-por-variables

H2 Connection
------------------------------------------

In case of using H2 connection with Geonetwork it is recommended to add the MVCC option to the database URL, in order to avoid some
database locking related problems.

In order to do so, just edit the $TOMCAT_HOME/webapps/geonetwork/WEB-INF/config.xml file and replace the following line::

    <url>jdbc:h2:geonetwork</url>

with this one:

    <url>jdbc:h2:geonetwork;MVCC=TRUE</url>
