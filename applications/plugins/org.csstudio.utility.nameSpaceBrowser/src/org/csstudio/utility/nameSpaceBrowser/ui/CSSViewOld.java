package org.csstudio.utility.nameSpaceBrowser.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

public class CSSViewOld extends Composite {

	private Composite parent;
	private Group group;
	private final ListViewer list;
	private boolean haveChildern = false;
	private CSSView children;
	private Automat auto;
	private HashMap<String, IControlSystemItem> itemList;
	private int start =-1;

	private final Hashtable<String, String> headline = new Hashtable<String, String>();
	private IWorkbenchPartSite site;
	private String defaultPVFilter;

	private CSSViewParameter para;
	private Text filter;

	public CSSViewOld(final Composite parent, Automat automat, IWorkbenchPartSite site, String defaultFilter) {
		this(parent, automat, site, defaultFilter, "ou=epicsControls"); //$NON-NLS-1$
	}

	public CSSViewOld(final Composite parent, Automat automat, IWorkbenchPartSite site , String defaultFilter, String selection) {
		super(parent, SWT.NONE);
		this.parent = parent;
		this.site = site;
		System.out.println("------------------------------------------------------------------------------");
		defaultPVFilter = defaultFilter;
		auto = automat;
		init();
		// Make a Textfield to Filter the list. Can text drop
		makeFilterField();
		//
	 	list = new ListViewer(group,SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
	 	System.out.println("Selection "+selection);
	 	String tmp= selection.split("=")[0];
 		try{
 			para = auto.event(Automat.Ereignis.valueOf(tmp),selection);
		}catch (IllegalArgumentException e) {
			para = auto.event(Automat.Ereignis.UNKNOWN,selection);
		}
			try{
				System.out.println("LDAP : "+para.name+" | "+para.filter);
				LDAPReader reader = new LDAPReader(para.name, para.filter);
				fillItemList(reader.getStringArray());
			}catch (IllegalArgumentException e) {
				System.out.println();
			}

			// fill the List
			if(para.newCSSView){
				start = 0;
				list.add(new ControlSystemItem("Alle",null));

			}
			else
				filter.setText(defaultPVFilter);
			if(itemList!=null){
				for (String name : itemList.keySet().toArray(new String[itemList.size()])) {
					if (itemList.get(name) instanceof ProcessVariable) {
						ProcessVariable new_name = (ProcessVariable) itemList.get(name);
						list.add(new_name);
					}
					else{
						list.add(itemList.get(name));
					}
				}
			}
	//		list.addFilter(new ViewerFilter() {
	//			@Override
	//			public boolean select(Viewer viewer, Object parentElement, Object element) {
	//				if(filter.getText().length()==0)
	//					return false;
	//				return false;
	//			}
	//   	    });
			list.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					makeChildren(para);
				}
			});
//		}catch (IllegalArgumentException e) {
//			System.out.println(e.getLocalizedMessage());
//		}

		parent.layout();
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
				String[] liste =itemList.keySet().toArray(new String[0]);
				if(e.keyCode==SWT.CR){
					ArrayList<String> ergebnisse = new ArrayList<String>();
					for(int i=0;i<liste.length;i++){
						if(liste[i].toLowerCase().matches(filter.getText().replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace("?", ".?").toLowerCase())){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
							ergebnisse.add(liste[i]);
						}
					}
					list.add(ergebnisse.toArray(new String[0]));
					parent.layout();
				}
			}
			public void keyPressed(KeyEvent e) {}

		});
	}

	private void fillItemList(String[] list) {
		if(list==null) return;
		itemList = new HashMap<String, IControlSystemItem>();
		for(int i=0;i<list.length;i++){
//			String [] s = new String[2];
			String [] s;
			String[] token = list[i].split(",");
//			s[0] = "";
//			s[1] = token[0].split("=")[1]; //$NON-NLS-1$
			s = token[0].split("="); //$NON-NLS-1$
			s[0] = "";
			if(s.length<2) break;
			for (int j = 0; j < token.length; j++) {
				s[0] +=token[j]+",";
			}
			if(s[0].split("=")[0].compareTo("eren")==0){
				itemList.put(s[1],new ProcessVariable(s[1], s[0]));
			}
			else{
				itemList.put(s[1],new ControlSystemItem(s[1], s[0]));
			}

			if(i==0){
				String temp;
				if(	(temp = headline.get(s[0].split("=")[0]))==null)
					group.setText(s[0]);
				else
					group.setText(temp);
			}
		}
	}

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
					children = new CSSView(parent, auto,site,defaultPVFilter,pv.getPath()); //$NON-NLS-1$
				}
				else{
					ControlSystemItem csi = (ControlSystemItem) itemList.get(list.getSelection().toString().substring(1, list.getSelection().toString().length()-1));
					children = new CSSView(parent, auto,site,defaultPVFilter,csi.getPath()); //$NON-NLS-1$
				}
			}
			else{
				String df = itemList.values().toArray(new ControlSystemItem[0])[0].getPath().split("=")[0]+"=*";
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
	public void dispose() {
		if(haveChildern){
			haveChildern =false;
			children.dispose();
			while(!children.isDisposed()){
			}
		}
//		auto.event(Automat.Ereignis.CONTROLLER, ""); //$NON-NLS-1$
		((GridLayout) parent.getLayout()).numColumns--;
		super.dispose();
		parent.layout(false);

	}

	private void makeContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu");
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
					list.getList().setSelection(e.y/list.getList().getItemHeight());
				}
			}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		site.registerContextMenu(manager, list);
	}
	public void setDefaultFilter(String defaultPVFilter) {
		this.defaultPVFilter = defaultPVFilter;
		if(!para.newCSSView)
			filter.setText(defaultPVFilter);
		if(haveChildern)
			children.setDefaultFilter(defaultPVFilter);


	}
}
