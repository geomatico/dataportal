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
import org.dataportal.users.User;

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

	    if (!this.authenticate(req, resp)) return;

		@SuppressWarnings("unchecked")
		Map<String, String[]> params = req.getParameterMap();
		
		if (params.containsKey("file")) {
            
            String userName = StringUtils.substringBeforeLast(params.get("user")[0], "@");
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

	    if (!this.authenticate(req, resp)) return;

        resp.setContentType(TYPEXML);
        resp.setCharacterEncoding(UTF8);
        PrintWriter writer2Client = resp.getWriter();
	    InputStream isRequestXML = req.getInputStream();    
		
		try {
	        String userName = StringUtils.substringBeforeLast(req.getParameter("user"), "@");
	        logger.debug("UserName to download: " + userName);

		    String fileName = download.askgn2download(isRequestXML, userName);
	        logger.debug("FILE to download: " + fileName);
	        writer2Client.println("<download>");
	        writer2Client.println("  <filename>"+fileName+"</filename>");
	        writer2Client.println("</download>");
	        writer2Client.flush();
	        writer2Client.close();
		} catch (Exception e) {
	        DataPortalError error = new DataPortalError();
	        error.setCode("error.ejecucion.servidor");
	        error.setMessage("Server error: " + e.getMessage());
	        writer2Client.print(error.getErrorMessage());
		}
	}

	protected boolean authenticate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login = req.getParameter("user");
        String password = req.getParameter("password");
        boolean userIsActive = false;
	    try {
            userIsActive = new User(login, password).isActive();
            if (!userIsActive) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied.");
            }
        } catch (Exception e) {
            DataPortalError error = new DataPortalError();
            error.setCode("error.login");
            error.setMessage("Error authenticating user");
            resp.getWriter().print(error.getErrorMessage());
        }
        return userIsActive;
	}
}
