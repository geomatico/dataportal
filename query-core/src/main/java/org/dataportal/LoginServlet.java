package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dataportal.datasources.Mail;
import org.dataportal.users.User;
import org.dataportal.Config;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static final String ACCESS = "access";
    private static final String CHANGE_PASS = "changePass";
    private static final String GENERATE_PASS = "generatePass";
    private static final String REGISTER = "register";
    
    private static final String CONTACT_MAIL = Config.get("mail.address");

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
        /*
        if(req.getParameter("hash") != null &&
            (req.getParameter("request").equals(REGISTER) || req.getParameter("request").equals(GENERATE_PASS)) ) {
                doPost(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request.");
        }
        */
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        PrintWriter out = resp.getWriter();
        
        try {
            String request = req.getParameter("request");
            String user = req.getParameter("user");
            
            if (request.equals(ACCESS)) {
                String password = req.getParameter("password");
                User u = new User(user, password);
                if(u.isActive()) {
                    out.print("{\"OK\":\""+user+"\"}");
                } else {
                    out.print("{\"ERROR\":\"Access denied.\"}");
                }
                
            } else if (request.equals(CHANGE_PASS)) {
                String password = req.getParameter("password");
                String newPassword = req.getParameter("newPassword");
                User u = new User(user, password);

                if(u.changePass(newPassword)) {
                    out.print("{\"OK\":\"Password changed. Use new password to access.\"}");
                } else {
                    out.print("{\"ERROR\":\"Access denied.\"}");
                }
                
            } else if (request.equals(GENERATE_PASS)) {
                if(req.getParameter("hash")==null){
                    String hash = new User().setHash(user, User.ACTIVE);
                    if(hash==null) {
                        out.print("{\"ERROR\":\"Bad mail address.\"}");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("link", req.getRequestURL().append("?request="+GENERATE_PASS+"&hash="+hash).toString());
                        params.put("contact", CONTACT_MAIL);
                        Mail.send(user, "[ICOS Data Portal] Password change confirmation", "newPassAskConfirmation", params);
                        
                        out.print("{\"OK\":\"We have sent you instructions to proceed with password change. Check your inbox.\"}");
                    }
                } else {
                    String hash = req.getParameter("hash");
                    User u = new User();
                    String newPass = u.newPass(hash);
                    if(hash==null) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This link is no longer valid.");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("pass", newPass);
                        params.put("contact", CONTACT_MAIL);
                        Mail.send(u.login(), "[ICOS Data Portal] Your new password", "newPassCreated", params);
                        
                        resp.sendRedirect("./PasswordChanged.html");
                    }
                }
                
            } else if (request.equals(REGISTER)) {
                if(req.getParameter("hash")==null){
                    String password = req.getParameter("password");
                    User u = new User(user, password);
                    if (u.exists()) {
                        out.print("{\"ERROR\":\"The user already exists.\"}");
                    } else {
                        String hash = u.save();
                        if(hash==null) {
                            out.print("{\"ERROR\":\"User could not be created.\"}");
                        } else {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("link", req.getRequestURL().append("?request="+REGISTER+"&hash="+hash).toString());
                            params.put("contact", CONTACT_MAIL);
                            Mail.send(user, "[ICOS Data Portal] New user confirmation", "newUserAskConfirmation", params);
                            
                            out.print("{\"OK\":\"We've sent you an email with further instructions to complete the registration.\"}");
                        }
                    }
                } else {
                    String hash = req.getParameter("hash");
                    user = new User().activate(hash);
                    if(user==null) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This link is no longer valid.");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("contact", CONTACT_MAIL);
                        Mail.send(user, "[ICOS Data Portal] Welcome to ICOS Data Portal", "newUserCreated", params);
                        
                        resp.sendRedirect("./NewUser.html");
                    }
                }               
                
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request.");
            } 
        
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
