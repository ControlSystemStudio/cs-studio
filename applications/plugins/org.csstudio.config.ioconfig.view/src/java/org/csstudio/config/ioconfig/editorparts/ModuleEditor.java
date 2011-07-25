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
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
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
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Spinner;
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
     * TODO (hrickens) : 
     * 
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 22.07.2011
     */
    private final class FilterButtonSelectionListener implements SelectionListener {
        private final TableViewer _mTypList;

        /**
         * Constructor.
         */
        public FilterButtonSelectionListener(@Nonnull TableViewer moduleTypList) {
            _mTypList = moduleTypList;
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            _mTypList.refresh();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            _mTypList.refresh();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 22.07.2011
     */
    private final class EditButtonSelectionListener implements SelectionListener {
        private final TableViewer _mTypList;

        /**
         * Constructor.
         * @param moduleTypList
         */
        public EditButtonSelectionListener(@Nonnull final TableViewer moduleTypList) {
            _mTypList = moduleTypList;
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            editSelected();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            editSelected();
        }
        
        private void editSelected() {
            GsdModuleModel2 firstElement = (GsdModuleModel2) ((StructuredSelection) _mTypList
                    .getSelection()).getFirstElement();
            GSDModuleDBO gsdModule = getNode().getGSDModule();
            gsdModule = openChannelConfigDialog(firstElement, gsdModule);
            if(gsdModule != null) {
                GSDFileDBO gsdFile = getNode().getGSDFile();
                if(gsdFile != null) {
                    gsdFile.addGSDModule(gsdModule);
                    getProfiBusTreeView().refresh(getNode());
                }
            }
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 22.07.2011
     */
    private final class FilterModifyListener implements ModifyListener {
        private final TableViewer _mTypList;

        /**
         * Constructor.
         * @param moduleTypList
         */
        public FilterModifyListener(@Nonnull TableViewer moduleTypList) {
            _mTypList = moduleTypList;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            _mTypList.refresh();
        }
    }

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
            ModuleDBO module = getNode();
            try {
                String createdBy = getUserName();
                GSDModuleDBO gsdModule;
                try {
                    module.setNewModel(selectedModuleNo, createdBy);
                    gsdModule = module.getGSDModule();
                } catch (IllegalArgumentException iea) {
                    // Unknown Module (--> Config the Epics Part)
                    gsdModule = createNewModulePrototype(selectedModule, selectedModuleNo, module);
                    if(gsdModule==null) {
                        return;
                    }
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
         * @param selectedModuleNo
         * @param module
         * @return
         * @throws PersistenceException
         */
        @CheckForNull
        public GSDModuleDBO createNewModulePrototype(@Nonnull GsdModuleModel2 selectedModule,
                                                     int selectedModuleNo,
                                                     @Nonnull ModuleDBO module) throws PersistenceException {
            GSDModuleDBO gsdModule;
            gsdModule = openChannelConfigDialog(selectedModule, null);
            if(gsdModule == null) {
                return null;
            }
            // TODO: (hrickens) Prüfen ob das nicht mit im openChannelConfigDialog
            // erledigt werden kann.
            gsdModule.setModuleId(selectedModuleNo);
            GSDFileDBO gsdFile = module.getGSDFile();
            if(gsdFile != null) {
                gsdFile.addGSDModule(gsdModule);
            }
            gsdModule.save();
            return gsdModule;
        }
        
        /**
         * @param selectedModule
         * @return
         */
        private boolean ifSameModule(@Nullable final GsdModuleModel2 selectedModule) {
            ModuleDBO module = getNode();
            return ( (selectedModule == null) || (module == null) || ( (module
                    .getGSDModule() != null) && (module.getGSDModule().getModuleId() == selectedModule
                    .getModuleNumber())));
        }
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
        _module = getNode();
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
        
        buildNameGroup(comp);
        
        final Group topGroup = new Group(comp, SWT.NONE);
        topGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        topGroup.setLayout(new GridLayout(3, false));
        topGroup.setText("Module selection");
        
        makeDescGroup(comp, 1);
        
        Text text = new Text(topGroup, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        // TODO (hrickens) [02.05.2011]: Hier sollte bei jeder änderung der Werte Aktualisiert werden. (Momentan garnicht aber auch nicht nur beim Speichern)
        text.setText(_module.getConfigurationData());
        
        Composite filterComposite = buildFilterComposite(topGroup);
        
        final Text filter = buildFilterText(filterComposite);
        final Button filterButton = buildFilterButton(filterComposite);
        Button epicsEditButton = buildEditButton(topGroup);
        buildModuleTypList(comp, topGroup, filter, filterButton);
        epicsEditButton.addSelectionListener(new EditButtonSelectionListener(_moduleTypList));
    }

    public void buildModuleTypList(@Nonnull final Composite comp,
                                   @Nonnull final Group topGroup,
                                   @Nonnull final Text filter,
                                   @Nonnull final Button filterButton) {
        _moduleTypList = new TableViewer(topGroup, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        _moduleTypList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));
        _moduleTypList.setContentProvider(new ComboContentProvider());
        _moduleTypList.setLabelProvider(new ModuleListLabelProvider(_moduleTypList.getTable()));
        setTypListFilter(filter, filterButton);
        
        setTypeListSorter();
        
        try {
            makeCurrentUserParamData(topGroup);
            _moduleTypList
                    .addSelectionChangedListener(new ISelectionChangedListenerForModuleTypeList(topGroup,
                                                                                                _moduleTypList));
            
            GSDFileDBO gsdFile = getGsdFile();
            if (gsdFile != null) {
                Map<Integer, GsdModuleModel2> gsdModuleList =
                                                              gsdFile.getParsedGsdFileModel()
                                                                      .getModuleMap();
                _moduleTypList.setInput(gsdModuleList);
                comp.layout();
                _moduleTypList.getTable().setData(_module.getModuleNumber());
                GsdModuleModel2 selectModuleModel = gsdModuleList.get(_module.getModuleNumber());
                if (selectModuleModel != null) {
                    _moduleTypList.setSelection(new StructuredSelection(selectModuleModel));
                }
            }
            _moduleTypList.getTable().showSelection();
        } catch (IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Module. GSD File read error", e2);
            LOG.error("Can't save Module. GSD File read error", e2);
        }
    }

    @Nonnull
    public Text buildFilterText(@Nonnull final Composite filterComposite) {
        final Text filter = new Text(filterComposite, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filter.setMessage("Module Filter");
        filter.addModifyListener(new FilterModifyListener(_moduleTypList));
        return filter;
    }

    @Nonnull
    public Composite buildFilterComposite(@Nonnull final Group topGroup) {
        Composite filterComposite = new Composite(topGroup, SWT.NONE);
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        filterComposite.setLayout(layout);
        return filterComposite;
    }

    /**
     * @param filterComposite
     * @return
     */
    @Nonnull
    public Button buildFilterButton(@Nonnull Composite filterComposite) {
        final Button filterButton = new Button(filterComposite, SWT.CHECK);
        filterButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        filterButton.setText("Only have prototype");
        filterButton.addSelectionListener(new FilterButtonSelectionListener(_moduleTypList));
        return filterButton;
    }

    /**
     * @param topGroup
     */
    @Nonnull
    public Button buildEditButton(@Nonnull final Group topGroup) {
        Button epicsEditButton = new Button(topGroup, SWT.PUSH);
        epicsEditButton.setText("Edit Prototype");
        return epicsEditButton;
    }

    /**
     * @param filter 
     * @param filterButton
     */
    public void setTypListFilter(@Nonnull final Text filter, @Nonnull final Button filterButton) {
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
    }

    /**
     * 
     */
    public void setTypeListSorter() {
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
    }

    /**
     * @param comp
     */
    public void buildNameGroup(@Nonnull final Composite comp) {
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
    }
    
    /**
     *
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     * @throws IOException 
     */
    protected void makeCurrentUserParamData(@Nonnull final Group topGroup) throws IOException {
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
        Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            _module.setName(nameWidget.getText());
            nameWidget.setData(nameWidget.getText());
        }
        
        Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner != null) {
            indexSpinner.setData(indexSpinner.getSelection());
        }
        
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
        cancelNameWidget();
        cancelIndexSpinner();
        cancelGsdModuleModel();
        for (Object prmTextObject : _prmTextCV) {
            if(prmTextObject instanceof ComboViewer) {
                cancelComboViewer(prmTextObject);
            } else if(prmTextObject instanceof Text) {
                cancelText(prmTextObject);
            }
        }
        save();
    }

    /**
     * 
     */
    public void cancelGsdModuleModel() {
        try {
            GSDFileDBO gsdFile = _module.getGSDFile();
            if (gsdFile != null) {
                GsdModuleModel2 gsdModuleModel = gsdFile.getParsedGsdFileModel()
                                                         .getModule((Integer) _moduleTypList
                                                                 .getTable().getData());
                if (gsdModuleModel != null) {
                    _moduleTypList.setSelection(new StructuredSelection(gsdModuleModel), true);
                }
            }
        } catch (NullPointerException e) {
            _moduleTypList.getTable().select(0);
        }
    }

    /**
     * 
     */
    public void cancelIndexSpinner() {
        Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner != null) {
            indexSpinner.setSelection((Short) indexSpinner.getData());
        }
    }

    /**
     * 
     */
    public void cancelNameWidget() {
        Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            nameWidget.setText((String) nameWidget.getData());
        }
    }

    /**
     * @param prmTextObject
     */
    public void cancelComboViewer(@Nonnull Object prmTextObject) {
        ComboViewer prmTextCV = (ComboViewer) prmTextObject;
        if(!prmTextCV.getCombo().isDisposed()) {
            Integer index = (Integer) prmTextCV.getCombo().getData();
            if(index != null) {
                prmTextCV.getCombo().select(index);
            }
        }
    }

    /**
     * @param prmTextObject
     */
    public void cancelText(@Nonnull Object prmTextObject) {
        Text prmText = (Text) prmTextObject;
        if(!prmText.isDisposed()) {
            String value = (String) prmText.getData();
            if(value != null) {
                prmText.setText(value);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public final void fill(@Nullable final GSDFileDBO gsdFile) {
        return;
    }
    
    /** {@inheritDoc} */
    @Override
    @CheckForNull
    public final GSDFileDBO getGsdFile() {
        return _module.getSlave().getGSDFile();
    }
    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public void setGsdFile(@CheckForNull GSDFileDBO gsdFile) {
        _module.getSlave().setGSDFile(gsdFile);
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
                                                   @CheckForNull final GSDModuleDBO gsdModuleDBO) {
        final GSDModuleDBO gsdModule = gsdModuleDBO == null ? new GSDModuleDBO(model.getName())
                : gsdModuleDBO;
        if(_module != null) {
            gsdModule.setModuleId(_module.getModuleNumber());
            if(_module.getGSDFile() != null) {
                gsdModule.setGSDFile(_module.getGSDFile());
            }
        }
        String createdBy = getUserName();
        Date date = new Date();
        if(gsdModuleDBO==null) {
            gsdModule.setCreatedBy(createdBy);
            gsdModule.setCreatedOn(date);
        }
        gsdModule.setUpdatedBy(createdBy);
        gsdModule.setUpdatedOn(date);
        ChannelConfigDialog channelConfigDialog = new ChannelConfigDialog(Display.getCurrent()
                .getActiveShell(), model, gsdModule);
        if(channelConfigDialog.open() == Window.OK) {
//            try {
//                channelConfigDialog.getGsdModule().save();
////                gsdModule.save();
                return gsdModule;
//            } catch (PersistenceException e) {
//                openErrorDialog(e, getProfiBusTreeView());
//            }
        }
        //         gsdModule = null;
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
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
            } else if(obj instanceof SlaveDBO) {
                SlaveDBO nodeParent = (SlaveDBO) obj;
                getNode().moveSortIndex(nodeParent.getfirstFreeStationAddress(AbstractNodeDBO.getMaxStationAddress()));
                nodeParent.addChild(getNode());
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
