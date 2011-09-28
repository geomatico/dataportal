/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletConfig;
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
	
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(DownloadServlet.class);
	
	private static final String CONTENTDISPOSITION = "Content-disposition";
	private static final String TYPEXML = "application/xml";
	private static final String TYPEZIP = "application/zip";
	private static final String UTF8 = "UTF-8";
	
	private DownloadController download;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.debug("INIT DownloadServlet");
		
		super.init(config);
		download = new DownloadController();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> params = req.getParameterMap();
		
		if (params.containsKey("file")) {
            
            String userName = StringUtils.substringBeforeLast(req.getRemoteUser(), "@");
            String fileName = params.get("file")[0];
            int fileSize = (int)download.getFileSize(fileName, userName);

            InputStream contents = download.getFileContents(fileName, userName);
            OutputStream out = resp.getOutputStream();

		    resp.setContentType(TYPEZIP);
            resp.setHeader(CONTENTDISPOSITION, "attachment; filename=" + fileName);
            resp.setContentLength(fileSize);
            
            IOUtils.copyLarge(contents, out);
            out.flush();
            out.close();
            contents.close();
	    }
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		InputStream isRequestXML = req.getInputStream();	
		
		String userName = StringUtils.substringBeforeLast(req.getRemoteUser(), "@");
		
		logger.debug("UserName to download: " + userName);
		
		String fileName = download.askgn2download(isRequestXML, userName);
   
        logger.debug("FILE to download: " + fileName);
		resp.setContentType(TYPEXML);
		resp.setCharacterEncoding(UTF8);
		PrintWriter writer2Client = resp.getWriter();
		writer2Client.println("<download>");
		writer2Client.println("  <filename>"+fileName+"</filename>");
		writer2Client.println("</download>");
		writer2Client.flush();
		writer2Client.close();
	}

}
