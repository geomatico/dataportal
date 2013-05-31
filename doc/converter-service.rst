Servicio "converter" de subida y conversión de datos
====================================================

Se ha incorporado un servicio para la subida y conversión de datos desde las diferentes instituciones. Este servicio está disponible para los usuarios registrados y permite seleccionar el archivo a subir, definir los metadatos globales, definir el conversor a utilizar y el servicio realizará las tareas necesarias para, al finalizar el proceso, incorporar en el sistema estos datos.

Introducción y objetivos
------------------------

La creación de este servicio parte con una primera necesidad, poder asistir al usuario en la incorporación de metadatos globales en el archivo que se va a incorporar al sistema. También se plantea la necesidad de facilitar el proceso de cnversión de los archivos de datos de las diferentes instituciones así como de incorporar estos al sistema de una manera sencilla e independiente de un administrador.

Para ello se han definido un servicio de subida y conversión de datos, y se ha implementado una interfaz gráfica que utiliza ese servicio.

Configuración del servicio
--------------------------

En primer lugar se ha definido un archivo con los metadatos globales que serán utilizados por la aplicación. Este se encuentra en la carpeta ``xml`` de la aplicación, y su estructura es de la siguiente manera::

	<?xml version="1.0"?>
	<globalmetadata>
		<globalattribute>
			<CFname>id</CFname>
			<formname>ID</formname>
			<defaultvalue></defaultvalue>
		</globalattribute>
		<globalattribute>
			<CFname>naming_authority</CFname>
			<formname>Naming Authority</formname>
			<defaultvalue>UUID</defaultvalue>
		</globalattribute>
		
		...
		
	</globalmetadata>
	
Este archivo es consultado cada vez que se crea el formulario de subida y conversión de datos. El significado de los campos es:

* **CFname**: nombre del atributo dentro de la convención Climate Forecast (CF).
* **formname**: nombre que se aplicará en la etiqueta del campo en el formulario.
* **defaultvalue**: valor por defecto que se incluirá en el cuadro de texto del campo y que, en caso de no ser modificado, será insertado en el archivo en la conversión.

Para insertar nuevos atributos globales, solo será necesario crear en este archivo mas registros que serán leidos en la carga del formulario.

Un nuevo valor que se ha incorporado es la ruta de la carpeta del catálogo del servidor Thredds donde se guardan los datos en formato NetCDF para su posterior consumo dentro de la plataforma. Este valor se modificará en el archivo ``dataportal.properties`` indicando la ruta del mismo.

	.. warning::
		Será necesario dar permisos de escritura al usuario ``tomcat`` en esa carpeta
		

Servicio de subida y conversión de datos
----------------------------------------

El servicio está disponible para los usuarios que estén registrados en la plataforma. Una vez ingresado en el sistema aparecerá la opción de *Subir datos* junto al menú de identificación del usuario. Una vez pulsado, aparece un menú de selección de archivos. Seleccionaremos el archivo que deseamos convertir desde el dialogo de selección, y pulsamos aceptar.

Después de seleccionar el archivo, apareceran los campos de los metadatos globales, y un selector de conversor. Los metadatos globales son los que tenemos definidos en el archivo XML comentado en la sección anterior. El listado de conversores se extrae de un archivo de conversores disponibles que se encuentra en la libreria ``netcdf-converter``. Al pulsar sobre el conversor que deseamos utilizar, parte de los atributos globales serán rellenados de forma automática con los valores asociados al conversor. TODOS los valores de los campos de los atributos son modificables, excepto el valor del ID que no será tenido en cuenta.

Tras realizar las operaciones anteriores, pulsando sobre el botón convertir, arrancaremos el proceso siguiente:

1. El servicio sube el archivo original (csv, txt, xls...) al servidor y lo guarda en una carpeta temporal.
2. El conversor seleccionado convierte el archivo a un ``NetCDF`` y después lo copia en la carpeta del servidor Thredds donde se encuentra el catálogo.
3. Se eliminan los archivos subidos y se notifica del resultado de la operación.

Se puede ver el proceso completo `aquí`_.

Roadmap y posibles mejoras
--------------------------

* Posibilidad de subida y conversión de paquetes de archivos, puede que desde formatos comprimidos como ZIP, RAR o tar.gz.

.. _aquí: http://vimeo.com/67297674
