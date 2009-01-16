package org.csstudio.display.pace.model.old;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * RowInfo
 * <p>
 * Create arrays of rows read from the xml configuration file.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class RowInfo
{
   private static int numRows=0;
   private static Rows[] rows;
   
   /** Constructor
    *  @param fstNode Row information from the xml file
    *  
    *  Read and store row information read from the xml file.
    */
   public RowInfo (Node fstNode)
   {
      final Element fstElmnt = (Element) fstNode;
      // search for tags
      final NodeList fstElmntLst = fstElmnt.getElementsByTagName("name");
      numRows = fstElmntLst.getLength();
      String macName = "";

      final NodeList macroLst = fstElmnt.getElementsByTagName("macros");
      
      String rowNames = new String("");
      System.out.println("Information of " + numRows + " rows");
      
      rows = new Rows[numRows];

      // retrieving the macro definitions from the xml file
      for (int i = 0; i < numRows; i++)
      {
         final Element fstNmElmnt = (Element) fstElmntLst.item(i);
         final NodeList fstNm = fstNmElmnt.getChildNodes();
         // instance name
         rowNames = (fstNm.item(0)).getNodeValue();

         // retrieve i-th macro
         final Element macElmnt = (Element) macroLst.item(i);
         final NodeList macLst = macElmnt.getChildNodes();
         macName = macLst.item(0).getNodeValue();
         // Create an array of rows from the row name and macros.
         rows[i] = new Rows(rowNames, macName);
      }
   }
   
   /** @return The number of rows. */
   public int numRows() 
   {
      return numRows;
   }
   
  /** @return The name of the input row number. 
   *   @param rowNum Index of the row.
   */
   public String getRowName(int rowNum)
   {
      return rows[rowNum].getRowName();
   }
   
  /** @return The macro based on input row and macro number. 
   *   @param rowNum Index of the row.
   *   @param macNum Index of the macro.
   */
   public Macros getMacro(int rowNum, int macNum)
   {
      return rows[rowNum].getMacro(macNum);
   }
   
  /** @return The number of macros for the input row number. 
   *   @param rowNum Index of the row.
   */
   public int getNumMacros(int rowNum)
   {
      return rows[rowNum].getNumMacros();
   }
  
  /** @return The value of the macro for the input instance number and macro number. 
   *  @param rowNum Index of the row.
   *  @param macNum Index of the macro.
   */
   public String getMacroVal(int rowNum, int macNum)
   {
      return rows[rowNum].getMacroVal(macNum);
   }
   
  /** @return The macro string for the input row number. 
   *   @param rowNum Index of the row.
   */
   public String getMacroStr(int rowNum)
   {
      return rows[rowNum].getMacroStr();
   }
   
  /** @return The instantiated macro string for the input row number. 
   *  @param macNum Index of the macro.
   */
   public String getMacroName(int rowNum, int macNum)
   {
      return rows[rowNum].getMacroName(macNum);
   }
   
}
