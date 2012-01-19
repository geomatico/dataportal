Ext.define('opendap.Panel', {
    extend: 'Ext.Panel',
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
       
        var timeVar = this.dataset.variables["time"];
        
        if (timeVar) {
            Ext.Object.each(this.dataset.variables, function(varName, variable) {
                if (Ext.Array.contains(variable.dimensions, "time") && varName != "time") {
                    var att = variable.attributes;
                    var data = variable.data;
                    
                    var name = (att.long_name && Ext.String.capitalize(att.long_name)) || att.standard_name || varName;
                    var units = att.units || "";
                    var fillValue = att._FillValue || null;
                    
                    var series = [];
                    for(i=0; i<data.length; i++) {
                        var time = timeVar.data[i];
                        var value = data[i][0];  // TODO: arbitrary # dimensions
                        if (value != fillValue) {
                            series.push({ time: time, value: value });
                        }
                    }
                    
                    this.add({
                        title: name + " vs. Time",
                        type: 'panel',
                        layout: 'fit',
                        items: [{
                            xtype: 'timeseries',
                            varName: name,
                            varUnits: units,
                            data: series,
                            width: timeVar.size*2
                        }]
                    });
                    
                }
            }, this);
        }
        
    }

});
