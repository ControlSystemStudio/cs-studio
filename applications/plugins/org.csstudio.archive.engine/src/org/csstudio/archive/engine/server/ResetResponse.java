package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.EngineModel;

/** Provide web page to reset engine statistics.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ResetResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ResetResponse(final EngineModel model)
    {
        super(model);
    }
    
    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Reset");

        html.text("Engine statistics are reset");
        model.reset();
        
        html.close();
    }
}
