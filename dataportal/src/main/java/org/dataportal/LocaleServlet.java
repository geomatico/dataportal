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
        this.availableLanguages.add("en"); //$NON-NLS-1$
        this.availableLanguages.add("es"); //$NON-NLS-1$
        this.availableLanguages.add("ca"); //$NON-NLS-1$
        //this.availableLanguages.add("gl");
        //this.availableLanguages.add("fr");
    }
        
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> params = req.getParameterMap();

		HttpSession session = req.getSession(true);
        session.setAttribute(LANG, "en"); // Defaults to English //$NON-NLS-1$
		
		if (params.containsKey("lang")) { //$NON-NLS-1$
		    String lang = params.get("lang")[0]; //$NON-NLS-1$
		    Messages.setLang(lang);
		    if (this.availableLanguages.contains(lang)) {
		        session.setAttribute(LANG, lang);
		        resp.getWriter().print(Messages.getString("localeservlet.lang_set_to")+lang+"'"); //$NON-NLS-1$ //$NON-NLS-2$
		    } else {
		        resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
		            Messages.getString("localeservlet.lang_not_available")); //$NON-NLS-1$
		    }
		} else {
		    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
		        Messages.getString("localeservlet.not_lang_parameter") //$NON-NLS-1$
		        + this.availableLanguages.toString());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	    doGet(req, resp);
	}
}
