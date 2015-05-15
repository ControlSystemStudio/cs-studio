package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.swt.graphics.FontData;

/**
 * Undo-able command to change plot legend font
 * Based on {@link ChangeLegendFontCommand}
 * @author Kunal Shroff
 *
 */
public class ChangeLegendFontCommand extends UndoableAction
{
    final private Model model;
    final private FontData oldFont;
    final private FontData newFont;

    /** Register and perform the command
     *  @param model Model to configure
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param newFont New value
     */
    public ChangeLegendFontCommand(final Model model,
            final UndoableActionManager operations_manager,
            final FontData new_font)
    {
        super(Messages.LegendFontLbl);
        this.model = model;
        this.oldFont = model.getLegendFont();
        this.newFont = new_font;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setLegendFont(newFont);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setLegendFont(oldFont);
    }
}