/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cosylab.vdct.inspector;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.cosylab.vdct.undo.UndoManager;

/**
 * TableClipboardAdapter enables Cut-Copy-Paste Clipboard functionality on JTables.
 * The clipboard data format used by the adapter is compatible with
 * the clipboard format used by Excel/OpenOffice Calc. This provides for clipboard
 * interoperability between enabled JTables and Excel/OpenOffice Calc.
 * @author <a href="mailto:matej.sekoranjaATcosylab.com">Matej Sekoranja</a>
 * @version $id$
 */
public class InspectorTableClipboardAdapter implements ActionListener {

    /**
     * Clipboard string selection.
     */
    private StringSelection stringSelection;

    /**
     * System clipboard.
     */
    private Clipboard clipboardSystem;

    /**
     * Managed table.
     */
    private JTable table;

    /**
     * Cut action keystroke.
     */
    private static final KeyStroke CUT_KEYSTROKE =
            KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);

    /**
     * Copy action keystroke.
     */
    private static final KeyStroke COPY_KEYSTROKE =
            KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

    /**
     * Paste action keystroke.
     */
    private static final KeyStroke PASTE_KEYSTROKE =
        KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);

    /**
     * Cut action name.
     */
    private static final String CUT_ACTION_NAME = "Cut";

    /**
     * Copy action name.
     */
    private static final String COPY_ACTION_NAME = "Copy";

    /**
     * Paste action name.
     */
    private static final String PASTE_ACTION_NAME = "Paste";

    /**
     * The adapter is constructed with a JTable on which it enables
     * Cut-Copy-Paste and acts as a clipboard listener.
     * @param table table on which to enable to Cut-Copy-Paste actions.
     */
    public InspectorTableClipboardAdapter(JTable table) {
        this.table = table;

        // register to the table
        table.registerKeyboardAction(this, CUT_ACTION_NAME, CUT_KEYSTROKE, JComponent.WHEN_FOCUSED);
        table.registerKeyboardAction(this, COPY_ACTION_NAME, COPY_KEYSTROKE, JComponent.WHEN_FOCUSED);
        table.registerKeyboardAction(this, PASTE_ACTION_NAME, PASTE_KEYSTROKE, JComponent.WHEN_FOCUSED);

        // cache clipboard system
        clipboardSystem = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Accessor to the table on which this adapter acts.
     * @return    managed table
     */
    public JTable getTable() {
        return table;
    }

    /**
     * This method is activated on the keystrokes we are listening to in this
     * implementation. Here it listens for Copy and Paste, and Cut commands.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(CUT_ACTION_NAME))
            performCopy(true);
        else if (e.getActionCommand().equals(COPY_ACTION_NAME))
            performCopy(false);
        else if (e.getActionCommand().equals(PASTE_ACTION_NAME))
            performPaste();
    }

    /**
     * Perform copy/cut action.
     * @param    cut        perform cut.
     */
    private void performCopy(boolean cut)
    {
        final String EMPTY_STRING = "";

        // check to ensure we have selected only a contiguous block of cells
        int numRows = table.getSelectedRowCount();
        int[] rowsSelected = table.getSelectedRows();

        // noop check
        if (numRows == 0)
            return;

        // inspector case
        //int numCols = table.getSelectedColumnCount();
        //int[] colsSelected = table.getSelectedColumns();
        int numCols = 2;
        int[] colsSelected = new int[] { 1, 2 };

        // noop check
        if (numCols == 0)
            return;

        /*
        boolean contiguousRows =
            (numRows-1) == (rowsSelected[rowsSelected.length - 1] - rowsSelected[0])
            && numRows == rowsSelected.length;

        boolean contiguousCols =
            (numCols-1) == (colsSelected[colsSelected.length - 1] - colsSelected[0])
            && numCols == colsSelected.length;

        if (!(contiguousRows && contiguousCols))
        {
            JOptionPane.showMessageDialog(null,
                        "Invalid copy selection, only contiguous block of cells is allowed.",
                        "Invalid Copy Selection",
                        JOptionPane.ERROR_MESSAGE);
            return;
        }
        */

        // undo support (to pack all into one action)
        try
        {
            if (cut)
                UndoManager.getInstance().startMacroAction();

            StringBuffer sbf = new StringBuffer();

            // construct string
            for (int i = 0; i < numRows; i++)
            {
                for (int j = 0; j < numCols; j++)
                {
                    sbf.append(table.getValueAt(rowsSelected[i], colsSelected[j]));
                    if (j < numCols - 1)
                        sbf.append("\t");
                    // inspector case
                    if (cut && colsSelected[j] == 2)
                        table.setValueAt(EMPTY_STRING, rowsSelected[i], colsSelected[j]);
                }
                sbf.append("\n");
            }

            // do the copy
            stringSelection = new StringSelection(sbf.toString());
            clipboardSystem = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboardSystem.setContents(stringSelection, stringSelection);
        }
           finally
           {
               if (cut)
                   UndoManager.getInstance().stopMacroAction();
           }

    }

    /**
     * Perform paste action.
     * Selections comprising non-adjacent cells result in invalid selection and
     * then copy action cannot be performed. Paste is done by aligning the upper
     * left corner of the selection with the 1st element in the current
     * selection of the JTable.
     */
    private void performPaste()
    {
        final String EMPTY_STRING = "";

        int startRow = (table.getSelectedRows())[0];

        int tableRowCount = table.getRowCount();
        int tableColumnCount = table.getColumnCount();

        // inspector case
        //int startCol = (table.getSelectedColumns())[0];
        int startCol = 1;

        boolean pasted = false;

        // w/ packed undo support
        try
        {
            String transferedString =
                (String)clipboardSystem.getContents(this).getTransferData(DataFlavor.stringFlavor);

            StringTokenizer st1 = new StringTokenizer(transferedString, "\n");
            for (int i = 0; st1.hasMoreTokens(); i++)
            {
                String rowString = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(rowString, "\t");
                int j = 0;
                for (; st2.hasMoreTokens(); j++)
                {
                    String value = (String)st2.nextToken();

                    // set value
                    int columnToSet = startCol + j;
                    // inspector sets only column 2
                    if (columnToSet == 2)
                        if (startRow + i < tableRowCount &&
                            columnToSet < tableColumnCount)
                        {
                            if (!pasted) {
                                pasted = true;
                                UndoManager.getInstance().startMacroAction();
                            }
                            table.setValueAt(value, startRow + i, columnToSet);
                        }
                }

                // clear the rest
                // only one field - inspector case
                if (startCol + j == 2)
                {
                    if (!pasted) {
                        pasted = true;
                        UndoManager.getInstance().startMacroAction();
                    }
                    table.setValueAt(EMPTY_STRING, startRow + i, 2);
                }


            }
        } catch (Throwable th) {
            // noop
        }
        finally
        {
            if (pasted)
                UndoManager.getInstance().stopMacroAction();
        }

    }

}

