/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

/**
 * Gives the pages that the webserver is serving.
 * @author Dominic Oram
 *
 */
public enum Page {
    MAIN,
    CHANNEL,
    CHANNEL_LIST,
    DISCONNECTED,
    ENVIRONMENT,
    GROUP,
    GROUPS,
    RESTART,
    STOP,
    RESET,
    DEBUG
}
