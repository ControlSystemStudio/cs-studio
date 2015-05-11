/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** RCP 'View' for display runtime
 *
 *  <p>Similar to an RCP editor it is associated to an 'input',
 *  but provides only a view to that *.opi, executing its content.
 *
 *  <p>Being a 'View' allows save/restore within a 'Perspective'.
 *
 *  @author Xihui Chen - Original author
 *  @author Kay Kasemir
 */
public class OPIView extends ViewPart implements IOPIRuntime
{
    /** View ID registered in plugin.xml */
    public static final String ID = "org.csstudio.opibuilder.opiView"; //$NON-NLS-1$

    /** Memento tags */
    private static final String TAG_INPUT = "input", //$NON-NLS-1$
                                TAG_FACTORY_ID = "factory_id"; //$NON-NLS-1$

    protected OPIRuntimeDelegate opiRuntimeDelegate;

    private IViewSite site;
    private IEditorInput input;

    /** For views that should be detached, tracks if that has been done */
    private boolean detached = false;

    /** SYNC on class for access
     *  @see #createSecondaryID()
     */
    private static int instance = 0;

    private OPIRuntimeToolBarDelegate opiRuntimeToolBarDelegate;

    /** See {@link #ignoreMemento()} */
    private static boolean ignoreMemento = false;

    private static boolean openFromPerspective = false;

    private static boolean openedByUser = false;

    /** When application starts up, restored views use the memento
     *  provided by RCP to restore their original content.
     *
     *  When displays are later opened by the user, from actions etc.,
     *  those views may use instance IDs that match a previously used
     *  view instance, but they should not use that old memento
     *  because they will receive the desired input by whatever code
     *  created them.
     */
    public static void ignoreMemento()
    {
        OPIView.ignoreMemento = true;
    }

    public OPIView()
    {
        opiRuntimeDelegate = new OPIRuntimeDelegate(this);
    }

    /** @return Unique secondary view ID for this instance of CSS */
    public static String createSecondaryID()
    {
        synchronized (OPIView.class)
        {
            return Integer.toString(++instance);
        }
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.site = site;

        // When RCP restores Views on CSS restart, the view ID is already set.
        // Adjust instance to not conflict with such restored views.
        synchronized (OPIView.class)
        {
            final String secondary = site.getSecondaryId();
            if (secondary != null)
            {
                int digits = 0;
                while (digits < secondary.length()  &&   "0123456789".contains(secondary.substring(digits, digits+1)))
                    ++digits;
                if (digits > 0)
                {
                    int id = Integer.parseInt(secondary.substring(0, digits));
                    if (instance < id)
                        instance = id;
                }
            }
        }

        if (ignoreMemento  ||  memento == null)
            return;

        // Load previously displayed input from memento
        final IMemento inputMem = memento.getChild(TAG_INPUT);
        final String  factoryID = memento.getString(TAG_FACTORY_ID);
        final IElementFactory factory = PlatformUI.getWorkbench().getElementFactory(factoryID);
        if (factory == null)
            throw new PartInitException(NLS.bind(
                    "Cannot instantiate input element factory {0} for OPIView",
                    factoryID));

        final IAdaptable element = factory.createElement(inputMem);
        if (!(element instanceof IEditorInput))
            throw new PartInitException("Instead of OPIView, " + factoryID + " returned " + element);
        setOPIInput((IEditorInput)element);
    }

    /** @param input Display file that this view should execute */
    public void setOPIInput(final IEditorInput input) throws PartInitException
    {
        IViewSite view = getViewSite();
        System.out.println(view.getId() + ":" + view.getSecondaryId() + " for " + input.getName());

        this.input = input;
        setTitleToolTip(input.getToolTipText());
        opiRuntimeDelegate.init(site, input);
        if (opiRuntimeToolBarDelegate != null)
            opiRuntimeToolBarDelegate.setActiveOPIRuntime(this);
    }

