Authentication = Ext.extend(Ext.util.Observable, {

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

        Authentication.superclass.constructor.call(this, config);
        
        Ext.get('login').on('click', this.showLogin, this);
        Ext.get('logout').on('click', this.doLogout, this);
        Ext.get('createUser').on('click', this.showSignup, this);
        Ext.get('updateUser').on('click', this.showProperties, this);
    },
    
    showLogin: function() {
        this.loginForm = new Ext.form.FormPanel({
            frame: true,
            width: 260,
            labelWidth: 60,
            defaults: {
                width: 165
            },
            items: [
                new Ext.form.TextField({
                    name: "user",
                    fieldLabel: this.userFieldLabel,
                    vtype: "email",
                    allowBlank: false,
                    blankText: this.userBlankText,
                    enableKeyEvents: true,
                    listeners: {
                        'keyup': function(field, e){
                            if(e.getCharCode()==e.ENTER){
                                this.loginForm.getForm().findField("password").focus(false, 50);
                            }
                        },
                        scope: this
                    }
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: this.passwordFieldLabel,
                    inputType: 'password',
                    allowBlank: false,
                    blankText: this.passwordBlankText,
                    enableKeyEvents: true,
                    listeners: {
                        'keyup': function(field, e){
                            if(e.getCharCode()==e.ENTER){
                                this.loginForm.getForm().findField("user").focus(false, 50);
                                var b = this.loginForm.buttons[0];
                                b.handler.call(b.scope, b, Ext.EventObject);
                            }
                        },
                        scope: this
                    }
                }),
                new Ext.form.DisplayField({
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
                            failure: function(response, opts) {
                                alert(this.loginFailureMessage + response.status);
                            },
                            scope: this
                        });
                    }
                },
                scope: this
            }],
            listeners: {
                afterlayout: function(form) {
                    this.getForm().findField("user").focus(false, 50);
                }
            }            
        });

        this.loginWindow = new Ext.Window({
            title: this.loginWindowTitle,
            layout: 'fit',
            height: 165,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [this.loginForm]
        });
      
        this.loginWindow.show();
    },
    
    showSignup: function() {
        var form = new Ext.form.FormPanel({
            frame: true,
            width: 260,
            labelWidth: 60,
            defaults: {
                width: 165
            },
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
            ]
        });

        var win = new Ext.Window({
            title: this.newUserTitle,
            layout: 'fit',
            height: 165,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: this.newUserButtonText,
            handler: function() {
                var fields = this.form.getForm().getFieldValues();
                this.form.getForm().submit({
                    url: 'login',
                    params: {
                        request: "register",
                        password: hex_md5(fields.user+":"+fields.password)
                    },
                    success: function(form, action) {
                        this.win.close();
                        this.auth.showActionResult(form, action);
                        this.fireEvent("signed_up", this.form.getForm().getValues().user);
                    },
                    failure: this.auth.showActionResult,
                    scope: this
                });
            },
            scope: {auth: this, form: form, win: win}
        });
        
        win.show();
    },

    showProperties: function() {
        var form = new Ext.form.FormPanel({
            frame: true,
            width: 260,
            labelWidth: 60,
            defaults: {
                width: 165
            },
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
            ]
        });

        var win = new Ext.Window({
            title: this.changePasswordTitle,
            layout: 'fit',
            height: 190,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: this.changePasswordButtonText,
            handler: function() {
                var fields = this.form.getForm().getFieldValues();
                this.form.getForm().submit({
                    url: 'login',
                    params: {
                        request: "changePass",
                        password: hex_md5(this.auth.user+":"+fields.password),
                        newPassword: hex_md5(this.auth.user+":"+fields.newPassword)
                    },
                    success: function(form, action) {
                        this.win.close();
                        this.auth.fireEvent("password_changed");
                        this.auth.doLogout();
                        this.auth.showActionResult(form, action);
                    },
                    failure: this.auth.showActionResult,
                    scope: this
                });
            },
            scope: {auth: this, form: form, win: win}
        });
        
        win.show();
    },
    
    showPasswordReminder: function() {
        var form = new Ext.form.FormPanel({
            frame: true,
            width: 260,
            labelWidth: 60,
            defaults: {
                width: 165
            },
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
            ]
        });

        var win = new Ext.Window({
            title: this.passwordReminderTitle,
            layout: 'fit',
            height: 165,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: this.passwordReminderButton,
            handler: function() {
                this.form.getForm().submit({
                    url: 'login',
                    params: { request: "generatePass" },
                    success: function(form, action) {
                        this.win.close();
                        this.auth.fireEvent("password_reminder_requested");
                        this.auth.showActionResult(form, action);
                    },
                    failure: this.auth.showActionResult,
                    scope: this
                });
            },
            scope: {auth: this, form: form, win: win}
        });
        
        win.show();
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
        var message = action.result.message;
        alert(message);
    }
});

Ext.reg('i_authenticate', Authentication);
