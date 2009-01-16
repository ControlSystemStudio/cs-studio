package table_gui;

import org.csstudio.display.pace.Preferences;
import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.csstudio.display.pace.model.old.Rows;
import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.logbook.LogbookFactory;
import org.csstudio.logbook.sns.SNSLogbookFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * SaveAllToElog
 * <p>
 * Creates and displays an elog entry.  The dialog enables the user to 
 * edit the entry.  If the user press the OK button to send the entry to 
 * the elog or press the Cancel button to abort the transaction.
 * 
 * @author Delphy Nypaver Armstrong
*/
public class SaveAllToElog extends TitleAreaDialog {
/**
 * Runs the application
 */
   
//   public String filename;
   private Composite shell;
   private Model model;
   private Label uLbl, pLbl;
   private Text userID, passwd;
   private Text text;
   private String val;
   private int col;
   private static String LOGBOOK_NAME = "";
   private ILogbookFactory logbook_factory;
   private Table t;
   public boolean wasSaved = false;
   
   
   /** Constructor
    *  @param s Parent shell
    *  @param model2 Table model
    *  @param table Table being displayed
    *  @param column Edited column number
    *  @param value Input limit value
    *  
    *  GUI that prepares elog entry
    *
    *  @author Delphy Nypaver Armstrong
    */
   
public SaveAllToElog(final Composite s, final Model model2, final Table table) {
   super((Shell)s.getParent());
   shell = s;
   model = model2;
   t = table;
   setShellStyle(getShellStyle() | SWT.RESIZE);
}




/**
 * Gets the value in the user ID field.
 *
 * @return The value entered in the userID field.
 */
public String getUserID()
{
  return userID.getText().trim();
}

/**
 * Gets the value in the password field.
 *
 * @return The value entered in the password field.
 */
public String getPassword()
{
  return String.valueOf(passwd.getText()).trim();
}

/** 
 * Send the displayed text to the elog
 * 
 * @return <code>true</code> if OK 
 */
private boolean CRpressed(Composite shell, String summary, String inval)
{
   wasSaved = false;
   ILogbook logbook = null;
   while (logbook == null)
   {
       try
       {
          LOGBOOK_NAME = Preferences.getDefaultLogbook();
          logbook_factory = LogbookFactory.getInstance();
          logbook = logbook_factory.connect(LOGBOOK_NAME, getUserID(), getPassword());
           // If we get here, we connected
           break;
       }
       catch (Exception ex)
       {
          Shell s = PlatformUI.getWorkbench().getDisplay().getActiveShell();
          setErrorMessage("Cannot open logbook.");
          s.getDisplay().beep();
          wasSaved = false;
          return false;
       }
   }
   try
   {
      String title = model.getTitle();
      logbook.createEntry(title, summary, null);

   }
   catch (Exception ex)
   {
      ex.printStackTrace();
      MessageDialog.openError(null,"Error", ex.getMessage());
      wasSaved = false;
      return false;
   }
   finally
   {
       logbook.close();
   }
   wasSaved = true;
   return true;
}

public boolean savedFlag()
{
   return wasSaved;
}
/** Set the dialog title. */
@Override
protected void configureShell(Shell shell)
{
    super.configureShell(shell);
    shell.setText("Enter Log Data");
}

/** Create the GUI and display the elog entry. */
@Override
protected Control createDialogArea(final Composite parent)
{    
   // From peeking at super.createDialogArea we happen to expect a Compos.
   final Composite area = (Composite) super.createDialogArea(parent);
   
   GridData d = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
   Composite gridCmp = new Composite(area,0);
   GridLayout gLayout = new GridLayout();
   gLayout.marginTop = 20;
   gLayout.marginLeft = 140;

   gridCmp.setLayout(gLayout);
   Label info = new Label(gridCmp, SWT.CENTER);
   info.setText("Enter your User ID and Password."); 
  // d.widthHint = 230;
   info.setLayoutData(d);
 
   final Composite gridComp = new Composite(area, 0);
   GridData g = new GridData();
   g.grabExcessHorizontalSpace = true;
   g.grabExcessVerticalSpace = true;
   g.horizontalAlignment = SWT.FILL;
   g.verticalAlignment = SWT.FILL;
   gridComp.setLayoutData(g);
   
   GridLayout gridLayout = new GridLayout();
   gridLayout.numColumns = 2;
   gridLayout.marginLeft = 100;
   gridLayout.marginRight = 50;

   gridComp.setLayout(gridLayout);
   
   // Create the label and input box 
   uLbl = new Label(gridComp, SWT.LEFT);
   uLbl.setText("UID:");
   
   GridData data = new GridData();
   data.widthHint = 70;
   uLbl.setLayoutData(data);
   userID = new Text(gridComp, SWT.BORDER | SWT.SINGLE);

   GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
   userID.setLayoutData(data2);
   
   pLbl = new Label(gridComp, SWT.LEFT);
   pLbl.setText("Password:");
   pLbl.setLayoutData(data);
   passwd = new Text(gridComp, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
   passwd.setLayoutData(data2);

   Composite titleComp = new Composite(area,0);
   GridData td = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
   td.grabExcessHorizontalSpace = true;
   td.grabExcessVerticalSpace = true;
   td.horizontalAlignment = SWT.FILL;
   td.verticalAlignment = SWT.FILL;
   titleComp.setLayoutData(td);
   
   GridLayout gtd = new GridLayout();
   gtd.marginTop = 20;
   gtd.numColumns = 2;
   gtd.marginLeft = 100;
   gtd.marginRight = 50;

   titleComp.setLayout(gtd);
   
   Label titleLbl = new Label(titleComp, SWT.LEFT);
   titleLbl.setText("Title:"); 
   titleLbl.setLayoutData(data);
   

   Text titleID = new Text(titleComp, SWT.BORDER | SWT.SINGLE);
   titleID.setLayoutData(data2);
   titleID.setText(model.getTitle());
   
   d = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
   gridCmp = new Composite(area,0);
   gLayout = new GridLayout();
   gLayout.marginTop = 20;
   gLayout.marginLeft = 140;

   gridCmp.setLayout(gLayout);
   Label cmtLbl = new Label(gridCmp, SWT.CENTER);
   cmtLbl.setText("Enter your comment."); 
  // d.widthHint = 230;
   cmtLbl.setLayoutData(d);  
   
   // Put our widgets in another box to have own layout in there 
   final Composite box = new Composite(area, 0);
   GridData gd = new GridData();
   gd.grabExcessHorizontalSpace = true;
   gd.grabExcessVerticalSpace = true;
   gd.horizontalAlignment = SWT.FILL;
   gd.verticalAlignment = SWT.FILL;
   box.setLayoutData(gd);
   
   final GridLayout layout = new GridLayout();
   layout.numColumns = 2;
   box.setLayout(layout);
   
      text = new Text(box, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      gd = new GridData();
      gd.heightHint = 550; // Size guess. Hope that 'RESIZE' works as well.
      gd.widthHint = 400;
      gd.horizontalSpan = layout.numColumns;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      gd.horizontalAlignment = SWT.FILL;
      gd.verticalAlignment = SWT.FILL;
      
      text.setLayoutData(gd);   
 
   final Calendar now = Calendar.getInstance(); // get current Calendar object
   final Date nowDate = new Date(now.getTimeInMillis()); // result
   SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm:ss");// format
  
   String summary = "";
   String dateTm = "  Date/Time: " + formatter.format(nowDate) + "\n\n";  
  
    String pvName = "";
    String oldVal = "";
    String newVal = "";
    String inVal = "";
    Cell model_cell;
    Rows rows;
    summary = dateTm;
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
             inVal = model_cell.getUserValue();
             
// This is the code updating the elog
           pvName = "  PV: " + model_cell.getPvName() + "\n";
           oldVal = "  Old Value: " + model_cell.getCurrentValue() + "\n";
           newVal = "  New Value: " + inVal + "\n";
           summary = summary + pvName + oldVal + newVal + "\n";
         }
       }
    }
    
    text.setText(summary);
    
    setMessage("Enter your User ID and Password.\nMay also update the title and comment.");

    return area;
}

/** If no errors exist, call the method to send the displayed text to the elog. */
@Override
protected void okPressed()
{
      if(text.getText().trim().length()<=0)
      {
         setErrorMessage("A comment must be entered.");
         Shell s = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         s.getDisplay().beep();
         return;
      }
      else if(userID.getText().length()<=0 || passwd.getText().length()<=0)
      {
         setErrorMessage("A User ID and Password must be entered.");
         Shell s = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         s.getDisplay().beep();
         return;
      }
      else
      {
        try{
           String summary = text.getText();
           if (CRpressed(shell, summary, val))
              super.okPressed();
        }
        catch(Exception e){
           e.printStackTrace();
        }
      }
}

}

