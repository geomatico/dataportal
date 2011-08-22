.. |TDS| replace:: *Thredds*
.. |GN|  replace:: *GeoNetwork*
.. |GS|  replace:: *GeoServer*
.. |DP|  replace:: *Data Portal*
.. |TCT| replace:: *Tomcat*

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

	$ sudo apt-get install sun-java6-jdk sun-java6-bin sun-java6-jre

Comprobaremos que Ubuntu está utilizando la versión que nos interesa de Java y no la que lleva instalada por defecto. Para ello indicamos en la terminal::

	$ java -version

que nos mostrará el mensaje::

	java version "1.6.0_26"
	Java(TM) SE Runtime Environment (build 1.6.0_26-b03)
	Java HotSpot(TM) Client VM (build 20.1-b02, mixed mode, sharing)

indicando nuestra versión de Java. En caso contrario será necesario instalar nuestra vesión como alternativa. Para ello primero comprobamos las alternativas que está utilizando. Accedemos a la ruta::
	
	$ cd /etc/alternatives

y mostramos las que nos interesan::

	$ ls -la ja*

podremos observar que alternativa de java está disponible::

	java -> /usr/lib/jvm/java-6-openjdk/jre/bin/java

para modificar esta debemos primero instalar nuestra versión de java como alternativa::

	$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-6-sun/jre/bin/java 1

y después asignaremos esta alternativa::

	$ sudo update-alternatives --set /usr/lib/jvm/java-6-sun/jre/bin/java

ahora podemos comprobar a que máquina de Java apunta::

	java -> /usr/lib/jvm/java-6-sun/jre/bin/java

Descargando Java
""""""""""""""""

Primero descargaremos la versión de Java que nos interesa desde::

	http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u27-download-440405.html

Accedemos a la carpeta de descarga del archivo y modificamos su permiso de ejecución::

	$ sudo chmod +x <nombre del archivo>

y ejecutamos::
	
	$ sudo ./<nombre del archivo>

este paso nos solicitará nuestra confirmación y descomprimirá los archivos en una carpeta en el mismo directorio donde lo hayamos ejecutado. Movemos esa carpeta a una localización mas acorde::

	$ mv <ruta de la carpeta> /usr/lib/jvm

después realizaremos los mismos pasos para asignar la alternativa que en el caso anterior.

 
**Referencias**

*	Cambiar las preferencias de Java (alternatives) en Debian/Ubuntu [1]_
*	Instalación de Java Guia-Ubuntu [2]_
	
Instalación de Tomcat
^^^^^^^^^^^^^^^^^^^^^
Instalación desde los repositorios
""""""""""""""""""""""""""""""""""
Abrimos un terminal y tecleamos::
	
	$ sudo apt-get install tomcat6

de esta manera instalaremos |TCT|. Para comprobar que la instalación es correcta::

	http://localhost:8080

apareciendo el mensaje **It works!**.
Esta instalación de |TCT| crea la siguiente estructura de directorios que mas adelante nos hará falta conocer::

	Directorio de logs; logs --> /var/lib/tomcat6/logs
	Directorio de configuracion; conf --> /var/lib/tomcat6/conf
	Directorio de aplicaciones; webapps --> /var/lib/tomcat6/webapps

La instalación creara un usuario y un grupo, tomcat6::tomcat6. Para arrancar/parar o reiniciar esta instancia de |TCT|::

	$ sudo /etc/init.d/tomcat6 [start|stop|restart]

Para acceder al manager de |TCT| primero instalaremos la aplicación necesaria para gestionar el servidor. Para ello tecleamos desde una terminal::

	$ sudo apt-get install tomcat6-admin

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
Instalación de Thredds Data Server
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
En este apartado se explicará la instalación y configuración del servidor |TDS|. En primer lugar necesitaremos descargarnos la versión adecuada del servidor, en nuestro caso será la versión 4.2::

	ftp://ftp.unidata.ucar.edu/pub/thredds/4.2/thredds.war

Descargamos un archivo .war que deberemos desplegar en nuestro servidor |TCT|. Antes de ello debemos efectuar unas configuraciones previas. 
Crearemos una variable de entorno que apunte a nuestro directorio de |TCT|. Editamos el archivo .bashrc de la sesión con la que estemos trabajando. Este archivo lo encontraremos en::

	$ cd ~

Modificamos el archivo **.bashrc** con un editor de texto::

	$ nano .bashrc

e incluiremos la siguiente linea::

	export TOMCAT_HOME=/usr/share/tomcat6

Aplicamos los cambios escribiendo en el terminal::

	$ source .bashrc

y comprobamos que aparece nuestra variable::

	$ echo $TOMCAT_HOME

que nos mostrará el valor que hemos introducido en el archivo **.bashrc**, /usr/share/tomcat6

Crearemos un script en la carpeta bin del |TCT| ($TOMCAT_HOME/bin) que permita a este encontrar unas determinadas variables que necesitará para arrancar |TDS|::

	$ sudo nano $TOMCAT_HOME/bin/setenv.sh

e incluiremos lo siguiente::

	#!/bin/sh
	#
	# ENVARS for Tomcat and TDS environment
	#
	JAVA_HOME="/usr/lib/jvm/java-6-sun"
	export JAVA_HOME

	JAVA_OPTS="-Xmx1500m -Xms512m -XX:MaxPermSize=180m -server -Djava.awt.headless=true -Djava.util.prefs.systemRoot=$CATALINA_HOME/content/thredds/javaUtilPrefs"
	export JAVA_OPTS

	CATALINA_HOME="/usr/share/tomcat6"
	export CATALINA_HOME

