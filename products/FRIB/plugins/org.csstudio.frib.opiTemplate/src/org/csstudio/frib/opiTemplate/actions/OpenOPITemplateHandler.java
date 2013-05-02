package org.csstudio.frib.opiTemplate.actions;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.IRunnerInput;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.GeometryUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgetActions.OpenOPIInViewAction;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPage;

public class OpenOPITemplateHandler extends AbstractHandler {
	private static final String MACRO_NAME = "ID"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final ProcessVariable[] pvs = AdapterUtil.convert(selection,
				ProcessVariable.class);
		final Shell shell = HandlerUtil.getActiveShell(event);
		Pattern pattern = Pattern
				.compile("(.+?_.+?:(.+?)\\d{0,2}_[DN][0-9]{4}).*");

		final Map<String, TreeSet<String>> cmap = new TreeMap<String, TreeSet<String>>();
		final Position position = Position.DETACHED;

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		final String secondID = OPIView.createNewInstance() + position.name();
		final GroupingContainerModel groupingContainer = new GroupingContainerModel();
		final IPath probeOPIPath = ResourceUtil.getPathFromString("platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/blank.opi");

		for (ProcessVariable pv : pvs) {
			Matcher matcher = pattern.matcher(pv.getName());
			String id = null;
			String device = null;
			while (matcher.find()) {
				id = matcher.group(1);
				device = matcher.group(2);
			}
			TreeSet<String> ids = cmap.get(device);
			if (ids == null) {
				ids = new TreeSet<String>();
				cmap.put(device, ids);
			}
			ids.add(id);
		}

		try {

			Job job = new Job("Loading device OPI...") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Loading templates",
							IProgressMonitor.UNKNOWN);
					
					try {
						if(pvs.length > 1000){
							throw new IllegalArgumentException();
						}
						 Display.getDefault().syncExec(new Runnable() {
							public void run() {
								//for (Map.Entry<String, TreeSet<String>> entry : cmap.entrySet()) {
									
									//groupingContainer.setName(entry.getKey());
									//groupingContainer.setPropertyValue("scale_options", "true true false");
									IViewPart opiView;
									try {									
										opiView = page.showView(OPIView.ID,secondID,IWorkbenchPage.VIEW_ACTIVATE);
										if (opiView instanceof OPIView) {
											if (position == Position.DETACHED){
												((WorkbenchPage) page).detachView(page.findViewReference(OPIView.ID,secondID));
											}
											RunnerInput runnerInput = new RunnerInput(probeOPIPath, null, null);
											((OPIView) opiView).setOPIInput(runnerInput);
											((OPIView) opiView).getDisplayModel().addChild(groupingContainer);
										}
									} catch (PartInitException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								//}
							}
						});

						 Display.getDefault().syncExec(new Runnable() {
							public void run() {
								IViewPart opiView = null;
								try {
									opiView = page.showView(OPIView.ID,secondID,IWorkbenchPage.VIEW_ACTIVATE);
								} catch (PartInitException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								for (Map.Entry<String, TreeSet<String>> entry : cmap.entrySet()) {
									//GroupingContainerModel groupingContainer = ((GroupingContainerModel) ((OPIView) opiView).getDisplayModel().getChildByName(entry.getKey()));
									IPath path = ResourceUtil.getPathFromString("platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"+ entry.getKey()+ "_header.opi");
									if(!ResourceUtil.isExsitingFile(path, false)){
										continue;
									}

									LinkingContainerModel linkingContainerHeader = new LinkingContainerModel();
									linkingContainerHeader.setName(entry.getKey() + "Header");
									linkingContainerHeader.setPropertyValue("opi_file","platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"+ entry.getKey()+ "_header.opi");
									linkingContainerHeader.setPropertyValue("auto_size", true);
									linkingContainerHeader.setPropertyValue("zoom_to_fit", false);
									linkingContainerHeader.setPropertyValue("border_style", 0);
									Rectangle range = getChildrenRange(groupingContainer);
									linkingContainerHeader.setY(range.y + range.height);
									groupingContainer.addChild(linkingContainerHeader);
									Iterator itr = entry.getValue().iterator();

									for (Integer i = 0; i < entry.getValue().size(); i++) {
										String id = itr.next().toString();
										LinkingContainerModel linkingContainer = new LinkingContainerModel();
										linkingContainer.setPropertyValue("opi_file","platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"+ entry.getKey()+ ".opi");
										linkingContainer.setPropertyValue("auto_size", true);
										linkingContainer.setPropertyValue("zoom_to_fit", false);
										linkingContainer.setPropertyValue("border_style", 0);
										linkingContainer.addMacro("index",i.toString());
										linkingContainer.addMacro("ID", id);
										range = getChildrenRange(groupingContainer);
										linkingContainer.setY(range.y + range.height);
										groupingContainer.addChild(linkingContainer);
									}
									Rectangle allSize = getChildrenRange(groupingContainer);
									groupingContainer.setLocation(allSize.x, allSize.y);
									groupingContainer.setSize(allSize.width, allSize.height);
								}
							}
						});
						
					} catch (final IllegalArgumentException e){
						Display.getDefault().asyncExec(new Runnable() {
						      public void run() {
						        MessageDialog.openInformation(shell, "Warning",
						            "Too many devices");
						      }
						    });
						e.printStackTrace();
						return Status.CANCEL_STATUS;
					}
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		} catch (final Exception e) {

			e.printStackTrace();

		}
		 

		return null;
	}

	private static Rectangle getChildrenRange(AbstractContainerModel container) {
		PointList pointList = new PointList(container.getChildren().size());
		for (Object child : container.getChildren()) {
			AbstractWidgetModel childModel = ((AbstractWidgetModel) child);
			pointList.addPoint(childModel.getLocation());
			pointList.addPoint(childModel.getX() + childModel.getWidth(),
					childModel.getY() + childModel.getHeight());
		}
		return pointList.getBounds();
	}
	
}
