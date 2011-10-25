package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

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
final class ModuleEditorSelectionChangedListenerForModuleTypeList implements
        ISelectionChangedListener {
    private final ModuleEditor _moduleEditor;
    private final Group _topGroup;
    private final TableViewer _mTypList;

    ModuleEditorSelectionChangedListenerForModuleTypeList(@Nonnull final ModuleEditor moduleEditor, @Nonnull final Group topGroup,
                                               @Nonnull final TableViewer moduleTypList) {
        _moduleEditor = moduleEditor;
        _topGroup = topGroup;
        _mTypList = moduleTypList;
    }

    @Override
    public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
        final GsdModuleModel2 selectedModule = (GsdModuleModel2) ((StructuredSelection) _mTypList
                .getSelection()).getFirstElement();

        if(ifSameModule(selectedModule)) {
            return;
        }

        final int selectedModuleNo = selectedModule.getModuleNumber();
        final int savedModuleNo = (Integer) _mTypList.getTable().getData();
        final boolean hasChanged = savedModuleNo != selectedModuleNo;
        final ModuleDBO module = _moduleEditor.getNode();
        try {
            final String createdBy = AbstractNodeEditor.getUserName();
            GSDModuleDBO gsdModule;
            try {
                module.setNewModel(selectedModuleNo, createdBy);
                gsdModule = module.getGSDModule();
            } catch (final IllegalArgumentException iea) {
                // Unknown Module (--> Config the Epics Part)
                gsdModule = createNewModulePrototype(selectedModule, selectedModuleNo, module);
                if(gsdModule==null) {
                    return;
                }
            }
            final Text nameWidget = _moduleEditor.getNameWidget();
            if(nameWidget != null) {
                nameWidget.setText(gsdModule.getName());
            }
        } catch (final PersistenceException e1) {
            _moduleEditor.openErrorDialog(e1, _moduleEditor.getProfiBusTreeView());
            ModuleEditor.LOG.error("Database error!", e1);
        }
        _moduleEditor.setSavebuttonEnabled("ModuleTyp", hasChanged);
        try {
            _moduleEditor.makeCurrentUserParamData(_topGroup);
        } catch (final IOException e) {
            ModuleEditor.LOG.error("File read error!", e);
            DeviceDatabaseErrorDialog.open(null, "File read error!", e);
        }
        _moduleEditor.getProfiBusTreeView().refresh(module.getParent());
    }

    @CheckForNull
    public GSDModuleDBO createNewModulePrototype(@Nonnull final GsdModuleModel2 selectedModule,
                                                 final int selectedModuleNo,
                                                 @Nonnull final ModuleDBO module) throws PersistenceException {
        GSDModuleDBO gsdModule;
        gsdModule = _moduleEditor.openChannelConfigDialog(selectedModule, null);
        if(gsdModule == null) {
            return null;
        }
        gsdModule.setModuleId(selectedModuleNo);
        final GSDFileDBO gsdFile = module.getGSDFile();
        if(gsdFile != null) {
            gsdFile.addGSDModule(gsdModule);
        }
        gsdModule.save();
        return gsdModule;
    }

    private boolean ifSameModule(@Nullable final GsdModuleModel2 selectedModule) {
        final ModuleDBO module = _moduleEditor.getNode();
        return selectedModule == null || module == null || module
                .getGSDModule() != null && module.getGSDModule().getModuleId() == selectedModule
                .getModuleNumber();
    }
}
