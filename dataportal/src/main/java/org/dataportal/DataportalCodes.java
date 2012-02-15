/**
 * 
 */
package org.dataportal;

/**
 * @author Micho Garcia
 *
 */
public interface DataportalCodes {
	
	// error messages
	/**
	 * 
	 */
	static final String IDNOTFOUND = "error.id.not.found";
	
	/**
	 * 
	 */
	static final String USERNOTFOUND = "error.user.not.found";
	
	/**
	 * 
	 */
	static final String RUNTIMESERVERERROR = "error.runtime.server";
	
	/**
	 * 
	 */
	static final String RUNTIMEERROR = "error.runtime";
	
	/*
	 * 
	 */
	static final String RDBMSERROR = "error.data.server";
	
	/**
	 * 
	 */
	static final String FAILECREATEDIRECTORY = "error.failed.create.directory";
	
	/**
	 * 
	 */
	static final String DDINOTFOUND = "error.ddi.not.found";

	/**
	 * 
	 */
	static final String INVALIDREQUEST = "error.invalid.request";
	
	/**
	 * 
	 */
	static final String ERRORLOGIN = "error.login";
	
	// dataportal constants
	/**
	 * 
	 */
	static final String USERACCESS = "org.dataportal.LoginServlet.user";

	/**
	 * 
	 */
	static final String LANG = "org.dataportal.LocaleServlet.lang";
}
