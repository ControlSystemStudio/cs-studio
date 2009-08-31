/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.Keywords;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdMasterModel;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
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
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class MasterConfigComposite extends NodeConfig {

    /*
     *  Data.
     */
    /** 
     * The ProfibusDPMaster.                   
     */
    private Master _master;
    /** 
     * The Selected GSD FFile for the Master.  
     */
    private GSDFile _gsdFile;

    /*
     *  GUI Elements.
     */
    /**
     *  Check button to set auto clear on/off.             
     */
    private Button _autoclearButton;
    /**
     *  The description field for the Vendor Information.
     */
    private Text _vendorText;
    /**
     *  The description field for the Board Information. 
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
     *  The description field for the Master User Data.  
     */
    private Text _masterUserDataText;
    /** 
     * The text field for the description of the Master.
     */
    private Text _descText;
    
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

    /**
     * @param parent Parent Composite.
     * @param profiBusTreeView The IO Config TreeView.
     * @param master the Profibus Master to Configer.
     */
    public MasterConfigComposite(final Composite parent,final ProfiBusTreeView profiBusTreeView, final Master master) {
        super(parent, profiBusTreeView, "Profibus Master Configuration",master, master==null);
        _master = master;
        if(_master==null){
            newNode();
            _master.setMinSlaveInt(6);
            _master.setPollTime(1000);
            _master.setDataControlTime(100);
        }else{
            _gsdFile = _master.getGSDFile();
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        String[] heads = {"Master", "GSD File LIst"};
        master(heads[0]);
        documents();
        ConfigHelper.makeGSDFileChooser(getTabFolder(),heads[1],this, Keywords.GSDFileTyp.Master);
        if(_gsdFile!=null){
            fill(_gsdFile);
        }

//        _tabFolder.pack ();
    }

    private void makeFmbSetGroup(Composite parent) {
        final int limit = 13000;
        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                int value = (Integer.parseInt(_maxSlaveInputLenText.getText())+Integer.parseInt(_maxSlaveOutputLenText.getText()))*Integer.parseInt(_maxNrSlaveText.getText());
                _maxCalcText.setText(String.format("%1$d < %2$d = %3$b", value,limit,value<limit));
                
            }
            
        };

        Group gName = new Group(parent,SWT.NONE);
        gName.setText("FMB Set");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,1));
        gName.setLayout(new GridLayout(2,false));
        
        Label maxNrSlaveLabel = new Label(gName, SWT.NONE);
        maxNrSlaveLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxNrSlaveLabel.setText("Max Number Slaves:");
        
        int min = 0;
        
        TreeMap<Short, ? extends Node> map = (TreeMap<Short, ? extends Node>) _master.getChildrenAsMap();
        if(map.size()>0) {
            min = map.lastKey();
        }
        int maxNrSlave;
        if(min<=0&&_master.getMaxNrSlave()<=0) {
            //default
            maxNrSlave = 60;
        } else if(min>_master.getMaxNrSlave()) {
            maxNrSlave = min;
        } else {
            maxNrSlave = _master.getMaxNrSlave();
        }
        _maxNrSlaveText = ProfibusHelper.getTextField(gName, true, Integer.toString(maxNrSlave), Ranges.getRangeValue(min,9999, 60), ProfibusHelper.VL_TYP_U16); 
        _maxNrSlaveText.addModifyListener(getMLSB());
        _maxNrSlaveText.addModifyListener(listener);
        
        Label maxSlaveOutputLenLabel = new Label(gName, SWT.NONE);
        maxSlaveOutputLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveOutputLenLabel.setText("Max Slave Output Len:");

        int slaveOutputLen = 160;
        if(_master.getMaxSlaveOutputLen()>=0) {
            slaveOutputLen = _master.getMaxSlaveOutputLen();
        }
        _maxSlaveOutputLenText = ProfibusHelper.getTextField(gName, true, Integer.toString(slaveOutputLen), Ranges.getRangeValue(0,9999, 100), ProfibusHelper.VL_TYP_U16);
        _maxSlaveOutputLenText.addModifyListener(getMLSB());
        _maxSlaveOutputLenText.addModifyListener(listener);
        
        Label maxSlaveInputLenLabel = new Label(gName, SWT.NONE);
        maxSlaveInputLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveInputLenLabel.setText("Max Slave Input Len:");

        int slaveInputLen = 160;
        if(_master.getMaxSlaveInputLen()>=0) {
            slaveInputLen = _master.getMaxSlaveInputLen();
        }
        _maxSlaveInputLenText = ProfibusHelper.getTextField(gName, true, Integer.toString(slaveInputLen), Ranges.getRangeValue(0,9999, 100), ProfibusHelper.VL_TYP_U16);
        _maxSlaveInputLenText.addModifyListener(getMLSB());
        _maxSlaveInputLenText.addModifyListener(listener);
        
        Label maxCalc = new Label(gName, SWT.NONE);
        maxCalc.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,2,1));
        maxCalc.setText("(Output Len + Input Len) * Max Nr Slaves < 13000");
        int value = (slaveInputLen+slaveOutputLen)*maxNrSlave;
        
        new Label(gName, SWT.NONE);
        _maxCalcText = ProfibusHelper.getTextField(gName, String.format("%1$d < %2$d = %3$b", value,limit,value<limit)); 
        
        
        Label maxSlaveDiagEntriesLabel = new Label(gName, SWT.NONE);
        maxSlaveDiagEntriesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveDiagEntriesLabel.setText("Max Slave Diag Entries:");
        
        value = 126;
        if(_master.getMaxSlaveDiagEntries()>=0) {
            value = _master.getMaxSlaveDiagEntries();
        }
        _maxSlaveDiagEntriesText = ProfibusHelper.getTextField(gName, true, Integer.toString(value), Ranges.getRangeValue(0,9999, 126), ProfibusHelper.VL_TYP_U16);
        _maxSlaveDiagEntriesText.addModifyListener(getMLSB());
        
        Label maxSlaveDiagLenLabel = new Label(gName, SWT.NONE);
        maxSlaveDiagLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveDiagLenLabel.setText("Max Slave Diag Len:");
        
        value = 32;
        if(_master.getMaxSlaveDiagLen()>=0) {
            value = _master.getMaxSlaveDiagLen();
        }
        _maxSlaveDiagLenText = ProfibusHelper.getTextField(gName, true, Integer.toString(value), Ranges.getRangeValue(0,9999, 32), ProfibusHelper.VL_TYP_U16);
        _maxSlaveDiagLenText.addModifyListener(getMLSB());
        
        Label maxBusParaLenLabel = new Label(gName, SWT.NONE);
        maxBusParaLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxBusParaLenLabel.setText("Max Bus Para Len:");
        
        value = 128;
        if(_master.getMaxBusParaLen()>=0) {
            value = _master.getMaxBusParaLen();
        }
        _maxBusParaLenText = ProfibusHelper.getTextField(gName, true, Integer.toString(value), Ranges.getRangeValue(0,9999, 128), ProfibusHelper.VL_TYP_U16);
        _maxBusParaLenText.addModifyListener(getMLSB());
        
        Label maxSlaveParaLenLabel = new Label(gName, SWT.NONE);
        maxSlaveParaLenLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxSlaveParaLenLabel.setText("Max Slave Para Len:");
        
        value = 244;
        if(_master.getMaxSlaveParaLen()>=0) {
            value = _master.getMaxSlaveParaLen();
        }
        _maxSlaveParaLenText = ProfibusHelper.getTextField(gName, true, Integer.toString(value), Ranges.getRangeValue(0,9999, 244), ProfibusHelper.VL_TYP_U16);
        _maxSlaveParaLenText.addModifyListener(getMLSB());        
        

    }

    /**
     * @param head is TabHead Text
     */
    private void master(final String head) {
        
        Composite comp = ConfigHelper.getNewTabItem(head,getTabFolder(),5);

        makeNameGroup(comp);
        makeRedundencyMasterGroup(comp);
        makeMemoryAddressingGroup(comp);
        makeParametersGroup(comp);
        makeInformationGroup(comp);
        makeMasterUserData(comp);
        makeDescGroup(comp);
        
        makeFmbSetGroup(comp);
    }
    
    private void makeDescGroup(Composite comp) {
        Group gDesc = new Group(comp,SWT.NONE);
        gDesc.setText("Description: ");
        gDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,1));
        gDesc.setLayout(new GridLayout(1,false));
        
        _descText = new Text(gDesc, SWT.BORDER | SWT.MULTI);
        _descText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _descText.setEnabled(false);
    }

    private void makeMemoryAddressingGroup(Composite comp) {
        _memAddressType=_master.getProfibusPnoId();
        _oldMemAddressType=_memAddressType;
        
        SelectionListener selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _memAddressType = (Integer) ((Button) e.getSource()).getData();
                setSavebuttonEnabled("MasterMemAddressType", _oldMemAddressType!=_memAddressType);
            }
        };
        
        Group gMemoryAddressing = new Group(comp,SWT.NONE);
        gMemoryAddressing.setText("Memory Address Mode:");
        gMemoryAddressing.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
        gMemoryAddressing.setLayout(new GridLayout(2,false));

        final Button direct = new Button(gMemoryAddressing, SWT.RADIO);
        direct.setText("Array");
        direct.setData(0);
        direct.addSelectionListener(selectionListener);
        final Button dyn = new Button(gMemoryAddressing, SWT.RADIO);
        dyn.setText("Compact");
        dyn.setData(1);
        dyn.addSelectionListener(selectionListener);
        switch(_memAddressType){
            case 1:
                dyn.setSelection(true);
                break;
            case 0:
            default :
                direct.setSelection(true);
                break;
        }        
    }

    private void makeMasterUserData(Composite comp) {
        Group masterUserData = new Group(comp,SWT.NONE);
        masterUserData.setText("Master User Data:");
        masterUserData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3,1));
        masterUserData.setLayout(new GridLayout(1,false));
        masterUserData.setTabList(new Control[0]);
        _masterUserDataText = new Text(masterUserData,SWT.NONE);
        _masterUserDataText.setEditable(false);
        _masterUserDataText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));

        // is a Default Value. Is not a part of the Master GSD File.
        if(_master!=null&&_master.getMasterUserData()!=null&&_master.getMasterUserData().length()>0){
            _masterUserDataText.setText(_master.getMasterUserData());
        }else{
            _masterUserDataText.setText("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        }
    }

    private void makeParametersGroup(Composite comp) {
        Group gParameters = new Group(comp,SWT.NONE);
        gParameters.setText("Parameters:");
        gParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,3));
        gParameters.setLayout(new GridLayout(3,false));
        
        new Label(gParameters,SWT.NONE);//.setText("[micros]");
        
        Label minSlaveIntervalLabel = new Label(gParameters,SWT.NONE);
        minSlaveIntervalLabel.setText("min. Slave Interval: ");
        _minSlaveIntervalText = ProfibusHelper.getTextField(gParameters, true, Integer.toString(_master.getMinSlaveInt()), Ranges.getRangeValue(0,10000, 6), ProfibusHelper.VL_TYP_U16);

        new Label(gParameters,SWT.NONE).setText("[tBit]");
        
        Label pollTimeOutLabel = new Label(gParameters,SWT.NONE);
        pollTimeOutLabel.setText("Poll Timeout: ");
        _pollTimeOutText = ProfibusHelper.getTextField(gParameters, true, Integer.toString(_master.getPollTime()), Ranges.getRangeValue(0,100000, 1000), ProfibusHelper.VL_TYP_U16);

        new Label(gParameters,SWT.NONE).setText("[tBit]");
        
        Label dataControlTimeLabel = new Label(gParameters,SWT.NONE);
        dataControlTimeLabel.setText("Data Control Time: ");
        _dataControlTimeText = ProfibusHelper.getTextField(gParameters, true, Integer.toString(_master.getDataControlTime()), Ranges.getRangeValue(0,10000, 100), ProfibusHelper.VL_TYP_U16);
        
        new Label(gParameters,SWT.NONE).setText("[tBit]");
        
        new Label(gParameters,SWT.NONE).setText("Autoclear: ");
        _autoclearButton = new Button(gParameters,SWT.CHECK|SWT.LEFT);
        _autoclearButton.setText("");
        if(_master!=null){
            _autoclearButton.setSelection(_master.isAutoclear());
        }
        _autoclearButton.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final   SelectionEvent e) {}

            public void widgetSelected(final   SelectionEvent e) {
                setSavebuttonEnabled("MasterAutoclear", _autoclearButton.getSelection());
            }
        });        
    }

    private void makeInformationGroup(Composite comp) {
        Group gInformation = new Group(comp,SWT.NONE);
        gInformation.setText("Information:");
        gInformation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3,1));
        gInformation.setLayout(new GridLayout(4,false));
        gInformation.setTabList(new Control[0]);
        
        Label vendorLabel = new Label(gInformation,SWT.NONE);
        vendorLabel.setText("Vendor: ");
        _vendorText = new Text(gInformation,SWT.NONE);
        _vendorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
        _vendorText.setEditable(false);

        Label pbBoardLabel = new Label(gInformation,SWT.NONE);
        pbBoardLabel.setText("Profibusboard: ");
        _pbBoardText = new Text(gInformation,SWT.NONE);
        _pbBoardText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
        _pbBoardText.setEditable(false);

        Label idNoLabel = new Label(gInformation,SWT.NONE);
        idNoLabel.setText("Ident. No.: ");
        _idNoText = new Text(gInformation,SWT.NONE);
        _idNoText.setEditable(false);

        Label stationTypLabel = new Label(gInformation,SWT.NONE);
        stationTypLabel.setText("Station Typ: ");
        _stationTypText = new Text(gInformation,SWT.NONE);
        _stationTypText.setEditable(false);
        
    }

    private void makeRedundencyMasterGroup(Composite comp) {
        Group gRedundencyMaster = new Group(comp,SWT.NONE);
        gRedundencyMaster.setText("Redundency Master:");
        gRedundencyMaster.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,1));
        gRedundencyMaster.setLayout(new GridLayout(2,false));        
    }

    private void makeNameGroup(Composite comp) {
        Group gName = new Group(comp,SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5,1));
        gName.setLayout(new GridLayout(3,false));    

        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _master.getName(),255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);
        
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _master, getMLSB(),"Station Address:",getProfiBusTreeView()));
    
    }

    /**
     * Store all Data in {@link Master} DB object.
     */
    public final void store() {
        super.store();
        Date now = new Date();

        //Name
        _master.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());

