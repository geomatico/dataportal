Ext.namespace('download');

download.Panel =  Ext.extend(Ext.Panel, {
    
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
    
    store: null,
    recordType: null,
    user: null,
    downloadButton: null,
    
    initComponent: function() {
        
        this.store = new Ext.data.Store({
            autoLoad: false,
            autoSave: false,
            url: 'download',
            user: this.user,
            reader: new Ext.data.XmlReader({
                root: 'response',
                record: 'item',
                id: 'id'
            }, this.recordType),
            writer: new Ext.data.XmlWriter({
                xmlEncoding: "UTF-8",
                documentRoot: 'response',
                writeAllFields: true,
                forceDocumentRoot: true
            }),
            send: function() {
                if (!this.writer) {
                    throw new Ext.data.Store.Error('writer-undefined');
                }
                
                var request = {
                    url: this.url,
                    success: function(response) {
                        var id = Ext.DomQuery.selectValue("//download/id", response.responseXML);
                        var fileName = Ext.DomQuery.selectValue("//download/filename", response.responseXML);
                        var success = Ext.DomQuery.selectValue('/response/@success', response.responseXML);
                        if (fileName) {
                            Ext.Msg.show({
                                title: this.downloadReadyTitle,
                                msg: String.format(this.downloadReadyMessage, id),
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
                    failure: function(response) {
                        alert(response.status + " " + response.statusText);
                    },
                    params: {
                        user: this.user.user,
                        password: this.user.password
                    },
                    method: "POST",
                    scope: this
                };
                
                this.writer.apply(request, [], "create", this.data.items);
                Ext.Ajax.request(request);
            }
        });
        
        this.downloadButton = new Ext.Button({
            text: this.downloadButtonText,
            margins: '5'
        });
        
        this.downloadButton.setHandler(this.notLoggedIn, this);
        
        this.user.on("logged_in", function(){
            this.downloadButton.setHandler(function() {
                this.store.send();
            }, this);
        }, this);
        
        this.user.on("logged_out", function() {
            this.downloadButton.setHandler(this.notLoggedIn, this);
        }, this);
                
        var config = {
            layout: {
                type: 'vbox',
                align:'stretch',
                animate: true
            },
            items: [
                new Ext.grid.GridPanel({
                    autoHeight: true,
                    flex: 1,
                    autoScroll: true,
                    autoExpandColumn: "title",
                    ds: this.store,
                    cm: new Ext.grid.ColumnModel([
                        {header: this.idColumnHeader, width: 30, sortable: true, dataIndex: 'id'},
                        {id: "title", header: this.titleColumnHeader, width: 'auto', sortable: true, dataIndex: 'title'},
                        {
                            xtype: "actioncolumn",
                            width: 30,
                            iconCls: "icon-delete",
                            tooltip: this.removeColumnTooltip,
                            align: "center",
                            handler: function(grid, rowIndex, colIndex) {
                                grid.store.remove(grid.store.getAt(rowIndex));
                            }
                        }
                    ])
                }),
                this.downloadButton
            ]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        download.Panel.superclass.initComponent.apply(this, arguments);
    },
    
    notLoggedIn: function() {
        alert(this.loginRequiredMessage);
    }
});

Ext.reg('i_downloadpanel', download.Panel);
