.. |TDS| replace:: *Thredds*
.. |GN|  replace:: *GeoNetwork*
.. |GS|  replace:: *GeoServer*
.. |DP|  replace:: *Data Portal*
.. |JVM| replace:: *Máquina Virtual Java* 

.. [1] http://www.keopx.net/blog/cambiar-las-preferencias-de-java-alternatives-en-debianubuntu
.. [2] http://www.guia-ubuntu.org/index.php?title=Java


Instalación
===========

TODO: Micho, excepto apdo. |GS|


Tomcat y Java
-------------
Instalación de JAVA de SUN
^^^^^^^^^^^^^^^^^^^^^^^^^^
Utilizando los repositorios de Ubuntu
"""""""""""""""""""""""""""""""""""""
Primero instalaremos la versión necesaria de Java, en este caso la 6. Para ello teclearemos en la terminal::

	sudo apt-get install sun-java6-jdk sun-java6-bin sun-java6-jre

Comprobaremos que Ubuntu está utilizando la versión que nos interesa de Java y no la que lleva instalada por defecto. Para ello indicamos en la terminal::

	java -version

que nos mostrará el mensaje::

	java version "1.6.0_26"
	Java(TM) SE Runtime Environment (build 1.6.0_26-b03)
	Java HotSpot(TM) Client VM (build 20.1-b02, mixed mode, sharing)

indicando nuestra versión de Java. En caso contrario será necesario instalar nuestra vesión como alternativa. Para ello primero comprobamos las alternativas que está utilizando. Accedemos a la ruta::
	
	cd /etc/alternatives

y mostramos las que nos interesan::

	ls -la ja*

podremos observar que alternativa de java está disponible::

	java -> /usr/lib/jvm/java-6-openjdk/jre/bin/java

para modificar esta debemos primero instalar nuestra versión de java como alternativa::

	sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-6-sun/jre/bin/java 1

y después asignaremos esta alternativa::

	sudo update-alternatives --set /usr/lib/jvm/java-6-sun/jre/bin/java

ahora podemos comprobar a que máquina de Java apunta::

	java -> /usr/lib/jvm/java-6-sun/jre/bin/java

Descargando Java
""""""""""""""""

Primero descargaremos la versión de Java que nos interesa desde::

	http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u27-download-440405.html

Accedemos a la carpeta de descarga del archivo y modificamos su permiso de ejecución::

	sudo chmod +x <nombre del archivo>

y ejecutamos::
	
	sudo ./<nombre del archivo>

este paso nos solicitará nuestra confirmación y descomprimirá los archivos en una carpeta en el mismo directorio donde lo hayamos ejecutado. Movemos esa carpeta a una localización mas acorde::

	mv <ruta de la carpeta> /usr/lib/jvm

después realizaremos los mismos pasos para asignar la alternativa que en el caso anterior.

 
**Referencias**

*	Cambiar las preferencias de Java (alternatives) en Debian/Ubuntu [1]_
*	Instalación de Java Guia-Ubuntu [2]_
	
Instalación de Tomcat
^^^^^^^^^^^^^^^^^^^^^
Instalación desde los repositorios
""""""""""""""""""""""""""""""""""
Abrimos un terminal y tecleamos::
	
	sudo apt-get install tomcat6

de esta manera instalaremos Tomcat. Para comprobar que la instalación es correcta::

	http://localhost:8080

apareciendo el mensaje **It works!**.
Esta instalación de Tomcat crea la siguiente estructura de directorios que mas adelante nos hará falta conocer::

	Directorio de logs; logs --> /var/lib/tomcat6/logs
	Directorio de configuracion; conf --> /var/lib/tomcat6/conf
	Directorio de aplicaciones; webapps --> /var/lib/tomcat6/webapps

La instalación creara un usuario y un grupo, tomcat6::tomcat6. Para arrancar/parar o reiniciar esta instancia de Tomcat::

	sudo /etc/init.d/tomcat6 [start|stop|restart]

Para acceder al manager de Tomcat primero instalaremos la aplicación necesaria para gestionar el servidor. Para ello tecleamos desde una terminal::

	sudo apt-get install tomcat6-admin

Una vez instalado incluiremos en el archivo tomcat-users.xml en el directorio de configuración el rol y el usuario necesario para acceder a la aplicación. Para ello incluiremos bajo la raiz <tomcat-users> lo siguiente::

	<role rolename="tomcat"/>
	<role rolename="manager"/>
	<user username="icos" password="XXXX" roles="tomcat,manager"/>

Reiniciaremos el servidor y probamos el acceso a través de::

	http://localhost:8080/manager/html

e introduciremos los datos incluidos en el fichero tomcat-users.xml


|GS|
----

TODO: Oscar

* cómo metadatar servicios OWS y publicar cartografía de base que se usará en |GN| y |DP|


|TDS|
-----

* cómo configurar las extensiones


|GN|
----

* qué versión de |GN| instalar



Harvesting |TDS| a |GN|
-----------------------

* cómo configurar tarea harvesting (pantallazo?)
* cómo tunear plantilla de harvesteo para que recoja BBOX, timespan, texto y variables de netCDF y los coloque en una ISO


|DP|
----

* nuestra app (tocar properties?)
