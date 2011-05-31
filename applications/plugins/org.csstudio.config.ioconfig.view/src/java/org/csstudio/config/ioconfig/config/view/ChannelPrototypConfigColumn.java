package org.csstudio.config.ioconfig.config.view;

import javax.annotation.Nonnull;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 25.11.2008
 */
enum ChannelPrototypConfigColumn {
    /**
     * The Offset column.
     */
    OFFSET("Offset"),
    /**
     * The Name column.
     */
    NAME("Name"),
    /**
     * The Type column.
     */
    TYPE("Type"),
    /**
     * The Size column.
     */
    SIZE("Size"),
    /**
     * The Input/Output column.
     */
    STRUCT("Struct"),
    /**
     * The Input/Output column.
     */
    STATUS("Status"),
    MIN("Min"),
    MAX("Max"),
    ORDER("Byte Order"),
    /**
     * The Shift column.
     */
    IO("I/O"),
    SHIFT("Shift");

    /**
     * The String representation oft the column.
     */
    private String _text;

    /**
     * Constructor.
     * 
     * @param text
     *            The String representation oft the column.
     */
    private ChannelPrototypConfigColumn(@Nonnull final String text) {
        _text = text;
    }

    /**
     * 
     * @return the String representation oft the column.
     */
    @Nonnull
    public String getText() {
        return _text;
    }

    /**
     * 
     * @return The String representation oft <b>all</b> column.
     */
    @Nonnull
    public static String[] getStringValues() {
        String[] all = new String[ChannelPrototypConfigColumn.values().length];
        for (int i = 0; i < ChannelPrototypConfigColumn.values().length; i++) {
            all[i] = ChannelPrototypConfigColumn.values()[i].name();
        }
        return all;
    }
}
