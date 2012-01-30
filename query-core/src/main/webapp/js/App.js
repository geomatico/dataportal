Ext.define('App', {
    extend: 'Ext.container.Viewport',

    /* i18n */
    getByIdTitle: "Get download by ID",
    searchTitle: "Search data",
    resultsTitle: "Results",
    downloadsTitle: "Download bundle",
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
    padding: 0,
    
    
    initComponent: function() {
        Ext.QuickTips.init();
        
        this.setHomeLocale();
        
        this.user = Ext.create('Authentication');
        
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
                'data_link',
                'opendap'
            ]
        });
        
        this.queryById = Ext.create('query.Identifier', {
            title: this.getByIdTitle,
            collapsible: true,
            animCollapse: false,
            buttons: [{
                text: this.showButtonText,
                handler: this.doLoadDataset,
                scope: this
            }]
        });
        
        this.variables = Ext.create('variables.Panel', {
            height: 258,
            width: 258,
            collapsible: true,
            border: true,
            scroll: false,
            viewConfig: {
               style: {overflow: 'auto', overflowX: 'hidden'}
            }
        });

        this.queryForm = Ext.create('query.Form', {
            title: this.searchTitle,
            variables: this.variables,
            buttons: [{
                text: this.searchButtonText,
                handler: this.doQuery,
                scope: this
            }]
        });
        
        this.resultGrid = Ext.create('result.Grid', {
            variables: this.variables,
            model: 'Result',
            downloadHandler: function(grid, rowIndex, colIndex) {
                var id = grid.store.getAt(rowIndex).get("id");
                this.addRecordToDownload(grid.store.getAt(rowIndex));
            },
            handlerScope: this,
            scroll: false,
            viewConfig: {
               style: {overflow: 'auto', overflowX: 'hidden'}
            }
        });

        this.downloadPanel = Ext.create('download.Panel', {
            model: 'Result',
            user: this.user
        });
        
        this.items = [{
            contentEl: 'header',
            region: 'north',
            xtype: 'box',
            height: 32
        }, {
            region: 'west',
            split: false,
            width: 295,
            collapsible: true,
            layout: {
                type: 'vbox',
                align:'stretch'
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
            split: true,
            width: 300,
            maxWidth: 600,
            minWidth: 150,
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
