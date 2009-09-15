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

import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototype;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 26.07.2007
 */
public class ModuleConfigComposite extends NodeConfig {

    /**
     * 
     * If the selection changes the old Channels will be deleted and the new
     * Channel created for the new Module. Have the Module no Prototype the
     * Dialog to generate Prototype is opened.
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 17.04.2009
     */
    private final class ISelectionChangedListenerForModuleTypeList implements
            ISelectionChangedListener {
        private final Group _topGroup;

        private ISelectionChangedListenerForModuleTypeList(Group topGroup) {
            _topGroup = topGroup;
        }

        public void selectionChanged(final SelectionChangedEvent event) {
            // get the Selected Module.
            GsdModuleModel selectedModule = (GsdModuleModel) ((StructuredSelection) _moduleTypList
                    .getSelection()).getFirstElement();

            // if the same Module do nothing.
            if (selectedModule == null) {
                return;
            } else if (_module == null
                    || (_module.getGSDModule() != null && _module.getGSDModule().getModuleId() == selectedModule
                            .getModuleNumber())) {
                return;
            }

            int selectedModuleNo = selectedModule.getModuleNumber();
            int savedModuleNo = (Integer) _moduleTypList.getTable().getData();
            boolean b = savedModuleNo != selectedModuleNo;

            _module.removeAllChild();
            _module.setModuleNumber(selectedModuleNo);
            GSDModule gsdModule = _module.getGSDModule();

            // Unknown Module (--> Config the Epics Part)
            if (gsdModule == null) {
                gsdModule = openChannelConfigDialog(selectedModule, null);
                if (gsdModule == null) {
                    return;
                }
                // TODO: Prüfen ob das nicht mit im openChannelConfigDialog
                // erledigt werden kann.
                gsdModule.setModuleId(selectedModuleNo);
                _module.getGSDFile().addGSDModule(gsdModule);
                try {
                    gsdModule.save();
                } catch (PersistenceException e) {
                    openErrorDialog(e);
                }
            }
            getNameWidget().setText(gsdModule.getName());
            setSavebuttonEnabled("ModuleTyp", b);

            // Generate Input Channel
            TreeSet<ModuleChannelPrototype> moduleChannelPrototypes = gsdModule
                    .getModuleChannelPrototypeNH();
            if (moduleChannelPrototypes != null) {
                ModuleChannelPrototype[] array = moduleChannelPrototypes
                        .toArray(new ModuleChannelPrototype[0]);
                for (int sortIndex = 0; sortIndex < array.length; sortIndex++) {
                    ModuleChannelPrototype prototype = array[sortIndex];
                    makeNewChannel(prototype, sortIndex);
                }
            }
            try {
                _module.localUpdate();
                _module.localSave();
            } catch (PersistenceException e) {
                // TODO Besseres Händling des Speicherns.
                e.printStackTrace();
            }
            getProfiBusTreeView().refresh(_module.getParent());
            makeCurrentUserParamData(_topGroup);
        }

        private void makeNewChannel(final ModuleChannelPrototype channelPrototype, int sortIndex) {
            if (channelPrototype.isStructure()) {
                makeStructChannel(channelPrototype, sortIndex);
            } else {
                makeNewPureChannel(channelPrototype, sortIndex);
            }
        }

        private void makeStructChannel(ModuleChannelPrototype channelPrototype, int sortIndex) {
            channelPrototype.getOffset();
            Date now = new Date();
            String createdBy = "Unknown";
            User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                createdBy = user.getUsername();
            }

            ChannelStructure channelStructure = ChannelStructure.makeChannelStructure(_module,
                    channelPrototype.isInput(), channelPrototype.getType(), channelPrototype
                            .getName());
            channelStructure.setName(channelPrototype.getName());
            channelStructure.setStructureType(channelPrototype.getType());
            channelStructure.setCreatedOn(now);
            channelStructure.setUpdatedOn(now);
            channelStructure.setCreatedBy(createdBy);
            channelStructure.setUpdatedBy(createdBy);
            channelStructure.moveSortIndex((short) sortIndex);
            try {
                channelPrototype.save();
            } catch (PersistenceException e) {
                // TODO Bessers vorghehen beim Speichern suchen. Oder ist das
                // überhaupt noch nötig?
                e.printStackTrace();
            }
        }

