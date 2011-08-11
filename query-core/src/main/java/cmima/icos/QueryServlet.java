/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;


/**
 * @author Micho Garcia
 *
 */
public class QueryServlet extends HttpServlet {

	
	private static Logger logger = Logger.getLogger(QueryServlet.class);
	
	/**
	 * Receive the request from client with parameters
	 * 
	 * 
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> parametros = req.getParameterMap();
		
		QueryController controller = new QueryController();
		String response = controller.ask2gn(parametros);
		
		String contentType = req.getParameter("response_format");
		res.setContentType(contentType);
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
