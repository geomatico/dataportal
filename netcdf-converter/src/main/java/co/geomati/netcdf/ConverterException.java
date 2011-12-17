package co.geomati.netcdf;


public class ConverterException extends Exception {

	public ConverterException(String msg, Throwable e) {
		super(msg, e);
	}

	public ConverterException(String msg) {
		super(msg);
	}

}
