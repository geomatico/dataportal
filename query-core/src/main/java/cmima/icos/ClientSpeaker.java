/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;


/**
 * @author Micho Garcia
 *
 */
public class ClientSpeaker extends HttpServlet {

	
	private static Logger logger = Logger.getLogger(ClientSpeaker.class);
	
	/**
	 * Receive the request from client with parameters
	 * 
	 * 
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		Enumeration paramNames = req.getParameterNames();
		HashMap<String, String[]> parametros = new HashMap<String, String[]>();
		while(paramNames.hasMoreElements()){
			String paramName = (String)paramNames.nextElement();

			logger.debug(paramName + " ; " +req.getParameterValues(paramName)[0]);
			parametros.put(paramName, req.getParameterValues(paramName));
		}
		
		GnThController controller = new GnThController();
		controller.ask2gn(parametros);
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req,res);
	}	
}
