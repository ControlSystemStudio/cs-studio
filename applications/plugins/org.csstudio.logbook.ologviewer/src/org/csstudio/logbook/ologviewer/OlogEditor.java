package org.csstudio.logbook.ologviewer;

import static org.csstudio.logbook.ologviewer.SimpleOlogTableColumnDescriptor.Builder.createColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import edu.msu.nscl.olog.api.Log;

public class OlogEditor extends EditorPart {
	public OlogEditor() {
	}

	private static final String ID = "org.csstudio.logbook.ologviewer.ologeditor";

	private static OlogEditor ologEditor;
	private OlogTableWidget ologTableWidget;
	private OlogDetailWidget ologDetailWidget;

	private ComboViewer comboViewer;

	private Label lblSearch;

	private static class OlogInput implements IEditorInput {

		@Override
		public Object getAdapter(Class adapter) {
			return null;
		}

		@Override
		public String getToolTipText() {
			return "Olog Editor";
		}

		@Override
		public IPersistableElement getPersistable() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			return "Olog";
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public boolean exists() {
			return false;
		}

	}

	private final static OlogInput ologInput = new OlogInput();
	private static Collection<OlogTableColumnDescriptor> ologTableColumnDescriptors = new ArrayList<OlogTableColumnDescriptor>();
	private Label label;

	public static OlogEditor openOlogEditorInstance() {
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			ologEditor = (OlogEditor) page.openEditor(ologInput, ID);
		} catch (PartInitException e) {
			Activator.getLogger().severe(e.getMessage());
		}
		return ologEditor;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.csstudio.logbook.ologtable.columndescription");

			if (config.length == 0) {
				Activator
						.getLogger()
						.log(Level.INFO,
								"No configured client for ChannelFinder found: using default configuration");
			}

			if (config.length >= 1) {
				System.out.println(config.length);
				for (IConfigurationElement configurationElement : config) {
					CellLabelProvider cellLabelProvider = (CellLabelProvider) configurationElement
							.createExecutableExtension("cellLabelProvider");
					String text = configurationElement.getAttribute("text");
					String tooltip = configurationElement
							.getAttribute("tooltip");
					int weight = Integer.parseInt(configurationElement
							.getAttribute("weight"));
					ologTableColumnDescriptors.add(createColumn(text)
							.withToooltip(tooltip).withWeight(weight)
							.withCellLabelProvider(cellLabelProvider).build());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Activator
					.getLogger()
					.log(Level.SEVERE,
							"Could not retrieve configured client for ChannelFinder",
							e);
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		lblSearch = new Label(parent, SWT.NONE);
		lblSearch.setAlignment(SWT.CENTER);
		GridData gd_lblSearch = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblSearch.widthHint = 85;
		lblSearch.setLayoutData(gd_lblSearch);
		lblSearch.setText("Log Search:");

		comboViewer = new ComboViewer(parent, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		combo.setToolTipText("Space seperated search criterias, patterns may include * and ? wildcards");

		ComboHistoryHelper name_helper = new ComboHistoryHelper(Activator
				.getDefault().getDialogSettings(),
				"org.csstudio.logbook.ologviewer", combo, 20, true) {

			@Override
			public void newSelection(final String queryText) {
				try {
					ologTableWidget.setOlogQuery(new OlogQuery(queryText));
				} catch (Exception e) {
					Activator.getLogger().severe(e.getMessage());
				}
			}
		};

		ologTableWidget = new OlogTableWidget(parent, SWT.None);
		ologTableWidget
				.setTableViewerColumnDescriptors(ologTableColumnDescriptors);

		ologTableWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		GridLayout gridLayout_1 = (GridLayout) ologTableWidget.getLayout();
		gridLayout_1.numColumns = 2;

		ologTableWidget
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (event.getSelection() instanceof IStructuredSelection) {
							Object element = ((IStructuredSelection) event
									.getSelection()).getFirstElement();
							if (element instanceof Log) {
								ologDetailWidget.setLog((Log) element);
							}
						}
					}
				});

		label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));

		ologDetailWidget = new OlogDetailWidget(parent, SWT.None);
		ologDetailWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		PopupMenuUtil.installPopupForView(ologTableWidget, getSite(),
				ologTableWidget);
		PopupMenuUtil.installPopupForView(ologDetailWidget, getSite(),
				ologDetailWidget);

		PopupMenuUtil.installPopupForView(ologDetailWidget.propertyTree.tree,
				getSite(), ologDetailWidget.propertyTree.treeViewer);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
