package org.csstudio.utility.nameSpaceSearch.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.nameSpaceSearch.Messages;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public class MainView extends ViewPart {
	public static final String ID = MainView.class.getName();
	private Text searchText;
	private TableViewer ergebnissTableView;
//	private int lastSort;
	private boolean lastSortBackward;
	private int[] sorts = {0,0,0};
	private Image up;
	private Image down;
	private HashMap<String, String> headline = new HashMap<String, String>();

	class myTableLabelProvider implements ITableLabelProvider{

		// No Image
		public Image getColumnImage(Object element, int columnIndex) {return null;}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ProcessVariable) {
				ProcessVariable pv = (ProcessVariable) element;
				return ""+pv.getPath()[columnIndex].split("=")[1];

			}
			if (element instanceof ArrayList) {
				Object o = ((ArrayList)element).get(columnIndex);
				if (o instanceof IControlSystemItem) {
					return ((IProcessVariable)o).getName();
				}
				return "AL: "+o.toString();
			}
			return "toStr: "+element.toString();

		}

		public void addListener(ILabelProviderListener listener) {}

		public void dispose() {}

		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {}

	}
	class myContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ArrayList) {
				return ((ArrayList)inputElement).toArray();
			}
			return (Object[])inputElement;
		}

		public void dispose() {		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {		}

	}

