/**
 * @requires GeoExt/Lang.js
 */

GeoExt.Lang.add("en", {
    "App.prototype": {
        queriesTitle: "1. Queries",
        resultsTitle: "2. Results",
        downloadsTitle: "3. Downloads",
        fileDownloadMessage: "This file is already set for download",
        homeTitle: "ICOS Spain Carbon Data Portal",
        homeLogin: "Login",
        homeCreateUser: "Sign Up",
        homeLogout: "Logout",
        homeUpdateUser: "Change Password"
    },
    "Authentication.prototype": {
        userFieldLabel: "Email address",
        userBlankText: "Enter your email address",
        passwordFieldLabel: "Password",
        passwordBlankText: "Enter your password",
        forgotPasswordMessage: "Forgot your password?",
        loginButtonText: "Login",
        loginWaitMessage: "Checking identity",
        loginFailureMessage: "Server-side failure with status code ",
        loginWindowTitle: "Welcome to the ICOS Spain Carbon Data Portal",
        newUserTitle: "New User",
        newUserButtonText: "Sign Up",
        oldPasswordFieldLabel: "Old Password",
        oldPasswordBlankText: "Enter your Old Password",
        newPasswordFieldLabel: "New Password",
        newPasswordBlankText: "Enter your New Password",
        changePasswordTitle: "Change Password",
        changePasswordButtonText: "Change password",
        passwordChangeMessage: "Your password is about to be reset. Please check your inbox for further instructions.",
        passwordReminderTitle: "Password Reminder",
        passwordReminderButton: "Reset password",
        logoutWaitMessage: "Logging out"
    },
    "download.Panel.prototype": {
        downloadReadyTitle: "Download Ready",
        downloadReadyMessage: "Your data request with UUID<br/><b>{0}</b><br/>is ready.<br/><br/>Click OK to download.",
        downloadErrorTitle: "Download Error",
        downloadButtonText: "Download",
        idColumnHeader: "Id",
        titleColumnHeader: "Title",
        removeColumnTooltip: "Remove from Downloads",
        loginRequiredMessage: "Sorry, you must Login to download data"
    },
    "query.Form.prototype": {
        textFieldLabel: "Text:",
        startDateFieldLabel: "From:",
        endDateFieldLabel: "To:",
        searchButtonText: "Search >>",
        variablesFieldTitle: "Variables",
        dateDisplayFormat: "M j, Y"
    },
    "query.Identifier.prototype": {
        idFieldLabel: "Dataset Identifier:",
        showButtonText: "Show Dataset >>"
    },
    "query.Map.prototype": {
        titleText: "Location:",
        navigationButtonTooltip: "Navigate",
        addBoxButtonTooltip: "Add Box",
        removeBoxButtonTooltip: "Remove Box"
    },
    "result.Grid.prototype": {
        errorTitle: "Search Error",
        genericErrorMessage: "Unspecified Query Error",
        summaryHeader: "Summary",
        extentHeader: "Spatial Extent",
        variablesHeader: "Variables",
        idHeader: "Id",
        titleHeader: "Title",
        fromDateHeader: "From date",
        toDateHeader: "To date",
        downloadActionTooltip: "Add to Downloads",
        dateDisplayFormat: "M j, Y",
        pagingDisplayMessage: "Displaying data records {0} - {1} of {2}",
        pagingEmptyMessage: "No data records to display"        
    },
    "OpenLayers.Control.DeleteFeature.prototype": {
        confirmText: "Do you really want to remove the selected feature?"
    }
});
