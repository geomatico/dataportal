Ext.define('variables.Panel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.variables',

    /* i18n */
    title: "Variables",
    /* ~i18n */
    
    rootVisible: false,
    useArrows: true,
    border: true,
    singleExpand: true,
    
    initComponent: function() {
        
        this.store = Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                children: []
            }
        });
        
        Ext.define('Domain', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'id', mapping: '@name', type: 'string'},
                {name: 'text', mapping: '@name', type: 'string'},
                {name: 'expanded', defaultValue: false},
                {name: 'iconCls', defaultValue: 'icon-domain'}
            ],
            hasMany: {model: 'Term', reader: {
                type: 'xml',
                root: 'domain',
                record: 'term'
            }, associationKey : 'term'}
        });

        Ext.define('Term', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'text', mapping: 'description', type: 'string'},
                {name: 'qtip', mapping: 'description', type: 'string'},
                {name: 'id', mapping: 'standard-name', type: 'string'},
                {name: 'checked', defaultValue: false},
                {name: 'iconCls', defaultValue: 'icon-variable'},
                {name: 'leaf', defaultValue: true}
            ],
            belongsTo: 'Domain'
        });
       
        var xmlStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'Domain',
            proxy: {
                type: 'ajax',
                url: 'xml/vocabulary.xml',
                reader: {
                    type: 'xml',
                    root: 'vocab',
                    record: 'domain',
                    getAssociatedDataRoot: function(data, associationName) {
                        if(associationName == 'term')
                           return data;
                        else
                           return this.callParent(arguments);
                    }
                }
            },
            sorters: {
                property: 'text'
            },
            listeners: {
                load: function (store){
                    store.each(function(record){
                        var node = this.getRootNode().appendChild(record.data);
                        record.terms().sort('text', 'ASC');
                        record.terms().each(function(record){
                            this.appendChild(record.data);
                        }, node);
                    }, this);
                },
                scope: this
            }
        });
        
        this.callParent(arguments);
    },
    
    getSelected: function() {
        var records = this.getView().getChecked(),
            names = [];
    
        Ext.Array.each(records, function(record) {
            names.push(record.get('id'));
        });
        
        return names;
    },
    
    getTexts: function(ids) {
        var texts = [];
        Ext.Array.each(ids, function(id){
            texts.push(this.findTextById(id));
        }, this);
        return texts;
    },
    
    findTextById: function(id) {
        var term = this.getRootNode().findChild('id', id, true);
        if(term)
            return term.data.text;
        else
            return id;
    }
    
});
