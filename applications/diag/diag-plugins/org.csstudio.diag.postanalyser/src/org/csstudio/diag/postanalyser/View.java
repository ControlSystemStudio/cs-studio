package org.csstudio.diag.postanalyser;

import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.diag.postanalyser.model.Channel;
import org.csstudio.diag.postanalyser.model.Model;
import org.csstudio.trends.databrowser2.ProcessVariableWithSamples;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.diirt.vtype.VType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Eclipse ViewPart for the post analyzer Model and GUI.
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public final class View extends ViewPart
{
    final static String ID = "org.csstudio.diag.postanalyser.view"; //$NON-NLS-1$

    final private Model model = new Model();
    private GUI gui;

    /** Called by <code>ObjectContribPopupAction</code> with received data. */
    public static void activateWithPVs(final ProcessVariableWithSamples[] pvs)
    {
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final View view = (View) page.showView(View.ID);
            if (view == null)
            {
                Activator.getLogger().severe("Cannot activate view"); //$NON-NLS-1$
                return;
            }
            for (ProcessVariableWithSamples pv : pvs)
                view.addPVSamples(pv);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "View activation error", ex); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(model, parent);

        // TODO Accept 'dropped' PVs?
        // As with the ProcessVariableDropTarget, this does not work.
        // For an unknown reason, the dragEnter() event of the drop target
        // never fires for 'parent' or gui.getMainControl().
        // Maybe another type of receiving widget is needed,
        // not Canvas?
        new ControlSystemDropTarget(parent, ProcessVariableWithSamples.class)
        {
            @Override
            public void handleDrop(final Object item)
            {
                if (item instanceof ProcessVariableWithSamples)
                {
                    final ProcessVariableWithSamples pv = (ProcessVariableWithSamples) item;
                    addPVSamples(pv);
                }
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    /** Add samples of PV to model. */
    private void addPVSamples(final ProcessVariableWithSamples pv)
    {
        final String name = pv.getProcessVariable().getName();

        // Convert the sequence of VType into simple doubles
        final VType[] samples = pv.getSamples();
        final int N = samples.length;
        double time[] = new double[N];
        double value[] = new double[N];
        // This skips all samples that won't map to a number,
        // because most analyzer methods can't handle them.
        int j = 0;
        for (int i = 0; i < N; ++i)
        {
            final VType v = samples[i];
            final double dbl = VTypeHelper.toDouble(v);
            if (Double.isNaN(dbl) || Double.isInfinite(dbl))
                continue;
            value[j] = dbl;
            final Instant stamp = VTypeHelper.getTimestamp(v);
            time[j] = TimestampHelper.toMillisecs(stamp) / 1000.0;
            ++j;
        }
        // Add as new channel to the model
        if (j == N)
        {
            model.addChannel(new Channel(name, time, value));
            return;
        }
        // We skipped some values, so create "shorter" arrays
        final double fixed_time[] = new double[j];
        final double fixed_value[] = new double[j];
        System.arraycopy(time,  0, fixed_time, 0, j);
        System.arraycopy(value, 0, fixed_value, 0, j);
        time = null;
        value = null;
        model.addChannel(new Channel(name, fixed_time, fixed_value));
    }
}
