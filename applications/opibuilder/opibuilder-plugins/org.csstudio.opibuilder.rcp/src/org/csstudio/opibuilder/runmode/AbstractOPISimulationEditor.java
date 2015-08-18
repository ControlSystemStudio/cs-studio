package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>AbstractOPISimulationEditor</code> is a simulation of the editor part, which is used to open files directly
 * from the navigator. When using the standard approach the launcher will receive the absolute system path,
 * which can cause problems when the opi files are linked together (the files become unaware of the workspace and other
 * resources that are linked into the workspace). This editor provides access to the complete location details of the
 * file. Immediately after the file is opened (editor is initialised) this editor is closed.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class AbstractOPISimulationEditor implements IEditorPart {

    /**
     * <code>Focusable</code> is a wrapper for the graphical object that this editor launches. The
     * editor will try to focus this object after it completes initialisation.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    protected static interface Focusable {
        /**
         * Focus the wrapped graphical object. Method is called after the editor is closed.
         */
        void focus();
    }

    private IEditorSite site;
    private IEditorInput input;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.site = site;
        this.input = input;
        IPath path = ResourceUtil.getPathInEditor(input);
        final Focusable focusable = run(path);
        Display.getCurrent().asyncExec(()-> {
            site.getPage().closeEditor(this, false);
            focusable.focus();
        });
    }

    /**
     * Execute launch command on the given path and return a wrapper around the launched graphical object. The launched
     * object can be a shell, view or anything else. The returned {@link Focusable} must not be null, but it does not
     * need to contain an object; if there is nothing to focus, the focusable can be an empty implementation
     *
     * @param path the path to open; this path is an absolute path within the workspace
     * @return the focusable wrapping the launched graphical element
     */
    public abstract Focusable run(IPath path);

    @Override
    public void addPropertyListener(IPropertyListener listener) {
    }

    @Override
    public void createPartControl(Composite parent) {
    }

    @Override
    public void dispose() {
        site = null;
        input = null;
    }

    @Override
    public IWorkbenchPartSite getSite() {
        return site;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Image getTitleImage() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    @Override
    public void removePropertyListener(IPropertyListener listener) {
    }

    @Override
    public void setFocus() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    @Override
    public IEditorInput getEditorInput() {
        return input;
    }

    @Override
    public IEditorSite getEditorSite() {
        return site;
    }
}
