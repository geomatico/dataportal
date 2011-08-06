/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.io.PrintWriter;
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
		
		// TODO molaba sacar esto de aquí
		GnThController controller = new GnThController();
		String response = controller.ask2gn(parametros);
		
		res.setContentType("application/xml");
		res.setCharacterEncoding("UTF-8");
		PrintWriter writer2Client = res.getWriter();
		writer2Client.print(response);
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
