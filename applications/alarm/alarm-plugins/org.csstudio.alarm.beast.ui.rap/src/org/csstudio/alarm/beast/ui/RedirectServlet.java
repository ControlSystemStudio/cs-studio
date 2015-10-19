package org.csstudio.alarm.beast.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectServlet extends HttpServlet {
    private static final long serialVersionUID = 4810837816002542963L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        redirect(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        redirect(request, response);
    }

    static void redirect(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            response.sendRedirect(response
                    .encodeRedirectURL(WebAlarmConstants.MAIN_SERVLET_NAME));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}