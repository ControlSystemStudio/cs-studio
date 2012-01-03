/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.config.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgDataBuilder;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 22.09.2008
 */
public class ChannelConfigDialog extends Dialog implements IHasDocumentableObject {

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 06.07.2011
     */
    private static final class PaintListenerImplementation implements PaintListener {
        private final int _leftUperCorner;
        private final int _size;
        private final SlaveCfgData _slaveCfgData;

        /**
         * Constructor.
         */
        PaintListenerImplementation(@Nonnull final SlaveCfgData slaveCfgData,
                                    final int leftUperCorner,
                                    final int size) {
            _slaveCfgData = slaveCfgData;
            _leftUperCorner = leftUperCorner;
            _size = size;
        }

        @Override
        public void paintControl(@Nonnull final PaintEvent e) {
            final int x0 = 0;
            final int x1 = _size * _slaveCfgData.getWordSize();
            e.gc.drawRectangle(x0, _leftUperCorner, x1, _size);
            e.gc.drawRectangle(x0, _leftUperCorner + _size, x1, _size);
            final String type = Messages.ChannelConfigDialog_AD;
            final Point stringExtent = e.gc.stringExtent(type);
            e.gc.drawString(type, (x1 - stringExtent.x) / 2, _leftUperCorner, true);
            for (int j = 1; j <= _slaveCfgData.getWordSize(); j++) {
                final int x2 = x0 + j * _size;
                e.gc.drawLine(x2, _leftUperCorner + _size, x2, _leftUperCorner + 2 * _size);
            }
        }
    }
    protected static final Logger LOG = LoggerFactory.getLogger(ChannelConfigDialog.class);
    private static int _DIRTY;
    /**
     * The configuration Tables for the input and outputChannels.
     */
    private TableViewer _inputTableViewer;
    private TableViewer _outputTableViewer;

    private final ArrayList<ModuleChannelPrototypeDBO> _inputChannelPrototypeModelList;
    private final ArrayList<ModuleChannelPrototypeDBO> _outputChannelPrototypeModelList;
    private final GsdModuleModel2 _moduleModel;
    private final GSDModuleDBO _gsdModule;
    /**
     * The Tab folder for the I/O Configuration Tabel's.
     */
    private TabFolder _ioTabFolder;

    private boolean _haveInputFields;
    private boolean _haveOutputFields;

    /**
     * If the data length of prototype word.
     */
    private boolean _isWordSize = true;

    private DocumentationManageView _documentationManageView;

