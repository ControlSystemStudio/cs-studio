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

import org.csstudio.config.ioconfig.config.view.helper.Baudrates;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.06.2007
 */
public class SubNetConfigComposite extends NodeConfig {

    /**
     * The Profibus Subnet Object.
     */
    private ProfibusSubnet _subnet;

    /** 
     * The text field for the description of the Subnet.
     */
    private Text _descText;
    
    /** 
     * A Combo field with the Highest Profibus address.
     */
    private Combo _adress;
    
    /** 
     * An array with all kinds of Baudrate.
     */
    private Baudrates[] _dbBaudrates;
    
    /** 
     * A List viewer to show and select all kinds of Baudrate. 
     */
    private ComboViewer _baudList;
    
    /** 
     * Text field for the Tslot Init value. 
     */
    private Text _tslotInit;
    /** 
     * Text field for the max Tsdr value.   
     */
    private Text _maxTsdr;
    /** 
     * Text field for the min Tsdr value.   
     */
    private Text _minTsdr;
    /** 
     * Text field for the tset Init value.  
     */
    private Text _tset;
    /** 
     * Text field for the tqui Init value.  
     */
    private Text _tqui;
    /** 
     * Combo to select the Gap Value (Range 1-128).
     */
    private Combo _gapCombo;
    /** 
     * Combo to select the retray limit.   
     */
    private Combo _retrayCombo;
    /** 
     * Text field for the tslot Init value. 
     */
    private Text _tslot;
    /** 
     * Text field for the tid2 Init value.  
     */
    private Text _tid2;
    /** 
     * Text field for the tid1 Init value.  
     */
    private Text _tid1;
    /** 
     * Text field for the trdy Init value.  
     */
    private Text _trdy;
    /** 
     * Text field for the ttr Init value.           
     */
    private Text _ttr;
    /** 
     * Text field for the Watchdog Init value.      
     */
    private Text _watchdog;
    /** 
     * Text field for the Watchdog in ms value.     
     */
    private Text _watchdog2;

