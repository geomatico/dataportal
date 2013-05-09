/**
 * 
 */
package co.geomati.netcdf;

import java.io.IOException;

/**
 * @author michogarcia
 *
 */
public interface IConverter {
	
	void doConversion(String[] files, String path) throws ConverterException, IOException;

}
