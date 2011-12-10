Propuesta convención CF
=======================

Convención usada actualmente
::::::::::::::::::::::::::::

Los ficheros NetCDF existentes en la instancia de Thredds de cíclope especifican un atributo "conventions=Unidata Observation Dataset v1.0".

Dicha norma está desaconsejada en favor de CF Conventions for Point Observations[1]:

* This Conventions is deprecated in favor of the CF Conventions for Point Observations 

y las CF Conventions for Point Observations forman parte del "draft incompleto" de la CF-1.6. Para este documento se ha trabajado con la versión 1.5 de CF, ignorando los trabajos en curso de la 1.6

En cuanto a los atributos globales, se sigue la norma NetCDF Attribute Convention for Dataset Discovery, en la que se referencia en la cabecera un vocabulario[2], pero las variables no contienen un atributo *standard_name* que haga referencia a las variables definidas en dicho vocabulario, como se recomienda en dicha norma.

Las entradas del vocabulario no deberían tener unidades, ya que es posible tomar datos de la misma variable en medidas distintas. Por ejemplo dos instituciones distintas pueden producir información de profundidad, una en metros y la otra en kilómetros.

Propuesta de convención
:::::::::::::::::::::::

Análisis de los casos de uso
-----------------------------

Además de los casos de uso propios de la fase 1, que requerían atributos globales para poder catalogar los datos en geonetwork, se requiere:

- Representar gráficamente las variables:

 - Saber qué variables contienen los valores de las coordenadas (lat, lon, vertical y tiempo)
 - Extraer las coordenadas en las que se dan los valores de las variables: si es una malla, datos puntuales estacionarios, trayectorias, etc.

- Identificar la organización que produjo un fichero nc y el ámbito (atmósfera, ecosistema o marino) a la que pertenece, para estadísticas.
- Saber qué variables de las existentes en los vocabularios están definidas en un fichero dado ¿Esto no se hace ya para poder buscar por variable? ¿Cómo se hace si esta información no viene en la cabecera?

Descripción de la convención
-----------------------------

Además de los atributos globales ya utilizados en la fase 1, se incorporarán:

- "insititution" según CF
- "ICOSDomain" = (ecosistema, atmósfera, medio marino)
- "id": identificador único del documento que no colisiona con ningún otro netcdf dentro del sistema.

Para cada variable se especificará el atributo "units" según CF, es decir, encontrando la unidad en el fichero udunits.dat del programa Udunits de Unidata. Se especificará un atributo standard_name que referenciará la entrada en el vocabulario[2] de la variable que se está midiendo.

Para las variables coordenadas se seguirá CF y se especificarán los atributos *units*, *standard_name* y *axis*. Las unidades para latitud y longitud serán siempre "degrees_north" y "degrees_east" respectivamente.

Para las coordenadas verticales se especificará en caso necesario un atributo *positive* con la dirección creciente de la dimensión. Aunque estén desaconsejadas, en caso de que las coordenadas verticales no sean dimensionales (nivel, capa, etc.) estas serán utilizadas. La alternativa es especificar mediante atributos una fórmula para convertir el nivel a atmósferas, lo cual complica en exceso la carga de los datos de los distintos proveedores.

Para la coordenada tiempo se seguirá también CF.

Para la obtención de las coordenadas de una variable se sigue la convención CF para trayectorias, datos puntuales y mallas (ver resumen de CF más abajo), aunque en principio se intuye que sólo va a haber datos puntuales y trayectorias. En la cabecera se incluirá un atributo "cdm_data_type" que, según las Discovery Conventions[3], indicará el tipo de datos almacenado en las variables del fichero. E.g., "Grid", "Image", "Station", "Trajectory", "Radial". Esto limita a que en un mismo fichero sólo pueda haber un tipo de datos.

Se considera la utilización de un único sistema de referencia, por simplicidad. El sistema común será el WGS84 y no se incluirá información sobre el CRS dentro del fichero.

Carga de datos
-----------------------------

Para la carga de datos en el thredds es necesario pues:

