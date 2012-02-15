Ext.define('Authentication', {
    extend: 'Ext.util.Observable',

    /* i18n */
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
    logoutWaitMessage: "Logging out",
    /* ~i18n */
    
    user: null,
    password: null,
    loginForm: null,
    signupForm: null,
    propertiesForm: null,
    reminderForm: null,
    loginWindow: null,
    
    constructor: function(config){
        this.addEvents({
            "logged_in": true,
            "logged_out": true,
            "signed_up": true,
            "password_changed": true,
            "password_reminder_requested": true
        });
        this.listeners = config && config.listeners || null;

        this.callParent(arguments);
        
        Ext.get('login').on('click', this.showLogin, this);
        Ext.get('logout').on('click', this.doLogout, this);
        Ext.get('createUser').on('click', this.showSignup, this);
        Ext.get('updateUser').on('click', this.showProperties, this);
    },
    
    showLogin: function() {
        this.loginForm = new Ext.form.FormPanel({
            frame: true,
            defaults: {
                margin: 5
            },
            labelWidth: 60,
            items: [
                new Ext.form.TextField({
                    name: "user",
                    fieldLabel: this.userFieldLabel,
                    vtype: "email",
                    allowBlank: false,
                    blankText: this.userBlankText
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: this.passwordFieldLabel,
                    inputType: 'password',
                    allowBlank: false,
                    blankText: this.passwordBlankText
                }),
                new Ext.form.DisplayField({
                    width: 200,
                    html: '<a href="#">'+this.forgotPasswordMessage+'</a>',
                    listeners: {
                        render: function(c) {
                            c.getEl().on('click', function(ev, el) {
                                this.loginWindow.close();
                                this.showPasswordReminder();
                            }, this, {stopEvent: true});
                        },
                        scope: this
                    }
                })
            ],
            buttons: [{
                text: this.loginButtonText,
                handler: function(){
                    if(this.loginForm.getForm().isValid()){
                        var params = this.loginForm.getForm().getValues();
                        params.password = hex_md5(params.user+":"+params.password);
                        params.request = "access";
                        Ext.Ajax.request({
                            url: 'login',
                            params: params,
                            waitMsg: this.loginWaitMessage,
                            success: function(response, opts) {
                                var result = Ext.decode(response.responseText);
                                if(result.success) {
                                    Ext.get("notLoggedLinks").hide();
                                    this.user = result.message;
                                    this.password = params.password;
                                    this.loginWindow.close();
                                    this.loginWindow = null;
                                    Ext.get("loggedLinks").show();
                                    this.fireEvent("logged_in", this.user);
                                } else {
                                    alert(result.message);
                                }
                            },
                            failure: this.showActionResult,
                            scope: this
                        });
                    }
                },
                scope: this
            }]
        });

        this.loginWindow = new Ext.Window({
            title: this.loginWindowTitle,
            layout: 'fit',
            closable: true,
            draggable: true,
            modal: true,
            items: [this.loginForm]
        }).show();
    },
    
    showSignup: function() {
        this.signupForm = new Ext.form.FormPanel({
            frame: true,
            defaults: {
                margin: 5
            },
            labelWidth: 60,
            items: [
                new Ext.form.TextField({
                    name: "user",
                    fieldLabel: this.userFieldLabel,
                    vtype: "email",
                    allowBlank: false,
                    blankText: this.userBlankText
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: this.passwordFieldLabel,
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: this.passwordBlankText
                })
            ],
            buttons: [{
                text: this.newUserButtonText,
                handler: function() {
                    if(this.signupForm.getForm().isValid()){
                        var fields = this.signupForm.getForm().getFieldValues();
                        this.signupForm.getForm().submit({
                            url: 'login',
                            params: {
                                request: "register",
                                password: hex_md5(fields.user+":"+fields.password)
                            },
                            success: function(form, action) {
                                this.signupWindow.close();
                                this.showActionResult(form, action);
                                this.fireEvent("signed_up", this.signupForm.getForm().getFieldValues().user);
                            },
                            failure: this.showActionResult,
                            scope: this
                        });
                    }
                },
                scope: this
            }]
        });

        this.signupWindow = new Ext.Window({
            title: this.newUserTitle,
            layout: 'fit',
            closable: true,
            draggable: true,
            modal: true,
            items: [this.signupForm]
        }).show();
    },

    showProperties: function() {
        this.propertiesForm = new Ext.form.FormPanel({
            frame: true,
            defaults: {
                margin: 5
            },
            labelWidth: 60,
            items: [
                new Ext.form.TextField({
                    name: "user",
                    fieldLabel: this.userFieldLabel,
                    vtype: "email",
                    value: this.user,
                    readOnly: true
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: this.oldPasswordFieldLabel,
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: this.oldPasswordBlankText
                }),
                new Ext.form.TextField({
                    name: "newPassword",
                    fieldLabel: this.newPasswordFieldLabel,
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: this.newPasswordBlankText
                })
            ],
            buttons: [{
                text: this.changePasswordButtonText,
                handler: function() {
                    var fields = this.propertiesForm.getForm().getFieldValues();
                    this.propertiesForm.getForm().submit({
                        url: 'login',
                        params: {
                            request: "changePass",
                            password: hex_md5(this.user+":"+fields.password),
                            newPassword: hex_md5(this.user+":"+fields.newPassword)
                        },
                        success: function(form, action) {
                            this.propertiesWindow.close();
                            this.fireEvent("password_changed");
                            this.doLogout();
                            this.showActionResult(form, action);
                        },
                        failure: this.showActionResult,
                        scope: this
                    });
                },
                scope: this
            }]
        });

        this.propertiesWindow = new Ext.Window({
            title: this.changePasswordTitle,
            layout: 'fit',
            closable: true,
            draggable: true,
            modal: true,
            items: [this.propertiesForm]
        }).show();
    },
    
    showPasswordReminder: function() {
        this.reminderForm = new Ext.form.FormPanel({
            frame: true,
            defaults: {
                margin: 5
            },
            labelWidth: 60,
            items: [
                new Ext.form.TextField({
                    name: "user",
                    fieldLabel: this.userFieldLabel,
                    vtype: "email",
                    allowBlank: false,
                    blankText: this.userBlankText,
                    enableKeyEvents: true
                }),
                new Ext.form.DisplayField({
                    value: this.passwordChangeMessage
                })
            ],
            buttons: [{
                text: this.passwordReminderButton,
                handler: function() {
                    this.reminderForm.getForm().submit({
                        url: 'login',
                        params: { request: "generatePass" },
                        success: function(form, action) {
                            this.reminderWindow.close();
                            this.fireEvent("password_reminder_requested");
                            this.showActionResult(form, action);
                        },
                        failure: this.showActionResult,
                        scope: this
                    });
                },
                scope: this                
            }]
        });

        this.reminderWindow = new Ext.Window({
            title: this.passwordReminderTitle,
            width: 290,
            layout: 'fit',
            closable: true,
            draggable: true,
            modal: true,
            items: [this.reminderForm]
        }).show();
    },
    
    doLogout: function() {
        if(this.user != null) {
            Ext.Ajax.request({
                url: 'login',
                params: {request: 'logout'},
                waitMsg: this.logoutWaitMessage,
                success: function(response, opts) {
                    Ext.get("loggedLinks").hide();
                    this.user = null;
                    this.password = null;
                    Ext.get("notLoggedLinks").show();
                    this.fireEvent("logged_out");
                },
                failure: function(response, opts) {
                    alert(this.loginFailureMessage + response.status);
                },
                scope: this
            });
        }
    },
    
    showActionResult: function(form, action) {
        Ext.Msg.alert('Message', action.result.message || action.response.responseText);
    }
});
