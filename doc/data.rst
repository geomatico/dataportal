.. |Discovery| replace:: NetCDF Attribute Convention for Dataset Discovery
.. |ODbL| replace:: Open Database License

.. _Discovery: http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html
.. _ODbL: http://opendatacommons.org/licenses/odbl/
.. _ISO8601: http://es.wikipedia.org/wiki/ISO_8601


Preparación de los datos netCDF
===============================

El *Data Portal* puede realizar búsquedas por ámbito geográfico, período temporal, conjunto de variables, o por texto libre, dentro de un conjunto de ficheros netCDF. Para que los datos originales sean recuperables mediante estos criterios, es necesario que éstos estén convenientemente descritos.

A tal efecto, se han seguido las convenciones |Discovery|_. En concreto, es necesario que los ficheros netCDF contengan *al menos* los siguientes atributos globales:


Atributos Globales Mínimos
--------------------------


========================  =================================================
Atributo                  Descripción
========================  =================================================
Metadata_Conventions      Literal: "Unidata Dataset Discovery v1.0".
id                        Identificador de este dataset.
naming_authority          Junto con *id*, deben formar un identificador
                          global único. Se recomienda usar dominio DNS
                          inverso, como en paquetes Java. En caso de usar
                          DOI, ver qué ponemos aquí.
title                     Descripción concisa del conjunto de datos
summary                   Un párrafo describiendo los datos con mayor
                          detalle.
keywords                  Una lista de palabras clave separada por comas.
license                   Licencia de uso de los datos. Se recomienda la |ODbL|_.
standard_name_vocabulary  De momento se acepta "UTM.CSIC.ES", que se refiere
                          a `este vocabulario 
                          <http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml>`_.
                          Quizá en algún momento se considere ampliar al más
                          estándar `"CF-1.0"
                          <http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/18/cf-standard-name-table.html>`_
geospatial_lat_min        Límites geográficos en los que están contenidas
                          las medidas, coordenadas geográficas en grados
                          decimales, sistema de referencia WGS84 (EPSG:4326).
geospatial_lat_max        *idem*
geospatial_lon_min        *idem* 
geospatial_lon_max        *idem*
time_coverage_start       Instante del dato más remoto, en formato ISO8601_.
time_coverage_end         Instante del dato más reciente, en formato ISO8601_.
========================  =================================================


Atributos Globales Recomendados
-------------------------------

Se recomienda seguir el resto de las convenciones |Discovery|_, en especial se recomienda incluir **alguna manera de contactar** con el creador o el publicador de los datos.

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

Puesto que en cada etapa estos metadatos toman nombres distintos, se detalla aquí una tabla-resumen:

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
     - (equivaldría a un *getRecordById*, o algo así) CONFIRMAR
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
     - ¿¿¿???
     - -- (no se ordena por bbox)
     - geo_extent (WKT)
   * - time_coverage_start
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition
     - start_date
     - ¿¿¿???
     - start_time
     - start_time
   * - time_coverage_end
     - /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition
     - end_date
     - ¿¿¿???
     - end_time
     - end_time
   * - -- (proceso harvesting extraería las variables, de algún modo...)
     - ISO TODO
     - variables
     - CSW TODO
     - -- (no se ordena por variables)
     - variables

     
FALTA DOCUMENTAR: *totalcount* (lo genera servicio CSW de GN) y *data_link* (lo genera proceso de harvesting).
