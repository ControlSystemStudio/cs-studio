package org.csstudio.trends.databrowser2;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectServletFilter implements javax.servlet.Filter {

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws ServletException, IOException {

		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;

			if ("/".equals(request.getPathInfo())) {
				response.sendRedirect(response.encodeRedirectURL("d"));
			} else {
				chain.doFilter(request, response);
			}
		} catch (ClassCastException e) {
			throw new ServletException("non-HTTP request or response");
		}

	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}