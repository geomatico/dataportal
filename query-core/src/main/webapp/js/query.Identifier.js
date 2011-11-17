Ext.namespace('query');

query.Identifier =  Ext.extend(Ext.form.FormPanel, {

    initComponent: function() {
                      
        var config = {
            padding: 4,
            height: 95,
            items: [{
                border: false,
                items: [{
                    text: 'Dataset Identifier:',
                    xtype: 'label'                    
                },{
                    name: 'id',
                    value: '',
                    width: '95%',
                    xtype: 'textfield'
                }]
            }],
            buttons: [{
                text: 'Show Dataset >>'
            }]
        };
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        
        query.Form.superclass.initComponent.apply(this, arguments);
    },
    
    getParams: function() {
        var params = this.form.getFieldValues();
        params.response_format = "text/xml";
        return params;
    }
    
});

Ext.reg('i_queryidentifier', query.Identifier);
