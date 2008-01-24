package org.csstudio.sds.ui.internal.editor;

import java.util.Set;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.platform.ui.workbench.ControlSystemItemEditorInput;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.internal.connection.ConnectionSettings;
import org.csstudio.sds.internal.connection.ConnectionUtil;
import org.csstudio.sds.internal.connection.custom.MultiThreadChannelPool;
import org.csstudio.sds.internal.statistics.StatisticUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.WidgetModelUtil;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * This view can be used to configure and play different performance scenarios
 * and provides some performance information.
 * 
 * @author Sven Wende
 * 
 */
public final class ConnectionView extends ViewPart {

	/**
	 * This is just called to properly initializes the connection util, which
	 * needs to know a valid Display instance.
	 */
	// TODO: Initialisierung sollte nicht auf diesem Call basieren (swende)
	private ConnectionUtil _util = ConnectionUtil.getInstance();

	/**
	 * The view identification as configured in the plugin.xml.
	 */
	public static final String VIEW_ID = "org.csstudio.sds.ui.internal.editor.ConnectionView"; //$NON-NLS-1$

	/**
	 * Listener that is notified when the workbench shuts down.
	 */
	private IWorkbenchListener _workbenchListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(LayoutUtil.createGridLayout(1, 0, 10, 10));

		createDisplayElementsList(parent);

		final Spinner refreshRateSpinner = createRefreshRateSpinner(parent);

		// create button, which connects the current display model to one of the
		// data simulators
		Composite buttonPanel = new Composite(parent, SWT.None);
		buttonPanel.setLayout(new FillLayout());
		buttonPanel.setLayoutData(LayoutUtil.createGridData(400));

		Button connectButton = new Button(buttonPanel, SWT.NONE);
		connectButton.setText("Connect");
		connectButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				/* determine all parameters */

				// model
				DisplayModel model = getDisplayModelFromActiveEditor();

				// connection service
				ConnectionService connectionService = ConnectionService.getInstance();

				// register the workbench listerner
				if (_workbenchListener == null) {
					_workbenchListener = new IWorkbenchListener() {
						public void postShutdown(final IWorkbench workbench) {
							// do nothing!
						}

						public boolean preShutdown(final IWorkbench workbench,
								final boolean forced) {
							return true;
						}
					};

					PlatformUI.getWorkbench().addWorkbenchListener(
							_workbenchListener);
				}

				// refreshRate
				int refreshRate = refreshRateSpinner.getSelection();

