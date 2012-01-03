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
        final String[] all = new String[ChannelPrototypConfigColumn.values().length];
        for (int i = 0; i < ChannelPrototypConfigColumn.values().length; i++) {
            all[i] = ChannelPrototypConfigColumn.values()[i].name();
        }
        return all;
    }
}
