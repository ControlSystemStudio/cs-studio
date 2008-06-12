package org.csstudio.platform.httpd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Test servlet that counts invocation and displays received parameters.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ServerTest extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    private int visitor = 1;

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException
    {
        resp.setContentType("text/html");
        final PrintWriter out = resp.getWriter();
        out.print("<html>\n");
        out.print("<body>\n");
        out.print("<h1>ServerInfo Call # " + visitor + "</h1>");
        ++visitor;
        
        final Enumeration<String> parms = req.getParameterNames();
        while (parms.hasMoreElements())
        {
            final String parm = parms.nextElement();
            final String value = req.getParameter(parm);
            out.println(String.format("%s = %s<p>\n", parm, value));
        }
        
        out.print("</body>\n");
        out.print("</html>\n");
        out.close();
    }
}
