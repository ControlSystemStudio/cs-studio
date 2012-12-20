package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.util.time.TimeDuration.ofHertz;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.channel.widgets.TunerChannelTableModel.Item;
import org.csstudio.channel.widgets.TunerSetpointTableModel.TableItem;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.data.VDouble;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class TunerWidget extends AbstractChannelQueryResultWidget implements
		ConfigurableWidget, ISelectionProvider {

	private boolean editingDone = false;
	private boolean setpointEditingDone = false;

	private Table channelTable;
	private Table stepValueTable;
	private Text stepCount;
	private Text stepSize;
	private TableViewer channelTableViewer;
	private TableViewer setpointTableViewer;

	private ErrorBar errorBar;

	private Button btnApply;

	private AbstractSelectionProviderWrapper selectionProvider;

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

		errorBar = new ErrorBar(this, SWT.NONE);
		FormData fd_errorBar = new FormData();
		fd_errorBar.top = new FormAttachment(0, 0);
		fd_errorBar.left = new FormAttachment(0, 2);
		fd_errorBar.right = new FormAttachment(100, -2);
		errorBar.setLayoutData(fd_errorBar);
		errorBar.setMarginBottom(5);

		// create the composite for the channel table
		Composite channelTableComposite = new Composite(this,
				SWT.DOUBLE_BUFFERED);
		FormData fd_channelTableComposite = new FormData();
		fd_channelTableComposite.bottom = new FormAttachment(100, -70);
		fd_channelTableComposite.right = new FormAttachment(60);
		fd_channelTableComposite.top = new FormAttachment(errorBar, 2);
		fd_channelTableComposite.left = new FormAttachment(0, 2);
		channelTableComposite.setLayoutData(fd_channelTableComposite);

		// create the channel/property/value/weight table
		channelTableViewer = new TableViewer(channelTableComposite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		channelTable = channelTableViewer.getTable();
		channelTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		channelTable.setLinesVisible(true);
		channelTable.setHeaderVisible(true);
		channelTableViewer
				.setContentProvider(new TunerStructuredContentProvider());

		updateChannelTable();

		selectionProvider = new AbstractSelectionProviderWrapper(
				channelTableViewer, this) {
			@Override
			protected ISelection transform(IStructuredSelection selection) {
				if (selection != null) {
					Collection<Channel> channels = new ArrayList<Channel>();
					for (Object o : selection.toList()) {
						Item item = (Item) o;
						if (item != null) {
							channels.add(item.getChannel());
						}
					}
					return new StructuredSelection(new ChannelViewerAdaptable(
							channels, TunerWidget.this));
				} else
					return new StructuredSelection();
			}
		};

		// create setpoint table composite
		Composite stepTableComposite = new Composite(this, SWT.DOUBLE_BUFFERED);
		FormData fd_stepValueTable = new FormData();
		fd_stepValueTable.right = new FormAttachment(100, -2);
		fd_stepValueTable.top = new FormAttachment(errorBar, 2);
		fd_stepValueTable.bottom = new FormAttachment(channelTableComposite, 0,
				SWT.BOTTOM);
		fd_stepValueTable.left = new FormAttachment(channelTableComposite, 2);
		stepTableComposite.setLayoutData(fd_stepValueTable);

		// create setpoint table

		setpointTableViewer = new TableViewer(stepTableComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		setpointTableViewer
				.setContentProvider(new IStructuredContentProvider() {

					private TunerSetpointTableModelListener listener = new TunerSetpointTableModelListener() {

						@Override
						public void setpointsChanged() {
							if (!setpointTableViewer.isCellEditorActive()
									|| setpointEditingDone) {
								// TODO A minor edit to a single value should
								// not result is redrawing the entire table.
								updateStepValueTable();
								setpointTableViewer.refresh();
								enableApplyButton();
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
		stepValueTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
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
				setTunerSetpointTableModel(new TunerSetpointTableModel(
						calculateSetpoints()));
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

		addPropertyChangeListener(new PropertyChangeListener() {

			List<String> properties = Arrays.asList("channels", "properties",
					"tags");

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					updateChannelTable();
				}
				// if channels has changed then the setpoint table needs to be
				// disposed.
				if (evt.getPropertyName().equals("channels")) {
					setTunerSetpointTableModel(null);
				}
				if (evt.getPropertyName().equals("setpoints")) {
					updateStepValueTable();
					enableApplyButton();
				}
			}

		});

		tunerChannelTableModel
				.addPVTableModelListener(new TunerChannelTableModelListener() {

					@Override
					public void dataChanged() {
						channelTableViewer.setInput(tunerChannelTableModel);
						channelTableViewer.refresh();
					}
				});
	}

	/**
	 * Determine is the apply button should be enabled based on the avaliability
	 * of setpoints to be written.
	 */
	private void enableApplyButton() {
		if (tunerSetpointTableModel != null) {
			if (tunerSetpointTableModel.getNumberOfSteps() > 0) {
				btnApply.setEnabled(true);
			} else {
				btnApply.setEnabled(false);
			}
		}
	}

	/**
	 * redraws the channelTable
	 */
	private void updateChannelTable() {
		// Dispose existing columns
		for (TableColumn column : channelTableViewer.getTable().getColumns()) {
			column.dispose();
		}

		TableColumnLayout channelTablelayout = new TableColumnLayout();
		channelTableViewer.getTable().getParent().setLayout(channelTablelayout);

		TableViewerColumn tableViewerColumnChannel = new TableViewerColumn(
				channelTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnChannel.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Item item = ((Item) element);
				return item == null ? "" : item.getChannelName();
			}
		});
		TableColumn tblclmnChannel = tableViewerColumnChannel.getColumn();
		tblclmnChannel.setWidth(100);
		tblclmnChannel.setText("Channel");
		channelTablelayout.setColumnData(tblclmnChannel, new ColumnWeightData(
				60));

		// create columns based on the properties/tags selected to be show.
		for (String property : this.properties) {
			TableViewerColumn tableViewerColumnProperty = new TableViewerColumn(
					channelTableViewer, SWT.DOUBLE_BUFFERED);

			final String propertyName = property;
			tableViewerColumnProperty
					.setLabelProvider(new ColumnLabelProvider() {

						public String getText(Object element) {
							Item item = ((Item) element);
							if (element != null && item.getChannel() != null) {
								if (item.getChannel().getPropertyNames()
										.contains(propertyName)) {
									String value = item.getChannel()
											.getProperty(propertyName)
											.getValue();
									return value != null ? value : "";
								}
							}
							return "";
						}
					});
			TableColumn tblclmnProp = tableViewerColumnProperty.getColumn();
			tblclmnProp.setWidth(60);
			tblclmnProp.setText(property);
			channelTablelayout.setColumnData(tblclmnProp, new ColumnWeightData(
					30));
		}

		for (String tag : this.tags) {
			TableViewerColumn tableViewerColumnTag = new TableViewerColumn(
					channelTableViewer, SWT.DOUBLE_BUFFERED);

			final String tagName = tag;
			tableViewerColumnTag.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					Item item = (Item) element;
					return element != null && item.getChannel() != null ? ((Item) element)
							.getChannel().getTag(tagName) == null ? ""
							: "tagged" : "";
				}
			});
			TableColumn tblclmnTag = tableViewerColumnTag.getColumn();
			tblclmnTag.setWidth(50);
			tblclmnTag.setText(tag);
			channelTablelayout.setColumnData(tblclmnTag, new ColumnWeightData(
					30));

		}

		TableViewerColumn tableViewerColumnMin = new TableViewerColumn(
				channelTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnMin.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Item item = (Item) element;
				if (item != null) {
					VDouble value = item.getValue();
					return value != null && value.getLowerCtrlLimit() != null ? value
							.getLowerCtrlLimit().toString() : "N/A";
				}
				return "";
			}
		});
		TableColumn tblclmnMin = tableViewerColumnMin.getColumn();
		tblclmnMin.setWidth(60);
		tblclmnMin.setText("Min");
		channelTablelayout.setColumnData(tblclmnMin, new ColumnWeightData(30));

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(
				channelTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				Item item = ((Item) element);
				return item != null && item.getValue() != null ? item
						.getValue().getValue().toString() : "";
			}
		});
		TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
		tblclmnValue.setWidth(60);
		tblclmnValue.setText("Value");
		channelTablelayout
				.setColumnData(tblclmnValue, new ColumnWeightData(30));

		TableViewerColumn tableViewerColumnMax = new TableViewerColumn(
				channelTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnMax.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				if (element != null) {
					VDouble value = ((Item) element).getValue();
					return value != null && value.getUpperCtrlLimit() != null ? value
							.getUpperCtrlLimit().toString() : "N/A";
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
				channelTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnWeight.setEditingSupport(new EditingSupport(
				channelTableViewer) {
			private boolean initialized = false;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
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

			@Override
			protected Object getValue(Object element) {
				Double value = ((Item) element).getWeight();
				return String.valueOf(value);
			}

			@Override
			protected void setValue(Object element, Object value) {
				try {
					editingDone = true;
					Item item = ((Item) element);
					if (tunerChannelTableModel != null) {
						if (value == null || value.toString().trim().isEmpty()) {
							tunerChannelTableModel.updateWeight(item, 0.0);
						} else {
							tunerChannelTableModel.updateWeight(item,
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
				return element == null ? "" : ((Item) element).getWeight()
						.toString();
			}
		});
		TableColumn tblclmnWeight = tableViewerColumnWeight.getColumn();
		tblclmnWeight.setWidth(100);
		tblclmnWeight.setText("Weight");
		channelTablelayout.setColumnData(tblclmnWeight,
				new ColumnWeightData(30));

		channelTableViewer.getTable().getParent().layout();
		channelTableViewer.refresh();
	}

	/**
	 * Based on the calculated setpoint this method creates and populates the
	 * setpoint table
	 */
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
				tableViewerColumnValue
						.setEditingSupport(new SetpointTableCellEditor(
								setpointTableViewer, columnCount - 1));
				TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
				tblclmnValue.setWidth(60);
				tblclmnValue.setText("Step" + columnCount);
				stepTablelayout.setColumnData(tblclmnValue,
						new ColumnWeightData(20));
				columnCount++;
			}
			setpointTableViewer.setInput(tunerSetpointTableModel);
		} else {
			setpointTableViewer.setInput(null);
		}
		setpointTableViewer.getTable().getParent().layout();
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

	private class SetpointTableCellEditor extends EditingSupport {
		final int columnCount;

		private boolean initialized = false;

		public SetpointTableCellEditor(ColumnViewer viewer, int columntCount) {
			super(viewer);
			this.columnCount = columntCount;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {

			TextCellEditor editor = new TextCellEditor(stepValueTable) {
				@Override
				public void activate(
						ColumnViewerEditorActivationEvent activationEvent) {
					if (activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
						super.activate(activationEvent);
					} else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
						this.setValue(String.valueOf(activationEvent.character));
					}
				}
			};
			CellEditor[] editors = new CellEditor[1];
			editors[0] = editor;
			setpointTableViewer.setCellEditors(editors);
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(((TableItem) element).getValue().get(
					columnCount));
		}

		@Override
		protected void setValue(Object element, Object value) {
			try {
				setpointEditingDone = true;
				String rowIdentifier = ((TableItem) element).channelName;
				if (rowIdentifier != null && tunerSetpointTableModel != null) {
					if (value == null || value.toString().trim().isEmpty()) {
						// tunerSetpointTableModel.setCalculatedSetpoint(columnCount,
						// rowIdentifier, value);
					} else {
						tunerSetpointTableModel.setCalculatedSetpoint(
								columnCount, rowIdentifier,
								Double.valueOf(value.toString()));
					}
					if (!initialized) {
						((Text) getCellEditor(element).getControl())
								.setSelection(1);
					}
				}
			} finally {
				setpointEditingDone = false;
				setpointTableViewer.refresh();
			}
		}

	}

	// Model for the channel, property, value and weight
	// TODO simply this model
	private TunerChannelTableModel tunerChannelTableModel = new TunerChannelTableModel(
			null);

	// Model for the setpoints
	private TunerSetpointTableModel tunerSetpointTableModel;

	// List of properties and tags selected to be displayed
	private List<String> properties = Collections.emptyList();
	private List<String> tags = Collections.emptyList();

	// the channelName, tagName, propertyName
	private String sortElement;

	// pv
	private PV<Map<String, VDouble>, Map<String, Double>> pv;

	// list of weights
	// private Map<String, Double> weights = new HashMap<String, Double>();

	// last ChannelQuery result
	private Result channelQueryResult;

	public Collection<Channel> getChannels() {
		return tunerChannelTableModel.getChannels();
	}

	private void setChannels(Collection<Channel> channels) {
		if (channels != null) {
			this.properties = new ArrayList<String>(
					ChannelUtil.getPropertyNames(channels));
			this.tags = new ArrayList<String>(
					ChannelUtil.getAllTagNames(channels));
			this.tunerChannelTableModel.setChannels(new ArrayList<Channel>(
					channels));
			reconnect();
		} else {
			this.properties = Collections.emptyList();
			this.tags = Collections.emptyList();
			this.tunerChannelTableModel.setChannels(null);
		}

		// TODO null should be replaced oldValue
		changeSupport.firePropertyChange("channels", null, channels);
	}

	public Collection<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		List<String> oldProperties = this.properties;
		this.properties = properties;
		changeSupport.firePropertyChange("properties", oldProperties,
				properties);
	}

	public Collection<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		List<String> oldTags = this.tags;
		this.tags = tags;
		changeSupport.firePropertyChange("tags", oldTags, tags);
	}

	@Override
	protected void queryCleared() {
		errorBar.setException(null);
		setChannels(null);
		setTunerSetpointTableModel(null);
		resetWidget();
	}

	@Override
	protected void queryExecuted(Result result) {
		errorBar.setException(result.exception);
		setChannels(result.channels);
		setTunerSetpointTableModel(null);
		resetWidget();
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
		this.channelQueryResult = result;
		final List<String> channelNames = new ArrayList<String>();
		Exception ex = result.exception;
		if (ex == null) {
			channelNames.addAll(ChannelUtil.getChannelNames(result.channels));
		}
		return Collections.unmodifiableList(channelNames);
	}

	private void reconnect() {

		pv = PVManager
				.readAndWrite(
						mapOf(latestValueOf(channels(ChannelUtil
								.getChannelNames(tunerChannelTableModel
										.getChannels()), VDouble.class,
								Double.class)))).notifyOn(SWTUtil.swtThread())
				.asynchWriteAndMaxReadRate(ofHertz(2));

		pv.addPVReaderListener(new PVReaderListener() {

			@Override
			public void pvChanged() {
				if (pv.getValue() != null) {
					setLastResult(pv.getValue());
				}
			}
		});

		pv.addPVWriterListener(new PVWriterListener() {

			@Override
			public void pvWritten() {
			}
		});
	}

	private List<Map<String, Double>> calculateSetpoints() {
		int stepCount = Integer.valueOf(this.stepCount.getText());
		List<Map<String, Double>> calculatedSetpoints = new ArrayList<Map<String, Double>>(
				stepCount);
		int stepSize = Integer.valueOf(this.stepSize.getText());
		for (int i = 0; i < stepCount; i++) {
			Map<String, Double> calculatedSetpointMap = new LinkedHashMap<String, Double>(
					tunerChannelTableModel.getRowsize());
			Item[] tableItems = tunerChannelTableModel.getItems();
			for (Item item : tableItems) {
				String key = item.getChannel().getName();
				double value = item.getValue().getValue()
						+ (item.getWeight() * stepSize * (i + 1));
				calculatedSetpointMap.put(key, value);
			}
			calculatedSetpoints.add(calculatedSetpointMap);
		}
		return calculatedSetpoints;
	}

	private void setTunerSetpointTableModel(
			TunerSetpointTableModel tunerSetpointTableModel) {
		TunerSetpointTableModel oldValue = this.tunerSetpointTableModel;
		if (this.tunerSetpointTableModel != null
				&& this.tunerSetpointTableModel.equals(tunerSetpointTableModel)) {
			return;
		}
		this.tunerSetpointTableModel = tunerSetpointTableModel;
		changeSupport.firePropertyChange("setpoints", oldValue,
				this.tunerSetpointTableModel);
	}

	private void setLastResult(Map<String, VDouble> value) {
		if (!channelTableViewer.isCellEditorActive() || editingDone) {
			tunerChannelTableModel.updateValues(value);
			channelTableViewer.setInput(tunerChannelTableModel);
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
			return ((TunerChannelTableModel) inputElement).getItems();
		}
	}

	private void writeCalculatedNextStep() {
		pv.write(tunerSetpointTableModel.getNextSetpoints());
	}

	private boolean configurable = true;

	private TunerConfigurationDialog dialog;

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldConfigurable = configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldConfigurable,
				configurable);
	}

	@Override
	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new TunerConfigurationDialog(this);
		dialog.open();
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);

	}

	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		channelTable.setMenu(menu);
	}
}
