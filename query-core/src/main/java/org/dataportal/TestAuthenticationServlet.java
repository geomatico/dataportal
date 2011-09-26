package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestAuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TestAuthenticationServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String userName = request.getRemoteUser();
	    String isIcosUser = request.isUserInRole("icosUser") ? "Yes" : "Nope";

	    PrintWriter out = response.getWriter();
	    out.println("Hi, " + userName);
	    out.println("Are you an icosUser? " + isIcosUser);
	}
}
