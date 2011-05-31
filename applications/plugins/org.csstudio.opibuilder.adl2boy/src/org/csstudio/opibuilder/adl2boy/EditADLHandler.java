/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/
package org.csstudio.opibuilder.adl2boy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Handler for menu item that converts the files.
 * 
 * @author John Hammonds, Argonne National Laboratory
 *
 */
public class EditADLHandler implements IHandler {

	public EditADLHandler() {
		// TODO Auto-generated constructor stub
	}

	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection){
			IStructuredSelection strucSelection = (IStructuredSelection)selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext(); ){
				Object element = iterator.next();
				String adlFileName = element.toString().substring(1);
				String outfileName = adlFileName.substring(0, element.toString().length()-4);
				String opiFileName = new String(outfileName + "opi");
				Path path = new Path(opiFileName);
				
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				String fullADLFileName = Platform.getLocation() + adlFileName;
				convertAdlToFile(fullADLFileName, file);
				try {
					HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					       .openEditor(new FileEditorInput(file), "org.csstudio.opibuilder.OPIEditor");
				}
				catch (Exception ex){
					System.out.println("Problem");
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * @param fullADLFileName
	 * @param file
	 */
	public void convertAdlToFile(String fullADLFileName, IFile file) {
		DisplayModel displayModel = TranslatorUtils.convertAdlToModel(fullADLFileName);
		
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream is = new ByteArrayInputStream(s.getBytes());
		try {
			file.create(is, false, null);
		}
		catch (Exception ex){
			System.out.println("Problem");
			ex.printStackTrace();
		}
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}
	/** 
	 * Print message that a given ADL file structure is not handled.
	 */
	private void printNotHandledMessage(String type) {
		System.out.println("EditHandler: " + type + " is not handled");
	}
	private void printNotCompletelyHandledMessage(String type) {
		System.out.println("EditHandler: " + type + " is not completely handled");
	}
}
