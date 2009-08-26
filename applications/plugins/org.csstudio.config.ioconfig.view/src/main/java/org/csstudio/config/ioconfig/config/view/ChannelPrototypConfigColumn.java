package org.csstudio.config.ioconfig.config.view;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
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
    private ChannelPrototypConfigColumn(final String text) {
        _text = text;
    }

    /**
     * 
     * @return the String representation oft the column.
     */
    public String getText() {
        return _text;
    }

    /**
     * 
     * @return The String representation oft <b>all</b> column.
     */
    public static String[] getStringValues() {
        String[] all = new String[ChannelPrototypConfigColumn.values().length];
        for (int i = 0; i < ChannelPrototypConfigColumn.values().length; i++) {
            all[i] = ChannelPrototypConfigColumn.values()[i].name();
        }
        return all;
    }
}
