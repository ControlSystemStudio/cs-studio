/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.GSDLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ShowFileSelectionListener;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.AbstractGsdPropertyModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.KeyValuePair;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmTextItem;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.04.2011
 */
public abstract class AbstractGsdNodeEditor extends AbstractNodeEditor {
    
    /**
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class ExtUserPrmDataContentProvider implements IStructuredContentProvider {
        /**
         * Constructor.
         */
        public ExtUserPrmDataContentProvider() {
            // Constructor.
        }
        
        @Override
        public void dispose() {
            // nothing to dispose
        }
        
        @Override
        @CheckForNull
        public Object[] getElements(@Nullable final Object inputElement) {
            if(inputElement instanceof ExtUserPrmData) {
                ExtUserPrmData eUPD = (ExtUserPrmData) inputElement;
                PrmText prmText = eUPD.getPrmText();
                if(prmText == null) {
                    PrmTextItem[] prmTextArray = new PrmTextItem[eUPD.getMaxValue()
                            - eUPD.getMinValue() + 1];
                    for (int i = eUPD.getMinValue(); i <= eUPD.getMaxValue(); i++) {
                        prmTextArray[i] = new PrmTextItem(Integer.toString(i), i);
                    }
                    return prmTextArray;
                }
                return prmText.getPrmTextItems().toArray();
            }
            return null;
        }
        
