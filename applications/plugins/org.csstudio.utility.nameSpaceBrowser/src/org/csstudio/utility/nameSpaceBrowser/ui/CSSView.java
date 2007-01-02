package org.csstudio.utility.nameSpaceBrowser.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.nameSpaceBrowser.Messages;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/***
 *
 * @author Helge Rickens
 *
 * CSSView is a NameSpaceBrowser View-Elemet.
 * Composed of
 *  - A Grpup with name of "Type"
 *  - A Filter: Textfield [filter]
 *  - A List of Elemets
 *    - is the Elemet a IProzessVariuable
 *
 *
 */

public class CSSView extends Composite {
	private Composite parent;
	private Group group;
	private final ListViewer list;
	private boolean haveChildern = false;
	private CSSView children;
	private Automat auto;
	private LinkedHashMap<String, ControlSystemItem> itemList;
	private int start =-1;

	private final Hashtable<String, String> headline = new Hashtable<String, String>();
	private IWorkbenchPartSite site;
	private String defaultPVFilter;

	private CSSViewParameter para;
	private Text filter;

	class myLabelProvider implements ILabelProvider{

		public Image getImage(Object element) {return null;}

		public String getText(Object element) {
 	    	  if (element instanceof IControlSystemItem) {
 	    		 return ((IControlSystemItem) element).getName();
 	    	  }else
 	    		  return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {}

		public void dispose() {}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {}

	}

	public CSSView(final Composite parent, Automat automat, IWorkbenchPartSite site, String defaultFilter) {
		this(parent, automat, site, defaultFilter, "ou=epicsControls"); //$NON-NLS-1$
	}

	public CSSView(final Composite parent, Automat automat, IWorkbenchPartSite site , String defaultFilter, String selection) {
		super(parent, SWT.NONE);
		this.parent = parent;
		this.site = site;
		defaultPVFilter = defaultFilter;
		auto = automat;
		init();
		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();
		//
	 	list = new ListViewer(group,SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
	 	list.getList().setSize(getClientArea().x-100, getClientArea().y-100);
	 	list.getList().addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) {
				list.getList().setSize(getSize().x-16, getSize().y-(filter.getSize().y+31));
			}
	 	});
	 	list.setLabelProvider(new myLabelProvider());
	 	list.setContentProvider(new ArrayContentProvider());
	 	list.getList().setToolTipText(Messages.getString("CSSView_ToolTip2"));

 		try{
 		 	String tmp= selection.split("=")[0]; //$NON-NLS-1$
 			para = auto.event(Automat.Ereignis.valueOf(tmp),selection);
		}catch (IllegalArgumentException e) {
			para = auto.event(Automat.Ereignis.UNKNOWN,selection);
		}
		try{
			LDAPReader reader = new LDAPReader(para.name, para.filter);
			fillItemList(reader.getStringArray());
		}catch (IllegalArgumentException e) {
			// TODO: Errorhandling
		}

		// fill the List
		if(itemList!=null){
			list.setInput(new ArrayList<ControlSystemItem>(itemList.values()).toArray(new ControlSystemItem[0]));
		}

