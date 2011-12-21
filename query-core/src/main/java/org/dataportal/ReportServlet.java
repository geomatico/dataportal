package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dataportal.utils.DataPortalException;
import org.dataportal.utils.ResponseWrapper;

@SuppressWarnings({"rawtypes","unchecked"})
public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    // Prepare response headers & writers
	    PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");	    

	    // Retrieve parameters
        Map<String, String[]> params = req.getParameterMap();
        String request = (params.containsKey("request") ? params.get("request")[0] : null);
        int year = (params.containsKey("year") ? Integer.parseInt(params.get("year")[0]) : 0);
        int month = (params.containsKey("month") ? Integer.parseInt(params.get("month")[0]) : 0);
        
        // Parameter validation
        if (request != null && year > 0 && month >= 0 && month <= 12) {
            try {
                // Get data
                List data = new ReportController().get(request, year, month);
                // Wrap & serialize response as JSON
                out.print(new ResponseWrapper(true, data).asJSON());
            } catch (DataPortalException dpe) {
                out.print(new ResponseWrapper(false, dpe.getMessage()).asJSON());
            }
        } else {
            String message = "Parameter error: expected 'request', 'year' and, optionally, 'month' as parameters.";
            out.print(new ResponseWrapper(false, message).asJSON());
        }

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