        @Override
        public void inputChanged(@Nullable final Viewer viewer,
                                 @Nullable final Object oldInput,
                                 @Nullable final Object newInput) {
            // nothing to do.
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileChangeListener implements ISelectionChangedListener {
        private final Button _fileSelect;
        
        /**
         * Constructor.
         * 
         * @param fileSelect
         */
        protected GSDFileChangeListener(@Nonnull final Button fileSelect) {
            _fileSelect = fileSelect;
        }
        
        @Override
        public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
            final StructuredSelection selection = (StructuredSelection) event.getSelection();
            if(selection == null || selection.isEmpty()) {
                _fileSelect.setEnabled(false);
                return;
            }
            final GSDFileDBO file = (GSDFileDBO) selection.getFirstElement();
            _fileSelect.setEnabled(getNode().needGSDFile() == GSDFileTypes.Master == file
                    .isMasterNonHN());
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileRemoveListener implements SelectionListener {
        private final TableViewer _tableViewer;
        
        /**
         * Constructor.
         * 
         * @param tableViewer
         */
        protected GSDFileRemoveListener(@Nonnull final TableViewer tableViewer) {
            _tableViewer = tableViewer;
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            // TODO:
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            final StructuredSelection selection = (StructuredSelection) _tableViewer.getSelection();
            final GSDFileDBO removeFile = (GSDFileDBO) selection.getFirstElement();
            
            if(removeFile != null) {
                if(MessageDialog.openQuestion(getShell(),
                                              "Lösche Datei aus der Datenbank",
                                              "Sind sie sicher das sie die Datei "
                                                      + removeFile.getName() + " löschen möchten")) {
                    try {
                        Repository.removeGSDFiles(removeFile);
                        List<GSDFileDBO> gsdFiles = getGsdFiles();
                        if(gsdFiles != null) {
                            gsdFiles.remove(removeFile);
                        }
                        _tableViewer.setInput(gsdFiles);
                    } catch (PersistenceException pE) {
                        DeviceDatabaseErrorDialog
                                .open(null, "Can't remove file from Database!", pE);
                        CentralLogger.getInstance().error(this, pE);
                    }
                }
            }
            
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileSelectionListener implements SelectionListener {
        private final TableViewer _tableViewer;
        private final Text _tSelected;
        
        /**
         * Constructor.
         * 
         * @param tableViewer
         * @param tSelected
         */
        protected GSDFileSelectionListener(@Nonnull final TableViewer tableViewer,
                                           @Nonnull final Text tSelected) {
            _tableViewer = tableViewer;
            _tSelected = tSelected;
        }
        
        private void doFileAdd() {
            try {
                setGsdFile((GSDFileDBO) ((StructuredSelection) _tableViewer.getSelection())
                        .getFirstElement());
                GSDFileDBO gsdFile = getGsdFile();
                if(gsdFile != null) {
                    fill(gsdFile);
                    _tSelected.setText(gsdFile.getName());
                    setSavebuttonEnabled("GSDFile", true);
                }
            } catch (PersistenceException e) {
                LOG.error(e);
                DeviceDatabaseErrorDialog.open(null, "Can't read GSDFile! Database error.", e);
            }
        }
        
        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            doFileAdd();
        }
        
        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            doFileAdd();
        }
    }
    
    /**
     * {@link PrmTextItem} Label provider that mark the default selection with a
     * '*'. The {@link ExtUserPrmData} give the default.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 18.10.2010
     */
    private final class PrmTextComboLabelProvider extends LabelProvider {
        
        private final ExtUserPrmData _extUserPrmData;
        
        /**
         * Constructor.
         * 
         * @param extUserPrmData
         */
        public PrmTextComboLabelProvider(@Nonnull ExtUserPrmData extUserPrmData) {
            _extUserPrmData = extUserPrmData;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getText(@Nullable Object element) {
            if(element instanceof PrmTextItem) {
                PrmTextItem prmText = (PrmTextItem) element;
                if(prmText.getIndex() == _extUserPrmData.getDefault()) {
                    return "*" + element.toString();
                }
            }
            return super.getText(element);
        }
    }
    
    /**
     * {@link PrmTextItem} Sorter
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class PrmTextViewerSorter extends ViewerSorter {
        /**
         * Constructor.
         */
        public PrmTextViewerSorter() {
            // Constructor.
        }
        
        @Override
        public int compare(@Nullable final Viewer viewer,
                           @Nullable final Object e1,
                           @Nullable final Object e2) {
            if( (e1 instanceof PrmTextItem) && (e2 instanceof PrmTextItem)) {
                PrmTextItem eUPD1 = (PrmTextItem) e1;
                PrmTextItem eUPD2 = (PrmTextItem) e2;
                return eUPD1.getIndex() - eUPD2.getIndex();
            }
            return super.compare(viewer, e1, e2);
        }
    }
    
    /**
    * @author hrickens
    * @author $Author: hrickens $
    * @since 14.06.2010
    */
    private final class ViewerSorterExtension extends ViewerSorter {
        
        public ViewerSorterExtension() {
            // default constructor
        }
        
        @Override
        public int compare(@Nullable final Viewer viewer,
                           @Nullable final Object e1,
                           @Nullable final Object e2) {
            if(e1 instanceof GSDFileDBO && e2 instanceof GSDFileDBO) {
                final GSDFileDBO file1 = (GSDFileDBO) e1;
                final GSDFileDBO file2 = (GSDFileDBO) e2;
                
                // sort wrong files to back.
                if(!(file1.isMasterNonHN() || file1.isSlaveNonHN())
                        && (file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                    return -1;
                } else if( (file1.isMasterNonHN() || file1.isSlaveNonHN())
                        && !(file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                    return 1;
                }
                
                // if master -> master file to top
                switch (getNode().needGSDFile()) {
                    case Master:
                        if(file1.isMasterNonHN() && !file2.isMasterNonHN()) {
                            return -1;
                        } else if(!file1.isMasterNonHN() && file2.isMasterNonHN()) {
                            return 1;
                        }
                        break;
                    case Slave:
                        // if slave -> slave file to top
                        if(file1.isSlaveNonHN() && !file2.isSlaveNonHN()) {
                            return -1;
                        } else if(!file1.isSlaveNonHN() && file2.isSlaveNonHN()) {
                            return 1;
                        }
                        break;
                    default:
                        // do nothing
                }
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
            return super.compare(viewer, e1, e2);
        }
    }
    
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AbstractGsdNodeEditor.class);
    
    private static void createGSDFileActions(@Nonnull final TableViewer viewer) {
        final Menu menu = new Menu(viewer.getControl());
        final MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowFileSelectionListener(viewer));
        showItem.setText("&Show");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_FOLDER));
        viewer.getTable().setMenu(menu);
    }
    
    private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();
    
    /**
     * @param currentUserParamDataComposite
     * @throws IOException
     */
    protected void buildCurrentUserPrmData(@Nonnull final Composite currentUserParamDataComposite) throws IOException {
        AbstractGsdPropertyModel parsedGsdFileModel = getGsdPropertyModel();
        if(parsedGsdFileModel != null) {
            Collection<KeyValuePair> extUserPrmDataRefMap = parsedGsdFileModel
                    .getExtUserPrmDataRefMap().values();
            for (KeyValuePair extUserPrmDataRef : extUserPrmDataRefMap) {
                ExtUserPrmData extUserPrmData = parsedGsdFileModel
                        .getExtUserPrmData(extUserPrmDataRef.getIntValue());
                Integer value = getUserPrmDataValue(extUserPrmDataRef, extUserPrmData);
                makeCurrentUserParamDataItem(currentUserParamDataComposite, extUserPrmData, value);
            }
        }
    }
    
    @SuppressWarnings("unused")
    private void createButtonArea(@Nonnull final TabFolder tabFolder,
                                  @Nonnull final Composite comp,
                                  @Nonnull final Text selectedText,
                                  @Nonnull final TableViewer gsdFileTableViewer) {
        new Label(comp, SWT.NONE);
        final Button fileSelect = new Button(comp, SWT.PUSH);
        fileSelect.setText("Select");
        fileSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileSelect.addSelectionListener(new GSDFileSelectionListener(gsdFileTableViewer,
                                                                     selectedText));
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        final Button fileAdd = new Button(comp, SWT.PUSH);
        fileAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileAdd.setText("Add File");
        fileAdd.addSelectionListener(new GSDFileAddListener(this,
                                                            tabFolder,
                                                            gsdFileTableViewer,
                                                            comp));
        final Button fileRemove = new Button(comp, SWT.PUSH);
        fileRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileRemove.setText("Remove File");
        fileRemove.addSelectionListener(new GSDFileRemoveListener(gsdFileTableViewer));
        
        gsdFileTableViewer.addSelectionChangedListener(new GSDFileChangeListener(fileSelect));
        
        new Label(comp, SWT.NONE);
    }
    
    /**
     * @param columnNum
     * @param comp
     * @return
     */
    @Nonnull
    private TableViewer createChooserArea(final int columnNum, @Nonnull final Composite comp) {
        final Group gAvailable = new Group(comp, SWT.NONE);
        gAvailable.setText("Available GSD File:");
        gAvailable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columnNum, 1));
        gAvailable.setLayout(new GridLayout(1, false));
        
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        final Composite tableComposite = new Composite(gAvailable, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);
        
