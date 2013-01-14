/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.sds.cosyrules.color;

import java.util.Arrays;

import org.csstudio.sds.model.IRule;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: $
 * @version $Revision: 1.5 $
 * @since 14.09.2010
 */
public class FontWidthRule implements IRule {
    private static final Logger LOG = LoggerFactory.getLogger(FontWidthRule.class);

    private static final int MIN_FONT_SIZE = 7;
    private static final String DEFAULT_FONT = "Arial, 10";
    private String _lastText = "";
//    private String _lastFont;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        LOG.error("Arguments: "+Arrays.toString(arguments));
        if (arguments.length >= 4) {
            String text = "100.00";
            if (arguments[0] instanceof String) {
                text = (String) arguments[0];
            } else if (arguments[0] instanceof Number) {
                text = ((Number) arguments[0]).toString();
            }

            LOG.error("\ttext.length(): "+text.length()+"\t_lastText.length():"+_lastText.length());
                LOG.error("Calculate Size from "+text);
                _lastText = text;
                return calulateSize(arguments, text);
        }
        LOG.error("Default: "+DEFAULT_FONT);
        return DEFAULT_FONT;
    }

    /**
     * @param arguments
     * @param Text
     * @return
     */
    private String calulateSize(final Object[] arguments, final String Text) {
        int width;
        int height;
        if (arguments[1] instanceof String) {
            try {
                width = Integer.parseInt((String) arguments[1]);
            } catch (NumberFormatException e) {
                width = 8;
            }
        } else if (arguments[2] instanceof Number) {
            width = ((Number) arguments[2]).intValue();
        } else {
            return DEFAULT_FONT;
        }

        if (arguments[2] instanceof String) {
            try {
                height = Integer.parseInt((String) arguments[2]);
            } catch (NumberFormatException e) {
                height = 8;
            }
        } else if (arguments[2] instanceof Number) {
            height = ((Number) arguments[2]).intValue();
        } else {
            return DEFAULT_FONT;
        }

        String font = (String) ( ( (arguments[3] != null) && (arguments[3] instanceof String) && ( ((String) arguments[3])
                .length() > 0)) ? arguments[3] : "Arial");

        return calulateFontSize(Text, height, width, font);
    }

    /**
     * @return
     *
     */
    private String calulateFontSize(final String text,
                                    final int maxHeight,
                                    final int maxWidth,
                                    final String font) {
        LOG.error("Text :"+text+"\tmaxHeight :"+maxHeight+"\tmaxWidth :"+maxWidth+"\tfont :"+font);
        int fontSize = (int) Math.ceil(maxHeight * 0.6);
        if (fontSize < MIN_FONT_SIZE) {
            fontSize = 10;
        }

        if ( (text != null) && (text.length() > 0)) {
            TextLayout tl = new TextLayout(null);
            tl.setText(text);
            FontData fd = new FontData(font, fontSize, 0);
            Font f = new Font(null, fd);
            tl.setFont(f);
            LOG.error("Text: "+text+"\twidth:  "+tl.getBounds().width+"\theight: "+tl.getBounds().height+"\tfontSize: "+fontSize);
            while ( ((maxWidth -2 < tl.getBounds().width) || (maxHeight < tl.getBounds().height)) && (fontSize > MIN_FONT_SIZE)) {
                fontSize--;
                f.dispose();
                fd = new FontData(font, fontSize, 0);
                f = new Font(null, fd);
                tl.setFont(f);
                LOG.error("Text: "+text+"\twidth:  "+tl.getBounds().width+"\theight: "+tl.getBounds().height+"\tfontSize: "+fontSize);
            }
            tl.dispose();
        }
        return font + ", " + fontSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        String desc = "Gibt für den angegebenen Text den Font mit der Maximalen größe zurück ";
        return desc;
    }

}
