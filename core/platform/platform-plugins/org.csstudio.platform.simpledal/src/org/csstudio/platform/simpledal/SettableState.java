package org.csstudio.platform.simpledal;

/**
 * Enumeration that contain the possible write access levels for control system
 * channels.
 *
 * @author Sven Wende
 *
 */
public enum SettableState {
    /**
     * Write access is permitted.
     */
    SETTABLE,

    /**
     * Write access is not permitted.
     */
    NOT_SETTABLE,

    /**
     * Unknown.
     */
    UNKNOWN;
}
