package org.csstudio.diag.diles;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.csstudio.diag.diles.actions.ChangeTrueFalseAction;
import org.csstudio.diag.diles.actions.DilesContextMenuProvider;
import org.csstudio.diag.diles.dnd.FlowchartDropTargetListener;
import org.csstudio.diag.diles.editpart.PartFactory;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.AnalogInput;
import org.csstudio.diag.diles.model.And;
import org.csstudio.diag.diles.model.Chart;
import org.csstudio.diag.diles.model.CommandTrueFalse;
import org.csstudio.diag.diles.model.Comparator;
import org.csstudio.diag.diles.model.FlipFlop;
import org.csstudio.diag.diles.model.HardwareOut;
import org.csstudio.diag.diles.model.HardwareTrueFalse;
import org.csstudio.diag.diles.model.Not;
import org.csstudio.diag.diles.model.Or;
import org.csstudio.diag.diles.model.Path;
import org.csstudio.diag.diles.model.Status;
import org.csstudio.diag.diles.model.TDDTimer;
import org.csstudio.diag.diles.model.TDETimer;
import org.csstudio.diag.diles.model.WireBendpoint;
import org.csstudio.diag.diles.model.Xor;
import org.csstudio.diag.diles.palette.DilesPalette;
import org.csstudio.diag.diles.providers.ModelLabelProvider;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class DilesEditor extends GraphicalEditorWithPalette {

	/**
	 * Path to XML file
	 */
	private String xmlFile = "C:/Documents and Settings/rpovsic/xml1.xml";

	/**
	 * The file extension for SDS display files.
	 */
	public static final String DILES_FILE_EXTENSION = "diles";

	public static Chart flowchart;

	public static Chart getChart() {
		return flowchart;
	}

	private boolean savePreviouslyNeeded = false;

	private PaletteRoot root;

	// Part 4: Allow the user to interact with the environment
	private KeyHandler sharedKeyHandler;

	/**
	 * Reference to in/out view for content of this editor
	 */
	private InOutView _inOutView;

	public DilesEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
	 * .EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {
		if (isDirty()) {
			if (!savePreviouslyNeeded()) {
				setSavePreviouslyNeeded(true);
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		} else {
			setSavePreviouslyNeeded(false);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		super.commandStackChanged(event);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				false);
		getGraphicalViewer()
				.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);

		// make x-direction grid very big so it won't be shown
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
				new Dimension(80, 80000));

		IAction showGrid = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(showGrid);

		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(new PartFactory());
		ContextMenuProvider provider = new DilesContextMenuProvider(
				getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(provider, getGraphicalViewer());
		getGraphicalViewer().setKeyHandler(
				new GraphicalViewerKeyHandler(getGraphicalViewer())
						.setParent(getCommonKeyHandler()));

		zoom();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		getCommandStack().flush();

		firePropertyChange(IEditorPart.PROP_DIRTY);

		saveToXml();
		//very ugly, see javaDoc of resetStrangeNumbers
		ModelLabelProvider.resetStrangeNumbers();
		_inOutView.updateTable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
		dialog.setFilterNames(new String[] { "Text File", "All Files" });
		String fileSelected = dialog.open();

		if (fileSelected != null) {
			xmlFile = fileSelected;
			saveToXml();
		}

	}

	private File getEditorInputFile() {
		IEditorInput input = getEditorInput();

		IFile file = (IFile) input.getAdapter(IFile.class);
		IPath location = file.getLocation();
		File f = null;
		if (location != null)
			f = location.toFile();

		return f;
	}

	private void saveToXml() {

		/*
		 * 
		 * ------- saving to XML
		 */
		Element root = new Element("org.csstudio.diag.diles");
		Element activities = new Element("activities");

		List<Activity> children = getChart().getChildren();

		for (int i = 0; i < children.size(); i++) {
			Element activity = new Element("Activity");
			activity.setAttribute("type", children.get(i).getClass()
					.getSimpleName());
			activity.setAttribute("unique_id", children.get(i).getUniqueId());

			activity.setAttribute("name",
					(children.get(i).getName() == null) ? "null" : children
							.get(i).getName());
			activity.setAttribute("column", Integer.toString(children.get(i)
					.getColumn()));
			activity.setAttribute("x", Integer.toString(children.get(i)
					.getLocation().x));
			activity.setAttribute("y", Integer.toString(children.get(i)
					.getLocation().y));
			activity.setAttribute("width", Integer.toString(children.get(i)
					.getSize().width));
			activity.setAttribute("height", Integer.toString(children.get(i)
					.getSize().height));
			if (children.get(i) instanceof AnalogInput) {
				activity.setAttribute("status", String
						.valueOf(((AnalogInput) children.get(i))
								.getDoubleResult()));
			} else {
				activity.setAttribute("status", Boolean.toString(children
						.get(i).getResult()));
			}
			activities.addContent(activity);

		}

		root.addContent(activities);

		Element connections = new Element("connections");

		for (int i = 0; i < children.size(); i++) {
			List<Path> conns = children.get(i).getSourceConnections();

			for (int j = 0; j < conns.size(); j++) {
				Element connection = new Element("connection");
				connection
						.setAttribute("source_id", conns.get(j).getSourceId());
				connection.setAttribute("source_terminal", conns.get(j)
						.getSourceName());
				connection
						.setAttribute("target_id", conns.get(j).getTargetId());
				connection.setAttribute("target_terminal", conns.get(j)
						.getTargetName());

				List<WireBendpoint> bends = conns.get(j).getBendpoints();
				for (int k = 0; k < bends.size(); k++) {
					Element bend = new Element("bendpoint");
					bend.setAttribute("first_relative_dimension_x",
							Integer.toString(bends.get(k)
									.getFirstRelativeDimension().width));
					bend.setAttribute("first_relative_dimension_y",
							Integer.toString(bends.get(k)
									.getFirstRelativeDimension().height));
					bend.setAttribute("second_relative_dimension_x",
							Integer.toString(bends.get(k)
									.getSecondRelativeDimension().width));
					bend.setAttribute("second_relative_dimension_y",
							Integer.toString(bends.get(k)
									.getSecondRelativeDimension().height));

					connection.addContent(bend);
				}

				connections.addContent(connection);
			}
		}

		root.addContent(connections);

		Document doc = new Document(root);

		try {
			Format f = Format.getPrettyFormat();

			XMLOutputter serializer = new XMLOutputter(f);

			FileOutputStream fs = new FileOutputStream(getEditorInputFile());

			serializer.output(doc, fs);

			fs.flush();
			fs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler
					.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
							getActionRegistry().getAction(
									ActionFactory.DELETE.getId()));
		}
		return sharedKeyHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (root == null)
			root = DilesPalette.getPaletteRoot();
		return root;
	}

	public void gotoMarker(IMarker marker) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(flowchart);
		getGraphicalViewer().addDropTargetListener(
				new FlowchartDropTargetListener(getGraphicalViewer()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#initializePaletteViewer
	 * ()
	 */
	@Override
	protected void initializePaletteViewer() {
		super.initializePaletteViewer();
		getPaletteViewer().addDragSourceListener(
				new TemplateTransferDragSourceListener(getPaletteViewer()));
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
	// */
	// @Override
	// public boolean isDirty() {
	// return isSaveOnCloseNeeded();
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.gef.ui.parts.GraphicalEditor#isSaveAsAllowed()
	// */
	// @Override
	// public boolean isSaveAsAllowed() {
	// return true;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveOnCloseNeeded()
	 */
	@Override
	public boolean isSaveOnCloseNeeded() {
		return getCommandStack().isDirty();
	}

	private boolean savePreviouslyNeeded() {
		return savePreviouslyNeeded;
	}

	public void setChart(Chart chart) {
		flowchart = chart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		/*
		 * Shell shell = PlatformUI.getWorkbench().
		 * getActiveWorkbenchWindow().getShell();
		 * 
		 * FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		 * dialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		 * dialog.setFilterNames(new String[] {"Text File", "All Files"});
		 * String fileSelected = dialog.open();
		 * 
		 * xmlFile = fileSelected;
		 * 
		 * if (fileSelected == null) { flowchart = new Chart(); return; }
		 */

		/*
		 * ---- reading from XML
		 */
		SAXBuilder builder1 = new SAXBuilder();

		try {
			Document doc = builder1.build(getEditorInputFile());
			Element root = doc.getRootElement();

			flowchart = new Chart();

			Element activities = root.getChild("activities");
			List<Element> activitiesList = activities.getChildren();

			for (int i = 0; i < activitiesList.size(); i++) {

				Activity activity = null;

				// Activity activity = new Activity();
				if (activitiesList.get(i).getAttributeValue("type").equals(
						"And")) {
					activity = new And();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("Or")) {
					activity = new Or();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("Xor")) {
					activity = new Xor();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("Not")) {
					activity = new Not();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("FlipFlop")) {
					activity = new FlipFlop();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("TDETimer")) {
					activity = new TDETimer();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("TDDTimer")) {
					activity = new TDDTimer();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("HardwareOut")) {
					activity = new HardwareOut();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("HardwareTrueFalse")) {
					activity = new HardwareTrueFalse();
					if (activitiesList.get(i).getAttributeValue("status")
							.equals("true")) {
						((HardwareTrueFalse) activity).setResult(true);
					} else {
						((HardwareTrueFalse) activity).setResult(false);
					}
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("Status")) {
					activity = new Status();
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("CommandTrueFalse")) {
					activity = new CommandTrueFalse();
					if (activitiesList.get(i).getAttributeValue("status")
							.equals("true")) {
						((CommandTrueFalse) activity).setResult(true);
					} else {
						((CommandTrueFalse) activity).setResult(false);
					}
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("AnalogInput")) {
					activity = new AnalogInput();
					((AnalogInput) activity).setDoubleResult(Double
							.valueOf(activitiesList.get(i).getAttributeValue(
									"status")));
				} else if (activitiesList.get(i).getAttributeValue("type")
						.equals("Comparator")) {
					activity = new Comparator();
				}

				activity.setUniqueId(activitiesList.get(i).getAttributeValue(
						"unique_id"));
				activity.setName(activitiesList.get(i)
						.getAttributeValue("name"));
				activity.setSize(new Dimension(activitiesList.get(i)
						.getAttribute("width").getIntValue(), activitiesList
						.get(i).getAttribute("height").getIntValue()));
				activity.setLocation(new Point(activitiesList.get(i)
						.getAttribute("x").getIntValue(), activitiesList.get(i)
						.getAttribute("y").getIntValue()));

				flowchart.addChild(activity);
			}

			Element connections = root.getChild("connections");
			List<Element> connectionsList = connections.getChildren();

			for (int i = 0; i < connectionsList.size(); i++) {
				Path path = new Path();

				for (int j = 0; j < flowchart.getChildren().size(); j++) {
					if (connectionsList.get(i).getAttribute("source_id")
							.getValue().equals(
									(flowchart.getChildren().get(j))
											.getUniqueId())) {
						path.setSource(flowchart.getChildren().get(j));
						path.setSourceName(connectionsList.get(i).getAttribute(
								"source_terminal").getValue());
						path.setSourceId(connectionsList.get(i).getAttribute(
								"source_id").getValue());
						path.attachSource();

						List<Element> bends = ((Element) connections
								.getChildren().get(i)).getChildren();
						for (int k = 0; k < bends.size(); k++) {
							WireBendpoint wb = new WireBendpoint();
							wb
									.setRelativeDimensions(
											new Dimension(
													bends
															.get(k)
															.getAttribute(
																	"first_relative_dimension_x")
															.getIntValue(),
													bends
															.get(k)
															.getAttribute(
																	"first_relative_dimension_y")
															.getIntValue()),
											new Dimension(
													bends
															.get(k)
															.getAttribute(
																	"second_relative_dimension_x")
															.getIntValue(),
													bends
															.get(k)
															.getAttribute(
																	"second_relative_dimension_y")
															.getIntValue()));
							wb.setWeight(0.5f);

							path.insertBendpoint(k, wb);
						}

					}
					if (connectionsList.get(i).getAttribute("target_id")
							.getValue().equals(
									(flowchart.getChildren().get(j))
											.getUniqueId())) {
						path.setTarget(flowchart.getChildren().get(j));
						path.setTargetName(connectionsList.get(i).getAttribute(
								"target_terminal").getValue());
						path.setTargetId(connectionsList.get(i).getAttribute(
								"target_id").getValue());
						path.attachTarget();
					}

				}

			}

		} catch (JDOMParseException e) {
			flowchart = new Chart();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void setSavePreviouslyNeeded(boolean value) {
		savePreviouslyNeeded = value;
	}

	private void zoom() {
		double[] zoomLevels;
		ArrayList<String> zoomContributions;

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		getGraphicalViewer().setRootEditPart(rootEditPart);
		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));
		zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0,
				4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);
		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new ChangeTrueFalseAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

	public void setInOutView(InOutView inOutView) {
		_inOutView = inOutView;
		if (_inOutView != null) {
			List<Activity> children = DilesEditor.getChart().getChildren();
			if (children != null) {
				_inOutView.setTableInput(children);
//				_inOutView.updateTable();
			}
		}
	}

	@Override
	public void setFocus() {
		super.setFocus();
		if (_inOutView != null) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().activate(_inOutView);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().hideView(_inOutView);
	}
}
