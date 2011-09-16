/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Servlet to comunicate with Client to download process
 * 
 * @author Micho Garcia
 *
 */
public class DownloadServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(DownloadServlet.class);
	
	private static final String CONTENTDISPOSITION = "Content-disposition";
	private static final String SEPARATOR = "/";
	private static final String TYPEXML = "application/xml";
	private static final String TYPETAR = "application/x-tar";
	private static final String UTF8 = "UTF-8";

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
		String filePath2Download = controller.askgn2download(isRequestXML);
		
		if (filePath2Download.equals("")) {
			resp.setContentType(TYPEXML);
			resp.setCharacterEncoding(UTF8);
			PrintWriter writer2Client = resp.getWriter();
			writer2Client.print(filePath2Download);
			writer2Client.flush();
			writer2Client.close();
		} else {
			String filename = StringUtils.substringAfterLast(filePath2Download, SEPARATOR);		
			logger.debug("FILE to download: " + filename);
			
			InputStream isFile = controller.downloadFile(filePath2Download);
			resp.setContentType(TYPETAR);
			resp.setHeader(CONTENTDISPOSITION, "attachment; filename=" + filename);
			OutputStream osResponse = resp.getOutputStream();
			IOUtils.copyLarge(isFile, osResponse);
			osResponse.flush();
			osResponse.close();
			isFile.close();
		}
	}

}
