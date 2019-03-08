/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.simplepv.IPV;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This is the implementation of {@link AbstractScriptStore} for Rhino script engine.
 * @author Xihui Chen
 *
 */
public class RhinoScriptStore extends AbstractScriptStore{

    private Context scriptContext;

    private Scriptable scriptScope;



    private Script script;


    public RhinoScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
            final IPV[] pvArray) throws Exception {
        super(scriptData, editpart, pvArray);

    }

    @Override
    protected void initScriptEngine() throws Exception {
        scriptContext = ScriptStoreFactory.getRhinoContext();
        scriptScope = new ImporterTopLevel(scriptContext);
        Object widgetController = Context.javaToJS(getEditPart(), scriptScope);
        Object pvArrayObject = Context.javaToJS(getPvArray(), scriptScope);
        Object displayObject = Context.javaToJS(getDisplayEditPart(), scriptScope);

        ScriptableObject.putProperty(scriptScope, ScriptService.WIDGET, widgetController);
        ScriptableObject.putProperty(scriptScope, ScriptService.PVS, pvArrayObject);
        ScriptableObject.putProperty(scriptScope, ScriptService.DISPLAY, displayObject);
        ScriptableObject.putProperty(scriptScope,
                ScriptService.WIDGET_CONTROLLER_DEPRECIATED, widgetController);
        ScriptableObject.putProperty(scriptScope,
                ScriptService.PV_ARRAY_DEPRECIATED, pvArrayObject);
    }


    @Override
    protected void compileString(String string) throws Exception{
        script = scriptContext.compileString(string, "rule", 1, null);
    }

    @Override
    protected void compileInputStream(File file, InputStream s) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(s));
        script = scriptContext.compileReader(reader, "script", 1, null); //$NON-NLS-1$
        s.close();
        reader.close();
    }

    @Override
    protected void execScript(final IPV triggerPV) throws Exception {
        ScriptableObject.putProperty(scriptScope,
                ScriptService.TRIGGER_PV, Context.javaToJS(triggerPV, scriptScope));
        script.exec(scriptContext, scriptScope);
    }

}
