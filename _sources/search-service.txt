.. _vocabulario.xml: http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml
.. _ISO8601: http://es.wikipedia.org/wiki/ISO_8601
.. _WKT: http://en.wikipedia.org/wiki/Well-known_text
.. _UTC: http://en.wikipedia.org/wiki/Coordinated_Universal_Time
.. _Luke: http://code.google.com/p/luke/downloads/list

.. |GN|  replace:: *GeoNetwork*
.. |DP|  replace:: *Data Portal*


Servicio "search" de búsqueda de datos
======================================

Este apartado define la interfaz del servicio de búsqueda *search*, tanto desde el punto de vista de la interfaz gráfica de usuario (su aspecto exterior), como los formatos de intercambio de información entre el cliente y el servidor.



Interfaz cliente-servidor
-------------------------

Las peticiones de búsqueda se harán mediante una peticion HTTP GET o POST a la siguiente url::

  [url_base]/search

Donde [url_base] es la URL donde está desplegada la aplicación. Por ejemplo::

  http://ciclope.cmima.csic.es:8080/dataportal/search


Tanto los parámetros de la petición como las respuestas se codificarán siempre en **UTF-8**.
  

Parámetros de la petición
^^^^^^^^^^^^^^^^^^^^^^^^^

Los parámetros que debe aceptar el servicio pueden dividirse en *parámetros de búsqueda*, *parámetros de paginación* y *parámetros de ordenación*.


Los *parametros de búsqueda* son:

  - **text**: Texto libre, a buscar entre cualquiera de los metadatos de tipo textual. Codificado en UTF-8.
  - **bboxes**: Lista de bounding boxes en EPSG:4326 expresada como un array, de formato::
  
      [[Xmin0,Ymin0,Xmax0,Ymax0],[Xmin1,Ymin1,Xmax1,Ymax1],...,[XminN,YminN,XmaxN,YmaxN]]
      
  - **start_date**: Formato "YYYY-MM-DD" (formato de fecha de calendario ISO8601_).
  - **end_date**: Formato "YYYY-MM-DD" (formato de fecha de calendario ISO8601_).
  - **variables**: Lista de variables separada por comas, de entre una lista controlada de valores. Los posibles valores son los distintos <nc_term> en `vocabulario.xml`_. Ejemplo::
  
      "sea_water_salinity,sea_water_temperature,sea_water_electrical_conductivity"
      
  - **response_format**: Formato MIME en que se solicita la respuesta. De momento sólo está definido el formato *text/xml*.
  
  
*Parámetros de paginación*:

  - **start**: Primer registro solicitado (offset, comenzando a contar por cero).
  - **limit**: Número máximo de resultados por página.
  
Por ejemplo, para solicitar la 3ª página a 20 resultados por página, los parámetros serían: *start=40* y *limit=20*.

  
*Parámetros de ordenación*:
  
  - **sort**: nombre del campo por el que se ordenan los elementos de la respuesta. Los posibles valores son:
    - "id",
    - "title",
    - "start_time", y
    - "end_time".
  - **dir**: "ASC" (orden ascendente) o "DESC" (orden descendente).

Por ejemplo, para obtener los registros más recientes primero, los parámetros serían: *sort=start_time* & *dir=DESC*.


Elementos de la respuesta
^^^^^^^^^^^^^^^^^^^^^^^^^

  - Flag **success**. Será *true* si todo ha ido bien, o *false* si ha habido algún error realizando la petición.
  
  - Atributo global **totalcount**. Se refiere al número total de resultados que cumplen los criterios de búsqueda, no contenidos en una respuesta específica. Por ejemplo, en una respuesta paginada donde se devuelvan 20 resultados por página, **totalcount** puede ser 1200 (así, toda la respuesta ocuparía 60 páginas de resultados).
  
  - Lista de resultados. Cada uno de ellos tendrá los siguientes elementos:

    - **id**: Identificador.
    - **title**: Título.
    - **summary**: Resumen.
    - **geo_extent**: Ámbito geográfico, expresado en WKT_. Se considerarán sólamente las primitivas geométricas 2D "POINT", "LINESTRING", "POLYGON" y "MULTIPOLYGON", expresadas en coordenadas geográficas WGS84 (EPSG:4326). Generalmente se tratará de un "POLYGON" de 4 vértices describiendo un BBOX.
    - **start_time** y **end_time**, en formato ISO8601_. Fracción horaria expresada en UTC_ (aka "Z").
    - **variables**: Lista de variables contenida en el dataset, separada por comas. Se espera que corresponda a una lista controlada de valores. Los posibles valores son los distintos <nc_term> de `vocabulario.xml`_.
    - **data_link**: Enlace a los datos.

    
