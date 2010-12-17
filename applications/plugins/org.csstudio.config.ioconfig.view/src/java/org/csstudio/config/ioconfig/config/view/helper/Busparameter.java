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
 * $Id: Busparameter.java,v 1.1 2009/08/26 07:09:21 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 25.06.2007
 */
public class Busparameter extends Composite {

	/** 
	 * The Subnet Object. 
	 */
    private ProfibusSubnetDBO _subnet;
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
     * The Save Button.                            
     */
    private Button _saveButton;    
    /** 
     * ModifyListener to aktivate the Save Button. 
     */
    private ModifyListener _enableSafe = new ModifyListener(){

        @Override
        public void modifyText(@Nullable final ModifyEvent e) {
            _saveButton.setEnabled(true);
        }
        
    };
    
    /**
     * @param parent The Parent Composite
     * @param style The Style of this Composite
     * @param subnet The subnet Object {@link PROFIBUS_subnet}
     */
    public Busparameter(@Nonnull final Composite parent, final int style,@Nonnull final ProfibusSubnetDBO subnet) {
        super(parent, style);
        _subnet = subnet;
        this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        this.setLayout(new GridLayout(2,true));
        
        // Left side
        Group left = new Group(this,SWT.NONE);
        left.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        left.setLayout(new GridLayout(3,true));
        left(left);

        // Rigth Side
        Group rigth = new Group(this,SWT.NONE);
        rigth.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        rigth.setLayout(new GridLayout(3,true));
        rigth(rigth);
    }

    /**
     * @param rigth The Parent Composite.
     */
    private void rigth(@Nonnull final Group rigth) {
        
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
        
        // ttr in ms
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        ProfibusHelper.getTextField(rigth, Float.valueOf(_subnet.getTtr()/1000)+"");
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
        new Label(rigth,SWT.NONE).setText("[t_Bit]");
        
        // ttr in ms
        front = new Label(rigth,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        _watchdog2 = new Text(rigth,SWT.SINGLE|SWT.RIGHT);
        _watchdog2.setEditable(false);
        _watchdog2.setText(_subnet.getWatchdog()+"");
        new Label(rigth,SWT.NONE).setText("ms");
        
        _ttr.addModifyListener(_enableSafe);
        _watchdog.addModifyListener(_enableSafe);
        
        for (Control children : rigth.getChildren()) {
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
            }
        }
    }

    /**
     * @param left The Parent Composite.
     */
    private void left(@Nonnull final Group left) {
        //Tslot_Init
        Label front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tslot_Init: ");
        String text;
        long min = Ranges.TSLOT_INIT.getMin();
        if(_subnet.getSlotTime()<min){
            text = Ranges.TSLOT_INIT.getMin()+"";
        }else{
            text = _subnet.getSlotTime()+"";
        }

        _tslotInit = ProfibusHelper.getTextField(left, true, text, Ranges.TSLOT_INIT, ProfibusHelper.VL_TYP_U16);
        _tslotInit.addModifyListener(_enableSafe);
        new Label(left,SWT.NONE).setText("[t_Bit]");

        //Max Tsdr
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Max. Tsdr.: ");
        if(_subnet.getMaxTsdr()<Ranges.MAX_TSDR.getMin()){
            text=Ranges.MAX_TSDR.getMin()+"";
        }else{
            text = _subnet.getMaxTsdr()+"";
        }
        _maxTsdr = ProfibusHelper.getTextField(left, true, text, Ranges.MAX_TSDR, ProfibusHelper.VL_TYP_U16);
        _maxTsdr.addModifyListener(_enableSafe);
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        //Min Tsdr
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Min. Tsdr.: ");
        if(_subnet.getMinTsdr()<Ranges.MIN_TSDR.getMin()){
            text = Ranges.MIN_TSDR.getMin()+"";
        }else{
            text = _subnet.getMinTsdr()+"";
        }

        _minTsdr = ProfibusHelper.getTextField(left, true, text, Ranges.MIN_TSDR, ProfibusHelper.VL_TYP_U16);
        _minTsdr.addModifyListener(_enableSafe);
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        // tset
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("tset: ");
        if(_subnet.getTset()<Ranges.TSET.getMin()){
            text = Ranges.TSET.getMin()+"";
        }else{
            text = _subnet.getTset()+"";
        }

        _tset = ProfibusHelper.getTextField(left, true, text, Ranges.TSET, ProfibusHelper.VL_TYP_U08);
        _tset.addModifyListener(_enableSafe);
        new Label(left,SWT.NONE).setText("[t_Bit]");

        // tqui
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tqui: ");
        if(_subnet.getMinTsdr()<Ranges.TQUI.getMin()){
            text = Ranges.TQUI.getMin()+"";
        }else{
            text = _subnet.getTqui()+"";
        }
        _tqui = ProfibusHelper.getTextField(left, true, text, Ranges.TQUI, ProfibusHelper.VL_TYP_U08);
        _tqui.addModifyListener(_enableSafe);
        new Label(left,SWT.NONE).setText("[t_Bit]");
        
        // Gap
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Gap-Factor: ");
        _gapCombo = new Combo(left,SWT.SINGLE);
        for(long i=Ranges.GAP_RANGE.getMin();i<=Ranges.GAP_RANGE.getMax();i++){
            _gapCombo.add(i+"");
        }
        if(1<=_subnet.getGap()&&_subnet.getGap()<=_gapCombo.getItemCount()){
            _gapCombo.select(_subnet.getGap()-1);
        }else{
            _gapCombo.select(0);
        }
        _gapCombo.addSelectionListener(new SelectionListener(){

            @Override
            public void widgetDefaultSelected(@Nullable final SelectionEvent e) {}

            @Override
            public void widgetSelected(@Nullable final SelectionEvent e) {
                _saveButton.setEnabled(true);
            }
            
        });
        front = new Label(left,SWT.NONE);
        
        // Retry limit
        front = new Label(left,SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Retry limit: ");
        _retrayCombo = new Combo(left,SWT.SINGLE);
        for(int i=1;i<=8;i++){
            _retrayCombo.add(i+"");
        }
        if(1<=_subnet.getRepeaterNumber()&&_subnet.getRepeaterNumber()<=_retrayCombo.getItemCount()){
            _retrayCombo.select(_subnet.getRepeaterNumber()-1);
        }else{
            _retrayCombo.select(0);
        }
        _retrayCombo.addSelectionListener(new SelectionListener(){

            @Override
            public void widgetDefaultSelected(@Nullable final SelectionEvent e) {}

            @Override
            public void widgetSelected(@Nullable final SelectionEvent e) {
                _saveButton.setEnabled(true);
            }
            
        });

        new Label(left, SWT.NONE).setText("");
        
        for (Control children : left.getChildren()) {
            
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
            }
        }

    }
}
