/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

/**
 * Engine model states.
 *
 * @author Kay Kasemir
 * @author bknerr
 * @since 24.08.2011
 */
public enum EngineState {
    /** Initial model state before it has been configured */
    IDLE,
    /** Configured model state before <code>start()</code> */
    CONFIGURED,
    /** Running model, state after <code>start()</code> */
    RUNNING,
    /** EngineState after <code>requestStop()</code>; still running. */
    SHUTDOWN_REQUESTED,
    /** EngineState after <code>requestRestart()</code>; still running. */
    RESTART_REQUESTED,
    /** EngineState while in <code>stop()</code>; will then be IDLE again. */
    STOPPING
}
