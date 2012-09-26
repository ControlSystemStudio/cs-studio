package org.csstudio.sds.ui.internal.pvlistview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback.ValidationResult;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.csstudio.platform.simpledal.IValidationProcess;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.internal.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class PvListView extends ViewPart {

	public static final String VIEW_ID = "org.csstudio.sds.ui.internal.pvlistview.PvListView";

	private Map<IProcessVariableAddress, List<AbstractWidgetModel>> pvsToWidgets;
	private List<Map<IProcessVariableAddress, ValidationResult>> validationResults;

	private final List<IValidationProcess> currentValidationProcesses;

	private TreeViewer treeViewer;

	// Need to be removed when View is disposed
	private ISelectionListener pageSelectionListener;
	private IPartListener2 partListener;

	// Currently linked editor
	private DisplayEditor linkedDisplayEditor;

	public PvListView() {
		pvsToWidgets = new HashMap<IProcessVariableAddress, List<AbstractWidgetModel>>();
		validationResults = new ArrayList<Map<IProcessVariableAddress, ValidationResult>>();
		currentValidationProcesses = new ArrayList<IValidationProcess>();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		pageSelectionListener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				handleWorkbenchPageSelection(part, selection);
			}
		};
		site.getPage().addSelectionListener(pageSelectionListener);

		partListener = new WorkbenchPartAdapter() {
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				handleWorkbenchPartClosed(partRef);
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				handleWorkbenchPartActivated(partRef);
			}
		};
		site.getPage().addPartListener(partListener);

		super.init(site);
	}

	private void createToolbarButtons(List<ValidatePvsAction> validateActions) {
		if (validateActions.size() > 0) {
			ValidateAllPvsAction allValidationsAction = new ValidateAllPvsAction(
					validateActions);
			getViewSite().getActionBars().getToolBarManager()
					.add(allValidationsAction);
			allValidationsAction.setEnabled(true);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		final Tree pvTree = new Tree(parent, SWT.BORDER | SWT.V_SCROLL);
		pvTree.setHeaderVisible(true);
		pvTree.setLinesVisible(true);

		treeViewer = new TreeViewer(pvTree);

		final TreeColumn pvColumn = new TreeColumn(pvTree, SWT.LEFT);
		pvColumn.setText("Process Variable Addresses");
		pvColumn.setAlignment(SWT.LEFT);
		pvColumn.setWidth(170);

		pvTree.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				handleViewResized(pvTree, pvColumn);
			}

		});

		// Create a table column for each validation service
		List<IProcessVariableAddressValidationService> validationServices = SdsUiPlugin
				.getDefault()
				.getProcessVariableAddressValidationServiceTracker()
				.getServices();
		List<ValidatePvsAction> validateActions = new ArrayList<ValidatePvsAction>(
				validationServices.size());
		for (IProcessVariableAddressValidationService iProcessVariableAddressValidationService : validationServices) {
			HashMap<IProcessVariableAddress, ValidationResult> serviceValidations = new HashMap<IProcessVariableAddress, IProcessVariableAddressValidationCallback.ValidationResult>();
			validationResults.add(serviceValidations);
			createValidationColumn(pvTree,
					iProcessVariableAddressValidationService,
					serviceValidations);
			validateActions.add(new ValidatePvsAction(
					iProcessVariableAddressValidationService,
					serviceValidations, this));
		}
		createToolbarButtons(validateActions);

		treeViewer.setContentProvider(new PvTreeContentProvider());
		treeViewer.setLabelProvider(new PvTreeLabelProvider());

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelectionInPvList((IStructuredSelection) event
						.getSelection());
			}
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClickInPvList((IStructuredSelection) event
						.getSelection());
			}
		});
		showInitialEditorSelection();
		treeViewer.setInput(pvsToWidgets);
	}

	public void selectPv(IProcessVariableAddress pvAddress,
			DisplayEditor displayEditor) {
		assert pvAddress != null : "Precondition failed: pvAddress != null";
		showAllPVsFromEditor(displayEditor);
		treeViewer.setSelection(new StructuredSelection(pvAddress));
	}

	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(pageSelectionListener);
		getSite().getPage().removePartListener(partListener);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	private void clearPvMappings() {
		pvsToWidgets.clear();
		for (IValidationProcess currentValidation : currentValidationProcesses) {
			currentValidation.cancel();
		}
		currentValidationProcesses.clear();
	}

	private void fillPvToModelMap(AbstractWidgetModel model) {
		List<IProcessVariableAddress> allPvAdresses = model.getAllPvAdresses();
		for (IProcessVariableAddress iProcessVariableAddress : allPvAdresses) {
			List<AbstractWidgetModel> modelListForPv = pvsToWidgets
					.get(iProcessVariableAddress);
			if (modelListForPv == null) {
				modelListForPv = new ArrayList<AbstractWidgetModel>();
				pvsToWidgets.put(iProcessVariableAddress, modelListForPv);
			}
			modelListForPv.add(model);
		}
		if (model instanceof ContainerModel) {
			for (AbstractWidgetModel childModel : ((ContainerModel) model)
					.getWidgets()) {
				fillPvToModelMap(childModel);
			}
		}
	}

	private void showInitialEditorSelection() {
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		if (activeEditor instanceof DisplayEditor) {
			List<AbstractBaseEditPart> selectedEditParts = ((DisplayEditor) activeEditor)
					.getSelectedEditParts();
			if (!selectedEditParts.isEmpty()) {
				for (AbstractBaseEditPart abstractBaseEditPart : selectedEditParts) {
					fillPvToModelMap(abstractBaseEditPart.getWidgetModel());
				}
			} else {
				fillPvToModelMap(((DisplayEditor) activeEditor)
						.getDisplayModel());
			}
		}
	}

	private void showAllPVsFromEditor(DisplayEditor displayEditor) {
		clearPvMappings();
		fillPvToModelMap(displayEditor.getDisplayModel());
		treeViewer.setInput(pvsToWidgets);
	}

	private void createValidationColumn(
			Tree pvTree,
			final IProcessVariableAddressValidationService validationService,
			final Map<IProcessVariableAddress, ValidationResult> serviceValidations) {
		TreeColumn validationColumn = new TreeColumn(pvTree, SWT.RIGHT);
		validationColumn.setText(validationService.getServiceName());
		validationColumn.setToolTipText(validationService.getServiceName()
				+ ": " + validationService.getServiceDescription());
		validationColumn.setAlignment(SWT.CENTER);
		validationColumn.setWidth(40);

		validationColumn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleValidationAction(validationService, serviceValidations);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void handleValidationAction(
			final IProcessVariableAddressValidationService validationService,
			final Map<IProcessVariableAddress, ValidationResult> serviceValidations) {
		List<IProcessVariableAddress> pvAddresses = new ArrayList<IProcessVariableAddress>(
				pvsToWidgets.keySet());
		Collections.sort(pvAddresses,
				new Comparator<IProcessVariableAddress>() {
					@Override
					public int compare(IProcessVariableAddress o1,
							IProcessVariableAddress o2) {
						return o1.getProperty().compareTo(o2.getProperty());
					}
				});

		IValidationProcess validationProcess = validationService
				.validateProcessVariableAddresses(pvAddresses,
						new IProcessVariableAddressValidationCallback() {
							@Override
							public void onValidate(
									final IProcessVariableAddress pvAddress,
									ValidationResult result, String comment) {
								serviceValidations.put(pvAddress, result);
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										treeViewer.refresh(pvAddress, true);
									}
								});
							}
						});
		currentValidationProcesses.add(validationProcess);
	}

	private void handleViewResized(Tree pvTree, TreeColumn pvColumn) {
		int widthDelta = pvTree.getBounds().width;
		TreeColumn[] columns = pvTree.getColumns();
		for (int columnIndex = 1; columnIndex < columns.length; columnIndex++) {
			widthDelta -= columns[columnIndex].getWidth();
		}
		pvColumn.setWidth(widthDelta);
	}

	private void handleSelectionInPvList(IStructuredSelection selection) {
		if (selection != null) {
			List<?> selectedObjects = ((IStructuredSelection) selection)
					.toList();
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (activeEditor instanceof DisplayEditor
					&& selectedObjects.size() > 0) {
				Object selectedObject = selectedObjects.get(0);

				List<AbstractWidgetModel> selectedPvModels = Collections
						.emptyList();
				if (selectedObject instanceof AbstractWidgetModel) {
					selectedPvModels = Collections
							.singletonList((AbstractWidgetModel) selectedObject);
				} else if (selectedObject instanceof IProcessVariableAddress) {
					selectedPvModels = pvsToWidgets.get(selectedObject);
				}

				GraphicalViewer graphicalViewer = ((DisplayEditor) activeEditor)
						.getGraphicalViewer();
				Map<?, ?> editPartRegistry = graphicalViewer
						.getEditPartRegistry();
				if (!selectedPvModels.isEmpty()) {
					EditPart firstSelectedEditPart = (EditPart) editPartRegistry
							.get(selectedPvModels.get(0));
					if (firstSelectedEditPart != null) {
						graphicalViewer.select(firstSelectedEditPart);
						graphicalViewer.reveal(firstSelectedEditPart);
					}
				}
				for (int index = 1; index < selectedPvModels.size(); index++) {
					EditPart selectedEditPart = (EditPart) editPartRegistry
							.get(selectedPvModels.get(index));
					if (selectedEditPart != null) {
						graphicalViewer.appendSelection(selectedEditPart);
					}
				}
			}
		}
	}

	private void handleDoubleClickInPvList(IStructuredSelection selection) {
		if (selection != null) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof IProcessVariableAddress) {
				try {
					PvSearchView pvSearchView = (PvSearchView) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(PvSearchView.VIEW_ID);
					pvSearchView
							.searchFor(((IProcessVariableAddress) selectedObject)
									.getProperty());
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleWorkbenchPageSelection(IWorkbenchPart part,
			ISelection selection) {
		// we ignore our own selection or null selection
		if (part != PvListView.this && selection != null) {
			if (part.equals(linkedDisplayEditor)
					&& !linkedDisplayEditor.getDisplayModel().isLoading()) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;

				clearPvMappings();

				Object[] selectedObjects = structuredSelection.toArray();
				for (Object object : selectedObjects) {
					if (object instanceof AbstractBaseEditPart) {
						fillPvToModelMap(((AbstractBaseEditPart) object)
								.getWidgetModel());
					}
				}
				treeViewer.setInput(pvsToWidgets);
			}
		}
	}

	private void handleWorkbenchPartActivated(IWorkbenchPartReference partRef) {
		final IWorkbenchPart activatedPart = partRef.getPart(true);
		if (activatedPart instanceof DisplayEditor) {
			if (activatedPart != linkedDisplayEditor) {
				for (Map<IProcessVariableAddress, ValidationResult> serviceValidations : validationResults) {
					serviceValidations.clear();
				}
			}
			linkedDisplayEditor = (DisplayEditor) activatedPart;
			linkedDisplayEditor
					.addModelLoadedListener(new DisplayModelLoadAdapter() {
						public void onDisplayModelLoaded() {
							if (PvListView.this.linkedDisplayEditor
									.equals(activatedPart)) {
								showAllPVsFromEditor(linkedDisplayEditor);
							}
						};
					});
		}
	}

	private void handleWorkbenchPartClosed(IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == linkedDisplayEditor) {
			linkedDisplayEditor = null;
			clearPvMappings();
			treeViewer.setInput(pvsToWidgets);
		}
	}

	private class WorkbenchPartAdapter implements IPartListener2 {

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {

		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}
	}

	private class PvTreeContentProvider implements ITreeContentProvider {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean hasChildren(Object element) {
			boolean result = false;
			if (element instanceof IProcessVariableAddress) {
				result = pvsToWidgets.get(element).size() > 1;
			}
			return result;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			IProcessVariableAddress[] elements = pvsToWidgets.keySet().toArray(
					new IProcessVariableAddress[] {});
			Arrays.sort(elements, new Comparator<IProcessVariableAddress>() {
				@Override
				public int compare(IProcessVariableAddress pv1,
						IProcessVariableAddress pv2) {
					return pv1.getFullName().compareTo(pv2.getFullName());
				}
			});
			return elements;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			IProcessVariableAddress address = (IProcessVariableAddress) parentElement;
			return pvsToWidgets.get(address).toArray();
		}
	}

	private class PvTreeLabelProvider implements ITableLabelProvider {

		@Override
		public String getColumnText(final Object element, int columnIndex) {
			String result = null;
			if (columnIndex == 0) {
				result = element.toString();

				if (element instanceof AbstractWidgetModel) {
					result = ((AbstractWidgetModel) element).getName();
				} else if (element instanceof IProcessVariableAddress) {
					result = ((IProcessVariableAddress) element).getFullName();
					if (result.length() == 0) {
						result = "EMPTY PV";
					}
				}
			}
			return result;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			Image result = null;
			if (columnIndex > 0 && element instanceof IProcessVariableAddress) {
				Map<IProcessVariableAddress, ValidationResult> validations = validationResults
						.get(columnIndex - 1);
				if (validations.containsKey(element)) {
					switch (validations.get(element)) {
					case VALID:
						result = CustomMediaFactory.getInstance()
								.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
										"icons/validationIndicatorGreen.png");
						break;
					case INVALID:
						result = CustomMediaFactory.getInstance()
								.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
										"icons/validationIndicatorRed.png");
						break;
					case ARCHIVED:
						result = CustomMediaFactory.getInstance()
								.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
										"icons/validationIndicatorYellow.png");
						break;
					default:
						break;
					}
				} else {
					result = CustomMediaFactory.getInstance()
							.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
									"icons/validationIndicatorGrey.png");
				}
			}
			return result;
		}

	}
}
