Ext.define('result.Grid', {
    extend: 'Ext.grid.Panel',
    
    /* i18n */
    errorTitle: "Search Error",
    genericErrorMessage: "Unspecified Query Error",
    summaryHeader: "Summary",
    extentHeader: "Spatial Extent",
    variablesHeader: "Variables",
    idHeader: "Id",
    titleHeader: "Title",
    fromDateHeader: "From date",
    toDateHeader: "To date",
    downloadActionTooltip: "Add to Downloads",
    dataplotActionTooltip: "Data Preview",
    dateDisplayFormat: "M j, Y",
    pagingDisplayMessage: "Displaying data records {0} - {1} of {2}",
    pagingEmptyMessage: "No data records to display",
    /* ~i18n */
    
    pageSize: 25,
    variables: null,
    recordType: null,
    downloadHandler: null,
    handlerScope: null,

    border: false,
    
    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl: [],
        onExpandBody: function(rowNode, record, dom) {
            var resultgrid = this.getCmp();
            var body = dom.firstChild.firstChild;
            if(!body.innerHTML) {
                var data = record.data;

                var varnames = resultgrid.variables.getTexts(data.variables.split(","));

                new Ext.Panel({
                    layout: 'hbox',
                    padding: 3,
                    defaults: {
                        height: 285
                    },
                    items: [{
                        title: resultgrid.summaryHeader,
                        html: data.summary,
                        flex: 2
                    },{
                        title: resultgrid.extentHeader,
                        items: [new result.Map({resultExtent: data.geo_extent, border: false})],
                        width: 260
                    },{
                        title: resultgrid.variablesHeader,
                        html: varnames.join("<br>"),
                        flex: 1
                    }],
                    listeners: {
                        afterlayout: function() {
                            this.getCmp().doComponentLayout();
                        },
                        scope: this
                    },
                    renderTo: body
                });
            }
            //resultgrid.invalidateScroller();
            //resultgrid.forceComponentLayout();
        }
    }],

    initComponent: function() {
        
        this.columns = [
            {
                header: this.idHeader,
                width: 150,
                sortable: true,
                dataIndex: 'id'
            },{
                header: this.titleHeader,
                flex: 1,
                sortable: true,
                dataIndex: 'title'
            },{
                header: this.fromDateHeader,
                width: 90,
                sortable: true,
                dataIndex: 'start_time',
                renderer: Ext.util.Format.dateRenderer(this.dateDisplayFormat)
            },{
                header: this.toDateHeader,
                width: 90,
                sortable: true,
                dataIndex: 'end_time',
                renderer: Ext.util.Format.dateRenderer(this.dateDisplayFormat)
            },{
                xtype: "actioncolumn",
                width: 30,
                iconCls: "icon-dataplot",
                tooltip: this.dataplotActionTooltip,
                align: "center",
                handler: this.dataplotHandler,
                scope: this
            },{
                xtype: "actioncolumn",
                width: 30,
                iconCls: "icon-download",
                tooltip: this.downloadActionTooltip,
                align: "center",
                handler: this.downloadHandler,
                scope: this.handlerScope
            }
        ];
        
        this.store = Ext.create('Ext.data.Store', {
            model: this.model,
            proxy: {
                type: 'ajax',
                url: 'search',
                reader: {
                    type: 'xml',
                    root: 'response',
                    record: 'item',
                    idProperty: 'id',
                    totalProperty: '@totalcount',
                    successProperty: '@success',
                    messageProperty: 'error/message'
                },
                listeners: {
                    exception: function (proxy, response, operation, options) {
                        if (response && response.success == false) {
                            Ext.Msg.show({
                                title: this.errorTitle,
                                msg: response.message || this.genericErrorMessage,
                                width: 300,
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.ERROR
                             });
                        }
                    },
                    scope: this
                },
                simpleSortMode: true
            },
            pageSize: this.pageSize,
            sorters: ['title'],
            remoteSort: true
        });

        this.dockedItems = [{
            xtype: 'pagingtoolbar',
            store: this.store,
            dock: 'bottom',
            displayInfo: true,
            displayMsg: this.pagingDisplayMessage,
            emptyMsg: this.pagingEmptyMessage
        }];
        
        this.callParent(arguments);
    },
    
    load: function(params, options) {
        this.getStore().getProxy().extraParams = params;
        this.getStore().load(options);
    },
    
    dataplotHandler: function(grid, rowIndex, colIndex) {
        var itemData = grid.store.getAt(rowIndex).data;
        
        // Construct JSON URL
        var json_url = itemData.opendap.split(".");
        json_url.pop();
        json_url = json_url.join(".") + ".json";
        if(location.href.split("/")[2] != json_url.split("/")[2])
            json_url = "proxy?url=" + json_url; // cross-domain
        
        // Create dataplot window
        Ext.create('Ext.window.Window', {
            title: this.dataplotActionTooltip + " - " + itemData.title,
            maximizable: true,
            minimizable: true,
            constrain: true,
            width: 800,
            height: 600,
            layout: 'fit',
            items: {
                xtype: 'dataplot',
                url: json_url
            }
        }).show();
    }

});
