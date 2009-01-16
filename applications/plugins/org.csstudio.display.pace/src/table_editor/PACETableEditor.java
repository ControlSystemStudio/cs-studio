package table_editor;

import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.csstudio.display.pace.model.old.Rows;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import sun.security.jca.GetInstance;
import table_gui.LabelProvider;
import table_gui.ModelProvider;
import table_gui.SaveAllToElog;
import table_gui.SaveToElog;
import table_gui.TableGui;

/**
 * 
 * @author nypaver
 * TODO Get document name in title
 * TODO "save" means write changed PV values, make elog entry
 * TODO "Save" dialog layout: Title should grow with window width
 *
 *  
 * OK: Edit single value
 * OK: See original value
 * TODO "Restore original value" in context menu -> R.O.V. for sel. column in selected rows
 * TODO "Set multiple rows to same value"
 *       from context menu or by clicking column header (doesn't "save")
 *
 * TODO "save" (File/Save, Ctrl-S, Disk Icon, "Do you want to save..." -> Save all changes
 * TODO "Save Selected" in context menu -> Only save changes in selected rows
 * TODO Online help: Usage, Create config file/format of config file...
 */
public class PACETableEditor extends EditorPart {

   /** File that we're editing */
   private IFile file;
   final public static String ID = "table_editor/PACETableEditor"; //$NON-NLS-1$

   /** A real application would have a "model" that
    *  starts by reading the file, then allows changes,
    *  knows if something's changed, etc.
    *  In this example, the file is read directly into the "text"
    *  variable, and the remaining model info is simply: Did we change?
    */
   public boolean changed = false;
   /** GUI Elements.
    *  Ideally, the editor should just refer to a GUI class
    *  which handles the actual SWT elements,
    *  and not directly handle all the Label, Text, ... widgets in here.
    */
  
   public Model model = null;
   public TableGui tg = null;
   private String filename = "";
   Action saveAction, restoreAction;
   public String[] pvNames = new String[700];
   public int numPvsPosted;
   public TableViewer table_viewer;
   public String inputVal;
   public int inputCol;
   private String inVal="";
   public int curColSel;

   /** Called by framework to initialize the editor
    *  @param site Site on workbench where editor should show itself
    *  @param input Input
    */
   @Override
   public void init(IEditorSite site, IEditorInput input)
           throws PartInitException
   {
       // Have to call setSite, setInput with received arguments...
       setSite(site);
       file = (IFile) input.getAdapter(IFile.class);
       if (file != null)
           setInput(input);
       else
           throw new PartInitException("Cannot handle " + input.getName());

       try
       {
           model = new Model(file.getContents());
       }
       catch (Exception ex)
       {
           throw new PartInitException(ex.getMessage());
       }
       table_viewer = null;
  
       /*
        *  this sets the editor's title
        */
       setContentDescription(filename);
   }

