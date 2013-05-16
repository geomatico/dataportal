/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Micho Garcia
 *
 */
public class ConverterServlet extends HttpServlet implements DataportalCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		resp.setContentType("text/json"); //$NON-NLS-1$
		PrintWriter out = resp.getWriter();
	}

	
}
