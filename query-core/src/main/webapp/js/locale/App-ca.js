/**
 * @requires GeoExt/Lang.js
 */

GeoExt.Lang.add("ca", {
    "App.prototype": {
        getByIdTitle: "Obtenir descàrrega per ID",
        searchTitle: "Cerca de dades",
        resultsTitle: "Resultats",
        downloadsTitle: "Paquet de descàrrega",
        fileDownloadMessage: "Aquest registre ja s'ha afegit a la llista de descàrregues",
        homeTitle: "Portal de Dades ICOS-Espanya",
        homeLogin: "Identifica",
        homeCreateUser: "Nou usuari",
        homeLogout: "Surt",
        homeUpdateUser: "Canvia la paraula de pas",
        searchButtonText: "Cerca >>",
        showButtonText: "Mostra dades >>"
    },
    "Authentication.prototype": {
        userFieldLabel: "E-mail",
        userBlankText: "Escriviu la vostra adreça de correu electrònic",
        passwordFieldLabel: "Paraula de pas",
        passwordBlankText: "Escriviu la vostra paraula de pas",
        forgotPasswordMessage: "Heu oblidat la paraula de pas?",
        loginButtonText: "Accedeix",
        loginWaitMessage: "Comprovant la seva identitat",
        loginFailureMessage: "S'ha esdevingut un error al servidor, amb codi d'estat ",
        loginWindowTitle: "Benvinguts al Portal de Dades ICOS-Espanya",
        newUserTitle: "Nou usuari",
        newUserButtonText: "Registreu-vos",
        oldPasswordFieldLabel: "Paraula de pas anterior",
        oldPasswordBlankText: "Escriviu la vostra paraula de pas antiga",
        newPasswordFieldLabel: "Paraula de pas nova",
        newPasswordBlankText: "Escriviu la vostra nova paraula de pas",
        changePasswordTitle: "Canvi de paraula de pas",
        changePasswordButtonText: "Canvia paraula de pas",
        passwordChangeMessage: "Sou a punt de reescriure la vostra paraula de pas. Si us plau, seguiu les instruccions que se us trametran per correu electrònic.",
        passwordReminderTitle: "Recordatori de paraula de pas",
        passwordReminderButton: "Reescriure paraula de pas",
        logoutWaitMessage: "Sortint"
    },
    "download.Panel.prototype": {
        downloadReadyTitle: "Descàrrega preparada",
        downloadReadyMessage: "La seva petició de dades amb UUID<br/><b>{0}</b><br/>està enllestida.<br/><br/>Cliqueu OK per descarregar-vos-la.",
        downloadErrorTitle: "Error a la descàrrega",
        downloadButtonText: "Descarrega",
        idColumnHeader: "Id",
        titleColumnHeader: "Títol",
        removeColumnTooltip: "Treure de les descàrregues",
        loginRequiredMessage: "Disculpeu, heu d'identificar-vos abans de descarregar les dades"
    },
    "query.Form.prototype": {
        textFieldLabel: "Text",
        startDateFieldLabel: "Des del:",
        endDateFieldLabel: "Fins el:",
        variablesFieldTitle: "Variables",
        dateDisplayFormat: "j M Y"
    },
    "query.Identifier.prototype": {
        idFieldLabel: "ID de Descàrrega"
    },
    "query.Map.prototype": {
        titleText: "Lloc:",
        navigationButtonTooltip: "Navega",
        addBoxButtonTooltip: "Afegeix Caixa",
        removeBoxButtonTooltip: "Elimina Caixa"
    },
    "result.Grid.prototype": {
        errorTitle: "Error de Cerca",
        genericErrorMessage: "Error de Cerca no especificat",
        summaryHeader: "Resum",
        extentHeader: "Extensió espacial",
        variablesHeader: "Variables",
        idHeader: "Id",
        titleHeader: "Títol",
        fromDateHeader: "Des del",
        toDateHeader: "Fins el",
        downloadActionTooltip: "Afegir com a Descàrrega",
        dateDisplayFormat: "j M Y",
        pagingDisplayMessage: "Mostrant registres del {0} al {1} d'un total de {2}",
        pagingEmptyMessage: "No hi ha dades per mostrar"        
    },
    "OpenLayers.Control.DeleteFeature.prototype": {
        confirmText: "Esteu segurs de voler eliminar l'element sel·leccionat?"
    }
});
