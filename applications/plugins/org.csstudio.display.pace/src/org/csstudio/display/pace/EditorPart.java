package org.csstudio.display.pace;

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
 */
public class EditorPart extends org.eclipse.ui.part.EditorPart
    implements ModelListener
{
    private Model model;
    boolean is_dirty = false;
    
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
        // Set window title
        setContentDescription(model.getTitle());
    }

    /** Create GUI */
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
    
    /** "Save" means create elog entry about changes, then write user values
     *   to PVs.
     *   @see org.eclipse.ui.part.EditorPart#doSave(IProgressMonitor)
     */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        // Create info text that lists changed values
        final StringBuilder body = new StringBuilder();
        body.append(Messages.SaveInto);
        for (int i=0; i<model.getInstanceCount(); ++i)
        {
            final Instance instance = model.getInstance(i);
            for (int c=0; c<model.getColumnCount(); ++c)
            {
                final Cell cell = instance.getCell(c);
                if (!cell.isEdited())
                    continue;
                body.append(NLS.bind(Messages.SavePVInfoFmt,
                                     new Object[]
                                     {
                                        cell.getName(),
                                        cell.getCurrentValue(),
                                        cell.getUserValue()
                                     }));
            }
        }
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
                    model.saveUserValues();
                }
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.SaveError, ex.getMessage());
        }
    }

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
