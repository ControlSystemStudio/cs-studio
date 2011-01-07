/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.config.view.OverviewLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmDataConst;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmDataRef;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSD2Module;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdSlaveModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * TODO (hrickens) :
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 21.05.2010
 */
public class SlaveEditor extends AbstractNodeEditor {

	public static final String ID = "org.csstudio.config.ioconfig.view.editor.slave";
	
	private static final Logger LOG = CentralLogger.getInstance().getLogger(SlaveEditor.class);

	private Text _aktUserPrmData;
	private Group _currentUserParamDataGroup;
	/**
	 * Marker of Background Color for normal use. Get from widget by first use.
	 */
	private Color _defaultBackgroundColor;
	private Button _failButton;
	private Button _freezeButton;
	private int _groupIdent;
	private int _groupIdentStored;
	private Group _groupsRadioButtons;
	/**
	 * The GSD File of the Slave.
	 */
	private GSDFileDBO _gsdFile;
	/**
	 * Slave ID Number.
	 */
	private Text _iDNo;
	private ComboViewer _indexCombo;
	/**
	 * Inputs.
	 */
	private Text _inputsText;
	/**
	 * The Module max size of this Slave.
	 */
	private int _maxSize;
	/**
	 * The number of max slots.
	 */
	private Text _maxSlots;
	/**
	 * Die Bedeutung dieses Feldes ist noch unbekannt.
	 */
	// private Text _unbekannt;
	/**
	 * The minimum Station Delay Time.
	 */
	private Text _minStationDelayText;
	private Text _origUserPrmData;
	/**
	 * Outputs.
	 */
	private Text _outputsText;
	private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();
	/**
	 * The Text field for the Revision.
	 */
	private Text _revisionsText;
	/**
	 * The Slave which displayed.
	 */
	private SlaveDBO _slave;
	/**
	 * Check Button to de.-/activate Station Address.
	 */
	private Button _stationAddressActiveCButton;
	private Button _syncButton;
	/**
	 * List with User Prm Data's.
	 */
	private TableViewer _userPrmDataList;
	/**
	 * The Text field for the Vendor.
	 */
	private Text _vendorText;

	private Button _watchDogButton;

	/**
	 * The Watchdog Time.
	 */
	private Text _watchDogText;