//        _master.moveSortIndex((short)getIndexSpinner().getSelection());
        _master.setFdlAddress((short)getIndexSpinner().getSelection());
        getIndexSpinner().setData(getIndexSpinner().getSelection());
        
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
        _oldMemAddressType=_memAddressType;
        // Document
        Set<Document> docs = getDocumentationManageView().getDocuments();
        _master.setDocuments(docs);

        // update header
        ((Text) this.getData("modifiedBy")).setText(ConfigHelper.getUserName());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        ((Text) this.getData("modifiedOn")).setText(df.format(now));
        
        // GSD File
        _master.setGSDFile(_gsdFile);
        
        // FMB Set
        //-----------------------------
        _master.setMaxNrSlave(Integer.parseInt(_maxNrSlaveText.getText())); 
        _master.setMaxSlaveOutputLen(Integer.parseInt(_maxSlaveOutputLenText.getText())); 
        _master.setMaxSlaveInputLen(Integer.parseInt(_maxSlaveInputLenText.getText())); 
        _master.setMaxSlaveDiagEntries(Integer.parseInt(_maxSlaveDiagEntriesText.getText()));
        _master.setMaxSlaveDiagLen(Integer.parseInt(_maxSlaveDiagLenText.getText()));
        _master.setMaxBusParaLen(Integer.parseInt(_maxBusParaLenText.getText()));
        _master.setMaxSlaveParaLen(Integer.parseInt(_maxSlaveParaLenText.getText()));
        //-----------------------------
        
        save();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean fill(final GSDFile gsdFile) {
        GsdMasterModel masterModel = GsdFactory.makeGsdMaster(gsdFile.getGSDFile());
        
        //setGSDData
        _master.setGSDMasterData(masterModel);
        
        ((Text)this.getData("version")).setText(masterModel.getRevisionNumber()+"");
        _vendorText.setText(masterModel.getVendorName());
        _pbBoardText.setText(masterModel.getModelName());
        String hex = Integer.toHexString(masterModel.getIdentNumber()).toUpperCase();
        if(hex.length()>4){
            hex=hex.substring(hex.length()-4,hex.length());
        }
        _idNoText.setText("0x"+hex);
        _stationTypText.setText(masterModel.getStationType()+"");
        _gsdFile = gsdFile;
        this.layout();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public final GSDFile getGSDFile() {
        return _master.getGSDFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node getNode() {
        if(_master==null){
            StructuredSelection selection = (StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection();
            if (selection.getFirstElement() instanceof ProfibusSubnet) {
                ProfibusSubnet profibusSubnet = (ProfibusSubnet) selection.getFirstElement();
                _master = new Master(profibusSubnet);    
            }
        }
        return _master;
    }

    
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void cancel() {
        super.cancel();
        getNameWidget().setText((String)getNameWidget().getData());
        getIndexSpinner().setSelection((Short)getIndexSpinner().getData());
        if(_master!=null){
            _gsdFile =null;
            if(_master.getGSDFile()!=null){
                _gsdFile = _master.getGSDFile();
                fill(_gsdFile);
            }else{
                ((Text)MasterConfigComposite.this.getData("version")).setText("");
                _vendorText.setText("");
                _pbBoardText.setText("");
                _idNoText.setText("");
                _stationTypText.setText("");
            }
            _minSlaveIntervalText.setText(_master.getMinSlaveInt()+"");
            _pollTimeOutText.setText(_master.getPollTime()+"");
            _dataControlTimeText.setText(_master.getDataControlTime()+"");
            _autoclearButton.setSelection(_master.isAutoclear());
        }else{
            _gsdFile = null;
            fill(_gsdFile);
            _minSlaveIntervalText.setText("");
            _pollTimeOutText.setText("");
            _dataControlTimeText.setText("");
            _autoclearButton.setSelection(false);
        }
    }
}
