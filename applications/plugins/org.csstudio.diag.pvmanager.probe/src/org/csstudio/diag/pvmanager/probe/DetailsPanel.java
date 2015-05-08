package org.csstudio.diag.pvmanager.probe;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.pvmanager.formula.ExpressionLanguage;

/**
 * Probe panel that allows to show details of the channel.
 *
 * @author carcassi
 *
 */
public class DetailsPanel extends Composite {

    private Text channelHandlerNameField;
    private Text usageCountField;
    private Text expressionNameField;
    private Text connectionField;
    private Label expressionNameLabel;
    private Composite channelSection;
    private Composite expressionSection;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public DetailsPanel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        typeSection = new Composite(this, SWT.NONE);
        typeSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_typeSection = new GridLayout(2, false);
        gl_typeSection.marginHeight = 0;
        gl_typeSection.marginBottom = 5;
        gl_typeSection.marginWidth = 0;
        typeSection.setLayout(gl_typeSection);

        typeLabel = new Label(typeSection, SWT.NONE);
        typeLabel.setText(Messages.Probe_infoExpressionType);
        typeLabel.setBounds(0, 0, 45, 20);

        typeField = new Text(typeSection, SWT.BORDER);
        typeField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        typeField.setEditable(false);
        typeField.setBounds(0, 0, 390, 26);

        expressionSection = new Composite(this, SWT.NONE);
        expressionSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_expressionSection = new GridLayout(2, false);
        gl_expressionSection.marginBottom = 5;
        gl_expressionSection.marginWidth = 0;
        gl_expressionSection.marginHeight = 0;
        expressionSection.setLayout(gl_expressionSection);

        expressionNameLabel = new Label(expressionSection, SWT.NONE);
        expressionNameLabel.setText(Messages.Probe_infoExpressionName);

        expressionNameField = new Text(expressionSection, SWT.BORDER);
        expressionNameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        expressionNameField.setEditable(false);

        channelSection = new Composite(this, SWT.NONE);
        GridLayout gl_channelSection = new GridLayout(2, false);
        gl_channelSection.marginBottom = 5;
        gl_channelSection.marginWidth = 0;
        gl_channelSection.marginHeight = 0;
        channelSection.setLayout(gl_channelSection);
        channelSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label channelHandlerNameLabel = new Label(channelSection, SWT.NONE);
        channelHandlerNameLabel.setText(Messages.Probe_infoChannelHandlerName);

        channelHandlerNameField = new Text(channelSection, SWT.BORDER);
        channelHandlerNameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        channelHandlerNameField.setEditable(false);

        Label usageCountLabel = new Label(channelSection, SWT.NONE);
        usageCountLabel.setText(Messages.Probe_infoUsageCount);

        usageCountField = new Text(channelSection, SWT.BORDER);
        usageCountField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        usageCountField.setEditable(false);

        Label connectionLabel = new Label(channelSection, SWT.NONE);
        connectionLabel.setText(Messages.Probe_infoConnected);

        connectionField = new Text(channelSection, SWT.BORDER);
        connectionField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        connectionField.setEditable(false);

        channelPropertiesLabel = new Label(channelSection, SWT.NONE);
        channelPropertiesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        channelPropertiesLabel.setText(Messages.Probe_infoChannelProperties);

        channelPropertiesField = new Text(channelSection, SWT.BORDER | SWT.MULTI);
        channelPropertiesField.setEditable(false);
        channelPropertiesField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    }

    private boolean needsDoLayout;
    private Label channelPropertiesLabel;
    private Text channelPropertiesField;
    private Composite typeSection;
    private Label typeLabel;
    private Text typeField;

    public void changeValue(DesiredRateReadWriteExpression<?, ?> expression, String formula) {
        needsDoLayout = false;

        setType(formula);
        setExpression(expression);
        setChannelProperties(formula);

        if (needsDoLayout) {
            this.getParent().layout();
        }
    }

    private boolean isChannel(String formula) {
        return ExpressionLanguage.channelFromFormula(formula) != null;
    }

    private void setType(String formula) {
        if (formula != null) {
            if (isChannel(formula)) {
                typeField.setText(Messages.Probe_infoChannel);
            } else {
                typeField.setText(Messages.Probe_infoFormula);
            }
        } else {
            typeField.setText(""); //$NON-NLS-1$
        }
    }

    private void setExpression(DesiredRateReadWriteExpression<?, ?> expression) {
        if (expression != null) {
            expressionNameField.setText(expression.getName());
            showSection(expressionSection);
        } else {
            expressionNameField.setText(""); //$NON-NLS-1$
            hideSection(expressionSection);
        }
    }

    private void setChannelProperties(String formula) {
        ChannelHandler handler = null;
        if (formula != null) {
            String channel = ExpressionLanguage.channelFromFormula(formula);
            handler = PVManager.getDefaultDataSource()
                    .getChannels().get(PVFormulaUtil.channelWithDataSource(channel));
        }
        setChannelProperties(handler);
        if (handler != null) {
            refreshOn(handler);
        } else {
            refreshOff();
        }
    }

    private ScheduledFuture<?> future;

    private void refreshOn(final ChannelHandler handler) {
        if (future != null) {
            future.cancel(false);
        }
        future = PVManager.getReadScannerExecutorService().scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                getDisplay().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        setChannelProperties(handler);
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void refreshOff() {
        if (future != null) {
            future.cancel(false);
        }
        future = null;
    }

    private int nRows = 0;
    private void setChannelProperties(ChannelHandler handler) {
        if (handler != null) {
            //if any widget is disposed, the channelHandlerName is disposed as well. In such case, just return
            if (channelHandlerNameField.isDisposed()) return;
            SortedMap<String, Object> sortedProperties = new TreeMap<String, Object>(
                    handler.getProperties());
            StringBuilder builder = new StringBuilder(sortedProperties.size() * 100);
            for (Map.Entry<String, Object> entry : sortedProperties
                    .entrySet()) {
                builder.append(entry.getKey())
                        .append(" = ").append(entry.getValue()) //$NON-NLS-1$
                        .append('\n'); //$NON-NLS-1$
            }
            channelHandlerNameField.setText(handler.getChannelName());
            usageCountField.setText(new StringBuilder(30)
                    .append(handler.getUsageCounter())
                    .append(" (") //$NON-NLS-1$
                    .append(handler.getReadUsageCounter())
                    .append('+') //$NON-NLS-1$
                    .append(handler.getWriteUsageCounter())
                    .append(')').toString()); //$NON-NLS-1$
            boolean writeConnected = false;
            if (handler instanceof MultiplexedChannelHandler) {
                writeConnected = ((MultiplexedChannelHandler<?, ?>) handler).isWriteConnected();
            }
            connectionField.setText(new StringBuilder(13).append(handler.isConnected()).append(" - ").append(writeConnected).toString()); //$NON-NLS-1$
            channelPropertiesField.setText(builder.toString());
            showSection(channelSection);
            if (nRows != sortedProperties.size()) {
                this.getParent().layout();
            }
            nRows = sortedProperties.size();
        } else {
            channelPropertiesField.setText(""); //$NON-NLS-1$
            hideSection(channelSection);
            nRows = 0;
        }
    }

    private void hideSection(Composite section) {
        needsDoLayout = ShowHideForGridLayout.hide(section) || needsDoLayout;
    }

    private void showSection(Composite section) {
        needsDoLayout = ShowHideForGridLayout.show(section) || needsDoLayout;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
