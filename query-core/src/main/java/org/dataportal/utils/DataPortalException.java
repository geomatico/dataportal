/**
 * 
 */
package org.dataportal.utils;

/**
 * @author Micho Garcia
 *
 */
public class DataPortalException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;

	public DataPortalException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataPortalException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DataPortalException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DataPortalException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
}