		list.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return true;
			}
			@Override
			public Object[] filter(Viewer viewer, Object parent, Object[] elements){
				String search = filter.getText().trim();
				ArrayList<Object> al = new ArrayList<Object>();
				for (Object element : elements) {
					String name=""; //$NON-NLS-1$
					if (element instanceof IControlSystemItem)
						name= ((IControlSystemItem) element).getName();
					if(search.length()==0||name.toLowerCase().matches(search.replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace("?", ".?").toLowerCase())){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
						al.add(element);
					}
				}
				return al.toArray();
			}
   	    });

		list.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO: Checken dadurch vermieden werden kann elemente zu höfig anzuklicken
				parent.setEnabled(false);
				makeChildren(para);
				parent.setEnabled(true);
			}
		});

		parent.layout();
		parent.pack();

		// Make List Drageble
		new ProcessVariableDragSource(list.getControl(), list);
		// MB3
		makeContextMenu();

	}

	private void init() {
		// set Layout
		this.setLayoutData(new GridData(SWT.LEFT,SWT.FILL,false,true,1,1));
		this.setLayout(new FillLayout());
		group = new Group(this, SWT.LINE_SOLID);
		group.setLayout(new GridLayout(1,false));

		// Namend the Records
		headline.put("efan", Messages.getString("CSSView_Facility")); //$NON-NLS-1$ //$NON-NLS-2$
		headline.put("ecom", Messages.getString("CSSView_ecom")); //$NON-NLS-1$ //$NON-NLS-2$
		headline.put("econ", Messages.getString("CSSView_Controller")); //$NON-NLS-1$ //$NON-NLS-2$
		headline.put("eren", Messages.getString("CSSView_Record")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void makeFilterField() {
		filter = new Text(group,SWT.SINGLE|SWT.BORDER);
		filter.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
		filter.setToolTipText(Messages.getString("CSSView_ToolTip1"));
//		 Eclipse
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	 	DropTarget target = new DropTarget(filter, operations);

	 	// Receive data in Text or File format
	 	final TextTransfer textTransfer = TextTransfer.getInstance();
	 	target.setTransfer(new Transfer[] {textTransfer});

	 	target.addDropListener(new DropTargetListener() {
	 	  public void dragEnter(DropTargetEvent event) {
	 	     if (event.detail == DND.DROP_DEFAULT) {
	 	         if ((event.operations & DND.DROP_COPY) != 0) {
	 	             event.detail = DND.DROP_COPY;
	 	         } else {
	 	             event.detail = DND.DROP_NONE;
	 	         }
	 	     }
	 	   }
	 	   public void dragOver(DropTargetEvent event) {}
	 	   public void dragOperationChanged(DropTargetEvent event) {
	 	        if (event.detail == DND.DROP_DEFAULT) {
	 	            if ((event.operations & DND.DROP_COPY) != 0) {
	 	                event.detail = DND.DROP_COPY;
	 	            } else {
	 	                event.detail = DND.DROP_NONE;
	 	            }
	 	        }
	 	    }
	 	    public void dragLeave(DropTargetEvent event) {	    }
	 	    public void dropAccept(DropTargetEvent event) {	    }
	 	    public void drop(DropTargetEvent event) {
	 	        if (textTransfer.isSupportedType(event.currentDataType)) {
	 	           filter.insert((String)event.data);
	 	        }
	 	    }

	 	});

		filter.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.CR){
					list.setInput(new ArrayList<Object>(itemList.values()).toArray());
				}
			}
			public void keyPressed(KeyEvent e) {}

		});
	}

	/*****************************
	 *
	 * @param list
	 * Fill the list with ProcessVariable or ControlSystemItem
	 *
	 */
	private void fillItemList(String[] list) {
		if(list==null) return;
		itemList = new LinkedHashMap<String, ControlSystemItem>();
		if(para.newCSSView){
			start = 0;
			itemList.put(Messages.getString("CSSView.All"), new ControlSystemItem(Messages.getString("CSSView.All"),null)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
			filter.setText(defaultPVFilter);

		for(int i=0;i<list.length;i++){
			String saubereListe = list[i];
			// Delete "-Chars that add from LDAP-Reader when the result contains special character
			if(saubereListe.startsWith("\"")){ //$NON-NLS-1$
				if(saubereListe.endsWith("\"")) //$NON-NLS-1$
					saubereListe = saubereListe.substring(1,saubereListe.length()-1);
				else
					saubereListe = saubereListe.substring(1);
			}
			String[] token = saubereListe.split("[,=]"); //$NON-NLS-1$

			if(token.length<2) {System.out.println(Messages.getString("CSSView.Error1")+list[i]+"'");break;} //$NON-NLS-1$ //$NON-NLS-2$

			if(token[0].compareTo("eren")==0){ //$NON-NLS-1$
				itemList.put(token[1],new ProcessVariable(token[1], saubereListe));
			}
			else{
				itemList.put(token[1],new ControlSystemItem(token[1], saubereListe));
			}

			if(i==0){
				String temp;
				if(	(temp = headline.get(token[0]))==null)
					group.setText(saubereListe);
				else {
					group.setText(temp);
				}
			}
		}
	}

	// make a new CSSView Children
	protected void makeChildren(CSSViewParameter parameter) {
		parent.setRedraw(false);

//		Have a Children, destroy it.
		if(haveChildern){
			children.dispose();
			while(!children.isDisposed()){;}
		}
//		make new Children
		if(parameter.newCSSView){
			((GridLayout) parent.getLayout()).numColumns++;
//			The first element is the "All" element
			if(list.getList().getSelectionIndex()>start){
				// if instanceof ProcessVariable
				if (itemList.get(list.getSelection().toString().substring(1, list.getSelection().toString().length()-1)) instanceof ProcessVariable) {
					ProcessVariable pv = (ProcessVariable) itemList.get(list.getSelection().toString().substring(1, list.getSelection().toString().length()-1));
					children = new CSSView(parent, auto,site,defaultPVFilter,pv.getPath()+","); //$NON-NLS-1$
				}
				else{
					ControlSystemItem csi = (ControlSystemItem) itemList.get(list.getSelection().toString().substring(1, list.getSelection().toString().length()-1));
					children = new CSSView(parent, auto,site,defaultPVFilter,csi.getPath()+","); //$NON-NLS-1$
				}
			}
			else{
				String df = itemList.values().toArray(new ControlSystemItem[0])[1].getPath().split("=")[0]+"=*"; //$NON-NLS-1$ //$NON-NLS-2$
				children = new CSSView(parent, auto, site,defaultPVFilter,df); //$NON-NLS-1$
			}
			haveChildern=true;
		}
		parent.setRedraw(true);
	}

	public Group getGroup(){
		return group;
	}

	public org.eclipse.swt.widgets.List getList(){
		return list.getList();
	}

	@Override
	// dispose self and Children
	public void dispose() {
		if(haveChildern){
			haveChildern =false;
			children.dispose();
			while(!children.isDisposed()){
			}
		}

		((GridLayout) parent.getLayout()).numColumns--;
		super.dispose();
		parent.layout(false);

	}

	// Make the MB3 ContextMenu
	private void makeContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		Control contr = list.getControl();
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		contr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				if (e.button == 3) {
					list.getList().setSelection(e.y/list.getList().getItemHeight()+list.getList().getVerticalBar().getSelection());
				}
			}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		site.registerContextMenu(manager, list);
	}

	// Setzt den Defaultfilter für IProzessVariablen
	public void setDefaultFilter(String defaultPVFilter) {
		this.defaultPVFilter = defaultPVFilter;
		if(!para.newCSSView)
			filter.setText(defaultPVFilter);
		if(haveChildern)
			children.setDefaultFilter(defaultPVFilter);


	}
}
