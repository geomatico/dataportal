Ext.define('timeseries.Chart', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.timeseries',
    
    // Default config (chart)
    layout: 'fit',
    animate: false, //true,
    shadow: false, //true,
    theme: 'Base',

    // Default config (timeseries)
    varName: 'Measure',
    varUnits: '',
    data: [],
    dateFormat: 'Y M d',
    step: [Ext.Date.MONTH, 1],
    
    initComponent: function() {
                
        Ext.define('timeSeriesModel', {
            extend: 'Ext.data.Model',
            fields: [
                {name: "time", type: "Date", dateFormat: "timestamp"},
                "value"
            ]
        });
        
        this.store = Ext.create('Ext.data.Store', {
            model: 'timeSeriesModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        });
        
        this.store.loadData(this.data);

        // TODO: Calculate dateFormat
        this.dateFormat = 'Y M d';
        
        // TODO: Calculate step
        this.step = [Ext.Date.MONTH, 1]; 
        
        // TODO: See how to subsample data
        
        var axisTitle = this.varName;
        if(this.varUnits && this.varUnits.length > 0) {
            axisTitle += " (" + this.varUnits + ")";
        }
        
        this.axes = [{
            type: 'Numeric',
            position: 'left',
            fields: ['value'],
            grid: true,
            title: axisTitle
        }, {
            type: 'Time',
            position: 'bottom',
            fields: ['time'],
            title: 'Time',
            constrain: false,
            grid: true,
            dateFormat: this.dateFormat,
            step: this.step
        }];
            
        this.series = [{
            type: 'line',
            axis: ['bottom', 'left'],
            smooth: false,
            xField: 'time',
            yField: 'value',
            showMarkers: true,
            style: {
                stroke: '#5555FF',
                'stroke-width': 1
            },
            markerConfig: {
                radius: 0.5,
                fill: '#5555FF',
                stroke: '#5555FF',
                'stroke-width': 0.5
            },
            highlight: true,
            getItemForPoint: function(x, y) {
                var items = this.items;
                if (!items || !items.length || !Ext.draw.Draw.withinBox(x, y, this.bbox)) {
                    return null;
                }
                var nearestItem = null;
                var smallestDiff = Number.MAX_VALUE;
                
                //Do binary search to find item with the nearest x point..
                var lowIndex = 0, highIndex = items.length - 1, currentIndex;
                while (lowIndex <= highIndex) {
                    currentIndex = Math.floor((lowIndex + highIndex) / 2);
                    var item = items[currentIndex];
                    
                    var diff = item.point[0] - x;
                    var absDiff = Math.abs(diff);
                    if(absDiff < smallestDiff){
                        nearestItem = item;
                        smallestDiff = absDiff;
                    }
                    //update bounds of search..
                    if(diff < 0) lowIndex = currentIndex+1;
                    else if(diff > 0) highIndex = currentIndex-1;
                    else break; //equal case..
                }
                return nearestItem;
            },
            highlightItem: function(item) {
                if(!item)
                    return;
                item.sprite.setAttributes({
                    radius: 4
                }, true);
                item.sprite._highlighted = true;
            },
            unHighlightItem: function(){
                var items = this.items;
                if(!items)
                    return;
                for(var i = 0, len = items.length; i < len; i++){
                    var sprite = items[i].sprite;
                    if(sprite && sprite._highlighted){
                        sprite.setAttributes({
                                radius: 0
                        }, true);
                        delete sprite._highlighted;
                    }
                }
            },
            tips: {
                trackMouse: true,
                width: 120,
                renderer: function(storeItem, item) {
                    this.setTitle(
                        "Date: " + Ext.Date.format(storeItem.get('time'),'j M Y') + '<br/>' +
                        "Time: " + Ext.Date.format(storeItem.get('time'),'H:m:s') + '<br/>' +
                        "Value: " + storeItem.get('value'));
                }
            },
            shrink: this.shrink
        }, {
            //fake series which just shows the fill..
            type: 'line',
            axis: ['left', 'bottom'],
            fill: true,
            xField: 'time',
            yField: 'value',
            showMarkers: false,
            style: {
                stroke: '#3333FF', //included due to chome bug..
                'stroke-width': 0,
                fill:  '#3333FF',
                opacity: 0.2 //v. annoyingly the opacity is applied to the line and the fill!
            },
            shrink: this.shrink
        }];
        
        this.callParent(arguments);
    },

    shrink: function (xValues, yValues, size) {
        // Start at the 2nd point...
        var len = xValues.length,
            ratio = Math.floor(len / size),
            i = 1,
            xSum = 0,
            ySum = 0,
            xRes = [xValues[0]],
            yRes = [yValues[0]];

        for (; i < len; ++i) {
            xSum += +xValues[i] || 0;
            ySum += +yValues[i] || 0;
            if (i % ratio == 0) {
                xRes.push(xSum/ratio);
                yRes.push(ySum/ratio);
                xSum = 0;
                ySum = 0;
            }
        }
        return {
            x: xRes,
            y: yRes
        };
    }
});