        final TableViewer gsdFileTableViewer = new TableViewer(tableComposite, SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        gsdFileTableViewer.setContentProvider(new ArrayContentProvider());
        gsdFileTableViewer.setSorter(new ViewerSorterExtension());
        gsdFileTableViewer.setLabelProvider(new GSDLabelProvider(getNode().needGSDFile()));
        gsdFileTableViewer.getTable().setHeaderVisible(false);
        gsdFileTableViewer.getTable().setLinesVisible(false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(gsdFileTableViewer.getTable());
        
        try {
            List<GSDFileDBO> load = Repository.load(GSDFileDBO.class);
            setGsdFiles(load);
        } catch (PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, "Can't read GSDFiles from Database!", e);
            CentralLogger.getInstance().error(this, e);
        }
        List<GSDFileDBO> gsdFiles = getGsdFiles();
        if(gsdFiles == null) {
            setGsdFiles(new ArrayList<GSDFileDBO>());
        } else if(!gsdFiles.isEmpty()) {
            gsdFileTableViewer.setInput(gsdFiles.toArray(new GSDFileDBO[gsdFiles.size()]));
        }
        return gsdFileTableViewer;
    }
    
    /**
     * (@inheritDoc)
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        setParent(parent);
        setBackgroundComposite();
        if(getNode().needGSDFile() != GSDFileTypes.NONE) {
            makeGSDFileChooser(getTabFolder(), "GSD File List");
        }
        documents();
    }
    
    @Nonnull
    private Text createSelectionArea(final int columnNum, @Nonnull final Composite comp) {
        final Text tSelected;
        final Group gSelected = new Group(comp, SWT.NONE);
        gSelected.setText("Selected GSD File:");
        gSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, columnNum, 1));
        gSelected.setLayout(new GridLayout(1, false));
        
        tSelected = new Text(gSelected, SWT.SINGLE | SWT.BORDER);
        tSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GSDFileDBO gsdFile = getGsdFile();
        if(gsdFile != null) {
            try {
                setGsdFile(gsdFile);
            } catch (PersistenceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            tSelected.setText(gsdFile.getName());
        }
        return tSelected;
    }
    
    /**
     * Fill the View whit data from GSDFile.
     * 
     * @param gsdFile
     *            the GSDFile whit the data.
     * @throws PersistenceException 
     */
    public abstract void fill(@Nullable GSDFileDBO gsdFile) throws PersistenceException;
    
