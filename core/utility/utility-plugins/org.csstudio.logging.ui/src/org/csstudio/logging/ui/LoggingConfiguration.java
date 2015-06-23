package org.csstudio.logging.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

/**
 * This view lists all the loggers currently available in cs-studio
 * The users can turn add or remove the consoleViewHandler from any of the logger
 * The users can configure the logging Level for these loggers too, thus allow temporary FINE logging.
 *
 * TODO redo the GUI to have a tree table
 *
 * @author Kunal Shroff
 *
 */
public class LoggingConfiguration extends ViewPart {

    private final static Logger LOGGER = Logger.getLogger(LoggingConfiguration.class.getName());
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String ID = "org.csstudio.logging.ui.LoggingConfiguration";

    private ScrolledComposite sc;
    private final Map<String, Logger> loggerMap = new TreeMap<String, Logger>();

    public LoggingConfiguration() {

    }

    @Override
    public void createPartControl(final Composite parent) {

        IPartService service = (IPartService) getSite().getService(IPartService.class);
        service.addPartListener(new IPartListener2() {



            @Override
            public void partActivated(IWorkbenchPartReference partRef) {
                // TODO Auto-generated method stub
                if(partRef.getId().equals(ID)){
                    updateLoggerMap();
                    Display.getCurrent().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            if (!parent.isDisposed()) {
                                //FIXME do not recreate the whole view every time it
                                //becomes active (it happens on every focus gained event)
                                createComposite(parent);
                            }
                        }
                    });
                }
            }

            @Override
            public void partBroughtToTop(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partClosed(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partDeactivated(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partOpened(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partHidden(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partVisible(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partInputChanged(IWorkbenchPartReference partRef) {
            }
        });

        // Initialization
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);

        updateLoggerMap();

//        Logger rootLogger = Logger.getLogger("");
//        final ArrayList<Handler> globalHandlers = new ArrayList<Handler>(Arrays.asList(rootLogger.getHandlers()));

        createComposite(parent);

        // Do some logging

        ScheduledExecutorService scheduler  = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Current Time is: " + sdf.format(new Date()));
                LOGGER.log(Level.SEVERE, "You are LATE!! Current Time is: " + sdf.format(new Date()));
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void setFocus() {

    }

    private void createComposite(Composite parent){
        // GUI
        if (sc == null) {
            sc = new ScrolledComposite(parent, SWT.V_SCROLL);
        } else {
            for (Control control : sc.getChildren()) {
                control.dispose();
            }
        }

        Composite composite = new Composite(sc, SWT.None);
        composite.setLayout(new GridLayout(3, false));

        Label lblHeading = new Label(composite, SWT.NONE);
        lblHeading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblHeading.setText("Logger Name:");

        Label btnHeading = new Label(composite, SWT.WRAP);
        btnHeading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        btnHeading.setText("Enable/Disable the logging to the eclipse console view");
        btnHeading.setToolTipText("Enable/Disable the logging to the eclipse console view");

        Label comboHeading = new Label(composite, SWT.WRAP);
        comboHeading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        comboHeading.setText("Set logger level");
        comboHeading.setToolTipText("Set logger level");

        for (final Entry<String, Logger> loggerEntry : loggerMap.entrySet()) {

            //Label
            Label lblNewLabel = new Label(composite, SWT.NONE);
            lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
            lblNewLabel.setText(loggerEntry.getKey());

            //ON/OFF
            Button btnNewButton = new Button(composite, SWT.TOGGLE);
            btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            btnNewButton.setData("loggerName", loggerEntry.getKey());

            // By default turn off the test logger associated with this plugin
            // TODO This test feature should be removed from this plugin
            if(loggerEntry.getKey().equals(ID)){
                btnNewButton.setText("OFF");
                btnNewButton.setSelection(false);
                loggerEntry.getValue().setUseParentHandlers(false);
            }else{
                btnNewButton.setText("ON");
                btnNewButton.setSelection(true);
            }
            btnNewButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button b = ((Button) e.widget);
                    if (b.getSelection()) {
                        b.setText("ON");
                        Logger logger = loggerMap.get((String)b.getData("loggerName"));
                        logger.setUseParentHandlers(true);
                        // this would explicitly add the handler on the individual logger
//                        logger.addHandler(consoleViewHandler);
//                        logger.addHandler(consoleHandler);
                    } else {
                        b.setText("OFF");
                        Logger logger = loggerMap.get((String)b.getData("loggerName"));
                        // Since these handlers are actually added on the parent logger("") we have to disable the use of parent Handlers
                        logger.setUseParentHandlers(false);
                        // precaution
//                        logger.removeHandler(consoleViewHandler);
//                        logger.removeHandler(consoleHandler);
                    }
                }
            });

            Level[] levels = {Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL};
            final ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
            comboViewer.setContentProvider(ArrayContentProvider.getInstance());
            comboViewer.setData("loggerName", loggerEntry.getKey());
            comboViewer.setInput(levels);
            comboViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                private Level _oldSelection;

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    Level newSelection = (Level) ((IStructuredSelection) event.getSelection()).getFirstElement();
                    if (newSelection != _oldSelection) {
                        loggerMap.get((String)comboViewer.getData("loggerName")).setLevel(newSelection);
                        _oldSelection = newSelection;
                    }
                }
            });
        }

        sc.setContent(composite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void updateLoggerMap(){
        LogManager manager = LogManager.getLogManager();
        Enumeration<String> loggerNames = manager.getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            String name = loggerNames.nextElement();
            Logger l = manager.getLogger(name);
            loggerMap.put(name, l);
        }
    }

}
