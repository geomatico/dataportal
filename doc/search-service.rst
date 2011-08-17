.. _vocabulario.xml: http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml
.. _ISO8601: http://es.wikipedia.org/wiki/ISO_8601
.. _WKT: http://en.wikipedia.org/wiki/Well-known_text
.. _UTC: http://en.wikipedia.org/wiki/Coordinated_Universal_Time


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

  - **start**: Primer registro solicitado (offset, comenzando a contar con 1).
  - **limit**: Número máximo de resultados por página.
  
Por ejemplo, para solicitar la 3ª página a 20 resultados por página, los parámetros serían: *start=41* & *limit=20*.

  
*Parámetros de ordenación*:
  
  - **sort**: nombre del campo por el que se ordenan los elementos de la respuesta. Los posibles valores son:
    - "id",
    - "title",
    - "start_time", y
    - "end_time".
  - **dir**: "ASC" (orden ascendente) o "DESC" (orden descendente).

Por ejemplo, para obtener los datos más recientes primero, los parámetros serían: *sort=start_time* & *dir=DESC*.


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

    
Interfaz gráfica de usuario
---------------------------

TODO tras tarea 3.3.


Formulario de búsqueda
^^^^^^^^^^^^^^^^^^^^^^

TODO tras tarea 3.3.


Tabla de resultados
^^^^^^^^^^^^^^^^^^^

TODO tras tarea 3.3.