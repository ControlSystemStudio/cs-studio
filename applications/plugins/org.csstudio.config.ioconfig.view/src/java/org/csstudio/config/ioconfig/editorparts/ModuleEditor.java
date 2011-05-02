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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.config.view.ChannelConfigDialog;
import org.csstudio.config.ioconfig.config.view.ModuleListLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmTextItem;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.widgets.Text;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class ModuleEditor extends AbstractGsdNodeEditor {
    
    public static final String ID = "org.csstudio.config.ioconfig.view.editor.module";
    
    private static final Logger LOG = CentralLogger.getInstance().getLogger(ModuleEditor.class);
    
    /**
     *
     * If the selection changes the old Channels will be deleted and the new Channel created for the
     * new Module. Have the Module no Prototype the Dialog to generate Prototype is opened.
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 17.04.2009
     */
    private final class ISelectionChangedListenerForModuleTypeList implements
            ISelectionChangedListener {
        private final Group _topGroup;
        
        private ISelectionChangedListenerForModuleTypeList(@Nonnull final Group topGroup) {
            _topGroup = topGroup;
        }
        
        @Override
        public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
            GsdModuleModel selectedModule = (GsdModuleModel) ((StructuredSelection) _moduleTypList
                    .getSelection()).getFirstElement();
            
            if (ifSameModule(selectedModule)) {
                return;
            }
            
            int selectedModuleNo = selectedModule.getModuleNumber();
            int savedModuleNo = (Integer) _moduleTypList.getTable().getData();
            boolean hasChanged = savedModuleNo != selectedModuleNo;
            
            ModuleDBO module = getModule();
            module.removeAllChild();
            module.setModuleNumber(selectedModuleNo);
            GSDModuleDBO gsdModule = module.getGSDModule();
            
            // Unknown Module (--> Config the Epics Part)
            if (gsdModule == null) {
                gsdModule = openChannelConfigDialog(selectedModule, null);
                if (gsdModule == null) {
                    return;
                }
                // TODO: (hrickens) Prüfen ob das nicht mit im openChannelConfigDialog
                // erledigt werden kann.
                gsdModule.setModuleId(selectedModuleNo);
                module.getGSDFile().addGSDModule(gsdModule);
                try {
                    gsdModule.save();
                } catch (PersistenceException e) {
                    openErrorDialog(e);
                }
            }
            getNameWidget().setText(gsdModule.getName());
            setSavebuttonEnabled("ModuleTyp", hasChanged);
            
            // Generate Input Channel
            TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes = gsdModule
                    .getModuleChannelPrototypeNH();
            try {
                if (moduleChannelPrototypes != null) {
                    ModuleChannelPrototypeDBO[] array = moduleChannelPrototypes
                            .toArray(new ModuleChannelPrototypeDBO[0]);
                    for (int sortIndex = 0; sortIndex < array.length; sortIndex++) {
                        ModuleChannelPrototypeDBO prototype = array[sortIndex];
                        makeNewChannel(prototype, sortIndex);
                    }
                }
                module.localUpdate();
                module.localSave();
            } catch (PersistenceException e) {
                LOG.error(e);
                DeviceDatabaseErrorDialog.open(null, "Database error!", e);
            }
            getProfiBusTreeView().refresh(module.getParent());
            try {
                makeCurrentUserParamData(_topGroup);
            } catch (IOException e) {
                LOG.error(e);
                DeviceDatabaseErrorDialog.open(null, "File read error!", e);
            }
        }
        
        /**
         * @param selectedModule
         * @return
         */
        private boolean ifSameModule(@Nullable final GsdModuleModel selectedModule) {
            return ((selectedModule == null) || (getModule() == null) || ((getModule()
                    .getGSDModule() != null) && (getModule().getGSDModule().getModuleId() == selectedModule
                    .getModuleNumber())));
        }
        
        private void makeNewChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype,
                                    final int sortIndex) throws PersistenceException {
            if (channelPrototype.isStructure()) {
                makeStructChannel(channelPrototype, sortIndex);
            } else {
                makeNewPureChannel(channelPrototype, sortIndex);
            }
        }
        
        private void makeStructChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype,
                                       final int sortIndex) throws PersistenceException {
            channelPrototype.getOffset();
            Date now = new Date();
            String createdBy = "Unknown";
            User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                createdBy = user.getUsername();
            }
            
            ChannelStructureDBO channelStructure = ChannelStructureDBO
                    .makeChannelStructure(getModule(),
                                          channelPrototype.isInput(),
                                          channelPrototype.getType(),
                                          channelPrototype.getName());
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
        
        private void makeNewPureChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype,
                                        final int sortIndex) throws PersistenceException {
            Date now = new Date();
            String createdBy = "Unknown";
            User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                createdBy = user.getUsername();
            }
            boolean isDigi = channelPrototype.getType().getBitSize() == 1;
            ChannelStructureDBO cs = ChannelStructureDBO.makeSimpleChannel(getModule(),
                                                                           channelPrototype
                                                                                   .getName(),
                                                                           channelPrototype
                                                                                   .isInput(),
                                                                           isDigi);
            cs.moveSortIndex((short) sortIndex);
            ChannelDBO channel = cs.getFirstChannel();
            channel.setCreatedOn(now);
            channel.setUpdatedOn(now);
            channel.setCreatedBy(createdBy);
            channel.setUpdatedBy(createdBy);
            channel.setChannelTypeNonHibernate(channelPrototype.getType());
            channel.setStatusAddressOffset(channelPrototype.getShift());
            channel.moveSortIndex((short) sortIndex);
        }
    }
    
    protected ModuleDBO getModule() {
        return _module;
    }
    
    /**
     * This class provides the content for the table.
     */
    public class ComboContentProvider implements IStructuredContentProvider {
        
        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        @CheckForNull
        public final Object[] getElements(@Nullable final Object arg0) {
            if (arg0 instanceof HashMap) {
                HashMap<Integer, GsdModuleModel> map = (HashMap<Integer, GsdModuleModel>) arg0;
                return map.values().toArray(new GsdModuleModel[0]);
            }
            return null;
        }
        
        /**
         * Disposes any resources.
         */
        @Override
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
        @Override
        public final void inputChanged(@Nullable final Viewer arg0,
                                       @Nullable final Object arg1,
                                       @Nullable final Object arg2) {
            // do nothing
        }
        
    }
    
    /**
     * The Module Object.
     */
    private ModuleDBO _module;
    
    /**
     * The List to choose the type of module.
     */
    private TableViewer _moduleTypList;
    
    private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();
    
    private Group _currentUserParamDataGroup;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        _module = (ModuleDBO) getNode();
        super.createPartControl(parent);
        
        if (_module == null) {
            newNode();
            _module.setModuleNumber(-1);
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        moduels("Module");
        selecttTabFolder(0);
    }
    
    /**
     * @param head
     *            the tabItemName
     *
     */
    private void moduels(@Nonnull final String head) {
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
        Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            setText(nameWidget, _module.getName(), 255);
            nameWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        }
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName,
                                                     _module,
                                                     getMLSB(),
                                                     "Sort Index",
                                                     getProfiBusTreeView()));
        
        /*
         * Top Composite.
         */
        final Group topGroup = new Group(comp, SWT.NONE);
        topGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        topGroup.setLayout(new GridLayout(3, false));
        topGroup.setText("Module selection");

        makeDescGroup(comp, 1);
        
        Text text = new Text(topGroup, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,false,false,3,1));
        // TODO (hrickens) [02.05.2011]: Hier sollte bei jeder änderung der Werte Aktualisiert werden. (Momentan garnicht aber auch nciht nur beim Speichern)
        text.setText(_module.getConfigurationData());

        Composite filterComposite = new Composite(topGroup, SWT.NONE);
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        filterComposite.setLayout(layout);
        
        final Text filter = new Text(filterComposite, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filter.setMessage("Module Filter");
        // filter.setLayoutData(GridDataFactory.fillDefaults().create());
        filter.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(@Nonnull final ModifyEvent e) {
                _moduleTypList.refresh();
            }
            
        });
        final Button filterButton = new Button(filterComposite, SWT.CHECK);
        filterButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        filterButton.setText("Only have prototype");
        filterButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                _moduleTypList.refresh();
            }
            
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                _moduleTypList.refresh();
            }
            
        });
        
        Button epicsEditButton = new Button(topGroup, SWT.PUSH);
        epicsEditButton.setText("Edit Prototype");
        epicsEditButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                action();
            }
            
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                action();
            }
            
            private void action() {
                GsdModuleModel firstElement = (GsdModuleModel) ((StructuredSelection) _moduleTypList
                        .getSelection()).getFirstElement();
                GSDModuleDBO gsdModule = _module.getGSDModule();
                gsdModule = openChannelConfigDialog(firstElement, gsdModule);
                if (gsdModule != null) {
                    _module.getGSDFile().addGSDModule(gsdModule);
                    getProfiBusTreeView().refresh(_module);
                }
            }
        });
        
        //        new Label(topGroup, SWT.NONE).setText("Module Type: ");
        
        _moduleTypList = new TableViewer(topGroup, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        _moduleTypList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));
        _moduleTypList.setContentProvider(new ComboContentProvider());
        _moduleTypList.setLabelProvider(new ModuleListLabelProvider(_moduleTypList.getTable(),
                                                                    getGsdFile()));
        _moduleTypList.addFilter(new ViewerFilter() {
            
            @Override
            public boolean select(@Nullable final Viewer viewer,
                                  @Nullable final Object parentElement,
                                  @Nullable final Object element) {
                if (element instanceof GsdModuleModel) {
                    GsdModuleModel gsdModuleModel = (GsdModuleModel) element;
                    if ((filter.getText() == null) || (filter.getText().length() < 1)) {
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
            public boolean select(@Nullable final Viewer viewer,
                                  @Nullable final Object parentElement,
                                  @Nullable final Object element) {
                if (filterButton.getSelection()) {
                    if (element instanceof GsdModuleModel) {
                        GsdModuleModel gmm = (GsdModuleModel) element;
                        int selectedModuleNo = gmm.getModuleNumber();
                        GSDFileDBO gsdFile = getGsdFile();
                        GSDModuleDBO module = null;
                        if (gsdFile != null) {
                            module = gsdFile.getGSDModule(selectedModuleNo);
                        }
                        return module != null;
                    }
                }
                return true;
            }
            
        });
        
        _moduleTypList.setSorter(new ViewerSorter() {
            
            @Override
            public int compare(@Nullable final Viewer viewer,
                               @Nullable final Object e1,
                               @Nullable final Object e2) {
                if ((e1 instanceof GsdModuleModel) && (e2 instanceof GsdModuleModel)) {
                    GsdModuleModel eUPD1 = (GsdModuleModel) e1;
                    GsdModuleModel eUPD2 = (GsdModuleModel) e2;
                    return eUPD1.getModuleNumber() - eUPD2.getModuleNumber();
                }
                return super.compare(viewer, e1, e2);
            }
            
        });
        
        try {
            makeCurrentUserParamData(topGroup);
        } catch (IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        _moduleTypList
                .addSelectionChangedListener(new ISelectionChangedListenerForModuleTypeList(topGroup));
        
        SlaveDBO slave = _module.getSlave();
        if (getGsdFile() != null) {
            Map<Integer, GsdModuleModel> gsdModuleList = slave.getGSDSlaveData().getGsdModuleList();
            _moduleTypList.setInput(gsdModuleList);
            comp.layout();
            _moduleTypList.getTable().setData(_module.getModuleNumber());
            GsdModuleModel selectModuleModel = gsdModuleList.get(_module.getModuleNumber());
            if (selectModuleModel != null) {
                _moduleTypList.setSelection(new StructuredSelection(selectModuleModel));
            }
        }
        _moduleTypList.getTable().showSelection();
    }
    
    /**
     *
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     * @throws IOException 
     */
    private void makeCurrentUserParamData(@Nonnull final Group topGroup) throws IOException {
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
            public void controlResized(@Nullable final ControlEvent e) {
                Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(scrollComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrollComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nullable final ControlEvent e) {
                Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(currentUserParamDataComposite.computeSize(r.width,
                                                                                     SWT.DEFAULT));
            }
        });
        
        buildCurrentUserPrmData(currentUserParamDataComposite);
        
        topGroup.layout();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Module
        _module.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());
        
        getIndexSpinner().setData(getIndexSpinner().getSelection());
        
        try {
            updateChannels();
            saveUserPrmData();
            // Document
            if (getDocumentationManageView() != null) {
                _module.setDocuments(getDocumentationManageView().getDocuments());
            }
            save();
        } catch (PersistenceException e) {
            LOG.error(e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Module! Database error.", e);
        } catch (IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Slave.GSD File read error", e2);
            CentralLogger.getInstance().error(this, e2.getLocalizedMessage());
        }
    }
    
    /**
     * @throws PersistenceException 
     *
     */
    private void updateChannels() throws PersistenceException {
        Set<ChannelStructureDBO> channelStructs = _module.getChannelStructs();
        for (ChannelStructureDBO channelStructure : channelStructs) {
            Set<ChannelDBO> channels = channelStructure.getChannels();
            for (ChannelDBO channel : channels) {
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
                    .get(_moduleTypList.getTable().getData());
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
    public final boolean fill(@Nullable final GSDFileDBO gsdFile) {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public final GSDFileDBO getGsdFile() {
        return _module.getSlave().getGSDFile();
    }
    
//    /**
//     *
//     * @param currentUserParamDataGroup
//     * @param extUserPrmData
//     * @param value
//     * @throws IOException 
//     */
//    private void makecurrentUserParamData(@Nonnull final Composite currentUserParamDataGroup,
//                                          @Nonnull final ExtUserPrmData extUserPrmData,
//                                          @CheckForNull final Integer value) throws IOException {
//        
//        Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);
//        
//        if (extUserPrmData != null) {
//            text.setText(extUserPrmData.getText() + ":");
//            //            prmTextMap = extUserPrmData.getPrmText();
//            PrmText prmText = extUserPrmData.getPrmText();
//            if ((prmText == null)
//                    && (extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10)) {
//                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
//            } else {
//                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup, value, extUserPrmData));
//            }
//        }
//        new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
//    }
    
    /**
     *
     * @param parent
     *            the Parent Composite.
     * @param value
     *            the Selected currentUserParamData Value.
     * @param extUserPrmData
     * @param prmTextMap
     * @return a ComboView for are currentUserParamData Property
     * @throws IOException 
     */
    @Nonnull
    private ComboViewer makeComboViewer(@Nonnull final Composite parent,
                                        @CheckForNull final Integer value,
                                        @Nonnull final ExtUserPrmData extUserPrmData) throws IOException {
        Integer localValue = value;
        ComboViewer prmTextCV = new ComboViewer(parent);
        RowData data = new RowData();
        data.exclude = false;
        prmTextCV.getCombo().setLayoutData(data);
        prmTextCV.setLabelProvider(new LabelProvider());
        prmTextCV.setContentProvider(new IStructuredContentProvider() {
            
            @Override
            @CheckForNull
            public Object[] getElements(@Nullable final Object inputElement) {
                if (inputElement instanceof ExtUserPrmData) {
                    ExtUserPrmData extUserPrmData = (ExtUserPrmData) inputElement;
                    PrmText prmText = extUserPrmData.getPrmText();
                    if (prmText == null) {
                        PrmTextItem[] prmTextArray = new PrmTextItem[extUserPrmData.getMaxValue()
                                - extUserPrmData.getMinValue() + 1];
                        for (int i = extUserPrmData.getMinValue(); i <= extUserPrmData
                                .getMaxValue(); i++) {
                            prmTextArray[i] = new PrmTextItem(Integer.toString(i), i);
                        }
                        return prmTextArray;
                    }
                    return prmText.getPrmTextItems().toArray();
                }
                return null;
            }
            
            @Override
            public void dispose() {
            }
            
            @Override
            public void inputChanged(final Viewer viewer,
                                     final Object oldInput,
                                     final Object newInput) {
            }
        });
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new ViewerSorter() {
            
            @Override
            public int compare(@Nullable final Viewer viewer,
                               @Nullable final Object e1,
                               @Nullable final Object e2) {
                if ((e1 instanceof PrmTextItem) && (e2 instanceof PrmTextItem)) {
                    PrmTextItem eUPD1 = (PrmTextItem) e1;
                    PrmTextItem eUPD2 = (PrmTextItem) e2;
                    return eUPD1.getIndex() - eUPD2.getIndex();
                }
                return super.compare(viewer, e1, e2);
            }
            
        });
        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmTextCV.setInput(extUserPrmData);
        PrmText prmTextMap = extUserPrmData.getPrmText();
        if (prmTextMap != null) {
            PrmTextItem prmText = prmTextMap.getPrmTextItem(localValue);
            if (prmText != null) {
                prmTextCV.setSelection(new StructuredSelection(prmTextMap
                        .getPrmTextItem(localValue)));
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
     * @throws IOException 
     */
    private void setModify(@Nonnull final ComboViewer prmTextCV) throws IOException {
        PrmTextItem prmText = (PrmTextItem) ((StructuredSelection) prmTextCV.getSelection())
                .getFirstElement();
        ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
        Integer index = extUserPrmData.getIndex();
        GsdModuleModel2 gsdModule = _module.getGsdModuleModel2();
        int bytePos = gsdModule.getExtUserPrmDataRefMap().get(index).getIndex();
        int bitMin = extUserPrmData.getMinBit();
        int bitMax = extUserPrmData.getMaxBit();
        
        int val = 0;
        if (prmText != null) {
            val = prmText.getIndex();
        }
//        gsdModule.addModify(bytePos, bitMin, bitMax, val);
    }
    
    /**
     * Open a Config-Dialog for {@link GSDModuleDBO} and create and store.
     *
     * @param model
     *            The {@link GsdModuleModel} Module Module from the GSD File.
     * @param gsdModule
     *            the {@link GSDModuleDBO} or null for a new one to configure .
     * @return the new or modified GSDModule or null when canceled.
     */
    @CheckForNull
    protected GSDModuleDBO openChannelConfigDialog(@Nonnull final GsdModuleModel model,
                                                   @CheckForNull GSDModuleDBO gsdModuleDBO) {
        GSDModuleDBO gsdModule = gsdModuleDBO == null ? new GSDModuleDBO(model.getName())
                : gsdModuleDBO;
        if (_module != null) {
            gsdModule.setModuleId(_module.getModuleNumber());
            if (_module.getGSDFile() != null) {
                gsdModule.setGSDFile(_module.getGSDFile());
            }
        }
        String createdBy = "UNKNOWN";
        User currentUser = SecurityFacade.getInstance().getCurrentUser();
        if ((currentUser != null) && (currentUser.getUsername() != null)) {
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
        
        getNode().setCreatedBy(name);
        getNode().setCreatedOn(new Date());
        getNode().setVersion(-2);
        
        Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection())
                .getFirstElement();
        
        try {
            if ((getNode() instanceof FacilityDBO) || (obj == null)) {
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
            } else if (obj instanceof ModuleDBO) {
                AbstractNodeDBO nodeParent = (AbstractNodeDBO) obj;
                getNode()
                        .moveSortIndex(nodeParent.getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS));
                // TODO Auto-generated catch block
                nodeParent.addChild(getNode());
            } else if (obj instanceof AbstractNodeDBO) {
                AbstractNodeDBO nodeParent = (AbstractNodeDBO) obj;
                getNode()
                        .moveSortIndex(nodeParent.getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS));
                nodeParent.addChild(getNode());
            }
        } catch (PersistenceException e) {
            LOG.error(e);
            DeviceDatabaseErrorDialog.open(null, "Can't create new Module! Database error.", e);
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    GsdModuleModel2 getGsdPropertyModel() throws IOException {
        return _module.getGsdModuleModel2();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Integer> getPrmUserDataList() {
        return _module.getConfigurationDataList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void setPrmUserData(@Nonnull Integer index, @Nonnull Integer value) {
        _module.setConfigurationDataByte(index, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    Integer getPrmUserData(@Nonnull Integer index) {
        if(_module.getConfigurationDataList().size()>index) {
            return _module.getConfigurationDataList().get(index);
        }
        return null;
    }
    
}
