package org.csstudio.utility.pvmanager.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.readMapOf;
import static org.epics.pvmanager.ExpressionLanguage.writeMapOf;

import java.util.Map;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.ReadMap;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.pvmanager.expression.WriteMap;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;
import org.epics.util.time.TimeDuration;

/**
 * Basic ui component that can display a VTable on screen.
 * 
 * @author carcassi
 */
public class ServicePanel extends Composite {
	private Text serviceField;
	private Text argumentField;
	private Text resultsField;

	private DesiredRateExpression<Map<String, Object>> argumentExpression;
	private WriteExpression<Map<String, Object>> resultExpression;
	private String serviceName;

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public ServicePanel(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));

		Label lblService = new Label(this, SWT.NONE);
		lblService.setText("Service:");

		serviceField = new Text(this, SWT.BORDER);
		serviceField.setEditable(false);
		serviceField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblArguments = new Label(this, SWT.NONE);
		lblArguments.setText("Arguments:");

		argumentField = new Text(this, SWT.BORDER);
		argumentField.setEditable(false);
		argumentField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblResult = new Label(this, SWT.NONE);
		lblResult.setText("Results:");

		resultsField = new Text(this, SWT.BORDER);
		resultsField.setEditable(false);
		resultsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(this, SWT.NONE);

		executeButton = new Button(this, SWT.NONE);
		executeButton.setEnabled(false);
		executeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, Object> args = argReader.getValue();
				serviceMethod.execute(args,
						new WriteFunction<Map<String, Object>>() {

							@Override
							public void writeValue(
									final Map<String, Object> newValue) {
								SWTUtil.swtThread(ServicePanel.this).execute(
										new Runnable() {

											@Override
											public void run() {
												resultsField.setText(String
														.valueOf(newValue));
											}
										});
								resultWriter.write(newValue);
							}
						}, new WriteFunction<Exception>() {

							@Override
							public void writeValue(final Exception newValue) {
								SWTUtil.swtThread(ServicePanel.this).execute(
										new Runnable() {

											@Override
											public void run() {
												resultsField.setText(String
														.valueOf(newValue
																.getMessage()));
											}
										});

							}

						});
			}
		});
		executeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
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
			serviceField.setText("");
			return;
		}

		try {
			serviceMethod = ServiceRegistry.getDefault().findServiceMethod(
					serviceName);
			serviceField.setText(serviceName);
		} catch (Exception ex) {
			serviceField.setText(ex.getMessage());
			return;
		}

		if (argumentExpression == null || resultExpression == null) {
			return;
		}

		argReader = PVManager.read(argumentExpression)
				.readListener(new PVReaderListener<Map<String, Object>>() {

					@Override
					public void pvChanged(
							final PVReaderEvent<Map<String, Object>> event) {
						SWTUtil.swtThread().execute(new Runnable() {

							@Override
							public void run() {
								argumentField.setText(String
										.valueOf(event.getPvReader().getValue()));
							}
						});
					}
				}).maxRate(TimeDuration.ofHertz(25));

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

	public void configureArgumentMap(String argumentPrefix) {
		if (serviceMethod == null) {
			return;
		}
		ReadMap<Object> map = readMapOf(Object.class);
		for (String argumentName : serviceMethod.getArgumentDescriptions()
				.keySet()) {
			map.add(latestValueOf(channel(argumentPrefix + argumentName)).as(
					argumentName));
		}
		setArgumentExpression(map);
	}

	public void configureResultMap(String resultPrefix) {
		if (serviceMethod == null) {
			return;
		}
		WriteMap<Object> map = writeMapOf(Object.class);
		for (String resultName : serviceMethod.getResultDescriptions().keySet()) {
			map.add(channel(resultPrefix + resultName).as(resultName));
		}
		setResultExpression(map);
	}

}
