package org.csstudio.opibuilder.adl2boy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.csstudio.opibuilder.adl2boy.translator.Arc2Model;
import org.csstudio.opibuilder.adl2boy.translator.Bar2Model;
import org.csstudio.opibuilder.adl2boy.translator.CartesianPlot2Model;
import org.csstudio.opibuilder.adl2boy.translator.Display2Model;
import org.csstudio.opibuilder.adl2boy.translator.Image2Model;
import org.csstudio.opibuilder.adl2boy.translator.Menu2Model;
import org.csstudio.opibuilder.adl2boy.translator.MessageButton2Model;
import org.csstudio.opibuilder.adl2boy.translator.Meter2Model;
import org.csstudio.opibuilder.adl2boy.translator.Oval2Model;
import org.csstudio.opibuilder.adl2boy.translator.PolyLine2Model;
import org.csstudio.opibuilder.adl2boy.translator.Polygon2Model;
import org.csstudio.opibuilder.adl2boy.translator.Rectangle2Model;
import org.csstudio.opibuilder.adl2boy.translator.RelatedDisplay2Model;
import org.csstudio.opibuilder.adl2boy.translator.Text2Model;
import org.csstudio.opibuilder.adl2boy.translator.TextEntry2Model;
import org.csstudio.opibuilder.adl2boy.translator.TextUpdate2Model;
import org.csstudio.opibuilder.adl2boy.translator.Valuator2Model;
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
				for (ADLWidget adlWidget : root.getObjects()){
					try {
						String widgetType = adlWidget.getType();
						if (widgetType.equals("arc")){
							displayModel.addChild((new Arc2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
						}
						else if (widgetType.equals("bar")){
							displayModel.addChild((new Bar2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
						
						}
						else if (widgetType.equals("byte")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("cartesian plot")){
							displayModel.addChild((new CartesianPlot2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("choice button")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("composite")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("dynamic symbol")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("file")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("image")){
							displayModel.addChild((new Image2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("indicator")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("menu")){
							displayModel.addChild((new Menu2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("message button")){
							displayModel.addChild((new MessageButton2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("toggle button")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("meter")){
							displayModel.addChild((new Meter2Model(adlWidget, colorMap)).getWidgetModel());
							printNotHandledMessage(widgetType);
								
						}
						else if (widgetType.equals("oval")){
							displayModel.addChild((new Oval2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("polygon")){
							displayModel.addChild((new Polygon2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("polyline")){
							displayModel.addChild((new PolyLine2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("line")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("rectangle")){
							displayModel.addChild((new Rectangle2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("related display")){
							displayModel.addChild((new RelatedDisplay2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("strip chart")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text")){
							displayModel.addChild((new Text2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text update")){
							displayModel.addChild((new TextUpdate2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text entry")){
							displayModel.addChild((new TextEntry2Model(adlWidget, colorMap)).getWidgetModel());
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("valuator")){
							displayModel.addChild((new Valuator2Model(adlWidget, colorMap)).getWidgetModel());
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("basic attribute")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("dynamic attribute")){
							printNotHandledMessage(widgetType);
							
						}
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
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
