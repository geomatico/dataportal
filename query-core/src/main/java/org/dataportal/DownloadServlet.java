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

import org.apache.log4j.Logger;

/**
 * Servlet to comunicate with Client to download process
 * 
 * @author Micho Garcia
 *
 */
public class DownloadServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(DownloadServlet.class);

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		InputStream isRequestXML = req.getInputStream();	
		
		QueryController controller = new QueryController();
		String response = controller.askgn2download(isRequestXML);
		
		resp.setContentType("application/xml");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer2Client = resp.getWriter();
		writer2Client.print(response);
		writer2Client.flush();
		writer2Client.close();
	}

}
