Ext.define('result.Map', {
    extend: 'base.Map',

    extentWKT: null,
    extentLayer: null,
    resultExtent: null,
    
    height: 258,

    initComponent: function() {
        
        this.extentLayer = new OpenLayers.Layer.Vector("Extent",{strategies:[new OpenLayers.Strategy.DrawTinyFeaturesAsPoints()]});
        
        this.extentReader = new OpenLayers.Format.WKT({
            internalProjection: this.mapProj,
            externalProjection: this.outProj
        });
        
        this.map.addLayers([this.extentLayer]);
        this.map.addControls([new OpenLayers.Control.Navigation()]);
        
        this.extent = this.fullExtent;
        
        if(this.resultExtent && (ext = this.extentReader.read(this.resultExtent))) {
            this.extentLayer.addFeatures([ext]);
            this.extent = ext.geometry.getBounds();
        }
        
        this.map.zoomToExtent(this.extent);
        
        this.callParent(arguments);   
    }
});
