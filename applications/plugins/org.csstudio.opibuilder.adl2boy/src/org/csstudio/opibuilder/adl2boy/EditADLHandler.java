package org.csstudio.opibuilder.adl2boy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.opibuilder.widgets.model.XMeterModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ColorMap;
import org.csstudio.utility.adlparser.fileParser.ParserADL;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.fileParser.widgets.*;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
				int displayForeColor = 0;
				int displayBackColor = 0;
				for (ADLWidget adlWidget : root.getObjects()){
					String widgetType = adlWidget.getType();
					if (widgetType.equals("display")){
						ADLDisplay adlDisp = new ADLDisplay(adlWidget);
						if (adlDisp !=null){
							setADLObjectProps(adlDisp, displayModel);
							setADLBasicAttributeProps(adlDisp, displayModel, false);
							displayForeColor = Integer.parseInt(adlDisp.get_clr());
							displayBackColor = Integer.parseInt(adlDisp.get_bclr());
							
						}
					}
				}
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
				displayModel.setBackgroundColor(colorMap[displayBackColor]);
				displayModel.setForegroundColor(colorMap[displayForeColor]);
				for (ADLWidget adlWidget : root.getObjects()){
					try {
						String widgetType = adlWidget.getType();
						if (widgetType.equals("arc")){
							Arc arcWidget = new Arc(adlWidget);
							ArcModel arcModel = new ArcModel();
							if (arcWidget != null) {
								setADLObjectProps(arcWidget, arcModel);
								setADLBasicAttributeProps(arcWidget, arcModel, false);
							}
							displayModel.addChild(arcModel);
							printNotCompletelyHandledMessage(widgetType);
						}
						else if (widgetType.equals("bar")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("byte")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("bar")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("cartesian plot")){
							CartesianPlot plotWidget = new CartesianPlot(adlWidget);
							XYGraphModel graphModel = new XYGraphModel();
							if (plotWidget != null) {
								setADLObjectProps(plotWidget, graphModel);
							}
							displayModel.addChild(graphModel);
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
							Image imageWidget = new Image(adlWidget);
							ImageModel imageModel = new ImageModel();
							if (imageWidget != null) {
								setADLObjectProps(imageWidget, imageModel);
								setADLBasicAttributeProps(imageWidget, imageModel, false);
							}
							displayModel.addChild(imageModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("indicator")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("menu")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("message button")){
							System.out.println("Creating a message Button");
							MessageButton messageButtonWidget = new MessageButton(adlWidget);
							ActionButtonModel buttonModel = new ActionButtonModel();
							if (messageButtonWidget != null) {
								setADLObjectProps(messageButtonWidget, buttonModel);
								setADLControlProps(messageButtonWidget, buttonModel);
							}
							buttonModel.setPropertyValue(ActionButtonModel.PROP_TEXT, messageButtonWidget.getLabel());
							int actionIndex = 0;
							String press_msg = messageButtonWidget.getPress_msg();
							if ( (press_msg != null) && !(press_msg.equals(""))){
								ActionsInput ai = buttonModel.getActionsInput();
								WritePVAction wpvAction = new WritePVAction();
								wpvAction.setPropertyValue(WritePVAction.PROP_PVNAME, messageButtonWidget.getAdlControl().getChan());
								wpvAction.setPropertyValue(WritePVAction.PROP_VALUE, press_msg);
								ai.addAction(wpvAction);
								buttonModel.setPropertyValue(ActionButtonModel.PROP_ACTION_INDEX, actionIndex);
								actionIndex++;
							}
							String release_msg = messageButtonWidget.getRelease_msg();
							if ( (release_msg != null) && !(release_msg.equals(""))){
								buttonModel.setPropertyValue(ActionButtonModel.PROP_TOGGLE_BUTTON, true);
								ActionsInput ai = buttonModel.getActionsInput();
								WritePVAction wpvAction = new WritePVAction();
								wpvAction.setPropertyValue(WritePVAction.PROP_PVNAME, messageButtonWidget.getAdlControl().getChan());
								wpvAction.setPropertyValue(WritePVAction.PROP_VALUE, release_msg);
								ai.addAction(wpvAction);
								buttonModel.setPropertyValue(ActionButtonModel.PROP_RELEASED_ACTION_INDEX, actionIndex);
								actionIndex++;
							}
							
							displayModel.addChild(buttonModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("toggle button")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("meter")){
							Meter meterWidget = new Meter(adlWidget);
							XMeterModel meterModel = new XMeterModel();
							if (meterWidget != null) {
								setADLObjectProps(meterWidget, meterModel);
								setADLMonitorProps(meterWidget, meterModel);
							}
							displayModel.addChild(meterModel);
							printNotHandledMessage(widgetType);
								
						}
						else if (widgetType.equals("oval")){
							Oval ovalWidget = new Oval(adlWidget);
							EllipseModel ellipseModel = new EllipseModel();
							if (ovalWidget != null) {
								setADLObjectProps(ovalWidget, ellipseModel);
								setADLBasicAttributeProps(ovalWidget, ellipseModel, false);
							}
							displayModel.addChild(ellipseModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("polygon")){
							Polygon polygonWidget = new Polygon(adlWidget);
							PolygonModel polygonModel = new PolygonModel();
							if (polygonWidget != null) {
								setADLObjectProps(polygonWidget, polygonModel);
								setADLBasicAttributeProps(polygonWidget, polygonModel, false);
							}
							polygonModel.setPoints(polygonWidget.getAdlPoints().getPointsList(), true);
							displayModel.addChild(polygonModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("polyline")){
							PolyLine polylineWidget = new PolyLine(adlWidget);
							PolyLineModel polylineModel = new PolyLineModel();
							if (polylineWidget != null) {
								setADLObjectProps(polylineWidget, polylineModel);
								setADLBasicAttributeProps(polylineWidget, polylineModel, false);
							}
							polylineModel.setPoints(polylineWidget.getAdlPoints().getPointsList(), true);
							displayModel.addChild(polylineModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("line")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("rectangle")){
							Rectangle rectWidget = new Rectangle(adlWidget);
							RectangleModel labelModel = new RectangleModel();
							if (rectWidget != null) {
								setADLObjectProps(rectWidget, labelModel);
								setADLBasicAttributeProps(rectWidget, labelModel, false);
							}
							displayModel.addChild(labelModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("related display")){
							RelatedDisplay rdWidget = new RelatedDisplay(adlWidget);
							MenuButtonModel menuModel = new MenuButtonModel();
							if (rdWidget != null) {
								setADLObjectProps(rdWidget, menuModel);
//								if (textWidget.getTextix() != null ){
//									labelModel.setText(textWidget.getTextix());
//									
//								}
								if ((rdWidget.getClr() != null) && !rdWidget.getClr().equals("")){
									menuModel.setForegroundColor( colorMap[Integer.parseInt(rdWidget.getClr())] );
								}
								if ((rdWidget.getBclr() != null) && !rdWidget.getClr().equals("")){
									menuModel.setBackgroundColor( colorMap[Integer.parseInt(rdWidget.getBclr())] );
								}
								RelatedDisplayItem[] rdDisplays = rdWidget.getRelatedDisplayItems();
								if ( rdDisplays.length > 0){
									ActionsInput ai = menuModel.getActionsInput();
									for (int ii=0; ii< rdDisplays.length; ii++){
										OpenDisplayAction odAction = new OpenDisplayAction();
										odAction.setPropertyValue(OpenDisplayAction.PROP_DESCRIPTION, rdDisplays[ii].getLabel().replaceAll("\"", ""));

										//Try to add the filename to the PROP_PATH
										IPath fPath = new Path(rdDisplays[ii].getName().replaceAll("\"", "").replace(".adl", ".opi"));
										System.out.println("Related display file: " + rdDisplays[ii].getName().replace(".adl", ".opi"));
										System.out.println("Related display file from IPath: " + fPath.toString() + ", " + fPath.getFileExtension());
										odAction.setPropertyValue(OpenDisplayAction.PROP_PATH, fPath);

										//Try to add macros
										String argsIn = "true, " + rdDisplays[ii].getArgs().replaceAll("\"", "");
										MacrosInput macIn = MacrosInput.recoverFromString( argsIn);
										
										odAction.setPropertyValue(OpenDisplayAction.PROP_MACROS, macIn );
										ai.addAction(odAction);
									}
								}
							}
							menuModel.setPropertyValue(MenuButtonModel.PROP_LABEL, true);
							displayModel.addChild(menuModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("strip chart")){
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text")){
							TextWidget textWidget = new TextWidget(adlWidget);
							LabelModel labelModel = new LabelModel();
							if (textWidget != null) {
								setADLObjectProps(textWidget, labelModel);
								setADLBasicAttributeProps(textWidget, labelModel, true);
								if (textWidget.getTextix() != null ){
									labelModel.setText(textWidget.getTextix());
									
								}
							}
							labelModel.setPropertyValue(LabelModel.PROP_TRANSPARENT, true);
							displayModel.addChild(labelModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text update")){
							TextUpdateWidget textUpdateWidget = new TextUpdateWidget(adlWidget);
							TextIndicatorModel labelModel = new TextIndicatorModel();
							if (textUpdateWidget != null) {
								setADLObjectProps(textUpdateWidget, labelModel);
								setADLMonitorProps(textUpdateWidget, labelModel);
							}
							displayModel.addChild(labelModel);
							printNotCompletelyHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("text entry")){
							TextEntryWidget textEntryWidget = new TextEntryWidget(adlWidget);
							TextInputModel labelModel = new TextInputModel();
							if (textEntryWidget != null) {
								setADLObjectProps(textEntryWidget, labelModel);
								setADLControlProps(textEntryWidget, labelModel);
							}
							displayModel.addChild(labelModel);
							printNotHandledMessage(widgetType);
							
						}
						else if (widgetType.equals("valuator")){
							printNotHandledMessage(widgetType);
							
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

	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	private void setADLObjectProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLObject()){
			ADLObject adlObj=adlWidget.getAdlObject();
			widgetModel.setX(adlObj.getX());
			widgetModel.setY(adlObj.getY());
			widgetModel.setHeight(adlObj.getHeight());
			widgetModel.setWidth(adlObj.getWidth());
		}
		
	}

	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	private void setADLBasicAttributeProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel, boolean colorForeground){
		if (adlWidget.hasADLBasicAttribute()){
			ADLBasicAttribute basAttr = adlWidget.getAdlBasicAttribute();
			System.out.println("Trying to load color " + basAttr.getClr() );
			if ((basAttr.getClr() != null) && (!(basAttr.getClr().equals(""))) ){
				if (colorForeground) {
					widgetModel.setForegroundColor(colorMap[Integer.parseInt(basAttr.getClr())]);
				}
				else {
					widgetModel.setBackgroundColor(colorMap[Integer.parseInt(basAttr.getClr())]);
				}
			}
		}
	}
	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	private void setADLControlProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLControl()){
			ADLControl control = adlWidget.getAdlControl();
			String foreClr = control.getForegroundColor();
			if ((foreClr != null) && (!(foreClr.equals(""))) ){
				widgetModel.setForegroundColor(colorMap[Integer.parseInt(foreClr)]);
			}
			String backClr = control.getBackgroundColor();
			if ((backClr != null) && (!(backClr.equals(""))) ){
				widgetModel.setBackgroundColor(colorMap[Integer.parseInt(backClr)]);
			}
			String channel = control.getChan();
			if ((channel != null) && (!(channel.equals(""))) ){
				widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, channel);
			}
		}
	}
	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	private void setADLMonitorProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLMonitor()){
			ADLMonitor monitor = adlWidget.getAdlMonitor();
			String foreClr = monitor.getForegroundColor();
			if ((foreClr != null) && (!(foreClr.equals(""))) ){
				widgetModel.setForegroundColor(colorMap[Integer.parseInt(foreClr)]);
			}
			String backClr = monitor.getBackgroundColor();
			if ((backClr != null) && (!(backClr.equals(""))) ){
				widgetModel.setBackgroundColor(colorMap[Integer.parseInt(backClr)]);
			}
			String channel = monitor.getChan();
			if ((channel != null) && (!(channel.equals(""))) ){
				widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, channel);
			}
		}
	}
}
