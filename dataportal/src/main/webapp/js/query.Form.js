Ext.define('query.Form', {
    extend: 'Ext.form.Panel',

    /* i18n */
    textFieldLabel: "Text",
    startDateFieldLabel: "From:",
    endDateFieldLabel: "To:",
    dateDisplayFormat: "M j, Y",
    /* ~i18n */

    fieldDefaults: {
        labelAlign: 'top'
    },
    autoScroll: true,
    border: true,
    frame: true,
    flex: 1,
    
    layout: 'anchor',
    
    map: null,
    variables: null,
    
    initComponent: function() {
        
        this.map = new query.Map();
        
        this.defaults = {
            border: false,
            margin: 5,
            layout: 'fit'
        };

        this.items = [{
            labelAlign: 'left',
            fieldLabel: this.textFieldLabel,
            labelWidth: 40,
            name: 'text',
            value: '',
            xtype: 'textfield'
        },
        this.map,
        {
            layout: 'column',
            width: 258,
            defaults: {
                columnWidth: 0.5,                                
                border: false,
                bodyCls: 'x-panel-body-default-framed'                
            },
            items: [{
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
        },
        this.variables
        ];

        this.callParent(arguments);
    },
    
    getParams: function() {
        
        var params = this.getForm().getFieldValues();
        
        // Get selected variables as a comma-separated list of variable names
        params.variables = this.variables.getSelected().join(",");
        
        // Format start and end dates
        if (params.start_date) params.start_date = Ext.Date.format(params.start_date, "Y-m-d");
        if (params.end_date) params.end_date = Ext.Date.format(params.end_date, "Y-m-d");

        // Get bboxes
        params.bboxes = this.map.getBBOXes();
        
        // Add static params
        params.response_format = "text/xml";
        
        return params;
    }
    
});
