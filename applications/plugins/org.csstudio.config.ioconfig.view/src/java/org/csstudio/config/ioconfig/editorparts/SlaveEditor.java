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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.OverviewLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
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

    /**
     * The Slave which displayed.
     */
    private SlaveDBO _slave;
    /**
     * The GSD File of the Slave.
     */
    private GSDFileDBO _gsdFile;
    /**
     * The Text field for the Vendor.
     */
    private Text _vendorText;
    /**
     * Slave ID Number.
     */
    private Text _iDNo;
    /**
     * The Text field for the Revision.
     */
    private Text _revisionsText;
    /**
     * The Module max size of this Slave.
     */
    private int _maxSize;
    /**
     * The number of max slots.
     */
    private Text _maxSlots;
    /**
     * List with User Prm Data's.
     */
    private TableViewer _userPrmDataList;
    /**
     * Inputs.
     */
    private Text _inputsText;
    /**
     * Outputs.
     */
    private Text _outputsText;
    /**
     * Die Bedeutung dieses Feldes ist noch unbekannt.
     */
    // private Text _unbekannt;
    /**
     * The minimum Station Delay Time.
     */
    private Text _minStationDelayText;
    /**
     * The Watchdog Time.
     */
    private Text _watchDogText;
    /**
     * Marker of Background Color for normal use. Get from widget by first use.
     */
    private Color _defaultBackgroundColor;
    /**
     * Check Button to de.-/activate Station Address.
     */
    private Button _stationAddressActiveCButton;
    private Button _freezeButton;
    private Button _failButton;
    private Button _watchDogButton;
    private Button _syncButton;
    private int _groupIdent;
    private int _groupIdentStored;
    private Group _groupsRadioButtons;
    private ComboViewer _indexCombo;
    private Group _currentUserParamDataGroup;
    private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();

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
        public void addListener(final ILabelProviderListener listener) {
            // handle no listener
        }

        /**
         * {@inheritDoc}
         */
        public void dispose() {
            if (_defaultBackgroundColor != null) {
                _defaultBackgroundColor.dispose();
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeListener(final ILabelProviderListener listener) {
            // handle no listener
        }

        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }

        public String getColumnText(final Object element, final int columnIndex) {
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
         * @param module
         * @param columnIndex
         */
        @Nullable
        private String onModule(@Nonnull final ModuleDBO module, final int columnIndex) {
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
        @Nullable
        private String onSlave(@Nonnull final SlaveDBO slave, final int columnIndex) {
//            TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst = slave.getGSDSlaveData()
//                    .getExtUserPrmDataConst();

            switch (columnIndex) {
                case 1:
                    return slave.getName();
                case 2:
//                    StringBuffer sb = new StringBuffer();
//                    Set<String> keySet = extUserPrmDataConst.keySet();
//                    for (String key : keySet) {
//                        sb.append(extUserPrmDataConst.get(key).getValue());
//                    }
//                    return sb.toString();
                    return slave.getPrmUserData();
                default:
                    break;
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        _slave = (SlaveDBO) getNode();
        super.createPartControl(parent);
        makeSlaveKonfiguration(parent);
        getTabFolder().setSelection(0);
    }

    /**
     * @param parent
     *            Parent Composite.
     * @param style
     *            Style of the Composite.
     * @param slave
     *            the Profibus Slave to Configer.
     */
    private void makeSlaveKonfiguration(final Composite parent) {
        boolean nevv = false;
        if (_slave == null) {
            if (!newNode("TODO")) {
                // this.dispose();
                setSaveButtonSaved();
                getProfiBusTreeView().getTreeViewer().setSelection(getProfiBusTreeView()
                        .getTreeViewer().getSelection());
                return;
            }
            nevv = true;
            _slave.setMinTsdr((short) 11);
            _slave.setWdFact1((short) 100);
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        String[] heads = { "Basics", "Settings", "Overview" };
        overview(heads[2]);
        settings(heads[1]);
        basics(heads[0]);
        if (_slave.getGSDFile() != null) {
            fill(_slave.getGSDFile());
        }

        getTabFolder().pack();
        if (nevv) {
            getTabFolder().setSelection(4);
        }
    }

    @SuppressWarnings("unchecked")
    private void overview(final String headline) {
        Composite comp = getNewTabItem(headline, 1);
        comp.setLayout(new GridLayout(1, false));

        TableViewer overViewer = new TableViewer(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                | SWT.FULL_SELECTION);
        overViewer.setContentProvider(new ArrayContentProvider());
        overViewer.setLabelProvider(new OverviewLabelProvider());
        overViewer.getTable().setHeaderVisible(true);
        overViewer.getTable().setLinesVisible(true);
        overViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumn c0 = new TableColumn(overViewer.getTable(), SWT.RIGHT);
        c0.setText("Adr");
        TableColumn c0b = new TableColumn(overViewer.getTable(), SWT.RIGHT);
        c0b.setText("Adr");
        TableColumn c1 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        c1.setText("Name");
        TableColumn c2 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        // c2.setWidth(200);
        c2.setText("IO Name");
        TableColumn c3 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        // c3.setWidth(200);
        c3.setText("IO Epics Address");
        TableColumn c4 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        // c4.setWidth(200);
        c4.setText("Desc");
        TableColumn c5 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        // c5.setWidth(60);
        c5.setText("Type");
        TableColumn c6 = new TableColumn(overViewer.getTable(), SWT.LEFT);
        // c5.setWidth(60);
        c6.setText("DB Id");

        overViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(final SelectionChangedEvent event) {
                // IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                getProfiBusTreeView().getTreeViewer().setSelection(event.getSelection(), true);
            }

        });

        ArrayList<AbstractNodeDBO> children = new ArrayList<AbstractNodeDBO>();
        Collection<ModuleDBO> modules = (Collection<ModuleDBO>) _slave.getChildrenAsMap().values();
        for (ModuleDBO module : modules) {
            children.add(module);
            Collection<ChannelStructureDBO> channelStructures = module.getChannelStructsAsMap()
                    .values();
            for (ChannelStructureDBO channelStructure : channelStructures) {
                Collection<ChannelDBO> channels = channelStructure.getChannelsAsMap().values();
                for (ChannelDBO channel : channels) {
                    children.add(channel);
                }

            }
        }
        overViewer.setInput(children);
        c0.pack();
        c0b.pack();
        c1.pack();
        c2.pack();
        c3.pack();
        c4.pack();
        c5.pack();
        c6.pack();
    }

    /**
     * @param head
     *            The Tab text.
     */
    private void basics(final String head) {

        Composite comp = getNewTabItem(head, 2);

        /*
         * Name
         */
        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
        gName.setLayout(new GridLayout(3, false));

        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _slave.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);

        // Label
        Label slotIndexLabel = new Label(gName, SWT.NONE);
        slotIndexLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        slotIndexLabel.setText("Station Adress:");

        _indexCombo = new ComboViewer(gName, SWT.DROP_DOWN | SWT.READ_ONLY);
        _indexCombo.getCombo().setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
        _indexCombo.setContentProvider(new ArrayContentProvider());
        _indexCombo.setLabelProvider(new LabelProvider());
        Collection<Short> freeStationAddress = _slave.getProfibusDPMaster().getFreeStationAddress();
        Short sortIndex = _slave.getSortIndex();
        if (sortIndex >= 0) {
            if (!freeStationAddress.contains(sortIndex)) {
                freeStationAddress.add(sortIndex);
            }
            _indexCombo.setInput(freeStationAddress);
            _indexCombo.setSelection(new StructuredSelection(sortIndex));
        } else {
            _indexCombo.setInput(freeStationAddress);
            _indexCombo.getCombo().select(0);
            _slave.setSortIndexNonHibernate((Short) ((StructuredSelection) _indexCombo
                    .getSelection()).getFirstElement());
        }
        _indexCombo.getCombo().setData(_indexCombo.getCombo().getSelectionIndex());
        _indexCombo.getCombo().addModifyListener(getMLSB());
        _indexCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                short index = (Short) ((StructuredSelection) _indexCombo.getSelection())
                        .getFirstElement();
                getNode().moveSortIndex(index);
                if (getNode().getParent() != null) {
                    getProfiBusTreeView().refresh(getNode().getParent());
                } else {
                    getProfiBusTreeView().refresh();
                }
            }
        });

        // setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _slave, getMLSB(),
        // "Station Adress:",getProfiBusTreeView()));
        // _defaultBackgroundColor = getIndexSpinner().getBackground();

        /*
         * Slave Information
         */
        Group slaveInfoGroup = new Group(comp, SWT.NONE);
        slaveInfoGroup.setText("Slave Information");
        slaveInfoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
        slaveInfoGroup.setLayout(new GridLayout(4, false));
        slaveInfoGroup.setTabList(new Control[0]);

        _vendorText = new Text(slaveInfoGroup, SWT.SINGLE | SWT.BORDER);
        _vendorText.setEditable(false);
        _vendorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

        _iDNo = new Text(slaveInfoGroup, SWT.SINGLE);
        _iDNo.setEditable(false);
        _iDNo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Label revisionsLable = new Label(slaveInfoGroup, SWT.NONE);
        revisionsLable.setText("Revision:");

        _revisionsText = new Text(slaveInfoGroup, SWT.SINGLE);
        _revisionsText.setEditable(false);
        _revisionsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        new Label(slaveInfoGroup, SWT.None).setText("Max. available slots:");
        _maxSlots = new Text(slaveInfoGroup, SWT.BORDER);
        _maxSlots.setEditable(false);

        /*
         * DP/FDL Access Group
         */
        Group dpFdlAccessGroup = new Group(comp, SWT.NONE);
        dpFdlAccessGroup.setText("DP / FDL Access");
        dpFdlAccessGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        dpFdlAccessGroup.setLayout(new GridLayout(2, false));

        Label stationAdrLabel = new Label(dpFdlAccessGroup, SWT.None);
        stationAdrLabel.setText("Station Address");

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd.minimumWidth = 50;

        _stationAddressActiveCButton = new Button(dpFdlAccessGroup, SWT.CHECK);
        _stationAddressActiveCButton.setText("Active");
        _stationAddressActiveCButton.setSelection(false);
        _stationAddressActiveCButton.setData(false);
        _stationAddressActiveCButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                change();
            }

            public void widgetSelected(final SelectionEvent e) {
                change();

            }

            private void change() {
                setSavebuttonEnabled("Button:" + _stationAddressActiveCButton.hashCode(),
                                     (Boolean) _stationAddressActiveCButton.getData() != _stationAddressActiveCButton
                                             .getSelection());
            }

        });

        /*
         * Input / Output Group
         */
        Group ioGroup = new Group(comp, SWT.NONE);
        ioGroup.setText("Inputs / Outputs");
        ioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
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
        inputLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        inputLabel.setText("Inputs: ");
        _inputsText = new Text(ioGroup, SWT.SINGLE);
        _inputsText.setEditable(false);
        _inputsText.setText(Integer.toString(input));

        Label outputsLabel = new Label(ioGroup, SWT.RIGHT);
        outputsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        outputsLabel.setText("Outputs: ");
        _outputsText = new Text(ioGroup, SWT.SINGLE);
        _outputsText.setEditable(false);
        _outputsText.setText(Integer.toString(output));

        /*
         * Description Group
         */
        makeDescGroup(comp, 3);
    }

    /**
     *
     */
    private void setSlots() {
        Formatter slotFormarter = new Formatter();
        slotFormarter.format(" %2d / %2d", _slave.getChildren().size(), _maxSize);
        _maxSlots.setText(slotFormarter.toString());
        if (_maxSize < _slave.getChildren().size()) {
            if (_defaultBackgroundColor == null) {
                _defaultBackgroundColor = Display.getDefault()
                        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
            }
            _maxSlots.setBackground(WARN_BACKGROUND_COLOR);
        } else {
            _maxSlots.setBackground(_defaultBackgroundColor);
        }
        slotFormarter = null;
    }

    /**
     * @param head
     *            the tabItemName
     */
    private void settings(final String head) {
        Composite comp = getNewTabItem(head, 2);
        comp.setLayout(new GridLayout(3, false));
        // Operation Mode
        Group operationModeGroup = new Group(comp, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.minimumWidth = 170;
        operationModeGroup.setLayoutData(layoutData);
        operationModeGroup.setText("Operation Mode");
        operationModeGroup.setLayout(new GridLayout(3, false));
        Label delayLabel = new Label(operationModeGroup, SWT.NONE);
        delayLabel.setText("Min. Station Delay");
        _minStationDelayText = ProfibusHelper.getTextField(operationModeGroup, true, _slave
                .getMinTsdr()
                + "", Ranges.WATCHDOG, ProfibusHelper.VL_TYP_U16);
        Label bitLabel = new Label(operationModeGroup, SWT.NONE);
        bitLabel.setText("Bit");

        _syncButton = new Button(operationModeGroup, SWT.CHECK);
        _syncButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        _syncButton.addTraverseListener(ProfibusHelper.getNETL());
        _syncButton.setText("Sync Request");
        _syncButton.setData(false);
        _syncButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                change(_syncButton);
            }

            public void widgetSelected(final SelectionEvent e) {
                change(_syncButton);
            }
        });
        _freezeButton = new Button(operationModeGroup, SWT.CHECK);
        _freezeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        _freezeButton.addTraverseListener(ProfibusHelper.getNETL());
        _freezeButton.setText("Freeze Request");
        _freezeButton.setSelection(false);
        _freezeButton.setData(false);
        _freezeButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                change(_freezeButton);
            }

            public void widgetSelected(final SelectionEvent e) {
                change(_freezeButton);
            }
        });
        _failButton = new Button(operationModeGroup, SWT.CHECK);
        _failButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        _failButton.addTraverseListener(ProfibusHelper.getNETL());
        _failButton.setText("Fail Save");
        _failButton.setEnabled(false);
        _failButton.setData(false);
        _failButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                change(_failButton);
            }

            public void widgetSelected(final SelectionEvent e) {
                change(_failButton);
            }
        });
        _watchDogButton = new Button(operationModeGroup, SWT.CHECK);
        _watchDogButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        _watchDogButton.addTraverseListener(ProfibusHelper.getNETL());
        _watchDogButton.setText("Watchdog Time");
        _watchDogButton.setSelection(true);
        _watchDogButton.setData(true);
        _watchDogText = ProfibusHelper.getTextField(operationModeGroup,
                                                    _watchDogButton.getSelection(),
                                                    Integer.toString(_slave.getWdFact1()),
                                                    Ranges.TTR,
                                                    ProfibusHelper.VL_TYP_U32);
        _watchDogText.addModifyListener(getMLSB());
        Label timeLabel = new Label(operationModeGroup, SWT.NONE);
        timeLabel.setText("ms");
        _watchDogButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                change();
            }

            public void widgetSelected(final SelectionEvent e) {
                change();
            }

            private void change() {
                SlaveEditor.this.change(_watchDogButton);
                _watchDogText.setEnabled(_watchDogButton.getSelection());
            }
        });

        makeCurrentUserParamData(comp);

        makeUserPRMMode(comp);

    }

    /**
     * @param comp
     */
    private void makeUserPRMMode(final Composite comp) {
        Group userPrmData = new Group(comp, SWT.V_SCROLL);
        userPrmData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
        userPrmData.setLayout(new GridLayout(2, false));
        userPrmData.setTabList(new Control[0]);
        userPrmData.setText("User PRM Mode");
        _userPrmDataList = new TableViewer(userPrmData, SWT.BORDER | SWT.V_SCROLL);

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

        // Groups
        _groupsRadioButtons = new Group(comp, SWT.NONE);
        _groupsRadioButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        _groupsRadioButtons.setLayout(new GridLayout(4, true));
        _groupsRadioButtons.setText("Groups");
        _groupIdentStored = _slave.getGroupIdent();
        if ( (_groupIdentStored < 0) || (_groupIdentStored > 7)) {
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

                public void widgetDefaultSelected(final SelectionEvent e) {
                    check();
                }

                public void widgetSelected(final SelectionEvent e) {
                    check();
                }

                private void check() {
                    _groupIdent = Short.parseShort(b.getText());
                    _groupIdent--;
                    setSavebuttonEnabled("groupButton" + _groupsRadioButtons.hashCode(),
                                         _groupIdent != _groupIdentStored);
                }

            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public final boolean fill(final GSDFileDBO gsdFile) {
        /*
         * Read GSD-File
         */
        if (gsdFile == null) {
            return false;
        } else if (gsdFile.equals(_gsdFile)) {
            return true;
        }

        _gsdFile = gsdFile;
        GsdSlaveModel slaveModel = GsdFactory.makeGsdSlave(_gsdFile);

        // setGSDData
        HashMap<Integer, GsdModuleModel> moduleList = GSD2Module.parse(_gsdFile, slaveModel);
        slaveModel.setGsdModuleList(moduleList);
        _slave.setGSDSlaveData(slaveModel);

        /*
         * Head
         */
        getHeaderField(HeaderFields.VERSION).setText(slaveModel.getGsdRevision() + "");

        /*
         * Basic - Slave Discription (read only)
         */
        _vendorText.setText(slaveModel.getVendorName());
        _iDNo.setText(String.format("0x%04X", slaveModel.getIdentNumber()));
        _revisionsText.setText(slaveModel.getRevision());

        /*
         * Basic - Inputs / Outputs (read only)
         */

        /*
         * Set all GSD-File Data to Slave.
         */
        _slave.setMinTsdr(_slave.getMinTsdr());
        _slave.setModelName(slaveModel.getModelName());
        _slave.setPrmUserData(slaveModel.getUserPrmData());
        _slave.setProfibusPNoID(slaveModel.getIdentNumber());
        _slave.setRevision(slaveModel.getRevision());

        /*
         * Basic - DP / FDL Access
         */

        /*
         * Modules
         */
        _maxSize = slaveModel.getMaxModule();
        setSlots();

        /*
         * Settings - Operation Mode
         */
        /*
         * Settings - Groups
         */
        /*
         * Settings - USER PRM MODE
         */

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
    public final GSDFileDBO getGSDFile() {
        return _slave.getGSDFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Name
        _slave.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());

        // _slave.moveSortIndex((short) getIndexSpinner().getSelection());
        Short stationAddress = (Short) ((StructuredSelection) _indexCombo.getSelection())
                .getFirstElement();
        _slave.setSortIndexNonHibernate(stationAddress);
        _slave.setFdlAddress(stationAddress);
        _indexCombo.getCombo().setData(_indexCombo.getCombo().getSelectionIndex());
        short minTsdr = 0;
        try {
            minTsdr = Short.parseShort(_minStationDelayText.getText());
        } catch (NumberFormatException e) {
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
    }

    /**
    *
    */
   private void saveUserPrmData() {
       TreeMap<String, ExtUserPrmDataConst> extUserPrmDataConst = _slave.getGSDSlaveData().getExtUserPrmDataConst();
       List<ExtUserPrmDataRef> extUserPrmDataRefMap = _slave.getGSDSlaveData().getExtUserPrmDataRefMap();
       if(extUserPrmDataRefMap.size()==_prmTextCV.size()) {
           for (int i = 0; i < extUserPrmDataRefMap.size(); i++) {
               ExtUserPrmDataRef ref = extUserPrmDataRefMap.get(i);
               Object prmTextObject = _prmTextCV.get(i);
               if (prmTextObject instanceof ComboViewer) {
                   ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                   handleComboViewer(extUserPrmDataConst, prmTextCV, ref.getValue());
               } else if (prmTextObject instanceof Text) {
                   Text prmText = (Text) prmTextObject;
                   extUserPrmDataConst = handleText(extUserPrmDataConst, prmText, ref.getValue());
               }
           }

       }else {
           //TODO: throw extUserPrmDataRefMap und prmTextCV passen nicht zu sammen
       }
//       GsdModuleModel mod = (GsdModuleModel) ((StructuredSelection) _moduleTypList.getSelection())
//               .getFirstElement();
//       if (mod != null) {
////           _slave.setModuleNumber(mod.getModuleNumber());
////           _moduleTypList.getTable().setData(mod.getModuleNumber());
//
////           String[] extUserPrmDataConst = _module.getGsdModuleModel().getExtUserPrmDataConst()
////                   .split(",");
//
//           String extUserPrmDataConst = _slave.getPrmUserData();
//           for (Object prmTextObject : _prmTextCV) {
//               if (prmTextObject instanceof ComboViewer) {
//                   ComboViewer prmTextCV = (ComboViewer) prmTextObject;
////                   handleComboViewer(extUserPrmDataConst, prmTextCV);
//               } else if (prmTextObject instanceof Text) {
//                   Text prmText = (Text) prmTextObject;
////                   extUserPrmDataConst = handleText(extUserPrmDataConst, prmText);
//               }
//           }
////           _module.setConfigurationData(Arrays.toString(extUserPrmDataConst)
////                   .replaceAll("[\\[\\]]", ""));
//       }
   }

    /**
     * @param extUserPrmDataConst
     * @param prmText
     * @return
     */
    @Nonnull
    private TreeMap<String, ExtUserPrmDataConst> handleText(@Nonnull final TreeMap<String,ExtUserPrmDataConst> extUserPrmDataConst,@Nonnull final Text prmText) {
        if (!prmText.isDisposed()) {
            String value = (String) prmText.getData();
            if (value != null) {
                prmText.setText(value);
                int val = Integer.parseInt(value);
//                return new String[] {String.format("%1$#04x", val) };
            }
        }
        return extUserPrmDataConst;
    }

    /**
     * @param extUserPrmDataConst
     * @param byteIndexString
     * @param prmTextObject
     */
    private void handleComboViewer(final TreeMap<String,ExtUserPrmDataConst> extUserPrmDataConst, final ComboViewer prmTextCV, final String byteIndexString) {
        if (!prmTextCV.getCombo().isDisposed()) {
            int byteIndex = ProfibusConfigXMLGenerator.getInt(byteIndexString);
            ExtUserPrmData input = (ExtUserPrmData) prmTextCV.getInput();
            StructuredSelection selection = (StructuredSelection) prmTextCV
                    .getSelection();
            Integer bitValue = ((PrmText) selection.getFirstElement()).getValue();
            String newValue = setValue2BitMask(input, bitValue, _slave.getPrmUserDataList().get(byteIndex));
            _slave.setPrmUserDataByte(byteIndex, newValue);
            Integer indexOf = prmTextCV.getCombo().indexOf(selection.getFirstElement()
                    .toString());
            prmTextCV.getCombo().setData(indexOf);
        }
    }

    /**
     * Change the a value on the Bit places, that is given from the input, to the bitValue.
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
                                    @Nonnull final Integer bitValue,
                                    @Nonnull final String value) {
        int val = ProfibusConfigXMLGenerator.getInt(value);
        int minBit = ranges.getMinBit();
        int maxBit = ranges.getMaxBit();
        if (maxBit < minBit) {
            minBit = ranges.getMaxBit();
            maxBit = ranges.getMinBit();
        }
        int mask = ~ ((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
        val = val & mask;
        val = val | (bitValue << minBit);
        return String.format("%1$#04x", val);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#cancel()
     */
    @Override
    public void cancel() {
        super.cancel();
        // getIndexSpinner().setSelection((Short) getIndexSpinner().getData());
        if (_indexCombo != null) {
            _indexCombo.getCombo().select((Integer) _indexCombo.getCombo().getData());
            getNameWidget().setText((String) getNameWidget().getData());
            _stationAddressActiveCButton.setSelection((Boolean) _stationAddressActiveCButton
                    .getData());
            _minStationDelayText.setText((String) _minStationDelayText.getData());
            _syncButton.setSelection((Boolean) _syncButton.getData());
            _failButton.setSelection((Boolean) _failButton.getData());
            _freezeButton.setSelection((Boolean) _freezeButton.getData());
            _watchDogButton.setSelection((Boolean) _watchDogButton.getData());
            _watchDogText.setEnabled(_watchDogButton.getSelection());
            _watchDogText.setText((String) _watchDogText.getData());
            _groupIdent = _groupIdentStored;
            for (Control control : _groupsRadioButtons.getChildren()) {
               if (control instanceof Button) {
                    Button button = (Button) control;
                    button
                            .setSelection(Short.parseShort(button.getText()) == _groupIdentStored + 1);
                }
            }

            if (_slave != null) {
                if (_slave.getGSDFile() != null) {
                    fill(_slave.getGSDFile());
                } else {
                    getHeaderField(HeaderFields.VERSION).setText("");
                    _vendorText.setText("");
                    getNameWidget().setText("");
                    _revisionsText.setText("");
                }
            } else {
                _gsdFile = null;
                fill(_gsdFile);
            }
        }
    }

    private void change(final Button button) {
        setSavebuttonEnabled("Button:" + button.hashCode(), (Boolean) button.getData() != button
                .getSelection());
    }

    /**
     *
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     */
    private void makeCurrentUserParamData(final Composite topGroup) {
        if (_currentUserParamDataGroup != null) {
            _currentUserParamDataGroup.dispose();
        }
        // Current User Param Data Group
        _currentUserParamDataGroup = new Group(topGroup, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
        _currentUserParamDataGroup.setLayoutData(gd);
        _currentUserParamDataGroup.setLayout(new FillLayout());
        _currentUserParamDataGroup.setText("Current User Param Data:");
        final ScrolledComposite scrollComposite = new ScrolledComposite(_currentUserParamDataGroup,
                                                                        SWT.V_SCROLL);
        final Composite currentUserParamDataComposite = new Composite(scrollComposite, SWT.NONE);
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
                scrollComposite.setMinSize(scrollComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrollComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nonnull final ControlEvent e) {
                Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(currentUserParamDataComposite.computeSize(r.width,
                                                                                     SWT.DEFAULT));
            }
        });

		// Current User Param Data
		GsdSlaveModel gsdSlaveModel = _slave.getGSDSlaveData();
		if (gsdSlaveModel != null) {
			List<ExtUserPrmDataRef> extUserPrmDataRefMap = gsdSlaveModel.getExtUserPrmDataRefMap();
			if (extUserPrmDataRefMap != null) {
				for (ExtUserPrmDataRef extUserPrmDataRef : extUserPrmDataRefMap) {
					int val = 0;
					String byteIndex = extUserPrmDataRef.getIndex();
					ExtUserPrmData extUserPrmData = gsdSlaveModel
							.getExtUserPrmData(extUserPrmDataRef.getValue());
					makeCurrentUserParamDataItem(currentUserParamDataComposite,
							extUserPrmData, val, byteIndex);
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
    private void makeCurrentUserParamDataItem(final Composite currentUserParamDataGroup,
                                          final ExtUserPrmData extUserPrmData,
                                          final Integer value, final String byteIndex) {
        HashMap<Integer, PrmText> prmTextMap = null;

        Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);

        if (extUserPrmData != null) {
            text.setText(extUserPrmData.getText() + ":");
            prmTextMap = extUserPrmData.getPrmText();
            if ( (extUserPrmData.getPrmText() == null)
                    && (extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10)) {
                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
            } else {
                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup,
                                               value,
                                               extUserPrmData,
                                               prmTextMap, byteIndex));
            }
        }
        new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
    }

    /**
     *
     * @param currentUserParamDataGroup
     * @param value
     * @param extUserPrmData
     * @return
     */
    private Text makeTextField(final Composite currentUserParamDataGroup,
                               final Integer value,
                               final ExtUserPrmData extUserPrmData) {
        Integer localValue = value;
        Text prmText = new Text(currentUserParamDataGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
        Formatter f = new Formatter();
        f.format("Min: %d, Min: %d", extUserPrmData.getMinValue(), extUserPrmData.getMaxValue());
        prmText.setToolTipText(f.toString());
        prmText.setTextLimit(Integer.toString(extUserPrmData.getMaxValue()).length());

        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmText.setText(localValue.toString());
        prmText.setData(localValue.toString());
        prmText.addModifyListener(getMLSB());
        prmText.addVerifyListener(new VerifyListener() {

            public void verifyText(final VerifyEvent e) {
                if (e.text.matches("^\\D+$")) {
                    e.doit = false;
                }
            }

        });
        return prmText;
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
    private ComboViewer makeComboViewer(final Composite parent,
                                        final Integer value,
                                        final ExtUserPrmData extUserPrmData,
                                        final HashMap<Integer, PrmText> prmTextMap, final String byteIndex) {
        final int index = ProfibusConfigXMLGenerator.getInt(byteIndex);
        Integer localValue = value;
        ComboViewer prmTextCV = new ComboViewer(parent);
        RowData data = new RowData();
        data.exclude = false;
        prmTextCV.getCombo().setLayoutData(data);
        prmTextCV.setLabelProvider(new LabelProvider());
        prmTextCV.setContentProvider(new IStructuredContentProvider() {

            public Object[] getElements(final Object inputElement) {
                if (inputElement instanceof ExtUserPrmData) {
                    ExtUserPrmData extUserPrmData = (ExtUserPrmData) inputElement;
                    HashMap<Integer, PrmText> prmText = extUserPrmData.getPrmText();
                    if (prmText == null) {
                        PrmText[] prmTextArray = new PrmText[extUserPrmData.getMaxValue()
                                - extUserPrmData.getMinValue() + 1];
                        for (int i = extUserPrmData.getMinValue(); i <= extUserPrmData
                                .getMaxValue(); i++) {
                            prmTextArray[i] = new PrmText(Integer.toString(i), i);
                        }
                        return prmTextArray;
                    }
                    return prmText.values().toArray();
                }
                return null;
            }

            public void dispose() {
            }

            public void inputChanged(final Viewer viewer,
                                     final Object oldInput,
                                     final Object newInput) {
            }
        });
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new ViewerSorter() {

            @Override
            public int compare(final Viewer viewer, final Object e1, final Object e2) {
                if ( (e1 instanceof PrmText) && (e2 instanceof PrmText)) {
                    PrmText eUPD1 = (PrmText) e1;
                    PrmText eUPD2 = (PrmText) e2;
                    return eUPD1.getValue() - eUPD2.getValue();
                }
                return super.compare(viewer, e1, e2);
            }

        });

        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmTextCV.setInput(extUserPrmData);
        if (prmTextMap != null) {
            PrmText prmText = prmTextMap.get(localValue);
            if (prmText != null) {
                prmTextCV.setSelection(new StructuredSelection(prmTextMap.get(localValue)));
            } else {
                prmTextCV.getCombo().select(0);
            }
        } else {
            prmTextCV.getCombo().select(localValue);
        }
        prmTextCV.getCombo().setData(prmTextCV.getCombo().getSelectionIndex());
        setModify(prmTextCV);
        return prmTextCV;
    }

    /**
     *
     * @param prmTextCV
     */
    private void setModify(final ComboViewer prmTextCV) {
        //        PrmText prmText = (PrmText) ((StructuredSelection) prmTextCV.getSelection())
        //                .getFirstElement();
        //        ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
        //        String index = extUserPrmData.getIndex();
        //        GsdModuleModel gsdModule = _slave.getGsdModuleModel();
        //        int bytePos = Integer.parseInt(gsdModule.getExtUserPrmDataRef(index));
        //        int bitMin = extUserPrmData.getMinBit();
        //        int bitMax = extUserPrmData.getMaxBit();
        //
        //        int val = 0;
        //        if (prmText != null) {
        //            val = prmText.getValue();
        //        }
        //        gsdModule.addModify(bytePos, bitMin, bitMax, val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }
}
