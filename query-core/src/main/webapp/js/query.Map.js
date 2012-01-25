Ext.define('query.Map', {
    extend: 'base.Map',
    
    /* i18n */
    titleText: "Location:",
    navigationButtonTooltip: "Navigate",
    addBoxButtonTooltip: "Add Box",
    removeBoxButtonTooltip: "Remove Box",
    /* ~i18n */

    bboxLayer: null,
    bboxWriter: null,
    
    width: 258,
    height: 285,
    
    initComponent: function() {

        this.bboxLayer = new OpenLayers.Layer.Vector("BBOXes");
        
        this.bboxWriter = new OpenLayers.Format.BBOXes({
            internalProjection: this.mapProj,
            externalProjection: this.outProj
        });
        
        this.map.addLayers([this.bboxLayer]);
        
        this.tbar = [
             {
                 xtype: 'tbtext',
                 text: this.titleText
             }, "->",
             new GeoExt.Action({
                 control: new OpenLayers.Control.Navigation(),
                 map: this.map,
                 toggleGroup: "draw",
                 allowDepress: false,
                 pressed: true,
                 tooltip: this.navigationButtonTooltip,
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
                 tooltip: this.addBoxButtonTooltip,
                 iconCls: 'bboxAdd',
                 group: "draw"
             }),
             new GeoExt.Action({
                 control: new OpenLayers.Control.DeleteFeature(this.bboxLayer),
                 map: this.map,
                 toggleGroup: "draw",
                 allowDepress: false,
                 tooltip: this.removeBoxButtonTooltip,
                 iconCls: 'bboxDel',
                 group: "draw"
             })
         ];
        
        this.callParent(arguments);

    },

    getBBOXes: function() {
        return this.bboxWriter.write(this.bboxLayer.features);
    }
});