//	public MainView() {	}
	/***************************************************************************
	 *
	 * Make the Plugin UI
	 * - A Textfield for the Searchword [searchText]
	 * - A Pushbutton to start search [serachButton]
	 * - A resulttable to view the resulte [ergebnissTable]
	 *   - Header as Button to Sort the table
	 *   - D&D/MB3 function on a row
	 *
	 ***************************************************************************/

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2,false));
		up = new Image(parent.getDisplay(),"c://tmp//up.gif");
		down = new Image(parent.getDisplay(),"c://tmp//down.gif");
		searchText = makeSearchField(parent);

		Button serachButton = new Button(parent,SWT.PUSH);
		serachButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
		serachButton.setFont(new Font(parent.getDisplay(),"SimSun",10,SWT.NONE));
		serachButton.setText(Messages.getString("MainView_searchButton")); //$NON-NLS-1$

		ergebnissTableView = new TableViewer(parent,SWT.SINGLE|SWT.FULL_SELECTION);

		Table ergebnissTable = ergebnissTableView.getTable();
		ergebnissTable.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		ergebnissTable.setLinesVisible (true);
		ergebnissTable.setHeaderVisible (true);

		ergebnissTableView.setContentProvider(new myContentProvider());
		ergebnissTableView.setLabelProvider(new myTableLabelProvider());

		serachButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				search(ergebnissTableView, searchText.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {}

		});

		searchText.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.CR){
					search(ergebnissTableView, searchText.getText());
				}
			}

			public void keyPressed(KeyEvent e) {}

		});

		// Make Table row Drageble
		new ProcessVariableDragSource(ergebnissTableView.getControl(), ergebnissTableView);
		// MB3
		makeContextMenu();
		searchText.forceFocus();
		parent.update();
		parent.layout();
	}

		@Override
	public void setFocus() {
		searchText.forceFocus();
	}

	public void startSearch(String search){
		searchText.setText(search);
		search(ergebnissTableView, search);
	}

	/***************************************************************************
	 *
	 * @param search
	 * - Clear the resulttable
	 * - start a LDAP search
	 * - fill the  resulttable
	 *   - first step generate the tableheadbuttons for sort the table
	 *
	 ***************************************************************************/
	protected void search(final TableViewer ergebnissTable, String search) {
		ArrayList<IControlSystemItem> tableElements = new ArrayList<IControlSystemItem>();
		ergebnissTable.getTable().removeAll();
		ergebnissTable.getTable().clearAll();
		search = search.replaceAll("\\*\\**", "*");
		String filter = "eren="+search;
		if(search.compareTo("*")!=0)
			filter.concat("*");
		LDAPReader ldapr = new LDAPReader("ou=EpicsControls",filter); //$NON-NLS-1$ //$NON-NLS-2$
		if(headline.isEmpty()){
			headline.put("efan", Messages.getString("MainView_facility")); //$NON-NLS-1$ //$NON-NLS-2$
			headline.put("ecom", Messages.getString("MainView_ecom")); //$NON-NLS-1$ //$NON-NLS-2$
			headline.put("econ", Messages.getString("MainView_Controller")); //$NON-NLS-1$ //$NON-NLS-2$
			headline.put("eren", Messages.getString("MainView_Record")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String[] list = ldapr.getStringArray();

		// Versuch das Image auf die rechte Seite zu bekommen.
//		System.out.println(new File(".").getAbsoluteFile());
//		Listener paintListener = new Listener() {
//			public void handleEvent(Event event) {
//				System.out.println(event);
//				System.out.println(event.type);
//				switch(event.type) {
//					case SWT.MeasureItem: {
//						System.out.println("MeasureItem");
//						Rectangle rect = up.getBounds();
//						event.width += rect.width;
//						event.height = Math.max(event.height, rect.height + 2);
//						break;
//					}
//					case SWT.PaintItem: {
//						System.out.println("PaintItem");
//						int x = event.x + event.width;
//						Rectangle rect = up.getBounds();
//						int offset = Math.max(0, (event.height - rect.height) / 2);
//						event.gc.drawImage(up, x, event.y + offset);
//						break;
//					}
//					case SWT.Paint: {
//						System.out.println("Paint");
//						int x = event.x + event.width;
//						Rectangle rect = up.getBounds();
//						int offset = Math.max(0, (event.height - rect.height) / 2);
//						event.gc.drawImage(up, x, event.y + offset);
//						break;
//					}
////					case SWT.Selection:{
////						System.out.println("Selection");
////						int x = event.x + event.width;
////						Rectangle rect = down.getBounds();
////						int offset = Math.max(0, (event.height - rect.height) / 2);
////						event.gc.drawImage(down, x, event.y + offset);
////						break;
////					}
//
//				}
//			}
//		};

		for(int i=0;i<list.length;i++){
			String[] elements = list[i].split(","); //$NON-NLS-1$
			String path ="";
			for(int j=0;j<elements.length;j++){
				if(i==0&&j>=ergebnissTable.getTable().getColumnCount()){
//					lastSort = new int[elements.length-1];
					final TableColumn tc = new TableColumn(ergebnissTable.getTable(),SWT.NONE);
					tc.setResizable(true);
					tc.setWidth(ergebnissTable.getTable().getSize().x/4);
					tc.setToolTipText(Messages.getString("MainView_ToolTip_Sort"));
					tc.setMoveable(true);
					final int spalte = j;
//					tc.addListener(SWT.MeasureItem, paintListener);
//					tc.addListener(SWT.PaintItem, paintListener);
//					tc.addListener(SWT.Paint, paintListener);

					tc.addSelectionListener(new SelectionListener(){
						boolean backward = true;
						public void widgetDefaultSelected(SelectionEvent e) {}
						public void widgetSelected(SelectionEvent e) {
								backward=!backward;
								tc.setAlignment(SWT.LEFT);
								if(sorts[0]!=spalte){
									TableColumn[] chil = tc.getParent().getColumns();
									chil[sorts[1]].setImage(null);
									sorts[1]=sorts[0];
									lastSortBackward=backward;
									if(lastSortBackward)
										chil[sorts[1]].setImage(new Image(ergebnissTable.getTable().getDisplay(),"c://tmp//down_old.gif"));
									else
										chil[sorts[1]].setImage(new Image(ergebnissTable.getTable().getDisplay(),"c://tmp//up_old.gif"));
								}
								sorts[0]=spalte;
								ergebnissTable.setSorter(new TableSorter(sorts[0],backward,sorts[1], lastSortBackward));
								if(backward)
									tc.setImage(down);
								else
									tc.setImage(up);
								lastSortBackward=backward;
						}

					});
					String temp;
					if((temp=headline.get(elements[j].split("=")[0]))!=null) //$NON-NLS-1$
						 tc.setText(temp);
					else
						 tc.setText(elements[j].split("=")[0]);
				}
				path +=elements[j];
			}
//			System.out.println(path);
			tableElements.add(new ProcessVariable(elements[0].split("=")[1],elements));
		 }
//		ergebnissTableView.getTable().addListener(SWT.MeasureItem, paintListener);
//		ergebnissTableView.getTable().addListener(SWT.PaintItem, paintListener);
//		ergebnissTableView.getTable().addListener(SWT.Paint, paintListener);
//		ergebnissTableView.getTable().addListener(SWT.Selection, paintListener);
		 ergebnissTableView.setContentProvider(new myContentProvider());
		 ergebnissTableView.setLabelProvider(new myTableLabelProvider());
		 ergebnissTable.setInput(tableElements);
	}

	/*****************************************************************************
	 * Make the MB3-ContextMenu
	 *
	 */
	private void makeContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu");
		Control contr = ergebnissTableView.getControl();
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
					StructuredSelection s =  (StructuredSelection) ergebnissTableView.getSelection();
					Object o = s.getFirstElement();
					if (o instanceof ArrayList) {
						System.out.println("First is"+((ArrayList)o).get(0));

					}
					System.out.println("S= "+s);
//					list.getList().setSelection(e.y/list.getList().getItemHeight());
				}
			}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		getSite().registerContextMenu(manager, ergebnissTableView);
	}
	/***
	 *
	 * - Make the searchtext
	 *   - Layout
	 *   - Dropsource
	 *
	 * @return
	 */
	private Text makeSearchField(Composite parent) {
			searchText = new Text(parent,SWT.BORDER|SWT.SINGLE);
			searchText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
			searchText.setText("*"); //$NON-NLS-1$
			searchText.setToolTipText(Messages.getString("MainView_ToolTip"));

			//	 Eclipse
			int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		 	DropTarget target = new DropTarget(searchText, operations);

		 	// Receive data in Text or File format
		 	final TextTransfer textTransfer = TextTransfer.getInstance();
		 	Transfer[] types = new Transfer[] {textTransfer};
		 	target.setTransfer(types);

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
		 	           searchText.insert((String)event.data);
		 	        }
		 	    }
		 	});

		return searchText;
	}

}

