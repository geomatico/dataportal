Ext.define('dataplot.Panel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.dataplot',
    
    dataset: null,
    max_samples: 512,
    
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
            disableCaching: false,
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
            
            if(this.dataset.variables[timeVarName].data.length > this.max_samples) {
                this.shrink();
            }
            
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
    
    shrink: function() {
        var len = this.dataset.variables["time"].data.length,
            ratio = Math.ceil(len / this.max_samples);
        
        Ext.Object.each(this.dataset.variables, function(varName, variable) {
            var timeDim = Ext.Array.indexOf(variable.dimensions, "time");
            if(timeDim==0) {
                var sum = 0,
                    arr = variable.data,
                    res = [arr[0]],
                    samples = 0,
                    fillValue = parseFloat(variable.attributes._FillValue) || null;
                for (i = 1; i < len; i++) {
                    if (arr[i] != fillValue) {
                        sum += +arr[i] || 0;
                        samples++;
                    }
                    if (i % ratio == 0) {
                        if(samples>0)
                            res.push(sum/samples);
                        else
                            res.push(fillValue);
                        sum = 0;
                        samples = 0;
                    }
                }
                variable.data = res;
            }
        });
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
        
        var datatype = this.dataset.global_attributes.cdm_data_type.toUpperCase();

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
                if(datatype=="STATION") {
                    feature.properties["station"] = i+1;
                } else {
                    for(p=0; p<trajectoryVars.length; p++) {
                        var key = trajectoryVars[p];
                        var variable = this.dataset.variables[key];
                        var att = variable.attributes;
                        var name = (att.long_name && Ext.String.capitalize(att.long_name)) || att.standard_name || key;
                        if(att.units) {
                            name += "<br/>("+att.units+")";
                        }
                        var value = variable.data[i];
                        feature.properties[name] = value;
                    }
                }
                features.push(feature);
            }

            this.add({
                title: "Map",
                xtype: 'panel',
                layout: 'fit',
                items: [{
                    xtype: 'dataplotmap',
                    featureCollection: {
                        "type": "FeatureCollection",
                        "features": features
                    }
                }]
            });
        }    
    }

});
