package org.csstudio.apputil.ui.formula;

import org.csstudio.platform.logging.CentralLogger;

/** Helper for creating the formula's input table.
 *  @author Kay Kasemir
 */
public class InputTableHelper
{
    enum Column
    {
        /** Column Identifier. */
        INPUT(Messages.InputName, 200, 100),

        /** Column Identifier. */
    	VARIABLE(Messages.VariableName, 80, 100);
        
        private final String title;
        private final int min_size;
        private final int weight;
        
        private Column(String title, int size, int weight)
        {
            this.title = title;
            this.min_size = size;
            this.weight = weight;
        }
       
        /** @return Column title */
        public String getTitle()
        {   return title; }
        
        /** @return Minimum column size. */
        public int getMinSize()
        {   return min_size;  }

        /** @return Column weight. */
        public int getWeight()
        {   return weight; }

        /** @return Column for the given ordinal. */
        public static Column fromOrdinal(int ordinal)
        {   // This is expensive, but java.lang.Enum offers no easy way...
            for (Column id : Column.values())
                if (id.ordinal() == ordinal)
                    return id;
            throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
        }
    }

	/** Get ID for a property.
	 * 
	 * @param title One of the column titles.
	 * @return Returns the requested Column.
	 * @throws Exception on error.
	 */
	@SuppressWarnings("nls")
    static public Column findColumn(String title) throws Exception
	{
		for (Column col : Column.values())
			if (col.getTitle().equals(title))
                return col;
		throw new Exception("Unknown column '" + title + "'");
	}

	/** Get e.g. the "NAME" from a ChartItem.
	 * 
	 * @param qso
	 * @param col_title One of the properties[] strings.
	 * @return Returns the requested property.
	 * @throws Exception on error.
	 */
	static public Object getProperty(InputItem input, String col_title) throws Exception
	{
        Column id = findColumn(col_title);
	    return getText(input, id);
	}

    /** Get a data piece of the entry.
     * @param input The ChartItem.
     * @param item 0 for properties[0] etc.
     * @return Returns the String for the entry
     */
    static public String getText(InputItem input, int col_index)
    {
        return getText(input, Column.fromOrdinal(col_index));
    }

	/** Get a data piece of the entry.
	 * @param input The ChartItem.
	 * @param item The Column of interest.
	 * @return Returns the String for the entry
	 */
	static public String getText(final InputItem input, final Column item)
	{
		try
        {
            switch (item)
            {
            case INPUT:
                return input.getInputName();
            case VARIABLE:
                return input.getVariableName();
            }
		}
		catch (Exception e)
		{
            CentralLogger.getInstance().error("InputTableHelper.getText:", e); //$NON-NLS-1$
		}
		return null;
	}
}
