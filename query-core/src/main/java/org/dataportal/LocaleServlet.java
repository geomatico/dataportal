package org.dataportal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LocaleServlet extends HttpServlet implements DataportalCodes {

    private static final long serialVersionUID = 1L;
    private List<String> availableLanguages = null;
    
    public LocaleServlet() {
        super();
        this.availableLanguages = new ArrayList<String>(3);
        this.availableLanguages.add("en");
        this.availableLanguages.add("es");
        this.availableLanguages.add("ca");
        //this.availableLanguages.add("gl");
        //this.availableLanguages.add("fr");
    }
        
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> params = req.getParameterMap();

		HttpSession session = req.getSession(true);
        session.setAttribute(LANG, "en"); // Defaults to English
		
		if (params.containsKey("lang")) {
		    String lang = params.get("lang")[0];
		    if (this.availableLanguages.contains(lang)) {
		        session.setAttribute(LANG, lang);
		        resp.getWriter().print("OK, lang set to '"+lang+"'");
		    } else {
		        resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
		            "Requested lang is not available. Lang set to default (English).");
		    }
		} else {
		    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
		        "Must provide a 'lang' parameter. Available langs are: "
		        + this.availableLanguages.toString());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	    doGet(req, resp);
	}
}
