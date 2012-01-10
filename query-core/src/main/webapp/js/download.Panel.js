Ext.define('download.Panel', {
    extend: 'Ext.Panel',
    
    /* i18n */
    downloadReadyTitle: "Download Ready",
    downloadReadyMessage: "Your data request with UUID<br/><b>{0}</b><br/>is ready.<br/><br/>Click OK to download.",
    downloadErrorTitle: "Download Error",
    downloadButtonText: "Download",
    idColumnHeader: "Id",
    titleColumnHeader: "Title",
    removeColumnTooltip: "Remove from Downloads",
    loginRequiredMessage: "Sorry, you must Login to download data",
    /* ~i18n */
    
    user: null,
    downloadButton: null,
    
    layout: {
        type: 'vbox',
        align: 'stretch',
        animate: true
    },
    
    border: false,
    
    initComponent: function() {

        this.store = Ext.create('Ext.data.Store', {
            model: this.model,
            autoLoad: false,
            autoSave: false,
            user: this.user,
            proxy: {
                type: 'ajax',
                url: 'download',
                reader: {
                    type: 'xml',
                    root: 'response',
                    record: 'item',
                    idProperty: 'id'
                },
                writer: {
                    type: 'xml',
                    documentRoot: 'response',
                    record: 'item',
                    writeAllFields: true
                }
            },
            pageSize: this.pageSize,
            sorters: ['title'],
            remoteSort: false
        });
               
        this.downloadButton = new Ext.Button({
            text: this.downloadButtonText,
            margins: '5',
            handler: this.notLoggedIn,
            scope: this
        });
        
        this.user.on("logged_in", function(){
            this.downloadButton.setHandler(this.send, this);
        }, this);
        
        this.user.on("logged_out", function() {
            this.downloadButton.setHandler(this.notLoggedIn, this);
        }, this);
                
        this.items = [
            Ext.create('Ext.grid.Panel', {
                flex: 1,
                autoScroll: true,
                store: this.store,
                border: false,
                columns: [
                    {
                        header: this.idColumnHeader,
                        width: 30,
                        sortable: true,
                        dataIndex: 'id'
                    },{
                        header: this.titleColumnHeader,
                        flex: 1,
                        sortable: true,
                        dataIndex: 'title'
                    },{
                        xtype: "actioncolumn",
                        width: 30,
                        iconCls: "icon-delete",
                        tooltip: this.removeColumnTooltip,
                        align: "center",
                        handler: function(grid, rowIndex, colIndex) {
                            grid.store.remove(grid.store.getAt(rowIndex));
                        }
                    }
                ]
            }),
            this.downloadButton
        ];
        
        this.callParent(arguments);
    },
    
    send: function() {
        if (!this.store.getProxy().getWriter()) {
            throw new Ext.data.Store.Error('writer-undefined');
        }

        this.store.getProxy().create(
            Ext.create('Ext.data.Operation', {
                action: 'create',
                records: this.store.data.items
            }),
            function(operation) {
                var response = operation.response;
                var id = Ext.DomQuery.selectValue("//download/id", response.responseXML);
                var fileName = Ext.DomQuery.selectValue("//download/filename", response.responseXML);
                var success = Ext.DomQuery.selectValue('/response/@success', response.responseXML);
                if (fileName) {
                    Ext.Msg.show({
                        title: this.downloadReadyTitle,
                        msg: Ext.String.format(this.downloadReadyMessage, id),
                        width: 350,
                        buttons: Ext.MessageBox.OK,
                        fn: function(id, text, opt) {
                            window.location = "download?file="+fileName+"&user="+this.user.user+"&password="+this.user.password; 
                        },
                        icon: Ext.MessageBox.INFO,
                        scope: this
                     });
                                               
                } else if (success=="false") {
                    var err = Ext.DomQuery.selectValue('/response/error/message', response.responseXML);
                    Ext.Msg.show({
                        title: this.downloadErrorTitle,
                        msg: err,
                        width: 300,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR,
                        scope: this
                     });
                }
            },
            this
        );
    },
    
    notLoggedIn: function() {
        alert(this.loginRequiredMessage);
    }
});
