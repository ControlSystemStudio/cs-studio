package table_editor;

import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;


public class PACETableListener implements Listener {
   private TableViewer table_viewer;
   private Model model;
   private PACETableEditor tabEditor;
   
   public PACETableListener(PACETableEditor ted)
   {
      tabEditor = ted;
      table_viewer = tabEditor.table_viewer;
      model = tabEditor.model;
   }
   public void handleEvent(Event event)
   {
         Table table = table_viewer.getTable();
         Rectangle clientArea = table.getClientArea();
         Point pt = new Point(event.x, event.y);
         int index = table.getTopIndex();
         while (index < table.getItemCount()) {
           boolean visible = false;
           final TableItem item = table.getItem(index);
           for (int i = 0; i < table.getColumnCount(); i++) {

             Rectangle rect = item.getBounds(i);
             if (rect.contains(pt)) {
                model.setPt(pt);
             }
                if(model.getColumn(i).getAccess()==Cell.Access.ReadOnly)
                   continue;
                if (rect.contains(pt)) {
                final PACETableEditor ted = new PACETableEditor();
               final int column = i;
               final int row = index;
               final Text text = new Text(table, SWT.NONE);
               Listener textListener = new Listener() {
                 public void handleEvent(final Event e) {
                   switch (e.type) {
  
                   case SWT.FocusOut:
                  //    model.getRow(column).getCell(index);
                     if((model.getRow(row).getCell(column).hasUserValue()==false) && 
                        (text.getText().compareTo(model.getRow(row).getCell(column).getCurrentValue())>0))
                     {
                        item.setText(column, text.getText());
                     }
                     text.dispose();
                     break;
                   case SWT.Traverse:
                     switch (e.detail) {
                     // press Enter in editable table cell
                     case SWT.TRAVERSE_RETURN:
                        final Display display = Display.getCurrent();
                        Cell model_cell = model.getRow(row).getCell(column);
                        String val = text.getText();
                        if(val.length()>0) {
                          model_cell.setUserValue(val);
                          tabEditor.curColSel = column;
                          tabEditor.changed = true;
                          tabEditor.setFirePropertyChanged(IEditorPart.PROP_DIRTY);
                        }
                        if(val.length()>0&& model_cell.hasUserValue()) {
                           item.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
                           model.getRow(row).getCell(column).setUserValue(text.getText());
                           tabEditor.inputCol = column;
                           tabEditor.inputVal = text.getText();
                           table_viewer.refresh();
                           tabEditor.changed = true;
                           tabEditor.setFirePropertyChanged(IEditorPart.PROP_DIRTY);
                        }
                     //FALL THROUGH
                     case SWT.TRAVERSE_ESCAPE:
                       text.dispose();
                       e.doit = false;
                     }
                     break;
                   }
                 }
               };
               tabEditor.curColSel = column;
               text.addListener(SWT.FocusOut, textListener);
               text.addListener(SWT.Traverse, textListener);
               tabEditor.tg.createEditor(table);
               tabEditor.tg.setEditItem(text, item, i);
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
   }

