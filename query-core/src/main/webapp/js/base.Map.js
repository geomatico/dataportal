Ext.define('base.Map', {
    extend: 'GeoExt.MapPanel',
    
    map: null,
    mapProj: null,
    outProj: null,
    fullExtent: null,
    
    constructor: function(config) {

        this.mapProj = new OpenLayers.Projection("EPSG:900913");
        this.outProj = new OpenLayers.Projection("EPSG:4326");
        this.fullExtent = new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508);
        
        this.map = new OpenLayers.Map({
            controls: [],
            projection: this.mapProj,
            displayProjection: this.outProj,
            units: "m",
            numZoomLevels: 8,
            maxResolution: 156543.0339,
            maxExtent: this.fullExtent,
            restrictedExtent: this.fullExtent,
            layers: [
                new OpenLayers.Layer.TMS( "Blue Marble",
                  [ "http://a.tile.mapbox.com/v3/",
                    "http://b.tile.mapbox.com/v3/",     
                    "http://c.tile.mapbox.com/v3/",
                    "http://d.tile.mapbox.com/v3/" ],
                  { 'layername': 'mapbox.blue-marble-topo-bathy-jul', 'type':'png',
                    'buffer': 0, 'transitionEffect':'resize',
                    isBaseLayer: true, wrapDateLine: true }
                ),
                new OpenLayers.Layer.TMS( "World Borders",
                    [ "http://a.tile.mapbox.com/v3/",
                      "http://b.tile.mapbox.com/v3/",     
                      "http://c.tile.mapbox.com/v3/",
                      "http://d.tile.mapbox.com/v3/" ],
                    { 'layername': 'mapbox.world-borders-light', 'type':'png',
                      'buffer': 0, 'transitionEffect':'resize',
                      isBaseLayer: false, wrapDateLine: true }
                )
            ]
        });
        
        this.callParent(arguments);
    }
});
