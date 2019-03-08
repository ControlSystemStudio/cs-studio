/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.simplepv.IPV;
import org.eclipse.core.runtime.IPath;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * This is the implementation of {@link AbstractScriptStore} for Jython PythonInterpreter.
 * @author Xihui Chen
 *
 */
public class JythonScriptStore extends AbstractScriptStore{

    private PythonInterpreter interp;
    private PySystemState state;

    private PyCode code;

    public JythonScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
            final IPV[] pvArray) throws Exception {
        super(scriptData, editpart, pvArray);

    }

    @Override
    protected void initScriptEngine() {
        IPath scriptPath = getAbsoluteScriptPath();
        //Add the path of script to python module search path
        state = Py.getSystemState();
        if(scriptPath != null && !scriptPath.isEmpty()){

            //If it is a workspace file.
            if(ResourceUtil.isExistingWorkspaceFile(scriptPath)){
                IPath folderPath = scriptPath.removeLastSegments(1);
                String sysLocation = ResourceUtil.workspacePathToSysPath(folderPath).toOSString();
                state.path.append(new PyString(sysLocation));
            }else if(ResourceUtil.isExistingLocalFile(scriptPath)){
                IPath folderPath = scriptPath.removeLastSegments(1);
                state.path.append(new PyString(folderPath.toOSString()));
            }
        }
        interp = PythonInterpreter.threadLocalStateInterpreter(state.getDict());
    }

    @Override
    protected void compileString(String string) throws Exception {
        code = interp.compile(string);
    }

    @Override
    protected void compileInputStream(File file, InputStream s) throws Exception {
        final PyList paths = interp.getSystemState().path;

        if (file != null)
        {
            // Add path to this script to top of python search path,
            // in case this script includes other scripts from
            // the same directory
            final String path = file.getParent();
            // Prevent concurrent modification
            synchronized (paths)
            {
                final int index = paths.indexOf(path);
                // Already top entry?
                if (index == 0)
                    return;
                // Remove if further down in the list
                if (index > 0)
                    paths.remove(index);
                // Add to front of list
                paths.add(0, path);
            }
        }
        code = interp.compile(new InputStreamReader(s));
    }

    @Override
    protected void execScript(final IPV triggerPV) throws Exception {
        interp.set(ScriptService.WIDGET, getEditPart());
        interp.set(ScriptService.PVS, getPvArray());
        interp.set(ScriptService.DISPLAY, getDisplayEditPart());
        interp.set(ScriptService.WIDGET_CONTROLLER_DEPRECIATED, getEditPart());
        interp.set(ScriptService.PV_ARRAY_DEPRECIATED, getPvArray());
        interp.set(ScriptService.TRIGGER_PV, triggerPV);
        interp.exec(code);
    }

    @Override
    protected void dispose() {
        if (interp != null) {
            PyObject o = interp.getLocals();
            if (o != null && o instanceof PyStringMap) {
                ((PyStringMap)o).clear();
            }
//            o = state.getBuiltins();
//            if (o != null && o instanceof PyStringMap) {
//                ((PyStringMap)o).clear();
//            }
            o = state.getDict();
            if (o != null && o instanceof PyStringMap) {
                ((PyStringMap)o).clear();
            }
            state.close();
            state.cleanup();
            interp.close();
            interp.cleanup();
            interp = null;
            state = null;
        }
        code = null;
        super.dispose();
    }
}
