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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.ChannelConfigDialog;
import org.csstudio.config.ioconfig.config.view.ModuleListLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
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
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import org.eclipse.swt.widgets.Label;
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
     * @author hrickens
     * @since 10.10.2011
     */
    private final class SaveModifyListener implements ModifyListener {
        private final String _event;
        private final Text _modifyedText;

        /**
         * Constructor.
         */
        public SaveModifyListener(@Nonnull final String event, @Nonnull final Text modifyedText) {
            _event = event;
            _modifyedText = modifyedText;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            setSavebuttonEnabled(_event, !_modifyedText.getText().equals(_modifyedText.getData()));
        }
    }

    /**
     * @author hrickens
     * @since 10.10.2011
     */
    private static final class SetTopIndexPaintListener implements PaintListener {

        private final Text _text1;
        private final Text _text2;
        private final Text _getText;
        /**
         * Constructor.
         */
        public SetTopIndexPaintListener(@Nonnull final Text setText1, @Nonnull final Text setText2, @Nonnull final Text getText) {
            _text1 = setText1;
            _text2 = setText2;
            _getText = getText;
        }
        @Override
        public void paintControl(@Nonnull final PaintEvent e) {
            _text1.setTopIndex(_getText.getTopIndex());
            _text2.setTopIndex(_getText.getTopIndex());
        }
    }

    /**
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
        public FilterButtonSelectionListener(@Nonnull final TableViewer moduleTypList) {
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
            final GsdModuleModel2 firstElement = (GsdModuleModel2) ((StructuredSelection) _mTypList
                    .getSelection()).getFirstElement();
            GSDModuleDBO gsdModule = getNode().getGSDModule();
            gsdModule = openChannelConfigDialog(firstElement, gsdModule);
            if(gsdModule != null) {
                final GSDFileDBO gsdFile = getNode().getGSDFile();
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
    private static final class FilterModifyListener implements ModifyListener {
        private final TableViewer _mTypList;

        /**
         * Constructor.
         * @param moduleTypList
         */
        public FilterModifyListener(@Nonnull final TableViewer moduleTypList) {
            _mTypList = moduleTypList;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            _mTypList.refresh();
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
                final Map<Integer, GsdModuleModel2> map = (Map<Integer, GsdModuleModel2>) arg0;
                return map.values().toArray(new GsdModuleModel2[0]);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final void dispose() {
            // We don't create any resources, so we don't dispose any
        }

        /**
         * {@inheritDoc}
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

    private Text _ioNamesText;

    private Text _channelNameText;

    private Text _channelsDescText;

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
        ioNames("IO-Names");
        moduels("Module");
        selecttTabFolder(0);
    }

    /**
     * @param string
     */
    private void ioNames(@Nonnull final String head) {
        final Composite comp = getNewTabItem(head, 2);
        comp.setLayout(new GridLayout(3, false));
        buildLabel(comp, "Name");
        buildLabel(comp, "IO Name");
        buildLabel(comp, "Short Description");

        GridData layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        layoutData.minimumWidth=40;
        _channelNameText = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
        _channelNameText.setLayoutData(layoutData);

        layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        layoutData.minimumWidth=100;
        layoutData.widthHint=100;
        _ioNamesText = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER);
        _ioNamesText.setLayoutData(layoutData);

        _channelsDescText = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        _channelsDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _channelNameText.addPaintListener(new SetTopIndexPaintListener(_ioNamesText,_channelsDescText, _channelNameText ));
        _ioNamesText.addPaintListener(new SetTopIndexPaintListener(_channelNameText,_channelsDescText, _ioNamesText));
        _channelsDescText.addPaintListener(new SetTopIndexPaintListener(_ioNamesText,_channelNameText, _channelsDescText));

        setIONamesText();
        _ioNamesText.addModifyListener(new SaveModifyListener("IONames", _ioNamesText));
        _channelsDescText.addModifyListener(new SaveModifyListener("ChannelsDesc", _channelsDescText));
    }

    private void buildLabel(@Nonnull final Composite comp, @Nonnull final String text) {
        final Label nameLabel = new Label(comp, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        nameLabel.setText(text);
    }

    private void setIONamesText() {
        final StringBuilder ioNamesSB = new StringBuilder();
        final StringBuilder channelNamesSB = new StringBuilder();
        final StringBuilder channelDescSB = new StringBuilder();
        final Set<Entry<Short, ChannelStructureDBO>> channelStructureEntrySet = getNode().getChildrenAsMap().entrySet();
        for (final Entry<Short, ChannelStructureDBO> channelStructureEntry : channelStructureEntrySet) {
            final Set<Entry<Short, ChannelDBO>> channelEntrySet = channelStructureEntry.getValue().getChildrenAsMap().entrySet();
            for (final Entry<Short, ChannelDBO> channelEntry : channelEntrySet) {
                String channelName = channelEntry.getValue().getName();
                if (channelName==null) {
                    channelName="";
                }
                channelNamesSB.append(channelName);
                channelNamesSB.append("\n");

                String ioName = channelEntry.getValue().getIoName();
                if (ioName==null) {
                    ioName="";
                }
                ioNamesSB.append(ioName);
                ioNamesSB.append("\n");

                String desc = channelEntry.getValue().getDescription();
                if (desc==null) {
                    desc=" ";
                }
                if(desc.contains("\r\n")) {
                    desc = desc.split("\r\n")[0];
                }
                channelDescSB.append(desc);
                channelDescSB.append("\n");
            }
        }
        _channelNameText.setText(channelNamesSB.toString());
        _channelNameText.setData(channelNamesSB.toString());
        _ioNamesText.setText(ioNamesSB.toString());
        _ioNamesText.setData(ioNamesSB.toString());
        _channelsDescText.setText(channelDescSB.toString());
        _channelsDescText.setData(channelDescSB.toString());
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

        final Text text = new Text(topGroup, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        // TODO (hrickens) [02.05.2011]: Hier sollte bei jeder änderung der Werte Aktualisiert werden. (Momentan garnicht aber auch nicht nur beim Speichern)
        text.setText(_module.getConfigurationData());

        final Composite filterComposite = buildFilterComposite(topGroup);

        final Text filter = buildFilterText(filterComposite);
        final Button filterButton = buildFilterButton(filterComposite);
        final Button epicsEditButton = buildEditButton(topGroup);
        buildModuleTypList(comp, topGroup, filter, filterButton);
        epicsEditButton.addSelectionListener(new EditButtonSelectionListener(_moduleTypList));
        filterButton.addSelectionListener(new FilterButtonSelectionListener(_moduleTypList));
        filter.addModifyListener(new FilterModifyListener(_moduleTypList));
    }

    private void buildModuleTypList(@Nonnull final Composite comp,
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
                    .addSelectionChangedListener(new ModuleEditorSelectionChangedListenerForModuleTypeList(this, topGroup,
                                                                                                _moduleTypList));

            final GSDFileDBO gsdFile = getGsdFile();
            if (gsdFile != null) {
                final Map<Integer, GsdModuleModel2> gsdModuleList =
                                                              gsdFile.getParsedGsdFileModel()
                                                                      .getModuleMap();
                _moduleTypList.setInput(gsdModuleList);
                comp.layout();
                _moduleTypList.getTable().setData(_module.getModuleNumber());
                final GsdModuleModel2 selectModuleModel = gsdModuleList.get(_module.getModuleNumber());
                if (selectModuleModel != null) {
                    _moduleTypList.setSelection(new StructuredSelection(selectModuleModel));
                }
            }
            _moduleTypList.getTable().showSelection();
        } catch (final IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Module. GSD File read error", e2);
            LOG.error("Can't save Module. GSD File read error", e2);
        }
    }

    @Nonnull
    private Text buildFilterText(@Nonnull final Composite filterComposite) {
        final Text filter = new Text(filterComposite, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filter.setMessage("Module Filter");
        return filter;
    }

    @Nonnull
    private Composite buildFilterComposite(@Nonnull final Group topGroup) {
        final Composite filterComposite = new Composite(topGroup, SWT.NONE);
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        final GridLayout layout = new GridLayout(2, false);
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
    private Button buildFilterButton(@Nonnull final Composite filterComposite) {
        final Button filterButton = new Button(filterComposite, SWT.CHECK);
        filterButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        filterButton.setText("Only have prototype");
        return filterButton;
    }

    /**
     * @param topGroup
     */
    @Nonnull
    private Button buildEditButton(@Nonnull final Group topGroup) {
        final Button epicsEditButton = new Button(topGroup, SWT.PUSH);
        epicsEditButton.setText("Edit Prototype");
        return epicsEditButton;
    }

    private void setTypListFilter(@Nonnull final Text filter, @Nonnull final Button filterButton) {
        _moduleTypList.addFilter(new ViewerFilter() {

            @Override
            public boolean select(@Nullable final Viewer viewer,
                                  @Nullable final Object parentElement,
                                  @Nullable final Object element) {
                if(element instanceof GsdModuleModel2) {
                    final GsdModuleModel2 gsdModuleModel = (GsdModuleModel2) element;
                    if( filter.getText() == null || filter.getText().length() < 1) {
                        return true;
                    }
                    final String filterString = ".*" + filter.getText().replaceAll("\\*", ".*") + ".*";
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
                        final GsdModuleModel2 gmm = (GsdModuleModel2) element;
                        final int selectedModuleNo = gmm.getModuleNumber();
                        final GSDFileDBO gsdFile = getGsdFile();
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

    private void setTypeListSorter() {
        _moduleTypList.setSorter(new ViewerSorter() {

            @Override
            public int compare(@Nullable final Viewer viewer,
                               @Nullable final Object e1,
                               @Nullable final Object e2) {
                if( e1 instanceof GsdModuleModel2 && e2 instanceof GsdModuleModel2) {
                    final GsdModuleModel2 eUPD1 = (GsdModuleModel2) e1;
                    final GsdModuleModel2 eUPD2 = (GsdModuleModel2) e2;
                    return eUPD1.getModuleNumber() - eUPD2.getModuleNumber();
                }
                return super.compare(viewer, e1, e2);
            }

        });
    }

    private void buildNameGroup(@Nonnull final Composite comp) {
        final Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        gName.setLayout(new GridLayout(3, false));

        setNameWidget(new Text(gName, SWT.BORDER | SWT.SINGLE));
        final Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            setText(nameWidget, _module.getName(), 255);
            nameWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        }
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName,
                                                     _module,
                                                     getMLSB(),
                                                     "Sort Index",
                                                     getProfiBusTreeView(), 99));
    }

    /**
     * @param topGroup The parent Group for the CurrentUserParamData content.
     * @throws IOException
     */
    protected void makeCurrentUserParamData(@Nonnull final Group topGroup) throws IOException {
        if(_currentUserParamDataGroup != null) {
            _currentUserParamDataGroup.dispose();
        }
        // Current User Param Data Group
        _currentUserParamDataGroup = new Group(topGroup, SWT.NONE);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
        _currentUserParamDataGroup.setLayoutData(gd);
        _currentUserParamDataGroup.setLayout(new FillLayout());
        _currentUserParamDataGroup.setText("Current User Param Data:");
        final ScrolledComposite scrollComposite = new ScrolledComposite(_currentUserParamDataGroup,
                                                                        SWT.V_SCROLL);
        final Composite currentUserParamDataComposite = new Composite(scrollComposite, SWT.NONE);
        final RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.wrap = false;
        rowLayout.fill = true;
        currentUserParamDataComposite.setLayout(rowLayout);
        scrollComposite.setContent(currentUserParamDataComposite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        _currentUserParamDataGroup.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nullable final ControlEvent e) {
                final Rectangle r = scrollComposite.getClientArea();
                scrollComposite.setMinSize(scrollComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrollComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nullable final ControlEvent e) {
                final Rectangle r = scrollComposite.getClientArea();
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
        final Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            _module.setName(nameWidget.getText());
            nameWidget.setData(nameWidget.getText());
        }

        final Spinner indexSpinner = getIndexSpinner();
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
        } catch (final PersistenceException e) {
            LOG.error("Can't save Module! Database error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Module! Database error.", e);
        } catch (final IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Slave.GSD File read error", e2);
            LOG.error("Can't save Slave.GSD File read error", e2);
        }
    }

    private void updateChannels() throws PersistenceException {
        int i = 0;
        final String[] ionames = _ioNamesText.getText().split("\n");
        final String[] descs = _channelsDescText.getText().split("\n");
        final Collection<ChannelStructureDBO> channelStructs = _module.getChildrenAsMap().values();
        for (final ChannelStructureDBO channelStructure : channelStructs) {
            final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
            for (final ChannelDBO channel : channels) {
                if(i<ionames.length) {
                    channel.setIoName(ionames[i].trim());
                }
                if(i<descs.length) {
                    String descPost ="";
                    final String description = channel.getDescription();
                    if(description!=null) {
                        final int indexOf = description.indexOf("\r\n");
                        if(indexOf>=0) {
                            descPost = description.substring(indexOf);
                        }
                    }
                    channel.setDescription(descs[i].trim()+descPost);
                }
                channel.assembleEpicsAddressString();
                i++;
            }
        }
        _ioNamesText.setData(_ioNamesText.getText());
        _channelsDescText.setData(_channelsDescText.getText());
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
        _channelNameText.setText((String) _channelNameText.getData());
        _ioNamesText.setText((String) _ioNamesText.getData());
        _channelsDescText.setText((String) _channelsDescText.getData());
        for (final Object prmTextObject : _prmTextCV) {
            if(prmTextObject instanceof ComboViewer) {
                cancelComboViewer(prmTextObject);
            } else if(prmTextObject instanceof Text) {
                cancelText(prmTextObject);
            }
        }
        save();
    }

    public void cancelGsdModuleModel() {
        try {
            final GSDFileDBO gsdFile = _module.getGSDFile();
            if (gsdFile != null) {
                final GsdModuleModel2 gsdModuleModel = gsdFile.getParsedGsdFileModel()
                                                         .getModule((Integer) _moduleTypList
                                                                 .getTable().getData());
                if (gsdModuleModel != null) {
                    _moduleTypList.setSelection(new StructuredSelection(gsdModuleModel), true);
                }
            }
        } catch (final NullPointerException e) {
            _moduleTypList.getTable().select(0);
        }
    }

    /**
     *
     */
    public void cancelIndexSpinner() {
        final Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner != null) {
            indexSpinner.setSelection((Short) indexSpinner.getData());
        }
    }

    /**
     *
     */
    public void cancelNameWidget() {
        final Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            nameWidget.setText((String) nameWidget.getData());
        }
    }

    /**
     * @param prmTextObject
     */
    public final void cancelComboViewer(@Nonnull final Object prmTextObject) {
        final ComboViewer prmTextCV = (ComboViewer) prmTextObject;
        if(!prmTextCV.getCombo().isDisposed()) {
            final Integer index = (Integer) prmTextCV.getCombo().getData();
            if(index != null) {
                prmTextCV.getCombo().select(index);
            }
        }
    }

    /**
     * @param prmTextObject
     */
    public final void cancelText(@Nonnull final Object prmTextObject) {
        final Text prmText = (Text) prmTextObject;
        if(!prmText.isDisposed()) {
            final String value = (String) prmText.getData();
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
     */
    @Override
    public void setGsdFile(@CheckForNull final GSDFileDBO gsdFile) {
        _module.getSlave().setGSDFile(gsdFile);
    }

    /**
     * Open a Config-Dialog for {@link GSDModuleDBO} and create and store.
     *
     * @param model The {@link GsdModuleModel} Module Module from the GSD File.
     * @param gsdModule the {@link GSDModuleDBO} or null for a new one to configure .
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
        final String createdBy = getUserName();
        final Date date = new Date();
        if(gsdModuleDBO==null) {
            gsdModule.setCreationData(createdBy, date);
        } else {
            gsdModule.setUpdatedBy(createdBy);
            gsdModule.setUpdatedOn(date);
        }
        final ChannelConfigDialog channelConfigDialog = new ChannelConfigDialog(Display.getCurrent()
                .getActiveShell(), model, gsdModule);
        if(channelConfigDialog.open() == Window.OK) {
            return gsdModule;
        }
        return null;
    }

    /**
     * Have no Name Dialog.
     * {@inheritDoc}
     */
    @Override
    protected boolean newNode() {
        getNode().setCreationData(getUserName(), new Date());
        getNode().setVersion(-2);

        final Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer().getSelection())
                .getFirstElement();

        try {
            if( obj == null) {
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
            } else if(obj instanceof SlaveDBO) {
                final SlaveDBO nodeParent = (SlaveDBO) obj;
                getNode().moveSortIndex(nodeParent.getfirstFreeStationAddress());
                nodeParent.addChild(getNode());
            }
        } catch (final PersistenceException e) {
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
    void setPrmUserData(@Nonnull final Integer index, @Nonnull final Integer value) {
        _module.setConfigurationDataByte(index, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    Integer getPrmUserData(@Nonnull final Integer index) {
        if(_module.getConfigurationDataList().size() > index) {
            return _module.getConfigurationDataList().get(index);
        }
        return null;
    }
}
