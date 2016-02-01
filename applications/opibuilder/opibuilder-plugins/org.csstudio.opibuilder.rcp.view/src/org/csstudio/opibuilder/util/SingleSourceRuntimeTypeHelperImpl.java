package org.csstudio.opibuilder.util;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIShell;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>SingleSourceRuntimeTypeHelperImpl</code> is an implementation of the helper, which works with OPI shells
 * and OPI views.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SingleSourceRuntimeTypeHelperImpl extends SingleSourceRuntimeTypeHelper {

    @Override
    protected IOPIRuntime iGetOPIRuntimeForEvent(ExecutionEvent event) {
        IOPIRuntime opiRuntime = iGetOPIRuntimeForShell(HandlerUtil.getActiveShell(event));
        if (opiRuntime == null) {
            // if the selected object isn't an OPIShell so grab the
            // OPIView or OPIRunner currently selected
            IWorkbenchPart part = HandlerUtil.getActivePart(event);
            if (part instanceof IOPIRuntime)
            {
                opiRuntime = (IOPIRuntime)part;
            }
        }
        return opiRuntime;
    }

    @Override
    protected void iOpenOPIShell(IPath path, MacrosInput input) {
        OPIShell.openOPIShell(path, input);
    }

    @Override
    protected IOPIRuntime iGetOPIRuntimeForShell(Shell shell) {
        return OPIShell.getOPIShellForShell(shell);
    }

    @Override
    protected void iOpenDisplay(IWorkbenchPage page, RunnerInput input, DisplayMode mode) {
        try
        {
            // Check for existing view with same input.
            for (IViewReference viewReference : page.getViewReferences())
            {
                if (viewReference.getId().startsWith(OPIView.ID))
                {
                    final IViewPart view = viewReference.getView(true);
                    if (view instanceof OPIView)
                    {
                        final OPIView opi_view = (OPIView)view;
                        if (input.equals(opi_view.getOPIInput()))
                        {
                            page.showView(viewReference.getId(), viewReference.getSecondaryId(), IWorkbenchPage.VIEW_ACTIVATE);
                            return;
                        }
                    }
                    else
                        OPIBuilderPlugin.getLogger().log(Level.WARNING,
                            "Found view " + view.getTitle() + " but its type is " + view.getClass().getName());
                }
            }
            // Open new View
            // Create view ID that - when used with OPIRunnerPerspective -
            // causes view to appear in desired location
            final String secondID =  OPIView.createSecondaryID();
            final Position position;
            switch (mode)
            {
            case NEW_TAB_LEFT:     position = Position.LEFT;     break;
            case NEW_TAB_RIGHT:    position = Position.RIGHT;    break;
            case NEW_TAB_TOP:      position = Position.TOP;      break;
            case NEW_TAB_BOTTOM:   position = Position.BOTTOM;   break;
            case NEW_TAB_DETACHED: position = Position.DETACHED; break;
            default:               position = Position.DEFAULT_VIEW;
            }
            final IViewPart view = page.showView(position.getOPIViewID(), secondID, IWorkbenchPage.VIEW_ACTIVATE);
            if (! (view instanceof OPIView))
                throw new PartInitException("Expected OPIView, got " + view);
            final OPIView opiView = (OPIView) view;

            // Set content of view
            opiView.setOPIInput(input);

            // Adjust position
            if (position == Position.DETACHED) {
                SingleSourcePlugin.getUIHelper().detachView(opiView);
                opiView.positionFromModel();
            }
        }
        catch (Exception e)
        {
            ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0} in view.", input.getPath()), e);
        }
    }
}
