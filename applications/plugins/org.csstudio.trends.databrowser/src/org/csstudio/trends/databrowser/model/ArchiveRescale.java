package org.csstudio.trends.databrowser.model;

/** How to handle newly received archived data?
 *  @author Kay Kasemir
 */
public enum ArchiveRescale
{
    /** Keep display as is */
    NONE,
    /** Perform auto-zoom */
    AUTOZOOM,
    /** Perform stagger */
    STAGGER
}
