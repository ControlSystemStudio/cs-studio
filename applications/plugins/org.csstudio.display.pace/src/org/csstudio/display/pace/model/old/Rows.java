package org.csstudio.display.pace.model.old;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;
import table_editor.PACETableEditor;

/**
 * Rows
 * <p>
 * Store Row and macro information.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class Rows extends PlatformObject implements IProcessVariable
{
   private String macroName;
   private String rowName;
   private Macros[] macros;
   private int numMacros;
   private Cell[] cells;
   public Model model;
   public PACETableEditor ted;
   
   /** Constructor
    *  @param cells An array of cells displayed on this row.
    *  
    *  Read and store cell information for this row.
    */
   public Rows(Cell cells[])
   {
      this.cells = cells;
   }

   /** @return The number of cells in this row. */
   public int getNumCells()
   {
      return cells.length;
   }
   
   /** @return The cell represented by the input number. 
   *   @param c Index of the cell.
   */
   public Cell getCell(int c)
   {
      return cells[c];
   }
   
   /** @return whether or not this cell is editable. */
   public boolean isEdited()
   {
      for(int i=0;i<getNumCells();i++)
        if(cells[i].hasUserValue()==true)
           return true;
      
      return false;
   }
  
   /** Create row of pvs from the input macro name. 
   *   @param rowname Name of the row.
   *   @param macname Macro string.
   */
   public Rows(String rowname, String macname)
   {
      macroName = macname;
      rowName = rowname;

      Element el;
      int mlen = 0;
      int end = 0;
      String txtVal = "";
      String mname="";
      String m = "";
      String mval = "";
      int start = 0;
      int eql = 0;
      
      // count the number of macros;
      numMacros= numChars('=',macroName);
      
      // initialize the macro array
      macros = new Macros[numMacros];
      
      mname = macroName;         
      mlen = mname.length();
      end = mname.indexOf(',');
      while(end!=-1)
      {
         txtVal=mname.substring(start,end);
         mname=mname.substring(end+1);

         // find the =
          eql = txtVal.indexOf('=');
          for(int i=0;i<numMacros;i++)
          {
             m=txtVal.substring(0,eql);
             mval=txtVal.substring(eql+1);
             macros[i] = new Macros(m, mval);
             if(txtVal.equals(mname)) txtVal="";
             else txtVal=mname;
             eql = txtVal.indexOf('=');
          }
          end = mname.indexOf(',');    
       }
   }
   
   /** @return The row name. */
   public String getRowName()
   {
      return rowName;
   }
   
  /** @return The macro string for this row. */
   public String getMacroStr()
   {
      return macroName;
   }
   
  /** @return The macro represented by the input index number.  
   *   @param macNum Index of the macro.
   */
   public Macros getMacro(int macNum)
   {
      return macros[macNum];
   }
   
  /** @return The instantiated macro string for this row. */
   public String getMacroName(int macNum)
   {
      return macros[macNum].getName();
   }
   
  /** @return The value of the macro for the input macro number.
   *   @param macNum Index of the macro.
   */
   public String getMacroVal(int macNum)
   {
      return macros[macNum].getVal();
   }
   
   /** @return The number of character occurrences in a string. 
    *  @param pattern Pattern searching for.
    *  @param str String being searched.
    */
   private int numChars(char pattern, String str)
   {
      int occurs = 0;
   
      for(int i = 0; i < str.length(); i++) {
        char next = str.charAt(i);
        if(next == pattern) {
          occurs++;
        }
      }
      return occurs;
   }
   
  /** @return The number of macros for this r // TODO Auto-generated method stubow. */
   public int getNumMacros()
   {
      return numMacros;
   }

public void setModel(Model model)
{
  this.model = model;
   
}

public String getName()
{
   String name = "";
   Table table = model.getTable();
   Point pt = model.getPt();
   int index = table.getTopIndex();
   while (index < table.getItemCount()) {
     final TableItem item = table.getItem(index);
   
       for (int i = 0; i < table.getColumnCount() && name.length()==0; i++) {
         Rectangle rect = item.getBounds(i);
         if (rect.contains(pt)) {
           final int column = i;
           final int row = index;
           name = model.getRow(row).getCell(column).getPvName();
         }
       }
       index ++;
   }
   return name;
}

public String getTypeId()
{
   return IProcessVariable.TYPE_ID;}

public Object getAdapter(Class adapter)
{
   return null;
}
}
