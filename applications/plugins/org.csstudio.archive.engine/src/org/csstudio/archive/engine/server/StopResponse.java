package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.EngineModel;

/** Provide web page for engine shutdown request.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class StopResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    StopResponse(final EngineModel model)
    {
        super(model);
    }
    
    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Shutdown");

        html.text("Engine will shut down....");
        model.requestStop();
        
        html.close();
    }
}
