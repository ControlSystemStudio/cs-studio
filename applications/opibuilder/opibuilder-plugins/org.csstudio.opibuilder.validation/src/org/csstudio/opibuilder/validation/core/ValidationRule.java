/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

/**
 *
 * <code>ValidationRule</code> defines the possible rules how to treat properties: read/write, read only, obligatory
 * write.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum ValidationRule {
    RO, RW, WRITE, DEPRECATED
}
