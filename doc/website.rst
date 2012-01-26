Sitio web
============================

Tecnologías utilizadas
----------------------------

Para la creación del sitio web se ha utilizado rest2web [1]_, que permite la generación de HTML estático
a partir de ficheros escritos en reStructuredText y una plantilla HTML.

reStructuredText [2]_ es un formato tipo *wiki* que permite crear de forma sencilla tablas, incluir imágenes, etc.
Aquí [3]_ se puede consultar una guía rápida del formato.

La principal ventaja de dicha aproximación es que el resultado es HTML estático y por tanto los
requisitos del servidor son mínimos.

Instalación
-------------

En ubuntu basta con teclear:: 

    sudo apt-get install rest2web
    
en una línea de comandos.

Descripción del proyecto
-------------------------

Estructura
^^^^^^^^^^^

El directorio raíz de la web contiene un fichero *webicos.ini* con la configuración y los directorios *input* y *output*.

*Input* contiene la plantilla y los ficheros en reStructuredText, mientras que *output* contiene el sitio web generado.

Para generar hay que ejecutar r2w webicos.ini en el directorio raíz.

Contenidos
^^^^^^^^^^^

En *input*, cada fichero .txt consta de dos partes, una sección *restindex* y otra con los contenidos que se muestran
en la web. Por ejemlo::

    restindex
        crumb: partners
        format:rest
        page-title: Project Overview
        encoding: utf-8
        output-encoding: None
        target: index.html
    /restindex
    
    Objectives
    ==========
    
    To provide the long-term observations required to understand the present state and predict future behaviour of the global carbon 
    cycle and greenhouse gas emissions.

La sección *restindex* permite especificar parámetros para la generación del HTML, como por ejemplo el título de la
página (*page-title*) o el nombre de la página destino (*target*). 

index.txt
^^^^^^^^^^^

Especial atención requiere el fichero index.txt, que contiene sólo la sección *restindex* con atributos globales de todo el sitio::

    restindex
        crumb: home
        format:rest
        page-title: ICOS Spain
        encoding: utf-8
        output-encoding: utf-8
        section-pages: ,overview,organization,infrastructure,outreach,links,contacts
        file: style.css
        file: images/cabecera.jpg
        file: images/geomatico.png
        file: images/lsce.gif
        file: images/logo_UE_fp7.jpeg
        file: images/icos_bottom_banner.png
        file: images/logo_aemet.png
        file: images/logo_ceam.png
        file: images/logo_csic.png
        file: images/logo_ic3.png
        file: images/logo_uclm.png
        file: images/logo_ugr.png
        file: images/logo_u_las_palmas.png
    
        build: no
    /restindex

Los elementos más frecuentemente actualizados de esta página son el parámetro *section-pages* en caso de quere añadir
una nueva sección o los parámetros *file* en caso de querer incluir en el directorio *output* un nuevo fichero, ya
sea imagen, hoja css, etc.

Plantilla
^^^^^^^^^^^

Dentro de *input* es posible encontrar el fichero *template.txt* que contiene la plantilla HTML que se usará para
generar el HTML correspondiente a cada sección.

Dentro de dicho fichero es posible encontrar macros como  "<% title %>" que son susituidas en el momento de la
generación por contenido extraido de los ficheros correspondientes a las secciones.

También es posible ejecutar código Python existente para generar contenido dinámicamente. Estas llamadas se delimitan
por "<#" y "#>", por ejemplo::

    <#
        print_details(default_section, item_wrapper = '%s', page_title = '')
    #>

Para más información sobre las llamadas y macros disponibles, consultar [4]_ .

Referencias
------------

.. [1] http://www.voidspace.org.uk/python/rest2web/
.. [2] http://docutils.sourceforge.net/rst.html
.. [3] http://docutils.sourceforge.net/docs/user/rst/quickref.html
.. [4] http://www.voidspace.org.uk/python/rest2web/templating.html