Donde le indicamos la memoria máxima 1500 en caso de sistemas de 32-bit o 4096 o más en sistemas de 64-bit, y en caso de usar WMS con |TDS| debemos añadirle la localización de javaUtilPrefs asignandole a ``-Djava.util.prefs.systemRoot`` la ruta.
Una vez realizado esto, reiniciaremos |TCT| y comprobamos que los cambios se han producido::

	$ ps -ef | grep tomcat

que nos mostrará::

	tomcat6   7376     1 45 14:48 ?        00:00:03 /usr/lib/jvm/java-6-sun/bin/java -Djava.util.logging.config.file=/var/lib/tomcat6/conf/logging.properties
	-Xmx1500m -Xms512m -XX:MaxPermSize=180m -server -Djava.awt.headless=true -Djava.util.prefs.systemRoot=/usr/share/tomcat6/content/thredds/javaUtilPrefs 
	-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/usr/share/tomcat6/endorsed -classpath /usr/share/tomcat6/bin/bootstrap.jar 
	-Dcatalina.base=/var/lib/tomcat6 -Dcatalina.home=/usr/share/tomcat6 -Djava.io.tmpdir=/tmp/tomcat6-tmp org.apache.catalina.startup.Bootstrap start

Donde podemos observar los valores que hemos introducido en nuestro script y que |TCT| ha incluido en el arranque.
Antes de realizar el despliegue de |TDS| crearemos la carpeta donde la instalación crea todos los archivos necesarios para la instalación y configuración del mismo. Para ello navegamos hasta el directorio donde el despliegue del war busca dicha carpeta por defecto::

	$ cd /var/lib/tomcat

y creamos la carpeta con el nombre por defecto::

	$ mkdir content

seguidamente le asignaremos permisos al usuario y grupo tomcat6::

	$ sudo chmod tomcat6:tomcat6 content

Una vez hecho esto procederemos al despliegue de |TDS| bien desde la pestaña manager de |TCT|, o copiando directamente el archivo thredds.war en la carpeta webapps de nuestra instancia de |TCT|. Es recomendable realizar un seguimiento de los cambios producidos en el servidor para comprobar que el despliegue de |TDS| se realiza correctamente, para ello ejecutaremos previamente en una consola::

	$ tail -f /var/lib/tomcat6/logs/catalina.out

de esta manera veremos por consola los mensajes que nos envia |TCT|.
Para comprobar que la instalación ha ido correctamente::

	http://localhost:8080/thredds

y accederemos al catalogo de ejemplo que viene en |TDS| por defecto.

Configuración de módulos en |TDS|
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
TDS Remote Management
"""""""""""""""""""""
Desde el Remote Management de |TDS| podemos acceder a información acerca del estado del servidor, reiniciar catálogos... Para porder acceder a este deberemos previamente configurar |TCT| para que permita el acceso mediante SSL. Lo primero que haremos será crear un certificado autofirmado en el servidor (keystore) y configuraremos |TCT| para utilizar un conector que permita el acceso mediante este protocolo.

Lo primero que haremos será utilizar la herramienta keytool para generar el certificado. Esta herramienta viene suministrada con el JDK de Java y la encontraremos en::
	
	$ $JAVA_HOME/bin/

y la ejecutaremos indicandole la ruta donde generaremos el archivo .keystore ($USER_HOME/.keystore por defecto)::

	$JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA -validity 365 -keystore ~/.keystore

y responderemos a las cuestiones que plantea. Respecto al password, por defecto |TCT| tiene definida *changeit* como contraseña por defecto, así que deberemos modificar en los valores del conector el valor de esta, indicandole la que hayamos definido en la creación del certificado. Para introducir esta y modificar algunos otros valores necesarios modificaremos el archivo server.xml de nuestra instancia de |TCT|::

	$ sudo nano /etc/tomcat6/server.xml

descomentaremos las lineas que activan el conector::

	  <!-- Define a SSL Coyote HTTP/1.1 Connector on port 8443 -->
    <Connector port="8443" 
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" disableUploadTimeout="true"
               acceptCount="100" debug="0" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" keystoreFile="<ruta al .keystore creado>" keystorePass="<contraseña al crear el keystore>"/>

introduciendo la ruta al archivo .keystore creado e indicandole la contraseña que hemos indicado en la creación del mismo. Una vez realizada esta modificación, reiniciaremos el |TCT| comprobaremos que los cambios se han realizado correctamente accediendo a::

	https://localhost:8443

Finalmente, para poder acceder al gestor remoto del |TDS| deberemos crear el usuario y el rol en |TCT| que permite este acceso. Para ello modificaremos el archivo tomcat-users.xml incluyendo lo siguiente::

	<role rolename="tdsConfig"/>
	<user username="<nombre usuario>" password="<password usuario>" roles="tdsConfig"/>

Está será la clave de acceso del usuario, por lo que no debe ser igual a la que se ha definido en el conector de |TCT|. Reiniciaremos el |TCT| de nuevo y comprobamos el acceso a través de::

	https://localhost:8443/thredds/admin/debug


 
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
