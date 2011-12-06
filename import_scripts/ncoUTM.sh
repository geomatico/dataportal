#!/bin/bash

# Uso: ncoUTM.sh 'fichero.nc'

# Dependencias:
#  apt-get install uuid
#  apt-get install nco
#  apt-get install gawk
#  apt-get install netcdf-bin


##############################
# ATRIBUTOS GLOBALES MINIMOS #
##############################

# Metadata_Conventions
ncatted -h -a Metadata_Conventions,global,o,c,"Unidata Dataset Discovery v1.0" $1

# id
var=`uuid`
ncatted -h -a id,global,o,c,$var $1

# naming_authority
ncatted -h -a naming_authority,global,o,c,"UUID" $1

# title
# summary
# keywords

# standard_name_vocabulary
ncatted -h -a standard_name_vocabulary,global,o,c,"http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml" $1

# license
ncatted -h -a license,global,o,c,"http://opendatacommons.org/licenses/odbl/1-0/" $1

# geospatial_lat_min
ncwa -y min -O -v latitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v latitude out.nc`
ncatted -h -a geospatial_lat_min,global,o,f,$var $1

# geospatial_lat_max
ncwa -y max  -O  -v latitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v latitude out.nc`
ncatted -h -a geospatial_lat_max,global,o,f,$var $1

# geospatial_lon_min
ncwa -y min  -O  -v longitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v longitude out.nc`
ncatted -h -a geospatial_lon_min,global,o,f,$var $1

# geospatial_lon_max
ncwa -y max  -O -v longitude $1 out.nc
var=`ncks -C -H -s "%f\n" -v longitude out.nc`
ncatted -h -a geospatial_lon_max,global,o,f,$var $1

# time_coverage_start
ncwa -y min  -O -v date $1 out.nc
var=`ncks -C -H -s "%i\n" -v date out.nc`
var=`gawk -v var=${var} 'BEGIN{print strftime("%Y-%m-%dT%H:%M:%S%z",var)}'`
ncatted -h -a time_coverage_start,global,o,c,$var $1

# time_coverage_end
ncwa -y max  -O -v date $1 out.nc
var=`ncks -C -H -s "%i\n" -v date out.nc`
var=`gawk -v var=${var} 'BEGIN{print strftime("%Y-%m-%dT%H:%M:%S%z",var)}'`
ncatted -h -a time_coverage_end,global,o,c,$var $1

# institution
ncatted -h -a institution,global,o,c,"Unidad de Tecnología Marina (UTM), CSIC" $1

# creator_url
ncatted -h -a creator_url,global,o,c,"http://www.utm.csic.es/" $1

# cdm_data_type
ncatted -h -a cdm_data_type,global,o,c,"Trajectory" $1

# icos_domain
ncatted -h -a icos_domain,global,o,c,"oceans" $1
