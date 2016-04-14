package org.csstudio.diag.postanalyser;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.diag.postanalyser.math.Filter;
import org.csstudio.diag.postanalyser.model.Algorithm;
import org.csstudio.diag.postanalyser.model.AlgorithmOutput;
import org.csstudio.diag.postanalyser.model.CorrelationAlgorithm;
import org.csstudio.diag.postanalyser.model.FFTAlgorithm;
import org.csstudio.diag.postanalyser.model.Model;
import org.csstudio.diag.postanalyser.model.ModelListener;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.DefaultColors;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.Trace;
import org.csstudio.swt.chart.actions.PrintCurrentImageAction;
import org.csstudio.swt.chart.actions.RemoveMarkersAction;
import org.csstudio.swt.chart.actions.RemoveSelectedMarkersAction;
import org.csstudio.swt.chart.actions.SaveCurrentImageAction;
import org.csstudio.swt.chart.actions.ShowButtonBarAction;
import org.csstudio.swt.chart.axes.TimeAxis;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

/** User interface of the post analyzer.
 *  <p>
 *  Used by <code>View</code> as well as standalone <code>TestMain</code>
 *  @author Kay Kasemir
 */
public class GUI implements ModelListener, AlgorithmJobListener
{
    /** Gap between certain GUI elements */
    private static final int GAP = 10;

    private static final String AUTO_ZOOM_X = "autozoom_x"; //$NON-NLS-1$

    /** Data Model */
    final private Model model;

    // GUI elements
    private InteractiveChart ichart;
    private ImageRegistry images = new ImageRegistry();
    private Button crop, baseline;
    private Combo algorithm_name, channel_name, fft_window, alt_channel;
    private Label fft_window_label, alt_channel_label, message;

    private AlgorithmJob algorithm_job = null;

    /** Construct GUI for model under parent */
    GUI(final Model model, final Composite parent)
    {
        this.model = model;
        createGUI(parent);
        createContextMenu();

        // Initial selections
        updateConditionalGUIElements();

        hookActions();

        // Fill GUI with model channels via fake event;
        // also performs algorithm.
        newChannels();

        // Listen to model changes
        model.addListener(this);
    }

    /** Set focus to some GUI element */
    public void setFocus()
    {
        algorithm_name.setFocus();
    }

