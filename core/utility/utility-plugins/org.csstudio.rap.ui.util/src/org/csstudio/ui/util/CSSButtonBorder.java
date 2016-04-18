/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.ui.util;

import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.ButtonBorder.ButtonScheme;

/**
 * <code>CSSButtonBorder</code> provide a single source access to the real ButtonBorder, which for some reason is
 * not same for RAP and RCP. RAP implements access to borders as methods, RCP does it with with fields.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class CSSButtonBorder {
    public static class SCHEMES {
        public static ButtonScheme BUTTON_SCROLLBAR = ButtonBorder.SCHEMES.BUTTON_SCROLLBAR();
        public static ButtonScheme BUTTON_CONTRAST = ButtonBorder.SCHEMES.BUTTON_CONTRAST();
    }
}
