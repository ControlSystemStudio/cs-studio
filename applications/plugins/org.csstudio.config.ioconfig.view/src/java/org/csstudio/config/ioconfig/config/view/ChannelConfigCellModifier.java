package org.csstudio.config.ioconfig.config.view;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 13.05.2009
 */
final class ChannelConfigCellModifier implements ICellModifier {
    private final TableViewer _tableViewer;

    ChannelConfigCellModifier(@Nonnull final TableViewer tableViewer) {
        _tableViewer = tableViewer;
    }

    // CHECKSTYLE OFF: CyclomaticComplexity
    @Override
    public boolean canModify(@Nullable final Object element, @Nonnull final String property) {
        final ChannelPrototypConfigColumn column = ChannelPrototypConfigColumn.valueOf(property);
        switch (column) {
            case OFFSET:
            case NAME:
            case TYPE:
            case SHIFT:
            case STRUCT:
            case STATUS:
            case MIN:
            case MAX:
            case ORDER:
                return true;
            case IO:
            case SIZE:
            default:
                return false;
        }
    }
    // CHECKSTYLE ON: CyclomaticComplexity

    // CHECKSTYLE OFF: CyclomaticComplexity
    @Override
    @CheckForNull
    public Object getValue(@Nonnull final Object element, @Nonnull final String property) {
        Object result = null;
        final ModuleChannelPrototypeDBO channel = (ModuleChannelPrototypeDBO) element;

        switch (ChannelPrototypConfigColumn.valueOf(property)) {
            case OFFSET:
                result = channel.getOffset() + ""; //$NON-NLS-1$
                break;
            case NAME:
                result = channel.getName() == null ? "" : channel.getName(); //$NON-NLS-1$
                break;
            case TYPE:
                result = channel.getType().ordinal();
                break;
            case SHIFT:
                result = channel.getShift() + ""; //$NON-NLS-1$
                break;
            case IO:
                result = channel.isInput();
                break;
            case STRUCT:
                result = channel.isStructure();
                break;
            case STATUS:
                result = channel.getShift() + ""; //$NON-NLS-1$
                break;
            case MIN:
                result = channel.getMinimum() == null ? "" : Integer.toString(channel //$NON-NLS-1$
                                                                              .getMinimum());
                break;
            case MAX:
                result = channel.getMaximum() == null ? "" : Integer.toString(channel //$NON-NLS-1$
                                                                              .getMaximum());
                break;
            case ORDER:
                final Integer byteOrdering = channel.getByteOrdering();
                result = byteOrdering == null ? "" : Integer.toString(byteOrdering);//$NON-NLS-1$
                break;
            default:
                break;
        }
        return result;
    }

    // CHECKSTYLE OFF: CyclomaticComplexity
    @Override
    public void modify(@Nonnull final Object element,
                       @Nonnull final String property,
                       @Nonnull final Object value) {
        ModuleChannelPrototypeDBO channel;
        if(element instanceof Item) {
            channel = (ModuleChannelPrototypeDBO) ((Item) element).getData();
        } else {
            channel = (ModuleChannelPrototypeDBO) element;
        }

        switch (ChannelPrototypConfigColumn.valueOf(property)) {
            case OFFSET:
                modifyOffset(value, channel);
                break;
            case NAME:
                channel.setName((String) value);
                break;
            case TYPE:
                modifyType(value, channel);
                break;
            case SHIFT:
                modifyShift(value, channel);
                break;
            case STRUCT:
                modifyStruct(value, channel);
                break;
            case STATUS:
                modifyShift(value, channel);
                break;
            case MIN:
                modifyMin(value, channel);
                break;
            case MAX:
                modifyMax(value, channel);
                break;
            case ORDER:
                modifyOrder(value, channel);
                break;
            default:
                break;
        }

        _tableViewer.refresh(channel);
    }

    public void modifyMax(@Nullable final Object value,
                          @Nonnull final ModuleChannelPrototypeDBO channel) {
        Integer max = null;
        if(value instanceof String) {
            max = Integer.parseInt((String) value);
        } else if(value instanceof Integer) {
            max = (Integer) value;
        }
        channel.setMaximum(max);
    }

    public void modifyMin(@Nullable final Object value,
                          @Nonnull final ModuleChannelPrototypeDBO channel) {
        Integer min = null;
        if(value instanceof String) {
            min = Integer.parseInt((String) value);
        } else if(value instanceof Integer) {
            min = (Integer) value;
        }
        channel.setMinimum(min);
    }

    public void modifyOffset(@Nullable final Object value,
                             @Nonnull final ModuleChannelPrototypeDBO channel) {
        int offset = 0;
        if(value instanceof String) {
            try {
                offset = Integer.parseInt((String) value);
            } catch (final NumberFormatException nfe) {
                offset = 0;
            }
        } else if(value instanceof Integer) {
            offset = (Integer) value;
        }
        channel.setOffset(offset);
    }

    public void modifyOrder(@Nullable final Object value,
                            @Nonnull final ModuleChannelPrototypeDBO channel) {
        Integer order = null;
        if(value instanceof String) {
            order = Integer.parseInt((String) value);
        } else if(value instanceof Integer) {
            order = (Integer) value;
        }
        channel.setByteOrdering(order);
    }

    public void modifyShift(@Nullable final Object value,
                            @Nonnull final ModuleChannelPrototypeDBO channel) {
        int shift = 0;
        if(value instanceof String) {
            shift = Integer.parseInt((String) value);
        } else if(value instanceof Integer) {
            shift = (Integer) value;
        }
        channel.setShift(shift);
    }

    public void modifyStruct(@Nullable final Object value,
                             @Nonnull final ModuleChannelPrototypeDBO channel) {
        if(value instanceof String) {
            final String io = (String) value;
            channel.setStructure("yes".equals(io)); //$NON-NLS-1$
        } else if(value instanceof Boolean) {
            channel.setStructure((Boolean) value);
        }
    }

    public void modifyType(@Nullable final Object value,
                           @Nonnull final ModuleChannelPrototypeDBO channel) {
        DataType dt = DataType.BIT;
        if(value instanceof String) {
            dt = DataType.valueOf((String) value);
        } else if(value instanceof Integer) {
            final Integer pos = (Integer) value;
            if(pos < DataType.values().length) {
                dt = DataType.values()[pos];
            }
        }
        channel.setType(dt);
    }
}
