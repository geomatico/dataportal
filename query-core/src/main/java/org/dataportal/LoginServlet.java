package org.dataportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dataportal.controllers.JPAUserController;
import org.dataportal.datasources.Mail;
import org.dataportal.model.User;
import org.dataportal.Config;

public class LoginServlet extends HttpServlet implements DataportalCodes {
	private static final long serialVersionUID = 1L;

	private static final String ACCESS = "access";
	private static final String CHANGE_PASS = "changePass";
	private static final String GENERATE_PASS = "generatePass";
	private static final String REGISTER = "register";
	private static final Object LOGOUT = "logout";

	private static final String CONTACT_MAIL = Config.get("mail.address");
	private JPAUserController usercontroller = new JPAUserController();

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("hash") != null
				&& (req.getParameter("request").equals(REGISTER) || req
						.getParameter("request").equals(GENERATE_PASS))) {
			doPost(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid request.");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/json");
		PrintWriter out = resp.getWriter();

		try {
			String request = req.getParameter("request");
			String username = req.getParameter("user");

			if (request == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Invalid request.");
				return;
			}

			if (request.equals(ACCESS)) {
				String password = req.getParameter("password");
				User user = new User(username, password);
				if (usercontroller.isActive(user)) {
					out.print("{success:true,message:\"" + username + "\"}");
					HttpSession session = req.getSession(true);
					session.setAttribute(USERACCESS, user);
				} else {
					out.print("{success:false,message:\"Access denied.\"}");
				}

			} else if (request.equals(CHANGE_PASS)) {
				String password = req.getParameter("password");
				String newPassword = req.getParameter("newPassword");
				User user = new User(username, password);

				if (usercontroller.changePass(user, newPassword)) {
					out.print("{success:true,message:\"Password changed. Use new password to Login.\"}");
				} else {
					out.print("{success:false,message:\"Access denied.\"}");
				}

			} else if (request.equals(GENERATE_PASS)) {
				if (req.getParameter("hash") == null) {
					User user = new User(username);
					String hash = usercontroller.setHash(user, JPAUserController.ACTIVE);
					if (hash == null) {
						out.print("{success:false,message:\"No user registered with this mail address.\"}");
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put(
								"link",
								req.getRequestURL()
										.append("?request=" + GENERATE_PASS
												+ "&hash=" + hash).toString());
						params.put("contact", CONTACT_MAIL);
						Mail.send(
								username,
								"[ICOS Data Portal] Password change confirmation",
								"newPassAskConfirmation", params);

						out.print("{success:true,message:\"We have sent you instructions to proceed with password change. Check your inbox.\"}");
					}
				} else {
					String hash = req.getParameter("hash");
					User user = new User(username);
					String newPass = usercontroller.newPass(user, hash);
					if (hash == null) {
						resp.sendError(HttpServletResponse.SC_FORBIDDEN,
								"This link is no longer valid.");
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put("pass", newPass);
						params.put("contact", CONTACT_MAIL);
						Mail.send(user.getId(),
								"[ICOS Data Portal] Your new password",
								"newPassCreated", params);

						resp.sendRedirect("./PasswordChanged.html");
					}
				}

			} else if (request.equals(REGISTER)) {
				if (req.getParameter("hash") == null) {
					String password = req.getParameter("password");
					User user = new User(username, password);
					if (usercontroller.exists(user)) {
						out.print("{success:false,message:\"The user already exists.\"}");
					} else {
						String hash = usercontroller.save(user);
						if (hash == null) {
							out.print("{success:false,message:\"User could not be created.\"}");
						} else {
							Map<String, String> params = new HashMap<String, String>();
							params.put(
									"link",
									req.getRequestURL()
											.append("?request=" + REGISTER
													+ "&hash=" + hash)
											.toString());
							params.put("contact", CONTACT_MAIL);
							Mail.send(username,
									"[ICOS Data Portal] New user confirmation",
									"newUserAskConfirmation", params);

							out.print("{success:true,message:\"We've sent you an email with further instructions to complete the registration.\"}");
						}
					}
				} else {
					String hash = req.getParameter("hash");
					username = usercontroller.activate(hash);
					if (username == null) {
						resp.sendError(HttpServletResponse.SC_FORBIDDEN,
								"This link is no longer valid.");
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put("contact", CONTACT_MAIL);
						Mail.send(
								username,
								"[ICOS Data Portal] Welcome to ICOS Data Portal",
								"newUserCreated", params);

						resp.sendRedirect("./NewUser.html");
					}
				}

			} else if (request.equals(LOGOUT)) {
				HttpSession session = req.getSession();
				session.setAttribute(USERACCESS, null);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Invalid request.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}
}