	@Override
	public void cancel() {
		super.cancel();
		// Text nameWidget = getNameWidget();
		// if(nameWidget!=null) {
		// nameWidget.setText((String) nameWidget.getData());
		// }
		// getIndexSpinner().setSelection((Short) getIndexSpinner().getData());
		if (_indexCombo != null) {
			_indexCombo.getCombo().select(
					(Integer) _indexCombo.getCombo().getData());
			getNameWidget().setText((String) getNameWidget().getData());
			getStationAddressActiveCButton().setSelection(
					(Boolean) getStationAddressActiveCButton().getData());
			_minStationDelayText.setText((String) _minStationDelayText
					.getData());
			_syncButton.setSelection((Boolean) _syncButton.getData());
			getFailButton().setSelection((Boolean) getFailButton().getData());
			_freezeButton.setSelection((Boolean) _freezeButton.getData());
			_watchDogButton.setSelection((Boolean) _watchDogButton.getData());
			_watchDogText.setEnabled(_watchDogButton.getSelection());
			_watchDogText.setText((String) _watchDogText.getData());
			_groupIdent = _groupIdentStored;
			for (Control control : _groupsRadioButtons.getChildren()) {
				if (control instanceof Button) {
					Button button = (Button) control;
					button.setSelection(Short.parseShort(button.getText()) == _groupIdentStored + 1);
				}
			}
            try {
                if (_slave != null) {
                    if (_slave.getGSDFile() != null) {
                        fill(_slave.getGSDFile());
                    } else {
                        getHeaderField(HeaderFields.VERSION).setText("");
                        _vendorText.setText("");
                        // getNameWidget().setText("");
                        _revisionsText.setText("");
                    }
                } else {
                    _gsdFile = null;
                    fill(_gsdFile);
                }
            } catch (PersistenceException e) {
                LOG.error(e);
                DeviceDatabaseErrorDialog.open(null, "Can't undo. Database error", e);
            }
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(@Nonnull final Composite parent) {
		_slave = (SlaveDBO) getNode();
		super.createPartControl(parent);
		try {
            makeSlaveKonfiguration();
            selecttTabFolder(0);
        } catch (PersistenceException e) {
            LOG.error(e);
            DeviceDatabaseErrorDialog.open(null, "Can't open Slave Edior! Database error", e);
        }
	}

	/**
	 * {@inheritDoc}
	 * @throws PersistenceException 
	 */
	@Override
	public void doSave(@Nullable final IProgressMonitor monitor) {
		super.doSave(monitor);
		// Name
		_slave.setName(getNameWidget().getText());
		getNameWidget().setData(getNameWidget().getText());

		// _slave.moveSortIndex((short) getIndexSpinner().getSelection());
		Short stationAddress = (Short) ((StructuredSelection) _indexCombo
				.getSelection()).getFirstElement();
		
		try {
            _slave.setSortIndexNonHibernate(stationAddress);
		_slave.setFdlAddress(stationAddress);
		_indexCombo.getCombo().setData(
				_indexCombo.getCombo().getSelectionIndex());
		short minTsdr = 0;
		try {
			minTsdr = Short.parseShort(_minStationDelayText.getText());
		} catch (NumberFormatException e) {
			// don't change the old Value when the new value a invalid.
		}
		_slave.setMinTsdr(minTsdr);

		_slave.setGroupIdent(_groupIdent);
		_groupIdentStored = _groupIdent;

		_slave.setSlaveFlag((short) 192);
		short wdFact = Short.parseShort(_watchDogText.getText());
		_watchDogText.setData(_watchDogText.getText());
		_slave.setWdFact1(wdFact);
		_slave.setWdFact2((short) (wdFact / 10));

		// Static Station status 136
		_slave.setStationStatus((short) 136);

		saveUserPrmData();

		// GSD File
		_slave.setGSDFile(_gsdFile);
		fill(_gsdFile);

		// Document
		Set<DocumentDBO> docs = getDocumentationManageView().getDocuments();
		_slave.setDocuments(docs);

		_slave.update();
		save();
		} catch (PersistenceException e1) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Slave. Database error", e1);
            CentralLogger.getInstance().error(this, e1.getLocalizedMessage());
		}
	}

	/** {@inheritDoc} 
	 * @throws PersistenceException */
	@Override
	public final boolean fill(@Nullable final GSDFileDBO gsdFile) throws PersistenceException {
		// Read GSD-File
		if (gsdFile == null) {
			return false;
		} else if (gsdFile.equals(_gsdFile)) {
			return true;
		}

		_gsdFile = gsdFile;
		GsdSlaveModel slaveModel = GsdFactory.makeGsdSlave(_gsdFile);

		// setGSDData
		HashMap<Integer, GsdModuleModel> moduleList = GSD2Module.parse(
				_gsdFile, slaveModel);
		slaveModel.setGsdModuleList(moduleList);
		_slave.setGSDSlaveData(slaveModel);

		// Head
		getHeaderField(HeaderFields.VERSION).setText(
				slaveModel.getGsdRevision() + "");

		// Basic - Slave Discription (read only)
		_vendorText.setText(slaveModel.getVendorName());
		_iDNo.setText(String.format("0x%04X", slaveModel.getIdentNumber()));
		_revisionsText.setText(slaveModel.getRevision());

		// Set all GSD-File Data to Slave.
		_slave.setMinTsdr(_slave.getMinTsdr());
		_slave.setModelName(slaveModel.getModelName());
		if (_slave.getPrmUserData() == null
				|| _slave.getPrmUserData().isEmpty()) {
			_slave.setPrmUserData(slaveModel.getUserPrmData());
		}

		_slave.setProfibusPNoID(slaveModel.getIdentNumber());
		_slave.setRevision(slaveModel.getRevision());

		// Modules
		_maxSize = slaveModel.getMaxModule();
		setSlots();
		// Settings - USER PRM MODE
		ArrayList<AbstractNodeDBO> nodes = new ArrayList<AbstractNodeDBO>();
		nodes.add(_slave);
		nodes.addAll(_slave.getChildrenAsMap().values());
		_userPrmDataList.setInput(nodes);
		TableColumn[] columns = _userPrmDataList.getTable().getColumns();
		for (TableColumn tableColumn : columns) {
			if (tableColumn != null) {
				tableColumn.pack();
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final GSDFileDBO getGsdFile() {
		return _slave.getGSDFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public final void setGsdFile(GSDFileDBO gsdFile) {
		_slave.setGSDFile(gsdFile);
	}

	/**
	 * @param extUserPrmDataRef
	 * @param extUserPrmData
	 * @return
	 */
	private int getUserPrmDataValue(
			@Nonnull ExtUserPrmDataRef extUserPrmDataRef,
			@Nonnull ExtUserPrmData extUserPrmData) {
		List<String> prmUserDataList = _slave.getPrmUserDataList();
		String value = extUserPrmDataRef.getValue();
		int intVal = ProfibusConfigXMLGenerator.getInt(value);
		String string = prmUserDataList.get(intVal);
		int val = getValueFromBitMask(extUserPrmData, string);
		return val;
	}

	private int getValueFromBitMask(@Nonnull final ExtUserPrmData ranges,
			@Nonnull final String value) {
		int val = ProfibusConfigXMLGenerator.getInt(value);
		int minBit = ranges.getMinBit();
		int maxBit = ranges.getMaxBit();
		if (maxBit < minBit) {
			minBit = ranges.getMaxBit();
			maxBit = ranges.getMinBit();
		}
		int mask = ((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
		val = (val & mask) >> ranges.getMinBit();
		return val;
	}

	/**
	 * @param extUserPrmDataConst
	 * @param byteIndexString
	 * @param prmTextObject
	 */
	private void handleComboViewer(
			@Nonnull final TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst,
			@Nonnull final ComboViewer prmTextCV,
			@Nonnull final String byteIndexString) {
		if (!prmTextCV.getCombo().isDisposed()) {
			int byteIndex = ProfibusConfigXMLGenerator.getInt(byteIndexString);
			ExtUserPrmData input = (ExtUserPrmData) prmTextCV.getInput();
			StructuredSelection selection = (StructuredSelection) prmTextCV
					.getSelection();
			Integer bitValue = ((PrmText) selection.getFirstElement())
					.getValue();
			String newValue = setValue2BitMask(input, bitValue, _slave
					.getPrmUserDataList().get(byteIndex));
			_slave.setPrmUserDataByte(byteIndex, newValue);
			Integer indexOf = prmTextCV.getCombo().indexOf(
					selection.getFirstElement().toString());
			prmTextCV.getCombo().setData(indexOf);
		}
	}

	/**
	 * @param extUserPrmDataConst
	 * @param prmText
	 * @return
	 */
	@Nonnull
	private TreeMap<String, ExtUserPrmDataConst> handleText(
			@Nonnull final TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst,
			@Nonnull final Text prmText) {
		if (!prmText.isDisposed()) {
			String value = (String) prmText.getData();
			if (value != null) {
				prmText.setText(value);
				int val = Integer.parseInt(value);
				// return new String[] {String.format("%1$#04x", val) };
			}
		}
		return extUserPrmDataConst;
	}

	/**
	 * @param head
	 *            The Tab text.
	 * @throws PersistenceException 
	 */
	private void makeBasics(@Nonnull final String head) throws PersistenceException {

		Composite comp = getNewTabItem(head, 2);

		makeBasicsName(comp);

		makeBasicsSlaveInfo(comp);

		makeBasicsDPFDLAccess(comp);

		makeBasicsIO(comp);

		makeDescGroup(comp, 3);
	}

	/**
	 * @param comp
	 */
	private void makeBasicsDPFDLAccess(@Nonnull Composite comp) {
		/*
		 * DP/FDL Access Group
		 */
		Group dpFdlAccessGroup = new Group(comp, SWT.NONE);
		dpFdlAccessGroup.setText("DP / FDL Access");
		dpFdlAccessGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		dpFdlAccessGroup.setLayout(new GridLayout(2, false));

		Label stationAdrLabel = new Label(dpFdlAccessGroup, SWT.None);
		stationAdrLabel.setText("Station Address");

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd.minimumWidth = 50;

		setStationAddressActiveCButton(new Button(dpFdlAccessGroup, SWT.CHECK));
		getStationAddressActiveCButton().setText("Active");
		getStationAddressActiveCButton().setSelection(false);
		getStationAddressActiveCButton().setData(false);
		getStationAddressActiveCButton().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetDefaultSelected(
							@Nonnull final SelectionEvent e) {
						change();
					}

					@Override
					public void widgetSelected(@Nonnull final SelectionEvent e) {
						change();

					}

					private void change() {
						setSavebuttonEnabled(
								"Button:"
										+ getStationAddressActiveCButton()
												.hashCode(),
								(Boolean) getStationAddressActiveCButton()
										.getData() != getStationAddressActiveCButton()
										.getSelection());
					}

				});
	}

	/**
	 * @param comp
	 * @throws PersistenceException 
	 */
	private void makeBasicsIO(@Nonnull Composite comp) throws PersistenceException {
		Group ioGroup = new Group(comp, SWT.NONE);
		ioGroup.setText("Inputs / Outputs");
		ioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		ioGroup.setLayout(new GridLayout(3, false));
		ioGroup.setTabList(new Control[0]);

		int input = 0;
		int output = 0;

		if (_slave.hasChildren()) {
			Iterator<ModuleDBO> iterator = _slave.getModules().iterator();
			while (iterator.hasNext()) {
				ModuleDBO module = iterator.next();
				input += module.getInputSize();
				output += module.getOutputSize();
			}
		}
		Label inputLabel = new Label(ioGroup, SWT.RIGHT);
		inputLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false, 2, 1));
		inputLabel.setText("Inputs: ");
		_inputsText = new Text(ioGroup, SWT.SINGLE);
		_inputsText.setEditable(false);
		_inputsText.setText(Integer.toString(input));

		Label outputsLabel = new Label(ioGroup, SWT.RIGHT);
		outputsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false, 2, 1));
		outputsLabel.setText("Outputs: ");
		_outputsText = new Text(ioGroup, SWT.SINGLE);
		_outputsText.setEditable(false);
		_outputsText.setText(Integer.toString(output));
	}

	/**
	 * @param comp
	 * @throws PersistenceException 
	 */
	private void makeBasicsName(@Nonnull Composite comp) throws PersistenceException {
		Group gName = new Group(comp, SWT.NONE);
		gName.setText("Name");
		gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
		gName.setLayout(new GridLayout(3, false));

		Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
		setText(nameText, _slave.getName(), 255);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		setNameWidget(nameText);

		// Label
		Label slotIndexLabel = new Label(gName, SWT.NONE);
		slotIndexLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		slotIndexLabel.setText("Station Adress:");

		_indexCombo = new ComboViewer(gName, SWT.DROP_DOWN | SWT.READ_ONLY);
		_indexCombo.getCombo().setLayoutData(
				new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
		_indexCombo.setContentProvider(new ArrayContentProvider());
		_indexCombo.setLabelProvider(new LabelProvider());
		Collection<Short> freeStationAddress = _slave.getProfibusDPMaster()
				.getFreeStationAddress();
		Short sortIndex = _slave.getSortIndex();
		if (!freeStationAddress.contains(sortIndex)) {
			freeStationAddress.add(sortIndex);
		}
		_indexCombo.setInput(freeStationAddress);
		_indexCombo.setSelection(new StructuredSelection(sortIndex));
		_indexCombo.getCombo().setData(
				_indexCombo.getCombo().getSelectionIndex());
		_indexCombo.getCombo().addModifyListener(getMLSB());
        _indexCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                short index = (Short) ((StructuredSelection) _indexCombo.getSelection())
                        .getFirstElement();
                try {
                    getNode().moveSortIndex(index);
                    if (getNode().getParent() != null) {
                        getProfiBusTreeView().refresh(getNode().getParent());
                    } else {
                        getProfiBusTreeView().refresh();
                    }
                } catch (PersistenceException e) {
                    LOG.error("Deveice Database Error. Cant't move node.",e);
                    DeviceDatabaseErrorDialog.open(null, "Cant't move node.", e);
                }
            }
        });
	}

	/**
	 * @param comp
	 */
	private void makeBasicsSlaveInfo(@Nonnull Composite comp) {
		/*
		 * Slave Information
		 */
		Group slaveInfoGroup = new Group(comp, SWT.NONE);
		slaveInfoGroup.setText("Slave Information");
		slaveInfoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 2));
		slaveInfoGroup.setLayout(new GridLayout(4, false));
		slaveInfoGroup.setTabList(new Control[0]);

		_vendorText = new Text(slaveInfoGroup, SWT.SINGLE | SWT.BORDER);
		_vendorText.setEditable(false);
		_vendorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				4, 1));

		_iDNo = new Text(slaveInfoGroup, SWT.SINGLE);
		_iDNo.setEditable(false);
		_iDNo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label revisionsLable = new Label(slaveInfoGroup, SWT.NONE);
		revisionsLable.setText("Revision:");

		_revisionsText = new Text(slaveInfoGroup, SWT.SINGLE);
		_revisionsText.setEditable(false);
		_revisionsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));

		new Label(slaveInfoGroup, SWT.None).setText("Max. available slots:");
		_maxSlots = new Text(slaveInfoGroup, SWT.BORDER);
		_maxSlots.setEditable(false);
	}

	/**
	 * 
	 * @param parent
	 *            the Parent Composite.
	 * @param value
	 *            the Selected currentUserParamData Value.
	 * @param extUserPrmData
	 * @param prmTextMap
	 * @param byteIndex
	 * @return a ComboView for are currentUserParamData Property
	 */
	@Nonnull
	private ComboViewer makeComboViewer(@Nonnull final Composite parent,
			@Nullable final Integer value,
			@Nonnull final ExtUserPrmData extUserPrmData,
			@CheckForNull final HashMap<Integer, PrmText> prmTextMap,
			@Nonnull final String byteIndex) {
		Integer localValue = value;

		ComboViewer prmTextCV = new ComboViewer(parent);
		RowData data = new RowData();
		data.exclude = false;
		prmTextCV.getCombo().setLayoutData(data);
		prmTextCV
				.setLabelProvider(new PrmTextComboLabelProvider(extUserPrmData));
		prmTextCV.setContentProvider(new ExtUserPrmDataContentProvider());
		prmTextCV.getCombo().addModifyListener(getMLSB());
		prmTextCV.setSorter(new PrmTextViewerSorter());

		if (localValue == null) {
			localValue = extUserPrmData.getDefault();
		}
		prmTextCV.setInput(extUserPrmData);

		if (prmTextMap != null) {
			PrmText prmText = prmTextMap.get(localValue);
			if (prmText != null) {
				prmTextCV.setSelection(new StructuredSelection(prmTextMap
						.get(localValue)));
			} else {
				prmTextCV.getCombo().select(0);
			}
		} else {
			prmTextCV.getCombo().select(localValue);
		}
		prmTextCV.getCombo().setData(prmTextCV.getCombo().getSelectionIndex());
		prmTextCV.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(@Nonnull SelectionChangedEvent event) {
				refreshUserPrmDate();
			}

		});
		return prmTextCV;
	}

	/**
	 * 
	 * @param topGroup
	 *            The parent Group for the CurrentUserParamData content.
	 */
	private void makeCurrentUserParamData(@Nonnull final Composite topGroup) {
		if (_currentUserParamDataGroup != null) {
			_currentUserParamDataGroup.dispose();
		}
		// Current User Param Data Group
		_currentUserParamDataGroup = new Group(topGroup, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
		_currentUserParamDataGroup.setLayoutData(gd);
		_currentUserParamDataGroup.setLayout(new FillLayout());
		_currentUserParamDataGroup.setText("Current User Param Data:");
		final ScrolledComposite scrollComposite = new ScrolledComposite(
				_currentUserParamDataGroup, SWT.V_SCROLL);
		final Composite currentUserParamDataComposite = new Composite(
				scrollComposite, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.wrap = false;
		rowLayout.fill = true;
		currentUserParamDataComposite.setLayout(rowLayout);
		scrollComposite.setContent(currentUserParamDataComposite);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		_currentUserParamDataGroup.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(@Nonnull final ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(scrollComposite.computeSize(r.width,
						SWT.DEFAULT));
			}
		});
		scrollComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(@Nonnull final ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(currentUserParamDataComposite
						.computeSize(r.width, SWT.DEFAULT));
			}
		});

		// Current User Param Data
		GsdSlaveModel gsdSlaveModel = _slave.getGSDSlaveData();
		if (gsdSlaveModel != null) {
			List<ExtUserPrmDataRef> extUserPrmDataRefMap = gsdSlaveModel
					.getExtUserPrmDataRefMap();
			if (extUserPrmDataRefMap != null) {
				for (ExtUserPrmDataRef extUserPrmDataRef : extUserPrmDataRefMap) {
					String byteIndex = extUserPrmDataRef.getIndex();
					ExtUserPrmData extUserPrmData = gsdSlaveModel
							.getExtUserPrmData(extUserPrmDataRef.getValue());
					Integer value = getUserPrmDataValue(extUserPrmDataRef,
							extUserPrmData); // TODO: Set the saved value!!!
					makeCurrentUserParamDataItem(currentUserParamDataComposite,
							extUserPrmData, value, byteIndex);
				}
			}
		}
		topGroup.layout();
	}

	/**
	 * 
	 * @param currentUserParamDataGroup
	 * @param extUserPrmData
	 * @param value
	 * @param byteIndex
	 */
	@SuppressWarnings("unused")
	private void makeCurrentUserParamDataItem(
			@Nonnull final Composite currentUserParamDataGroup,
			@Nullable final ExtUserPrmData extUserPrmData, final Integer value,
			final String byteIndex) {
		HashMap<Integer, PrmText> prmTextMap = null;

		Text text = new Text(currentUserParamDataGroup, SWT.SINGLE
				| SWT.READ_ONLY);

		if (extUserPrmData != null) {
			text.setText(extUserPrmData.getText() + ":");
			prmTextMap = extUserPrmData.getPrmText();
			if ((prmTextMap == null || prmTextMap.isEmpty())
					&& (extUserPrmData.getMaxValue()
							- extUserPrmData.getMinValue() > 10)) {
				_prmTextCV.add(makeTextField(currentUserParamDataGroup, value,
						extUserPrmData));
			} else {
				_prmTextCV.add(makeComboViewer(currentUserParamDataGroup,
						value, extUserPrmData, prmTextMap, byteIndex));
			}
		}
		new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
	}

	/**
	 * @param comp
	 */
	private void makeOperationMode(@Nonnull Composite comp) {
		// Operation Mode
		Group operationModeGroup = new Group(comp, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		layoutData.minimumWidth = 170;
		operationModeGroup.setLayoutData(layoutData);
		operationModeGroup.setText("Operation Mode");
		operationModeGroup.setLayout(new GridLayout(3, false));
		Label delayLabel = new Label(operationModeGroup, SWT.NONE);
		delayLabel.setText("Min. Station Delay");
		_minStationDelayText = ProfibusHelper.getTextField(operationModeGroup,
				true, _slave.getMinTsdr() + "", Ranges.WATCHDOG,
				ProfibusHelper.VL_TYP_U16);
		_minStationDelayText.addModifyListener(getMLSB());

		Label bitLabel = new Label(operationModeGroup, SWT.NONE);
		bitLabel.setText("Bit");

		_syncButton = new Button(operationModeGroup, SWT.CHECK);
		_syncButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 3, 1));
		_syncButton.addTraverseListener(ProfibusHelper.getNETL());
		_syncButton.setText("Sync Request");
		_syncButton.setData(false);
		_syncButton
				.addSelectionListener(new ButtonDirtyChangeSelectionListener(
						_syncButton));
		_freezeButton = new Button(operationModeGroup, SWT.CHECK);
		_freezeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 3, 1));
		_freezeButton.addTraverseListener(ProfibusHelper.getNETL());
		_freezeButton.setText("Freeze Request");
		_freezeButton.setSelection(false);
		_freezeButton.setData(false);
		_freezeButton.addSelectionListener(new ButtonDirtyChangeSelectionListener(
						_freezeButton));
		setFailButton(new Button(operationModeGroup, SWT.CHECK));
		final Button failButton = getFailButton();
		if (failButton != null) {
			failButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,false, 3, 1));
			failButton.addTraverseListener(ProfibusHelper.getNETL());
			failButton.setText("Fail Save");
			failButton.setEnabled(false);
			failButton.setData(false);
			failButton.addSelectionListener(new ButtonDirtyChangeSelectionListener(failButton));
		}
		_watchDogButton = new Button(operationModeGroup, SWT.CHECK);
		_watchDogButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		_watchDogButton.addTraverseListener(ProfibusHelper.getNETL());
		_watchDogButton.setText("Watchdog Time");
		_watchDogButton.setSelection(true);
		_watchDogButton.setData(true);
		_watchDogText = ProfibusHelper.getTextField(operationModeGroup,
				_watchDogButton.getSelection(),Integer.toString(_slave.getWdFact1()),
				Ranges.TTR, ProfibusHelper.VL_TYP_U32);
		_watchDogText.addModifyListener(getMLSB());
		Label timeLabel = new Label(operationModeGroup, SWT.NONE);
		timeLabel.setText("ms");
		_watchDogButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
				change();
			}

			@Override
			public void widgetSelected(@Nonnull final SelectionEvent e) {
				change();
			}

			private void change() {
				SlaveEditor.this.change(_watchDogButton);
				_watchDogText.setEnabled(_watchDogButton.getSelection());
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void makeOverview(@Nonnull final String headline) throws PersistenceException {
		Composite comp = getNewTabItem(headline, 1);
		comp.setLayout(new GridLayout(1, false));
		List<TableColumn> columns = new ArrayList<TableColumn>();
		String[] headers = new String[] {"Adr", "Adr", "Name", "IO Name", "IO Epics Address",
				"Desc", "Type", "DB Id" };
		int[] styles = new int[] {SWT.RIGHT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
				SWT.LEFT, SWT.LEFT };

		TableViewer overViewer = new TableViewer(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER |
				SWT.FULL_SELECTION);
		overViewer.setContentProvider(new ArrayContentProvider());
		overViewer.setLabelProvider(new OverviewLabelProvider());
		overViewer.getTable().setHeaderVisible(true);
		overViewer.getTable().setLinesVisible(true);
		overViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		for (int i = 0; i < headers.length && i < styles.length; i++) {
			TableColumn c = new TableColumn(overViewer.getTable(), styles[i]);
			c.setText(headers[i]);
			columns.add(c);
		}

		overViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(
					@Nonnull final SelectionChangedEvent event) {
				getProfiBusTreeView().getTreeViewer().setSelection(event.getSelection(), true);
			}
		});

		ArrayList<AbstractNodeDBO> children = new ArrayList<AbstractNodeDBO>();
		Collection<ModuleDBO> modules = (Collection<ModuleDBO>) _slave.getChildrenAsMap().values();
		for (ModuleDBO module : modules) {
			children.add(module);
			Collection<ChannelStructureDBO> channelStructures = module
					.getChannelStructsAsMap().values();
			for (ChannelStructureDBO channelStructure : channelStructures) {
				Collection<ChannelDBO> channels = channelStructure.getChannelsAsMap().values();
				for (ChannelDBO channel : channels) {
					children.add(channel);
				}
			}
		}
		overViewer.setInput(children);
		for (TableColumn tableColumn : columns) {
			tableColumn.pack();
		}
	}

	/**
	 * @param head
	 *            the tabItemName
	 */
	private void makeSettings(@Nonnull final String head) {
		Composite comp = getNewTabItem(head, 2);
		comp.setLayout(new GridLayout(3, false));

		makeOperationMode(comp);

		makeCurrentUserParamData(comp);

		makeUserPRMMode(comp);

		makeSettingsGroups(comp);

	}

	/**
	 * @param comp
	 */
	private void makeSettingsGroups(@Nonnull final Composite comp) {
		// Groups
		_groupsRadioButtons = new Group(comp, SWT.NONE);
		_groupsRadioButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));
		_groupsRadioButtons.setLayout(new GridLayout(4, true));
		_groupsRadioButtons.setText("Groups");
		_groupIdentStored = _slave.getGroupIdent();
		if ((_groupIdentStored < 0) || (_groupIdentStored > 7)) {
			_groupIdentStored = 0;
		}
		_groupIdent = _groupIdentStored;
		for (int i = 0; i <= 7; i++) {
			final Button b = new Button(_groupsRadioButtons, SWT.RADIO);
			b.setText(Integer.toString(i + 1));
			if (i == _groupIdent) {
				b.setSelection(true);
			}
			b.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(
						@Nonnull final SelectionEvent e) {
					check();
				}

				@Override
				public void widgetSelected(@Nonnull final SelectionEvent e) {
					check();
				}

				private void check() {
					_groupIdent = Short.parseShort(b.getText());
					_groupIdent--;
					setSavebuttonEnabled(
							"groupButton" + _groupsRadioButtons.hashCode(),
							_groupIdent != _groupIdentStored);
				}

			});
		}
	}

	/**
	 * @param parent
	 *            Parent Composite.
	 * @param style
	 *            Style of the Composite.
	 * @param slave
	 *            the Profibus Slave to Configer.
	 * @throws PersistenceException 
	 */
	private void makeSlaveKonfiguration() throws PersistenceException {
		boolean nevv = false;
		String[] heads = {"Basics", "Settings", "Overview" };
		makeOverview(heads[2]);
		makeSettings(heads[1]);
		makeBasics(heads[0]);
		if (_slave.getGSDFile() != null) {
			fill(_slave.getGSDFile());
		}

		getTabFolder().pack();
		if (nevv) {
		    selecttTabFolder(4);
		}
	}

	/**
	 * 
	 * @param currentUserParamDataGroup
	 * @param value
	 * @param extUserPrmData
	 * @return
	 */
	@Nonnull
	private Text makeTextField(
			@Nonnull final Composite currentUserParamDataGroup,
			@CheckForNull final Integer value,
			@Nonnull final ExtUserPrmData extUserPrmData) {
		Integer localValue = value;
		Text prmText = new Text(currentUserParamDataGroup, SWT.BORDER
				| SWT.SINGLE | SWT.RIGHT);
		Formatter f = new Formatter();
		f.format("Min: %d, Min: %d", extUserPrmData.getMinValue(),
				extUserPrmData.getMaxValue());
		prmText.setToolTipText(f.toString());
		prmText.setTextLimit(Integer.toString(extUserPrmData.getMaxValue())
				.length());

		if (localValue == null) {
			localValue = extUserPrmData.getDefault();
		}
		prmText.setText(localValue.toString());
		prmText.setData(localValue.toString());
		prmText.addModifyListener(getMLSB());
		prmText.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(@Nonnull final VerifyEvent e) {
				if (e.text.matches("^\\D+$")) {
					e.doit = false;
				}
			}

		});
		return prmText;
	}

	/**
	 * @param comp
	 */
	private void makeUserPRMMode(@Nonnull final Composite comp) {
		Group userPrmData = new Group(comp, SWT.V_SCROLL);
		userPrmData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 2));
		userPrmData.setLayout(new GridLayout(2, false));
		userPrmData.setTabList(new Control[0]);
		userPrmData.setText("User PRM Mode");
		_userPrmDataList = new TableViewer(userPrmData, SWT.BORDER
				| SWT.V_SCROLL);

		Table table = _userPrmDataList.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 15));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		_userPrmDataList.setContentProvider(new ArrayContentProvider());
		_userPrmDataList.setLabelProvider(new RowNumLabelProvider());

		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText("");
		tc.setWidth(20);
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Name");
		tc.setWidth(130);
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Ext User Prm Data Const");
		tc.setWidth(450);
	}

	protected void refreshUserPrmDate() {
		saveUserPrmData();
//		String userPrmData = "no data";
//		if (_slave.getGSDSlaveData() != null
//				&& _slave.getGSDSlaveData().getUserPrmData() != null) {
//			userPrmData = _slave.getGSDSlaveData().getUserPrmData();
//		}
//		String prmUserData = _slave.getPrmUserData();
//		if (prmUserData == null) {
//			prmUserData = "no data";
//		}
//		_aktUserPrmData.setText(prmUserData);
	}

	/**
    *
    */
	private void saveUserPrmData() {
		// Check have the Salve a GSD-File
		if (_slave.getGSDSlaveData() != null) {
			TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst;
			extUserPrmDataConst = _slave.getGSDSlaveData()
					.getExtUserPrmDataConst();
			List<ExtUserPrmDataRef> extUserPrmDataRefMap;
			extUserPrmDataRefMap = _slave.getGSDSlaveData()
					.getExtUserPrmDataRefMap();

			if (extUserPrmDataRefMap.size() == _prmTextCV.size()) {
				for (int i = 0; i < extUserPrmDataRefMap.size(); i++) {
					ExtUserPrmDataRef ref = extUserPrmDataRefMap.get(i);
					Object prmTextObject = _prmTextCV.get(i);
					if (prmTextObject instanceof ComboViewer) {
						ComboViewer prmTextCV = (ComboViewer) prmTextObject;
						handleComboViewer(extUserPrmDataConst, prmTextCV,
								ref.getValue());
					} else if (prmTextObject instanceof Text) {
						Text prmText = (Text) prmTextObject;
						extUserPrmDataConst = handleText(extUserPrmDataConst,
								prmText);
					}
				}

			} else {
				// TODO: throw extUserPrmDataRefMap und prmTextCV passen nicht
				// zusammen
			}
		}
	}

	/**
     *
     */
	private void setSlots() {
		Formatter slotFormarter = new Formatter();
		slotFormarter.format(" %2d / %2d", _slave.getChildren().size(),
				_maxSize);
		_maxSlots.setText(slotFormarter.toString());
		if (_maxSize < _slave.getChildren().size()) {
			if (getDefaultBackgroundColor() == null) {
				setDefaultBackgroundColor(Display.getDefault().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND));
			}
			_maxSlots.setBackground(WARN_BACKGROUND_COLOR);
		} else {
			_maxSlots.setBackground(getDefaultBackgroundColor());
		}
		slotFormarter = null;
	}

	/**
	 * Change the a value on the Bit places, that is given from the input, to
	 * the bitValue.
	 * 
	 * @param ranges
	 *            give the start and end Bit position.
	 * @param bitValue
	 *            the new Value for the given Bit position.
	 * @param value
	 *            the value was changed.
	 * @return the changed value as Hex String.
	 */
	@Nonnull
	private String setValue2BitMask(@Nonnull final ExtUserPrmData ranges,
			@Nonnull final Integer bitValue, @Nonnull final String value) {
		int val = ProfibusConfigXMLGenerator.getInt(value);
		int minBit = ranges.getMinBit();
		int maxBit = ranges.getMaxBit();
		if (maxBit < minBit) {
			minBit = ranges.getMaxBit();
			maxBit = ranges.getMinBit();
		}
		int mask = ~((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
		val = val & mask;
		val = val | (bitValue << minBit);
		return String.format("%1$#04x", val);
	}

	@CheckForNull
	protected Color getDefaultBackgroundColor() {
		return _defaultBackgroundColor;
	}

	@Nonnull
	protected Button getStationAddressActiveCButton() {
		assert _stationAddressActiveCButton != null : "Access to early. Button is null.";
		return _stationAddressActiveCButton;
	}

	protected void setDefaultBackgroundColor(
			@Nonnull Color defaultBackgroundColor) {
		_defaultBackgroundColor = defaultBackgroundColor;
	}

	protected void setFailButton(@Nonnull Button failButton) {
		_failButton = failButton;
	}

	protected void setStationAddressActiveCButton(
			@Nonnull Button stationAddressActiveCButton) {
		_stationAddressActiveCButton = stationAddressActiveCButton;
	}

	void change(@Nonnull final Button button) {
		setSavebuttonEnabled("Button:" + button.hashCode(),
				(Boolean) button.getData() != button.getSelection());
	}

	@CheckForNull
	Button getFailButton() {
		return _failButton;
	}

	/**
	 * TODO (hrickens) :
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 19.10.2010
	 */
	private final class ButtonDirtyChangeSelectionListener implements
			SelectionListener {
		private final Button _button;

		/**
		 * Constructor.
		 * 
		 * @param syncButton
		 */
		public ButtonDirtyChangeSelectionListener(@Nonnull Button button) {
			_button = button;
		}

		@Override
		public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
			change(_button);
		}

		@Override
		public void widgetSelected(@Nonnull final SelectionEvent e) {
			change(_button);
		}
	}

	/**
	 * TODO (hrickens) :
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 08.10.2010
	 */
	private final class ExtUserPrmDataContentProvider implements
			IStructuredContentProvider {
		/**
		 * Constructor.
		 */
		public ExtUserPrmDataContentProvider() {
			// Constructor.
		}

		@Override
		public void dispose() {
			// not to dispose
		}

		@Override
		public Object[] getElements(@Nullable final Object inputElement) {
			if (inputElement instanceof ExtUserPrmData) {
				ExtUserPrmData eUPD = (ExtUserPrmData) inputElement;
				HashMap<Integer, PrmText> prmText = eUPD.getPrmText();
				if (prmText == null) {
					PrmText[] prmTextArray = new PrmText[eUPD.getMaxValue()
							- eUPD.getMinValue() + 1];
					for (int i = eUPD.getMinValue(); i <= eUPD.getMaxValue(); i++) {
						prmTextArray[i] = new PrmText(Integer.toString(i), i);
					}
					return prmTextArray;
				}
				return prmText.values().toArray();
			}
			return null;
		}

		@Override
		public void inputChanged(@Nullable final Viewer viewer,
				@Nullable final Object oldInput, @Nullable final Object newInput) {
			// nothing to do.
		}
	}

	/**
	 * {@link PrmText} Label provider that mark the default selection with a
	 * '*'. The {@link ExtUserPrmData} give the default.
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 18.10.2010
	 */
	private final class PrmTextComboLabelProvider extends LabelProvider {

		private final ExtUserPrmData _extUserPrmData;

		/**
		 * Constructor.
		 * 
		 * @param extUserPrmData
		 */
		public PrmTextComboLabelProvider(@Nonnull ExtUserPrmData extUserPrmData) {
			_extUserPrmData = extUserPrmData;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(@Nullable Object element) {
			if (element instanceof PrmText) {
				PrmText prmText = (PrmText) element;
				if (prmText.getValue() == _extUserPrmData.getDefault()) {
					return "*" + element.toString();
				}
			}
			return super.getText(element);
		}
	}

	/**
	 * {@link PrmText} Sorter
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 08.10.2010
	 */
	private final class PrmTextViewerSorter extends ViewerSorter {
		/**
		 * Constructor.
		 */
		public PrmTextViewerSorter() {
			// Constructor.
		}

		@Override
		public int compare(@Nullable final Viewer viewer,
				@Nullable final Object e1, @Nullable final Object e2) {
			if ((e1 instanceof PrmText) && (e2 instanceof PrmText)) {
				PrmText eUPD1 = (PrmText) e1;
				PrmText eUPD2 = (PrmText) e2;
				return eUPD1.getValue() - eUPD2.getValue();
			}
			return super.compare(viewer, e1, e2);
		}
	}

	/**
	 * 
	 * @author hrickens
	 * @author $Author: hrickens $
	 * @version $Revision: 1.3 $
	 * @since 14.08.2007
	 */
	class RowNumLabelProvider implements ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addListener(@Nullable final ILabelProviderListener listener) {
			// handle no listener
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dispose() {
			Color defaultBackgroundColor = getDefaultBackgroundColor();
			if (defaultBackgroundColor != null) {
				defaultBackgroundColor.dispose();
			}
		}

		@Override
		public Image getColumnImage(@Nullable final Object element,
				@Nullable final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(@Nullable final Object element,
				final int columnIndex) {
			if (element instanceof SlaveDBO) {
				SlaveDBO slave = (SlaveDBO) element;
				return onSlave(slave, columnIndex);
			} else if (element instanceof ModuleDBO) {
				ModuleDBO module = (ModuleDBO) element;
				return onModule(module, columnIndex);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isLabelProperty(@Nullable final Object element,
				@Nullable final String property) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeListener(
				@Nullable final ILabelProviderListener listener) {
			// handle no listener
		}

		/**
		 * @param module
		 * @param columnIndex
		 */
		@CheckForNull
		private String onModule(@Nonnull final ModuleDBO module,
				final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return module.getSortIndex().toString();
			case 1:
				return module.getName();
			case 2:
				return module.getExtUserPrmDataConst();

			default:
				break;
			}
			return null;
		}

		/**
		 * @param slave
		 */
		@CheckForNull
		private String onSlave(@Nonnull final SlaveDBO slave,
				final int columnIndex) {
			// TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst =
			// slave.getGSDSlaveData()
			// .getExtUserPrmDataConst();

			switch (columnIndex) {
			case 1:
				return slave.getName();
			case 2:
				// StringBuffer sb = new StringBuffer();
				// Set<String> keySet = extUserPrmDataConst.keySet();
				// for (String key : keySet) {
				// sb.append(extUserPrmDataConst.get(key).getValue());
				// }
				// return sb.toString();
				return slave.getPrmUserData();
			default:
				break;
			}
			return null;
		}
	}
}
