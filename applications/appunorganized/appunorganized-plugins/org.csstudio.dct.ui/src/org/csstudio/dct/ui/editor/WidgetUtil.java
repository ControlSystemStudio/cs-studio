package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.editor.tables.ColumnConfig;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Utility class that helps to create standard widgets used in the editing forms
 * of CSS-DCT.
 *
 * @author Sven Wende
 *
 */
public final class WidgetUtil {
    /**
     * Private constructor which prevents instantiation of this class.
     */
    private WidgetUtil() {
    }

    /**
     * Creates and returns a new standard table with 3 columns (for key, value,
     * error).
     *
     * @param parent
     *            the parent composite
     * @param commandStack
     *            a command stack
     * @return a new standard table with 3 columns (for key, value, error)
     */
    public static ConvenienceTableWrapper create3ColumnTable(Composite parent, CommandStack commandStack) {
        ColumnConfig[] cc = new ColumnConfig[3];
        cc[0] = new ColumnConfig("KEY", "Description", 200);
        cc[1] = new ColumnConfig("VALUE", "Value", 300);
        cc[2] = new ColumnConfig("ERROR", "Error", 300);
        return new ConvenienceTableWrapper(parent, SWT.NONE, commandStack, cc);
    }

    public static ConvenienceTableWrapper create4ColumnTable(Composite parent, CommandStack commandStack) {
        ColumnConfig[] cc = new ColumnConfig[3];
        cc[0] = new ColumnConfig("KEY", "Key", 50);
        cc[0] = new ColumnConfig("DESCRIPTION", "Description", 150);
        cc[1] = new ColumnConfig("VALUE", "Value", 300);
        cc[2] = new ColumnConfig("ERROR", "Error", 300);
        return new ConvenienceTableWrapper(parent, SWT.NONE, commandStack, cc);
    }
}
