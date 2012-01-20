package org.csstudio.config.ioconfig.config.view;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 03.06.2009
 */
final class AddChannelPrototypeModelSelectionListener implements SelectionListener {
    private final ChannelConfigDialog _channelConfigDialog;
    private final ArrayList<ModuleChannelPrototypeDBO> _outChannelPrototypeModelList;
    private final ArrayList<ModuleChannelPrototypeDBO> _inChannelPrototypeModelList;
    private final GSDModuleDBO _gsdMod;
    private final TableViewer _outputTableViewer;
    private final TableViewer _inputTableViewer;

    /**
     * Constructor.
     */
    public AddChannelPrototypeModelSelectionListener(@Nonnull final ChannelConfigDialog channelConfigDialog,
                                                     @Nonnull final GSDModuleDBO gsdModule,
                                                     @Nonnull final ArrayList<ModuleChannelPrototypeDBO> outputList,
                                                     @Nonnull final ArrayList<ModuleChannelPrototypeDBO> inputList,
                                                     @Nonnull final TableViewer outputTableViewer,
                                                     @Nonnull final TableViewer inputTableViewer) {
        _channelConfigDialog = channelConfigDialog;
        _gsdMod = gsdModule;
        _outChannelPrototypeModelList = outputList;
        _inChannelPrototypeModelList = inputList;
        _outputTableViewer = outputTableViewer;
        _inputTableViewer = inputTableViewer;
    }

    @Override
    public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
        addItem();
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        addItem();
    }

    private void addItem() {
        final Button button = _channelConfigDialog.getOkButton();
        button.setEnabled(true);
        DataType type;
        if(_channelConfigDialog.isWord()) {
            type = DataType.UINT16;
        } else {
            type = DataType.UINT8;
        }
        final ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        final String user = AbstractNodeEditor.getUserName();
        final Date date = new Date();
        moduleChannelPrototype.setCreationData(user, date);
        moduleChannelPrototype.setName(""); //$NON-NLS-1$

        moduleChannelPrototype.setGSDModule(_gsdMod);
        if(_channelConfigDialog.isInputSelected()) {
            add2InputTab(type, moduleChannelPrototype);
        } else {
            add2OutputTab(type, moduleChannelPrototype);
        }
    }

    /**
     * @param type
     * @param moduleChannelPrototype
     */
    protected void add2InputTab(@Nonnull final DataType type,
                                @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        int offset = 0;
        DataType tmpType = type;
        ModuleChannelPrototypeDBO lastModuleChannelPrototypeModel;
        if(!_inChannelPrototypeModelList.isEmpty()) {
            lastModuleChannelPrototypeModel = _inChannelPrototypeModelList
            .get(_inChannelPrototypeModelList.size() - 1);
            offset = lastModuleChannelPrototypeModel.getOffset();
            offset += lastModuleChannelPrototypeModel.getSize();
            tmpType = lastModuleChannelPrototypeModel.getType();
        }
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setType(tmpType);
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setGSDModule(_gsdMod);
        _gsdMod.addModuleChannelPrototype(moduleChannelPrototype);
        _inChannelPrototypeModelList.add(moduleChannelPrototype);
        _inputTableViewer.refresh();
    }

    /**
     * @param type
     * @param moduleChannelPrototype
     */
    protected void add2OutputTab(@Nonnull final DataType type,
                                 @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        int offset = 0;
        DataType tmpType = type;
        ModuleChannelPrototypeDBO lastModuleChannelPrototypeModel;
        if(!_outChannelPrototypeModelList.isEmpty()) {
            lastModuleChannelPrototypeModel = _outChannelPrototypeModelList
            .get(_outChannelPrototypeModelList.size() - 1);
            offset = lastModuleChannelPrototypeModel.getOffset();
            offset += lastModuleChannelPrototypeModel.getSize();
            tmpType = lastModuleChannelPrototypeModel.getType();
        }
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setType(tmpType);
        moduleChannelPrototype.setInput(false);
        _gsdMod.addModuleChannelPrototype(moduleChannelPrototype);
        _outChannelPrototypeModelList.add(moduleChannelPrototype);
        _outputTableViewer.refresh();
    }

}
