#!/bin/bash
ncwa -y min -O -v latitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v latitude out.nc`
ncatted -a geospatial_lat_min,global,o,f,$var $1


ncwa -y max  -O  -v latitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v latitude out.nc`
ncatted -a geospatial_lat_max,global,o,f,$var $1

ncwa -y min  -O  -v longitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v longitude out.nc`
ncatted -a geospatial_lon_min,global,o,f,$var $1

ncwa -y max  -O -v longitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v longitude out.nc`
ncatted -a geospatial_lon_max,global,o,f,$var $1

ncwa -y min  -O -v date $1 out.nc
var=`ncks -C -H -s "%i\n" -v date out.nc`
var=`gawk -v var=${var} 'BEGIN{print strftime("%Y-%m-%dT%H:%M:%S%z",var)}'`
ncatted -a time_coverage_start,global,o,c,$var $1

ncwa -y max  -O -v date $1 out.nc
var=`ncks -C -H -s "%i\n" -v date out.nc`
var=`gawk -v var=${var} 'BEGIN{print strftime("%Y-%m-%dT%H:%M:%S%z",var)}'`
ncatted -a time_coverage_end,global,o,c,$var $1

