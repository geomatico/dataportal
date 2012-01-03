Ext.define('query.Identifier', {
    extend: 'Ext.form.Panel',

    /* i18n */
    idFieldLabel: "Dataset Identifier:",
    /* ~i18n */
    
    padding: 4,
    height: 95,
    
    initComponent: function() {
                      
        this.items = [{
            border: false,
            items: [{
                text: this.idFieldLabel,
                xtype: 'label'
            },{
                name: 'id',
                value: '',
                width: '95%',
                xtype: 'textfield'
            }]
        }];
        
        this.callParent(arguments);
    },
    
    getParams: function() {
        var params = this.form.getFieldValues();
        params.response_format = "text/xml";
        return params;
    }
    
});
