Ext.namespace('Icos.result');

Icos.result.Grid =  Ext.extend(Ext.grid.GridPanel, {
    
    pageSize: 25,
    
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
            },
            [ // RecordType
                 'id',
                 'title',
                 'summary',
                 'geo_extent',
                 {name: 'start_time', type: 'date'},
                 {name: 'end_time', type: 'date'},
                 'variables',
                 'data_link'
            ]),
            remoteSort: true,
            sortInfo: {
                field: 'title',
                direction: 'ASC'
            }
        });        
        
        var config = {
            autoHeight: true,
            autoScroll: true,
            autoExpandColumn: "title",
            ds: ds,
            cm: new Ext.grid.ColumnModel([
                {header: "Id", width: 100, sortable: true, dataIndex: 'id'},
                {id: "title", header: "Title", width: 'auto', sortable: true, dataIndex: 'title'},
                {header: "Summary", width: 125, sortable: false, dataIndex: 'summary'},
                {header: "Geo Extent", width: 225, sortable: false, dataIndex: 'geo_extent'},
                {header: "From date", width: 70, sortable: true, dataIndex: 'start_time', renderer: Ext.util.Format.dateRenderer('d/m/Y')}, // H:i:s
                {header: "To date", width: 70, sortable: true, dataIndex: 'end_time', renderer: Ext.util.Format.dateRenderer('d/m/Y')},
                {header: "Variables", width: 150, sortable: false, dataIndex: 'variables'},
                {header: "Data Link", width: 250, sortable: false, dataIndex: 'data_link', renderer: function(value){return '<a href="'+value+'" target="_blank">'+value+'</a>';}}
            ]),
            bbar: new Ext.PagingToolbar({
                pageSize: this.pageSize,
                store: ds,
                displayInfo: true,
                displayMsg: 'Displaying data records {0} - {1} of {2}',
                emptyMsg: "No data records to display"
            })
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
