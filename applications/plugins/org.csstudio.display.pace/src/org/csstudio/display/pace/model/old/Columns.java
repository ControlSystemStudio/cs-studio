package org.csstudio.display.pace.model.old;

import org.csstudio.display.pace.model.old.Cell.Access;
/**
 * Columns
 * <p>
 * Store Column information.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class Columns
{
   final private String columnName;
   final private Access columnAccess;
   final private String pvName;
   
   /** Constructor
    *  @param columnName The name of the column
    *  @param columnAccess Whether or not this column is editable
    *  @param pv The pv associated with this column.
    *  
    *  Read and store cell information for this row.
    */
   public Columns(String name, Access access, String pv)
   {
      columnName = name;
      columnAccess = access;
      pvName = pv;
   }
   
   /** @return The column name. */
   public String getName()
   {
      return columnName;
   }
   
   /** @return pv name with macros not substituted. */
   public String getPvName()
   {
      return pvName;
   }
   
   /** @return The access of this column, either rw or ro. */
   public Access getAccess()
   {
      return columnAccess;
   }

}
