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
    
    initComponent: function() {
        
        this.columns = [
            //expander,
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
        
        /*
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
                    padding: 5,
                    layoutConfig: {
                        align : 'stretch'
                    },
                    defaults: {
                        height: 285,
                        padding: 5
                    },
                    items: [{
                        title: this.summaryHeader,
                        html: record.get("summary"),
                        columnWidth: .70
                    },{
                        title: this.extentHeader,
                        items: [new result.Map({resultExtent: record.get("geo_extent"), border: false})],
                        padding: 0,
                        width: 260
                    },{
                        title: this.variablesHeader,
                        html: varnames.join("<br>"),
                        columnWidth: .30 
                    }],
                    renderTo: body
                });
            }
        }, this);
        */

        this.dockedItems = [{
            xtype: 'pagingtoolbar',
            store: this.store,
            dock: 'bottom',
            displayInfo: true,
            displayMsg: this.pagingDisplayMessage,
            emptyMsg: this.pagingEmptyMessage
        }];
        //this.plugins = expander;
        
        this.callParent(arguments);
    },
    
    load: function(params, options) {
        this.getStore().getProxy().extraParams = params;
        this.getStore().load();
    }

});