    public ChannelConfigDialog(@Nullable final Shell parentShell,
                               @Nonnull final GsdModuleModel2 gsdModuleModel,
                               @Nonnull final GSDModuleDBO gsdModule) {
        super(parentShell);
        setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE);
        _moduleModel = gsdModuleModel;
        _gsdModule = gsdModule;
        _inputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        _outputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        for (final ModuleChannelPrototypeDBO moduleChannelPrototype : _gsdModule
                .getModuleChannelPrototypeNH()) {
            if (moduleChannelPrototype.isInput()) {
                _inputChannelPrototypeModelList.add(moduleChannelPrototype);
            } else {
                _outputChannelPrototypeModelList.add(moduleChannelPrototype);
            }
        }
    }

    public void closeAllCellEditors(@CheckForNull final TableViewer tableViewer) {
        if(tableViewer != null) {
            tableViewer.getTable().setFocus();
            // finish last edit
            try {
                for (final CellEditor editor : tableViewer.getCellEditors()) {
                    if(editor != null) {
                        editor.deactivate();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createGraphicalDataStructurePresentation(final int size,
                                                         final int leftUperCorner,
                                                         @Nonnull final SlaveCfgData slaveCfgData,
                                                         @Nonnull final Composite box) {
        for (int i = 0; i < slaveCfgData.getNumber(); i++) {
            final Canvas canvas = new Canvas(box, SWT.NONE);
            final int horizSpan = slaveCfgData.getWordSize() / 8;
            final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, horizSpan, 1);
            gridData.widthHint = size * slaveCfgData.getWordSize() + 15;
            gridData.heightHint = 2 * size + 5;
            canvas.setLayoutData(gridData);
            final PaintListenerImplementation listener;
            listener = new PaintListenerImplementation(slaveCfgData, leftUperCorner, size);
            canvas.addPaintListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return _gsdModule;
    }

    @Nonnull
    public GSDModuleDBO getGsdModule() {
        return _gsdModule;
    }

    /**
     *
     */
    public void setEmptyChannelPrototypeName2Unused() {
        final Set<ModuleChannelPrototypeDBO> moduleChannelPrototype;
        moduleChannelPrototype =  _gsdModule.getModuleChannelPrototype();
        if (moduleChannelPrototype != null) {
            for (final ModuleChannelPrototypeDBO moduleChannelPrototypeDBO : moduleChannelPrototype) {
                String name = moduleChannelPrototypeDBO.getName();
                if(name == null || name.isEmpty()) {
                    name = "unused"; //$NON-NLS-1$
                    moduleChannelPrototypeDBO.setName(name);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSavebuttonEnabled(@Nullable final String event, final boolean enabled) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSaveButtonSaved() {
        // nothing to do
    }

    private void createDocumetView(@Nonnull final TabItem item) {
        final String head = Messages.ChannelConfigDialog_Documents;
        item.setText(head);
        _documentationManageView = new DocumentationManageView(_ioTabFolder, SWT.NONE, this);
        item.setControl(_documentationManageView);
        _ioTabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            private void docTabSelectionAction(@Nonnull final SelectionEvent e) {
                if(e.item.equals(item)) {
                    _documentationManageView.onActivate();
                }
            }

        });
    }

    /**
     *
     * @param infoDialogArea
     */
    private void createInfo(@Nonnull final Composite infoDialogArea) {
        final int size = 12;
        final int leftUperCorner = 0;

        final List<SlaveCfgData> slaveCfgDataList = new SlaveCfgDataBuilder(_moduleModel.getValue())
        .getSlaveCfgDataList();

        final Composite info = new Composite(infoDialogArea, SWT.NONE);
        info.setLayout(new GridLayout(4, true));
        info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        final TabFolder tabFolder = new TabFolder(info, SWT.TOP);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

        for (final SlaveCfgData slaveCfgData : slaveCfgDataList) {
            final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
            tabItem.setText("Module " + slaveCfgData.getParameterAsHexString());
            final Composite box = new Composite(tabFolder, SWT.NONE);
            box.setLayout(new GridLayout(4, true));
            box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

            String dataFormat;
            if(slaveCfgData.isWordSize()) {
                _isWordSize &= true;
                dataFormat = "Word's: "; //$NON-NLS-1$
            } else {
                _isWordSize &= false;
                dataFormat = "Byte's: "; //$NON-NLS-1$
            }
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Count + dataFormat + slaveCfgData.getNumber());
            _haveInputFields |= slaveCfgData.isInput();
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Input_ + slaveCfgData.isInput());
            _haveOutputFields = slaveCfgData.isOutput();
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Output_ + slaveCfgData.isOutput());
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Parameter_ + slaveCfgData.getParameterAsHexString());

            createGraphicalDataStructurePresentation(size, leftUperCorner, slaveCfgData, box);
            tabItem.setControl(box);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createButtonsForButtonBar(@Nonnull final Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns = 2;
        ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
        GridData data;
        GridLayout gridLayout;

        // Button Left side
        final Composite left = new Composite(parent, SWT.NONE);
        data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
        data.grabExcessHorizontalSpace = true;
        left.setLayoutData(data);
        gridLayout = new GridLayout(0, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        left.setLayout(gridLayout);
        final Button addButton = createButton(left, IDialogConstants.NEXT_ID, Messages.ChannelConfigDialog_Add, false);
        addButton
                .addSelectionListener(new AddChannelPrototypeModelSelectionListener(this,
                                                                                    _gsdModule,
                                                                                    _outputChannelPrototypeModelList,
                                                                                    _inputChannelPrototypeModelList,
                                                                                    _outputTableViewer,
                                                                                    _inputTableViewer));
        final Button removeButton = createButton(left,
                                                 IDialogConstants.BACK_ID,
                                                 Messages.ChannelConfigDialog_Remove,
                                                 false);
        final RemoveChannelPrototypeModelSelectionListener rsListener;
        rsListener = new RemoveChannelPrototypeModelSelectionListener(_gsdModule,
                                                                      _outputChannelPrototypeModelList,
                                                                      _outputTableViewer,
                                                                      _inputChannelPrototypeModelList,
                                                                      _inputTableViewer,
                                                                      _ioTabFolder);
        removeButton.addSelectionListener(rsListener);

        // Button Left side
        final Composite right = new Composite(parent, SWT.NONE);
        data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
        right.setLayoutData(data);
        gridLayout = new GridLayout(0, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        right.setLayout(gridLayout);
        super.createButtonsForButtonBar(right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected final Control createDialogArea(@Nonnull final Composite parent) {
        getShell().setText(Messages.ChannelConfigDialog_Module + _moduleModel.getName());
        final Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
        createInfo(dialogAreaComposite);
        _ioTabFolder = new TabFolder(dialogAreaComposite, SWT.TOP);
        _ioTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        if(_haveInputFields) {
            final TabItem inputTabItem = new TabItem(_ioTabFolder, SWT.NONE);
            inputTabItem.setText(Messages.ChannelConfigDialog_Input);
            _inputTableViewer = createChannelTable(_ioTabFolder, _inputChannelPrototypeModelList);
            inputTabItem.setControl(_inputTableViewer.getTable());
        }
        if(_haveOutputFields) {
            final TabItem outputTabItem = new TabItem(_ioTabFolder, SWT.NONE);
            outputTabItem.setText(Messages.ChannelConfigDialog_Output);
            _outputTableViewer = createChannelTable(_ioTabFolder, _outputChannelPrototypeModelList);
            outputTabItem.setControl(_outputTableViewer.getTable());
        }

        createDocumetView(new TabItem(_ioTabFolder, SWT.NONE));
        parent.layout();
        return dialogAreaComposite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void okPressed() {
        closeAllCellEditors(_inputTableViewer);
        closeAllCellEditors(_outputTableViewer);
        setEmptyChannelPrototypeName2Unused();
        _gsdModule.setDocuments(_documentationManageView.getDocuments());
        try {
            _gsdModule.save();
        } catch (final PersistenceException e) {
            e.printStackTrace();
            DeviceDatabaseErrorDialog.open(null, "The Settings not saved!\n\nDataBase Failure:", e);
        }
        super.okPressed();
    }

    @Nonnull
    public static CellEditor buildIntegerEdior(@Nonnull final Table table,
                                               @Nullable final ICellEditorValidator cellEditorValidator) {
        final TextCellEditor editor = new TextCellEditor(table);
        editor.setValidator(cellEditorValidator);
        editor.activate();
        return editor;
    }

    public static void buildTableCellEditors(@Nonnull final TableViewer tableViewer) {
        final ICellEditorValidator cellEditorValidator = new ICellEditorValidator() {

            @Override
            @CheckForNull
            public String isValid(@Nullable final Object value) {
                if(value instanceof String) {
                    final String stringValue = (String) value;
                    try {
                        Integer.parseInt(stringValue);
                        return null;
                    } catch (final Exception e) {
                        return Messages.ChannelConfigDialog_ErrorNoInt;
                    }
                }
                return Messages.ChannelConfigDialog_ErrorNoString;
            }

        };
        final Table table = tableViewer.getTable();
        final CellEditor[] editors = new CellEditor[9];
        // Offset
        editors[0] = buildIntegerEdior(table, cellEditorValidator);
        editors[1] = buildNameEditor(table);
        // Type
        editors[2] = new ComboBoxCellEditor(table, DataType.getNames(), SWT.DROP_DOWN
                                            | SWT.READ_ONLY);
        editors[2].activate();
        // Size isn't to edit
        editors[3] = null;
        // Structure
        editors[4] = new CheckboxCellEditor(table, SWT.CHECK);
        editors[4].activate();
        // Status //ehemals Shift
        editors[5] = buildIntegerEdior(table, cellEditorValidator);
        // MIN
        editors[6] = buildIntegerEdior(table, cellEditorValidator);
        // MAX
        editors[7] = buildIntegerEdior(table, cellEditorValidator);
        // Byte Order
        editors[8] = buildIntegerEdior(table, cellEditorValidator);

        tableViewer.setCellEditors(editors);
    }

    /**
     * @param table The parent table for the new column.
     * @param style the style of control to construct.
     * @param width Sets the new width of the receiver.
     * @param header the new column header text.
     */
    private static void addTableColumn(@Nonnull final Table table,
                                       final int style,
                                       final int width,
                                       @Nonnull final String header) {
        final TableColumn tc = new TableColumn(table, style);
        tc.setText(header);
        tc.setResizable(true);
        tc.setWidth(width);
    }

    @Nonnull
    private static CellEditor buildNameEditor(@Nonnull final Table table) {
        final TextCellEditor editor = new TextCellEditor(table);
        editor.activate();
        editor.addPropertyChangeListener(new IPropertyChangeListener() {

            @Override
            public void propertyChange(@Nonnull final PropertyChangeEvent event) {
                final String oldValue = (String) event.getOldValue();
                final String newValue = (String) event.getNewValue();
                if( isNoOldValueAndNewValue(oldValue, newValue)) {
                    ChannelConfigDialog.dirtyPlus();
                } else if( isOldValueAndNoNewValue(oldValue, newValue)) {
                    ChannelConfigDialog.dirtyMinus();
                }
            }

            private boolean isOldValueAndNoNewValue(@Nonnull final String oldValue, @Nonnull final String newValue) {
                return oldValue != null && oldValue.length() > 0
                        && (newValue == null || newValue.length() < 1);
            }

            private boolean isNoOldValueAndNewValue(@CheckForNull final String oldValue, @CheckForNull final String newValue) {
                return (oldValue == null || oldValue.length() == 0) && newValue != null
                        && newValue.length() > 0;
            }

        });
        return editor;
    }

    @Nonnull
    private static TableViewer createChannelTable(@Nonnull final Composite tableParent,
                                                  @Nullable final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList) {
        final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
        | SWT.HIDE_SELECTION;

        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd.minimumHeight = 100;
        final Table table = new Table(tableParent, style);
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.OFFSET.getText());
        addTableColumn(table, SWT.LEFT, 120, ChannelPrototypConfigColumn.NAME.getText());
        addTableColumn(table, SWT.LEFT, 75, ChannelPrototypConfigColumn.TYPE.getText());
        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.SIZE.getText());
        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.STRUCT.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.STATUS.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.MIN.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.MAX.getText());
        addTableColumn(table, SWT.LEFT, 55, ChannelPrototypConfigColumn.ORDER.getText());
        final TableViewer tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new ChannelPrototypeConfigTableLabelProvider());
        tableViewer.setContentProvider(new ChannelTableContentProvider());
        tableViewer.setColumnProperties(ChannelPrototypConfigColumn.getStringValues());
        buildTableCellEditors(tableViewer);
        tableViewer.setCellModifier(new ChannelConfigCellModifier(tableViewer));
        tableViewer.setInput(channelPrototypeModelList);
        return tableViewer;
    }

    protected static void dirtyMinus() {
        ChannelConfigDialog._DIRTY++;
    }

    protected static void dirtyPlus() {
        ChannelConfigDialog._DIRTY--;
    }

    @Nonnull
    protected Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }

    public boolean isWord() {
        return _isWordSize;
    }

    public boolean isInputSelected() {
        return _ioTabFolder.getSelection()[0].getText().equals(Messages.ChannelConfigDialog_Input);
    }
}
