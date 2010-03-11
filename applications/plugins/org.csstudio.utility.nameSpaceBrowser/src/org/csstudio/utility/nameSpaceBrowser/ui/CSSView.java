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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
 *         CSSView is a NameSpaceBrowser View-Elemet. Composed of - A Grpup with name of "Type" - A
 *         Filter: Textfield [filter] - A List of Elemets - is the Elemet a IProzessVariuable
 * 
 * 
 */

public class CSSView extends Composite implements Observer {

	class myLabelProvider implements ILabelProvider {

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
	private static final int JAVA_REG_EXP = 1;
	private static final int JAVA_REG_EXP_NO_CASE = 2;

	private static final int SIMPLE_WIN = 3;
	// UI elements
	private final Display disp;
	private final Composite parent;
	private Group group;
	private Text filter;

	private final NameSpaceResultList ergebnisListe;
	//    private ListViewer listViewer;
	private TableViewer listViewer;
	private boolean haveChildern = false;
	private boolean haveFixFirst = true;
	private CSSView children;
	private final Automat automat;
	private final NameSpace nameSpace;
	private LinkedHashMap<String, ControlSystemItem> itemList;

	private int start = -1;
	private final IWorkbenchPartSite site;

	private String defaultPVFilter;
	private CSSViewParameter para;
	private final String[] headlines;
	private final int level;

	private String _fixFirst;

	public CSSView(final Composite parent, final Automat automat, final NameSpace nameSpace,
			final IWorkbenchPartSite site, final String defaultFilter, final String selection, final String[] headlines,
			final int level, final NameSpaceResultList resultList) {
		this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();

		//
		makeListField(selection);
	}

	public CSSView(final Composite parent, final Automat automat, final NameSpace nameSpace,
			final IWorkbenchPartSite site, final String defaultFilter, final String selection, final String[] headlines,
			final int level, final NameSpaceResultList resultList, final String fixFrist) {
		this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
		haveFixFirst = true;
		_fixFirst = fixFrist;
		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();
		//
		makeListField(selection);
	}

	private CSSView(final Composite parent, final Automat automat, final NameSpace nameSpace,
			final IWorkbenchPartSite site, final String defaultFilter, final String[] headlines, final int level,
			final NameSpaceResultList resultList) {
		super(parent, SWT.NONE);
		disp = parent.getDisplay();

		this.automat = automat;
		this.nameSpace = nameSpace;
		this.parent = parent;
		this.site = site;
		this.headlines = headlines;
		this.level = level;
		defaultPVFilter = defaultFilter;
		ergebnisListe = resultList;
		ergebnisListe.addObserver(this);
		init();
	}

