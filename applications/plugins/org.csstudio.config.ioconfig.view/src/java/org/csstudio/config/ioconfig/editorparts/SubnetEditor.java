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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.Baudrates;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges;
import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class SubnetEditor extends AbstractNodeEditor<ProfibusSubnetDBO> {

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.subnet";

    private static final Logger LOG = LoggerFactory.getLogger(SubnetEditor.class);

    /**
     * An array with all kinds of Baudrates.
     */
    private static final Baudrates[] DB_BAUDRATES = new Baudrates[] {Baudrates.DP_KBAUD_9_6,
                                                                     Baudrates.DP_KBAUD_19_2, Baudrates.DP_KBAUD_45_45, Baudrates.DP_KBAUD_93_75,
                                                                     Baudrates.DP_KBAUD_187_5, Baudrates.DP_KBAUD_500, Baudrates.DP_KBAUD_750,
                                                                     Baudrates.DP_MBAUD_1_5, Baudrates.DP_MBAUD_3, Baudrates.DP_MBAUD_6,
                                                                     Baudrates.DP_MBAUD_12, };

    /**
     * The Profibus Subnet Object.
     */
    private ProfibusSubnetDBO _subnet;

    /**
     * A Combo field with the Highest Profibus address.
     */
    private Combo _hSAddress;

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
     * Combo to select the facility of subnet.
     */
    private ComboViewer _facilityViewer;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cancel() {
        super.cancel();

        // Cancel General

        getNameWidget().setText((String) getNameWidget().getData());
        resetSelection(_facilityViewer.getCombo());

        getIndexSpinner().setSelection(((Short) getIndexSpinner().getData()));


        // Net Setting
        _subnet.setHsa(Short.valueOf(_hSAddress.getItem(_hSAddress.getSelectionIndex())));
        _hSAddress.setText( ((Integer) _hSAddress.getData()).toString());

        _baudList.getCombo().select((Integer) _baudList.getCombo().getData());

        // -- Busparameter
        resetString(_maxTsdr);
        resetString(_tslotInit);
        resetString(_minTsdr);
        resetString(_tset);
        resetString(_tqui);
        resetSelection(_gapCombo);

        _subnet.setRepeaterNumber(Short.parseShort(_retrayCombo.getItem(_retrayCombo
                                                                        .getSelectionIndex()))); // repeater Number ???

        resetSelection(_retrayCombo);
        resetString(_ttr);
        resetString(_watchdog);

        // Document

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        super.createPartControl(parent);
        _subnet = getNode();
        // No Subnet, create a new one.
        if (_subnet == null) {
            newNode();
            _subnet.setTtr(750000);
            _subnet.setWatchdog(1000);
        }
        // Headline for the different Tab's.
        final String[] heads = {"General", "Net Settings" };
        netSetting(heads[1]);
        general(heads[0]);
        selecttTabFolder(0);
        getTabFolder().pack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        final Date now = new Date();
        boolean updateChildrens = false;

        // Store General

        if(!getNameWidget().getText().equals(_subnet.getName())) {
            _subnet.setName(getNameWidget().getText());
            updateChildrens = true;
        }
        getNameWidget().setData(getNameWidget().getText());

        getIndexSpinner().setData(_subnet.getSortIndex());
        _subnet.setUpdatedOn(new Date());

        _subnet.setUpdatedBy(ConfigHelper.getUserName());

        _subnet.setUpdatedOn(now);

        final String facility = (String) ((StructuredSelection) _facilityViewer.getSelection())
        .getFirstElement();
        _subnet.setProfil(facility);
        _facilityViewer.getCombo().setData(_facilityViewer.getCombo().getSelectionIndex());

        // Net Setting
        int index = _hSAddress.getSelectionIndex();
        if (index < 0) {
            index = 0;
        }
        _subnet.setHsa(Short.valueOf(_hSAddress.getItem(index)));
        _hSAddress.setData(_hSAddress.getSelectionIndex());

        _subnet.setBaudRate( ((Baudrates) _baudList.getElementAt(_baudList.getCombo()
                                                                 .getSelectionIndex())).getVal()
                                                                 + "");
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

        _subnet.setRepeaterNumber(Short.parseShort(_retrayCombo.getItem(_retrayCombo
                                                                        .getSelectionIndex()))); // repeater Number ???
        _retrayCombo.setData(_retrayCombo.getItem(_retrayCombo.getSelectionIndex()));

        _subnet.setTtr(Long.parseLong(_ttr.getText()));
        _ttr.setData(_ttr.getText());

        _subnet.setWatchdog(Integer.parseInt(_watchdog.getText()));
        _watchdog.setData(_watchdog.getText());

        // update header
        getHeaderField(HeaderFields.MODIFIED_BY).setText(ConfigHelper.getUserName());
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        getHeaderField(HeaderFields.MODIFIED_ON).setText(df.format(now));

        // Document
        final Set<DocumentDBO> docs = getDocumentationManageView().getDocuments();
        _subnet.setDocuments(docs);


        try {
            if (updateChildrens) {
                _subnet.update();
            }
            save();
        } catch (final PersistenceException e) {
            LOG.error("Can't save Subnet! Database error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Subnet! Database error.", e);
        }
    }

    /**
     * @param parent
     *            The Parent Composite.
     */
    private void bottom(@Nonnull final Composite parent) {
        final GridDataFactory gdf = GridDataFactory.fillDefaults();

        final Group bottomGroup = new Group(parent, SWT.NONE);
        bottomGroup.setLayout(new GridLayout(3, false));
        bottomGroup.setLayoutData(gdf.create());
        bottomGroup.setText("Busparameter");

        // Left side
        final Composite left = new Composite(bottomGroup, SWT.NONE);
        left.setLayoutData(gdf.create());
        left.setLayout(new GridLayout(3, true));
        left(left);
        // Separator
        new Label(bottomGroup, SWT.VERTICAL | SWT.SEPARATOR).setLayoutData(new GridData(SWT.FILL,
                                                                                        SWT.FILL,
                                                                                        false,
                                                                                        false));
        // Rigth Side
        final Composite rigth = new Composite(bottomGroup, SWT.NONE);
        rigth.setLayoutData(gdf.create());
        rigth.setLayout(new GridLayout(3, true));
        rigth(rigth);

    }

    /**
     * @param head
     *            is TabHead Text
     */
    private void general(@Nonnull final String head) {
        final InstanceScope instanceScope = new InstanceScope();
        final IEclipsePreferences prefNode = instanceScope.getNode(IOConfigActivator.PLUGIN_ID);

        final Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5, 470, 260);
        comp.setLayout(new GridLayout(4, false));

        final Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
        gName.setLayout(new GridLayout(4, false));

        final Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _subnet.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);

        _facilityViewer = new ComboViewer(gName);
        _facilityViewer.setContentProvider(new ArrayContentProvider());
        final String[] facilities = prefNode.get(PreferenceConstants.DDB_FACILITIES, "NONE").split(",");
        _facilityViewer.setInput(facilities);
        if ( _subnet.getProfil() == null || _subnet.getProfil().isEmpty()) {
            _facilityViewer.getCombo().select(0);
            _facilityViewer.getCombo().setData(0);
        } else {
            _facilityViewer.setSelection(new StructuredSelection(_subnet.getProfil()));
            _facilityViewer.getCombo().setData(_facilityViewer.getCombo().getSelectionIndex());
        }
        _facilityViewer.getCombo().addModifyListener(getMLSB());

        setIndexSpinner(ConfigHelper.getIndexSpinner(gName,
                                                     _subnet,
                                                     getMLSB(),
                                                     "Index",
                                                     getProfiBusTreeView()));

        makeDescGroup(comp, 3);
    }

    /**
     * @param left
     *            The Parent Composite.
     */
    private void left(@Nonnull final Composite left) {
        // Tslot_Init
        Label front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tslot_Init: ");
        String val;
        final long min = Ranges.TSLOT_INIT.getMin();
        if (_subnet.getSlotTime() < min) {
            val = Ranges.TSLOT_INIT.getDefault() + "";
        } else {
            val = _subnet.getSlotTime() + "";
        }

        _tslotInit = ProfibusHelper.getTextField(left,
                                                 true,
                                                 val,
                                                 Ranges.TSLOT_INIT,
                                                 ProfibusHelper.VL_TYP_U16);
        _tslotInit.addModifyListener(getMLSB());
        new Label(left, SWT.NONE).setText("[t_Bit]");

        // Max Tsdr
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Max. Tsdr.: ");
        if (_subnet.getMaxTsdr() < Ranges.MAX_TSDR.getMin()) {
            val = Ranges.MAX_TSDR.getDefault() + "";
        } else {
            val = _subnet.getMaxTsdr() + "";
        }
        _maxTsdr = ProfibusHelper.getTextField(left, true, val, Ranges.MAX_TSDR, ProfibusHelper.VL_TYP_U16);
        _maxTsdr.addModifyListener(getMLSB());
        new Label(left, SWT.NONE).setText("[t_Bit]");

        // Min Tsdr
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Min. Tsdr.: ");
        if (_subnet.getMinTsdr() < Ranges.MIN_TSDR.getMin()) {
            val = Ranges.MIN_TSDR.getDefault() + "";
        } else {
            val = _subnet.getMinTsdr() + "";
        }

        _minTsdr = ProfibusHelper.getTextField(left,
                                               true,
                                               val,
                                               Ranges.MIN_TSDR,
                                               ProfibusHelper.VL_TYP_U16);
        _minTsdr.addModifyListener(getMLSB());
        new Label(left, SWT.NONE).setText("[t_Bit]");

        // tset
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("tset: ");
        if (_subnet.getTset() < Ranges.TSET.getMin()) {
            val = Ranges.TSET.getDefault() + "";
        } else {
            val = _subnet.getTset() + "";
        }

        _tset = ProfibusHelper.getTextField(left, true, val, Ranges.TSET, ProfibusHelper.VL_TYP_U08);
        _tset.addModifyListener(getMLSB());
        new Label(left, SWT.NONE).setText("[t_Bit]");

        // tqui
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tqui: ");
        if (_subnet.getMinTsdr() < Ranges.TQUI.getMin()) {
            val = Ranges.TQUI.getDefault() + "";
        } else {
            val = _subnet.getTqui() + "";
        }
        _tqui = ProfibusHelper.getTextField(left,
                                            true,
                                            val,
                                            Ranges.TQUI,
                                            ProfibusHelper.VL_TYP_U08);
        _tqui.addModifyListener(getMLSB());
        new Label(left, SWT.NONE).setText("[t_Bit]");

        // Gap
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Gap-Factor: ");
        _gapCombo = new Combo(left, SWT.SINGLE | SWT.DROP_DOWN | SWT.SIMPLE | SWT.RIGHT);
        _gapCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        for (long i = Ranges.GAP_RANGE.getMin(); i <= Ranges.GAP_RANGE.getMax(); i++) {
            _gapCombo.add(i + "");
        }
        if ( 1 <= _subnet.getGap() && _subnet.getGap() <= _gapCombo.getItemCount()) {
            _gapCombo.select(_subnet.getGap() - 1);
        } else {
            // default GAP is 10 (index 9)
            _gapCombo.select(9);
        }
        _gapCombo.setData(_gapCombo.getSelectionIndex());
        _gapCombo.addModifyListener(getMLSB());
        _gapCombo.addTraverseListener(ProfibusHelper.getNETL());

        front = new Label(left, SWT.NONE);

        // Retry limit
        front = new Label(left, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Retry limit: ");
        _retrayCombo = new Combo(left, SWT.SINGLE | SWT.DROP_DOWN | SWT.SIMPLE | SWT.RIGHT);
        _retrayCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        for (int i = 1; i <= 8; i++) {
            _retrayCombo.add(i + "");
        }
        if ( 1 <= _subnet.getRepeaterNumber()
                && _subnet.getRepeaterNumber() <= _retrayCombo.getItemCount()) {
            _retrayCombo.select(_subnet.getRepeaterNumber() - 1);
        } else {
            _retrayCombo.select(0);
        }
        _retrayCombo.setData(_retrayCombo.getSelectionIndex());
        _retrayCombo.addModifyListener(getMLSB());

        new Label(left, SWT.NONE).setText("");

        for (final Control children : left.getChildren()) {
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            }
        }

    }

    /**
     * @param headline is TabHead Text
     */
    private void netSetting(@Nonnull final String headline) {
        final Composite comp = ConfigHelper.getNewTabItem(headline, getTabFolder(), 1, 470, 350);

        final Group topGroup = new Group(comp, SWT.NONE);
        topGroup.setLayout(new GridLayout(1, false));
        topGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        bottom(comp);
        final GridLayoutFactory glf = GridLayoutFactory.fillDefaults();
        glf.numColumns(2);
        final GridDataFactory gdf = GridDataFactory.fillDefaults();
        final Composite buttonComp = new Composite(topGroup, SWT.NONE);
        buttonComp.setLayout(glf.create());
        buttonComp.setLayoutData(gdf.create());
        final Composite buttonComp2 = new Composite(topGroup, SWT.NONE);
        buttonComp2.setLayout(glf.create());
        buttonComp2.setLayoutData(gdf.create());

        final Label adressLabel = new Label(buttonComp, SWT.NONE);
        adressLabel.setText("Highest Profibus Station:");

        _hSAddress = new Combo(buttonComp, SWT.SINGLE | SWT.RIGHT);
        _hSAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        for (int i = 1; i <= 126; i++) {
            _hSAddress.add("" + i);
        }
        setCombo(_hSAddress, Short.toString(_subnet.getHsa()));

        final Label baudLabel = new Label(buttonComp2, SWT.NONE);
        baudLabel.setText("Baudrate: ");

        //--- DP BAUDRATES ----
        _baudList = new ComboViewer(buttonComp2, SWT.SINGLE | SWT.V_SCROLL | SWT.RIGHT);
        _baudList.getCombo().setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        _baudList.add(DB_BAUDRATES);
        // Default is 1,5 MBit
        _baudList.getCombo().select(7);
        for (int i = 0; i < DB_BAUDRATES.length; i++) {
            if ( _subnet.getBaudRate() != null && _subnet.getBaudRate().equals(Integer
                                                                               .toString(DB_BAUDRATES[i].getVal()))) {
                _baudList.getCombo().select(i);
                break;
            }
        }
        _baudList.getCombo().setData(_baudList.getCombo().getSelectionIndex());
        _baudList.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(@Nullable final SelectionChangedEvent event) {
                final boolean b = (Integer) _baudList.getCombo().getData() != _baudList.getCombo()
                .getSelectionIndex();
                setSavebuttonEnabled("SubNetBaud", b);
            }
        });
    }

    /**
     * @param rigth
     *            The Parent Group.
     */
    private void rigth(@Nonnull final Composite rigth) {
        final Control[] control = new Control[2];

        // tslot
        Label front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tslot: ");
        _tslot = new Text(rigth, SWT.SINGLE | SWT.RIGHT);
        _tslot.setEditable(false);
        _tslot.setText(_subnet.getSlotTime() + "");
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // tid2
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tid2: ");
        _tid2 = new Text(rigth, SWT.SINGLE | SWT.RIGHT);
        _tid2.setEditable(false);
        _tid2.setText(_subnet.getMaxTsdr() + "");
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // Trdy
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Trdy: ");
        _trdy = new Text(rigth, SWT.SINGLE | SWT.RIGHT);
        _trdy.setEditable(false);
        _trdy.setText(_subnet.getMinTsdr() + "");
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // tid1
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Tid1: ");
        _tid1 = new Text(rigth, SWT.SINGLE | SWT.RIGHT);
        _tid1.setEditable(false);
        _tid1.setText(_subnet.getTset() + "");
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // ttr
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Ttr: ");
        _ttr = ProfibusHelper.getTextField(rigth,
                                           true,
                                           _subnet.getTtr() + "",
                                           Ranges.TTR,
                                           ProfibusHelper.VL_TYP_U32);
        new Label(rigth, SWT.NONE).setText("[t_Bit]");
        control[0] = _ttr;

        // ttr in ms
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        final Text ttr2 = ProfibusHelper.getTextField(rigth, Float.valueOf(_subnet.getTtr() / 1000f)
                                                      + "");
//        final Text ttr2 = ProfibusHelper.getTextField(rigth, Float.valueOf(_subnet.getTtr() / 1000)
//                                                      + "");
        new Label(rigth, SWT.NONE).setText("ms");

        // ttr Typisch
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Ttr Typisch: ");
        ProfibusHelper.getTextField(rigth, _subnet.getMinTsdr() + "");
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // Watchdog
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("Watchdog: ");
        _watchdog = ProfibusHelper.getTextField(rigth,
                                                true,
                                                _subnet.getWatchdog() + "",
                                                Ranges.WATCHDOG,
                                                ProfibusHelper.VL_TYP_U16);
        control[1] = _watchdog;
        new Label(rigth, SWT.NONE).setText("[t_Bit]");

        // ttr in ms
        front = new Label(rigth, SWT.NONE);
        front.setAlignment(SWT.RIGHT);
        front.setText("=");
        _watchdog2 = new Text(rigth, SWT.SINGLE | SWT.RIGHT);
        _watchdog2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        _watchdog2.setEditable(false);
        _watchdog2.setText(Integer.toString(Integer.parseInt(_watchdog.getText()) / 10));
        new Label(rigth, SWT.NONE).setText("ms");

        _ttr.addModifyListener(getMLSB());
        _ttr.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                ttr2.setText(Float.toString(Integer.parseInt(_ttr.getText()) / 1000f));
            }

        });

        _watchdog.addModifyListener(getMLSB());
        _watchdog.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(@Nonnull final ModifyEvent e) {
                _watchdog2.setText(Float.toString(Integer.parseInt(_watchdog.getText()) / 10f));
            }
        });

        for (final Control children : rigth.getChildren()) {
            if (children instanceof Label) {
                children.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            }
        }
        rigth.setTabList(control);
    }
}
