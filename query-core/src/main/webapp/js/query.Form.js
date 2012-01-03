Ext.define('query.Form', {
    extend: 'Ext.form.Panel',

    /* i18n */
    textFieldLabel: "Text:",
    startDateFieldLabel: "From:",
    endDateFieldLabel: "To:",
    variablesFieldTitle: "Variables",
    dateDisplayFormat: "M j, Y",
    /* ~i18n */

    fieldDefaults: {
        labelAlign: 'top'
    },
    autoScroll: true,
    border: true,
    padding: 4,
    flex: 1,
    
    map: null,
    vocabulary: null,
        
    initComponent: function() {
        
        //this.map = new query.Map(); // TODO: Restore GeoExt

        this.items = [{
            border: false,
            items: [{
                text: this.textFieldLabel,
                xtype: 'label'
            },{
                name: 'text',
                value: '',
                xtype: 'textfield',
                width: 200
            }]
        },
        //this.map, // TODO Restore GeoExt
        {
            layout: 'column',
            border: false,
            items: [{
                columnWidth: 0.5,                                
                border: false,
                items: [{
                    text: this.startDateFieldLabel,
                    xtype: 'label'
                }, {
                    name: 'start_date',
                    xtype: 'datefield',
                    width: 100,
                    format: this.dateDisplayFormat
                }]
            }, {
                columnWidth: 0.5,
                border: false,
                items: [{
                    text: this.endDateFieldLabel,
                    xtype: 'label'
                }, {
                    name: 'end_date',
                    xtype: 'datefield',
                    width: 100,
                    format: this.dateDisplayFormat
                }]
            }]
        }];

        this.callParent(arguments);

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
                submitValue: false,
                isFormField: false
            });
        });

        var checkboxgroup = {
            xtype: 'checkboxgroup',
            name: 'variables',
            cls: 'variables',
            columns: 1,
            items: checkboxitems,
            isFormField: true
        };
        
        this.add({ 
            title: this.variablesFieldTitle,
            xtype: 'fieldset',
            fieldLabel: "&nbsp;", // Ah, si, si, creetelo!
            hideLabel: true,
            collapsible: true,
            items: checkboxgroup
        });
        
        this.doLayout();
    },
    
    getParams: function() {
        
        var params = this.getForm().getFieldValues();
        
        // Transform checked variables to a comma-separated list of variable names
        var vars = this.getForm().getFields().findBy(function(o){return o.name=="variables";}).getValue();
        params.variables = Ext.Object.getKeys(vars).join(",");
        
        // Format start and end dates
        if (params.start_date) params.start_date = Ext.Date.format(params.start_date, "Y-m-d");
        if (params.end_date) params.end_date = Ext.Date.format(params.end_date, "Y-m-d");

        // Get bboxes
        //params.bboxes = this.map.getBBOXes(); // TODO restore map
        params.bboxes = "[]";
        
        // Add static params
        params.response_format = "text/xml";
        
        return params;
    }
    
});