        private void makeNewPureChannel(ModuleChannelPrototype channelPrototype, int sortIndex) {
            Date now = new Date();
            String createdBy = "Unknown";
            User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                createdBy = user.getUsername();
            }
            boolean isDigi = channelPrototype.getType().getBitSize() == 1;
            ChannelStructure cs = ChannelStructure.makeSimpleChannel(_module, channelPrototype
                    .getName(), channelPrototype.isInput(), isDigi);
            cs.moveSortIndex((short) sortIndex);
            Channel channel = cs.getFirstChannel();
            channel.setCreatedOn(now);
            channel.setUpdatedOn(now);
            channel.setCreatedBy(createdBy);
            channel.setUpdatedBy(createdBy);
            channel.setChannelTypeNonHibernate(channelPrototype.getType());
            channel.setStatusAddressOffset(channelPrototype.getShift());
            channel.moveSortIndex((short) sortIndex);
        }
    }


    /**
     * This class provides the content for the table.
     */
    public class ComboContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public final Object[] getElements(final Object arg0) {
            if (arg0 instanceof HashMap) {
                HashMap<Integer, GsdModuleModel> map = (HashMap<Integer, GsdModuleModel>) arg0;
                return map.values().toArray(new GsdModuleModel[0]);
            }
            return null;
        }

        /**
         * Disposes any resources.
         */
        public final void dispose() {
            // We don't create any resources, so we don't dispose any
        }

        /**
         * Called when the input changes.
         * 
         * @param arg0
         *            the parent viewer
         * @param arg1
         *            the old input
         * @param arg2
         *            the new input
         */
        public final void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
        }

    }

    /**
     * The Module Object.
     */
    private Module _module;

    /**
     * The List to choose the type of module.
     */
    private TableViewer _moduleTypList;

    private ArrayList<Object> _prmTextCV = new ArrayList<Object>();

    private Group _currentUserParamDataGroup;

    /**
     * @param parent
     *            Parent Composite.
     * @param profiBusTreeView
     *            The IO Config TreeViewer.
     * 
     * @param module
     *            the Profibus Module to Configer.
     */
    public ModuleConfigComposite(final Composite parent, final ProfiBusTreeView profiBusTreeView,
            final Module module) {
        super(parent, profiBusTreeView, "Profibus Module Configuration", module, module == null);
        _module = module;

        if (_module == null) {
            newNode();
            _module.setModuleNumber(0);
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        String[] heads = { "Module" };
        moduels(heads[0]);
        documents();
        getTabFolder().pack();
    }

    /**
     * @param head
     *            the tabItemName
     * 
     */
    private void moduels(final String head) {
        final Composite comp = getNewTabItem(head, 2);
        comp.setLayout(new GridLayout(2, false));

        /*
         * Name
         */
        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        gName.setLayout(new GridLayout(3, false));

        setNameWidget(new Text(gName, SWT.BORDER | SWT.SINGLE));
        setText(getNameWidget(), _module.getName(), 255);
        getNameWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _module, getMLSB(), "Sort Index",
                getProfiBusTreeView()));

        /*
         * Top Composite.
         */
        final Group topGroup = new Group(comp, SWT.NONE);
        topGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        topGroup.setLayout(new GridLayout(4, false));
        
        makeDescGroup(comp,1);
        
        new Label(topGroup, SWT.NONE).setText("Filter: ");
        
        
        Composite filterComposite = new Composite(topGroup, SWT.NONE);
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        filterComposite.setLayout(layout);

        final Text filter = new Text(filterComposite, SWT.SINGLE | SWT.BORDER);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        // filter.setLayoutData(GridDataFactory.fillDefaults().create());
        filter.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                _moduleTypList.refresh();
            }

        });
        final Button filterButton = new Button(filterComposite, SWT.CHECK);
        filterButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        filterButton.setText("Only have prototype");
        filterButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                _moduleTypList.refresh();
            }

            public void widgetSelected(SelectionEvent e) {
                _moduleTypList.refresh();
            }

        });

        Button epicsEditButton = new Button(topGroup, SWT.PUSH);
        epicsEditButton.setText("Edit Prototype");
        epicsEditButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                action();
            }

            public void widgetSelected(final SelectionEvent e) {
                action();
            }

            private void action() {
                GsdModuleModel firstElement = (GsdModuleModel) ((StructuredSelection) _moduleTypList
                        .getSelection()).getFirstElement();
                GSDModule gsdModule = _module.getGSDModule();
                gsdModule = openChannelConfigDialog(firstElement, gsdModule);
                if (gsdModule != null) {
                    _module.getGSDFile().addGSDModule(gsdModule);
                    getProfiBusTreeView().refresh(_module);
                }
            }
        });

        new Label(topGroup, SWT.NONE).setText("Module Type: ");

        _moduleTypList = new TableViewer(topGroup, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        _moduleTypList.getTable().setLayoutData(
                new GridData(SWT.CENTER, SWT.FILL, false, true, 2, 3));
        _moduleTypList.setContentProvider(new ComboContentProvider());
        _moduleTypList.setLabelProvider(new ModuleListLabelProvider(_moduleTypList.getTable(),getGSDFile()));
        _moduleTypList.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer, final Object parentElement,
                    final Object element) {
                if (element instanceof GsdModuleModel) {
                    GsdModuleModel gsdModuleModel = (GsdModuleModel) element;
                    if (filter.getText() == null || filter.getText().length() < 1) {
                        return true;
                    }
                    String filterString = ".*" + filter.getText().replaceAll("\\*", ".*") + ".*";
                    return gsdModuleModel.toString().matches(filterString);
                }
                return false;
            }

        });

        _moduleTypList.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer, final Object parentElement,
                    final Object element) {
                if (filterButton.getSelection()) {
                    if (element instanceof GsdModuleModel) {
                        GsdModuleModel gmm = (GsdModuleModel) element;
                        int selectedModuleNo = gmm.getModuleNumber();
                        GSDModule module = getGSDFile().getGSDModule(selectedModuleNo);
                        return module != null;
                        // GsdModuleModel gsdModuleModel = (GsdModuleModel)
                        // element;
                        // if (filter.getText() == null ||
                        // filter.getText().length() < 1) {
                        // return true;
                        // }
                        // String filterString = ".*" +
                        // filter.getText().replaceAll("\\*", ".*") +
                        // ".*";
                        // return
                        // gsdModuleModel.toString().matches(filterString);
                    }
                }
                return true;
            }

        });

        _moduleTypList.setSorter(new ViewerSorter() {

            @Override
            public int compare(final Viewer viewer, final Object e1, final Object e2) {
                if (e1 instanceof GsdModuleModel && e2 instanceof GsdModuleModel) {
                    GsdModuleModel eUPD1 = (GsdModuleModel) e1;
                    GsdModuleModel eUPD2 = (GsdModuleModel) e2;
                    return eUPD1.getModuleNumber() - eUPD2.getModuleNumber();
                }
                return super.compare(viewer, e1, e2);
            }

        });

        makeCurrentUserParamData(topGroup);
        _moduleTypList.addSelectionChangedListener(new ISelectionChangedListenerForModuleTypeList(
                topGroup));

        Slave slave = _module.getSlave();
        if (getGSDFile() != null) {
            HashMap<Integer, GsdModuleModel> gsdModuleList = slave.getGSDSlaveData()
                    .getGsdModuleList();
            _moduleTypList.setInput(gsdModuleList);
            _moduleTypList.getTable().setData(_module.getModuleNumber());
            GsdModuleModel gsdModuleModel2 = gsdModuleList.get(_module.getModuleNumber());
            if (gsdModuleModel2 != null) {
                _moduleTypList.setSelection(new StructuredSelection(gsdModuleModel2), true);
            } else {
                _moduleTypList.getTable().select(0);
            }
        } else {
            _moduleTypList.getTable().select(0);
        }
        _moduleTypList.getTable().showSelection();