    /**
     * @param parent The Parent Composite.
     * @param profiBusTreeView The Tree of all node from the IO Config.
     * @param subnet to Configure. Is NULL create a new one.
     */
    public SubNetConfigComposite(final Composite parent, final ProfiBusTreeView profiBusTreeView, final ProfibusSubnet subnet) {
        super(parent, profiBusTreeView, "Profibus Subnet Configuration", subnet, subnet==null);
        _subnet = subnet;

        // No Subnet, create a new one.
        if(_subnet==null){
            newNode();
            _subnet.setTtr(750000);
            _subnet.setWatchdog(1000);
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        parent.addControlListener(new ControlListener() {

            public void controlMoved(final ControlEvent e) {
            }

            public void controlResized(final ControlEvent e) {
                SubNetConfigComposite.this.pack();
            }
        });

        // Headline for the different Tab's.
        String[] heads = { "General", "Net Settings" };
        general(heads[0]);
        netSetting(heads[1]);
        documents();
        getTabFolder().pack();
    }

    /**
     * Store all Data in {@link Subnet} DB object.
     */
    public final void store() {
        super.store();
        Date now = new Date();
        
        // Store General
        
        _subnet.setName(getNameWidget().getText());
//        _subnet.updateName(_nameText.getText());
        getNameWidget().setData(getNameWidget().getText());
        
//        _subnet.setSortIndex((short)getIndexSpinner().getSelection());
        getIndexSpinner().setData(_subnet.getSortIndex());
        _subnet.setUpdatedOn(new Date());
        
        _subnet.setDescription(_descText.getText());
        _descText.setData(_descText.getText());
        
        _subnet.setUpdatedBy(ConfigHelper.getUserName());

        _subnet.setUpdatedOn(now);
        
        // Net Setting
        int index = _adress.getSelectionIndex();
        if(index<0){
            index = 0;
        }
        _subnet.setHsa(Short.valueOf(_adress.getItem(index)));
        _adress.setData(_adress.getSelectionIndex());

        _subnet.setBaudRate(((Baudrates) _baudList.getElementAt(_baudList.getCombo()
                .getSelectionIndex())).getVal()+"");
        _baudList.getCombo().setData(_baudList.getCombo().getSelectionIndex());
        // -- Busparameter
        _subnet.setSlotTime(Integer.parseInt(_tslotInit.getText()));
        _tslotInit.setData(_tslotInit.getText());
        
        _subnet.setMaxTsdr(Integer.parseInt(_maxTsdr.getText()));
        _maxTsdr.setData(_maxTsdr.getText());
        
        _subnet.setMinTsdr(Integer.parseInt(_minTsdr.getText()));
        _minTsdr.setData(_minTsdr.getText());
        
        _subnet.setTset(Short.parseShort(_tset.getText()));
        _tset.setData(_tset.getText());
        
        _subnet.setTqui(Short.parseShort(_tqui.getText()));
        _tqui.setData(_tqui.getText());
        
        _subnet.setGap(Short.parseShort(_gapCombo.getItem(_gapCombo.getSelectionIndex())));
        _gapCombo.setData(_gapCombo.getItem(_gapCombo.getSelectionIndex()));
        
        _subnet.setRepeaterNumber(Short.parseShort(_retrayCombo.getItem(_retrayCombo.getSelectionIndex())));  // repeater Number ???
        _retrayCombo.setData(_retrayCombo.getItem(_retrayCombo.getSelectionIndex()));
        
        _subnet.setTtr(Long.parseLong(_ttr.getText()));
        _ttr.setData(_ttr.getText());
        
        _subnet.setWatchdog(Integer.parseInt(_watchdog.getText()));
        _watchdog.setData(_watchdog.getText());

        
        // update header
        ((Text) this.getData("modifiedBy")).setText(ConfigHelper.getUserName());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        ((Text) this.getData("modifiedOn")).setText(df.format(now));

        // Document
        Set<Document> docs = getDocumentationManageView().getDocuments();
        _subnet.setDocuments(docs);
        
        save();
    }

    /**
     * {@inheritDoc}
     */
    public final void cancel() {
        super.cancel();

        // Cancel General
        
        getNameWidget().setText((String)getNameWidget().getData());
        
        getIndexSpinner().setSelection((Integer)getIndexSpinner().getData());
        
        _descText.setText((String)_descText.getData());
        
        // Net Setting
        _subnet.setHsa(Short.valueOf(_adress.getItem(_adress.getSelectionIndex())));
        _adress.setText(((Integer)_adress.getData()).toString());

        _baudList.getCombo().select((Integer)_baudList.getCombo().getData());

        // -- Busparameter
        _tslotInit.setText((String)_tslotInit.getData());
        
        _maxTsdr.setText((String)_maxTsdr.getData());
        
        _minTsdr.setText((String)_minTsdr.getData());
        
        _tset.setText((String)_tset.getData());
        
        _tqui.setText((String)_tqui.getData());
        
        _gapCombo.select((Integer)_gapCombo.getData());
        
        _subnet.setRepeaterNumber(Short.parseShort(_retrayCombo.getItem(_retrayCombo.getSelectionIndex())));  // repeater Number ???
        _retrayCombo.select((Integer)_retrayCombo.getData());
        
        _ttr.setText((String)_ttr.getData());
        
        _watchdog.setText((String)_watchdog.getData());

        
        // Document
        
    }

    
    /**
     * @param head
     *            is TabHead Text
     */
    private void general(final String head) {
        Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5);
        comp.setLayout(new GridLayout(4, false));

        Group gName = new Group(comp,SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5,1));
        gName.setLayout(new GridLayout(3,false));

        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _subnet.getName(),255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _subnet, getMLSB(),"Index",getProfiBusTreeView()));
        
        Group gDesc = new Group(comp,SWT.NONE);
        gDesc.setText("Description: ");
        gDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5,1));
        gDesc.setLayout(new GridLayout(1,false));
        _descText = new Text(gDesc, SWT.BORDER | SWT.MULTI);
        _descText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        setText(_descText, _subnet.getDescription(),255);
    }

    /**
     * @param headline
     *            is TabHead Text
     */
    private void netSetting(final String headline) {
        final Composite comp = ConfigHelper.getNewTabItem(headline, getTabFolder(), 5);
        comp.setLayout(new GridLayout(2, false));

        Group topGroup = new Group(comp,SWT.NONE);
        topGroup.setLayout(new GridLayout(2, false));
        topGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,2,1));
        
        bottom(comp);
        final Composite buttonComp = new Composite(topGroup, SWT.NONE);
        buttonComp.setLayout(new GridLayout(2, false));
        buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        
        Label headLabel = new Label(buttonComp, SWT.NONE);
        headLabel.setText("Highest Profibus Station");
        headLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        Label adressLabel = new Label(buttonComp, SWT.NONE);
        adressLabel.setText("HSA:");

        _adress = new Combo(buttonComp, SWT.SINGLE);
        _adress .setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
        for (int i = 1; i <= 126; i++) {
            _adress.add("" + i);
        }
        setCombo(_adress, Short.toString(_subnet.getHsa()));

        Label baudLabel = new Label(buttonComp, SWT.NONE);
        baudLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        baudLabel.setText("Baudrate: ");

        /**
         * --- DP BAUDRATES ----
         */
        _dbBaudrates = new Baudrates[] {
                new Baudrates(" 9,6  kBAUD", "#define DP_KBAUD_9_6    0x00",0x00),
                new Baudrates("19,2  kBAUD", "#define DP_KBAUD_19_2   0x01",0x01),
                new Baudrates("45,45 kBAUD", "#define DP_KBAUD_45_45  0x0B",0x0B),
                new Baudrates("93,75 kBAUD", "#define DP_KBAUD_93_75  0x02",0x02),
                new Baudrates("187,5 kBAUD", "#define DP_KBAUD_187_5  0x03",0x03),
                new Baudrates("500   kBAUD", "#define DP_KBAUD_500    0x04",0x04),
                new Baudrates("750   kBAUD", "#define DP_KBAUD_750    0x05",0x05),
                new Baudrates("  1,5 MBAUD", "#define DP_MBAUD_1_5    0x06",0x06),
                new Baudrates("  3   MBAUD", "#define DP_MBAUD_3      0x07",0x07),
                new Baudrates("  6   MBAUD", "#define DP_MBAUD_6      0x08",0x08),
                new Baudrates(" 12   MBAUD", "#define DP_MBAUD_12     0x09",0x09) };
        _baudList = new ComboViewer(buttonComp, SWT.SINGLE | SWT.V_SCROLL|SWT.RIGHT);
        _baudList.getCombo().setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
        _baudList.add(_dbBaudrates);
        // Default is 1,5 MBit
        _baudList.getCombo().select(7);
        for (int i = 0; i < _dbBaudrates.length; i++) {
            if ((_subnet.getBaudRate() != null && _subnet.getBaudRate().equals(Integer.toString(
                    _dbBaudrates[i].getVal())))) {
                _baudList.getCombo().select(i);
                break;
            }
        }
        _baudList.getCombo().setData(_baudList.getCombo().getSelectionIndex());
        _baudList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                boolean b = ((Integer)_baudList.getCombo().getData())!=_baudList.getCombo().getSelectionIndex();
                setSavebuttonEnabled("SubNetBaud", b);
            }
        });
    }

    /**
     * @param parent The Parent Composite.
     */
    private void bottom(final Composite parent) {
        Group bottomGroup = new Group(parent,SWT.NONE);
        bottomGroup.setLayout(new GridLayout(2, false));
        bottomGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,2,1));
        bottomGroup.setText("Busparameter");
        
        // Left side
        Group left = new Group(bottomGroup,SWT.NONE);
        left.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        left.setLayout(new GridLayout(3,true));
        left(left);
        // Rigth Side
        Group rigth = new Group(bottomGroup,SWT.NONE);
        rigth.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        rigth.setLayout(new GridLayout(3,true));
        rigth(rigth);
        
    }

    /**
     * @param rigth The Parent Group.
     */
    private void rigth(final Group rigth) {
        Control[] control = new Control[2];
        
        // tslot
        Label front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tslot: ");
        _tslot = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _tslot.setEditable(false);
        _tslot.setText(_subnet.getSlotTime()+"");
        new Label(rigth,SWT.NONE).setText("[t_Bit]");

        // tid2
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tid2: ");
        _tid2 = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _tid2.setEditable(false);
        _tid2.setText(_subnet.getMaxTsdr()+"");
        new Label(rigth,SWT.NONE).setText("[t_Bit]");

        // Trdy
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Trdy: ");
        _trdy = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _trdy.setEditable(false);
        _trdy.setText(_subnet.getMinTsdr()+"");
        new Label(rigth,SWT.NONE).setText("[t_Bit]");

        // tid1
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tid1: ");
        _tid1 = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _tid1.setEditable(false);
        _tid1.setText(_subnet.getTset()+"");
        new Label(rigth,SWT.NONE).setText("[t_Bit]");

        // ttr
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Ttr: ");
        _ttr = ProfibusHelper.getTextField(rigth, true, _subnet.getTtr()+"", Ranges.TTR, ProfibusHelper.VL_TYP_U32);
        new Label(rigth,SWT.NONE).setText("[t_Bit]");
        control[0]=_ttr; 
        
        // ttr in ms
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        final Text ttr2 = ProfibusHelper.getTextField(rigth, Float.valueOf(_subnet.getTtr()/1000)+"");
        new Label(rigth,SWT.NONE).setText("ms");

        // ttr Typisch
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Ttr Typisch: ");
        ProfibusHelper.getTextField(rigth, _subnet.getMinTsdr()+"");
        new Label(rigth,SWT.NONE).setText("[t_Bit]");

        // Watchdog      
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Watchdog: ");
        _watchdog  = ProfibusHelper.getTextField(rigth, true, _subnet.getWatchdog()+"", Ranges.WATCHDOG, ProfibusHelper.VL_TYP_U16);
        control[1]=_watchdog;
        new Label(rigth,SWT.NONE).setText("[t_Bit]");
        
        // ttr in ms
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        _watchdog2 = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _watchdog2.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false,false));
        _watchdog2.setEditable(false);
        _watchdog2.setText(Integer.toString(Integer.parseInt(_watchdog.getText())/10));
        new Label(rigth,SWT.NONE).setText("ms");
        
        _ttr.addModifyListener(getMLSB());
        _ttr.addModifyListener(new ModifyListener(){

            public void modifyText(final ModifyEvent e) {
                ttr2.setText(Float.toString(Integer.parseInt(_ttr.getText())/1000f));
            }
            
        });

        _watchdog.addModifyListener(getMLSB());
        _watchdog.addModifyListener(new ModifyListener(){
            public void modifyText(final ModifyEvent e) {
                _watchdog2.setText(Float.toString(Integer.parseInt(_watchdog.getText())/10f));
            }
        });
        
        for (Control children : rigth.getChildren()) {
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
            }
