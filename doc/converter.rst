.. |dsc| replace:: *DatasetConversion*
.. |ds| replace:: *Dataset*
.. |s| replace:: *Station*
.. |t| replace:: *Trajectory*
.. |ts| replace:: *TimeSerie*
.. |dss| replace:: *Dataset*'s
.. |div| replace:: *DatasetIntVariable*
.. |ddv| replace:: *DatasetDoubleVariable*

Conversión a NetCDF
============================

Configuración del proyecto
----------------------------

Se ha creado un proyecto Java para generar NetCDFs que cumplan la convención utilizada en ICOS. Para hacer uso del proyecto
es necesario crear un proyecto Java y añadir la dependencia a *netcdf-converter*. Con maven, basta con añadir la siguiente
dependencia en el *pom.xml*:: 

	<dependency>
		<groupId>co.geomati</groupId>
		<artifactId>netcdf-converter</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>

Las dependencias están desplegadas en el repositorio maven de Fernando, por lo que habrá que añadir 
también la siguiente sección de repositorios::

	<repositories>
		<repository>
			<id>fergonco</id>
			<name>Repo en fergonco.es</name>
			<url>http://www.fergonco.es/maven-repo/</url>
		</repository>
	</repositories>

Interfaces a implementar
---------------------------

Una vez el proyecto depende del conversor es necesario implementar la interfaz |dsc|, a la que se le pedirán
los |dss| para la conversión.

Los |dss| devueltos por la implementación de |dsc| deberán implementar la interfaz |ds| y además, en función
de si contiene datos estacionarios o trayectorias, una (y sólo una) de las siguientes interfaces:

- |s| (o *GeoreferencedStation* ver javadoc).
- |t|.

En caso de implementar |s| puede implementar opcionalmente la interfaz |ts| indicando así que los datos
dependen de la dimensión tiempo.

En caso de |t|, esta extiende a |ts| por lo que no es necesario implementarla explícitamente.

Las implementaciones de |ds| deben devolver una o más variables principales que serán una implementación
de |div| o de |ddv|, dependiendo de si la variable en cuestión contiene valores enteros o decimales.

Tras la actualización se ha añadido una nueva interfaz que deben implementar los conversores, ``co.geomati.netcdf.IConverter``. Esta interfaz obliga a implementar el método doConversion, que es el que será llamado para realizar la conversión. Los conversores han dejado de ser clases éstáticas por lo que se hace necesaria su instanciación. 

Configuración de archivos con conversores disponibles
-----------------------------------------------------

Con la idea de facilitar lo máximo posible la creación de nuevos conversores, y su incorporación al sistema, se ha definido una estrategía que permita realizar esto de la manera menos intrusiva posible. Para ello además de las interfaces se ha diseñado un archivo de configuración XML que expone un listado con los conversores disponibles en la librería. El archivo es de la siguiente manera::

	<?xml version="1.0"?>
	<converters>
		<converter>
			<name>UTM</name>
			<class>co.geomati.netcdf.utm.ConvertUTM</class>
			<institution_name>UTM</institution_name>
			<institution_realname>Unidad de Tecnología Marina (UTM), CSIC</institution_realname>
			<institution_url>http://www.utm.csic.es/</institution_url>
			<icos_domain>oceans</icos_domain>
		</converter>
		<converter>
			<name>AEMET</name>
			<class>co.geomati.netcdf.aemet.ConvertAEMET</class>
			<institution_name>AEMET</institution_name>
			<institution_realname>Centro de Investigación Atmosférica de Izaña, AEMET
			</institution_realname>
			<institution_url>http://www.izana.org/</institution_url>
			<icos_domain>atmosphere</icos_domain>
		</converter>
		
		...
		
	</converters>

* **converter**: Etiqueta que da comienzo a la definición del conversor
* **name**: Nombre del conversor, este valor será el que aparezca en el menú de conversión del DataPortal
* **class**: Clase que será implementada para realizar la conversión
* **institution_name, institution_realname, institution_url e icos_domain**: son datos que serán cargados en el menú de conversión como metadatos globales

Para añadir un nuevo conversor, será necesario incorporar un nuevo registro a este archiv que haga referencia al nuevo conversor.
		
Conversión
----------

Para lanzar la conversión, tras la actualización serán necesarios varios pasos:

1. Obtener el conversor que queremos utilizar::

	IConverter converter = Converter.getConverterToUse(name del converter);
	
2. Fijar la ruta del directorio temporal, por defecto es el directorio temporal del sistema::

	Converter.setTempSavePath(Ruta al archivo temporal);
	
3. Añadir los atributos globales pasandole estos en la clase co.geomati.co.NCGlobalAttributes::

	Converter.setGlobalAttributes(globalAttributes);
	
4. Realizar la conversión desde el conversor::

	converter.doConversion(params ...);

En el directorio temporal encontraremos el resultado de la conversión.

Limitaciones
------------

Coordenadas verticales no están soportadas.

Las variables no pueden tener más dimensión que tiempo y posición.
