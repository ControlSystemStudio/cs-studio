package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.EngineModel;

public class NoResponse extends AbstractResponse {

    protected NoResponse(EngineModel model) {
        super(model);
    }

    @Override
    public void fillResponse(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.sendError(400, "Page not found");
    }

}
