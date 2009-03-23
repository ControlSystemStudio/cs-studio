package org.csstudio.display.pace;

import java.util.HashMap;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.display.pace.gui.GUI;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Instance;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.csstudio.logbook.ILogbook;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/** Eclipse EditorPart for the PACE Model and GUI
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *  
 *      reviewed by Delphy 01/28/09
 */
public class EditorPart extends org.eclipse.ui.part.EditorPart
    implements ModelListener
{
    private Model model;
    private boolean is_dirty = false;
    
    /** Initialize Model from editor input */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        // Get file behind input
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
            setInput(input);
        else
            throw new PartInitException("Cannot handle " + input.getName()); //$NON-NLS-1$
        // Create model from file
        try
        {
            model = new Model(file.getContents());
        }
        catch (Exception ex)
        {
            throw new PartInitException(ex.getMessage());
        }
        // Set window title and message
        setPartName(file.getName());
        setContentDescription(model.getTitle());
    }

    /** Create GUI using the model as input. */
    @Override
    public void createPartControl(final Composite parent)
    {
        new GUI(parent, model, getSite());
        model.addListener(this);
        try
        {
            model.start();
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().getLogger(this).error(ex);
        }
        parent.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                model.removeListener(EditorPart.this);
                model.stop();
            }
        });
    }

    @Override
    public void setFocus()
    {
        // no need to set focus
    }

    /** @return <code>true</code> if Model contains user changes */
    @Override
    public boolean isDirty()
    {
        return is_dirty;
    }
    
    /** Create the 'body', the main text of the ELog entry which
     *  lists all the changes.
     *  @return ELog text
     */
    private String createElogText()
    {
        final StringBuilder body = new StringBuilder(Messages.SaveIntro);
        
        // While changes to most cells are meant to be logged,
        // some cells' "main" PV might actually be the "comment" meta-PV
        // of another cell.
        // In that case, the comment should be logged with the "main" cell,
        // and the individual logging of the comment cell should be suppressed.
        
        // Map of 'main' cells to 'comment' cells
        final HashMap<Cell, Cell> comment_cell_map = new HashMap<Cell, Cell>();

        // Locate secondary comment cells
        for (int i=0; i<model.getInstanceCount(); ++i)
        {
            final Instance instance = model.getInstance(i);
            for (int c=0; c<model.getColumnCount(); ++c)
            {
                final Cell main_cell = instance.getCell(c);
                if (!main_cell.isEdited()  ||  !main_cell.hasMetaInformation())
                    continue;
                // Look for possible 'comment' cell for main_cell
                final String comment_pv_name = main_cell.getCommentPVName();
                boolean found_comment = false;
                for (int j=0; j<model.getInstanceCount() && !found_comment; ++j)
                {
                    final Instance search_instance = model.getInstance(j);
                    for (int d=0; d<model.getColumnCount(); ++d)
                    {
                        final Cell search_cell = search_instance.getCell(d);
                        if (search_cell.getName().equals(comment_pv_name))
                        {
                            comment_cell_map.put(main_cell, search_cell);
                            found_comment = true;
                            break;
                        }
                    }
                }
            }
        }

        // Loop over all cells to log changes
        for (int i=0; i<model.getInstanceCount(); ++i)
        {
            final Instance instance = model.getInstance(i);
            // Check every cell in each instance (row) to see if they have been 
            // edited.  If they have add them to the elog message.
            for (int c=0; c<model.getColumnCount(); ++c)
            {
                final Cell cell = instance.getCell(c);
                if (!cell.isEdited())
                    continue;
                // Skip comment cells which will be logged when handling
                // the associated "main" cell
                if (comment_cell_map.containsValue(cell))
                   continue;
                body.append(NLS.bind(Messages.SavePVInfoFmt,
                                     new Object[]
                                     {
                                        cell.getName(),
                                        cell.getCurrentValue(),
                                        cell.getUserValue()
                                     }));
                // If the cell has comments, find the comment pv and log it's changed
                // value with the limit change log report.
                final Cell comment_cell = comment_cell_map.get(cell);
                if (comment_cell != null)
                    body.append(NLS.bind(Messages.SaveCommentInfoFmt,
                                         comment_cell.getValue()));
            }
        }
        return body.toString();
    }

    /** "Save" means create elog entry about changes, then write user values
     *   to PVs.
     *   @see org.eclipse.ui.part.EditorPart#doSave(IProgressMonitor)
     */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        final String body = createElogText();
        // Display ELog entry dialog
        final Shell shell = getSite().getShell();
        try
        {
            final ElogDialog dialog = new ElogDialog(shell,
                    Messages.SaveTitle, Messages.SaveMessage,
                    body.toString(), null)
            {
                // Perform ELog entry, then save changed values
                @Override
                public void makeElogEntry(String logbook_name, String user,
                        String password, String title, String body)
                        throws Exception
                {
                    // TODO Think "transaction"
                    // Both the logbook entry and the PV updates can fail
                    // for some reason.
                    // The whole elog-and-pv-update should be handled
                    // as a transaction that either succeeds or fails
                    // as a whole.
                    //
                    // Cannot make elog entry?
                    // Show error, don't write PVs.
                    // This case is currently OK!
                    //
                    // Not OK:
                    // Make elog entry, then at least one PV 'write'
                    // fails.
                    // Now what?
                    // Remove the elog entry? Can't.
                    // Restore PVs that did write OK with old value?
                    // What if that fails, too?
                    // For now we only show an error message.
                    // Add elog entry about failure?
                    final ILogbook logbook = getLogbook_factory()
                        .connect(logbook_name, user, password);
                    try
                    {
                        logbook.createEntry(title, body, null);
                    }
                    finally
                    {
                        logbook.close();
                    }
                    // Exceptions in here are caught by ELog dialog
                    // and will be displayed there
                    // (maybe not in the most obvious way...)
                    model.saveUserValues(user);
                }
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.SaveError, ex.getMessage());
        }
    }

    /** "SaveAs isn't allowed and should not get invoked,
     *  but in case it is, we handle it like 'doSave'
     */
    @Override
    public void doSaveAs()
    {
        doSave(new NullProgressMonitor());
    }

    /** @return <code>false</code> to prohibit 'save as' */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /** Update the editor's "dirty" state when model changes
     *  @see ModelListener
     */
    public void cellUpdate(final Cell cell)
    {
        if (is_dirty == model.isEdited())
            return;
        is_dirty = model.isEdited();
        firePropertyChange(PROP_DIRTY);
    }
}
