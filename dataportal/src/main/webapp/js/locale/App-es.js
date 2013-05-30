/**
 * @requires GeoExt/Lang.js
 */

GeoExt.Lang.add("es", {
    "App.prototype": {
        getByIdTitle: "Obtener descarga por ID",
        searchTitle: "Buscar datos",
        resultsTitle: "Resultados",
        downloadsTitle: "Paquete de descarga",
        fileDownloadMessage: "Este registro ya está añadido a la lista de descargas",
        homeTitle: "Portal de Datos ICOS-España",
        homeLogin: "Identificarse",
        homeCreateUser: "Nuevo usuario",
        homeLogout: "Salir",
        homeUpdateUser: "Cambiar contraseña",
        homeUploadData: "Subir datos",
        searchButtonText: "Buscar >>",
        showButtonText: "Mostrar datos >>",
        aboutButtonTooltip: "Acerca de...",
        reportButtonTooltip: "Informe de Uso",
        uploadFormTitle: "Subir datos"
    },
    "Authentication.prototype": {
        userFieldLabel: "E-mail",
        userBlankText: "Escriba su dirección de correo electrónico",
        passwordFieldLabel: "Contraseña",
        passwordBlankText: "Escriba su contraseña",
        forgotPasswordMessage: "¿Olvidó la contraseña?",
        loginButtonText: "Entrar",
        loginWaitMessage: "Comprobando su identidad",
        loginFailureMessage: "Ocurrió un fallo del servidor con código de estado ",
        loginWindowTitle: "Bienvenido al Portal de Datos ICOS-España",
        newUserTitle: "Nuevo usuario",
        newUserButtonText: "Regístrese",
        oldPasswordFieldLabel: "Contraseña anterior",
        oldPasswordBlankText: "Escriba su contraseña antigüa",
        newPasswordFieldLabel: "Contraseña nueva",
        newPasswordBlankText: "Escriba su nueva contraseña",
        changePasswordTitle: "Cambio de Contraseña",
        changePasswordButtonText: "Cambiar contraseña",
        passwordChangeMessage: "Su contraseña está a punto de reescribirse. Por favor consulte su bandeja de entrada, donde encontará nuevas instrucciones.",
        passwordReminderTitle: "Recordatorio de contraseña",
        passwordReminderButton: "Reescribir contraseña",
        logoutWaitMessage: "Saliendo"
    },
    "download.Panel.prototype": {
        downloadReadyTitle: "Descarga preparada",
        downloadReadyMessage: "Su petición de datos con UUID<br/><b>{0}</b><br/>está lista.<br/><br/>Clique OK para descargársela.",
        downloadErrorTitle: "Error de descarga",
        downloadButtonText: "Descargar",
        idColumnHeader: "Id",
        titleColumnHeader: "Título",
        removeColumnTooltip: "Quitar de las descargas",
        loginRequiredMessage: "Disculpe, debe identificarse antes de descargar datos",
        waitMessage: "Preparando descarga..."
    },
    "variables.Panel.prototype": {
        title: "Variables"
    },
    "query.Form.prototype": {
        textFieldLabel: "Texto",
        startDateFieldLabel: "Desde:",
        endDateFieldLabel: "Hasta:",
        dateDisplayFormat: "j M Y"
    },
    "query.Identifier.prototype": {
        idFieldLabel: "ID de Descarga"
    },
    "query.Map.prototype": {
        titleText: "Lugar:",
        navigationButtonTooltip: "Navegar",
        addBoxButtonTooltip: "Añadir Caja",
        removeBoxButtonTooltip: "Eliminar Caja"
    },
    "result.Grid.prototype": {
        errorTitle: "Error de Búsqueda",
        genericErrorMessage: "Error de Búsqueda sin especificar",
        summaryHeader: "Resumen",
        extentHeader: "Extensión espacial",
        variablesHeader: "Variables",
        idHeader: "Id",
        titleHeader: "Título",
        fromDateHeader: "Desde el",
        toDateHeader: "Hasta el",
        downloadActionTooltip: "Añadir como Descarga",
        dataplotActionTooltip: "Inspeccionar Datos",
        dateDisplayFormat: "j M Y",
        pagingDisplayMessage: "Mostrando registros del {0} al {1} de un total de {2}",
        pagingEmptyMessage: "No hay datos que mostrar"        
    },
    "OpenLayers.Control.DeleteFeature.prototype": {
        confirmText: "¿Está seguro de querer eliminar el elemento seleccionado?"
    },
    "converter.Form.prototype": {
    	uploadFileLabel : "Subir archivo",
    	selectFileButtonText : "Seleccionar archivo:",
    	waitingMessageText : "Subiendo...",
    	waitingTitleText : "Subiendo datos y convirtiendo...",
    	cancelButtonText : 'Cancelar',
    	convertButtonText : 'Convertir',
    	failureMessageTitle : '¡Error!',
    	successMessageTitle : '¡Finalizado con éxito!',
    	successMessageText : 'Datos subidos y generado archivo .NC con ID: ',
    	inValidFormText : '¡Formulario no válido!',
    	globalMetadataPanelTitle : 'Metadatos globales',
    	converterComboLabel : 'Elija conversor',
    	converterPanelTitle : 'Conversores disponibles'
    }
});
