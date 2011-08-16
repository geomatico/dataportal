Ext.namespace('Icos.result');

Icos.result.Grid =  Ext.extend(Ext.grid.GridPanel, {
    
    initComponent: function() {
        
        var config = {
            autoHeight: true,
            autoScroll: true,
            autoExpandColumn: "title",
            ds: new Ext.data.Store({
                autoLoad: false,
                url: 'search', // 'xml/fakeSearch.xml',
                storeId: 'searchResponse',
                reader: new Ext.data.XmlReader({
                    root: 'response',
                    record: 'metadata',
                    id: 'id',
                    totalProperty: '@totalcount'
                },
                [ // RecordType
                     'uuid',
                     'schema',
                     'title',
                     'summary',
                     {name: 'geo_wkt', mapping: 'extent'},
                     {name: 'start_date', mapping: 'temporalextent > time_coverage_start', type: 'date'},
                     {name: 'end_date', mapping: 'temporalextent > time_coverage_end', type: 'date'},
                     'variables',
                     {name: 'score', type: 'float'}
                ])
            }),
            cm: new Ext.grid.ColumnModel([
                {header: "Score", width: 40, sortable: true, dataIndex: 'score'},
                {id: "title", header: "Title", width: 'auto', sortable: true, dataIndex: 'title'},
                {header: "Abstract", width: 25, sortable: false, dataIndex: 'summary'},
                {header: "Geo Extent (WKT)", width: 225, sortable: false, dataIndex: 'geo_wkt'},
                {header: "From date", width: 70, sortable: false, dataIndex: 'start_date', renderer: Ext.util.Format.dateRenderer('d/m/Y')},
                {header: "To date", width: 70, sortable: false, dataIndex: 'end_date', renderer: Ext.util.Format.dateRenderer('d/m/Y')},
                {header: "Variables", width: 150, sortable: false, dataIndex: 'variables'}
            ])
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.result.Grid.superclass.initComponent.apply(this, arguments);
    },
    
    load: function(params) {
        this.getStore().load({params:params});
    }

});

Ext.reg('i_resultgrid', Icos.result.Grid);
