/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.nameSpaceBrowser.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.nameSpaceBrowser.Messages;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat.Ereignis;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat.Zustand;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceResultList;
import org.csstudio.utility.namespace.utility.ProcessVariable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/***
 * 
 * @author Helge Rickens
 * 
 *         CSSView is a NameSpaceBrowser View-Element. Composed of - A Group with name of "Type" - A
 *         Filter: Textfield [filter] - A List of Elements - is the Element a IProcessVariable
 * 
 * 
 */
public class CSSView extends Composite implements Observer {

	class CSSLabelProvider implements ILabelProvider {

		public void addListener(final ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public Image getImage(final Object element) {
			return null;
		}

		public String getText(final Object element) {
			if (element instanceof IControlSystemItem) {
				final String[] name = ((IControlSystemItem) element).getName().split("[/ ]");
				return name[name.length - 1];
			} else
				return element.toString();
		}

		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}

		public void removeListener(final ILabelProviderListener listener) {
		}

	}
	private static final int JAVA_REG_EXP = 1; // FIXME (bknerr) : AntiPattern! Use Enum
	private static final int JAVA_REG_EXP_NO_CASE = 2;

	private static final int SIMPLE_WIN = 3;
	// UI elements
	private final Display _display;
	private final Composite parent;
	private Group group;
	private Text filter;

	private NameSpaceResultList _resultList;

	private TableViewer _tableViewer;
	
	private boolean _hasChild = false;
	private boolean haveFixFirst = true;
	private CSSView _child;
	private final Automat automat;
	private final NameSpace _nameSpace;
	private LinkedHashMap<String, ControlSystemItem> itemList;

	private int start = -1;
	private final IWorkbenchPartSite site;

	private String defaultPVFilter;
	private CSSViewParameter _cssParameter;
	private final String[] headlines;
	private final int level;

	private String _fixFirst;
	
	private long _id;

	public CSSView(
			final Composite parent, 
			final Automat automat, 
			final NameSpace nameSpace,
			final IWorkbenchPartSite site, 
			final String defaultFilter, 
			final String selection, 
			final String[] headlines,
			final int level,
			final NameSpaceResultList resultList) {
		this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
		
		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();
		makeListField(selection);
	}

	public CSSView(
			final Composite parent, 
			final Automat automat, 
			final NameSpace nameSpace,
			final IWorkbenchPartSite site, 
			final String defaultFilter, 
			final String selection, 
			final String[] headlines,
			final int level,
			final NameSpaceResultList resultList,
			final String fixFrist) {
		this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
		
		haveFixFirst = true;
		_fixFirst = fixFrist;

		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();
		makeListField(selection);
	}

	private CSSView(final Composite parent, final Automat automat, final NameSpace nameSpace,
			final IWorkbenchPartSite site, final String defaultFilter, final String[] headlines, final int level, NameSpaceResultList resultList) {
		super(parent, SWT.NONE);
		_display = parent.getDisplay();

		this.automat = automat;
		this._nameSpace = nameSpace;
		this.parent = parent;
		this.site = site;
		this.headlines = headlines;
		this.level = level;
		defaultPVFilter = defaultFilter;
		
		init();
		
		Random rand = new Random();
		_id = rand.nextLong();
		
		setObservable(resultList); // FIXME (bknerr) : Antipattern - herein this pointer is used although object is not completely constructed
	}

	@Override
	/**
	 *  dispose self and Children
	 */
	public void dispose() {
		if (_hasChild) {
			_hasChild = false;
			_child.dispose();
			while (!_child.isDisposed()) {
			}
		}

		((GridLayout) parent.getLayout()).numColumns--;
		super.dispose();
		parent.layout(false);

	}

