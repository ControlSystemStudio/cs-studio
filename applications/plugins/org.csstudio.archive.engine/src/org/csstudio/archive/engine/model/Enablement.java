package org.csstudio.archive.engine.model;

/** Describes how a channel acts on a group.
 *  @author Kay Kasemir
 */
public enum Enablement
{
    /** Channel is simply part of the group, but doesn't act on the group */
    Passive,
    /** Channel enables archiving of the group when non-zero */
    Enabling,
    /** Channel disables archiving of the group when non-zero */
    Disabling
}
