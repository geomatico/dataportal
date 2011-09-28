Ext.namespace('query');

query.Form =  Ext.extend(Ext.form.FormPanel, {

    map: null,
    vocabulary: null,
        
    initComponent: function() {
        
        this.map = new query.Map();
               
        var config = {
            labelAlign: 'top',
            autoScroll: true,
            border: true,
            padding: 4,
            flex: 1,
            items: [{
                border: false,
                items: [{
                    text: 'Text:',
                    xtype: 'label'
                },{
                    name: 'text',
                    value: '',
                    xtype: 'textfield',
                    style: 'margin: 4px;width:200px' // Guarradas a domicilio
                }]
            },
            this.map,
            {
                layout: 'column',
                padding: 5,
                border: false,
                items: [{
                    columnWidth: 0.5,                                
                    border: false,
                    items: [{
                        text: 'From:',
                        xtype: 'label'
                    }, {
                        name: 'start_date',
                        xtype: 'datefield',
                        format: 'Y-m-d'
                    }]
                }, {
                    columnWidth: 0.5,
                    border: false,
                    items: [{
                        text: 'To:',
                        xtype: 'label'
                    }, {
                        name: 'end_date',
                        xtype: 'datefield',
                        format: 'Y-m-d'
                    }]
                }]
            }],
            buttons: [{
                text: 'Search >>'
            }]
        };
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        
        query.Form.superclass.initComponent.apply(this, arguments);

        if(this.vocabulary) {
            this.vocabulary.on('load', this.addVariableFieldset, this);
        }
    },

    addVariableFieldset: function(store, records, options) {
        var checkboxitems = [];
        store.each( function(record) {
            checkboxitems.push({
                boxLabel: (record.data.nc_long_term || record.data.en_long_term),
                name: record.data.nc_term || record.data.en_term,
                submitValue: false
            });
        });

        var checkboxgroup = new Ext.form.CheckboxGroup({
            name: 'variables',
            cls: 'variables',
            columns: 1,
            items: checkboxitems
        });
       
        this.add({ 
            title: 'Variables',
            xtype: 'fieldset',
            fieldLabel: "&nbsp;", // Ah, si, si, creetelo!
            hideLabel: true,
            collapsible: true,
            autoHeight: true,
            items: checkboxgroup
        });
        
        this.doLayout();
    },
    
    getParams: function() {
        
        var params = this.form.getFieldValues();
        
        // Transform checked boxes to name array
        var varArray = [];
        Ext.each(params.variables, function(item){
            if(item.getValue()){
                varArray.push(item.getName());
            };
        });
        params.variables = varArray.join(",");
        
        // Format start and end dates
        if (params.start_date!="") params.start_date = params.start_date.format("Y-m-d");
        if (params.end_date!="") params.end_date = params.end_date.format("Y-m-d");

        // Get bboxes
        params.bboxes = this.map.getBBOXes();
        
        // Add static params
        params.response_format = "text/xml";
        
        return params;
    }
    
});

Ext.reg('i_queryform', query.Form);
