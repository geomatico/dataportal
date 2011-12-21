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

Las implementaciones de |ds| deben devolver una variable principal que será una implementación
de |div| o de |ddv|, dependiendo de si la variable contiene valores enteros o decimales.

Conversión
------------

Una vez las interfaces hayan sido implementadas, ya sólo es necesario ejecutar un *main* con la siguiente línea::

	public static void main(String[] args) {
		Converter.convert(new UTMDatasetConversion());
	}

Limitaciones
------------

Coordenadas verticales no están soportadas.

Las variables no pueden tener más dimensión que tiempo y posición.

No se soportan variables auxiliares (no más de una variable por posición espaciotemporal).