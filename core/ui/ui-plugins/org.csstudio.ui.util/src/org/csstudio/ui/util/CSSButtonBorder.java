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
        public static ButtonScheme BUTTON_SCROLLBAR = ButtonBorder.SCHEMES.BUTTON_SCROLLBAR;
        public static ButtonScheme BUTTON_CONTRAST = ButtonBorder.SCHEMES.BUTTON_CONTRAST;
    }
}