				/* connect model */
				if (model != null && connectionService != null
						&& refreshRate >= 0) {
					StatisticUtil.getInstance().init();
					model.setLive(true);
				} else {
					MessageDialog.openInformation(getSite().getShell(),
							"Connection not possible",
							"Not all parameters were set correct!");
				}
			}

		});

		Button disconnectButton = new Button(buttonPanel, SWT.NONE);
		disconnectButton.setText("Disconnect");
		disconnectButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				// connection service
				ConnectionService connectionService = ConnectionService.getInstance();

				connectionService
						.disconnectModel(getDisplayModelFromActiveEditor());

				getDisplayModelFromActiveEditor().setLive(false);

				StatisticUtil.getInstance().init();
			}

		});
	}

	/**
	 * Creates a spinner widget for the refresh rate setting.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the spinner widget
	 */
	private Spinner createRefreshRateSpinner(final Composite parent) {
		Group group = LayoutUtil.createGroupWithFillLayout(parent,
				"Setup Refresh Rate");
		group.setLayoutData(LayoutUtil.createGridData(400));

		final Spinner refreshRateSpinner = new Spinner(group, SWT.None);
		refreshRateSpinner.setMinimum(0);
		refreshRateSpinner.setMaximum(4000);
		refreshRateSpinner.setIncrement(50);
		refreshRateSpinner.setSelection(10);
		return refreshRateSpinner;
	}

	/**
	 * Creates a widget via which a certain amount of widget models can be
	 * easily created in one step.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createDisplayElementsList(final Composite parent) {
		Group group = LayoutUtil.createGroupWithFillLayout(parent,
				"Create Display Elements");
		group.setLayoutData(LayoutUtil.createGridData(400));

		TreeViewer modelsViewer = new TreeViewer(group);
		modelsViewer.setContentProvider(new BaseWorkbenchContentProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return ((Set) element).toArray();
			}
		});

		modelsViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(final Object element) {
				String contributingPluginId = WidgetModelFactoryService
						.getInstance()
						.getContributingPluginId((String) element);
				String iconPath = WidgetModelFactoryService.getInstance()
						.getIcon((String) element);

				return CustomMediaFactory.getInstance().getImageFromPlugin(
						contributingPluginId, iconPath);
			}
		});

		modelsViewer.setInput(WidgetModelFactoryService.getInstance()
				.getWidgetTypes());

		modelsViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(
							final SelectionChangedEvent event) {
						IStructuredSelection sel = (IStructuredSelection) event
								.getSelection();
						String typeId = (String) sel.getFirstElement();
						DisplayModel model = getDisplayModelFromActiveEditor();

						AbstractWidgetModel newElement;
						int k = 0;

						for (int i = 0; i < ConnectionSettings.CREATE_TESTDUMMIES_COUNT; i++) {

							newElement = WidgetModelFactoryService
									.getInstance()
									.getWidgetModelFactory(typeId)
									.createWidgetModel();

							if (ConnectionSettings.CREATE_TESTDUMMIES_WITH_FILL) {
								if (newElement.getDoubleTestProperty() != null) {
									DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
									dynamicsDescriptor
											.addInputChannel(new ParameterDescriptor(
													(ConnectionSettings.FOR_DAL?"dal-epics":"local")+"://Random:"
															+ i
															+  (ConnectionSettings.FOR_DAL ? ""
																	: (" "+MultiThreadChannelPool.RANDOM_DOUBLE_MARKER)),
													Double.class));
									newElement.getProperty(
											newElement.getDoubleTestProperty())
											.setDynamicsDescriptor(
													dynamicsDescriptor);
									k++;
								}
							}

							if (ConnectionSettings.CREATE_TESTDUMMIES_WITH_COLOR) {
								if (newElement.getColorTestProperty() != null) {
									DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(
											"scriptedColor");
									dynamicsDescriptor
											.addInputChannel(new ParameterDescriptor(
													(ConnectionSettings.FOR_DAL?"dal-epics":"local")+"://Random:"
															+ k
															+ (ConnectionSettings.FOR_DAL ? ""
																	: ("?" + MultiThreadChannelPool.RANDOM_DOUBLE_MARKER)),
													Double.class));
									newElement.getProperty(
											newElement.getColorTestProperty())
											.setDynamicsDescriptor(
													dynamicsDescriptor);
									k++;

								}
							}

							if (newElement.getDoubleSeqTestProperty() != null) {
								DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
								dynamicsDescriptor
										.addInputChannel(new ParameterDescriptor(
												(ConnectionSettings.FOR_DAL?"dal-epics":"local")+"://Random:"
														+ k
														+ (ConnectionSettings.FOR_DAL ? ""
																: ("?" + MultiThreadChannelPool.RANDOM_DOUBLE_ARRAY_MARKER)),
												Double.class));
								newElement.getProperty(
										newElement.getDoubleSeqTestProperty())
										.setDynamicsDescriptor(
												dynamicsDescriptor);
								k++;

							}

							model.addWidget(newElement);

						}
						WidgetModelUtil.orderModel(model);
					}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	/**
	 * Tries to receive a display model from the currently active editor. If no
	 * corresponding editor is open an appropriate one is opened automatically.
	 * 
	 * @return a display model
	 */
	protected DisplayModel getDisplayModelFromActiveEditor() {

		DisplayModel model = null;

		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editor == null || !(editor instanceof DisplayEditor)) {
			openEmptyEditor();
			model = getDisplayModelFromActiveEditor();
		} else {
			model = ((IDisplayEditor) editor).getDisplayModel();
		}

		return model;
	}

	/**
	 * Opens an display editor with an empty display model.
	 */
	private void openEmptyEditor() {
		String query = "x." + DisplayEditor.SDS_FILE_EXTENSION; //$NON-NLS-1$

		// we need a dummy editor input...
		IEditorInput editorInput = new ControlSystemItemEditorInput(
				CentralItemFactory.createProcessVariable("x")); //$NON-NLS-1$

		IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(query);

		if (descriptor != null && editorInput != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				page.openEditor(editorInput, descriptor.getId());
			} catch (PartInitException e) {
				CentralLogger.getInstance()
						.error(this, "Cannot open editor", e); //$NON-NLS-1$
			}
		}
	}
}
