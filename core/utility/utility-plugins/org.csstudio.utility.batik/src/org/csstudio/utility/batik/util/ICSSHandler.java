/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import org.eclipse.swt.graphics.Color;

/**
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public interface ICSSHandler {

    public void updateCSSColor(Color colorToChange, Color newColor);

    public void resetCSSStyle();

}
