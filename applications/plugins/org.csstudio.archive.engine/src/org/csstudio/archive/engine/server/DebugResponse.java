package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.EngineModel;

/** Provide web page for triggering debug output
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class DebugResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    DebugResponse(final EngineModel model)
    {
        super(model);
    }
    
    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Debug");

        html.text("Engine wrote debug info ....");
        model.dumpDebugInfo();
        
        html.close();
    }
}
