package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

@SuppressWarnings({"rawtypes","unchecked"})
public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	    // Retrieve parameters
        Map<String, String[]> params = req.getParameterMap();
        String request = (params.containsKey("request") ? params.get("request")[0] : null);
        int year = (params.containsKey("year") ? Integer.parseInt(params.get("year")[0]) : 0);
        int month = (params.containsKey("month") ? Integer.parseInt(params.get("month")[0]) : 0);
        
        // Parameter validation
        if (request != null && year > 0 && month >= 0 && month <= 12) {
            // Get data
            ReportController stats = new ReportController();
            List data = stats.get(request, year, month);

            // Serialize response as JSON
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            JSONArray.fromObject(data).write(out);
            out.close();
        } else {
            PrintWriter out = resp.getWriter();
            out.println("Parameter validation error: expected 'request', 'year' and, optionally, 'month'.");
        }

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
