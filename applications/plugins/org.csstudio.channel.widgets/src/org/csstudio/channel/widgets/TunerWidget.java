package org.csstudio.channel.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.statisticsOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vNumber;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleOf;
import static org.epics.util.time.TimeDuration.ofHertz;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.ChannelQuery.Result;

import org.csstudio.channel.widgets.TunerSetpointTableModel.TableItem;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.data.VDouble;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class TunerWidget extends AbstractChannelQueryResultWidget {

	private boolean editingDone = false;

	private Table channelTable;
	private Table stepValueTable;
	private Text stepCount;
	private Text stepSize;
	private TableViewer channelTableViewer;
	private TableViewer setpointTableViewer;

	private Button btnApply;

	public TunerWidget(Composite parent, int style) {
		super(parent, style);
		// Close PV on dispose
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pv != null) {
					pv.close();
					pv = null;
				}
			}
		});

		draw();

	}

	private void draw() {

		setLayout(new FormLayout());

		Composite channelTableComposite = new Composite(this, SWT.NONE);
		FormData fd_channelTableComposite = new FormData();
		fd_channelTableComposite.bottom = new FormAttachment(100, -70);
		fd_channelTableComposite.right = new FormAttachment(60);
		fd_channelTableComposite.top = new FormAttachment(0, 2);
		fd_channelTableComposite.left = new FormAttachment(0, 2);
		channelTableComposite.setLayoutData(fd_channelTableComposite);
		
		TableColumnLayout channelTablelayout = new TableColumnLayout();
		channelTableComposite.setLayout(channelTablelayout);
		
		channelTableViewer = new TableViewer(channelTableComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		channelTable = channelTableViewer.getTable();
		channelTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		channelTable.setLinesVisible(true);
		channelTable.setHeaderVisible(true);
		channelTableViewer
				.setContentProvider(new TunerStructuredContentProvider());

		TableViewerColumn tableViewerColumnChannel = new TableViewerColumn(
				channelTableViewer, SWT.NONE);
		tableViewerColumnChannel.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return element == null ? ""
						: ((Entry<String, VDouble>) element).getKey();
			}
		});
		TableColumn tblclmnChannel = tableViewerColumnChannel.getColumn();
		tblclmnChannel.setWidth(100);
		tblclmnChannel.setText("Channel");
		channelTablelayout.setColumnData(tblclmnChannel, new ColumnWeightData(60));

		TableViewerColumn tableViewerColumnMin = new TableViewerColumn(
				channelTableViewer, SWT.NONE);
		tableViewerColumnMin.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				if (element != null) {
					VDouble value = ((Entry<String, VDouble>) element)
							.getValue();
					return value.getLowerCtrlLimit() == null ? "N/A" : value
							.getLowerCtrlLimit().toString();
				}
				return "";
			}
		});
		TableColumn tblclmnMin = tableViewerColumnMin.getColumn();
		tblclmnMin.setWidth(60);
		tblclmnMin.setText("Min");
		channelTablelayout.setColumnData(tblclmnMin, new ColumnWeightData(30));

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(
				channelTableViewer, SWT.NONE);
		tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return element == null ? ""
						: ((Entry<String, VDouble>) element).getValue()
								.getValue().toString();
			}
		});
		TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
		tblclmnValue.setWidth(60);
		tblclmnValue.setText("Value");
		channelTablelayout.setColumnData(tblclmnValue, new ColumnWeightData(30));

		TableViewerColumn tableViewerColumnMax = new TableViewerColumn(
				channelTableViewer, SWT.NONE);
		tableViewerColumnMax.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				if (element != null) {
					VDouble value = ((Entry<String, VDouble>) element)
							.getValue();
					return value.getUpperCtrlLimit() == null ? "N/A" : value
							.getUpperCtrlLimit().toString();
				}
				return "";
			}
		});
		TableColumn tblclmnMax = tableViewerColumnMax.getColumn();
		tblclmnMax.setResizable(true);
		tblclmnMax.setMoveable(false);
		tblclmnMax.setWidth(60);
		tblclmnMax.setText("Max");
		channelTablelayout.setColumnData(tblclmnMax, new ColumnWeightData(30));

		TableViewerColumn tableViewerColumnWeight = new TableViewerColumn(
				channelTableViewer, SWT.NONE);
		tableViewerColumnWeight.setEditingSupport(new EditingSupport(
				channelTableViewer) {
			private boolean initialized = false;

			protected boolean canEdit(Object element) {
				return true;
			}

			protected CellEditor getCellEditor(Object element) {
				TextCellEditor editor = new TextCellEditor(channelTable) {
					@Override
					public void activate(
							ColumnViewerEditorActivationEvent activationEvent) {
						if (activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
							super.activate(activationEvent);
						} else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
							this.setValue(String
									.valueOf(activationEvent.character));
						}
					}
				};
				CellEditor[] editors = new CellEditor[1];
				editors[0] = editor;
				channelTableViewer.setCellEditors(editors);
				return editor;
			}

			protected Object getValue(Object element) {
				Double value = weights.get(((Entry<String, VDouble>) element)
						.getKey());
				return String.valueOf(value);
			}

			protected void setValue(Object element, Object value) {
				try {
					String channel = ((Entry<String, VDouble>) element)
							.getKey();
					if (channel != null && weights != null) {
						if (value == null || value.toString().trim().isEmpty()) {
							weights.put(channel, 0.0);
						} else {
							weights.put(channel,
									Double.valueOf(value.toString()));
						}
						if (!initialized) {
							((Text) getCellEditor(element).getControl())
									.setSelection(1);
						}
					}
				} finally {
					editingDone = false;
					channelTableViewer.refresh();
				}
			}
		});
		tableViewerColumnWeight.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return element == null ? "" : weights.get(
						((Entry<String, VDouble>) element).getKey()).toString();
			}
		});
		TableColumn tblclmnWeight = tableViewerColumnWeight.getColumn();
		tblclmnWeight.setWidth(100);
		tblclmnWeight.setText("Weight");
		channelTablelayout.setColumnData(tblclmnWeight, new ColumnWeightData(30));

		
		Composite stepTableComposite = new Composite(this, SWT.NONE);
		FormData fd_stepValueTable = new FormData();
		fd_stepValueTable.right = new FormAttachment(100, -2);
		fd_stepValueTable.top = new FormAttachment(0, 2);
		fd_stepValueTable.bottom = new FormAttachment(channelTableComposite, 0,
				SWT.BOTTOM);
		fd_stepValueTable.left = new FormAttachment(channelTableComposite, 2);
		stepTableComposite.setLayoutData(fd_stepValueTable);
		
		setpointTableViewer = new TableViewer(stepTableComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		setpointTableViewer
				.setContentProvider(new IStructuredContentProvider() {

					private SetpointTableModelListener listener = new SetpointTableModelListener() {

						@Override
						public void setpointsChanged() {
							if (!setpointTableViewer.isCellEditorActive()) {
								updateStepValueTable();
								setpointTableViewer.refresh();
							}
						}
					};

					@Override
					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {
						if (oldInput != null) {
							((TunerSetpointTableModel) oldInput)
									.addSetpointTableModelListener(listener);
						}
						if (newInput != null) {
							((TunerSetpointTableModel) newInput)
									.addSetpointTableModelListener(listener);
						}
					}

					@Override
					public void dispose() {

					}

					@Override
					public Object[] getElements(Object inputElement) {
						return ((TunerSetpointTableModel) inputElement)
								.getItems();
					}
				});
		stepValueTable = setpointTableViewer.getTable();
		stepValueTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		stepValueTable.setLinesVisible(true);
		stepValueTable.setHeaderVisible(true);

		updateStepValueTable();

		Label lblStepCount = new Label(this, SWT.NONE);
		FormData fd_lblStepCount = new FormData();
		fd_lblStepCount.left = new FormAttachment(channelTableComposite, 2,
				SWT.LEFT);
		fd_lblStepCount.top = new FormAttachment(channelTableComposite, 5);
		lblStepCount.setLayoutData(fd_lblStepCount);
		lblStepCount.setText("Step Count:");
		

		stepCount = new Text(this, SWT.BORDER);
		FormData fd_stepCount = new FormData();
		fd_stepCount.left = new FormAttachment(lblStepCount, 5);
		fd_stepCount.right = new FormAttachment(lblStepCount, 84, SWT.RIGHT);
		fd_stepCount.top = new FormAttachment(channelTableComposite, 5);

		stepCount.setLayoutData(fd_stepCount);

		Label lblStepSize = new Label(this, SWT.NONE);
		FormData fd_lblStepSize = new FormData();
		fd_lblStepSize.top = new FormAttachment(channelTableComposite, 5);
		fd_lblStepSize.left = new FormAttachment(stepCount, 5);
		lblStepSize.setLayoutData(fd_lblStepSize);
		lblStepSize.setText("Step Size:");

		stepSize = new Text(this, SWT.BORDER);
		FormData fd_stepSize = new FormData();
		fd_stepSize.left = new FormAttachment(lblStepSize, 5);
		fd_stepSize.top = new FormAttachment(channelTableComposite, 5);
		stepSize.setLayoutData(fd_stepSize);

		Button btnGenerate = new Button(this, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				calculateSetpoints();
				if (tunerSetpointTableModel.getNumberOfSteps() > 0) {
					btnApply.setEnabled(true);
				}
			}
		});
		FormData fd_btnGenerate = new FormData();
		fd_btnGenerate.bottom = new FormAttachment(stepCount, 0, SWT.BOTTOM);
		fd_btnGenerate.left = new FormAttachment(stepSize, 5);
		fd_btnGenerate.top = new FormAttachment(channelTableComposite, 5);
		btnGenerate.setLayoutData(fd_btnGenerate);
		btnGenerate.setText("Generate Setpoints");

		btnApply = new Button(this, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				writeCalculatedNextStep();
			}
		});
		FormData fd_btnApply = new FormData();
		fd_btnApply.left = new FormAttachment(stepValueTable, 2);
		fd_btnApply.right = new FormAttachment(100, -2);
		fd_btnApply.bottom = new FormAttachment(100, -2);
		fd_btnApply.top = new FormAttachment(stepSize, 6);
		
		btnApply.setLayoutData(fd_btnApply);
		btnApply.setText("Apply");
		btnApply.setEnabled(false);
	}

	//

	@SuppressWarnings("unused")
	private void updateStepValueTable() {
		for (TableColumn column : setpointTableViewer.getTable().getColumns()) {
			column.dispose();
		}
		TableColumnLayout stepTablelayout = new TableColumnLayout();
		setpointTableViewer.getTable().getParent().setLayout(stepTablelayout);
		
		if (tunerSetpointTableModel != null
				&& tunerSetpointTableModel.getCalculatedSetpoints() != null
				&& tunerSetpointTableModel.getNumberOfSteps() > 0) {
			int columnCount = 1;
			for (Map<String, Double> entry : tunerSetpointTableModel
					.getCalculatedSetpoints()) {
				TableViewerColumn tableViewerColumnValue = new TableViewerColumn(
						setpointTableViewer, SWT.NONE);
				tableViewerColumnValue
						.setLabelProvider(new SetpointTableLabelProvider(
								columnCount - 1));
				TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
				tblclmnValue.setWidth(60);
				tblclmnValue.setText("Step" + columnCount);
				stepTablelayout.setColumnData(tblclmnValue, new ColumnWeightData(20));
				columnCount++;
			}
			setpointTableViewer.setInput(tunerSetpointTableModel);
		}else{
		setpointTableViewer.setInput(null);
		}
		setpointTableViewer.refresh();
	}

	private class SetpointTableLabelProvider extends ColumnLabelProvider {
		final int columnCount;

		public SetpointTableLabelProvider(int columnCount) {
			this.columnCount = columnCount;
		}

		@Override
		public String getText(Object element) {
			return element == null ? "" : ((TableItem) element).getValue()
					.get(columnCount).toString();
		}
	}

	// list of channels
	private Collection<Channel> channels;
	private List<String> channelNames;
	// pv
	private PV<Map<String, VDouble>, Map<String, Double>> pv;

	// list of weights
	private Map<String, Double> weights = new HashMap<String, Double>();

	// last ChannelQuery result
	private Result channelQueryResult;

	@Override
	protected void queryCleared() {
		setChannelNames(null);
		setTunerSetpointTableModel(null);
		resetWidget();
	}

	@Override
	protected void queryExecuted(Result result) {
		setChannelNames(null);
		setTunerSetpointTableModel(null);
		resetWidget();
		List<String> finalChannels = getResultChannels(result);
		if (finalChannels != null && !finalChannels.isEmpty()) {
			setChannelNames(finalChannels);
		} else if (finalChannels == null) {
			// assumes the entered string to be an waveform pv
		}

	}

	private void resetWidget() {
		stepCount.setText("0");
		stepSize.setText("0");
		btnApply.setEnabled(false);
		setTunerSetpointTableModel(null);
	}

	private List<String> getResultChannels(Result result) {

		if (result == null)
			return null;

		// setLastError(result.exception);
		this.channelQueryResult = result;
		final List<String> channelNames = new ArrayList<String>();
		Exception ex = result.exception;
		if (ex == null) {
			channelNames.addAll(ChannelUtil.getChannelNames(result.channels));
		}
		return Collections.unmodifiableList(channelNames);
	}

	private Map<String, VDouble> lastValue;

	private TunerSetpointTableModel tunerSetpointTableModel;

	private void reconnect() {

		pv = PVManager
				.readAndWrite(
						mapOf(latestValueOf(channels(channelNames,
								VDouble.class, Double.class))))
				.notifyOn(SWTUtil.swtThread())
				.asynchWriteAndMaxReadRate(ofHertz(2));

		pv.addPVReaderListener(new PVReaderListener() {

			@Override
			public void pvChanged() {
				// setLastError(pv.lastException());
				if (pv.getValue() != null) {
					setLastResult(pv.getValue());
				}
			}
		});

		pv.addPVWriterListener(new PVWriterListener() {

			@Override
			public void pvWritten() {
				// TODO Auto-generated method stub

				System.out.println("write completed");
			}
		});
	}

	private void calculateSetpoints() {
		int stepCount = Integer.valueOf(this.stepCount.getText());
		List<Map<String, Double>> calculatedSetpoints = new ArrayList<Map<String, Double>>(
				stepCount);
		int stepSize = Integer.valueOf(this.stepSize.getText());
		for (int i = 0; i < stepCount; i++) {
			Map<String, Double> calculatedSetpointMap = new HashMap<String, Double>(
					lastValue.size());
			for (Entry<String, VDouble> entry : lastValue.entrySet()) {
				String key = entry.getKey();
				double value = entry.getValue().getValue()
						+ (weights.get(key) * stepSize * (i + 1));
				calculatedSetpointMap.put(key, value);
			}
			calculatedSetpoints.add(calculatedSetpointMap);
		}
		setTunerSetpointTableModel(new TunerSetpointTableModel(
				calculatedSetpoints));
	}

	private void setTunerSetpointTableModel(
			TunerSetpointTableModel tunerSetpointTableModel) {
		if (this.tunerSetpointTableModel != null
				&& this.tunerSetpointTableModel.equals(tunerSetpointTableModel)) {
			return;
		}
		this.tunerSetpointTableModel = tunerSetpointTableModel;
		updateStepValueTable();
	}

	private void setChannels(List<Channel> Channels) {
		setChannelNames(new ArrayList<String>(
				ChannelUtil.getChannelNames(Channels)));
	}

	private void setChannelNames(List<String> channelNames) {
		if (this.channelNames != null && this.channelNames.equals(channelNames)) {
			return;
		}
		this.channelNames = channelNames;
		this.weights.clear();
		if (this.channelNames != null && !this.channelNames.isEmpty()) {
			for (String channelName : channelNames) {
				this.weights.put(channelName, (double) 0);
			}
			reconnect();
		}
	}

	private void setLastResult(Map<String, VDouble> value) {
		if (!channelTableViewer.isCellEditorActive() || editingDone) {
			lastValue = Collections.unmodifiableMap(pv.getValue());
			channelTableViewer.setInput(lastValue.entrySet().toArray());
		}
	}

	private class TunerStructuredContentProvider implements
			IStructuredContentProvider {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public void dispose() {

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}
	}

	private void writeCalculatedNextStep() {
		pv.write(tunerSetpointTableModel.getNextSetpoints());
	}
}
