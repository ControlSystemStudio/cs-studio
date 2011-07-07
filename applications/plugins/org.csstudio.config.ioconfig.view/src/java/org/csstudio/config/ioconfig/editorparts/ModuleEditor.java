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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.config.ioconfig.config.view.ChannelConfigDialog;
import org.csstudio.config.ioconfig.config.view.ModuleListLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmTextItem;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class ModuleEditor extends AbstractGsdNodeEditor<ModuleDBO> {
    
    public static final String ID = "org.csstudio.config.ioconfig.view.editor.module";
    
    protected static final Logger LOG = LoggerFactory.getLogger(ModuleEditor.class);
    
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
        private final TableViewer _mTypList;
        
        ISelectionChangedListenerForModuleTypeList(@Nonnull final Group topGroup,
                                                   @Nonnull final TableViewer moduleTypList) {
            _topGroup = topGroup;
            _mTypList = moduleTypList;
        }
        
        @Override
        public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
            GsdModuleModel2 selectedModule = (GsdModuleModel2) ((StructuredSelection) _mTypList
                    .getSelection()).getFirstElement();
            
            if(ifSameModule(selectedModule)) {
                return;
            }
            
            int selectedModuleNo = selectedModule.getModuleNumber();
            int savedModuleNo = (Integer) _mTypList.getTable().getData();
            boolean hasChanged = savedModuleNo != selectedModuleNo;
            ModuleDBO module = getModule();
            try {
                String createdBy = getUserName();
                GSDModuleDBO gsdModule;
                try {
                    module.setNewModel(selectedModuleNo, createdBy);
                    gsdModule = module.getGSDModule();
                } catch (IllegalArgumentException iea) {
                    // Unknown Module (--> Config the Epics Part)
                    gsdModule = openChannelConfigDialog(selectedModule, null);
                    if(gsdModule == null) {
                        return;
                    }
                    // TODO: (hrickens) Prüfen ob das nicht mit im openChannelConfigDialog
                    // erledigt werden kann.
                    gsdModule.setModuleId(selectedModuleNo);
                    GSDFileDBO gsdFile = module.getGSDFile();
                    if(gsdFile != null) {
                        gsdFile.addGSDModule(gsdModule);
                    }
                    gsdModule.save();
                }
                Text nameWidget = getNameWidget();
                if(nameWidget != null) {
                    nameWidget.setText(gsdModule.getName());
                }
            } catch (PersistenceException e1) {
                openErrorDialog(e1, getProfiBusTreeView());
                LOG.error("Database error!", e1);
            }
            setSavebuttonEnabled("ModuleTyp", hasChanged);
            try {
                makeCurrentUserParamData(_topGroup);
            } catch (IOException e) {
                LOG.error("File read error!", e);
                DeviceDatabaseErrorDialog.open(null, "File read error!", e);
            }
            getProfiBusTreeView().refresh(module.getParent());
        }
        
        /**
         * @param selectedModule
         * @return
         */
        private boolean ifSameModule(@Nullable final GsdModuleModel2 selectedModule) {
            return ( (selectedModule == null) || (getModule() == null) || ( (getModule()
                    .getGSDModule() != null) && (getModule().getGSDModule().getModuleId() == selectedModule
                    .getModuleNumber())));
        }
    }
    
    @Nonnull
    protected ModuleDBO getModule() {
        return (ModuleDBO) getNode();
    }
    
    /**
     * This class provides the content for the table.
     */
    public static class ComboContentProvider implements IStructuredContentProvider {
        
        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        @CheckForNull
        public final Object[] getElements(@Nullable final Object arg0) {
            if(arg0 instanceof Map) {
                Map<Integer, GsdModuleModel2> map = (Map<Integer, GsdModuleModel2>) arg0;
                return map.values().toArray(new GsdModuleModel2[0]);
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
        
        if(_module == null) {
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
        if(nameWidget != null) {
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
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        // TODO (hrickens) [02.05.2011]: Hier sollte bei jeder änderung der Werte Aktualisiert werden. (Momentan garnicht aber auch nicht nur beim Speichern)
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
                GsdModuleModel2 firstElement = (GsdModuleModel2) ((StructuredSelection) _moduleTypList
                        .getSelection()).getFirstElement();
                GSDModuleDBO gsdModule = _module.getGSDModule();
                gsdModule = openChannelConfigDialog(firstElement, gsdModule);
                if(gsdModule != null) {
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
                if(element instanceof GsdModuleModel2) {
                    GsdModuleModel2 gsdModuleModel = (GsdModuleModel2) element;
                    if( (filter.getText() == null) || (filter.getText().length() < 1)) {
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
                if(filterButton.getSelection()) {
                    if(element instanceof GsdModuleModel2) {
                        GsdModuleModel2 gmm = (GsdModuleModel2) element;
                        int selectedModuleNo = gmm.getModuleNumber();
                        GSDFileDBO gsdFile = getGsdFile();
                        GSDModuleDBO module = null;
                        if(gsdFile != null) {
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
                if( (e1 instanceof GsdModuleModel2) && (e2 instanceof GsdModuleModel2)) {
                    GsdModuleModel2 eUPD1 = (GsdModuleModel2) e1;
                    GsdModuleModel2 eUPD2 = (GsdModuleModel2) e2;
                    return eUPD1.getModuleNumber() - eUPD2.getModuleNumber();
                }
                return super.compare(viewer, e1, e2);
            }
            
        });
        
        try {
            makeCurrentUserParamData(topGroup);
            _moduleTypList
                    .addSelectionChangedListener(new ISelectionChangedListenerForModuleTypeList(topGroup,
                                                                                                _moduleTypList));
            
            SlaveDBO slave = _module.getSlave();
            if(getGsdFile() != null) {
                //            Map<Integer, GsdModuleModel2> gsdModuleList = slave.getGSDSlaveData().getGsdModuleList2();
                Map<Integer, GsdModuleModel2> gsdModuleList = slave.getGSDFile()
                        .getParsedGsdFileModel().getModuleMap();
                _moduleTypList.setInput(gsdModuleList);
                comp.layout();
                _moduleTypList.getTable().setData(_module.getModuleNumber());
                GsdModuleModel2 selectModuleModel = gsdModuleList.get(_module.getModuleNumber());
                if(selectModuleModel != null) {
                    _moduleTypList.setSelection(new StructuredSelection(selectModuleModel));
                }
            }
            _moduleTypList.getTable().showSelection();
        } catch (IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Module. GSD File read error", e2);
            LOG.error("Can't save Module. GSD File read error", e2);
        }
    }
    
    /**
     *
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     * @throws IOException 
     */
    private void makeCurrentUserParamData(@Nonnull final Group topGroup) throws IOException {
        if(_currentUserParamDataGroup != null) {
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
            if(getDocumentationManageView() != null) {
                _module.setDocuments(getDocumentationManageView().getDocuments());
            }
            save();
        } catch (PersistenceException e) {
            LOG.error("Can't save Module! Database error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Module! Database error.", e);
        } catch (IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Slave.GSD File read error", e2);
            LOG.error("Can't save Slave.GSD File read error", e2);
        }
    }
    
    /**
     * @throws PersistenceException 
     *
     */
    private void updateChannels() throws PersistenceException {
        Set<ChannelStructureDBO> channelStructs = _module.getChildren();
        for (ChannelStructureDBO channelStructure : channelStructs) {
            Set<ChannelDBO> channels = channelStructure.getChildren();
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
            GsdModuleModel2 gsdModuleModel = _module.getGSDFile().getParsedGsdFileModel()
                    .getModule((Integer) _moduleTypList.getTable().getData());
            if(gsdModuleModel != null) {
                _moduleTypList.setSelection(new StructuredSelection(gsdModuleModel), true);
            }
        } catch (NullPointerException e) {
            _moduleTypList.getTable().select(0);
        }
        for (Object prmTextObject : _prmTextCV) {
            if(prmTextObject instanceof ComboViewer) {
                ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                if(!prmTextCV.getCombo().isDisposed()) {
                    Integer index = (Integer) prmTextCV.getCombo().getData();
                    if(index != null) {
                        prmTextCV.getCombo().select(index);
                    }
                }
            } else if(prmTextObject instanceof Text) {
                Text prmText = (Text) prmTextObject;
                if(!prmText.isDisposed()) {
                    String value = (String) prmText.getData();
                    if(value != null) {
                        prmText.setText(value);
                    }
                }
            }
        }
        save();
    }
    
    /** {@inheritDoc} */
    @Override
    public final void fill(@Nullable final GSDFileDBO gsdFile) {
        return;
    }
    
    /** {@inheritDoc} */
    @Override
    public final GSDFileDBO getGsdFile() {
        return _module.getSlave().getGSDFile();
    }
    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public void setGsdFile(GSDFileDBO gsdFile) {
        _module.getSlave().setGSDFile(gsdFile);
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
        
        //        int val = 0;
        //        if(prmText != null) {
        //            val = prmText.getIndex();
        //        }
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
    protected GSDModuleDBO openChannelConfigDialog(@Nonnull final GsdModuleModel2 model,
                                                   @CheckForNull GSDModuleDBO gsdModuleDBO) {
        GSDModuleDBO gsdModule = gsdModuleDBO == null ? new GSDModuleDBO(model.getName())
                : gsdModuleDBO;
        if(_module != null) {
            gsdModule.setModuleId(_module.getModuleNumber());
            if(_module.getGSDFile() != null) {
                gsdModule.setGSDFile(_module.getGSDFile());
            }
        }
        String createdBy = "UNKNOWN";
        User currentUser = SecurityFacade.getInstance().getCurrentUser();
        if( (currentUser != null) && (currentUser.getUsername() != null)) {
            createdBy = currentUser.getUsername();
        }
        gsdModule.setCreatedBy(createdBy);
        gsdModule.setUpdatedBy(createdBy);
        Date date = new Date();
        gsdModule.setCreatedOn(date);
        gsdModule.setUpdatedOn(date);
        ChannelConfigDialog channelConfigDialog = new ChannelConfigDialog(Display.getCurrent()
                .getActiveShell(), model, gsdModule);
        if(channelConfigDialog.open() == ChannelConfigDialog.OK) {
            gsdModule.setConfigurationData(channelConfigDialog.getConfigurationData());
            String parameter = channelConfigDialog.getParameter();
            if(parameter.length() > 254) {
                parameter = parameter.substring(0, 254);
            }
            gsdModule.setParameter(parameter);
            try {
                gsdModule.save();
                return gsdModule;
            } catch (PersistenceException e) {
                openErrorDialog(e, getProfiBusTreeView());
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
        getNode().setCreatedBy(getUserName());
        getNode().setCreatedOn(new Date());
        getNode().setVersion(-2);
        
        Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection())
                .getFirstElement();
        
        try {
            if( obj == null) {
                getProfiBusTreeView().getTreeViewer().setInput(getModule());
            } else if(obj instanceof SlaveDBO) {
                SlaveDBO nodeParent = (SlaveDBO) obj;
                getModule().moveSortIndex(nodeParent.getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS));
                nodeParent.addChild(getModule());
            }
        } catch (PersistenceException e) {
            LOG.error("Can't create new Module! Database error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't create new Module! Database error.", e);
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    GsdModuleModel2 getGsdPropertyModel() throws IOException {
        return _module.getGsdModuleModel2();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
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
        if(_module.getConfigurationDataList().size() > index) {
            return _module.getConfigurationDataList().get(index);
        }
        return null;
    }
    
}
