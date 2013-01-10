/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carcassi
 */
public class FontUtil {
    
    private static Font liberationSansRegular = loadFont("LiberationSans-Regular.ttf");
    
    private static Font loadFont(String name) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, HorizontalAxisRenderer.class.getResourceAsStream("LiberationSans-Regular.ttf"));
            return font.deriveFont(Font.PLAIN, 10);
        } catch (FontFormatException ex) {
            Logger.getLogger(HorizontalAxisRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HorizontalAxisRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("Couldn't load");
    }

    public static Font getLiberationSansRegular() {
        return liberationSansRegular;
    }
    
}
