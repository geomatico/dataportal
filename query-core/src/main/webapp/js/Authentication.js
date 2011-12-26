Authentication = Ext.extend(Ext.util.Observable, {
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
        
        Ext.select('.login').on('click', this.showLogin, this);
        Ext.select('.logout').on('click', this.doLogout, this);
        Ext.select('.createUser').on('click', this.showSignup, this);
        Ext.select('.updateUser').on('click', this.showProperties, this);
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
                    fieldLabel: "Email address",
                    vtype: "email",
                    allowBlank: false,
                    blankText: "Enter your email address",
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
                    fieldLabel: "Password",
                    inputType: 'password',
                    allowBlank: false,
                    blankText: "Enter your password",
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
                    html: '<a href="#">'+"Forgot your password?"+'</a>',
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
                text: 'Login',
                handler: function(){
                    if(this.loginForm.getForm().isValid()){
                        var params = this.loginForm.getForm().getValues();
                        params.password = hex_md5(params.user+":"+params.password);
                        params.request = "access";
                        Ext.Ajax.request({
                            url: 'login',
                            params: params,
                            waitMsg: 'Checking identity',
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
                                alert('Server-side failure with status code ' + response.status);
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
            title: 'Welcome to ICOS Carbon Data Portal',
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
                    fieldLabel: "Email address",
                    vtype: "email",
                    allowBlank: false,
                    blankText: "Enter your email address"
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: "Password",
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: "Enter your Password"
                })
            ]
        });

        var win = new Ext.Window({
            title: 'New User',
            layout: 'fit',
            height: 165,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: 'Sign Up',
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
                    fieldLabel: "Email address",
                    vtype: "email",
                    value: this.user,
                    readOnly: true
                }),
                new Ext.form.TextField({
                    name: "password",
                    fieldLabel: "Old Password",
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: "Enter your Old Password"
                }),
                new Ext.form.TextField({
                    name: "newPassword",
                    fieldLabel: "New Password",
                    inputType: 'password',
                    allowBlank: false,
                    submitValue: false,
                    blankText: "Enter your New Password"
                })
            ]
        });

        var win = new Ext.Window({
            title: 'Change Password',
            layout: 'fit',
            height: 190,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: 'Change password',
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
                    fieldLabel: "Email address",
                    vtype: "email",
                    allowBlank: false,
                    blankText: "Enter your email address",
                    enableKeyEvents: true
                }),
                new Ext.form.DisplayField({
                    value: "Your password is about to be reset. Please check your inbox for further instructions."
                })
            ]
        });

        var win = new Ext.Window({
            title: 'Password Reminder',
            layout: 'fit',
            height: 165,
            width: 260,
            closable: true,
            draggable: true,
            modal: true,
            items: [form]
        });
        
        form.addButton({
            text: 'Reset password',
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
                waitMsg: 'Logging out',
                success: function(response, opts) {
                    Ext.get("loggedLinks").hide();
                    this.user = null;
                    this.password = null;
                    Ext.get("notLoggedLinks").show();
                    this.fireEvent("logged_out");
                },
                failure: function(response, opts) {
                    alert('Server-side failure with status code ' + response.status);
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
