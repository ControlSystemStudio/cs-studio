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
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof ModuleChannelPrototypeDBO) {
            ModuleChannelPrototypeDBO prototype = (ModuleChannelPrototypeDBO) element;

            ChannelPrototypConfigColumn[] values = new ChannelPrototypConfigColumn[] { ChannelPrototypConfigColumn.OFFSET, ChannelPrototypConfigColumn.NAME, ChannelPrototypConfigColumn.TYPE,
                    ChannelPrototypConfigColumn.SIZE, ChannelPrototypConfigColumn.STRUCT , ChannelPrototypConfigColumn.STATUS, ChannelPrototypConfigColumn.MIN,ChannelPrototypConfigColumn.MAX,ChannelPrototypConfigColumn.ORDER };
            if (columnIndex < values.length) {
                ChannelPrototypConfigColumn column = values[columnIndex];
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
                        if (prototype.getShift() < 0) {
                            return "";
                        }
                        return Integer.toString(prototype.getShift());
                    case IO:
                        if (prototype.isInput()) {
                            return "Input";
                        }
                        return "Output";
                    case STRUCT:
                        if (prototype.isStructure()) {
                            return "yes";
                        }
                        return "no";
                    case MIN:
                        Integer minimum = prototype.getMinimum();
                        if(minimum==null) {
                            return "";
                        }
                        return minimum.toString();
                    case MAX:
                        Integer maximum = prototype.getMaximum();
                        if(maximum==null) {
                            return "";
                        }
                        return maximum.toString();
                    case ORDER:
                        Integer byteOrdering = prototype.getByteOrdering();
                        if(byteOrdering==null||byteOrdering<1) {
                            return "";
                        }
                        return Integer.toString(byteOrdering);
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
    public void addListener(final ILabelProviderListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLabelProperty(final Object element, final String property) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final ILabelProviderListener listener) {

    }
}
