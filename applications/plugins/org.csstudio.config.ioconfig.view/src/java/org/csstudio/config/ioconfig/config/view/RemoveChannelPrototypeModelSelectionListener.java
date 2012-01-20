package org.csstudio.config.ioconfig.config.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 03.06.2009
 */
final class RemoveChannelPrototypeModelSelectionListener implements SelectionListener {
    private final ArrayList<ModuleChannelPrototypeDBO> _outChannelPrototypeModelList;
    private final ArrayList<ModuleChannelPrototypeDBO> _inChannelPrototypeModelList;
    private final GSDModuleDBO _gsdModule2Remove;
    private final TableViewer _iTableViewer;
    private final TableViewer _oTableViewer;
    private final TabFolder _rslIoTabFolder;

    /**
     * Constructor.
     */
    public RemoveChannelPrototypeModelSelectionListener(@Nonnull final GSDModuleDBO gsdModule, @Nonnull final ArrayList<ModuleChannelPrototypeDBO> outputList, @Nonnull final TableViewer outputTableViewer,
                                   @Nonnull final ArrayList<ModuleChannelPrototypeDBO> inputList, @Nonnull final TableViewer inputTableViewer, @Nonnull final TabFolder ioTabFolder) {
        _gsdModule2Remove = gsdModule;
        _outChannelPrototypeModelList = outputList;
        _inChannelPrototypeModelList = inputList;
        _iTableViewer = inputTableViewer;
        _oTableViewer = outputTableViewer;
        _rslIoTabFolder = ioTabFolder;
    }

    @Override
    public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
        removeItem();
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        removeItem();
    }

    private void remove(@Nonnull final TableViewer tableViewer, @Nonnull final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList, @Nonnull final GSDModuleDBO gsdModule2Remove) {
        IStructuredSelection selection;
        selection = (IStructuredSelection) tableViewer.getSelection();
        if(selection.size() > 0) {
            @SuppressWarnings("unchecked")
            final
            List<ModuleChannelPrototypeDBO> list = selection.toList();
            channelPrototypeModelList.removeAll(list);
            gsdModule2Remove.removeModuleChannelPrototype(list);
            for (final Object object : list) {
                if(object instanceof DBClass) {
                    final DBClass dbClass = (DBClass) object;
                    removeNode(dbClass);
                }
            }
        } else {
            final ModuleChannelPrototypeDBO remove = channelPrototypeModelList
            .remove(channelPrototypeModelList.size() - 1);
            removeNode(remove);
        }
        tableViewer.refresh();

    }

    private void removeItem() {
        if(_rslIoTabFolder.getSelection()[0].getText().equals(Messages.ChannelConfigDialog_Input)) {
            remove(_iTableViewer, _inChannelPrototypeModelList, _gsdModule2Remove);
        } else {
            remove(_oTableViewer, _outChannelPrototypeModelList, _gsdModule2Remove);
        }
    }

    /**
     * @param node
     */
    private void removeNode(@Nonnull final DBClass node) {
        try {
            Repository.removeNode(node);
        } catch (final PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, Messages.ChannelConfigDialog_CantRemove, e);
            ChannelConfigDialog.LOG.error(Messages.ChannelConfigDialog_CantRemove, e);
        }
    }
}
