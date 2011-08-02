Ext.namespace('Icos.query');

Icos.query.Map =  Ext.extend(GeoExt.MapPanel, {

    bboxLayer: null,
    bboxWriter: null,
    mapProj: null,
    outProj: null,
    
    initComponent: function() {

        this.mapProj = new OpenLayers.Projection("EPSG:900913");
        this.outProj = new OpenLayers.Projection("EPSG:4326");
        
        this.bboxLayer = new OpenLayers.Layer.Vector("BBOXes");
        
        this.bboxWriter = new OpenLayers.Format.BBOXes({
            internalProjection: this.mapProj,
            externalProjection: this.outProj
        });

        // TODO: Delete this sample BBOX
        bounds = OpenLayers.Bounds.fromArray([1, 38, 5, 41]).transform(this.outProj, this.mapProj);
        box = new OpenLayers.Feature.Vector(bounds.toGeometry());
        this.bboxLayer.addFeatures([box]);
        
        var config = {
            xtype: 'gx_mappanel',
            map: {
                controls: [new OpenLayers.Control.Navigation()],
                projection: this.mapProj,
                displayProjection: this.outProj
            },
            layers: [new OpenLayers.Layer.OSM("Base Layer"), this.bboxLayer],
            zoom: 0,
            height: 283,
            tbar: [{
                xtype: 'tbtext',
                text: 'Location:'
            }, "->", {
                xtype: 'tbbutton',
                qtip: 'Navigate',
                iconCls: 'mapNav',
                handler: function(btn) {
                    // TODO
                }
            }, {
                xtype: 'tbbutton',
                qtip: 'Add filter',
                iconCls: 'bboxAdd',
                handler: function(btn) {
                    // TODO
                }
            }, {
                xtype: 'tbbutton',
                qtip: 'Remove filter',
                iconCls: 'bboxDel',
                handler: function(btn) {
                    // TODO
                }
            }]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.query.Map.superclass.initComponent.apply(this, arguments);
    },

    getBBOXes: function() {
        return this.bboxWriter.write(this.bboxLayer.features);
    }
});

Ext.reg('i_querymap', Icos.query.Map);
