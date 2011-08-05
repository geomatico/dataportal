Ext.namespace('Icos.query');

Icos.query.Map =  Ext.extend(GeoExt.MapPanel, {

    map: null,
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
        
        this.map = new OpenLayers.Map({
            controls: [],
            projection: this.mapProj,
            displayProjection: this.outProj,
            units: "m",
            numZoomLevels: 7,
            maxResolution: 156543.0339,
            maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
            restrictedExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
            layers: [
                //new OpenLayers.Layer.OSM("Base Layer"),
                new OpenLayers.Layer.WMS(
                    "Base Layer",
                    "http://ciclope.cmima.csic.es:8080/geoserver/wms",
                    {layers: 'gn:world', format: 'image/jpeg'},
                    {isBaseLayer: true, wrapDateLine: true}
                ),
                new OpenLayers.Layer.WMS(
                        "Base Layer",
                        "http://ciclope.cmima.csic.es:8080/geoserver/wms",
                        {layers: 'gn:gboundaries', format: 'image/png', transparent: true},
                        {isBaseLayer: false, wrapDateLine: true}
                    ),
                this.bboxLayer
            ]
        });

        var config = {
            xtype: 'gx_mappanel',
            map: this.map,
            zoom: 0,
            height: 283,
            tbar: [
                {
                    xtype: 'tbtext',
                    text: 'Location:'
                }, "->",
                new GeoExt.Action({
                    control: new OpenLayers.Control.Navigation(),
                    map: this.map,
                    toggleGroup: "draw",
                    allowDepress: false,
                    pressed: true,
                    tooltip: "Navigate",
                    iconCls: 'mapNav',
                    group: "draw",
                    checked: true
                }),
                new GeoExt.Action({
                    control: new OpenLayers.Control.DrawFeature(
                        this.bboxLayer, OpenLayers.Handler.RegularPolygon, {handlerOptions: {irregular: true}}
                    ),
                    map: this.map,
                    toggleGroup: "draw",
                    allowDepress: false,
                    tooltip: "Add Box",
                    iconCls: 'bboxAdd',
                    group: "draw"
                }),
                new GeoExt.Action({
                    control: new OpenLayers.Control.DeleteFeature(this.bboxLayer),
                    map: this.map,
                    toggleGroup: "draw",
                    allowDepress: false,
                    tooltip: "Remove Box",
                    iconCls: 'bboxDel',
                    group: "draw"
                })                
            ]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.query.Map.superclass.initComponent.apply(this, arguments);
    },

    getBBOXes: function() {
        return this.bboxWriter.write(this.bboxLayer.features);
    }
});

Ext.reg('i_querymap', Icos.query.Map);
