.. |Discovery| replace:: NetCDF Attribute Convention for Dataset Discovery
.. |Crosswalk| replace:: NOAA's NetCDF Attribute Convention for Dataset Discovery Conformance Test
.. |ODbL| replace:: Open Database License
.. |CF_vocab| replace:: CF standard names v.18
.. |UTM_vocab| replace:: vocabulario usado en la UTM
.. |cdm| replace:: Tipo de datos Thredds

.. _Discovery: http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html
.. _Crosswalk: https://geo-ide.noaa.gov/wiki/index.php?title=NetCDF_Attribute_Convention_for_Dataset_Discovery_Conformance_Test
.. _ODbL: http://opendatacommons.org/licenses/odbl/
.. _ISO8601: http://es.wikipedia.org/wiki/ISO_8601
.. _CF_vocab: http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/18/cf-standard-name-table.html
.. _UTM_vocab: http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml
.. _cdm: http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#cdm_data_type_Attribute


Preparación de los datos netCDF
===============================

El *Data Portal* puede realizar búsquedas por ámbito geográfico, período temporal, conjunto de variables, o por texto libre, dentro de un conjunto de ficheros netCDF. Para que los datos originales sean recuperables mediante estos criterios, es necesario que éstos estén convenientemente descritos.

A tal efecto, se han seguido las convenciones |Discovery|_. En concreto, es necesario que los ficheros netCDF contengan *al menos* los siguientes atributos globales:


Atributos Globales Mínimos
--------------------------

========================  =========================================================
Atributo                  Descripción
========================  =========================================================
Metadata_Conventions      Literal: "Unidata Dataset Discovery v1.0".
id                        Identificador de este dataset. Idealmente un DOI.
                          De momento un UUID.
naming_authority          Junto con *id*, debe formar un identificador
                          global único. Como UUID ya es global y único *per se*,
                          pondremos el literal "UUID", que indica su tipo.
title                     Descripción concisa del conjunto de datos.
summary                   Un párrafo describiendo los datos con mayor
                          detalle.
keywords                  Una lista de palabras clave separada por comas.
standard_name_vocabulary  Idealmente se usaría el estándar |CF_vocab|_.
                          Si esto no es posible, crearemos nuestro propio
                          vocabulario "ICOS-SPAIN", ampliando el |UTM_vocab|_.
                          Este atributo contendrá la URL donde pueda descargarse
                          el vocabulario.
license                   Licencia de uso de los datos. Se recomienda la |ODbL|_.
geospatial_lat_min        Límites geográficos en los que están contenidas
                          las medidas, coordenadas geográficas en grados
                          decimales, sistema de referencia WGS84 (EPSG:4326).
geospatial_lat_max        *idem*
geospatial_lon_min        *idem* 
geospatial_lon_max        *idem*
time_coverage_start       Instante del dato más remoto, en formato ISO8601_.
time_coverage_end         Instante del dato más reciente, en formato ISO8601_.
institution               Institución que publica los datos.
creator_url               Dirección web de la istitución.
cdm_data_type             |cdm|_. Uno de "Grid", "Image", "Station", "Radial"
                          o "Trajectory".
icos_domain               Atributo propio (no responde a ninguna convención),
                          que necesitamos a efectos estadísticos.
========================  =========================================================


Lista de valores de 'institution' y 'creator_url'
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * "Unidad de Tecnología Marina (UTM), CSIC" => http://www.utm.csic.es/
  * "Centro de Estudios Ambientales del Mediterráneo (CEAM)" => http://www.ceam.es/
  * "Centro de Investigación Atmosférica de Izaña, AEMET" => http://www.izana.org/
  * "Institut Català de Ciències del Clima (IC3)" => http://www.ic3.cat/
  * **... TODO: completar con lista de contribuidores en ICOS-SPAIN.**

  
Lista de valores de 'icos_domain'
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  * "environment" (terrestre/ecosistemas)
  * "atmosphere"
  * "oceans"
  

Atributos Globales Recomendados
-------------------------------

Se recomienda seguir el resto de las convenciones |Discovery|_, en especial se recomienda
incluir **alguna manera de contactar** con el creador o el publicador de los datos.