En caso de error, el flag **success** será *false*, y la respuesta contendrá un error con los siguientes elementos:

  - **code**: Código del error. A criterio del desarrollador, debería servir para determinar la causa.
  - **message**: Mensaje amable para mostrar al usuario frustrado.

    
Respuesta codificada en "text/xml"
""""""""""""""""""""""""""""""""""

Ejemplo de respuesta XML válida::

    <?xml version="1.0" encoding="UTF-8"?>
    <response success="true" totalcount="330">
        <item>
            <id>5df54bf0-3a7d-44bf-9abf-84d772da8df1</id>
            <title>Global Wind-Wave Forecast Model and Ocean Wave model</title>
            <summary>This is a sample global ocean file that comes with default Thredds installation. Its global attributes have been modified to conform with Dataset Discovery convention</summary>
            <geo_extent>POLYGON ((-10 50, 10 50, 10 40, -10 40, -10 50))</geo_extent>
            <start_time>2011-07-10T12:00:00Z</start_time>
            <end_time>2011-07-15T12:00:00Z</end_time>
            <variables>sea_water_salinity,sea_water_temperature,sea_water_electrical_conductivity</variables>
            <data_link>http://ciclope.cmima.csic.es:8080/thredds/fileServer/cmimaTest/ocean-metadata-sample.nc</data_link>
        </item>
        <item>
            <id>5df54bf0-3a7d-44bf-9abf-84d772da8df1</id>
            <title>Global Wind-Wave Forecast Model and Ocean Wave model</title>
            <summary>This is a sample global ocean file that comes with default Thredds installation. Its global attributes have been modified to conform with Dataset Discovery convention</summary>
            <geo_extent>POLYGON ((-10 50, 10 50, 10 40, -10 40, -10 50))</geo_extent>
            <start_time>2011-07-10T12:00:00Z</start_time>
            <end_time>2011-07-15T12:00:00Z</end_time>
            <variables>sea_water_salinity,sea_water_temperature,sea_water_electrical_conductivity</variables>
            <data_link>http://ciclope.cmima.csic.es:8080/thredds/fileServer/cmimaTest/ocean-metadata-sample.nc</data_link>
        </item>
    </response>

    
Respuesta XML en caso de error::

    <?xml version="1.0" encoding="UTF-8"?>
    <response success="false">
        <error>
            <code>java.lang.NullPointerException</code>
            <message>Ooops, something went wrong here and there. Please do, or don't.</message>
        </error>
    </response>

Configuración de |GN| para búsqueda por variables
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
|GN| incluye el motor de búsqueda Apache Lucene para realizar búsquedas dentro del catálogo. Lucene está configurado para que estas búsquedas puedan ser realizadas por unos determinados campos. Para conseguir realizar búsquedas por un tipo de campos no incluidos, habrá que modificar |GN|. Los siguientes pasos indican como se ha de realizar esta modificación.

Indexación del campo en Lucene
""""""""""""""""""""""""""""""
En primer lugar indicaremos que nuevos campos han de ser indexados. Para ello en cada carpeta de esquema existe un archivo ``index-fields.xsl`` en el que se indica que campos han de ser indexados. La ruta para el esquema ISO19139 con el que estamos trabajando en el |DP| será::

	/var/lib/tomcat6/webapps/geonetwork/xml/schemas/iso19139/index-fields.xsl

Este archivo se trata de una plantilla XSLT que se encarga de extraer datos de nuestros metadatos en incluirlos en el índice de Lucene. Para realizar esta acción, debemos indicar que valores queremos que se incluyan en el índice, el nombre del campo en los que se incluirá y una serie de parametros que indican como han de incluirse estos:

* **store**: guarda el valor del campo en el índice
* **index**: indexa el campo, casi siempre será ``true``
* **token**: extrae el valor del campo en pequeños trozos (tokens)::
	
	P.ej.: 'es un valor de campo' --> 'es','un','valor','de','campo'

El fragmento de plantilla que debe quedar trás la ejecución de la plantilla será::

	<Field name="<nombre del campo>" string="{<valor del campo>}"
	store="true" index="true"
	token="true"/>

Así estamos indicando que cree un campo ``<nombre del campo>`` en el índice y que incluya el valor ``<valor del campo>`` en el mismo. Esta última será una expresión XPATH que recoja del metadato en el esquema que estamos trabajando, la información que nos interesa. En el caso del |DP| quedaría de la siguiente manera::

	<xsl:for-each select="//gmd:contentInfo/gmi:MI_CoverageDescription/gmd:dimension">
	<Field name="variable" token="false" store="true" index="true" string="{string(gmd:MD_Band/gmd:sequenceIdentifier/gco:MemberName/gco:aName/gco:CharacterString)}" />
	</xsl:for-each>

Así estamos indicando que para cada valor que aparezca de ``gmd:Dimension`` incluya este en un campo denominado ``variable`` en el índice. 
Una vez que hemos modificado el archivo ``index-fields.xsl`` debemos re-indexar todos los metadatos con la nueva disposición. Para ello iremos a la sección **Index settings** de la ventana de administración y en ``Rebuild Lucene index`` indicaremos ``Rebuild``. Esta operación tardará en función del número de metadatos que tengamos en el servidor.

Una vez que esta operación ha finalizado, debemos comprobar si las nuevas variables han sido indexadas. Para ello desargaremos la herramienta Luke_ - Lucene Index Toolbox, que nos permitirá explorar el indice y comprobar los cambios que hemos realizado. Una vez descargada, navegaremos hasta el directorio, modificaremos los permisos dandole de ejecución y ejecutaremos::

	$ java -jar lukeall-3.3.0.jar

Lo primero que nos indicará la aplicación es que seleccionemos la ruta donde se encuentra el índice, que en nuestro caso será::

	$TOMCAT_HOME/webapps/geonetwork/WEB-INF/lucene/nonspatial

.. image:: img/luke-lucene.png
		:width: 500 px
		:alt: Cáptura inicio Luke
		:align: center

Una vez abierto el índice, en la pestaña ``overview``, podremos comprobar que el campo se encuentra indexado, y visualizar los términos que ha guardado. También podremos ir a la pestaña ``search`` y realizar unas pruebas de búsqueda para el campo que acabamos de incluir.

Inclusión del campo en servicio CSW
"""""""""""""""""""""""""""""""""""
Una vez que hemos indexado el campo de búsqueda, debemos incluir este como campo soportado en las búsquedas a través del servicio CSW. Para ello modificaremos el archivo config-csw.xml que se encuentra en::

	$TOMCAT_HOME/webapps/geonetwork/WEB-INF/config-csw.xml

Debemos añadir dentro de los campos	``Additional queryable properties`` la linea que incluya nuestro campo como consultable. Para ello añadiremos en esa sección::

	<parameter name="<Nombre del campo dentro del esquema>" field="<nombre del campo incluido en el índice>" type="SupportedISOQueryables" />

Que para el caso del |DP| quedaría::

	<parameter name="ContentInfo" field="variable" type="SupportedISOQueryables" />

De esta manera, si consultamos del GetCapabilities del servidor observaremos si el campo ha sido incluido::

	<ows:Constraint name="SupportedISOQueryables">
	...
	<ows:Value>ContentInfo</ows:Value>
	...

Ya tendremos preparado nuestro servidor para realizar consultas por ``ContenInfo``.
    
Interfaz gráfica de usuario
---------------------------

TODO tras tarea 3.3.


Formulario de búsqueda
^^^^^^^^^^^^^^^^^^^^^^

TODO tras tarea 3.3.


Tabla de resultados
^^^^^^^^^^^^^^^^^^^

TODO tras tarea 3.3.
