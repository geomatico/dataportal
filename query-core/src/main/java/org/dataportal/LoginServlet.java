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
import org.dataportal.model.User;

public class LoginServlet extends HttpServlet implements DataportalCodes {
	private static final long serialVersionUID = 1L;

	private static final String ACCESS = "access"; //$NON-NLS-1$
	private static final String CHANGE_PASS = "changePass"; //$NON-NLS-1$
	private static final String GENERATE_PASS = "generatePass"; //$NON-NLS-1$
	private static final String REGISTER = "register"; //$NON-NLS-1$
	private static final Object LOGOUT = "logout"; //$NON-NLS-1$

	private static final String CONTACT_MAIL = Config.get("mail.address"); //$NON-NLS-1$
	private JPAUserController usercontroller = new JPAUserController();

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		String lang = (String) session.getAttribute(LANG);
		Messages.setLang(lang);
		
		if (req.getParameter("hash") != null //$NON-NLS-1$
				&& (req.getParameter("request").equals(REGISTER) || req //$NON-NLS-1$
						.getParameter("request").equals(GENERATE_PASS))) { //$NON-NLS-1$
			doPost(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					Messages.getString("loginservlet.invalid_request")); //$NON-NLS-1$
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		String lang = (String) session.getAttribute(LANG);
		Messages.setLang(lang);
		
		resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		resp.setContentType("text/json"); //$NON-NLS-1$
		PrintWriter out = resp.getWriter();

		try {
			String request = req.getParameter("request"); //$NON-NLS-1$
			String username = req.getParameter("user"); //$NON-NLS-1$

			if (request == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						Messages.getString("loginservlet.invalid_request")); //$NON-NLS-1$
				return;
			}

			if (request.equals(ACCESS)) {
				String password = req.getParameter("password"); //$NON-NLS-1$
				User user = new User(username, password);
				if (usercontroller.isActive(user)) {
					out.print("{success:true,message:\"" + username + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$
					session.setAttribute(USERACCESS, user);
				} else {
					out.print("{success:false,message:\"" + Messages.getString("loginservlet.access_denied") + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

			} else if (request.equals(CHANGE_PASS)) {
				String password = req.getParameter("password"); //$NON-NLS-1$
				String newPassword = req.getParameter("newPassword"); //$NON-NLS-1$
				User user = new User(username, password);

				if (usercontroller.changePass(user, newPassword)) {
					out.print("{success:true,message:\"" + Messages.getString("loginservlet.pass_changed") + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					out.print("{success:false,message:\"" + Messages.getString("loginservlet.access_denied") + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

			} else if (request.equals(GENERATE_PASS)) {
				if (req.getParameter("hash") == null) { //$NON-NLS-1$
					User user = new User(username);
					String hash = usercontroller.setHash(user, JPAUserController.ACTIVE);
					if (hash == null) {
						out.print("{success:false,message:\"" + Messages.getString("loginservlet.not_user_register") + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put(
								"link", //$NON-NLS-1$
								req.getRequestURL()
										.append("?request=" + GENERATE_PASS //$NON-NLS-1$
												+ "&hash=" + hash).toString()); //$NON-NLS-1$
						params.put("contact", CONTACT_MAIL); //$NON-NLS-1$
						SystemSingleton
								.getMail()
								.send(
								username,
								Messages.getString("loginservlet.pass_change_confirm"), //$NON-NLS-1$
								"newPassAskConfirmation", params); //$NON-NLS-1$

						out.print("{success:true,message:\"" + Messages.getString("loginservlet.send_instructions") + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				} else {
					String hash = req.getParameter("hash"); //$NON-NLS-1$
					User user = new User(username);
					String newPass = usercontroller.newPass(user, hash);
					if (hash == null) {
						resp.sendError(HttpServletResponse.SC_FORBIDDEN,
								Messages.getString("loginservlet.link_not_valid")); //$NON-NLS-1$
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put("pass", newPass); //$NON-NLS-1$
						params.put("contact", CONTACT_MAIL); //$NON-NLS-1$
						SystemSingleton
								.getMail()
								.send(user.getId(),
								Messages.getString("loginservlet.your_new_pass"), //$NON-NLS-1$
								"newPassCreated", params); //$NON-NLS-1$

						resp.sendRedirect("./PasswordChanged.html"); //$NON-NLS-1$
					}
				}

			} else if (request.equals(REGISTER)) {
				if (req.getParameter("hash") == null) { //$NON-NLS-1$
					String password = req.getParameter("password"); //$NON-NLS-1$
					User user = new User(username, password);
					if (usercontroller.exists(user)) {
						out.print("{success:false,message:\"" + Messages.getString("loginservlet.user_alredy_exits") + "\"}");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						String hash = usercontroller.save(user);
						if (hash == null) {
							out.print("{success:false,message:\"" + Messages.getString("loginservlet.user_not_created") + "\"}");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						} else {
							Map<String, String> params = new HashMap<String, String>();
							params.put(
									"link", //$NON-NLS-1$
									req.getRequestURL()
											.append("?request=" + REGISTER //$NON-NLS-1$
													+ "&hash=" + hash) //$NON-NLS-1$
											.toString());
							params.put("contact", CONTACT_MAIL); //$NON-NLS-1$
							SystemSingleton
									.getMail()
									.send(username,
									Messages.getString("loginservlet.new_user_confirm"), //$NON-NLS-1$
									"newUserAskConfirmation", params); //$NON-NLS-1$

							out.print("{success:true,message:\"" + Messages.getString("loginservlet.email_sent") + "\"}");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				} else {
					String hash = req.getParameter("hash"); //$NON-NLS-1$
					username = usercontroller.activate(hash);
					if (username == null) {
						resp.sendError(HttpServletResponse.SC_FORBIDDEN,
								Messages.getString("loginservlet.link_not_valid")); //$NON-NLS-1$
					} else {
						Map<String, String> params = new HashMap<String, String>();
						params.put("contact", CONTACT_MAIL); //$NON-NLS-1$
						SystemSingleton
								.getMail()
								.send(
								username,
								Messages.getString("loginservlet.welcome_to_icos"), //$NON-NLS-1$
								"newUserCreated", params); //$NON-NLS-1$

						resp.sendRedirect("./NewUser.html"); //$NON-NLS-1$
					}
				}

			} else if (request.equals(LOGOUT)) {
				session.setAttribute(USERACCESS, null);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						Messages.getString("loginservlet.invalid_request")); //$NON-NLS-1$
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}
}