    /** Create GUI elements */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new GridLayout());
        GridData gd;

        // Upper box: Plot
        ichart = new InteractiveChart(parent, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        ichart.setLayoutData(gd);

        Button autozoom_x = new Button(ichart.getButtonBar(), SWT.CENTER);
        final ImageDescriptor image =
            Activator.getImageDescriptor("icons/autozoom_x.gif"); //$NON-NLS-1$
        if (image == null)
            autozoom_x.setText(Messages.GUI_AutoZoomX);
        else
        {
            images.put(AUTO_ZOOM_X, image);
            autozoom_x.setImage(images.get(AUTO_ZOOM_X));
        }
        autozoom_x.setToolTipText(Messages.GUI_AutoZoomX_TT);
        autozoom_x.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                autozoom_x();
            }
        });

        // Lower box: Algorithm control elements
        final Group control_frame = new Group(parent, 0);
        control_frame.setText(Messages.GUI_Algorithm);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        control_frame.setLayoutData(gd);
        final FormLayout form_layout = new FormLayout();
        form_layout.marginWidth = 3;
        form_layout.marginHeight = 3;
        control_frame.setLayout(form_layout);
        FormData fd;

        algorithm_name = new Combo(control_frame, SWT.DROP_DOWN | SWT.READ_ONLY);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        algorithm_name.setLayoutData(fd);
        for (int i=0; i<model.getAlgorithmCount(); ++i)
            algorithm_name.add(model.getAlgorithm(i).getName());
        algorithm_name.select(0);

        Label l = new Label(control_frame, 0);
        l.setText(Messages.GUI_Channel_);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(algorithm_name, GAP);
        l.setLayoutData(fd);

        channel_name = new Combo(control_frame, SWT.DROP_DOWN | SWT.READ_ONLY);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(l);
        channel_name.setLayoutData(fd);

        fft_window_label = new Label(control_frame, 0);
        fft_window_label.setText(Messages.GUI_Window_);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(channel_name, GAP);
        fft_window_label.setLayoutData(fd);

        fft_window = new Combo(control_frame, SWT.DROP_DOWN | SWT.READ_ONLY);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(fft_window_label);
        fft_window.setLayoutData(fd);

        // Fill with filter types
        for (Filter.Type type : Filter.Type.values())
            fft_window.add(type.toString());
        fft_window.select(0);

        alt_channel_label = new Label(control_frame, 0);
        alt_channel_label.setText(Messages.GUI_SecondChannel_);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(channel_name, GAP);
        alt_channel_label.setLayoutData(fd);

        alt_channel = new Combo(control_frame, SWT.DROP_DOWN | SWT.READ_ONLY);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(alt_channel_label);
        alt_channel.setLayoutData(fd);

        crop = new Button(control_frame, 0);
        crop.setText(Messages.GUI_Crop);
        crop.setToolTipText(Messages.GUI_Crop_TT);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(channel_name, GAP);
        crop.setLayoutData(fd);

        baseline = new Button(control_frame, 0);
        baseline.setText(Messages.GUI_Baseline);
        baseline.setToolTipText(Messages.GUI_Baseline_TT);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(crop, GAP);
        baseline.setLayoutData(fd);

        message = new Label(control_frame, 0);
        fd = new FormData();
        fd.top = new FormAttachment(algorithm_name, GAP);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        message.setLayoutData(fd);
    }

    /** Create and connect the context menu. */
    private void createContextMenu()
    {
        final Chart chart = ichart.getChart();

        final RemoveSelectedMarkersAction remove_marker_action
            = new RemoveSelectedMarkersAction(chart);

        final MenuManager context_menu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        context_menu.add(new ShowButtonBarAction(ichart));
        context_menu.add(new RemoveMarkersAction(chart));
        context_menu.add(remove_marker_action);
        context_menu.add(new Separator());
        //context_menu.add(export_action);
        //context_menu.add(new Separator());
        context_menu.add(new ExportAction(this, chart.getShell()));
        context_menu.add(new SaveCurrentImageAction(chart));
        context_menu.add(new PrintCurrentImageAction(chart));
        context_menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        context_menu.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                remove_marker_action.updateEnablement();
            }
        });

        final Menu menu = context_menu.createContextMenu(chart);
        chart.setMenu(menu);
    }

    /** @return Main control of the GUI (for drop target) */
    Control getMainControl()
    {
        return ichart.getChart();
    }

    /** Model has new channels
     *  @see ModelListener
     */
    @Override
    public void newChannels()
    {
        setChannelsFromModel(channel_name);
        setChannelsFromModel(alt_channel);
        channel_name.getParent().layout();
        performAlgorithm();
    }

    /** Set combo box to channel names from model. */
    private void setChannelsFromModel(final Combo combo)
    {
        combo.removeAll();
        for (int i=0; i<model.getChannelCount(); ++i)
            combo.add(model.getChannel(i).getName());
        combo.select(0);
    }

    /** Show or hide the algorithm-specific GUI elements. */
    private void updateConditionalGUIElements()
    {
        final Algorithm algorithm = getCurrentAlgorithm();

        boolean show_crop = true;

        boolean show = algorithm instanceof FFTAlgorithm;
        fft_window_label.setVisible(show);
        fft_window.setVisible(show);
        if (show)
            show_crop = false;

        show = algorithm instanceof CorrelationAlgorithm;
        alt_channel_label.setVisible(show);
        alt_channel.setVisible(show);
        if (show)
            show_crop = false;

        crop.setVisible(show_crop);
        baseline.setVisible(show_crop);

        alt_channel.getParent().layout();
    }

    /** Connect actions to GUI elements */
    private void hookActions()
    {
        algorithm_name.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                updateConditionalGUIElements();
                performAlgorithm();
            }
        });

        final SelectionListener update = new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                performAlgorithm();
            }
        };
        channel_name.addSelectionListener(update);
        alt_channel.addSelectionListener(update);
        fft_window.addSelectionListener(update);

        crop.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                crop();
            }
        });
        baseline.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                baseline();
            }
        });
    }

    protected void crop()
    {
        model.cropChannel(channel_name.getText().trim(),
                ichart.getChart().getXAxis().getLowValue(),
                ichart.getChart().getXAxis().getHighValue());
        performAlgorithm();
    }

    protected void baseline()
    {
        model.baseline(channel_name.getText().trim(),
                ichart.getChart().getYAxis(0).getLowValue());
        performAlgorithm();
    }


    /** Set X axis range to cover all traces in the plot. */
    private void autozoom_x()
    {
        double xmin = Double.MAX_VALUE;
        double xmax = -Double.MAX_VALUE;
        final Chart chart = ichart.getChart();
        for (int i=0; i<chart.getNumTraces(); ++i)
        {
            final ChartSampleSequence samples =
                chart.getTrace(i).getSampleSequence();
            final int N = samples.size();
            if (N < 1)
                continue;
            final double x0 = samples.get(0).getX();
            final double xN = samples.get(N-1).getX();
            if (xmin > x0)
                xmin = x0;
            if (xmax < xN)
                xmax = xN;
        }
        if (xmin == xmax  ||
            xmin == Double.MAX_VALUE  ||
            xmax == Double.MIN_VALUE)
            return;
        chart.getXAxis().setValueRange(xmin, xmax);
    }

    /** Perform the selected algorithm */
    private void performAlgorithm()
    {
        // Ask ongoing job to stop since we no longer care
        if (algorithm_job != null)
            algorithm_job.cancel();
        final Algorithm algorithm = getCurrentAlgorithm();
        try
        {
            final Chart chart = ichart.getChart();
            // Remove all traces
            while (chart.getNumTraces() > 0)
                chart.removeTrace(0);
            // Setup Algorithm
            algorithm.reset();
            try
            {
                final String name = channel_name.getText().trim();
                if (name.isEmpty())
                    return;
                algorithm.setInput(model.getChannel(name));
            }
            catch (Exception ex)
            {
                throw new IllegalArgumentException(Messages.GUI_NoChannelError);
            }
            if (algorithm instanceof CorrelationAlgorithm)
            {
                try
                {
                    final String name = alt_channel.getText().trim();
                    if (name.isEmpty())
                        return;
                    ((CorrelationAlgorithm)algorithm).setCorrelationChannel(model.getChannel(name));
                }
                catch (Exception ex)
                {
                    throw new IllegalArgumentException(Messages.GUI_NoSecondChannelError);
                }
            }
            if (algorithm instanceof FFTAlgorithm)
            {
                final Filter.Type type = Filter.Type.valueOf(fft_window.getText());
                ((FFTAlgorithm)algorithm).setFilterType(type);
            }
            algorithm_job = new AlgorithmJob(algorithm, this);
            algorithm_job.schedule();
        }
        catch (Throwable ex)
        {
            algorithmFailed(algorithm, ex);
        }
    }

    private Algorithm getCurrentAlgorithm()
    {
        final int alg_num = algorithm_name.getSelectionIndex();
        final Algorithm algorithm = model.getAlgorithm(alg_num);
        return algorithm;
    }

    /** Algorithm started in <code>performAlgorithm</code> completed
     *  @see AlgorithmJobListener
     */
    @Override
    public void algorithmDone(final Algorithm algorithm)
    {
        // Handle response from background thread in GUI thread
        Display.getDefault().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Display message
                    message.setText(algorithm.getMessage());
                    // Display output trace(s)
                    final Chart chart = ichart.getChart();
                    chart.setRedraw(false);
                    chart.changeXAxis(algorithm.needTimeAxis());
                    chart.getYAxis(0).setLabel(algorithm.getYAxisLabel());
                    chart.getXAxis().setLabel(algorithm.getXAxisLabel());
                    final AlgorithmOutput[] outputs = algorithm.getOutputs();
                    for (int i=0; i<outputs.length; ++i)
                    {
                        final AlgorithmOutput output = outputs[i];
                        final Color color = new Color(null,
                                DefaultColors.getRed(i),
                                DefaultColors.getGreen(i),
                                DefaultColors.getBlue(i));
                        chart.addTrace(output.getName(), output.getSamples(),
                                color, 1, 0, output.getTraceType());
                    }
                    // Note order: Zoom everything onto x axis...
                    autozoom_x();
                    // .. then zoom Y axis, which only uses "visible" samples
                    chart.autozoom();
                    chart.setRedraw(true);
                }
                catch (Exception ex)
                {
                    algorithmFailed(algorithm, ex);
                }

            }
        });
    }

    /** Algorithm started in <code>performAlgorithm</code> failed
     *  @see AlgorithmJobListener
     */
    @Override
    public void algorithmFailed(final Algorithm algorithm, final Throwable ex)
    {
        Activator.getLogger().log(Level.SEVERE, "algorithm failed", ex); //$NON-NLS-1$

        // Handle response from background thread in GUI thread
        Display.getDefault().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                final String err = ex.getMessage();
                if (err != null)
                    message.setText(err);
                else
                    message.setText(Messages.GUI_AlgorithmError);
            }
        });
    }

    /** Export analysis data to file
     *  @param filename
     */
    public void exportToFile(final String filename)
    {
        // Background job cannot access GUI elements, so read them here
        final Algorithm algorithm = getCurrentAlgorithm();
        final String channel_text = channel_name.getText();
        final String alt_channel_text = alt_channel.getText();
        final String fft_window_text = fft_window.getText();
        final String message_text = message.getText();

        final Job job = new Job("Export") //$NON-NLS-1$
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                exportData(filename, algorithm, channel_text,
                        alt_channel_text, fft_window_text, message_text);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    /** Helper for file export, runs in background job */
    @SuppressWarnings("nls")
    private void exportData(final String filename, final Algorithm algorithm,
            final String channel_text, final String alt_channel_text,
            final String fft_window_text, final String message_text)
    {
        try
        {
            final PrintWriter out = new PrintWriter(filename);
            out.println("# Postanalyzer Data File");
            out.println("#");
            out.println("# Algorithm   : " + algorithm.getName());
            out.println("# Channel     : " + channel_text);

            if (algorithm instanceof CorrelationAlgorithm)
                out.println("# 2nd Channel :" + alt_channel_text);
            if (algorithm instanceof FFTAlgorithm)
                out.println("# Window      :" + fft_window_text);
            out.println("# Message     : " + message_text);
            out.println();

            final Chart chart = ichart.getChart();
            final boolean time_axis = chart.getXAxis() instanceof TimeAxis;
            final int traces = chart.getNumTraces();
            for (int t=0; t<traces; ++t)
            {
                final Trace trace = chart.getTrace(t);
                out.println("# " + trace.getName());
                final ChartSampleSequence samples = trace.getSampleSequence();
                final int N = samples.size();
                for (int i=0; i<N; ++i)
                {
                    final ChartSample sample = samples.get(i);
                    if (time_axis)
                    {
                        final Instant time =
                            TimestampHelper.fromMillisecs((long) sample.getX());
                        out.println(time.toString() + "\t" + sample.getY());
                    }
                    else
                        out.println(sample.getX() + "\t" + sample.getY());
                }
                out.println();
            }
            out.close();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Cannot write to " + filename, ex);
        }
    }
}
