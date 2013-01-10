package org.csstudio.webopi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Redirect root directory to servlet "WebOPI". This can help to access
 * WebOPI without specifying the servlet name "WebOPI".
 * @author Xihui Chen
 *
 */
public class RedirectServlet extends HttpServlet { 
  
private static final long serialVersionUID = -3424333798547040185L;

protected void doGet( HttpServletRequest request,
                        HttpServletResponse response )
    throws ServletException, IOException
  {
    redirect( request, response );
  }
 
  protected void doPost( HttpServletRequest request,
                         HttpServletResponse response )
    throws ServletException, IOException
  {
    redirect( request, response );
  }
 
  static void redirect( HttpServletRequest request, 
                        HttpServletResponse response )
    throws IOException
  {
    if( request.getPathInfo().equals( "/" ) ) {
      response.sendRedirect( response.encodeRedirectURL( "w" ) ); //$NON-NLS-1$
    } else {
      response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }
  }
}