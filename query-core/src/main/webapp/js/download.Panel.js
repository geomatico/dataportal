Ext.namespace('download');

download.Panel =  Ext.extend(Ext.Panel, {
    
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
                        var success = Ext.DomQuery.selectValue('/response/@success', response.responseXML);
                        if (success=="true"){
                            var fileName = Ext.DomQuery.selectValue("/response/filename", response.responseXML);
                            window.location = "download?file="+fileName;                            
                        } else if (success=="false") {
                            alert(Ext.DomQuery.selectValue('/response/error/message', response.responseXML));
                        }
                    },
                    failure: function(response) {
                        alert(response.status + " " + response.statusText);
                    },
                    params: {
                        user: this.user.user,
                        password: this.user.password
                    },
                    method: "POST"
                };
                
                this.writer.apply(request, [], "create", this.data.items);
                Ext.Ajax.request(request);
            }
        });
        
        this.downloadButton = new Ext.Button({
            text: "Download",
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
                        {header: "Id", width: 30, sortable: true, dataIndex: 'id'},
                        {id: "title", header: "Title", width: 'auto', sortable: true, dataIndex: 'title'},
                        {
                            xtype: "actioncolumn",
                            width: 30,
                            iconCls: "icon-delete",
                            tooltip: "Remove from Downloads",
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
        alert("You must be logged in to download data");
    }
});

Ext.reg('i_downloadpanel', download.Panel);
