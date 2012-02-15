/**
 * @requires OpenLayers/Strategy/Cluster.js
 */

/**
 * Class: OpenLayers.Strategy.DrawTinyFeaturesAsPoints
 * Render very small features as points, so they become visible.
 *
 * Inherits from:
 *  - <OpenLayers.Strategy.Cluster>
 */
OpenLayers.Strategy.DrawTinyFeaturesAsPoints = OpenLayers.Class(OpenLayers.Strategy.Cluster, {
    
    size: 5,
    distance: 1,
    
    initialize: function(options) {
        OpenLayers.Strategy.Cluster.prototype.initialize.apply(this, [options]);
    },
    
    createCluster: function(feature) {
        var bbox = feature.geometry.getBounds();
        var size = Math.max(bbox.getWidth(), bbox.getHeight()) / this.resolution;
        if(size <= this.size){
            return OpenLayers.Strategy.Cluster.prototype.createCluster.apply(this, [feature]);
        } else {
            return feature;
        }
    },
    
    CLASS_NAME: "OpenLayers.Strategy.DrawTinyFeaturesAsPoints" 
});
