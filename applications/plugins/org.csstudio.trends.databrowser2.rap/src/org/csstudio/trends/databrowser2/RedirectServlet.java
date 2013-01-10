package org.csstudio.trends.databrowser2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Redirect root directory to servlet "DataBrowser". This can help to access
 * DataBrowser without specifying the servlet name "DataBrowser".
 * 
 * @author Davy Dequidt
 * 
 */
public class RedirectServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7368589913921209513L;

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
		if (request.getPathInfo().equals("/")) {
			response.sendRedirect(response.encodeRedirectURL("d")); //$NON-NLS-1$
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}