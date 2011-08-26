Ext.namespace('Icos.download');

Icos.download.Panel =  Ext.extend(Ext.Panel, {
    
    store: null,
    recordType: null,
    
    initComponent: function() {
        
        this.store = new Ext.data.Store({
            autoLoad: false,
            autoSave: false,
            url: 'xml/fakeDownload.xml',
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
                        alert(response.responseText);
                    },
                    failure: function(response) {
                        alert(response.status + " " + response.statusText);
                    },
                    method: "POST"
                };
                
                this.writer.apply(request, [], "create", this.data.items);
                
                Ext.Ajax.request(request);
            }
        });
                
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
                new Ext.Button({
                    text: "Download",
                    margins: '5',
                    listeners: {
                        'click': function() {
                            this.store.send();
                        },
                        scope: this
                    }
                })
            ]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.download.Panel.superclass.initComponent.apply(this, arguments);
    }
});

Ext.reg('i_downloadpanel', Icos.download.Panel);
