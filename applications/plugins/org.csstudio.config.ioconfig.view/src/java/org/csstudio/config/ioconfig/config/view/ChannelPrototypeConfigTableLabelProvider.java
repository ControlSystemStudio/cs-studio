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
/*
 * $Id: ChannelPrototypeConfigTableLabelProvider.java,v 1.1 2009/08/26 07:09:23 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 18.12.2008
 */
public class ChannelPrototypeConfigTableLabelProvider implements ITableLabelProvider {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(@Nullable final ILabelProviderListener listener) {
        // don't use Listener
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // not to dispose
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Image getColumnImage(@Nullable final Object element, final int columnIndex) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    @Override
    @CheckForNull
    public String getColumnText(@Nullable final Object element, final int columnIndex) {
        if (element instanceof ModuleChannelPrototypeDBO) {
            final ModuleChannelPrototypeDBO prototype = (ModuleChannelPrototypeDBO) element;
            
            final ChannelPrototypConfigColumn[] values = new ChannelPrototypConfigColumn[] {
                                                                                            ChannelPrototypConfigColumn.OFFSET, ChannelPrototypConfigColumn.NAME,
                                                                                            ChannelPrototypConfigColumn.TYPE, ChannelPrototypConfigColumn.SIZE,
                                                                                            ChannelPrototypConfigColumn.STRUCT, ChannelPrototypConfigColumn.STATUS,
                                                                                            ChannelPrototypConfigColumn.MIN, ChannelPrototypConfigColumn.MAX,
                                                                                            ChannelPrototypConfigColumn.ORDER, };
            if (columnIndex < values.length) {
                final ChannelPrototypConfigColumn column = values[columnIndex];
                switch (column) {
                    case OFFSET:
                        return Integer.toString(prototype.getOffset());
                    case NAME:
                        return prototype.getName();
                    case TYPE:
                        return prototype.getType().toString();
                    case SIZE:
                        return prototype.getType().getSize();
                    case SHIFT:
                    case STATUS:
                        return getStatus(prototype);
                    case IO:
                        return getIO(prototype);
                    case STRUCT:
                        return getStruct(prototype);
                    case MIN:
                        return getMinimum(prototype);
                    case MAX:
                        return getMaximum(prototype);
                    case ORDER:
                        return getOrder(prototype);
                    default:
                        return null;
                }
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLabelProperty(@Nullable final Object element, @Nullable final String property) {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(@Nullable final ILabelProviderListener listener) {
        // don't use Listener
    }
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getIO(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        if (prototype.isInput()) {
            return "Input";
        }
        return "Output";
    }
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getMaximum(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        final Integer maximum = prototype.getMaximum();
        if(maximum==null) {
            return "";
        }
        return maximum.toString();
    }
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getMinimum(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        final Integer minimum = prototype.getMinimum();
        if(minimum==null) {
            return "";
        }
        return minimum.toString();
    }
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getOrder(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        final Integer byteOrdering = prototype.getByteOrdering();
        if(byteOrdering==null||byteOrdering<1) {
            return "";
        }
        return Integer.toString(byteOrdering);
    }
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getStatus(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        if (prototype.getShift() < 0) {
            return "";
        }
        return Integer.toString(prototype.getShift());
    }
    // CHECKSTYLE ON: CyclomaticComplexity
    
    /**
     * @param prototype
     * @return
     */
    @Nonnull
    private String getStruct(@Nonnull final ModuleChannelPrototypeDBO prototype) {
        if (prototype.isStructure()) {
            return "yes";
        }
        return "no";
    }
}
