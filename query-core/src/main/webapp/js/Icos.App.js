Ext.namespace('Icos');

Icos.App =  Ext.extend(Ext.Viewport, {
    
    queryForm: null,
    resultGrid: null,
    
    initComponent: function() {
        // TODO: Manage state & session
        //Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
        
        Ext.QuickTips.init();
        
        this.queryForm = new Icos.query.Form({
            xtype: 'i_queryform',
            listeners: {
                scope: this,
                render: function(form) {
                    form.buttons[0].on({
                        scope: this,
                        click: this.doQuery
                    });
                }
            }
        });
        
        this.resultGrid = new Icos.result.Grid({
            xtype: 'i_resultgrid'
        });
        
        var config = {
            layout: 'border',
            items: [{
                contentEl: 'header',
                region: 'north',
                xtype: 'box',
                height: 64
            }, {
                title: '1. Queries',
                region: 'west',
                split: false,
                width: 287,
                collapsible: true,
                layout: {
                    type: 'vbox',
                    align:'stretch',
                    animate: true
                },
                items: [
                    this.queryForm
                ]
            }, {
                title: '2. Results',
                region: 'center',
                items: [
                    this.resultGrid
                ],
                autoScroll: true
            }, {
                title: '3. Downloads',
                region: 'east',
                layout: 'fit',
                split: false,
                width: 225
            }]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.App.superclass.initComponent.apply(this, arguments);
    },

    doQuery: function() {
        this.resultGrid.load(this.queryForm.getParams());
    }
});

Ext.reg('i_app', Icos.App);
