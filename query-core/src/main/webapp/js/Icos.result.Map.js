Ext.namespace('Icos.result');

Icos.result.Map =  Ext.extend(GeoExt.MapPanel, {

    map: null,
    fullExtent: null,
    extentWKT: null,
    extentLayer: null,
    resultExtent: null,
    mapProj: null,
    outProj: null,
    
    initComponent: function() {

        this.mapProj = new OpenLayers.Projection("EPSG:900913");
        this.outProj = new OpenLayers.Projection("EPSG:4326");
        this.fullExtent = new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508);
        
        this.extentLayer = new OpenLayers.Layer.Vector("Extent");
        
        this.extentReader = new OpenLayers.Format.WKT({
            internalProjection: this.mapProj,
            externalProjection: this.outProj
        });
        
        this.map = new OpenLayers.Map({
            controls: [new OpenLayers.Control.Navigation()],
            projection: this.mapProj,
            displayProjection: this.outProj,
            units: "m",
            numZoomLevels: 7,
            maxResolution: 156543.0339,
            maxExtent: this.fullExtent,
            restrictedExtent: this.fullExtent,
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
                this.extentLayer
            ]
        });

        var extent = this.fullExtent;
        
        if(this.resultExtent && (ext = this.extentReader.read(this.resultExtent))) {
            this.extentLayer.addFeatures([ext]);
            extent = ext.geometry.getBounds();
        }
        
        var config = {
            xtype: 'gx_mappanel',
            map: this.map,
            extent: extent,
            height: 258
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        Icos.result.Map.superclass.initComponent.apply(this, arguments);
    }
});

Ext.reg('i_resultmap', Icos.result.Map);
