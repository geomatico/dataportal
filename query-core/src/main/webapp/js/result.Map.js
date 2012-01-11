Ext.define('result.Map', {
    extend: 'GeoExt.MapPanel',

    map: null,
    fullExtent: null,
    extentWKT: null,
    extentLayer: null,
    resultExtent: null,
    mapProj: null,
    outProj: null,
    
    height: 258,

    initComponent: function() {

        this.mapProj = new OpenLayers.Projection("EPSG:900913");
        this.outProj = new OpenLayers.Projection("EPSG:4326");
        this.fullExtent = new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508);
        
        this.extentLayer = new OpenLayers.Layer.Vector("Extent",{strategies:[new OpenLayers.Strategy.DrawTinyFeaturesAsPoints()]});
        
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

        this.extent = this.fullExtent;
        
        if(this.resultExtent && (ext = this.extentReader.read(this.resultExtent))) {
            this.extentLayer.addFeatures([ext]);
            this.extent = ext.geometry.getBounds();
        }
        
        this.map.zoomToExtent(this.extent);
        
        this.callParent(arguments);
    }
});
