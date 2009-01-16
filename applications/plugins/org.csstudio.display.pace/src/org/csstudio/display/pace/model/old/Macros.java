package org.csstudio.display.pace.model.old;
/**
 * Macros
 * <p>
 * Stores macro information read from the xml file.
 * 
 * @author Delphy Nypaver Armstrong
 */
public class Macros
{
  String macro_name;
  String macro_def;
  
  /** Constructor
   *  @param name The macro's name
   *  @param val The value of the macro
   *
   *  Store the macro name and value.
   */
  public Macros(String name, String val)
  {
     macro_name = new String(name);
     macro_def = new String(val);
  }
  
  /** @return The macro name. */
  public String getName()
  {
     return macro_name;
  }
  
  /** @return The macro value. */
  public String getVal()
  {
     return macro_def;
  }
  
}