//            if (children instanceof Text) {
//                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
//                children.addKeyListener(_switchFocusAtEnter);
//            }
        }
        rigth.setTabList(control);
    }

    /**
     * @param left The Parent Composite.
     */
    private void left(final Group left) {
        //Tslot_Init
        Label front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tslot_Init: ");
        String value;
        long min = Ranges.TSLOT_INIT.getMin();
        if(_subnet.getSlotTime()<min){
            value = Ranges.TSLOT_INIT.getDefault()+"";
        }else{
            value = _subnet.getSlotTime()+"";
        }

        _tslotInit = ProfibusHelper.getTextField(left, true, value, Ranges.TSLOT_INIT, ProfibusHelper.VL_TYP_U16);
        _tslotInit.addModifyListener(getMLSB());
        new Label(left,SWT.NONE).setText("[t_Bit]");

        //Max Tsdr
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Max. Tsdr.: ");
        if(_subnet.getMaxTsdr()<Ranges.MAX_TSDR.getMin()){
            value=Ranges.MAX_TSDR.getDefault()+"";
        }else{
            value = _subnet.getMaxTsdr()+"";
        }
        _maxTsdr = ProfibusHelper.getTextField(left, true, value, Ranges.MAX_TSDR, ProfibusHelper.VL_TYP_U16);
        _maxTsdr.addModifyListener(getMLSB());
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        //Min Tsdr
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Min. Tsdr.: ");
        if(_subnet.getMinTsdr()<Ranges.MIN_TSDR.getMin()){
            value = Ranges.MIN_TSDR.getDefault()+"";
        }else{
            value = _subnet.getMinTsdr()+"";
        }

        _minTsdr = ProfibusHelper.getTextField(left, true, value, Ranges.MIN_TSDR, ProfibusHelper.VL_TYP_U16);
        _minTsdr.addModifyListener(getMLSB());
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        // tset
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("tset: ");
        if(_subnet.getTset()<Ranges.TSET.getMin()){
            value = Ranges.TSET.getDefault()+"";
        }else{
            value = _subnet.getTset()+"";
        }

        _tset = ProfibusHelper.getTextField(left, true, value, Ranges.TSET, ProfibusHelper.VL_TYP_U08);
        _tset.addModifyListener(getMLSB());
        new Label(left,SWT.NONE).setText("[t_Bit]");

        // tqui
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tqui: ");
        if(_subnet.getMinTsdr()<Ranges.TQUI.getMin()){
            value = Ranges.TQUI.getDefault()+"";
        }else{
            value = _subnet.getTqui()+"";
        }
        _tqui = ProfibusHelper.getTextField(left, true, value, Ranges.TQUI, ProfibusHelper.VL_TYP_U08);
        _tqui.addModifyListener(getMLSB());
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        // Gap
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Gap-Factor: ");
        _gapCombo = new Combo(left,SWT.SINGLE|SWT.DROP_DOWN|SWT.SIMPLE|SWT.RIGHT);
        _gapCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
        for(long i=Ranges.GAP_RANGE.getMin();i<=Ranges.GAP_RANGE.getMax();i++){
            _gapCombo.add(i+"");
        }
        if(1<=_subnet.getGap()&&_subnet.getGap()<=_gapCombo.getItemCount()){
            _gapCombo.select(_subnet.getGap()-1);
        }else{
            // default GAP is 10 (index 9)
            _gapCombo.select(9);
        }
        _gapCombo.setData(_gapCombo.getSelectionIndex());
        _gapCombo.addModifyListener(getMLSB());
        _gapCombo.addTraverseListener(ProfibusHelper.getNETL());
        
        front = new Label(left,SWT.NONE);
        
        // Retry limit
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Retry limit: ");
        _retrayCombo = new Combo(left,SWT.SINGLE|SWT.DROP_DOWN|SWT.SIMPLE|SWT.RIGHT);
        _retrayCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
        for(int i=1;i<=8;i++){
            _retrayCombo.add(i+"");
        }
        if(1<=_subnet.getRepeaterNumber()&&_subnet.getRepeaterNumber()<=_retrayCombo.getItemCount()){
            _retrayCombo.select(_subnet.getRepeaterNumber()-1);
        }else{
            _retrayCombo.select(0);
        }
        _retrayCombo.setData(_retrayCombo.getSelectionIndex());
        _retrayCombo.addModifyListener(getMLSB());

        front = new Label(left,SWT.NONE);
        
        for (Control children : left.getChildren()) {
            
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
            }
//            if (children instanceof Text) {
//                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
//                children.addKeyListener(_switchFocusAtEnter);
//            }
            
        }

    }

    
    /** {@inheritDoc} */
    @Override
    public final boolean fill(final GSDFile gsdFile) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public final GSDFile getGSDFile() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node getNode() {
        if(_subnet==null){
            StructuredSelection selection = (StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection();
            if (selection.getFirstElement() instanceof Ioc) {
                Ioc ioc = (Ioc) selection.getFirstElement();
                _subnet = new ProfibusSubnet(ioc);    
            }
        }
        return _subnet;
    }
}