//        getIndexSpinner().setSelection(getNode().getSortIndex());
        
    }

    /**
     * 
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     */
    private void makeCurrentUserParamData(final Group topGroup) {
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
            public void controlResized(final ControlEvent e) {
                Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(scrollComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrollComposite.addControlListener(new ControlAdapter() {
            public void controlResized(final ControlEvent e) {
                Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(currentUserParamDataComposite.computeSize(r.width,
                        SWT.DEFAULT));
            }
        });
        // Current User Param Data
        GsdModuleModel gsdModuleModel = _module.getGsdModuleModel();
        String[] values = {};
        // if (_module.getConfigurationData() != null) {
        if (_module.getGsdModuleModel().getValue() != null) {
            values = _module.getGsdModuleModel().getValue().split(",");
        }
        if (gsdModuleModel != null) {
            if (gsdModuleModel.getAllExtUserPrmDataRef().size() * 2 != values.length
                    || values.length % 2 != 0) {
                for (ExtUserPrmData extUserPrmData : gsdModuleModel.getAllExtUserPrmDataRef()) {
                    makecurrentUserParamData(currentUserParamDataComposite, extUserPrmData, null);
                }
            } else {
                for (int i = 0; i < values.length; i++) {
                    ExtUserPrmData extUserPrmData = gsdModuleModel.getExtUserPrmData(values[i]);
                    i++;
                    int val = Integer.parseInt(values[i]);
                    makecurrentUserParamData(currentUserParamDataComposite, extUserPrmData, val);
                }
            }
        }
        topGroup.layout();
    }

    /**
     * Store all Data in {@link Module} DB object.
     */
    public final void store() {
        super.store();
        // Module
        _module.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());

        // _module.moveSortIndex((short) _indexSpinner.getSelection());
        getIndexSpinner().setData((short) getIndexSpinner().getSelection());

        updateChannels();
        
        GsdModuleModel mod = (GsdModuleModel) ((StructuredSelection) _moduleTypList.getSelection())
                .getFirstElement();
        if (mod != null) {
            _module.setModuleNumber(mod.getModuleNumber());
            _moduleTypList.getTable().setData(mod.getModuleNumber());
            _module.setConfigurationData(mod.getValue());
        }

        // Document
        _module.setDocuments(getDocumentationManageView().getDocuments());

        for (Object prmTextObject : _prmTextCV) {
            if (prmTextObject instanceof ComboViewer) {
                ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                if (!prmTextCV.getCombo().isDisposed()) {
                    ExtUserPrmData input = (ExtUserPrmData) prmTextCV.getInput();
                    StructuredSelection selection = (StructuredSelection) prmTextCV.getSelection();
                    String[] extUserPrmDataConst = _module.getGsdModuleModel()
                            .getExtUserPrmDataConst().split(",");
                    String extUserPrmDataRef = _module.getGsdModuleModel().getExtUserPrmDataRef(
                            input.getIndex());

                    Integer value = ((PrmText) selection.getFirstElement()).getValue();
                    if (value < extUserPrmDataConst.length) {
                        int index = ProfibusConfigXMLGenerator.getInt(extUserPrmDataRef);
                        int val = ProfibusConfigXMLGenerator.getInt(extUserPrmDataConst[index]);
                        int max = (2 ^ (input.getMaxBit() - input.getMaxBit()) + 1) << input
                                .getMaxBit();
                        value = value << input.getMaxBit();
                        int mask = ~max;
                        val = val & mask;
                        val = val | value;
                        extUserPrmDataConst[index] = String.format("%1$#04x", val);
                        // TODO: Set the extUserPrmDataConst
                        // TODO: Store the Combo selection.
                        // userPrmDataIndex1,selectionIndex1;userPrmDataIndex2,selectionIndex2#0x21,0x23,0x00
                        // _module.set
                    }

                    Integer indexOf = prmTextCV.getCombo().indexOf(
                            selection.getFirstElement().toString());
                    prmTextCV.getCombo().setData(indexOf);
                }
            } else if (prmTextObject instanceof Text) {
                Text prmText = (Text) prmTextObject;
                if (!prmText.isDisposed()) {
                    String value = (String) prmText.getData();
                    if (value != null) {
                        prmText.setText(value);
                    }
                }
            }
        }
        save();
    }

    /**
     * 
     */
    private void updateChannels() {
        Set<ChannelStructure> channelStructs = _module.getChannelStructs();
        for (ChannelStructure channelStructure : channelStructs) {
            Set<Channel> channels = channelStructure.getChannels();
            for (Channel channel : channels) {
                channel.assembleEpicsAddressString();
            }
        }
    }

    /**
     * Cancel all change value.
     */
    @Override
    public final void cancel() {
        super.cancel();
        // Module
        getNameWidget().setText((String) getNameWidget().getData());
        getIndexSpinner().setSelection((Short) getIndexSpinner().getData());

        try {
            GsdModuleModel gsdModuleModel = _module.getSlave().getGSDSlaveData().getGsdModuleList()
                    .get((Integer) _moduleTypList.getTable().getData());
            if (gsdModuleModel != null) {
                _moduleTypList.setSelection(new StructuredSelection(gsdModuleModel), true);
            }
        } catch (NullPointerException e) {
            _moduleTypList.getTable().select(0);
        }

        for (Object prmTextObject : _prmTextCV) {
            if (prmTextObject instanceof ComboViewer) {
                ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                if (!prmTextCV.getCombo().isDisposed()) {
                    Integer index = (Integer) prmTextCV.getCombo().getData();
                    if (index != null) {
                        prmTextCV.getCombo().select(index);
                    }
                }
            } else if (prmTextObject instanceof Text) {
                Text prmText = (Text) prmTextObject;
                if (!prmText.isDisposed()) {
                    String value = (String) prmText.getData();
                    if (value != null) {
                        prmText.setText(value);
                    }
                }
            }
        }
        save();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean fill(final GSDFile gsdFile) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public final GSDFile getGSDFile() {
        return _module.getSlave().getGSDFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node getNode() {
        if (_module == null) {
            StructuredSelection selection = (StructuredSelection) getProfiBusTreeView().getTreeViewer()
                    .getSelection();
            if (selection.getFirstElement() instanceof Slave) {
                Slave slave = (Slave) selection.getFirstElement();
                _module = new Module(slave);
            }
        }
        return _module;
    }

    /**
     * 
     * @param currentUserParamDataGroup
     * @param extUserPrmData
     * @param value
     */
    private void makecurrentUserParamData(final Composite currentUserParamDataGroup,
            final ExtUserPrmData extUserPrmData, final Integer value) {
        HashMap<Integer, PrmText> prmTextMap = null;

        Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);

        if (extUserPrmData != null) {
            text.setText(extUserPrmData.getText() + ":");
            prmTextMap = extUserPrmData.getPrmText();
            if (extUserPrmData.getPrmText() == null
                    && extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10) {
                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
            } else {
                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup, value, extUserPrmData,
                        prmTextMap));
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
    private Text makeTextField(final Composite currentUserParamDataGroup, final Integer value,
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
     * @return a ComboView for are currentUserParamData Property
     */
    private ComboViewer makeComboViewer(final Composite parent, final Integer value,
            final ExtUserPrmData extUserPrmData, final HashMap<Integer, PrmText> prmTextMap) {
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

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new ViewerSorter() {

            @Override
            public int compare(final Viewer viewer, final Object e1, final Object e2) {
                if (e1 instanceof PrmText && e2 instanceof PrmText) {
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
        PrmText prmText = (PrmText) ((StructuredSelection) prmTextCV.getSelection())
                .getFirstElement();
        ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
        String index = extUserPrmData.getIndex();
        GsdModuleModel gsdModule = _module.getGsdModuleModel();
        int bytePos = Integer.parseInt(gsdModule.getExtUserPrmDataRef(index));
        int bitMin = extUserPrmData.getMinBit();
        int bitMax = extUserPrmData.getMaxBit();

        int val = 0;
        if (prmText != null) {
            val = prmText.getValue();
        }
        gsdModule.addModify(bytePos, bitMin, bitMax, val);
    }

    /**
     * Open a Config-Dialog for {@link GSDModule} and create and store.
     * 
     * @param model
     *            The {@link GsdModuleModel} Module Module from the GSD File.
     * @param gsdModule
     *            the {@link GSDModule} or null for a new one to configure .
     * @return the new or modified GSDModule or null when canceled.
     */
    private GSDModule openChannelConfigDialog(final GsdModuleModel model, GSDModule gsdModule) {
        if (gsdModule == null) {
            gsdModule = new GSDModule(model.getName());
        }
        gsdModule.setModuleId(_module.getModuleNumber());
        String createdBy = "UNKNOWN";
        User currentUser = SecurityFacade.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUsername() != null) {
            createdBy = currentUser.getUsername();
        }
        gsdModule.setCreatedBy(createdBy);
        gsdModule.setUpdatedBy(createdBy);
        Date date = new Date();
        gsdModule.setCreatedOn(date);
        gsdModule.setUpdatedOn(date);
        ChannelConfigDialog channelConfigDialog = new ChannelConfigDialog(Display.getCurrent()
                .getActiveShell(), model, gsdModule, _module);
        if (channelConfigDialog.open() == ChannelConfigDialog.OK) {
            gsdModule.setConfigurationData(channelConfigDialog.getConfigurationData());
            String parameter = channelConfigDialog.getParameter();
            if (parameter.length() > 254) {
                parameter = parameter.substring(0, 254);
            }
            gsdModule.setParameter(parameter);
            try {
                gsdModule.save();
                return gsdModule;
            } catch (PersistenceException e) {
                openErrorDialog(e);
                return null;
            }
        }
        gsdModule = null;
        return null;
    }

    /**
     * Have no Name Dialog. {@inheritDoc}
     */
    @Override
    protected boolean newNode() {
        User user = SecurityFacade.getInstance().getCurrentUser();
        String name = "Unknown";
        if (user != null) {
            name = user.getUsername();
        }
        // if (obj == null) {
        // obj = _profiBusTreeView.getInvisibleRoot();
        // }

        getNode().setCreatedBy(name);
        getNode().setCreatedOn(new Date());
        getNode().setVersion(-2);

        Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection())
                .getFirstElement();

        if (getNode() instanceof Facility || obj == null) {
            getProfiBusTreeView().getTreeViewer().setInput(getNode());
            // TODO neue facility erstellen und speichern..
        } else if (getNode() instanceof Module) {
            Node nodeParent = (Node) obj;
            getNode()
                    .moveSortIndex(nodeParent.getfirstFreeStationAddress(Node.MAX_STATION_ADDRESS));
            nodeParent.addChild(getNode());
        } else if (obj instanceof Node) {
            Node nodeParent = (Node) obj;
            getNode()
                    .moveSortIndex(nodeParent.getfirstFreeStationAddress(Node.MAX_STATION_ADDRESS));
            nodeParent.addChild(getNode());
        }
        return true;
    }
}
