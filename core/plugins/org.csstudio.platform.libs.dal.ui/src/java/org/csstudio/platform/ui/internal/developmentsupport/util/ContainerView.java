/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.ui.internal.developmentsupport.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.ui.workbench.IWorkbenchIds;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.IWorkbenchActionConstants;
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
	private static IProcessVariableAddress[] _samplePvAdresses;

	static {
		_sampleStrings = new String[] { "a", "b" };

		ProcessVariableAdressFactory f = ProcessVariableAdressFactory
				.getInstance();

		_samplePvAdresses = new IProcessVariableAddress[] {
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

	private List<IProcessVariableAddress> _modelForPvConsumer = new ArrayList<IProcessVariableAddress>();

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
		ProcessVariableExchangeUtil.addProcessVariableAddressDropSupport(tv
				.getControl(), DND.DROP_MOVE | DND.DROP_COPY,
				new IProcessVariableAdressReceiver() {
					public void receive(IProcessVariableAddress[] pvs,
							DropTargetEvent event) {
						for (IProcessVariableAddress pv : pvs) {
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
		ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(tv
				.getTree(), DND.DROP_MOVE | DND.DROP_COPY,
				new IProcessVariableAdressProvider() {
					public IProcessVariableAddress getPVAdress() {
						List<IProcessVariableAddress> l = getProcessVariableAdresses();
						if (l.size() > 0) {
							return l.get(0);
						}
						return null;
					}

					public List<IProcessVariableAddress> getProcessVariableAdresses() {
						IStructuredSelection sel = (IStructuredSelection) tv
								.getSelection();

						List<IProcessVariableAddress> list = (List<IProcessVariableAddress>) sel
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

		DragSource dragSource = new DragSource(tv.getControl(), DND.DROP_MOVE
				| DND.DROP_COPY);

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(new DragSourceAdapter() {

			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					IStructuredSelection sel = (IStructuredSelection) tv
							.getSelection();
					List<String> list = (List<String>) sel.toList();

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
