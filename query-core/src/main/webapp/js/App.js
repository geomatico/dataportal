Ext.define('App', {
    extend: 'Ext.container.Viewport',

    /* i18n */
    queriesTitle: "1. Queries",
    resultsTitle: "2. Results",
    downloadsTitle: "3. Downloads",
    fileDownloadMessage: "This file is already set for download",
    homeTitle: "ICOS Spain Carbon Data Portal",
    homeLogin: "Login",
    homeCreateUser: "Sign Up",
    homeLogout: "Logout",
    homeUpdateUser: "Change Password",
    showButtonText: "Show Dataset >>",
    searchButtonText: "Search >>",
    /* ~i18n */
    
    vocabulary: null,
    dataRecordType: null,
    queryForm: null,
    resultGrid: null,
    downloadPanel: null,
    user: null,
    
    layout: 'border',
    
    initComponent: function() {
        Ext.QuickTips.init();
        
        //this.setHomeLocale();
        
        this.user = Ext.create('Authentication');
        
        Ext.define('Term', {
            extend: 'Ext.data.Model',
            fields: [
                 'sado_term', 'nc_term', 'nc_long_term',
                 'en_term', 'en_long_term',
                 'nc_units', 'nc_data_type',
                 'nc_coordinate_axis_type'
            ]
        });
        
        this.vocabulary = Ext.create('Ext.data.Store', {
            model: 'Term',
            proxy: {
                type: 'ajax',
                url: 'xml/vocabulario.xml',
                reader: {
                    type: 'xml',
                    record: 'term',
                    idProperty: 'nc_term',
                }
            },
            sorters: ['nc_long_term'],
            remoteSort: false
        });

        Ext.define('Result', {
            extend: 'Ext.data.Model',
            fields: [
                'id',
                'title',
                'summary',
                'institution',
                'creator_url',
                'data_type',
                'icos_domain',
                'geo_extent',
                {name: 'start_time', type: 'date'},
                {name: 'end_time', type: 'date'},
                'variables',
                'data_link'
            ]
        });
        
        this.queryById = Ext.create('query.Identifier', {
            buttons: [{
                text: this.showButtonText,
                handler: this.doLoadDataset,
                scope: this
            }]
        });

        this.queryForm = Ext.create('query.Form', {
            vocabulary: this.vocabulary,
            buttons: [{
                text: this.searchButtonText,
                handler: this.doQuery,
                scope: this
            }]
        });
        
        this.vocabulary.load();
        
        this.resultGrid = Ext.create('result.Grid', {
            vocabulary: this.vocabulary,
            model: 'Result',
            downloadHandler: function(grid, rowIndex, colIndex) {
                var id = grid.store.getAt(rowIndex).get("id");
                this.addRecordToDownload(grid.store.getAt(rowIndex));
            },
            handlerScope: this
        });

        this.downloadPanel = Ext.create('download.Panel', {
            model: 'Result',
            user: this.user
        });
        
        this.items = [{
            contentEl: 'header',
            region: 'north',
            xtype: 'box',
            height: 64
        }, {
            title: this.queriesTitle,
            region: 'west',
            split: false,
            width: 287,
            collapsible: true,
            layout: {
                type: 'vbox',
                align:'stretch',
                animate: true
            },
            items: [
                this.queryById,
                this.queryForm
            ]
        }, {
            title: this.resultsTitle,
            region: 'center',
            layout: 'fit',
            items: [
                this.resultGrid
            ]
        }, {
            title: this.downloadsTitle,
            region: 'east',
            layout: 'fit',
            split: false,
            width: 287,
            items: [
                this.downloadPanel
            ]
        }];
        
        this.callParent(arguments);
    },

    doQuery: function() {
        this.resultGrid.load(this.queryForm.getParams());
    },

    doLoadDataset: function() {
        this.resultGrid.store.removeAll();
        this.downloadPanel.store.removeAll();
        this.resultGrid.load(this.queryById.getParams(), {
            callback: function() {
                // Propagate records to download list
                this.resultGrid.store.each(function(record) {
                    this.addRecordToDownload(record);
                }, this);
            },
            scope: this
        });
    },
    
    addRecordToDownload: function(record, silent) {
        var clonedRecord = record.copy();
        if(this.downloadPanel.store.getById(clonedRecord.data.id)) {
            alert(this.fileDownloadMessage);
        } else {
            this.downloadPanel.store.add(clonedRecord);
        }
    },
    
    setHomeLocale: function() {
        Ext.get("title").update(this.homeTitle);
        Ext.get("login").update(this.homeLogin);
        Ext.get("createUser").update(this.homeCreateUser);
        Ext.get("logout").update(this.homeLogout);
        Ext.get("updateUser").update(this.homeUpdateUser);
    }
    
});