    @CheckForNull
    abstract AbstractGsdPropertyModel getGsdPropertyModel() throws IOException;
    
    @Nonnull
    abstract Integer getPrmUserData(@Nonnull Integer index);
    
    @Nonnull
    abstract List<Integer> getPrmUserDataList();
    
    int getUserPrmDataValue(@Nonnull KeyValuePair extUserPrmDataRef,
                            @Nonnull ExtUserPrmData extUserPrmData) {
        List<Integer> prmUserDataList = getPrmUserDataList();
        List<Integer> values = new ArrayList<Integer>();
        values.add(prmUserDataList.get(extUserPrmDataRef.getIndex()));
        int maxBit = extUserPrmData.getMaxBit();
        if( (maxBit > 7) && (maxBit < 16)) {
            values.add(prmUserDataList.get(extUserPrmDataRef.getIndex() + 1));
        }
        int val = getValueFromBitMask(extUserPrmData, values);
        return val;
    }
    
    private int getValueFromBitMask(@Nonnull final ExtUserPrmData ranges,
                                    @Nonnull final List<Integer> values) {
        // TODO (hrickens) [21.04.2011]: Muss refactort werde da der gleiche code auch in setValue2BitMask() verwendent wird.      
        int lowByte = 0;
        int highByte = 0;
        if(values.size() > 0) {
            lowByte = values.get(0);
        }
        if(values.size() > 1) {
            highByte = values.get(1);
        }
        int val = highByte * 256 + lowByte;
        int minBit = ranges.getMinBit();
        int maxBit = ranges.getMaxBit();
        if(maxBit < minBit) {
            minBit = ranges.getMaxBit();
            maxBit = ranges.getMinBit();
        }
        int mask = ((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
        val = (val & mask) >> ranges.getMinBit();
        return val;
    }
    
    private void handleComboViewer(@Nonnull final ComboViewer prmTextCV,
                                   @Nonnull final Integer byteIndex) throws IOException {
        if(!prmTextCV.getCombo().isDisposed()) {
            ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
            StructuredSelection selection = (StructuredSelection) prmTextCV.getSelection();
            Integer bitValue = ((PrmTextItem) selection.getFirstElement()).getIndex();
            setValue2BitMask(extUserPrmData, byteIndex, bitValue);
            Integer indexOf = prmTextCV.getCombo().indexOf(selection.getFirstElement().toString());
            prmTextCV.getCombo().setData(indexOf);
        }
    }
    
    @Nonnull
    private void handleText(@Nonnull final Text prmText, @Nonnull Integer byteIndex) {
        if(!prmText.isDisposed()) {
            Object data = prmText.getData("ExtUserPrmData");
            if(data instanceof ExtUserPrmData) {
                ExtUserPrmData extUserPrmData = (ExtUserPrmData) data;
                
                String value = prmText.getText();
                Integer bitValue;
                if(value == null || value.isEmpty()) {
                    bitValue = extUserPrmData.getDefault();
                } else {
                    bitValue = Integer.parseInt(value);
                }
                setValue2BitMask(extUserPrmData, byteIndex, bitValue);
                prmText.setData(bitValue);
            }
        }
    }
    
    /**
     * 
     * @param parent
     *            the Parent Composite.
     * @param value
     *            the Selected currentUserParamData Value.
     * @param extUserPrmData
     * @param prmText
     * @return a ComboView for are currentUserParamData Property
     */
    @Nonnull
    ComboViewer makeComboViewer(@Nonnull final Composite parent,
                                @Nullable final Integer value,
                                @Nonnull final ExtUserPrmData extUserPrmData,
                                @CheckForNull final PrmText prmText) {
        Integer localValue = value;
        
        ComboViewer prmTextCV = new ComboViewer(parent);
        RowData data = new RowData();
        data.exclude = false;
        prmTextCV.getCombo().setLayoutData(data);
        prmTextCV.setLabelProvider(new PrmTextComboLabelProvider(extUserPrmData));
        prmTextCV.setContentProvider(new ExtUserPrmDataContentProvider());
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new PrmTextViewerSorter());
        
        if(localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmTextCV.setInput(extUserPrmData);
        
        if(prmText != null) {
            PrmTextItem prmTextItem = prmText.getPrmTextItem(localValue);
            if(prmTextItem != null) {
                prmTextCV.setSelection(new StructuredSelection(prmTextItem));
            } else {
                prmTextCV.getCombo().select(0);
            }
        } else {
            prmTextCV.getCombo().select(localValue);
        }
        prmTextCV.getCombo().setData(prmTextCV.getCombo().getSelectionIndex());
        return prmTextCV;
    }
    
    @SuppressWarnings("unused")
    private void makeCurrentUserParamDataItem(@Nonnull final Composite currentUserParamDataGroup,
                                              @Nullable final ExtUserPrmData extUserPrmData,
                                              @Nullable final Integer value) {
        PrmText prmText = null;
        
        Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);
        
        if(extUserPrmData != null) {
            text.setText(extUserPrmData.getText() + ":");
            prmText = extUserPrmData.getPrmText();
            if( (prmText == null || prmText.isEmpty())
                    && (extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10)) {
                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
            } else {
                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup,
                                               value,
                                               extUserPrmData,
                                               prmText));
            }
        }
        new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
    }
    
    /**
     * 
     * @param tabFolder
     *            The Tab Folder to add the Tab Item.
     * @param head
     *            Headline for the Tab.
     * @return Tab Item Composite.
     */
    @Nonnull
    private Composite makeGSDFileChooser(@Nonnull final TabFolder tabFolder,
                                         @Nonnull final String head) {
        final int columnNum = 7;
        final Composite comp = ConfigHelper.getNewTabItem(head, tabFolder, columnNum, 520, 200);
        
        final Text selectedText = createSelectionArea(columnNum, comp);
        
        final TableViewer gsdFileTableViewer = createChooserArea(columnNum, comp);
        
        createButtonArea(tabFolder, comp, selectedText, gsdFileTableViewer);
        
        createGSDFileActions(gsdFileTableViewer);
        
        return comp;
        
    }
    
    /**
     * 
     * @param currentUserParamDataGroup
     * @param value
     * @param extUserPrmData
     * @return
     */
    @Nonnull
    Text makeTextField(@Nonnull final Composite currentUserParamDataGroup,
                       @CheckForNull final Integer value,
                       @Nonnull final ExtUserPrmData extUserPrmData) {
        Integer localValue = value;
        Text prmText = new Text(currentUserParamDataGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
        Formatter f = new Formatter();
        f.format("Min: %d, Min: %d Default: %d",
                 extUserPrmData.getMinValue(),
                 extUserPrmData.getMaxValue(),
                 extUserPrmData.getDefault());
        prmText.setToolTipText(f.toString());
        prmText.setTextLimit(Integer.toString(extUserPrmData.getMaxValue()).length());
        
        if(localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmText.setText(localValue.toString());
        prmText.setData(localValue.toString());
        prmText.setData("ExtUserPrmData", extUserPrmData);
        
        prmText.addModifyListener(getMLSB());
        prmText.addVerifyListener(new VerifyListener() {
            
            @Override
            public void verifyText(@Nonnull final VerifyEvent e) {
                if(e.text.matches("^\\D+$")) {
                    e.doit = false;
                }
            }
            
        });
        return prmText;
    }
    
    /**
     * @throws IOException 
    *
    */
    protected void saveUserPrmData() throws IOException {
        AbstractGsdPropertyModel gsdPropertyModel = getGsdPropertyModel();
        if(gsdPropertyModel != null) {
            Collection<KeyValuePair> extUserPrmDataRefMap = gsdPropertyModel
                    .getExtUserPrmDataRefMap().values();
            
            if(extUserPrmDataRefMap.size() == _prmTextCV.size()) {
                int i = 0;
                for (KeyValuePair ref : extUserPrmDataRefMap) {
                    Object prmTextObject = _prmTextCV.get(i);
                    if(prmTextObject instanceof ComboViewer) {
                        ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                        handleComboViewer(prmTextCV, ref.getIndex());
                    } else if(prmTextObject instanceof Text) {
                        Text prmText = (Text) prmTextObject;
                        handleText(prmText, ref.getIndex());
                    }
                    i++;
                }
                
            }
        }
    }
    
    abstract void setPrmUserData(@Nonnull Integer index, @Nonnull Integer value);
    
    /**
     * Change the a value on the Bit places, that is given from the input, to
     * the bitValue.
     * 
     * @param extUserPrmData
     *            give the start and end Bit position.
     * @param bitValue
     *            the new Value for the given Bit position.
     * @param value
     *            the value was changed.
     * @return the changed value.
     */
    @Nonnull
    private void setValue2BitMask(@Nonnull final ExtUserPrmData extUserPrmData,
                                  @Nonnull final Integer byteIndex,
                                  @Nonnull final Integer bitValue) {
        // TODO (hrickens) [21.04.2011]: Muss refactort werde da der gleiche code auch in AbstractGsdPropertyModel#setExtUserPrmDataValue verwendent wird.
        int val = bitValue;
        int minBit = extUserPrmData.getMinBit();
        int maxBit = extUserPrmData.getMaxBit();
        if(maxBit < minBit) {
            minBit = extUserPrmData.getMaxBit();
            maxBit = extUserPrmData.getMinBit();
        }
        int mask = ~((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
        if( (maxBit > 7) && (maxBit < 16)) {
            int modifyByteHigh = 0;
            int modifyByteLow = 0;
            modifyByteHigh = getPrmUserData(byteIndex);
            modifyByteLow = getPrmUserData(byteIndex + 1);
            
            int parseInt = modifyByteHigh * 256 + modifyByteLow;
            val = val << (minBit);
            int result = (parseInt & mask) | (val);
            modifyByteLow = result % 256;
            modifyByteHigh = (result - modifyByteLow) / 256;
            setPrmUserData(byteIndex + 1, modifyByteHigh);
            setPrmUserData(byteIndex, modifyByteLow);
        } else {
            int modifyByte = getPrmUserData(byteIndex);
            val = val << (minBit);
            int result = (modifyByte & mask) | (val);
            setPrmUserData(byteIndex, result);
        }
    }
    
}
