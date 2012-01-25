package org.dataportal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            URL url = new URL(req.getParameter("url"));
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                out.append(inputLine);
            in.close();
        } catch (MalformedURLException e) {
            out.println("Malformed URL");
        }
        out.flush();
        out.close();
    }
}
