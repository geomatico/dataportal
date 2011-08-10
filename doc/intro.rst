Escenario
---------

Partimos de una colección compleja y heterogénea de de datasets. Se diferencian dos grandes grupos:

1. Datos del contínuo (underway): Sets de datos más sencillos, que no son los que nos interesan en esta primera aproximación. Almacenados en bases de datos PostGIS, accessibles mediante servicios web OGC (WMS, WFS y WCS) publicados en un GeoServer.

2. Datos más complejos (4 o más dimensiones): ficheros netCDF con un contenido muy diverso: datos marinos, atmosféricos, y terrestres. Los ficheros netCDF se sirven con Thredds. Estos ficheros son los que nos interesan. La lástima es que no podemos servir todo con GeoServer, por ejemplo.


Portal
------

El portal consistiría en una primera pantalla de login para validar el usuario. A continuación habría dos opciones:

 1. Realizar una búsqueda de datos.
 2. Recuperar búsqueda guardada por su identificador QI (Query Identifier).


Pantalla de búsqueda
--------------------

Permite seleccionar por:
 
 * Rango temporal: fecha inicial – fecha final.
 * Boundig Box: buscar en un área geográfica. La particularidad en este caso sería permitir definir múltiples cajas de búsqueda simultáneas.
 * Selección de variables: qué variables queremos recuperar: temperatura, salinidad, velocidad del viento, etc. Las variables se han de buscar dentro del fichero netCDF (¿definiendo consultas con ncML?). El listado de posibles variables se lee de un fichero XML del tipo::

    <term>
      <sado_term>salinidad</sado_term>
      <nc_term>sea_water_salinity</nc_term>
      <nc_long_term>sea water salinity</nc_long_term>
      <nc_units>psu</nc_units>
      <nc_data_type>DOUBLE</nc_data_type>
      <nc_coordinate_axis_type></nc_coordinate_axis_type>
    </term>
    <term>
      <sado_term>temperatura</sado_term>
      <nc_term>sea_water_temperature</nc_term>
      <nc_long_term>sea water temperature</nc_long_term>
      <nc_units>degree_Celsius</nc_units>
      <nc_data_type>DOUBLE</nc_data_type>
      <nc_coordinate_axis_type></nc_coordinate_axis_type>
    </term>
    [...]

Una vez realizada la búsqueda, se descargan los datos que corresponden al filtro. Con otro botón se genera un identificador de búsqueda (QI) de manera que se pueda reproducir la búsqueda y recuperar la misma colección de datos posteriormente. El QI se genera llamando a un servicio externo. La aplicación de búsqueda deberá almacenar el QI junto con los datos recuperados y los criterios de búsqueda utilizados, el usuario que ha realizado la búsqueda, etc.


Recuperar la búsqueda a partir de un QI
---------------------------------------

Otro usuario podría introducir el QI, de modo que el formulario muester los criterios de búsqueda utlizados, así como un enlace a la colección de datos que se recuperaron en su momento. El objetivo es que las búsquedas sean reproducibles, y los criterios de búsqueda recuperables.

Report
------

Report en PDF: Un usuario validado puede ver con cuántos QI está relacionado. O, dicho de otro modo, un publicador de datos puede ver en qué búsquedas aparecen datos suyos, y un consumidor de datos puede consultar sus búsquedas y descargas anteriores.


Esquema de bloques
------------------

(todo)
