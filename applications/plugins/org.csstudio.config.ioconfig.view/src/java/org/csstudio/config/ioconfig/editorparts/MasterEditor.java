/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.AbstractGsdPropertyModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class MasterEditor extends AbstractGsdNodeEditor {
    
    private static final Logger LOG = CentralLogger.getInstance().getLogger(MasterEditor.class);
    
    public static final String ID = "org.csstudio.config.ioconfig.view.editor.master";
    
    /*
     * Data.
     */
    /**
     * The ProfibusDPMaster.
     */
    private MasterDBO _master;
    /**
     * The Selected GSD FFile for the Master.
     */
    private GSDFileDBO _gsdFile;
    
    /*
     * GUI Elements.
     */
    /**
     * Check button to set auto clear on/off.
     */
    private Button _autoclearButton;
    /**
     * The description field for the Vendor Information.
     */
    private Text _vendorText;
    /**
     * The description field for the Board Information.
     */
    private Text _pbBoardText;
    /**
     * The description field for the ID.
     */
    private Text _idNoText;
    /**
     * The description field for the Station Type.
     */
    private Text _stationTypText;
    /**
     * The description field for the min Slave interval.
     */
    private Text _minSlaveIntervalText;
    /**
     * The description field for the poll.
     */
    private Text _pollTimeOutText;
    /**
     * The description field for the Data Control Time.
     */
    private Text _dataControlTimeText;
    /**
     * The description field for the Master User Data.
     */
    private Text _masterUserDataText;
    
    /**
     * Selection of the Memory Address Type.
     */
    private Integer _memAddressType;
    /**
     * The Persistent Memory Address Type.
     */
    private Integer _oldMemAddressType;
    private Text _maxNrSlaveText;
    private Text _maxSlaveOutputLenText;
    private Text _maxSlaveInputLenText;
    private Text _maxSlaveDiagEntriesText;
    private Text _maxBusParaLenText;
    private Text _maxSlaveParaLenText;
    private Text _maxSlaveDiagLenText;
    private Text _maxCalcText;
    private ComboViewer _indexCombo;
    private Button _redundentButton;
    private Collection<Short> _freeStationAddress;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        _master = (MasterDBO) getNode();
        _gsdFile = _master.getGSDFile();
        super.createPartControl(parent);
        String[] heads = {"Master", "GSD File List"};
        try {
            master(heads[0]);
            fill(_gsdFile);
        } catch (PersistenceException e) {
            LOG.error(e);
            DeviceDatabaseErrorDialog.open(null, "Can't create Master Editor! Database Error", e);
        }
        selecttTabFolder(0);
    }
    
    @SuppressWarnings("unused")
    private void makeFmbSetGroup(@Nonnull final Composite parent) throws PersistenceException {
        final int limit = 13000;
        ModifyListener listener = new ModifyListener() {
            
            @Override
            public void modifyText(@Nonnull final ModifyEvent e) {
                int value = (Integer.parseInt(_maxSlaveInputLenText.getText()) + Integer
                        .parseInt(_maxSlaveOutputLenText.getText()))
                        * Integer.parseInt(_maxNrSlaveText.getText());
                _maxCalcText.setText(String.format("%1$d < %2$d = %3$b",
                                                   value,
                                                   limit,
                                                   value < limit));
                
            }
            
        };
        
        Group gName = new Group(parent, SWT.NONE);
        gName.setText("FMB Set");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4));
        gName.setLayout(new GridLayout(2, false));
        
        Label maxNrSlaveLabel = new Label(gName, SWT.NONE);
        maxNrSlaveLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxNrSlaveLabel.setText("Max Number Slaves:");
        
        int min = 0;
        
        TreeMap<Short, ? extends AbstractNodeDBO> map = (TreeMap<Short, ? extends AbstractNodeDBO>) _master
                .getChildrenAsMap();
        if(map.size() > 0) {
            min = map.lastKey();
        }
        int maxNrSlave;
        if( (min <= 0) && (_master.getMaxNrSlave() <= 0)) {
            // default
            maxNrSlave = 60;
        } else if(min > _master.getMaxNrSlave()) {
            maxNrSlave = min;
        } else {
            maxNrSlave = _master.getMaxNrSlave();
        }
        _maxNrSlaveText = ProfibusHelper.getTextField(gName,
                                                      true,
                                                      Integer.toString(maxNrSlave),
                                                      Ranges.getRangeValue(min, 9999, 60),
                                                      ProfibusHelper.VL_TYP_U16);
        _maxNrSlaveText.addModifyListener(getMLSB());
        _maxNrSlaveText.addModifyListener(listener);
        
        Label maxSlaveOutputLenLabel = new Label(gName, SWT.NONE);
        maxSlaveOutputLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveOutputLenLabel.setText("Max Slave Output Len:");
        
        int slaveOutputLen = 160;
        if(_master.getMaxSlaveOutputLen() >= 0) {
            slaveOutputLen = _master.getMaxSlaveOutputLen();
        }
        _maxSlaveOutputLenText = ProfibusHelper.getTextField(gName,
                                                             true,
                                                             Integer.toString(slaveOutputLen),
                                                             Ranges.getRangeValue(0, 9999, 100),
                                                             ProfibusHelper.VL_TYP_U16);
        _maxSlaveOutputLenText.addModifyListener(getMLSB());
        _maxSlaveOutputLenText.addModifyListener(listener);
        
        Label maxSlaveInputLenLabel = new Label(gName, SWT.NONE);
        maxSlaveInputLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveInputLenLabel.setText("Max Slave Input Len:");
        
        int slaveInputLen = 160;
        if(_master.getMaxSlaveInputLen() >= 0) {
            slaveInputLen = _master.getMaxSlaveInputLen();
        }
        _maxSlaveInputLenText = ProfibusHelper.getTextField(gName,
                                                            true,
                                                            Integer.toString(slaveInputLen),
                                                            Ranges.getRangeValue(0, 9999, 100),
                                                            ProfibusHelper.VL_TYP_U16);
        _maxSlaveInputLenText.addModifyListener(getMLSB());
        _maxSlaveInputLenText.addModifyListener(listener);
        
        Label maxCalc = new Label(gName, SWT.NONE);
        maxCalc.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        maxCalc.setText("(Output Len + Input Len) * Max Nr Slaves < 13000");
        int value = (slaveInputLen + slaveOutputLen) * maxNrSlave;
        
        new Label(gName, SWT.NONE);
        _maxCalcText = ProfibusHelper.getTextField(gName, String.format("%1$d < %2$d = %3$b",
                                                                        value,
                                                                        limit,
                                                                        value < limit));
        
        Label maxSlaveDiagEntriesLabel = new Label(gName, SWT.NONE);
        maxSlaveDiagEntriesLabel
                .setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveDiagEntriesLabel.setText("Max Slave Diag Entries:");
        
        value = 126;
        if(_master.getMaxSlaveDiagEntries() >= 0) {
            value = _master.getMaxSlaveDiagEntries();
        }
        _maxSlaveDiagEntriesText = ProfibusHelper.getTextField(gName,
                                                               true,
                                                               Integer.toString(value),
                                                               Ranges.getRangeValue(0, 9999, 126),
                                                               ProfibusHelper.VL_TYP_U16);
        _maxSlaveDiagEntriesText.addModifyListener(getMLSB());
        
        Label maxSlaveDiagLenLabel = new Label(gName, SWT.NONE);
        maxSlaveDiagLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveDiagLenLabel.setText("Max Slave Diag Len:");
        
        value = 32;
        if(_master.getMaxSlaveDiagLen() >= 0) {
            value = _master.getMaxSlaveDiagLen();
        }
        _maxSlaveDiagLenText = ProfibusHelper.getTextField(gName,
                                                           true,
                                                           Integer.toString(value),
                                                           Ranges.getRangeValue(0, 9999, 32),
                                                           ProfibusHelper.VL_TYP_U16);
        _maxSlaveDiagLenText.addModifyListener(getMLSB());
        
        Label maxBusParaLenLabel = new Label(gName, SWT.NONE);
        maxBusParaLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxBusParaLenLabel.setText("Max Bus Para Len:");
        
        value = 128;
        if(_master.getMaxBusParaLen() >= 0) {
            value = _master.getMaxBusParaLen();
        }
        _maxBusParaLenText = ProfibusHelper.getTextField(gName,
                                                         true,
                                                         Integer.toString(value),
                                                         Ranges.getRangeValue(0, 9999, 128),
                                                         ProfibusHelper.VL_TYP_U16);
        _maxBusParaLenText.addModifyListener(getMLSB());
        
        Label maxSlaveParaLenLabel = new Label(gName, SWT.NONE);
        maxSlaveParaLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveParaLenLabel.setText("Max Slave Para Len:");
        
        value = 244;
        if(_master.getMaxSlaveParaLen() >= 0) {
            value = _master.getMaxSlaveParaLen();
        }
        _maxSlaveParaLenText = ProfibusHelper.getTextField(gName,
                                                           true,
                                                           Integer.toString(value),
                                                           Ranges.getRangeValue(0, 9999, 244),
                                                           ProfibusHelper.VL_TYP_U16);
        _maxSlaveParaLenText.addModifyListener(getMLSB());
        
    }
    
    /**
     * @param head
     *            is TabHead Text
     * @throws PersistenceException 
     */
    private void master(@Nonnull final String head) throws PersistenceException {
        
        Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 4, 350, 240);
        // Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 6,
        // 650, 440);
        ((GridLayout) comp.getLayout()).makeColumnsEqualWidth = false;
        makeNameGroup(comp, 4);
        makeRedundencyMasterGroup(comp, 2);
        makeParametersGroup(comp);
        makeMemoryAddressingGroup(comp);
        initIndexCombo();
        makeInformationGroup(comp, 2);
        makeFmbSetGroup(comp);
        makeMasterUserData(comp, 2);
        makeDescGroup(comp, 3);
    }
    
    private void makeMemoryAddressingGroup(@Nonnull final Composite comp) {
        _memAddressType = _master.getProfibusPnoId();
        _oldMemAddressType = _memAddressType;
        
        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                _memAddressType = (Integer) ((Button) e.getSource()).getData();
                setSavebuttonEnabled("MasterMemAddressType",
                                     !_oldMemAddressType.equals(_memAddressType));
            }
        };
        
        Group gMemoryAddressing = new Group(comp, SWT.NONE);
        gMemoryAddressing.setText("Memory Address Mode:");
        gMemoryAddressing.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        gMemoryAddressing.setLayout(new GridLayout(2, false));
        
        final Button direct = new Button(gMemoryAddressing, SWT.RADIO);
        direct.setText("Array");
        direct.setData(0);
        direct.addSelectionListener(selectionListener);
        final Button dyn = new Button(gMemoryAddressing, SWT.RADIO);
        dyn.setText("Compact");
        dyn.setData(1);
        dyn.addSelectionListener(selectionListener);
        switch (_memAddressType) {
            case 1:
                dyn.setSelection(true);
                break;
            case 0:
            default:
                direct.setSelection(true);
                break;
        }
    }
    
    private void makeMasterUserData(@Nonnull final Composite comp, final int column) {
        Group masterUserData = new Group(comp, SWT.NONE);
        masterUserData.setText("Master User Data:");
        masterUserData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, column, 1));
        masterUserData.setLayout(new GridLayout(1, false));
        masterUserData.setTabList(new Control[0]);
        _masterUserDataText = new Text(masterUserData, SWT.NONE);
        _masterUserDataText.setEditable(false);
        _masterUserDataText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        
        // is a Default Value. Is not a part of the Master GSD File.
        if( (_master != null) && (_master.getMasterUserData() != null)
                && (_master.getMasterUserData().length() > 0)) {
            _masterUserDataText.setText(_master.getMasterUserData());
        } else {
            _masterUserDataText
                    .setText("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        }
    }
    
    private void makeParametersGroup(@Nonnull final Composite comp) {
        Group gParameters = new Group(comp, SWT.NONE);
        gParameters.setText("Parameters:");
        gParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 3));
        gParameters.setLayout(new GridLayout(3, false));
        
        // min. Slave Interval
        new Label(gParameters, SWT.NONE);// .setText("[micros]");
        new Label(gParameters, SWT.NONE).setText("min. Slave Interval: ");
        _minSlaveIntervalText = ProfibusHelper.getTextField(gParameters,
                                                            true,
                                                            Integer.toString(_master
                                                                    .getMinSlaveInt()),
                                                            Ranges.getRangeValue(0, 10000, 6),
                                                            ProfibusHelper.VL_TYP_U16);
        _minSlaveIntervalText.addModifyListener(getMLSB());
        
        // Poll Timeout
        new Label(gParameters, SWT.NONE).setText("[tBit]");
        new Label(gParameters, SWT.NONE).setText("Poll Timeout: ");
        _pollTimeOutText = ProfibusHelper.getTextField(gParameters,
                                                       true,
                                                       Integer.toString(_master.getPollTime()),
                                                       Ranges.getRangeValue(0, 100000, 1000),
                                                       ProfibusHelper.VL_TYP_U16);
        _pollTimeOutText.addModifyListener(getMLSB());
        
        // Data Control Time
        new Label(gParameters, SWT.NONE).setText("[tBit]");
        new Label(gParameters, SWT.NONE).setText("Data Control Time: ");
        _dataControlTimeText = ProfibusHelper.getTextField(gParameters,
                                                           true,
                                                           Integer.toString(_master
                                                                   .getDataControlTime()),
                                                           Ranges.getRangeValue(0, 10000, 100),
                                                           ProfibusHelper.VL_TYP_U16);
        _dataControlTimeText.addModifyListener(getMLSB());
        
        // Autoclear
        new Label(gParameters, SWT.NONE).setText("[tBit]");
        new Label(gParameters, SWT.NONE).setText("Autoclear: ");
        _autoclearButton = new Button(gParameters, SWT.CHECK | SWT.LEFT);
        _autoclearButton.setText("");
        
        if(_master != null) {
            _autoclearButton.setSelection(_master.isAutoclear());
        }
        _autoclearButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                saveButtonEnb();
            }
            
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                saveButtonEnb();
            }
            
            private void saveButtonEnb() {
                setSavebuttonEnabled("MasterAutoclear", _autoclearButton.getSelection());
                
            }
        });
    }
    
    private void makeInformationGroup(@Nonnull final Composite comp, final int column) {
        Group gInformation = new Group(comp, SWT.NONE);
        gInformation.setText("Information:");
        gInformation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, column, 1));
        gInformation.setLayout(new GridLayout(4, false));
        gInformation.setTabList(new Control[0]);
        
        Label vendorLabel = new Label(gInformation, SWT.NONE);
        vendorLabel.setText("Vendor: ");
        _vendorText = new Text(gInformation, SWT.NONE);
        _vendorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        _vendorText.setEditable(false);
        
        Label idNoLabel = new Label(gInformation, SWT.NONE);
        idNoLabel.setText("Ident. No.: ");
        _idNoText = new Text(gInformation, SWT.NONE);
        _idNoText.setEditable(false);
        
        Label pbBoardLabel = new Label(gInformation, SWT.NONE);
        pbBoardLabel.setText("Profibusboard: ");
        _pbBoardText = new Text(gInformation, SWT.NONE);
        _pbBoardText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        _pbBoardText.setEditable(false);
        
        Label stationTypLabel = new Label(gInformation, SWT.NONE);
        stationTypLabel.setText("Station Typ: ");
        _stationTypText = new Text(gInformation, SWT.NONE);
        _stationTypText.setEditable(false);
        
    }
    
    private void makeRedundencyMasterGroup(@Nonnull final Composite comp, final int column) throws PersistenceException {
        Group gRedundencyMaster = new Group(comp, SWT.NONE);
        gRedundencyMaster.setText("Redundency Master:");
        gRedundencyMaster.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, column, 1));
        gRedundencyMaster.setLayout(new GridLayout(3, false));
        
        _redundentButton = new Button(gRedundencyMaster, SWT.CHECK);
        _redundentButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        _redundentButton.setText("Redunden IOC: ");
        
        if( (_master.getRedundant() < 0)
                && !_master.getFreeStationAddress().contains((short) (_master.getSortIndex() + 1))) {
            _redundentButton.setEnabled(false);
            gRedundencyMaster
                    .setToolTipText("The Station address for the redundency Master is occupied");
        }
        _redundentButton.setSelection(_master.getRedundant() >= 0);
        _redundentButton.setData(_master.getRedundant() >= 0);
        
        _redundentButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                select();
            }
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                select();
            }
            
            private void select() {
                setSavebuttonEnabled("MasterRedundent",
                                     _redundentButton.getSelection() != (Boolean) _redundentButton
                                             .getData());
                Short sortIndex = (Short) ((StructuredSelection) _indexCombo.getSelection())
                        .getFirstElement();
                try {
                    _freeStationAddress = _master.getFreeMStationAddress(_redundentButton
                            .getSelection());
                    _indexCombo.setInput(_freeStationAddress);
                    _indexCombo.setSelection(new StructuredSelection(sortIndex));
                } catch (PersistenceException e) {
                    LOG.error(e);
                    DeviceDatabaseErrorDialog.open(null, "Database error!", e);
                }
            }
        });
    }
    
    private void makeNameGroup(@Nonnull final Composite comp, final int column) {
        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, column, 1));
        gName.setLayout(new GridLayout(3, false));
        
        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _master.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);
        
        // Label
        Label slotIndexLabel = new Label(gName, SWT.NONE);
        slotIndexLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        slotIndexLabel.setText("Station Adress:");
        
        // Sort Index Combo
        _indexCombo = new ComboViewer(gName, SWT.DROP_DOWN | SWT.READ_ONLY);
        _indexCombo.getCombo().setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
        _indexCombo.setContentProvider(new ArrayContentProvider());
        _indexCombo.setLabelProvider(new LabelProvider());
    }
    
    /**
     * @throws PersistenceException 
     *
     */
    private void initIndexCombo() throws PersistenceException {
        fillStationAddressCombo();
        _indexCombo.getCombo().setData(_indexCombo.getCombo().getSelectionIndex());
        _indexCombo.getCombo().addModifyListener(getMLSB());
        _indexCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                short index = (Short) ((StructuredSelection) _indexCombo.getSelection())
                        .getFirstElement();
                try {
                    getNode().moveSortIndex(index);
                    if(getNode().getParent() != null) {
                        getProfiBusTreeView().refresh(getNode().getParent());
                    } else {
                        getProfiBusTreeView().refresh();
                    }
                } catch (PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't move Master! Database Error", e);
                    LOG.error(e);
                }
            }
        });
    }
    
    private void fillStationAddressCombo() throws PersistenceException {
        _freeStationAddress = _master.getFreeMStationAddress(_redundentButton.getSelection());
        Short sortIndex = _master.getSortIndex();
        if(sortIndex >= 0) {
            if(!_freeStationAddress.contains(sortIndex)) {
                _freeStationAddress.add(sortIndex);
            }
            _indexCombo.setInput(_freeStationAddress);
            _indexCombo.setSelection(new StructuredSelection(sortIndex));
        } else {
            _indexCombo.setInput(_freeStationAddress);
            _indexCombo.getCombo().select(0);
            _master.setSortIndexNonHibernate((Short) ((StructuredSelection) _indexCombo
                    .getSelection()).getFirstElement());
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        Date now = new Date();
        
        // Name
        _master.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());
        
        Short stationAddress = (Short) ((StructuredSelection) _indexCombo.getSelection())
                .getFirstElement();
        
        try {
            _master.setSortIndexNonHibernate(stationAddress);
            if(_redundentButton.getSelection()) {
                _redundentButton.setData(true);
                _master.setRedundant((short) (stationAddress + 1));
            } else {
                _redundentButton.setData(false);
                _master.setRedundant((short) -1);
            }
            _indexCombo.getCombo().setData(_indexCombo.getCombo().getSelectionIndex());
            
            // Information
            _master.setVendorName(_vendorText.getText());
            _master.setProfibusdpmasterBez(_pbBoardText.getText());
            // Parameters
            _master.setMinSlaveInt(Integer.valueOf(_minSlaveIntervalText.getText()));
            _master.setPollTime(Integer.valueOf(_pollTimeOutText.getText()));
            _master.setDataControlTime(Integer.valueOf(_dataControlTimeText.getText()));
            _master.setAutoclear(_autoclearButton.getSelection());
            // MasterUserData
            _master.setMasterUserData(_masterUserDataText.getText());
            // Mem Adress Type
            _master.setProfibusPnoId(_memAddressType);
            _oldMemAddressType = _memAddressType;
            // Document
            Set<DocumentDBO> docs = getDocumentationManageView().getDocuments();
            _master.setDocuments(docs);
            
            // update header
            getHeaderField(HeaderFields.MODIFIED_BY).setText(ConfigHelper.getUserName());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            getHeaderField(HeaderFields.MODIFIED_ON).setText(df.format(now));
            
            // GSD File
            _master.setGSDFile(_gsdFile);
            
            // FMB Set
            _master.setMaxNrSlave(Integer.parseInt(_maxNrSlaveText.getText()));
            _master.setMaxSlaveOutputLen(Integer.parseInt(_maxSlaveOutputLenText.getText()));
            _master.setMaxSlaveInputLen(Integer.parseInt(_maxSlaveInputLenText.getText()));
            _master.setMaxSlaveDiagEntries(Integer.parseInt(_maxSlaveDiagEntriesText.getText()));
            _master.setMaxSlaveDiagLen(Integer.parseInt(_maxSlaveDiagLenText.getText()));
            _master.setMaxBusParaLen(Integer.parseInt(_maxBusParaLenText.getText()));
            _master.setMaxSlaveParaLen(Integer.parseInt(_maxSlaveParaLenText.getText()));
            
            fillStationAddressCombo();
            save();
        } catch (PersistenceException e) {
            LOG.error(e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Master! Database error.", e);
        }
    }
    
    /** {@inheritDoc}  */
    @Override
    public final void fill(@CheckForNull final GSDFileDBO gsdFile) throws PersistenceException {
        if(gsdFile == null) {
            return;
        }
        ParsedGsdFileModel parsedGsdFileModel;
        parsedGsdFileModel = gsdFile.getParsedGsdFileModel();
        
        // setGSDData
        getHeaderField(HeaderFields.VERSION).setText(parsedGsdFileModel.getRevision());
        _vendorText.setText(parsedGsdFileModel.getVendorName());
        _pbBoardText.setText(parsedGsdFileModel.getModelName());
        String hex = Integer.toHexString(parsedGsdFileModel.getIdentNumber()).toUpperCase();
        if(hex.length() > 4) {
            hex = hex.substring(hex.length() - 4, hex.length());
        }
        _idNoText.setText("0x" + hex);
        _stationTypText.setText(parsedGsdFileModel.getIntProperty("Station_Type").toString());
        _gsdFile = gsdFile;
    }
    
    /** {@inheritDoc} */
    @Override
    @CheckForNull
    public final GSDFileDBO getGsdFile() {
        if(_master != null) {
            return _master.getGSDFile();
        }
        return null;
    }
    
    @Override
    public final void setGsdFile(GSDFileDBO gsdFile) {
        _master.setGSDFile(gsdFile);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void cancel() {
        super.cancel();
        if(_indexCombo != null) {
            _indexCombo.getCombo().select((Integer) _indexCombo.getCombo().getData());
            getNameWidget().setText((String) getNameWidget().getData());
            Boolean selected = (Boolean) _redundentButton.getData();
            _redundentButton.setSelection(selected);
            if(_master != null) {
                _gsdFile = _master.getGSDFile();
                getHeaderField(HeaderFields.VERSION).setText("");
                _vendorText.setText("");
                _pbBoardText.setText("");
                _idNoText.setText("");
                _stationTypText.setText("");
                try {
                    fill(_gsdFile);
                    _minSlaveIntervalText.setText(_master.getMinSlaveInt() + "");
                    _pollTimeOutText.setText(_master.getPollTime() + "");
                    _dataControlTimeText.setText(_master.getDataControlTime() + "");
                    _autoclearButton.setSelection(_master.isAutoclear());
                } catch (PersistenceException e) {
                    LOG.error(e);
                    DeviceDatabaseErrorDialog.open(null, "Can't undo. Database error", e);
                }
            } else {
                _gsdFile = null;
                _minSlaveIntervalText.setText("");
                _pollTimeOutText.setText("");
                _dataControlTimeText.setText("");
                _autoclearButton.setSelection(false);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    AbstractGsdPropertyModel getGsdPropertyModel() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    List<Integer> getPrmUserDataList() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void setPrmUserData(Integer index, Integer value) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    Integer getPrmUserData(Integer index) {
        // TODO Auto-generated method stub
        return null;
    }
}
