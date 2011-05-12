/**
 *
 */
package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author hrickens
 *
 */
public class GSDLabelProvider extends LabelProvider implements IFontProvider, IColorProvider {
    
    private final GSDFileTypes _gSDFileType;
    private final Color _gray = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    private final Color _red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    
    /**
     *
     * @param gSDFileType is only true if preferred master GSD file to be displayed.
     */
    public GSDLabelProvider(@Nonnull final GSDFileTypes gSDFileType) {
        _gSDFileType = gSDFileType;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Font getFont(@Nullable final Object element) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Color getBackground(@Nullable final Object element) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Color getForeground(@Nullable final Object element) {
        if (element instanceof GSDFileDBO) {
            GSDFileDBO file = (GSDFileDBO) element;
            if ( ( (_gSDFileType == GSDFileTypes.Master) && file.isMasterNonHN())
                    || ( (_gSDFileType == GSDFileTypes.Slave) && file.isSlaveNonHN())) {
                return null;
            }
            if (file.isMasterNonHN() || file.isSlaveNonHN()) {
                return _gray;
            }
        }
        return _red;
    }
    
}
