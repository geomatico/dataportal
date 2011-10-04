Authentication = Ext.extend(Ext.util.Observable, {
    user: null,
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
        this.listeners = config.listeners;

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
                    id: "user",
                    fieldLabel: "Email address",
                    vtype: "email",
                    allowBlank: false,
                    blankText: "Enter your email address"
                }),
                new Ext.form.TextField({
                    id: "password",
                    fieldLabel: "Password",
                    inputType: 'password',
                    allowBlank: false,
                    blankText: "Enter your password"
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
            }]
        });

        this.loginWindow = new Ext.Window({
            title: 'Welcome to ICOS Carbon Data Portal',
            layout: 'fit',
            height: 140,
            width: 260,
            closable: true,
            resizable: false,
            draggable: false,
            items: [this.loginForm]
        });
      
        this.loginWindow.show();
    },
    
    showSignup: function() {
        // TODO
        alert("signup not implemented - yet");
        this.fireEvent("signed_up", "----");
    },

    showProperties: function() {
        // TODO
        alert("user edit not implemented - yet");
        this.fireEvent("signed_up", "----");
    },
    
    doLogout: function() {
        if(this.user != null) {
            Ext.get("loggedLinks").hide();
            this.user = null;
            this.password = null;
            Ext.get("notLoggedLinks").show();
            this.fireEvent("logged_out");            
        }
    }
});

Ext.reg('i_authenticate', Authentication);