1. Obtener el nombre de la variable que se mide (long_name)
2. Obtener las unidades y obtener el código correspondiente en udunits.dat
3. Obtener el nombre estándar de la variable para hacer referencia al vocabulario (actualizar el vocabulario si la variable no está presente).
4. Obtener si es una trayectoria, medidas puntuales o una malla
5. Asegurarse de que los datos vienen en WGS84.
6. Si hay coordenadas verticales, rezar porque sean dimensionales. Si no, usar la nomenclatura desaconsejada (units=(level|layer|sigma_level))

Cambios con respecto a la convención (implícita) de la fase 1
---------------------------------------------------------------

- Eliminación de las unidades en el vocabulario. 
- Se usaría un UUID como "id" (fácil de generar en java, a falta de DOIs).
- Especificación del atributo global "naming_autority=es.icos.dataportal".
- Especificación del atributo global "standard_name_vocabulary=URL del vocabulario icos-spain"
- Especificación del atributo global "icos_domain" con valores (atmósfera|ecosistema|oceánico)
- Especificación del atributo "cdm_data_type" con uno de los valores de tipo de datos thredds[4].
- Codificación de las trayectorias, mallas, etc. como se especifica en CF (ver :ref:`coordsys`), incluyendo el atributo "axis" y un "standard_name" que indique una variable existente en el vocabulario.
- Especificación del atributo global "conventions = CF-1.5".
- Especificación del atributo global "Metadata_Conventions=Unidata Dataset Discovery v1.0".
- Especificación del atributo global "institution" y "creator_url".
- Especificación de los "atributos globales mínimos" (sobre todo bbox y time range) según: https://github.com/michogar/dataportal/blob/master/doc/data.rst
- Opcionalmente, especificación del resto de atributos globales recomendados en las discovery conventions: http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html

Climate & Forecast
::::::::::::::::::

Es una convención que define ciertas normas y recomendaciones para los contenidos de los ficheros netcdf.

Las definiciones que más nos pueden interesar son las relativas a la organización de las variables que contienen las coordenadas y su relación con las variables que contienen los datos que han sido medidos, de manera que para una variable de interés concreta, sea posible saber qué variables contienen las coordenadas (lat, lon, vertical y tiempo) dónde y cuándo se midió cada valor, tanto si las mediciones representan una malla, datos puntuales, trayectorias, etc.

A continuación se describe su versión 1.5.

Descripción de los contenidos
-----------------------------

Descripción de los contenidos del fichero. En particular "institution" permite describir la organización que produjo los datos

Descripción de los datos
--------------------------

Se requiere el atributo "units" para todas las variables dimensionales (creo que se refiere a aquellas que tienen unidad, las que no son porcentajes, ratios, etc.)

Los valores válidos son los aceptados por un paquete informático de Unidata llamado Udunits, que incorpora un fichero udunits.dat con la lista de los nombres de unidad soportados.

long_name es el nombre de la variable que se presenta a los usuarios

standard_name define una manera de identificar unívocamente la magnitud que se está representando. Consta de una referencia a la tabla de standard names[5] seguida opcionalmente de algunos modificadores.

Descripción de las coordenadas
----------------------------------------------

Las variables que representan latitud y longitud deben llevar siempre el atributo "units". Se recomienda:

- Para latitud: degrees_north. Equivalente a degree_north, degree_N, degrees_N, degreeN y degreesN
- Para longitud: degrees_east. Equivalente a degree_east, degree_E, degrees_E, degreeE y degreesE

Opcionalmente, para indicar que una variable representa datos de latitud o longitud, es posible especificar un attributo (standard_name="latitude" | standard_name="longitude") o un atributo (axis="Y" | axis="X")

Descripción altura o profundidad
--------------------------------

Las variables que representan altura o profundidad deben llevar siempre el atributo "units".

Cuando las coordenadas son dimensionales, es posible indicar:

- Unidades de presión: bar, millibar, decibar, atmosphere (atm), pascal (Pa), hPa
- Unidades de longitud: meter, m, kilometer, km
- Otras

Si units no indica una unidad válida de presión, es necesario indicar el atributo "positive=(up|down)"

