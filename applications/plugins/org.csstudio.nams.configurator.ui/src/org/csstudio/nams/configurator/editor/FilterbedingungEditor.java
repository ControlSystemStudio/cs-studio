
package org.csstudio.nams.configurator.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FilterbedingungEditor extends AbstractEditor<FilterbedingungBean> {

	public static class FilterTypeBean extends
			AbstractConfigurationBean<FilterTypeBean> {
		private SupportedFilterTypes _type;

		@Override
		public String getDisplayName() {
			return "(internal bean for storing selcted filter type in filter condition editor)"; //$NON-NLS-1$
		}

		@Override
		public int getID() {
			return 0;
		}

		public SupportedFilterTypes getType() {
			return this._type;
		}

		@Override
		public void setID(final int id) {
			// Ignored.
		}

		public void setType(final SupportedFilterTypes type) {
			final SupportedFilterTypes oldValue = this._type;
			this._type = type;
			this.pcs.firePropertyChange("type", oldValue, type); //$NON-NLS-1$
		}

		@Override
		protected void doUpdateState(final FilterTypeBean bean) {
			// Kommt nicht vor...
		}

		@Override
		public void setDisplayName(String name) {
			// nothing to do here
		}
	}

	public enum SupportedFilterTypes {
		JUNCTOR_CONDITION(Messages.FilterbedingungEditor_supported_filter_types_or_condition, JunctorConditionBean.class), STRING_CONDITION(
				Messages.FilterbedingungEditor_supported_filter_types_string_condition, StringFilterConditionBean.class), STRING_ARRAY_CONDITION(
				Messages.FilterbedingungEditor_supported_filter_types_string_array_condition, StringArrayFilterConditionBean.class), PV_CONDITION(
				Messages.FilterbedingungEditor_supported_filter_types_pv_condition, PVFilterConditionBean.class), TIMEBASED_CONDITION(
				Messages.FilterbedingungEditor_supported_filter_types_time_based_condition, TimeBasedFilterConditionBean.class);

		public static SupportedFilterTypes fromClass(final Class<?> cls) {
			for (final SupportedFilterTypes pValue : SupportedFilterTypes
					.values()) {
				if (pValue.getCls().equals(cls)) {
					return pValue;
				}
			}
			throw new RuntimeException("Unsupported Filtertype : " + cls); //$NON-NLS-1$
		}

		public static SupportedFilterTypes fromString(final String value) {
			for (final SupportedFilterTypes pValue : SupportedFilterTypes
					.values()) {
				if (pValue.getFilterName().equals(value)) {
					return pValue;
				}
			}
			throw new RuntimeException("Unsupported Filtertype : " + value); //$NON-NLS-1$
		}

		private final String filterName;

		private final Class<?> cls;

		private SupportedFilterTypes(final String name, final Class<?> cls) {
			this.filterName = name;
			this.cls = cls;
		}

		public Class<?> getCls() {
			return this.cls;
		}

		public String getFilterName() {
			return this.filterName;
		}

		@Override
		public String toString() {
			return this.filterName;
		}
	}

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterbedingungEditor"; //$NON-NLS-1$

	private static IProcessVariableConnectionService pvConnectionService;

	public static String getId() {
		return FilterbedingungEditor.EDITOR_ID;
	}

	public static void staticInject(
			final IProcessVariableConnectionService pvConnectionService) {
		FilterbedingungEditor.pvConnectionService = pvConnectionService;
	}

	private final FilterTypeBean selectedFilterType = new FilterTypeBean();

	private Text _nameTextEntry;
	private Combo _rubrikComboEntry;
	private Text _defaultMessageTextEntry;

	private Composite filterSpecificComposite;

	private StackLayout filterLayout;

	private Composite[] stackComposites;
	private Text stringCompareValueText;
	private Map<SupportedFilterTypes, AbstractConfigurationBean<?>> specificBeans;
	private Text pvChannelName;
	private Text pvCompareValue;
	private List arrayCompareValueList;
	private Text timeDelayText;
	private Button timeBehaviorCheck;
	private Text timeStartCompareText;
	private ListViewer arrayCompareValueListViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;

	private ScrolledForm mainForm;

	private Text timeStopCompareText;

	@Override
	public void createPartControl(final Composite parent) {
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.mainForm = this.formToolkit.createScrolledForm(parent);
		final Composite outermain = this.mainForm.getBody();
		outermain.setBackground(parent.getBackground());
		outermain.setLayout(new GridLayout(1, true));

		final Composite upperComposite = new Composite(outermain, SWT.NONE);
		upperComposite.setLayout(new GridLayout(this.NUM_COLUMNS, false));
		this._nameTextEntry = this.createTextEntry(upperComposite, Messages.FilterbedingungEditor_name,
				true);
		this._rubrikComboEntryViewer = this.createComboEntry(upperComposite,
				Messages.FilterbedingungEditor_category, true, AbstractEditor.getConfigurationBeanService()
						.getRubrikNamesForType(RubrikTypeEnum.FILTER_COND));
		this._rubrikComboEntry = this._rubrikComboEntryViewer.getCombo();
		this.addSeparator(upperComposite);
		this._defaultMessageTextEntry = this.createDescriptionTextEntry(
				upperComposite, Messages.FilterbedingungEditor_description);
		this.createTitledComboForEnumValues(upperComposite, Messages.FilterbedingungEditor_filtertype,
				SupportedFilterTypes.values(), this.selectedFilterType, "type"); //$NON-NLS-1$

		this.initializeAddOnBeans();

		this.selectedFilterType
				.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(final PropertyChangeEvent evt) {
						FilterbedingungEditor.this.filterLayout.topControl = FilterbedingungEditor.this.stackComposites[FilterbedingungEditor.this.selectedFilterType
								.getType().ordinal()];
						FilterbedingungEditor.this.filterSpecificComposite
								.layout();

						FilterbedingungEditor.this
								.getWorkingCopyOfEditorInput()
								.setFilterSpecificBean(
										(FilterConditionAddOnBean) FilterbedingungEditor.this.specificBeans
												.get(FilterbedingungEditor.this.selectedFilterType
														.getType()));
					}
				});

		this.filterSpecificComposite = new Composite(outermain, SWT.NONE);
		this.filterLayout = new StackLayout();
		this.filterSpecificComposite.setLayout(this.filterLayout);

		this.stackComposites = new Composite[5];

		// ConjunctionFilterComposite
		this.stackComposites[0] = new Composite(this.filterSpecificComposite,
				SWT.TOP);
		this.stackComposites[0].setLayout(new GridLayout(this.NUM_COLUMNS,
				false));
		new Label(this.stackComposites[0], SWT.NONE);
		final Label label = new Label(this.stackComposites[0], SWT.LEFT
				| SWT.WRAP);
		label.setText(Messages.FilterbedingungEditor_or_condition_warning1
				+ Messages.FilterbedingungEditor_or_condition_warning2
				+ Messages.FilterbedingungEditor_or_condition_warning3);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		this.createTextEntry(this.stackComposites[0], Messages.FilterbedingungEditor_filtercondition, false);
		this.createTextEntry(this.stackComposites[0], Messages.FilterbedingungEditor_filtercondition, false);

		// StringFilterComposite
		this.stackComposites[1] = new Composite(this.filterSpecificComposite,
				SWT.TOP);
		this.stackComposites[1].setLayout(new GridLayout(this.NUM_COLUMNS,
				false));
		final IConfigurationBean stringConfigurationBean = this.specificBeans
				.get(SupportedFilterTypes.STRING_CONDITION);

		this.createTitledComboForEnumValues(this.stackComposites[1],
				Messages.FilterbedingungEditor_compare_key, MessageKeyEnum.values(), stringConfigurationBean,
				StringFilterConditionBean.PropertyNames.keyValue.name());

		this.createTitledComboForEnumValues(this.stackComposites[1],
				Messages.FilterbedingungEditor_operator, StringRegelOperator.values(),
				stringConfigurationBean,
				StringFilterConditionBean.PropertyNames.operator.name());

		//			
		// createComboEntry(stackComposites[1],
		// "Operator", false, array2StringArray(StringRegelOperator
		// .values()));

		this.stringCompareValueText = this.createTextEntry(
				this.stackComposites[1], Messages.FilterbedingungEditor_compare_value, true);
		// StringArrayFilterComposite
		this.stackComposites[2] = new Composite(this.filterSpecificComposite,
				SWT.TOP);
		this.stackComposites[2].setLayout(new GridLayout(this.NUM_COLUMNS,
				false));

		final IConfigurationBean stringArrayConfigurationBean = this.specificBeans
				.get(SupportedFilterTypes.STRING_ARRAY_CONDITION);
		this.createTitledComboForEnumValues(this.stackComposites[2],
				Messages.FilterbedingungEditor_message_key, MessageKeyEnum.values(),
				stringArrayConfigurationBean,
				StringArrayFilterConditionBean.PropertyNames.keyValue.name());

		this.createTitledComboForEnumValues(this.stackComposites[2],
				Messages.FilterbedingungEditor_operator, StringRegelOperator.values(),
				stringArrayConfigurationBean,
				StringArrayFilterConditionBean.PropertyNames.operator.name());

		this.arrayCompareValueListViewer = this.createListEntry(
				this.stackComposites[2], Messages.FilterbedingungEditor_compare_values, true);
		this.arrayCompareValueList = this.arrayCompareValueListViewer.getList();
		final Text arrayNewCompareValueText = this.createTextEntry(
				this.stackComposites[2], Messages.FilterbedingungEditor_new_compare_value, true);
		arrayNewCompareValueText.addKeyListener(new KeyListener() {

			public void keyPressed(final KeyEvent e) {
				if (e.character == SWT.CR) {
					FilterbedingungEditor.this
							.addStringArrayCompareValue(arrayNewCompareValueText);
				}
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				// Not used yet
			}
		});
		final Button buttonAdd = this.createButtonEntry(
				this.stackComposites[2], Messages.FilterbedingungEditor_add_compare_value_button, true, 2);
		buttonAdd.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				// Not used yet
			}

			@Override
			public void mouseDown(final MouseEvent e) {
				// Not used yet
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				FilterbedingungEditor.this
						.addStringArrayCompareValue(arrayNewCompareValueText);
			}
		});
		final Button button = this.createButtonEntry(this.stackComposites[2],
				Messages.FilterbedingungEditor_remove_compare_value_button, true, 2);
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				// Not used yet
			}

			@Override
			public void mouseDown(final MouseEvent e) {
				if (FilterbedingungEditor.this.arrayCompareValueList
						.getSelectionIndex() > -1) {
					final String[] items = ((StringArrayFilterConditionBean) FilterbedingungEditor.this
							.getWorkingCopyOfEditorInput()
							.getFilterSpecificBean()).getCompareValues()
							.toArray(new String[0]);
					final ArrayList<String> itemList = new ArrayList<String>();
					for (int i = 0; i < items.length; i++) {
						if (FilterbedingungEditor.this.arrayCompareValueList
								.getSelectionIndex() != i) {
							itemList.add(items[i]);
						}
					}
					((StringArrayFilterConditionBean) FilterbedingungEditor.this
							.getWorkingCopyOfEditorInput()
							.getFilterSpecificBean())
							.setCompareValues(itemList);
				}
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				// Not used yet
			}
		});
		// PVComposite
		this.stackComposites[3] = new Composite(this.filterSpecificComposite,
				SWT.TOP);
		this.stackComposites[3].setLayout(new GridLayout(this.NUM_COLUMNS,
				false));
		this.pvChannelName = this.createTextEntry(this.stackComposites[3],
				Messages.FilterbedingungEditor_channel_name, true);

		final PVFilterConditionBean pvConfigurationBean = (PVFilterConditionBean) this.specificBeans
				.get(SupportedFilterTypes.PV_CONDITION);
		
		// Combo box for the data type
		ComboViewer suggestedTypeCombo = this.createTitledComboForEnumValues(this.stackComposites[3],
				Messages.FilterbedingungEditor_suggested_type, SuggestedProcessVariableType.values(),
				pvConfigurationBean,
				PVFilterConditionBean.PropertyNames.suggestedType.name());
		
		// Clear the value field because if the suggested type changes, the value is invalid.
		suggestedTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                pvCompareValue.setText("");
            }
        });
		
		this.createTitledComboForEnumValues(this.stackComposites[3],
				Messages.FilterbedingungEditor_operator, Operator.values(), pvConfigurationBean,
				PVFilterConditionBean.PropertyNames.operator.name());

		this.pvCompareValue = this.createTextEntry(this.stackComposites[3],
				Messages.FilterbedingungEditor_compare_value, true);
		
		this.pvCompareValue.addVerifyListener(new VerifyListener() {
            
            @Override
            public void verifyText(VerifyEvent e) {
                
                String string = e.text;
                
                SuggestedProcessVariableType t = pvConfigurationBean.getSuggestedType();
                
                System.out.println(string);
                
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    
                    if(t == SuggestedProcessVariableType.DOUBLE) {
                        if((chars[i] < '0' || chars[i] > '9') && (chars[i] != '.')) {
                            e.doit = false;
                            return;
                        }
                    } else if(t == SuggestedProcessVariableType.LONG) {
                        if (!('0' <= chars[i] && chars[i] <= '9')) {
                            e.doit = false;
                            return;
                        }
                    } else {
                        e.doit = true;
                        return;
                    }
                }

            }
        });
		
		final Button checkPVChannel = this.createButtonEntry(
				this.stackComposites[3], Messages.FilterbedingungEditor_pv_connection_test, true, 2);
		
		checkPVChannel.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(final MouseEvent e) {
			}

			public void mouseDown(final MouseEvent e) {
				final String channelName = FilterbedingungEditor.this.pvChannelName
						.getText();
				if ((channelName != null) && (channelName.length() > 0)) {
					try {
						final SuggestedProcessVariableType suggestedType = pvConfigurationBean
								.getSuggestedType();
						if (SuggestedProcessVariableType.DOUBLE
								.equals(suggestedType)) {
							FilterbedingungEditor.pvConnectionService
									.readValueSynchronously(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName), ValueType.DOUBLE);
						} else if (SuggestedProcessVariableType.LONG
								.equals(suggestedType)) {
							FilterbedingungEditor.pvConnectionService
									.readValueSynchronously(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName), ValueType.LONG);
						} else if (SuggestedProcessVariableType.STRING
								.equals(suggestedType)) {
							FilterbedingungEditor.pvConnectionService
									.readValueSynchronously(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName), ValueType.STRING);
						}
					} catch (final ConnectionException connectionException) {
						MessageDialog
								.openError(
										e.widget.getDisplay().getActiveShell(),
										Messages.FilterbedingungEditor_pv_connection_test_title
												+ channelName,
										Messages.FilterbedingungEditor_pv_connection_test_text
												+ Messages.FilterbedingungEditor_pv_connection_test_text2
												+ EditorUIUtils
														.throwableAsMessageString(connectionException));
						connectionException.printStackTrace();
						return;
					}
					MessageDialog
							.openInformation(
									e.widget.getDisplay().getActiveShell(),
									Messages.FilterbedingungEditor_pv_connection_test_title
											+ channelName,
									Messages.FilterbedingungEditor_pv_connection_test_success1
											+ Messages.FilterbedingungEditor_pv_connection_test_success2
											+ Messages.FilterbedingungEditor_pv_connection_test_success3
											+ Messages.FilterbedingungEditor_pv_connection_test_success4);
				}
			}

			public void mouseUp(final MouseEvent e) {
			}
		});

		// TimeBasedComposite
		this.stackComposites[4] = new Composite(this.filterSpecificComposite,
				SWT.TOP);
		this.stackComposites[4].setLayout(new GridLayout(this.NUM_COLUMNS,
				false));
		final IConfigurationBean timeBasedConfigurationBean = this.specificBeans
				.get(SupportedFilterTypes.TIMEBASED_CONDITION);

		this.timeDelayText = this.createTextEntry(this.stackComposites[4],
				Messages.FilterbedingungEditor_delay_time, true);
		this.timeBehaviorCheck = this.createCheckBoxEntry(
				this.stackComposites[4], Messages.FilterbedingungEditor_alarm_on_timeout, true);
		this.addSeparator(this.stackComposites[4]);
		this
				.createTitledComboForEnumValues(
						this.stackComposites[4],
						Messages.FilterbedingungEditor_start_key_value,
						MessageKeyEnum.values(),
						timeBasedConfigurationBean,
						TimeBasedFilterConditionBean.PropertyNames.startKeyValue
								.name());

		this
				.createTitledComboForEnumValues(
						this.stackComposites[4],
						Messages.FilterbedingungEditor_start_operator,
						StringRegelOperator.values(),
						timeBasedConfigurationBean,
						TimeBasedFilterConditionBean.PropertyNames.startOperator
								.name());

		this.timeStartCompareText = this.createTextEntry(
				this.stackComposites[4], Messages.FilterbedingungEditor_start_compare_value, true);
		this.addSeparator(this.stackComposites[4]);
		this.createTitledComboForEnumValues(this.stackComposites[4],
				Messages.FilterbedingungEditor_stop_key_value, MessageKeyEnum.values(),
				timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.confirmKeyValue
						.name());

		this.createTitledComboForEnumValues(this.stackComposites[4],
				Messages.FilterbedingungEditor_stop_operator, StringRegelOperator.values(),
				timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.confirmOperator
						.name());

		this.timeStopCompareText = this.createTextEntry(
				this.stackComposites[4], Messages.FilterbedingungEditor_stop_compare_value, true);

		// LinkedList<String> types = new LinkedList<String>();
		// for (JunctorConditionType type : JunctorConditionType.values()) {
		// types.add(type.toString());
		// }
		// junctorTypeCombo.setItems(types.toArray(new String[types.size()]));

		final FilterConditionAddOnBean filterSpecificBean = (FilterConditionAddOnBean) this
				.getOriginalEditorInput().getFilterSpecificBean();
		if (filterSpecificBean instanceof JunctorConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.JUNCTOR_CONDITION);
		} else if (filterSpecificBean instanceof StringFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.STRING_CONDITION);
		} else if (filterSpecificBean instanceof StringArrayFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.STRING_ARRAY_CONDITION);
		} else if (filterSpecificBean instanceof PVFilterConditionBean) {
			this.selectedFilterType.setType(SupportedFilterTypes.PV_CONDITION);
		} else if (filterSpecificBean instanceof TimeBasedFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.TIMEBASED_CONDITION);
		} else {
			throw new RuntimeException("Unsupported AddOnBean " //$NON-NLS-1$
					+ filterSpecificBean.getClass());
		}
		this.initDataBinding();
		this.initDND();
		// listener.handleEvent(null); // zur initialisierung
		// checkJunktionType();
	}

	// private void checkJunktionType() {
	// String typeString = (String) ((StructuredSelection)
	// junctorTypeComboViewer
	// .getSelection()).getFirstElement();
	// try {
	// JunctorConditionType type = JunctorConditionType
	// .valueOf(typeString);
	// junctorSecondFilterText
	// .setVisible(type != JunctorConditionType.NOT);
	// } catch (Exception e) {
	//
	// }
	// }

	@Override
	public boolean isDirty() {
		return super.isDirty()
				&& !(this.getWorkingCopyOfEditorInput().getFilterSpecificBean() instanceof JunctorConditionBean);
	}

	/*-class TextDropTarget extends DropTargetAdapter {
		private final Text text;
		private final boolean first;

		public TextDropTarget(Text junctorFilterText, boolean first) {
			text = junctorFilterText;
			this.first = first;
		}

		public void dragEnter(DropTargetEvent event) {
			try {
				IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
						.getTransfer().getSelection();
				if (selection.getFirstElement() instanceof FilterbedingungBean) {
					event.detail = DND.DROP_LINK;
				}
			} catch (Throwable e) {
			}
		}

		public void drop(DropTargetEvent event) {
			try {
				IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
						.getTransfer().getSelection();
				FilterbedingungBean bean = (FilterbedingungBean) selection
						.getFirstElement();
				JunctorConditionBean junctorBean = (JunctorConditionBean) specificBeans
						.get(SupportedFilterTypes.JUNCTOR_CONDITION);
				if (first) {
					junctorBean.setFirstCondition(bean);
				} else {
					junctorBean.setSecondCondition(bean);
				}
				text.setText(bean.getDisplayName());
			} catch (Throwable e) {
			}
		}
	}*/

	@Override
	public void setFocus() {
		this._nameTextEntry.setFocus();
	}

	@Override
	protected void doInit(final IEditorSite site, final IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		final DataBindingContext context = new DataBindingContext();

		final IObservableValue nameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterbedingungBean.PropertyNames.name.name());

		final IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterbedingungBean.PropertyNames.description.name());

		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterbedingungBean.AbstractPropertyNames.rubrikName
								.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(this._nameTextEntry,
				SWT.Modify), nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(
				this._defaultMessageTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);

		this.initStringAddOnBeanDataBinding(context);
		this.initJunctorAddOnBeanDataBinding(context);
		this.initPVAddOnBeanDataBinding(context);
		this.initStringArrayAddOnBeanDataBinding(context);
		this.initTimeBasedAddOnBeanDataBinding(context);

		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);

	}

	private void addStringArrayCompareValue(final Text newCompareValue) {
		final StringArrayFilterConditionBean specificBean = (StringArrayFilterConditionBean) this
				.getWorkingCopyOfEditorInput().getFilterSpecificBean();
		final java.util.List<String> list = specificBean.getCompareValues();
		if (!list.contains(newCompareValue.getText())) {
			list.add(newCompareValue.getText());
			specificBean.setCompareValues(list);
			newCompareValue.setText(""); //$NON-NLS-1$
		}
	}

	private void initDND() {
		// DropTarget target1 = new DropTarget(junctorFirstFilterText,
		// DND.DROP_LINK);
		// DropTarget target2 = new DropTarget(junctorSecondFilterText,
		// DND.DROP_LINK);
		//
		// target1.setTransfer(new Transfer[] { LocalSelectionTransfer
		// .getTransfer() });
		// target2.setTransfer(new Transfer[] { LocalSelectionTransfer
		// .getTransfer() });
		//
		// target1
		// .addDropListener(new TextDropTarget(junctorFirstFilterText,
		// true));
		// target2.addDropListener(new TextDropTarget(junctorSecondFilterText,
		// false));
	}

	private void initializeAddOnBeans() {
		this.specificBeans = new HashMap<SupportedFilterTypes, AbstractConfigurationBean<?>>();
		this.specificBeans.put(SupportedFilterTypes.JUNCTOR_CONDITION,
				new JunctorConditionBean());
		this.specificBeans.put(SupportedFilterTypes.STRING_CONDITION,
				new StringFilterConditionBean());
		this.specificBeans.put(SupportedFilterTypes.STRING_ARRAY_CONDITION,
				new StringArrayFilterConditionBean());
		this.specificBeans.put(SupportedFilterTypes.PV_CONDITION,
				new PVFilterConditionBean());
		this.specificBeans.put(SupportedFilterTypes.TIMEBASED_CONDITION,
				new TimeBasedFilterConditionBean());
		final AbstractConfigurationBean<?> filterSpecificBean = this
				.getWorkingCopyOfEditorInput().getFilterSpecificBean();

		this.specificBeans.put(SupportedFilterTypes
				.fromClass(filterSpecificBean.getClass()), filterSpecificBean);

		for (final AbstractConfigurationBean<?> bean : this.specificBeans
				.values()) {
			bean.addPropertyChangeListener(this);
		}

	}

	private void initJunctorAddOnBeanDataBinding(
			final DataBindingContext context) {
		// IObservableValue firstConditionTextObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.firstCondition
		// .name());
		//
		// IObservableValue secondConditionTextObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.secondCondition
		// .name());

		// IObservableValue stringJunctorObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.junctor.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(junctorTypeCombo),
		// stringJunctorObservable, new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return JunctorConditionType.valueOf((String) value);
		// }
		// }, new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return ((JunctorConditionType) value).name();
		// }
		// });

		// context.bindValue(SWTObservables.observeText(junctorFirstFilterText,
		// SWT.Modify), firstConditionTextObservable,
		// new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return junctorFirstFilterText.getData();
		// }
		// }, null);
		//
		// context.bindValue(SWTObservables.observeText(junctorSecondFilterText,
		// SWT.Modify), secondConditionTextObservable,
		// new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return junctorSecondFilterText.getData();
		// }
		// }, null);

	}

	private void initPVAddOnBeanDataBinding(final DataBindingContext context) {
		final IObservableValue pvChannelNameTextObservable = BeansObservables
				.observeValue(this.specificBeans
						.get(SupportedFilterTypes.PV_CONDITION),
						PVFilterConditionBean.PropertyNames.channelName.name());

		final IObservableValue pvCompareValueTextObservable = BeansObservables
				.observeValue(this.specificBeans
						.get(SupportedFilterTypes.PV_CONDITION),
						PVFilterConditionBean.PropertyNames.compareValue.name());

		// IObservableValue pvOperatorObservable =
		// BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.PV_CONDITION),
		// PVFilterConditionBean.PropertyNames.operator.name());

		// IObservableValue pvTypeObservable = BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.PV_CONDITION),
		// PVFilterConditionBean.PropertyNames.suggestedType.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(pvOperator),
		// pvOperatorObservable, null, null);

		context.bindValue(SWTObservables.observeText(this.pvChannelName,
				SWT.Modify), pvChannelNameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this.pvCompareValue,
				SWT.Modify), pvCompareValueTextObservable, null, null);

		// context.bindValue(SWTObservables.observeSelection(pvSuggestedType),
		// pvTypeObservable, null, null);
	}

	private void initStringAddOnBeanDataBinding(final DataBindingContext context) {
		// IObservableValue keyComboObservable = BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.STRING_CONDITION),
		// StringFilterConditionBean.PropertyNames.keyValue.name());

		final IObservableValue stringCompareValueTextObservable = BeansObservables
				.observeValue(this.specificBeans
						.get(SupportedFilterTypes.STRING_CONDITION),
						StringFilterConditionBean.PropertyNames.compValue
								.name());

		// IObservableValue stringOperatorObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.STRING_CONDITION),
		// StringFilterConditionBean.PropertyNames.operator.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(stringOperatorCombo),
		// stringOperatorObservable,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

		// context.bindValue(SWTObservables
		// .observeSelection(stringCompareKeyCombo), keyComboObservable,
		// new MessageKeyToModelStrategy(), null);

		context.bindValue(SWTObservables.observeText(
				this.stringCompareValueText, SWT.Modify),
				stringCompareValueTextObservable, null, null);
	}

	private void initStringArrayAddOnBeanDataBinding(
			final DataBindingContext context) {
		final StringArrayFilterConditionBean addOn = (StringArrayFilterConditionBean) this.specificBeans
				.get(SupportedFilterTypes.STRING_ARRAY_CONDITION);

		// IObservableValue arrayKeyValueComboObservable = BeansObservables
		// .observeValue(addOn,
		// StringArrayFilterConditionBean.PropertyNames.keyValue
		// .name());
		//
		// IObservableValue arrayOperatorComboObservable = BeansObservables
		// .observeValue(addOn,
		// StringArrayFilterConditionBean.PropertyNames.operator
		// .name());

		final IObservableList arrayCompareValueListObservable = BeansObservables
				.observeList(
						context.getValidationRealm(),
						addOn,
						StringArrayFilterConditionBean.PropertyNames.compareValues
								.name());

		// bind observables
		// context.bindValue(
		// SWTObservables.observeSelection(arrayMessageKeyCombo),
		// arrayKeyValueComboObservable, null, null);
		//
		// context.bindValue(SWTObservables.observeSelection(arrayOperatorCombo),
		// arrayOperatorComboObservable, null, null);

		context.bindList(SWTObservables
				.observeItems(this.arrayCompareValueList),
				arrayCompareValueListObservable, null, null);

	}

	private void initTimeBasedAddOnBeanDataBinding(
			final DataBindingContext context) {
		final TimeBasedFilterConditionBean addOn = (TimeBasedFilterConditionBean) this.specificBeans
				.get(SupportedFilterTypes.TIMEBASED_CONDITION);

		final IObservableValue timeBasedStartCompareObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.startCompValue
								.name());

		// IObservableValue timeBasedStartKeyObservable = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.startKeyValue
		// .name());

		// IObservableValue timeBasedStartOperator = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.startOperator
		// .name());
		final IObservableValue timeDelayObservable = BeansObservables
				.observeValue(addOn,
						TimeBasedFilterConditionBean.PropertyNames.timePeriod
								.name());
		// bind observables
		context.bindValue(SWTObservables.observeText(this.timeDelayText,
				SWT.Modify), timeDelayObservable, new UpdateValueStrategy() {

			@Override
			public Object convert(final Object value) {
				Millisekunden result = Millisekunden.valueOf(0);
				try {
					result = Millisekunden.valueOf(Long
							.parseLong((String) value)*1000);
				} catch (final Throwable e) {
					FilterbedingungEditor.this.timeDelayText.setText("0"); //$NON-NLS-1$
				}

				return result;
			}

		}, new UpdateValueStrategy() {

			@Override
			public Object convert(final Object value) {
				return ((Millisekunden)value).alsLongVonMillisekunden() / 1000;
			}

		});

		context.bindValue(SWTObservables.observeText(this.timeStartCompareText,
				SWT.Modify), timeBasedStartCompareObservable, null, null);

		// context.bindValue(SWTObservables.observeSelection(timeStartKeyCombo),
		// timeBasedStartKeyObservable, new MessageKeyToModelStrategy(),
		// null);
		//
		// context.bindValue(SWTObservables
		// .observeSelection(timeStartOperatorCombo),
		// timeBasedStartOperator,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

		final IObservableValue timeBasedStopCompareObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.confirmCompValue
								.name());

		// IObservableValue timeBasedStopKeyObservable = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.confirmKeyValue
		// .name());
		//
		// IObservableValue timeBasedStopOperator =
		// BeansObservables.observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.confirmOperator
		// .name());

		final IObservableValue timeBehaviorObservable = BeansObservables
				.observeValue(addOn,
						TimeBasedFilterConditionBean.PropertyNames.timeBehavior
								.name());
//		final IObservableValue rubrikTextObservable = BeansObservables
//				.observeValue(this.getWorkingCopyOfEditorInput(),
//						FilterbedingungBean.AbstractPropertyNames.rubrikName
//								.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(this.timeStopCompareText,
				SWT.Modify), timeBasedStopCompareObservable, null, null);

		// context.bindValue(SWTObservables.observeSelection(timeStopKeyCombo),
		// timeBasedStopKeyObservable, new MessageKeyToModelStrategy(),
		// null);

		context.bindValue(SWTObservables
				.observeSelection(this.timeBehaviorCheck),
				timeBehaviorObservable, new UpdateValueStrategy() {

					@Override
					public Object convert(final Object value) {
						final Boolean status = (Boolean) value;
						return status ? TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM
								: TimeBasedType.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM;
					}

				}, new UpdateValueStrategy() {

					@Override
					public Object convert(final Object value) {
						return TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM == value ? true
								: false;
					}

				});
		//
		// context.bindValue(SWTObservables
		// .observeSelection(timeStopOperatorCombo),
		// timeBasedStopOperator,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

//		context.bindValue(SWTObservables
//				.observeSelection(this._rubrikComboEntry),
//				rubrikTextObservable, null, null);
	}

}
