Ext.namespace('Icos.result');

Icos.result.Grid =  Ext.extend(Ext.grid.GridPanel, {
    
    pageSize: 25,
    vocabulary: null,
    recordType: null,
    downloadHandler: null,
    handlerScope: null,
    
    initComponent: function() {
        
        ds = new Ext.data.Store({
            autoLoad: false,
            url: 'search', // 'xml/fakeSearch.xml',
            storeId: 'searchResponse',
            reader: new Ext.data.XmlReader({
                root: 'response',
                record: 'item',
                id: 'id',
                totalProperty: '@totalcount'
            }, this.recordType ),
            remoteSort: true,
            sortInfo: {
                field: 'title',
                direction: 'ASC'
            }
        });        
        
        var expander = new Ext.ux.grid.RowExpander();
        
        expander.on('expand', function(expander, record, body, rowindex) {
            var variables = record.get("variables").split(",");
            var varnames = [];
            for (var i=0; i < variables.length; i++) {
                variable = variables[i];
                term = this.vocabulary.getById(variable);
                name = term ? (term.get("nc_long_term") || term.get("en_long_term")) : variable;
                varnames.push(name);
            }
            
            if(!body.innerHTML) {
                new Ext.Panel({
                    layout:'column',
                    height: 'auto',
                    padding: 5,
                    layoutConfig: {
                        align : 'stretch'
                    },
                    defaults: {
                        height: 285
                    },
                    items: [{
                        title: 'Summary',
                        html: record.get("summary"),
                        columnWidth: .70
                    },{
                        title: 'Spatial Extent',
                        items: [new Icos.result.Map({resultExtent: record.get("geo_extent")})],
                        width: 260,
                        height: 300
                    },{
                        title: 'Variables',
                        html: varnames.join("<br>"),
                        padding: 5,
                        columnWidth: .30
                    }],
                    renderTo: body
                });
            }
        }, this);
        
        var config = {
            autoHeight: true,
            autoScroll: true,
            autoExpandColumn: "title",
            ds: ds,
            cm: new Ext.grid.ColumnModel([
                expander,
                {header: "Id", width: 150, sortable: true, dataIndex: 'id'},
                {id: "title", header: "Title", width: 'auto', sortable: true, dataIndex: 'title'},
                {header: "From date", width: 90, sortable: true, dataIndex: 'start_time', renderer: Ext.util.Format.dateRenderer('Y-m-d')}, // H:i:s
                {header: "To date", width: 90, sortable: true, dataIndex: 'end_time', renderer: Ext.util.Format.dateRenderer('Y-m-d')},
                {
                    xtype: "actioncolumn",
                    width: 30,
                    iconCls: "icon-download",
                    tooltip: "Add to Downloads",
                    align: "center",
                    handler: this.downloadHandler,
                    scope: this.handlerScope
                }
            ]),
            bbar: new Ext.PagingToolbar({
                pageSize: this.pageSize,
                store: ds,
                displayInfo: true,
                displayMsg: 'Displaying data records {0} - {1} of {2}',
                emptyMsg: "No data records to display"
            }),
            plugins: expander
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.result.Grid.superclass.initComponent.apply(this, arguments);
    },
    
    load: function(params) {
        this.getStore().baseParams = params;
        this.getStore().load({
            params: {
                start: 0,
                limit: this.pageSize
            }
        });
    }

});

Ext.reg('i_resultgrid', Icos.result.Grid);
