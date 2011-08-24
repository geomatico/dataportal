/* Copyright (c) 2011 Oscar Fonts. Published under the Clear BSD license.
 * See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @requires OpenLayers/Format.js
 * @requires OpenLayers/Feature/Vector.js
 */

/**
 * Class: OpenLayers.Format.WKT
 * Class for reading and writing Well-Known Text.  Create a new instance
 * with the <OpenLayers.Format.WKT> constructor.
 * 
 * Inherits from:
 *  - <OpenLayers.Format>
 */
OpenLayers.Format.BBOXes = OpenLayers.Class(OpenLayers.Format, {
    
    /**
     * Constructor: OpenLayers.Format.BBOXes
     * Create a new writer for BBOXes
     *
     * Parameters:
     * options - {Object} An optional object whose properties will be set on
     *           this instance
     *
     * Returns:
     * {<OpenLayers.Format.BBOXex>} A new BBOX writer.
     */
    initialize: function(options) {
        if (!this.internalProjection && !this.externalProjection) {
            this.internalProjection = new OpenLayers.Projection("EPSG:900913");
            this.externalProjection = new OpenLayers.Projection("EPSG:4326");          
        }
        OpenLayers.Format.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: write
     * Serialize a feature or array of features into a BBOXes string, whose format is:
     * [[Xmin0,Ymin0,Xmax0,Ymax0],[Xmin1,Ymin1,Xmax1,Ymax1],...,[XminN,YminN,XmaxN,YmaxN]]
     *
     * Parameters:
     * features - {<OpenLayers.Feature.Vector>|Array} A feature or array of
     *            features
     *
     * Returns:
     * {String} The BBOXes string representation of the input geometries
     */
    write: function(features) {
        var collection, geometry, type, data, isCollection;
        if(features.constructor == Array) {
            collection = features;
        } else {
            collection = [features];
        }
        var boxes = [];
        for(var i=0, len=collection.length; i<len; ++i) {
            geometry = collection[i].geometry;
            if (this.internalProjection && this.externalProjection) {
                geometry = geometry.clone();
                geometry.transform(this.internalProjection, 
                                   this.externalProjection);
            }                       
            bounds = '['+geometry.getBounds().toBBOX()+']';
            boxes.push(bounds);
        }
        return '['+boxes.join(',')+']';
    },

    CLASS_NAME: "OpenLayers.Format.BBOXes" 
});