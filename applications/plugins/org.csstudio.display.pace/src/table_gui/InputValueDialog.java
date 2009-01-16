package table_gui;

import org.csstudio.display.pace.model.old.Model;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;


/**
 * InputValueDialog
 * <p>
 * Displays the input dialog for inputting the limit value for multiple selections.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class InputValueDialog extends Dialog
{
   private Shell shell;
   private Model model;
   private Double value;
   
   /** Constructor
    *  @param s Parent shell
    *  @param m Table model
    *  @param col Edited column number
    *  @param val Input limit value
    *  @param t Table being displayed
    *  
    *  Create the dialog for inputting the limit value to be applied to the 
    *  pv represented in the selected columns.  Send the new values to channel
    *  access and make an elog entry.
    */
   
   public InputValueDialog(final Shell s, Model m, final int col, final String val, final Table t) 
   {
      super(s);

      Display display = s.getDisplay();
      model = m;
      
      
      final Shell shell =
        new Shell(display, SWT.TITLE | SWT.DIALOG_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
      shell.setLayout(new FormLayout());
      shell.setText("Input Value for Selected Rows");

      //Fill Layout panel
      Composite fillComp = new Composite(shell, SWT.NONE);
      fillComp.setLayout(new FillLayout(SWT.VERTICAL));

    //Grid Layout panel
      Composite btnComp = new Composite(fillComp, SWT.NONE);
      RowLayout btnLayout = new RowLayout();
      btnLayout.spacing = 13;
      btnLayout.pack = false;
      btnLayout.marginRight = 30;

      btnLayout.marginBottom = 20;
      btnLayout.marginTop = 30;
      btnComp.setLayout(btnLayout);
      
      String cn = model.getColumnName(col);
      int len = cn.length();
      int diff = 30 - len;
      String bl = " ";
      while(bl.length() <  diff) bl = bl + " ";;
      
      Label colName = new Label(btnComp, SWT.NULL);
      colName.setText(bl + cn);
      final Text limitVal = new Text(btnComp, SWT.BORDER | SWT.SINGLE);
      limitVal.setText(val);
      
      //Row Layout panel
      Composite rowComp = new Composite(fillComp, SWT.NONE);
      RowLayout rowLayout = new RowLayout();
      rowLayout.pack = false;
      rowLayout.spacing = 7;
      rowLayout.marginLeft = 120;

      rowComp.setLayout(rowLayout);
         
      // Button to send the input limits to the elog
      final Button buttonOK = new Button(rowComp, SWT.PUSH);
      buttonOK.setText("Ok");
      buttonOK.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            String inval = limitVal.getText();
            if(inval.length() == 0)
               MessageDialog.openInformation(s, "Nothing Entered", "Please enter a  value.");
            else
            {
               SaveToElog dialog = new SaveToElog(s, model, t, col, inval);
               shell.dispose();
               dialog.open();
            }
           }
       });

      // Button to cancel the limit change transaction
      Button buttonCancel = new Button(rowComp, SWT.PUSH);
      buttonCancel.setText("Cancel");
      buttonCancel.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            shell.dispose();
            return;
           }
       });
      
      shell.pack();
      shell.open();
   }
   
}