    @Override
    public void createPartControl(final Composite parent) {
        if(SWT.getPlatform().startsWith("rap")){ //$NON-NLS-1$
            SingleSourceHelper.rapOPIViewCreatePartControl(this, parent);
            return;
        }

        opiRuntimeDelegate.createGUI(parent);
        createToolbarButtons();
        parent.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                if(parent.getShell().getText().length() == 0){ //the only way to know it is detached.
                    if(!detached){
                        detached = true;
                        UIBundlingThread.getInstance().addRunnable(new Runnable() {
                            public void run() {
                                final Rectangle bounds;
                                if(opiRuntimeDelegate.getDisplayModel() != null)
                                    bounds = opiRuntimeDelegate.getDisplayModel().getBounds();
                                else
                                    bounds = new Rectangle(0, 0, 800, 600);
                                if (openedByUser) {
                                    if (bounds.x >= 0 && bounds.y > 1)
                                        parent.getShell().setLocation(bounds.x, bounds.y);
                                    else {
                                        org.eclipse.swt.graphics.Rectangle winSize = getSite()
                                                .getWorkbenchWindow().getShell().getBounds();
                                        parent.getShell().setLocation(
                                                winSize.x + winSize.width / 5
                                                        + (int) (Math.random() * 100),
                                                winSize.y + winSize.height / 8
                                                        + (int) (Math.random() * 100));
                                    }
                                }
                                parent.getShell().setSize(bounds.width+45, bounds.height+65);
                            }
                        });
                    }
                }else
                    detached = false;
            }
        });
    }

    public void createToolbarButtons(){
        opiRuntimeToolBarDelegate = new OPIRuntimeToolBarDelegate();
        IActionBars bars = getViewSite().getActionBars();
        opiRuntimeToolBarDelegate.init(bars, getSite().getPage());
        opiRuntimeToolBarDelegate.contributeToToolBar(bars.getToolBarManager());
        opiRuntimeToolBarDelegate.setActiveOPIRuntime(this);
    }


    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        if(input == null)
            return;
        IPersistableElement persistable = input.getPersistable();
        if (persistable != null) {
            /*
             * Store IPersistable of the IEditorInput in a separate section
             * since it could potentially use a tag already used in the parent
             * memento and thus overwrite data.
             */
            IMemento persistableMemento = memento
                    .createChild(TAG_INPUT);
            persistable.saveState(persistableMemento);
            memento.putString(TAG_FACTORY_ID,
                    persistable.getFactoryId());
            // save the name and tooltip separately so they can be restored
            // without having to instantiate the input, which can activate
            // plugins
//            memento.putString(IWorkbenchConstants.TAG_NAME, input.getName());
//            memento.putString(IWorkbenchConstants.TAG_TOOLTIP,
//                    input.getToolTipText());
        }
    }

    @Override
    public void setFocus() {

    }

    public void setWorkbenchPartName(String name) {
        setPartName(name);
        setTitleToolTip(getOPIInput().getToolTipText());
    }

    public OPIRuntimeDelegate getOPIRuntimeDelegate() {
        return opiRuntimeDelegate;
    }

    public IEditorInput getOPIInput() {
        return getOPIRuntimeDelegate().getEditorInput();
    }

    public DisplayModel getDisplayModel() {
        return getOPIRuntimeDelegate().getDisplayModel();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        Object obj = opiRuntimeDelegate.getAdapter(adapter);
        if (obj != null)
            return obj;
        else
            return super.getAdapter(adapter);

    }

    public static boolean isOpenFromPerspective() {
        return openFromPerspective;
    }

    public static void setOpenFromPerspective(boolean openFromPerspective) {
        OPIView.openFromPerspective = openFromPerspective;
    }

    /** Mark as opened by user, interactively
     *
     *  <p>Detached view, when opened by user, will be positioned
     *  somewhere within the Workbench window, so user can find it.
     *
     *  @param openedByUser Mark as opened interactively?
     */
    public static void setOpenedByUser(boolean openedByUser) {
        OPIView.openedByUser = openedByUser;
    }
}