Opcionalmente, para indicar que una variable representa coordenadas verticales es posible especificar un attributo standard_name con un valor apropiado o un atributo (axis="Z")

En caso de coordenadas adimensionales, es posible, aunque desaconsejado, utilizar "level", "layer" o "sigma_level" como valor de "units". La forma recomendada por la convención CF es utilizar una serie de atributos que definen una fórmula que transforma un determinado valor de "level" a un valor dimensional. Estos atributos son "standard_name" para identificar la formula y "formula_terms" para especificar las entradas.

Descripción variables de tiempo
-------------------------------

Las variables que representan tiempo deben llevar siempre el atributo "units". Se especifica::

	units=[unidad temporal] since [fecha_inicio]

Por ejemplo::

	units=seconds since 1992-10-8 15:15:42.5 -6:00

La unidad temporal puede ser: day (d), hour (hr, h), minute (min) y second (sec, s)

Una variable temporal puede ser identificada con las unidades sólo, pero también es posible utilizar "standard_name" con un valor apropiado o (axis='Z').

.. _coordsys:

Sistemas de coordenadas
-----------------------

Las dimensiones de una variable espacio temporal son utilizadas para localizar los valores de la variable en el espacio y en el tiempo. Existen varias maneras de localizar dichos valores.

Latitud, longitud, vertical y tiempo independiente
..................................................

Cada una de las dimensiones es identificada por una "coordinate variable" según se explica en el NetCDF User Guide.

Series temporales de datos estacionarios
........................................

La variable tiene, en lugar de dimensiones latitud y longitud, una dimensión que identifica la posición de la medida. Variables coordenada auxiliares dan la latitud y longitud para cada posición. En el siguiente ejemplo se puede ver como una de las dimensiones de "humidity" es "station" y que las variables lat y lon tienen como única dimensión "station". Es decir, existe un valor de lat/lon para cada valor de station::

	dimensions:
	 station = 10 ; // measurement locations
	 pressure = 11 ; // pressure levels
	 time = UNLIMITED ;
	variables:
	 float humidity(time,pressure,station) ;
	  humidity:long_name = "specific humidity" ;
	  humidity:coordinates = "lat lon" ;
	 double time(time) ;
	  time:long_name = "time of measurement" ;
	  time:units = "days since 1970-01-01 00:00:00" ;
	 float lon(station) ;
	  lon:long_name = "station longitude";
	  lon:units = "degrees_east";
	 float lat(station) ;
	  lat:long_name = "station latitude" ;
	  lat:units = "degrees_north" ;
	 float pressure(pressure) ;
	  pressure:long_name = "pressure" ;
	  pressure:units = "hPa" ;

Trayectorias
............

El mismo caso que el anterior pero la variable tiene una dimensión temporal y existen variables coordenada auxiliares que dan la latitud, longitud y coordenada vertical para cada valor de tiempo.
En el siguiente ejemplo está la variable coordenada "time" que es la dimensión de todas las variables coordenada auxiliares: lat, lon y z::

	dimensions:
	 time = 1000 ;
	variables:
	 float O3(time) ;
	  O3:long_name = "ozone concentration" ;
	  O3:units = "1e-9" ;
	  O3:coordinates = "lon lat z" ;
	 double time(time) ;
	  time:long_name = "time" ;
	  time:units = "days since 1970-01-01 00:00:00" ;
	 float lon(time) ;
	  lon:long_name = "longitude" ;
	  lon:units = "degrees_east" ;
	 float lat(time) ;
	  lat:long_name = "latitude" ;
	  lat:units = "degrees_north" ;
	 float z(time) ;
	  z:long_name = "height above mean sea level" ;
	  z:units = "km" ;
	  z:positive = "up" ;


[1] http://www.unidata.ucar.edu/software/netcdf-java/formats/UnidataObsConvention.html

[2] http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml

[3] http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#cdm_data_type_Attribute

[4] http://www.unidata.ucar.edu/projects/THREDDS/tech/catalog/InvCatalogSpec.html#dataType

[5] http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/current/cf-standard-name-table.xml