	/*****************************
	 * 
	 * @param list
	 *            Fill the list with ProcessVariable or ControlSystemItem
	 * 
	 */
	private void fillItemList(final List<ControlSystemItem> list) {
		if (list == null)
			return;
		
		itemList = new LinkedHashMap<String, ControlSystemItem>();
		if (_cssParameter.newCSSView && haveFixFirst) {
			if (_fixFirst == null) {
				start = 0;
				itemList.put(Messages.getString("CSSView.All"), 
						     new ControlSystemItem(Messages.getString("CSSView.All"), _cssParameter.filter + "ALL,")); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (_fixFirst.trim().length() > 0) {
				itemList.put(_fixFirst, new ControlSystemItem(_fixFirst, _cssParameter.filter + _fixFirst)); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} else {
			filter.setText(defaultPVFilter);
		}
		boolean first = true;
		for (final ControlSystemItem row : list) { 
			
			// FIXME (bknerr) : just insert rwo (CSI) in map - no difference - instanceof checks still yield PV!!!
			if (row instanceof ProcessVariable) {
				final ProcessVariable pv = (ProcessVariable) row;
				itemList.put(pv.getName(), pv);
			} else {
				itemList.put(row.getName(), row);
			}
			
			if (first) {
				first = false;
				group.setText(headlines[level]);
			}
		}
	}

	public Group getGroup() {
		return group;
	}

	//    public org.eclipse.swt.widgets.List getList() {
	//        return listViewer.getTable();
	//    }
	public org.eclipse.swt.widgets.Table getList() {
		return _tableViewer.getTable();
	}

	/**
	 * @param selection
	 * @return CSSViewParameter
	 */
	private CSSViewParameter getParameter(final String selection) {
		return automat.goDown(Ereignis.DOWN, selection);
	}

	private void init() {
		// set Layout
		this.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		this.setLayout(new FillLayout());
		group = new Group(this, SWT.LINE_SOLID);
		group.setLayout(new GridLayout(1, false));

	}

	// make a new CSSView Children
	/**
	 * @param parameter
	 */
	protected void makeChild(final CSSViewParameter parameter) {
		parent.setRedraw(false);
		// Has a child, destroy it.
		if (_hasChild) {
			_child.dispose();
			while (!_child.isDisposed()) {
				;
			}
		}
		// make new Children

		((GridLayout) parent.getLayout()).numColumns++;
		// The first element is the "All" element
		//        if (listViewer.getList().getSelectionIndex() > start || _fixFirst != null) {
		if (_tableViewer.getTable().getSelectionIndex() > start || _fixFirst != null) {
			
			// FIXME (bknerr) : is difference between PC and CSI important here ?
			// only getPath is required which is the very same code !
			
			
//			if (itemList.get(_tableViewer.getSelection().toString().substring(1,
//					_tableViewer.getSelection().toString().length() - 1)) instanceof ProcessVariable) {

			String selectionString = _tableViewer.getSelection().toString();
			String substring = selectionString.substring(1,
					selectionString.length() - 1);
			
			ControlSystemItem controlSystemItem = itemList.get(substring);
			
			if (controlSystemItem instanceof ProcessVariable) {
				
				final ProcessVariable pv = (ProcessVariable) controlSystemItem;
				
				_child = new CSSView(parent, automat, _nameSpace, site, defaultPVFilter, pv.getPath(), headlines, level + 1, _resultList.getNew()); //$NON-NLS-1$

			} else {
				final ControlSystemItem csi = controlSystemItem;
				
				if (_fixFirst == null) {
					_child = 
						new CSSView(parent, automat, _nameSpace, site, defaultPVFilter, csi.getPath(), headlines, level + 1, _resultList.getNew()); //$NON-NLS-1$
				} else {
					_child = 
						new CSSView(parent, automat, _nameSpace, site, defaultPVFilter, csi.getPath(), headlines, level + 1, _resultList.getNew(), _fixFirst); //$NON-NLS-1$
				}

			}
		} else {
			
			Collection<ControlSystemItem> values = itemList.values();
			ControlSystemItem[] array = values.toArray(new ControlSystemItem[values.size()]);
			ControlSystemItem item = array[0];
			String path = item.getPath();
			String[] fields = path.split("=");
			String df = fields[0] + "=*,";
			
			//final String df = values.toArray(new ControlSystemItem[0])[1].getPath().split("=")[0] + "=*,"; //$NON-NLS-1$ //$NON-NLS-2$
			
			_child = new CSSView(parent, automat, _nameSpace, site, defaultPVFilter, df, headlines, level + 1, _resultList.getNew()); //$NON-NLS-1$
		}
		_hasChild = true;

		parent.setRedraw(true);
	}

	// Make the MB3 ContextMenu
	private void makeContextMenu() {
		final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		final Control contr = _tableViewer.getControl();
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		// contr.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseDown(MouseEvent e) {
		// super.mouseDown(e);
		// if (e.button == 3) {
		// list.getSelection();
		// //
		// list.getList().setSelection(e.y/list.getList().getItemHeight()+list.getList().getVerticalBar().getSelection());
		// }
		// }
		// });
		final Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		site.registerContextMenu(manager, _tableViewer);
	}

	private void makeFilterField() {
		filter = new Text(group, SWT.SINGLE | SWT.BORDER);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		filter.setToolTipText(Messages.getString("CSSView_ToolTip1"));
		// Eclipse
		final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		final DropTarget target = new DropTarget(filter, operations);

		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		target.setTransfer(new Transfer[] { textTransfer });

		target.addDropListener(new DropTargetListener() {
			public void dragEnter(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragLeave(final DropTargetEvent event) {
			}

			public void dragOperationChanged(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(final DropTargetEvent event) {
			}

			public void drop(final DropTargetEvent event) {
				if (textTransfer.isSupportedType(event.currentDataType)) {
					filter.insert((String) event.data);
				}
			}

			public void dropAccept(final DropTargetEvent event) {
			}

		});

		filter.addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
			}

			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					_tableViewer.setInput(new ArrayList<Object>(itemList.values()).toArray());
				} else if (e.keyCode == SWT.F1) {
					PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
				}
			}

		});

	}

	/**
	 * @param selection
	 * 
	 */
	private void makeListField(final String selection) {
		_cssParameter = getParameter(selection);

		final Zustand zu = automat.getZustand();
		if (zu == Zustand.RECORD) {
			//            listViewer = new ListViewer(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			_tableViewer = new TableViewer(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		} else {
			//            listViewer = new ListViewer(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
			_tableViewer = new TableViewer(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		}
		_tableViewer.getControl().getStyle();
		//        listViewer.getList().setSize(getClientArea().x - 100, getClientArea().y - 100);
		_tableViewer.getTable().setSize(getClientArea().x - 100, getClientArea().y - 100);
		//        listViewer.getList().addPaintListener(new PaintListener() {
		_tableViewer.getTable().addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				//                listViewer.getList().setSize(getSize().x - 16,
				_tableViewer.getTable().setSize(getSize().x - 16,
						getSize().y - (filter.getSize().y + 31));
			}
		});
		_tableViewer.setLabelProvider(new CSSLabelProvider());
		_tableViewer.setContentProvider(new ArrayContentProvider());
		_tableViewer.setSorter(new ViewerSorter());
		//        listViewer.getList().setToolTipText(Messages.getString("CSSView_ToolTip2"));
		_tableViewer.getTable().setToolTipText(Messages.getString("CSSView_ToolTip2"));

		_nameSpace.setName(_cssParameter.name);
		_nameSpace.setFilter(_cssParameter.filter);
		_fixFirst = _cssParameter.fixFirst;
		_nameSpace.setErgebnisListe(_resultList);
		_nameSpace.setSelection(selection);
		_nameSpace.start();

		//        listViewer.getList().addKeyListener(new KeyListener() {
		_tableViewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
			}

			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.F1) {
					PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
				}
			}
		});
	}

	/**
	 * @param serach1
	 *            this String a not modified (by no Casesensitivity toLowerCase)
	 * @param regExp
	 *            this String a modified
	 * @return true match search and regExp and false don´t match
	 */
	protected boolean searchString(final String search1, final String regExp) { 
		return searchString(search1, regExp, SIMPLE_WIN);
	}

	/**
	 * @param serach1
	 *            this String a not modified (by no Casesensitivity toLowerCase)
	 * @param regExp
	 *            this String a modified by searchType
	 * @param searchTyp
	 *            :
	 * 
	 *            <pre>
	 * SIMPLE_WIN: Search used only * and ? as Wildcard
	 * JAVA_REG_EXP: used String match
	 * JAVA_REG_EXP_NO_CASE: used String match after toLowerCase
	 * </pre>
	 * @return true match search and regExp and false don´t match
	 */

	protected boolean searchString(final String search1, final String regExp, final int searchTyp) { // FIXME (bknerr) : encapsulate somewhere else !
		switch (searchTyp) {
		case JAVA_REG_EXP:
			return search1.matches(regExp);
		case JAVA_REG_EXP_NO_CASE:
			return search1.toLowerCase().matches(regExp.toLowerCase());
		case SIMPLE_WIN:
		default:
			return search1.toLowerCase().matches(
					regExp.replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace(
							"?", ".?").toLowerCase()
							+ ".*");
		}

	}

	// Setzt den Defaultfilter für IProzessVariablen
	public void setDefaultFilter(final String defaultPVFilter) {
		this.defaultPVFilter = defaultPVFilter;
		if (!_cssParameter.newCSSView) {
			filter.setText(defaultPVFilter);
		}
		if (_hasChild) {
			_child.setDefaultFilter(defaultPVFilter);
		}
	}

	public void update(final Observable arg0, final Object arg1) {
		_display.syncExec(new Runnable() {
			public void run() {
				fillItemList(_resultList.getCSIResultList());
				workItemList();
			}
		});
	}

	private void workItemList() {
		// fill the List
		if (itemList != null && itemList.size() > 1) {
//			_tableViewer.setInput(new ArrayList<ControlSystemItem>(itemList.values()).toArray(new ControlSystemItem[0])); // FIXME (bknerr) : wtf?
			_tableViewer.setInput(new ArrayList<ControlSystemItem>(itemList.values()));
		}

		_tableViewer.addFilter(new ViewerFilter() {
			@Override
			public Object[] filter(final Viewer viewer, final Object parent, final Object[] elements) {
				
				final String search = filter.getText().trim();
				final ArrayList<Object> al = new ArrayList<Object>();
				for (final Object element : elements) {
					String name = ""; //$NON-NLS-1$
					if (element instanceof IControlSystemItem) {
						final String[] names = ((IControlSystemItem) element).getName().split("[/ ]");
						name = names[names.length - 1];
						// name= ((IControlSystemItem) element).getName();
					}
					if (search.length() == 0 || searchString(name, search)) {
						al.add(element);
					}
				}
				return al.toArray();
			}

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return true;
			}
		});

		final Zustand zu = automat.getZustand();
		if (zu != Zustand.RECORD) {
			_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(final SelectionChangedEvent event) {
					// TODO: Checken ob dadurch vermieden werden kann das ein Elemente zu häufig
					// angeklickt werden kann.
					if (_cssParameter.newCSSView) {
						parent.setEnabled(false);
						makeChild(_cssParameter);
						parent.setEnabled(true);
					}
				}
			});
		}

		parent.layout();
		parent.pack();

		// Make List Drageble
		//        new ProcessVariableDragSource(listViewer.getControl(), listViewer);
		//        new ProcessVariableDragSource(listViewer.getList(), listViewer);
		new ProcessVariableDragSource(_tableViewer.getTable(), _tableViewer);
		// MB3
		makeContextMenu();

	}

	/**
	 * Sets the observable entity (resultList) in this observer (CSSView) 
	 * and adds <code>this</code> object as observer to its list.
	 * 
	 * @param resultList the observable object for <code>this</code> observer
	 */
	public void setObservable(NameSpaceResultList resultList) {
		_resultList = resultList;
		_resultList.addObserver(this);
	}
}
