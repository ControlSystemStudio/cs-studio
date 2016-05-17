/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.ui.fx.util;

/**
 *
 * <code>Credentials</code> is a wrapper for username and password.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Credentials {

    private final String username;
    private final char[] password;
    private final boolean remember;

    /**
     * Constructs new credentials from pieces.
     *
     * @param username the username
     * @param password the password
     * @param remember a hint whether the received should store these credentials or not
     */
    public Credentials(String username, char[] password, boolean remember) {
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * @return true if the receiver should remember credentials or false otherwise
     */
    public boolean isRemember() {
        return remember;
    }
}
