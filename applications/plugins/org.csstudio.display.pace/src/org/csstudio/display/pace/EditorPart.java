package org.csstudio.display.pace;

import org.csstudio.display.pace.gui.GUI;
import org.csstudio.display.pace.model.Model;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/** Eclipse EditorPart for the PACE Model and GUI
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public class EditorPart extends org.eclipse.ui.part.EditorPart
{
    private Model model;
    private GUI gui;
    
    /** Initialize Model from editor input */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
            setInput(input);
        else
            throw new PartInitException("Cannot handle " + input.getName());

        try
        {
            model = new Model(file.getContents());
        }
        catch (Exception ex)
        {
            throw new PartInitException(ex.getMessage());
        }
    }

    /** Create GUI */
    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(parent, model, getSite());
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
                model.stop();
            }
        });
    }

    @Override
    public void setFocus()
    {
        // NOP
    }

    @Override
    public boolean isDirty()
    {
        return model.isEdited();
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs()
    {
        // TODO Auto-generated method stub

    }

    /** @return <code>false</code> to prohibit 'save as' */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }
}
