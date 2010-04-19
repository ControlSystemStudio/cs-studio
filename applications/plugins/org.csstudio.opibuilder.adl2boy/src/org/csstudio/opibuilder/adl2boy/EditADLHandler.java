package org.csstudio.opibuilder.adl2boy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.csstudio.opibuilder.adl2boy.translator.Display2Model;
import org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ColorMap;
import org.csstudio.utility.adlparser.fileParser.ParserADL;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class EditADLHandler implements IHandler {
	RGB colorMap[] = new RGB[0];

	public EditADLHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection){
			IStructuredSelection strucSelection = (IStructuredSelection)selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext(); ){
				Object element = iterator.next();
				System.out.println(element.toString());
				String adlFileName = element.toString().substring(1);
				String outfileName = adlFileName.substring(0, element.toString().length()-4);
				System.out.println("outfileName " + outfileName);
				String opiFileName = new String(outfileName + "opi");
				System.out.println("opiFileName " +  opiFileName);
				Path path = new Path(opiFileName);
				
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				DisplayModel displayModel = new DisplayModel();
				System.out.println(Platform.getLocation());
				ADLWidget root = ParserADL.getNextElement(new File(Platform.getLocation() + adlFileName));
				// Configure the display
				for (ADLWidget adlWidget : root.getObjects()){
					String widgetType = adlWidget.getType();
					try {
						if (widgetType.equals("color map")){
							ColorMap tempColorMap = new ColorMap(adlWidget);
							colorMap = tempColorMap.getColors();
							
						}
					}
					catch (Exception ex) {
						System.out.println("Error reading ColorMap");
						ex.printStackTrace();
					}
				}

				for (ADLWidget adlWidget : root.getObjects()){
					String widgetType = adlWidget.getType();
					if (widgetType.equals("display")){
						displayModel = (DisplayModel)(new Display2Model(adlWidget, colorMap)).getWidgetModel();
					}
				}
				TranslatorUtils.ConvertChildren(root.getObjects(), displayModel, colorMap);
				
				String s = XMLUtil.WidgetToXMLString(displayModel, true);
				InputStream is = new ByteArrayInputStream(s.getBytes());
				try {
					file.create(is, false, null);
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

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
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
