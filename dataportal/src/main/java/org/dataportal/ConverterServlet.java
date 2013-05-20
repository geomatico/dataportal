/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import co.geomati.netcdf.Converter;

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
		
		String converters = req.getParameter("converters"); 
		
		if (converters != null) {
			resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
			resp.setContentType("text/xml"); //$NON-NLS-1$
			
			InputStream StreamConvertersXML = Converter.class.getClassLoader().getResourceAsStream("co/geomati/netcdf/converters.xml");
			String convertersXML = IOUtils.toString(StreamConvertersXML);
			PrintWriter out = resp.getWriter();
			out.print(convertersXML);
			StreamConvertersXML.close();
			out.close();
		} else {
			doPost(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		resp.setContentType("text/json"); //$NON-NLS-1$
		PrintWriter out = resp.getWriter();
	}	
}
