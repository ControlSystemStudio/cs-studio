package org.csstudio.config.ioconfig.config.view;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Rickens Helge
 * @author $Author: $
 * @since 14.12.2010
 */
public class OverviewLabelProvider implements ITableLabelProvider, IColorProvider {
    
    @Override
    public void addListener(@Nullable final ILabelProviderListener listener) {
        // Don't use Listener
    }
    
    @Override
    public void dispose() {
        // Nothing to dispose
    }
    
    @Override
    @CheckForNull
    public Color getBackground(@Nullable final Object element) {
        if (element instanceof ModuleDBO) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        }
        return null;
    }
    
    @Override
    @CheckForNull
    public Image getColumnImage(@Nullable final Object element, final int columnIndex) {
        return null;
    }
    
    @Override
    @CheckForNull
    public String getColumnText(@Nullable final Object element, final int columnIndex) {
        if (element instanceof ChannelDBO) {
            final ChannelDBO channel = (ChannelDBO) element;
            getChannelColumnText(channel, columnIndex);
        }
        if (element instanceof ModuleDBO) {
            final ModuleDBO module = (ModuleDBO) element;
            return getModuleColumnText(module, columnIndex);
            
        }
        return null;
    }
    
    @Override
    @CheckForNull
    public Color getForeground(@Nullable final Object element) {
        return null;
    }
    
    @Override
    public final boolean isLabelProperty(@Nullable final Object element,@Nullable final String property) {
        return false;
    }
    
    @Override
    public void removeListener(@Nullable final ILabelProviderListener listener) {
        // Don't use Listener
        
    }
    
    /**
     * @param element
     * @param columnIndex
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    @CheckForNull
    private String getChannelColumnText(@Nonnull final ChannelDBO channel, final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "  ";
            case 1:
                try {
                    return channel.getFullChannelNumber()+"";
                } catch (final PersistenceException e) {
                    return "DB Error!";
                }
            case 2:
                return channel.getName();
            case 3:
                return channel.getIoName();
            case 4:
                return channel.getEpicsAddressString();
            case 5:
                return channel.getDescription();
            case 6:
                return channel.getChannelType().name();
            case 7:
                return Integer.toString(channel.getId());
            default:
                return null;
        }
    }
    // CHECKSTYLE On: CyclomaticComplexity
    
    /**
     * @param element
     * @param columnIndex
     * @return
     */
    @CheckForNull
    private String getModuleColumnText(@Nonnull final ModuleDBO module, final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return module.getSortIndex()+"";
            case 2:
                return module.getName();
            default:
                return null;
        }
    }
    
}
