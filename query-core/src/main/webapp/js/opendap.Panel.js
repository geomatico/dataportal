Ext.define('opendap.Panel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.opendap',
        
    layout: 'accordion',
    
    dataset: null,
    
    initComponent: function() {
        
        this.read(function(result, request) {
            this.dataset = Ext.JSON.decode(result.responseText);
            this.parse();
        });
        
        this.callParent(arguments);
    },
    
    read: function(callback) {
        Ext.Ajax.request({
            url: this.url,
            success: callback,
            failure: function(result, request) {
                Ext.Msg.alert('Failed', result.responseText);
            },
            scope: this
        });        
    },
    
    parse: function() {
        var timeVarName = "time";
        
        if (this.dataset.variables[timeVarName]) {
            
            var lonVarName, latVarName;
            Ext.Object.each(this.dataset.variables, function(varName, variable) {
                if (variable.attributes.standard_name == "longitude")
                    lonVarName = varName;
                if (variable.attributes.standard_name == "latitude")
                    latVarName = varName;
            });
                        
            if (lonVarName && latVarName) {
                this.addMaps(lonVarName, latVarName, timeVarName);
            }
            
            this.addTimeseries(timeVarName);
        }
    },
    
    addTimeseries: function(timeVarName) {
        var timeVar = this.dataset.variables[timeVarName];
        Ext.Object.each(this.dataset.variables, function(varName, variable) {
            if (Ext.Array.contains(variable.dimensions, timeVarName) && varName != timeVarName) {
                var att = variable.attributes;
                var data = variable.data;
                
                var name = (att.long_name && Ext.String.capitalize(att.long_name)) || att.standard_name || varName;
                var units = att.units || "";
                var fillValue = att._FillValue || null;
                
                var series = [];
                for(i=0; i<data.length; i++) {
                    var time = timeVar.data[i];
                    var value = data[i][0] || data[i];  // TODO: arbitrary # dimensions => series
                    if (value != fillValue) {
                        series.push({ time: time, value: value });
                    }
                }
                
                this.add({
                    title: name,
                    type: 'panel',
                    layout: 'fit',
                    items: [{
                        xtype: 'timeseries',
                        varName: name,
                        varUnits: units,
                        data: series
                    }]
                });
                
            }
        }, this);
    },
    
    addMaps: function(lonVarName, latVarName, timeVarName) {

        // Detect Trajectory Variables
        var trajectoryVars = [];
        Ext.Object.each(this.dataset.variables, function(varName, variable) {
            if(variable.attributes.coordinates) {
                var coords = variable.attributes.coordinates.split(" ");
                if (Ext.Array.contains(coords, lonVarName) &&
                    Ext.Array.contains(coords, latVarName) &&
                    Ext.Array.contains(variable.dimensions, timeVarName)) {
                    trajectoryVars.push(varName); // This is a trajectory variable
                }
            }
        }, this);
        
        // If there are trajectory variables, convert data to GeoJSON
        var lons = this.dataset.variables[lonVarName].data;
        var lats = this.dataset.variables[latVarName].data;
        var times = this.dataset.variables[timeVarName].data;
        if(trajectoryVars.length > 0) {
            var features = [];
            for(i=0; i<times.length; i++) {
                var feature = {
                    "type": "Feature",
                    "geometry": {
                        "type": "Point",
                        "coordinates": [lons[i], lats[i]]
                    },
                    "properties": {}
                };
                for(p=0; p<trajectoryVars.length; p++) {
                    var key = trajectoryVars[p];
                    var variable = this.dataset.variables[key];
                    var att = variable.attributes;
                    var name = (att.long_name && Ext.String.capitalize(att.long_name)) || att.standard_name || key;
                    if(att.units) {
                        name += " ("+att.units+")";
                    }
                    var value = variable.data[i];
                    feature.properties[name] = value;
                }
                features.push(feature);
            }

            this.add({
                title: "Trajectory Map",
                xtype: 'panel',
                layout: 'fit',
                items: [{
                    xtype: 'trajectorymap',
                    featureCollection: {
                        "type": "FeatureCollection",
                        "features": features
                    }
                }]
            });
        }    
    }

});
