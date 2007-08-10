package org.csstudio.platform.ui.internal.developmentsupport.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.platform.internal.developmentsupport.util.DummyContentModelProvider;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.rfc.IPVAdressListProvider;
import org.csstudio.platform.model.rfc.IProcessVariableAdress;
import org.csstudio.platform.model.rfc.PvAdressFactory;
import org.csstudio.platform.ui.dnd.DnDUtil;
import org.csstudio.platform.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.platform.ui.dnd.rfc.PVTransfer;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableAdressDragSourceAdapter;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.ui.workbench.ControlSystemItemEditorInput;
import org.csstudio.platform.ui.workbench.IWorkbenchIds;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * A view, which is used for DnD demos.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ContainerView extends ViewPart {

	private static String[] _sampleStrings;
	private static IProcessVariableAdress[] _samplePvAdresses;

	static {
		_sampleStrings = new String[] { "a", "b" };

		PvAdressFactory f = PvAdressFactory.getInstance();

		_samplePvAdresses = new IProcessVariableAdress[] {
				f.createProcessVariableAdress("epics://cryo/pump1"),
				f.createProcessVariableAdress("epics://cryo/pump2"),
				f.createProcessVariableAdress("epics://cryo/pump3"),
				f.createProcessVariableAdress("tine://cryo/pump1"),
				f.createProcessVariableAdress("tine://cryo/pump2"),
				f.createProcessVariableAdress("tine://cryo/pump3"),
				f.createProcessVariableAdress("tango://cryo/pump1"),
				f.createProcessVariableAdress("tango://cryo/pump2"),
				f.createProcessVariableAdress("tango://cryo/pump3"),
				f.createProcessVariableAdress("dal-epics://cryo/pump1"),
				f.createProcessVariableAdress("dal-epics://cryo/pump2"),
				f.createProcessVariableAdress("dal-epics://cryo/pump3"),
				f.createProcessVariableAdress("dal-tine://cryo/pump1"),
				f.createProcessVariableAdress("dal-tine://cryo/pump2"),
				f.createProcessVariableAdress("dal-tine://cryo/pump3"),
				f.createProcessVariableAdress("dal-tango://cryo/pump1"),
				f.createProcessVariableAdress("dal-tango://cryo/pump2"),
				f.createProcessVariableAdress("dal-tango://cryo/pump3") };
	}
	/**
	 * The ID of this view as configured in the plugin manifest.
	 */
	public static final String ID = "org.csstudio.platform.developmentsupport.util.ui.ContainerView"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		createPvProviderTree(parent);
		createTextProviderTree(parent);
		createPvConsumerTree(parent);
	}

	private List<IProcessVariableAdress> _modelForPvConsumer=new ArrayList<IProcessVariableAdress>();
	
	private void createPvConsumerTree(final Composite parent) {
		final TreeViewer tv = new TreeViewer(parent);

		tv.setContentProvider(new BaseWorkbenchContentProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return ((List) element).toArray();
			}
		});

		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.setInput(_modelForPvConsumer);

		getViewSite().setSelectionProvider(tv);

		// add drag support
		ProcessVariableExchangeUtil.addProcessVariableDropSupport(tv.getControl(), DND.DROP_MOVE | DND.DROP_COPY, new IProcessVariableAdressReceiver(){
			public void receive(IProcessVariableAdress[] pvs, DropTargetEvent event) {
				for(IProcessVariableAdress pv : pvs) {
					_modelForPvConsumer.add(pv);
				}
				
				tv.refresh();
			}
		});

		// create context menu
		configureContextMenu(tv);
	}

	
	private void createPvProviderTree(final Composite parent) {
		final TreeViewer tv = new TreeViewer(parent);

		tv.setContentProvider(new BaseWorkbenchContentProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return (Object[]) element;
			}
		});

		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.setInput(_samplePvAdresses);

		getViewSite().setSelectionProvider(tv);

		// add drag support
		ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(
				tv.getTree(), DND.DROP_MOVE | DND.DROP_COPY,
				new IPVAdressListProvider() {
					public List<IProcessVariableAdress> getPVAdressList() {
						IStructuredSelection sel = (IStructuredSelection) tv
								.getSelection();
						List<IProcessVariableAdress> list = (List<IProcessVariableAdress>) sel
								.toList();
						return list;
					}
				});

		// create context menu
		configureContextMenu(tv);
	}

	private void createTextProviderTree(final Composite parent) {
		final TreeViewer tv = new TreeViewer(parent);

		tv.setContentProvider(new BaseWorkbenchContentProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return (Object[]) element;
			}
		});

		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.setInput(_sampleStrings);

		getViewSite().setSelectionProvider(tv);

		// add drag support
		// FIXME: nur Text-Support anbieten
		// ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(
		// pvAdressesTv.getTree(), DND.DROP_MOVE | DND.DROP_COPY, this);

		DragSource dragSource = new DragSource(tv.getControl(),
				DND.DROP_MOVE | DND.DROP_COPY);

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(new DragSourceAdapter() {

			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					IStructuredSelection sel = (IStructuredSelection) tv
							.getSelection();
					List<String> list = (List<String>) sel
							.toList();

					StringBuffer sb = new StringBuffer();

					for (String s : list) {
						sb.append(s);
						sb.append("\n"); //$NON-NLS-1$
					}
					event.data = sb.toString();
				}
			}

		});

		// create context menu
		configureContextMenu(tv);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Configures all listeners for the TreeViewer.
	 */
	private void configureContextMenu(TreeViewer tv) {
		MenuManager menuMgr = new MenuManager("", ID); //$NON-NLS-1$
		menuMgr.add(new GroupMarker(IWorkbenchIds.GROUP_CSS_MB3));
		menuMgr.add(new Separator());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		menuMgr.setRemoveAllWhenShown(true);

		Menu contextMenu = menuMgr.createContextMenu(tv.getTree());
		tv.getTree().setMenu(contextMenu);

		// Register viewer with site. This has to be done before making the
		// actions.
		getViewSite().registerContextMenu(menuMgr, tv);
		getViewSite().setSelectionProvider(tv);
	}

}
