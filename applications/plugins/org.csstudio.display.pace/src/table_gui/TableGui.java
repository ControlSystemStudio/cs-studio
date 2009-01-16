package table_gui;

import java.util.List;

import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.csstudio.display.pace.model.old.Rows;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


   /**
    * SaveToElog
    * <p>
    *  GUI that displays the model content in a table.
    *  
    *  @author Kay Kasemir
    *  @author Delphy Nypaver Armstrong
    */
   public class TableGui
   {
       
       /** TableViewer that links model and Table widget */
       private TableViewer table_viewer;
       private Model model;
       private int selCol, selRow;
       public static String fname;
       public int inputCol;
       public TableEditor editor;
       
       public TableGui()
       {
  
       }
       
       /** Constructor
        *  @param s Parent shell
        *  @param model The Model to display
        *  
        *  Create GUI to display the model in a table
        */
       public TableGui(final Shell s, final Model model)
       {
           this.model = model;

           // Some tweaks to the underlying table widget
           final Table table = new Table(s,
                 SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
         
        // adding this composite puts buttons side by side
           final Composite composite = new Composite(s, SWT.NONE);
              GridLayout gridLayout = new GridLayout();
              gridLayout.numColumns = 1;
              composite.setLayout(gridLayout);

           
           final GridLayout layout = new GridLayout();
           layout.numColumns = 2;
           s.setLayout(layout);
           
           Composite cmp = new Composite(s, SWT.NO_FOCUS);
           RowLayout rowLayout = new RowLayout();
           rowLayout.pack = false;
           rowLayout.marginLeft = 450;
           rowLayout.marginTop = 5;
           rowLayout.marginRight = 5;
           rowLayout.marginBottom = 5;
           rowLayout.spacing = 15;

           cmp.setLayout(rowLayout);
 
         // Button to Save the changed limit values
           final Button savebtn = new Button(cmp, SWT.PUSH);
           savebtn.setText("Save");

           savebtn.addSelectionListener(new SelectionAdapter() {
             public void widgetSelected(SelectionEvent event) {
                int selections[] = table.getSelectionIndices();
                boolean changed = false;
                int curCol = -1;
                String curVal = "";
                Cell model_cell = null;
                int col = -1;
                if(table.getSelection().length==1)
                {
                   Rows rows = model.getRow(selections[0]);
                   for(int c = 0; c < model.getNumColumns() && changed == false;c++)
                   {
           // This is the code updating the Table
                      model_cell = rows.getCell(c);
                      if( model_cell.hasUserValue()) 
                      {
                         changed = true;
                         col = c;
                      }
                   
                   }
                   if(changed==true)
                   {
                      SaveToElog dialog = new SaveToElog(s, model, table, col, model_cell.getUserValue());
                      dialog.open();
                   }
                   else
                      MessageDialog.openInformation(s, "Nothing Entered", "Please enter a  value.");
                }
                else if(table.getSelection().length>1)
                {
                   for (int i = 0; i < selections.length && changed == false; i++) 
                   {   
                      Rows rows = model.getRow(selections[i]);
                      for(int c = 0; c < model.getNumColumns() && changed == false;c++)
                      {
              // Find the edited column
                        model_cell = rows.getCell(c);
                        if( model_cell.hasUserValue()) 
                        {
                          changed = true;
                          curCol = c;
                          curVal = model_cell.getUserValue();
                        }
                      }
                   }
                   if(changed==false)
                    MessageDialog.openInformation(s, "Nothing Entered", "Please enter a  value.");
                   else
                      new InputValueDialog(s, model, curCol, curVal, table);

                }
                 else
                 {
                    MessageDialog.openError(s, "Error", "Please make a selection.");
                 }
               }
           });
          
           // Button to close the table model display
           final Button button = new Button(cmp, SWT.PUSH);
           button.setText("Close");
           button.addSelectionListener(new SelectionAdapter() {
             public void widgetSelected(SelectionEvent event) {
                     try{
                       model.stopAll();
                     }
                     catch(Exception e){
                        e.printStackTrace();
                     }
                     s.getDisplay().dispose();
                 }
           });
           
           GridData gdat = new GridData(GridData.FILL_HORIZONTAL);
           cmp.setLayoutData(gdat);
                      
           // Create TableViewer that displays Model in Table
           table_viewer = new TableViewer(table);
           table.setHeaderVisible(true);
           table.setLinesVisible(true);
           GridData gd = new GridData();
           gd.grabExcessHorizontalSpace = true;
           gd.grabExcessVerticalSpace = true;
           gd.horizontalAlignment = SWT.FILL;
           gd.verticalAlignment = SWT.FILL;
           table.setLayoutData(gd);
           final TableEditor editor = new TableEditor(table);
           editor.horizontalAlignment = SWT.LEFT;
           editor.grabHorizontal = true;
           table.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event e) {
                String string = "";
                TableItem[] selection = table.getSelection();
               // for (int i = 0; i < selection.length; i++) {
               //    for (int j = 0; j < table.getColumnCount(); j++) {
                //       System.out.println(i + " " + j + " " +selection[i].getText(j));
                //   }
              // }
              }
            });
           // Listen for mouse events to retrieve limit input values
           table.addListener(SWT.MouseDown, new Listener() {
             public void handleEvent(Event event) {
               Rectangle clientArea = table.getClientArea();
               Point pt = new Point(event.x, event.y);
               int index = table.getTopIndex();
               while (index < table.getItemCount()) {
                 boolean visible = false;
                 final TableItem item = table.getItem(index);
                 for (int i = 0; i < table.getColumnCount(); i++) {
                   if(model.getColumn(i).getAccess()==Cell.Access.ReadOnly)
                      continue;
                   Rectangle rect = item.getBounds(i);
                   if (rect.contains(pt)) {
                     final int column = i;
                     final int row = index;
                     final Text text = new Text(table, SWT.NONE);
                     Listener textListener = new Listener() {
                       public void handleEvent(final Event e) {
                         switch (e.type) {
                         case SWT.FocusOut:
                        //    model.getRow(column).getCell(index);
                           if(model.getRow(row).getCell(column).hasUserValue()==false)
                            item.setText(column, text.getText());
                           text.dispose();
                           break;
                         case SWT.Traverse:
                           switch (e.detail) {
                           // press Enter in editable table cell
                           case SWT.TRAVERSE_RETURN:
                              model.getRow(row).getCell(column).setUserValue(text.getText());
                              table_viewer.refresh();
                           //FALL THROUGH
                           case SWT.TRAVERSE_ESCAPE:
                             text.dispose();
                             e.doit = false;
                           }
                           break;
                         }
                       }
                     };
                     text.addListener(SWT.FocusOut, textListener);
                     text.addListener(SWT.Traverse, textListener);
                     editor.setEditor(text, item, i);
                     text.setText(item.getText(i));
                     text.selectAll();
                     text.setFocus();
                     return;
                   }
                   if (!visible && rect.intersects(clientArea)) {
                     visible = true;
                   }
                 }
                 if (!visible)
                   return;
                 index++;
               }
             }
           });
           ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

           // Connect TableViewer to the Model: Provide content from model...
           table_viewer.setContentProvider(
                   new ModelProvider(table_viewer, model));
           int numColumns = model.getNumColumns();
           // Create the columns of the table, using a fixed initial width.
           for (int c=0; c<numColumns; ++c)
           {
               final TableViewerColumn col =
                   new TableViewerColumn(table_viewer, SWT.LEFT);
               final int curCol = c;
               col.getColumn().setText(model.getColumnName(c));
               col.getColumn().setMoveable(true);
               col.getColumn().setWidth(200);
               col.getColumn().addSelectionListener(new SelectionAdapter()
               {
                  @Override
                  public void widgetSelected(SelectionEvent e)
                  {
                     int selections[] = table.getSelectionIndices();
   
                     if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite && 
                           selections.length>0)
                     {
                        String curVal = "";
                        for (int i = 0; i < selections.length; i++) 
                        {   
                           Rows rows = model.getRow(selections[i]);
                           for(int c = 0; c < model.getNumColumns();c++)
                           {
                      // Find the edited column
                              final Cell model_cell = rows.getCell(curCol);
                              if( model_cell.hasUserValue()) 
                              {
                                 curVal = model_cell.getUserValue();
                              }
                            }
                        }
                        // display the limit input dialog
                        new InputValueDialog(s, model, curCol, curVal, table);
                     }
                     else if(model.getColumn(curCol).getAccess()==Cell.Access.ReadWrite &&
                           table.getSelection().length<=0)
                     {
                        MessageDialog.openError(s, "Error", "Please make a selection.");
                        return;
                    }
                  }
               });
               // Tell column how to display the model elements
               col.setLabelProvider(new LabelProvider(c, model));
           }
           // Setting the item count causes the first 'refresh' of the table
           table_viewer.setItemCount(model.getNumRows());
       }
       
    /** Refresh the table.  */
    public void refresh(Shell shell)
    {  
       if(!shell.isDisposed()) table_viewer.refresh();
    }
    
    public void setFilename(String filename)
    {
       fname = filename;
    }
    
    
    public String getFilename()
    {
       return fname;
    }
    
    public String promptForValue(Model model, TableViewer table_viewer, int curCol,
          String inVal) {
       int selections[] = table_viewer.getTable().getSelectionIndices();
       inputCol = curCol;
       int col = inputCol;
       boolean newVal = false;
       Cell model_cell = null;
       for(int i=0;i<selections.length;i++)
       {
          Rows rows = model.getRow(selections[i]);
          for(int c = 0; c < model.getNumColumns();c++)
          {
             model_cell = rows.getCell(c);
             if( model_cell.hasUserValue())
             {
                newVal = true;
                inVal = model_cell.getUserValue();
             }
             if(newVal==true) break;
          }
          if(newVal==true) break;
       }
  
       if( !newVal) 
       {
          Rows rows = model.getRow(selections[0]);
          model_cell = rows.getCell(curCol);
          inVal = model_cell.getCurrentValue();
       }
       
     //  if(selections.length == 1) return inVal;
       int vlen = inVal.length();
       if(vlen<=0) return inVal;
       String cn = model.getColumnName(col);
       int len = cn.length();
       int diff = 30 - len;
       String bl = " ";
       while(bl.length() <  diff) bl = bl + " ";
       String text = bl + cn;
       Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
       InputDialog dlg = new InputDialog(shell, 
          "Input Value for Selected Rows", text, inVal, new LengthValidator());
       if (dlg.open() != Window.OK)
          return null;
       return dlg.getValue();
    }
    
    public Shell getShell()
    {
       return PlatformUI.getWorkbench().getDisplay().getActiveShell();
    }
    
    public void createEditor(Table table)
    {
       editor = new TableEditor(table);
       editor.horizontalAlignment = SWT.LEFT;
       editor.grabHorizontal = true;
    }
    
    public void setEditItem(Text text, TableItem item, int column)
    {
       editor.setEditor(text, item, column);
    }
    
    
    /**
     * This class validates a String. It makes sure that the String is between 5 and 8
     * characters
     */
    class LengthValidator implements IInputValidator {
      /**
       * Validates the String. Returns null for no error, or an error message
       * 
       * @param newText the String to validate
       * @return String
       */
      public String isValid(String newText) {
        int len = newText.length();
        String msg = newText + " is an invalid number.";
        // Determine if input is empty
        if (len < 1) return "A value must be entered.";
        if (!validNumber(newText)) return msg;

        // Input must be OK
        return null;
      }
      
      public boolean validNumber(String aNumber) { 
         try { 
             Double.parseDouble(aNumber);  
         } catch(NumberFormatException exc) { 
             return false; 
         } 
         return true;
    }
  }
}
