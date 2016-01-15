/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface SymbolImageListener {

    public void symbolImageLoaded();

    public void repaintRequested();

    public void sizeChanged();

}
