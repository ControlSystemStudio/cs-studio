/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.io.InputStream;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.simplepv.IPV;
import org.eclipse.core.runtime.IPath;
import org.python.core.PyCode;
import org.python.core.PyString;
import org.python.core.PySystemState;

/**
 * This is the implementation of {@link AbstractScriptStore} for Jython PythonInterpreter.
 * @author Xihui Chen
 *
 */
public class JythonScriptStore extends AbstractScriptStore{

	private PythonInterpreter interpreter;

	private PyCode code;

	public JythonScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
			final IPV[] pvArray) throws Exception {
		super(scriptData, editpart, pvArray);

	}

	@Override
    protected void initScriptEngine() {
		IPath scriptPath = getAbsoluteScriptPath();
		//Add the path of script to python module search path
		PySystemState state = new PySystemState();
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

		interpreter = new PythonInterpreter(null, state);
	}

	@Override
	protected void compileString(String string) throws Exception {
		code = interpreter.compile(string);
	}

	@Override
	protected void compileInputStream(InputStream s) throws Exception {
		code = interpreter.compile(s);
	}

	@Override
	protected void execScript(final IPV triggerPV) throws Exception {
		interpreter.set(ScriptService.WIDGET, getEditPart());
		interpreter.set(ScriptService.PVS, getPvArray());
		interpreter.set(ScriptService.DISPLAY, getDisplayEditPart());
		interpreter.set(ScriptService.WIDGET_CONTROLLER_DEPRECIATED, getEditPart());
		interpreter.set(ScriptService.PV_ARRAY_DEPRECIATED, getPvArray());
		interpreter.set(ScriptService.TRIGGER_PV, triggerPV);
		interpreter.exec(code);
	}


}
