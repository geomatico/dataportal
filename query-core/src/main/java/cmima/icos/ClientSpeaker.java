/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * @author Micho Garcia
 *
 */
public class ClientSpeaker extends HttpServlet {

	/**
	 * 
	 */

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		Enumeration paramNames = req.getParameterNames();
		while(paramNames.hasMoreElements()){
			String paramName = (String)paramNames.nextElement();
			HashMap<String, String[]> parametros = new HashMap<String, String[]>();
			parametros.put(paramName, req.getParameterValues(paramName));
		}
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req,res);
	}	
}