	@Override
	/**
	 *  dispose self and Children
	 */
	public void dispose() {
		if (haveChildern) {
			haveChildern = false;
			children.dispose();
			while (!children.isDisposed()) {
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
		if (para.newCSSView && haveFixFirst) {
			if (_fixFirst == null) {
				start = 0;
				itemList
				.put(
						Messages.getString("CSSView.All"), new ControlSystemItem(Messages.getString("CSSView.All"), para.filter + "ALL,")); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (_fixFirst.trim().length() > 0) {
				itemList.put(_fixFirst, new ControlSystemItem(_fixFirst, para.filter + _fixFirst)); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} else {
			filter.setText(defaultPVFilter);
		}
		boolean first = true;
		for (final ControlSystemItem row : list) {
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
		System.out.println("nothing");
	}

	public Group getGroup() {
		return group;
	}

	//    public org.eclipse.swt.widgets.List getList() {
	//        return listViewer.getTable();
	//    }
	public org.eclipse.swt.widgets.Table getList() {
		return listViewer.getTable();
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
	protected void makeChildren(final CSSViewParameter parameter) {
		parent.setRedraw(false);
		// Have a Children, destroy it.
		if (haveChildern) {
			children.dispose();
			while (!children.isDisposed()) {
				;
			}
		}
		// make new Children

		((GridLayout) parent.getLayout()).numColumns++;
		// The first element is the "All" element
		//        if (listViewer.getList().getSelectionIndex() > start || _fixFirst != null) {
		if (listViewer.getTable().getSelectionIndex() > start || _fixFirst != null) {
			if (itemList.get(listViewer.getSelection().toString().substring(1,
					listViewer.getSelection().toString().length() - 1)) instanceof ProcessVariable) {
				final ProcessVariable pv = (ProcessVariable) itemList
				.get(listViewer.getSelection().toString().substring(1,
						listViewer.getSelection().toString().length() - 1));
				//				children = new CSSView(parent, automat, nameSpace,site,defaultPVFilter,pv.getPath()+",",headlines,level+1, ergebnisListe.getNew()); //$NON-NLS-1$
				children = new CSSView(parent, automat, nameSpace, site, defaultPVFilter, pv
						.getPath(), headlines, level + 1, ergebnisListe.getNew()); //$NON-NLS-1$
			} else {
				if (_fixFirst == null) {
					final ControlSystemItem csi = itemList.get(listViewer
							.getSelection().toString().substring(1,
									listViewer.getSelection().toString().length() - 1));
					//					children = new CSSView(parent, automat, nameSpace,site,defaultPVFilter,csi.getPath()+",",headlines,level+1,ergebnisListe.getNew()); //$NON-NLS-1$
					children = new CSSView(parent, automat, nameSpace, site, defaultPVFilter, csi
							.getPath(), headlines, level + 1, ergebnisListe.getNew()); //$NON-NLS-1$
				} else {
					final ControlSystemItem csi = itemList.get(listViewer
							.getSelection().toString().substring(1,
									listViewer.getSelection().toString().length() - 1));
					//					children = new CSSView(parent, automat, nameSpace,site,defaultPVFilter,csi.getPath()+",",headlines,level+1,ergebnisListe.getNew(),_fixFirst); //$NON-NLS-1$
					children = new CSSView(parent, automat, nameSpace, site, defaultPVFilter, csi
							.getPath(), headlines, level + 1, ergebnisListe.getNew(), _fixFirst); //$NON-NLS-1$
				}

			}
		} else {
			final String df = itemList.values().toArray(new ControlSystemItem[0])[1].getPath().split("=")[0] + "=*,"; //$NON-NLS-1$ //$NON-NLS-2$
			children = new CSSView(parent, automat, nameSpace, site, defaultPVFilter, df,
					headlines, level + 1, ergebnisListe.getNew()); //$NON-NLS-1$
		}
		haveChildern = true;

		parent.setRedraw(true);
	}

	// Make the MB3 ContextMenu
	private void makeContextMenu() {
		final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		final Control contr = listViewer.getControl();
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
		site.registerContextMenu(manager, listViewer);
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
					listViewer.setInput(new ArrayList<Object>(itemList.values()).toArray());
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
		para = getParameter(selection);

		final Zustand zu = automat.getZustand();
		if (zu == Zustand.RECORD) {
			//            listViewer = new ListViewer(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			listViewer = new TableViewer(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		} else {
			//            listViewer = new ListViewer(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
			listViewer = new TableViewer(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		}
		listViewer.getControl().getStyle();
		//        listViewer.getList().setSize(getClientArea().x - 100, getClientArea().y - 100);
		listViewer.getTable().setSize(getClientArea().x - 100, getClientArea().y - 100);
		//        listViewer.getList().addPaintListener(new PaintListener() {
		listViewer.getTable().addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				//                listViewer.getList().setSize(getSize().x - 16,
				listViewer.getTable().setSize(getSize().x - 16,
						getSize().y - (filter.getSize().y + 31));
			}
		});
		listViewer.setLabelProvider(new myLabelProvider());
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setSorter(new ViewerSorter());
		//        listViewer.getList().setToolTipText(Messages.getString("CSSView_ToolTip2"));
		listViewer.getTable().setToolTipText(Messages.getString("CSSView_ToolTip2"));

		nameSpace.setName(para.name);
		nameSpace.setFilter(para.filter);
		_fixFirst = para.fixFirst;
		nameSpace.setErgebnisListe(ergebnisListe);
		nameSpace.setSelection(selection);
		nameSpace.start();

		//        listViewer.getList().addKeyListener(new KeyListener() {
		listViewer.getTable().addKeyListener(new KeyListener() {
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

	protected boolean searchString(final String search1, final String regExp, final int searchTyp) {
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
		if (!para.newCSSView) {
			filter.setText(defaultPVFilter);
		}
		if (haveChildern) {
			children.setDefaultFilter(defaultPVFilter);
		}
	}

	public void update(final Observable arg0, final Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				fillItemList(ergebnisListe.getCSIResultList());
				workItemList();
			}
		});
	}

	private void workItemList() {
		// fill the List
		if (itemList != null) {
			listViewer.setInput(new ArrayList<ControlSystemItem>(itemList.values())
					.toArray(new ControlSystemItem[0]));
		}

		listViewer.addFilter(new ViewerFilter() {
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
			listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(final SelectionChangedEvent event) {
					// TODO: Checken ob dadurch vermieden werden kann das ein Elemente zu häufig
					// angeklickt werden kann.
					if (para.newCSSView) {
						parent.setEnabled(false);
						makeChildren(para);
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
		new ProcessVariableDragSource(listViewer.getTable(), listViewer);
		// MB3
		makeContextMenu();

	}
}
