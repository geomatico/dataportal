Ext.define('dataplot.Map', {
    extend: 'base.Map',
    alias: 'widget.dataplotmap',
    
    featureCollection: null,
    info: null,
    
    initComponent: function() {
        
        this.trajectoryLayer = new OpenLayers.Layer.Vector("Data");
        
        var reader = new OpenLayers.Format.GeoJSON({
            internalProjection: new OpenLayers.Projection("EPSG:900913"),
            externalProjection: new OpenLayers.Projection("EPSG:4326")
        });
        var features = reader.read(this.featureCollection);
        this.trajectoryLayer.addFeatures(features);
        
        this.trajectoryLayer.events.on({
            'featureselected': this.showProperties,
            'featureunselected': this.showProperties,
            scope: this
        });
        
        this.map.addLayers([this.trajectoryLayer]);

        this.map.addControls([
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.SelectFeature(
                this.trajectoryLayer,
                {
                    multiple: false,
                    hover: true,
                    autoActivate: true
                }
            )
        ]);
               
        this.extent = this.trajectoryLayer.getDataExtent();
        this.map.zoomToExtent(this.extent);
        
        this.items = [{
            xtype: 'panel',
            itemId: 'info',
            cls: 'info',
            //layout: 'fit',
            maxWidth: 240,
            maxHeight: 180,
            autoScroll: true
                
        }];
        
        this.callParent(arguments);   
    },
    
    showProperties: function(e) {
        var feature = e.feature;
        //var layer = e.object;
        //var features = layer.selectedFeatures;
        if (feature) {
            var info = this.query("#info")[0];
            var content = "<div class='content'>";
            Ext.Object.each(feature.attributes, function(name, value) {
                content += "<div class='attribute'>";
                content += "<div class='name'>"+name+"</div>";
                content += "<div class='value'>"+value+"</div>";
                content += "</div>";
            });
            content += "</div>";
            info.update(content);
        }
    },
    
});