   /** Called by framework when Editor is supposed to 'show'
    *  itself in given location on workbench
    *  @param parent Parent window
    */
   @Override
   public void createPartControl(final Composite parent)
   {
       if (model==null)
           return;
       
      tg = new TableGui();
      
      try
      {
         model.startAll();
      }
      catch (Exception ex)
      {
          // TODO show exception
      }
      // Create TableViewer that displays Model in Table
      // Some tweaks to the underlying table widget
      final Table table = new Table(parent,
            SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
      

      table.setHeaderVisible(true);
      table.setLinesVisible(true);

    // Create TableViewer that displays Model in Table
    table_viewer = new TableViewer(table);
    // Enable hashmap for resolving 'PVListEntry' to associated SWT widget.
   table_viewer.setUseHashlookup(true);
   
    // Add context menu to the name table.
    createActions();

    makeContextMenu(getSite());
      for(int i=0;i<model.getNumRows();i++)
         model.getRow(i).setModel(model);
      model.setTable(table_viewer.getTable());
      GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      gd.horizontalAlignment = SWT.FILL;
      gd.verticalAlignment = SWT.FILL;
      table.setLayoutData(gd);
      // Listen for mouse events to retrieve limit input values
      PACETableListener listener = new PACETableListener(this);
      table.addListener(SWT.MouseDown, listener);
      ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);
      myTableContent(table);
      table.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            table.dispose();    
       /* Close the view */
            IWorkbenchPage wbp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if(wbp!=null) 
            {
            wbp.hideView(wbp.findView("TableView.paceview"));
               try{
                  model.stopAll();
                }
                catch(Exception ex){
                   ex.printStackTrace();
                }
            }
         }
      });
      Listener sortListener = new Listener() {
         public void handleEvent(Event e) {
           TableColumn column = (TableColumn) e.widget;
           String cname = column.getText();
           curColSel = -1;
           for(int i=0;i<model.getNumColumns()&&curColSel==-1;i++)
           {
              if(cname.equals(model.getColumnName(i)))
                 curColSel=i;
           }
         }
       };
       for(int i=0;i<table.getColumnCount();i++)
          table.getColumn(i).addListener(SWT.Selection, sortListener);
       
      // Setting the item count causes the first 'refresh' of the table
      table_viewer.setItemCount(model.getNumRows());
      
      IWorkbench workbench = PlatformUI.getWorkbench();

            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();
            IPath location = root.getLocation();   
          //  System.out.println(location.toString());
         final int INTERVAL = 3000; //( ms)
         final Display d = workbench.getDisplay();
         d.timerExec (INTERVAL, new Runnable () {
            public void run () {
               if (d.isDisposed ()) return;
               refresh();
               d.update ();
               d.timerExec (INTERVAL, this);
            }
         });  
         
         /** Called by framework to initialize the editor
          *  @param site Site on workbench where editor should show itself
          *  @param input Input
          */
 
  

         // TODO: Create selectionProvider which provides the currently
         // selected PV as IProcessVariable
         // ISelectionProvider selectionProvider;
         /*
          *     public ISelection getSelection()
          *     {
                     ... somehow get name of selected PV
                    return new StructuredSelection(
                   CentralItemFactory.createProcessVariable(pv_name);
                }
          */
         // TODO   getSite().registerContextMenu(menuManager, selectionProvider);
      }

   private void fillContextMenu(IMenuManager mgr) {
      int cellNo = findCell();
      if(cellNo == -1) 
      { 
         mgr.setVisible(false);
         return;
      }
      Table table = table_viewer.getTable();
      // Find the selected column
      int curCol=-1;
      int selections[] = table.getSelectionIndices();
      Cell model_cell=null;
      Rows rows = null;
      
         for (int i = 0; i < selections.length && curCol==-1; i++) 
         {
            rows = model.getRow(selections[i]);
            model_cell = rows.getCell(curColSel);
            if(model.getColumn(curColSel).getAccess()==Cell.Access.ReadWrite &&
                  model_cell.hasUserValue())  curCol=curColSel;
         }
    
      if(curCol!=curColSel && model.getColumn(curColSel).getAccess()!=Cell.Access.ReadWrite)
         curCol=-1;
      clearPvs();
      setEditors();
      mgr.setVisible(true);
      if(curCol!=-1 && curCol == curColSel)
      {
        mgr.add(saveAction);
        mgr.add(restoreAction);
      }
      mgr.add(new Separator());
      
   }
   
   /** Add context menu.
    *  Basically empty, only contains MB_ADDITIONS to allow object contribs.
    *  <p>
    *  TODO: This doesn't work on all platforms.
    *  On Windows, the combo box already comes with a default context menu
    *  for cut/copy/paste/select all/...
    *  Sometimes you see the CSS context menu on right-click,
    *  and sometimes you don't.
    */
   private void makeContextMenu(IWorkbenchPartSite site)
   {
       // Add empty context menu so that other CSS apps can
      // See Plug-ins book p. 285
      MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
      manager.setRemoveAllWhenShown(true);
     
      manager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager mgr) {
                 fillContextMenu(mgr);
         }
 });
      // Other plug-ins can contribute there actions here
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      
      Menu menu = manager.createContextMenu(table_viewer.getControl());
      table_viewer.getControl().setMenu(menu);
    
      // TODO (or maybe not):
      // table_viewer is "SelectionProvider",
      // and it asks ModelProvider for currently selected items,
      // which in turn returns "Rows" object,
      // which are always IProcessVariable
      // (even if they return null in getName()...)
      // Instead:
      // Implement own SelectionProvider instead of passing table_viewer in here:
      // (and remember that getSelection() has to return IStructuredSelection)
      site.registerContextMenu(manager, table_viewer);
   }
   
   /**
    * Create the actions.
    */
   public void createActions() {
      final int numColumns = model.getNumColumns();
      // Find the selected column

      restoreAction = new Action("Restore Original Value") {
         public void run() { 
            Table table = table_viewer.getTable();
            Cell model_cell=null;
            Rows rows = null;
            int curCol= -1;
            int selections[] = table.getSelectionIndices();
            int c = curColSel;
               for (int i = 0; i < selections.length; i++) 
               {
                  rows = model.getRow(selections[i]);
                  model_cell = rows.getCell(c);
                  if(model.getColumn(c).getAccess()==Cell.Access.ReadWrite &&
                        model_cell.hasUserValue())  
                  {
                     curCol=c;
                     changed = false;
                     model_cell.setUserValue(null);
                     refresh();
                  }
               }
        
            if(curCol==-1)
            {
               MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", 
               "Please Enter a value in the selected column.");
               return;
            }
            
            anyDirty();
         }
      };

         saveAction = new Action("Save Selected") {
            public void run() { 
               Table table = table_viewer.getTable();
               int selections[] = table.getSelectionIndices();
               Cell model_cell=null;
               Rows rows = null;
               int curCol = -1;
               String curVal = "";
               
               for (int i = 0; i < selections.length && curCol==-1; i++) 
                  {
                     rows = model.getRow(selections[i]);
                     model_cell = rows.getCell(curColSel);
                     if(model.getColumn(curColSel).getAccess()==Cell.Access.ReadWrite &&
                           model_cell.hasUserValue())  
                        {
                           curCol=curColSel;
                           curVal=model_cell.getUserValue();
                        }
                  }
               for (int i = 0; i < selections.length; i++) 
               {
                  rows = model.getRow(selections[i]);
                  model_cell = rows.getCell(curColSel);
                  if(model.getColumn(curColSel).getAccess()==Cell.Access.ReadWrite &&
                        !model_cell.hasUserValue())  
                     {
                        model_cell.setUserValue(curVal);
                     }
               }
               if(curCol==-1)
               {
                  MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", 
                  "Please Enter a value in the selected column.");
                  return;
               }
               createCols(curCol, table);
            }
         };
   }

   /**
    * Save changes.
    */
   private void saveChanges() {
      doSave(null);
   }
   
   public static boolean activateWithPV(IProcessVariable pv_name)
   {
       new ShowTableAction();
       return false;
   }
   
   
   @Override
   public void setFocus()
   {
      table_viewer.getTable().setFocus();
   }

   /** Save current state of model.
    */
   @Override
   public void doSave(IProgressMonitor monitor)
   {
      Table table = table_viewer.getTable();
      SaveAllToElog dialog = new SaveAllToElog(tg.getShell(), model, table);
      dialog.open();
                  // Reset the model and GUI to indicate that the 'dirty' state
                  // changed (to 'clean').
      if(dialog.savedFlag())
      {
         clearTable();
      }
      //clearTable();
   }
   
   public void setFirePropertyChanged(int pid)
   {
      firePropertyChange(pid);
   }

   /** @return <code>true</code> if model changed (was edited) */
   @Override
   public boolean isDirty()
   {
       return changed;
   }
   
   /**
    * Are any cells dirty?
    */
   
   public boolean anyDirty()
   {
      Cell model_cell;
      Rows rows;
      int numRows = model.getNumRows();
      int numCols = model.getNumColumns();
      // find the edited column
   
      for(int i=0;i<numRows;i++)
      {
         rows = model.getRow(i);
         for(int c=0;c<numCols;c++)
         {
            if(rows.getCell(c).getAccess()==Cell.Access.ReadOnly) continue;
            model_cell = rows.getCell(c);
            if(model_cell.hasUserValue()) 
            {
               return true;
            }
         }
      }
      changed=false;
      firePropertyChange(PROP_DIRTY);
      return false;
   }

   /** @return <code>false</code> to suppress "Save As" */
   @Override
   public boolean isSaveAsAllowed()
   {
       return false;
   }

   /** Never invoked.
    *  @see #isSaveAsAllowed()
    */
   @Override
   public void doSaveAs()
   {
       // If supported, prompt for new file name,
       // then save changes in model to current input (file)
   }
   
   
   /** Refresh the table.  */
   public void refresh()
   {  if(table_viewer==null) return; 
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      if(shell==null) return;
      if(!shell.isDisposed() && !table_viewer.getTable().isDisposed()) table_viewer.refresh();
   }
   
   private void  myTableContent(final Table table) {
      
      // Connect TableViewer to the Model: Provide content from model...
      table_viewer.setContentProvider(
              new ModelProvider(table_viewer, model));
      final int numColumns = model.getNumColumns();
      System.out.println("NC "+ numColumns);
      // Create the columns of the table, using a fixed initial width.
      for (int c=0; c<numColumns; ++c)
      {
          final TableViewerColumn col =
              new TableViewerColumn(table_viewer, SWT.LEFT);
          final int curCol = c;
          col.getColumn().setText(model.getColumnName(c));

          col.getColumn().setMoveable(true);
          col.getColumn().setWidth(200);
       //   col.getColumn().notifyListeners(13, event)
          col.getColumn().addSelectionListener(new SelectionAdapter()
          {
             @Override
            public void widgetSelected(SelectionEvent e)
             {
               createMultiCols(curCol, table);
             }
          });
          
          // Tell column how to display the model elements
          col.setLabelProvider(new LabelProvider(c, model));
      }
   }      
   
   private void createCols(int curCol, final Table table)
   {
 
      int selections[] = table.getSelectionIndices();
      Cell model_cell=null;
      if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite && 
            selections.length>0)
      {
         if(table.getSelection().length==1)
         {
            Rows rows = model.getRow(selections[0]);
            int c = curCol;
    // This is the code updating the Table
               model_cell = rows.getCell(c);
               changed = model_cell.isChanged();
               if(changed==false && model_cell.hasUserValue()==false)
               {
                  MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", 
                  "Please Enter a value in the selected column.");
                  return;
               }
               if( model_cell.hasUserValue()) 
               {
                  firePropertyChange(IEditorPart.PROP_DIRTY);
                  inputCol = c;
                  inVal = model_cell.getUserValue();
                  SaveToElog dialog = new SaveToElog(tg.getShell(), model, table, inputCol, inVal);
                  dialog.open();
                     // Reset the model and GUI to indicate that the 'dirty' state
                     // changed (to 'clean').
                  if(dialog.savedFlag())
                  {
                     clearTable();
                  }
                  else return;
               }
            if(inputCol!=curCol) 
            {
               MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", 
               "Please enter a value in the selected column.");
               return;
           }    
        }

         else if(table.getSelection().length>1)
         {
            boolean noEntry = true;
            int numEntries = 0;
            for (int i = 0; i < selections.length; i++) 
            {   
               Rows rows = model.getRow(selections[i]);
               model_cell = rows.getCell(curCol);
               changed = model_cell.isChanged();
               if(changed==true && model_cell.hasUserValue()==true)
               {
                  noEntry = false;
                  numEntries++;
               }
            }
            if(noEntry && numEntries==0)
            {
               MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", 
               "Please enter a value in the selected column.");
               return;
            }    
            for (int i = 0; i < selections.length; i++) 
            {   
               Rows rows = model.getRow(selections[i]);
             //  for(int c = 1; c < model.getNumColumns();c++)
             //  {
       // Find the edited column
               int c = curCol;
                 model_cell = rows.getCell(c);
                 changed = model_cell.isChanged();

                 if( model_cell.hasUserValue()) 
                 {
                   firePropertyChange(IEditorPart.PROP_DIRTY);
                   inputCol = c;
                   inVal = model_cell.getUserValue();
                   /**
                    * Ask user for value.
                    */
               /*    if(numEntries>1)
                   {
                      inVal = tg.promptForValue(model, table_viewer,curCol, inVal);
                      if(inVal==null) return;
                      model_cell.setUserValue(inVal);
                   }*/
                   SaveToElog dialog = new SaveToElog(tg.getShell(), model, table, inputCol, inVal);
                   dialog.open();
                      // Reset the model and GUI to indicate that the 'dirty' state
                      // changed (to 'clean').
                   if(dialog.savedFlag())
                   {
                      clearTable();
                   }
                   else return;

                 }
               //  if(inVal.length()>0) break;
              // }
            }
        } 
      }
      else if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite &&
            table.getSelection().length<=0)
      {
         MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", "Please make a selection.");
         return;
     }
   }
   
   private void createMultiCols(int curCol, final Table table)
   {
 
                int selections[] = table.getSelectionIndices();
                Cell model_cell=null;
                if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite && 
                      selections.length>1)
                {
                      boolean noEntry = true;
                      int numEntries = 0;
                      inVal = tg.promptForValue(model, table_viewer,curCol, inVal);
                      if(inVal==null) return;

                      for (int i = 0; i < selections.length; i++) 
                      {   
                         Rows rows = model.getRow(selections[i]);
                         model_cell = rows.getCell(curCol);
                         model_cell.setUserValue(inVal);
                           changed = model_cell.isChanged();

                             firePropertyChange(IEditorPart.PROP_DIRTY);
                             inputCol = curCol;
                      }
                  } 
          
                else if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite &&
                      selections.length<=1)
                {
                   MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", "A multiple selection is required.");
                   return;
               }
   }
   
   int findCell()
   {
      Table table = model.table;
      Point pt = model.pt;
      if(pt==null) return -1;
      int index = table.getTopIndex();
      while (index < table.getItemCount()) {
        final TableItem item = table.getItem(index);
      
          for (int i = 1; i < table.getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
               curColSel = i;
              return 1;
            }
          }
          index ++;
      }
      return -1;
   }
   
   public void clearTable()
   {
      
      Table table = table_viewer.getTable();
      int numRows = model.getNumRows();
      int numCols = model.getNumColumns();
      Cell model_cell;
      for(int i=0;i<numRows;i++)
      {
         Rows rows = model.getRow(i);
         for(int c=0;c<numCols;c++)
         {
            if(rows.getCell(c).getAccess()==Cell.Access.ReadOnly) continue;
            model_cell = rows.getCell(c);
            if(model_cell.hasUserValue()) 
            {
               changed = false;
               model_cell.setUserValue(null);
               firePropertyChange(IEditorPart.PROP_DIRTY);
               refresh();
            }
        }
         for(int c=0;c<numCols;c++)
         {
            if(rows.getCell(c).getAccess()==Cell.Access.ReadOnly) continue;
            model_cell = rows.getCell(c);
         }
      }
   }
   
   public void addPvName(String name)
   {
      pvNames[numPvsPosted++] = name;
     // System.out.println(name + " np " + numPvsPosted);
   }
   
   public void clearPvs()
   {
      for(int i=0;i<numPvsPosted;i++)
      {
         pvNames[i]="";
      }
      numPvsPosted = 0;
   }
   
   public void setEditors()
   {
      int selections[] = table_viewer.getTable().getSelectionIndices();
      for (int i = 0; i < selections.length; i++) 
      {   
         Rows rows = model.getRow(selections[i]);
         rows.ted = this;
      }
   }
   
   public String getPvName(int index)
   {
      return pvNames[index];
   }
   
}

