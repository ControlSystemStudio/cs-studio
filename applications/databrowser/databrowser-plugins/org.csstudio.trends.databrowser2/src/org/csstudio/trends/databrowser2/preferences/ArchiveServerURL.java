/*******************************************************************************
 * Copyright (c) 2013 Cosylab d.d.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.preferences;

/**
 * Private class which represents one archive server.
 *
 * @author Takashi Nakamoto
 */
public class ArchiveServerURL {
    private String url;
    private String alias;

    public ArchiveServerURL(String url, String alias) {
        this.url = url;
        this.alias = alias;
    }

    public String getURL() {
        return url;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        if (alias != null && alias.length() != 0) {
            return alias + " - " + url;
        } else {
            return url;
        }
    }
}
