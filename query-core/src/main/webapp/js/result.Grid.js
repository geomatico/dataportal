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
    dateDisplayFormat: "M j, Y",
    pagingDisplayMessage: "Displaying data records {0} - {1} of {2}",
    pagingEmptyMessage: "No data records to display",
    /* ~i18n */
    
    pageSize: 25,
    vocabulary: null,
    recordType: null,
    downloadHandler: null,
    handlerScope: null,

    border: false,
    
    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl: [],
        onExpandBody: function(rowNode, record, dom) {
            var body = dom.firstChild.firstChild;
            if(!body.innerHTML) {
                var data = record.data;
                var resultgrid = this.getCmp();

                var variables = data.variables.split(",");
                var varnames = [];
                for (var i=0; i < variables.length; i++) {
                    variable = variables[i];
                    term = resultgrid.vocabulary.data.getByKey(variable);
                    name = term ? (term.data.nc_long_term || term.data.en_long_term) : variable;
                    varnames.push(name);
                }

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
                    renderTo: body
                });
            }
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
                simpleSortMode: true
            },
            pageSize: this.pageSize,
            sorters: ['title'],
            remoteSort: true
        });
            
        /*
        listeners: {
            exception: function(proxy, type, action, options, response) {
                if (type=="remote" && response && response.success == false) {
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
        }
        */

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
        this.getStore().load();
    }

});
