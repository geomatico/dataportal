Ext.define('query.Identifier', {
    extend: 'Ext.form.Panel',

    /* i18n */
    idFieldLabel: "Download ID",
    /* ~i18n */
    
    height: 100,
    border: false,
    frame: true,
    
    initComponent: function() {
                      
        this.items = [{
            border: false,
            labelAlign: 'left',
            fieldLabel: this.idFieldLabel,
            name: 'id',
            value: '',
            xtype: 'textfield',
            padding: 0,
            style: 'margin:5px;width:260px'
        }];
        
        this.callParent(arguments);
    },
    
    getParams: function() {
        var params = this.form.getFieldValues();
        params.response_format = "text/xml";
        return params;
    }
    
});
