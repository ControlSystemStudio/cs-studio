package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.EngineModel;

/** Provide web page for engine restart request.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class RestartResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    RestartResponse(final EngineModel model)
    {
        super(model);
    }
    
    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Restart");

        html.text("Engine will restart....");
        model.requestRestart();
        
        html.close();
    }
}