Ejemplo de un set de atributos globales según las |Discovery|_, expresados en *ncML*::

  <attribute name="Metadata_Conventions" value="Unidata Dataset Discovery v1.0" />
  <attribute name="id" value="icos-sample-1" />
  <attribute name="naming_authority" value="co.geomati" />
  <attribute name="title" value="Global Wind-Wave Forecast Model and Ocean Wave model" />
  <attribute name="summary" value="This is a sample global ocean file that comes with default Thredds installation. Its global attributes have been modified to conform with Dataset Discovery convention" />
  <attribute name="keywords" value="ocean, sample, metadata, icos, geomati.co" />
  <attribute name="license" value="Open Database License v.1.0" />
  <attribute name="standard_name_vocabulary" value="UTM.CSIC.ES" />
  <attribute name="geospatial_lat_min" value="40" />
  <attribute name="geospatial_lat_max" value="50" />
  <attribute name="geospatial_lon_min" value="-10" />
  <attribute name="geospatial_lon_max" value="10" />
  <attribute name="time_coverage_start" value="2011-07-10T12:00" />
  <attribute name="time_coverage_end" value="2011-07-15T12:00" />
  
  <attribute name="geospatial_vertical_min" value="-1" />
  <attribute name="geospatial_vertical_max" value="1" />
  <attribute name="geospatial_vertical_units" value="m" />
  <attribute name="geospatial_vertical_resolution" value="1" />
  <attribute name="geospatial_vertical_positive" value="up" />
  <attribute name="time_coverage_duration" value="P10D" />
  <attribute name="time_coverage_resolution" value="1" />

  <attribute name="Metadata_Link" value="http://ciclope.cmima.csic.es:8080/thredds/iso/wcsExample/ocean-metadata-sample.nc" />
  <attribute name="history" value="2011-08-16 - metadata changed to conform to data portal specification" />
  <attribute name="comment" value="Half of this metadata is fake" />

  <attribute name="creator_name" value="gribtocdl" />
  <attribute name="creator_url" value="http://www.unidata.ucar.edu/projects/THREDDS/" />
  <attribute name="creator_email" value="support@unidata.ucar.edu" />
  <attribute name="institution" value="unidata" />
  <attribute name="date_created" value="2003-04-02 12:12:50" />
  <attribute name="date_modified" value="2011-07-15 11:00:00" />
  <attribute name="date_issued" value="2011-07-10 12:00:00" />
  <attribute name="project" value="ICOS-SPAIN" />
  <attribute name="acknowledgement" value="UTM/CSIC" />
  <attribute name="contributor_name" value="Oscar Fonts" />
  <attribute name="contributor_role" value="Inventing Discovery Metadata Attributes" />
  <attribute name="publisher_name" value="UTM/CMIMA/CSIC" />
  <attribute name="publisher_url" value="http://www.utm.csic.es/" />
  <attribute name="publisher_email" value="esto@un.correo.es" />
  <attribute name="publisher_email" value="esto@un.correo.es" />

  <attribute name="processing_level" value="Who knows" />
  <attribute name="cdm_data_type" value="Grid" />
  <attribute name="record" value="reftime, valtime" />
  
  <attribute name="Conventions" value="NUWG" />
  <attribute name="GRIB_reference" value="Office Note 388 GRIB" />
  <attribute name="GRIB_URL" value="http://www.nco.ncep.noaa.gov/pmb/docs/on388/" />
  <attribute name="version" type="double" value="0.0" />

  
Tabla equivalencias metadatos
-----------------------------

A lo largo de la aplicación, estos **atributos netCDF** se transformarán en una **fichas ISO** en un proceso de harvesting. El servicio **search** podrá filtrar por alguno de estos conceptos vía **parámetros de búsqueda**, que se convertirán en un **request CSW**, y que dará lugar a unos **resultados de búsqueda**. Estos resultados, además, serán **ordenables** por alguno de los parámetros.

Puesto que en cada etapa estos metadatos siguen esquemas y toman nombres distintos, se detalla aquí una tabla de equivalencias (ISO basado en |Crosswalk|_):

.. list-table:: *Equivalencia entre los nombres de los metadatos en diferentes fases del proceso*
   :header-rows: 1
   
   * - netCDF global attribute
     - ISO 19115:2003/19139
     - Search request param
     - CSW Filter
     - Search order-by param
     - Search result field
   * - id
     - /gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString
     - -- (no se busca por id, de momento)
     - Operación *getRecordById*.
     - id
     - id
   * - title
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString
     - text (texto libre)
     - any
     - title
     - title
   * - summary
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString
     - text (texto libre)
     - any
     - -- (no se ordena por resumen)
     - summary
   * - geospatial_lat_min, geospatial_lat_max, geospatial_lon_min, geospatial_lon_max
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal
       /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal
       /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLongitude/gco:Decimal
       /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLongitude/gco:Decimal
     - bboxes (múltiples)
     - OGC BBOX filter
     - -- (no se ordena por bbox)
     - geo_extent (WKT)
   * - time_coverage_start
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition
     - start_date
     - TempExtent_begin
     - start_time
     - start_time
   * - time_coverage_end
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition
     - end_date
     - TempExtent_end
     - end_time
     - end_time
   * - variables (*no son atributos*)
     - /gmd:MD_Metadata/gmd:contentInfo/gmi:MI_CoverageDescription/gmd:dimension/gmd:MD_Band/gmd:sequenceIdentifier/gco:MemberName/gco:aName/gco:CharacterString
     - variables (multiselect)
     - ContentInfo
     - -- (no se ordena por variables)
     - variables
   * - institution
     - 	/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString
     - -- (no se busca por institucion)
     - -- (no se filtra por institucion)
     - -- (no se ordena por institucion)
     - institution
   * - creator_url
     - /gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL
     - -- (no se busca por URL de institución)
     - -- (no se filtra por URL de institución)
     - -- (no se ordena por URL de institución)
     - creator_url
   * - cdm_data_type
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode *May need some extensions to this codelist. Current values: vector, grid, textTable, tin, stereoModel, video.*
     - -- (no se busca por tipo)
     - -- (no se filtra por tipo)
     - -- (no se ordena por tipo)
     - data_type
   * - icos_domain
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode
     - -- (no se busca por área temática)
     - -- (no se filtra por área temática)
     - -- (no se ordena por área temática)
     - icos_domain