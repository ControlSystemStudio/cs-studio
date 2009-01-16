
package org.csstudio.display.pace.model.old;

import org.csstudio.display.pace.model.old.Cell.Access;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ColumnInfo
 * <p>
 * Create arrays of columns read from the xml configuration file.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class ColumnInfo
{
   private int numCols = 0;
   private Columns[] cols;
   /** Constructor
    *  @param fstNode Column information from the xml file
    *  
    *  Read and store column information read from the xml file.
    */
   public void createColumns(Node fstNode)
   {
      final Element fstElmnt = (Element) fstNode;
      final NodeList fstElmntLst = fstElmnt.getElementsByTagName("name");
      final NodeList accessLst = fstElmnt.getElementsByTagName("access");
      final NodeList pvLst = fstElmnt.getElementsByTagName("pv");

      numCols = fstElmntLst.getLength()+1;
      cols = new Columns[numCols];
      
      String columnName = new String("");
      String columnAccess = new String("");
      String pvMacroNames = new String("");

      // read the column information from the xml file
      System.out.println("Information of " + numCols + " columns");
      cols[0] = new Columns("Row Name", Access.ReadOnly, pvMacroNames);

      for (int i = 1; i < numCols; i++)
      {
         int j=i-1;
         final Element fstNmElmnt = (Element) fstElmntLst.item(j);
         final NodeList fstNm = fstNmElmnt.getChildNodes();
         columnName = fstNm.item(0).getNodeValue();
         
         final Element accessElmnt = (Element) accessLst.item(j);
         final NodeList accessNm = accessElmnt.getChildNodes();
         columnAccess = accessNm.item(0).getNodeValue();
         
         // Store whether or not the column is editable.
         Access access;
         if (columnAccess.equals("ro"))
            access = Access.ReadOnly;
         else
            access = Access.ReadWrite;

         final Element pvElmnt = (Element) pvLst.item(j);
         final NodeList pvNm = pvElmnt.getChildNodes();
         pvMacroNames = pvNm.item(0).getNodeValue();
       //  System.out.println(columnName + " " + columnAccess + " " + pvMacroNames);
         
         cols[i] = new Columns(columnName, access, pvMacroNames);
      //  System.out.println(i + " get " + getPvNames(i));
      }
   }
   
   /** @return The number of columns. */
   public int numCols() 
   {
      return numCols;
   }
   
   /** @return The column at the input index number. 
   *   @param i Column number.
   */
   public Columns getColumn(int i)
   {
      return cols[i];
   }
}
