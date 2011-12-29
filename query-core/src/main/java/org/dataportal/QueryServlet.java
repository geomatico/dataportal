/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.persistence.RollbackException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.dataportal.model.User;
import org.dataportal.utils.DataPortalException;

/**
 * @author Micho Garcia
 *
 */
public class QueryServlet extends HttpServlet implements DataportalCodes {
	
    private static final long serialVersionUID = 1L;
    
    private DataPortalError error;

    /**
	 * Receive the request from client with parameters
	 * 
	 * 
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute(USERACCESS);
		String lang = (String) session.getAttribute(LANG);
		Messages.setLang(lang);
				
		@SuppressWarnings("unchecked")
		Map<String, String[]> parametros = req.getParameterMap();
		
		QueryController controller = new QueryController(lang);
		if (user != null)
			controller.setUser(user);
		
		StringBuffer response = new StringBuffer();
		
		try {
			response.append(controller.ask2gn(parametros));
		} catch (Exception e) {
			Class<?> clase = e.getClass();
			error = new DataPortalError();
			if (clase.equals(DataPortalException.class)) {
				DataPortalException dtException = (DataPortalException) e;
				error.setCode(dtException.getCode());
			} else if (clase.equals(RollbackException.class)) {
				error.setCode(RDBMSERROR);
			} else {
				error.setCode(RUNTIMEERROR);				
			}
			error.setMessage(e.getMessage());
			response.append(error.getErrorMessage());
		}
		
		String contentType = req.getParameter("response_format");
		res.setContentType(contentType);
		res.setCharacterEncoding("UTF-8");
		PrintWriter writer2Client = res.getWriter();
		writer2Client.print(response.toString());
		writer2Client.flush();
		writer2Client.close();
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req,res);
	}	
}
