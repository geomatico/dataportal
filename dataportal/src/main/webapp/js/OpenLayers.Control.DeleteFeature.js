/* Copyright (c) 2011 Oscar Fonts. Published under the Clear BSD license.
 * See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */
OpenLayers.Control.DeleteFeature = OpenLayers.Class(OpenLayers.Control.SelectFeature, {

    confirm: false,
    confirmText: "Do you really want to remove the selected feature?",
    
    select: function(feature) {
        OpenLayers.Control.SelectFeature.prototype.select.apply(this, [feature]);
        
        if (!this.confirm || confirm(this.confirmText)) {
            feature.layer.removeFeatures(feature);
        } else {
            this.unselect(feature);
        }
    },

    CLASS_NAME: "OpenLayers.Control.DeleteFeature"
});