package org.csstudio.utility.pvmanager.widgets;

import static org.diirt.datasource.ExpressionLanguage.readMapOf;
import static org.diirt.datasource.ExpressionLanguage.writeMapOf;
import static org.diirt.datasource.formula.ExpressionLanguage.formula;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVWriter;
import org.diirt.datasource.expression.DesiredRateExpression;
import org.diirt.datasource.expression.ReadMap;
import org.diirt.datasource.expression.WriteExpression;
import org.diirt.datasource.expression.WriteMap;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceRegistry;
import org.diirt.util.time.TimeDuration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 *
 *
 * @author carcassi, shroffk
 */
public class ServiceButton extends Composite {

    private DesiredRateExpression<Map<String, Object>> argumentExpression;
    private WriteExpression<Map<String, Object>> resultExpression;
    private String serviceName;
    private ErrorBar errorBar;

    /**
     * Creates a new display.
     *
     * @param parent
     */
    public ServiceButton(Composite parent) {
        super(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        errorBar = new ErrorBar(this, SWT.NONE);
        errorBar.setMarginBottom(5);

        executeButton = new Button(this, SWT.NONE);
        executeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        executeButton.setEnabled(false);
        executeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Map<String, Object> args = argReader.getValue();
                serviceMethod.executeAsync(args, new Consumer<Map<String, Object>>() {

                    @Override
                    public void accept(final Map<String, Object> newValue) {
                        resultWriter.write(newValue);
                    }
                }, new Consumer<Exception>() {

                    @Override
                    public void accept(final Exception error) {
                        SWTUtil.swtThread(ServiceButton.this).execute(new Runnable() {

                            @Override
                            public void run() {
                                errorBar.setException(error);
                                Logger.getLogger(ServiceButton.class.getName())
                                        .log(Level.WARNING,
                                             "Service invocation error for '" + serviceName + "', '" + serviceMethod + "'",
                                             error);
                            }
                        });

                    }

                });
            }
        });
        executeButton.setText("Execute");
    }

    private PVReader<Map<String, Object>> argReader;
    private PVWriter<Map<String, Object>> resultWriter;
    private ServiceMethod serviceMethod;
    private Button executeButton;

    private void reconnect() {
        executeButton.setEnabled(false);
        if (argReader != null) {
            argReader.close();
            argReader = null;
        }

        if (resultWriter != null) {
            resultWriter.close();
            resultWriter = null;
        }

        serviceMethod = null;

        if (serviceName == null) {
            return;
        }

        try {
            serviceMethod = ServiceRegistry.getDefault().findServiceMethod(
                    serviceName);
        } catch (Exception ex) {
            errorBar.setException(ex);
            // TODO display exception
            return;
        }

        if (argumentExpression == null || resultExpression == null) {
            return;
        }

        argReader = PVManager.read(argumentExpression).maxRate(TimeDuration.ofHertz(25));
        resultWriter = PVManager.write(resultExpression).async();
        executeButton.setEnabled(true);
    }

    public DesiredRateExpression<Map<String, Object>> getArgumentExpression() {
        return argumentExpression;
    }

    public void setArgumentExpression(
            DesiredRateExpression<Map<String, Object>> argumentExpression) {
        this.argumentExpression = argumentExpression;
        reconnect();
    }

    public WriteExpression<Map<String, Object>> getResultExpression() {
        return resultExpression;
    }

    public void setResultExpression(
            WriteExpression<Map<String, Object>> resultExpression) {
        this.resultExpression = resultExpression;
        reconnect();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        reconnect();
    }

    public void configureArgumentMap(Map<String, String> argumentPvs) {
        if (serviceMethod == null) {
            return;
        }
        if(serviceMethod.getArgumentMap().keySet().containsAll(argumentPvs.keySet())){
            ReadMap<Object> map = readMapOf(Object.class);
                for (Entry<String, String> argumentPV : argumentPvs.entrySet()) {
                    map.add(formula(argumentPV.getValue(),Object.class).as(argumentPV.getKey()));
                }
                setArgumentExpression(map);
        }else{
            // raise exception, invalid arguments
        }

    }

    public void configureResultMap(Map<String, String> resultPvs) {
        if (serviceMethod == null) {
            return;
        }
        if (serviceMethod.getResultMap().keySet()
                .containsAll(resultPvs.keySet())) {
            WriteMap<Object> map = writeMapOf(Object.class);
            for (Entry<String, String> resultPV : resultPvs.entrySet()) {
                map.add(formula(resultPV.getValue()).as(resultPV.getKey()));
            }
            setResultExpression(map);
        } else {
            // raise exception, invalid arguments
        }

    }

    public void setLabel(String label) {
        executeButton.setText(label);
    }

}