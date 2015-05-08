package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven Wende (C1 WPS), Kai Meyer (C1 WPS)
 *
 */
public class ColorAndFontService implements IColorAndFontService {
    private static final Logger LOG = LoggerFactory.getLogger(ColorAndFontService.class);

    private static RGB FALLBACK_COLOR = new RGB(255, 0, 0);
    private static FontData FALLBACK_FONT = new FontData("Arial", 8, SWT.None);

    private IFile _file;
    private long _currentTimeStamp = -1;
    private AbstractColorAndFontHandler _colorAndFontSaxHandler;

    public ColorAndFontService(IFile file, AbstractColorAndFontHandler handler) {
        assert file != null : "file != null";
        assert handler != null : "handler != null";

        _file = file;
        _colorAndFontSaxHandler = handler;
    }

    private void checkForUpdate() {
        long modificationStamp = _file.getModificationStamp();
        if (_currentTimeStamp > modificationStamp) {
            _colorAndFontSaxHandler.reset();
            _currentTimeStamp = modificationStamp;
        } else if (_currentTimeStamp < modificationStamp) {
            InputStream inputStream = null;
            try {
                File path = _file.getLocation().toFile();
                inputStream = new FileInputStream(path);
                SAXParser parser = SAXParserFactory.newInstance()
                        .newSAXParser();

                parser.parse(inputStream, _colorAndFontSaxHandler);
                _currentTimeStamp = modificationStamp;
            } catch (Exception e) {
                LOG.debug(e.toString());
            } finally {
                closeStream(inputStream);
            }
        }
    }

    private void closeStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.debug(e.toString());
            }
        }
    }

    private NamedStyle getSelectedStyle() {
        String styleName = SdsUiPlugin.getCorePreferenceStore().getString(
                PreferenceConstants.PROP_SELECTED_COLOR_AND_FONT_STYLE);
        return _colorAndFontSaxHandler.getStyle(styleName);
    }

    public Font getFont(String fontOrVariable) {
        FontData fd = null;

        if (ColorAndFontUtil.isVariable(fontOrVariable)) {
            checkForUpdate();
            NamedStyle selectedStyle = getSelectedStyle();
            if (selectedStyle != null) {
                NamedFont nf = selectedStyle.getFont(fontOrVariable);

                if (nf != null) {
                    fd = nf.getFontData();
                }
            }
        } else {
            fd = toFontData(fontOrVariable);
        }

        // .. fallback
        if (fd == null) {
            fd = FALLBACK_FONT;
        }

        assert fd != null;

        return CustomMediaFactory.getInstance().getFont(fd);
    }

    public Color getColor(String hexOrVariable) {
        assert hexOrVariable != null;

        RGB rgb = null;

        if (ColorAndFontUtil.isVariable(hexOrVariable)) {
            checkForUpdate();
            NamedStyle selectedStyle = getSelectedStyle();
            if (selectedStyle != null) {
                NamedColor namedColor = selectedStyle.getColor(hexOrVariable);
                rgb = namedColor != null ? namedColor.getRgb() : FALLBACK_COLOR;
            }
        } else if (ColorAndFontUtil.isHex(hexOrVariable)) {
            rgb = toRgb(hexOrVariable);
        }

        // .. fallback
        if (rgb == null) {
            rgb = FALLBACK_COLOR;
        }

        assert rgb != null;

        return CustomMediaFactory.getInstance().getColor(rgb);
    }

    public List<NamedColor> listAvailableColors() {
        checkForUpdate();
        NamedStyle selectedStyle = getSelectedStyle();
        if (selectedStyle != null) {
            return selectedStyle.listAllColors();
        } else {
            return Collections.emptyList();
        }
    }

    public List<NamedFont> listAvailableFonts() {
        checkForUpdate();
        NamedStyle selectedStyle = getSelectedStyle();
        if (selectedStyle != null) {
            return selectedStyle.listAllFonts();
        } else {
            return Collections.emptyList();
        }
    }

    public List<NamedStyle> getStyles() {
        checkForUpdate();
        return _colorAndFontSaxHandler.getStyles();
    }

    private FontData toFontData(String font) {
        assert font != null;

        String[] tmp = font.split(",");

        String fontName = "Arial";
        int size = -1;
        boolean bold = false;
        boolean italic = false;

        // .. resolve name
        if (tmp.length > 0) {
            fontName = tmp[0].trim();
        }

        // .. resolve size
        if (tmp.length > 1) {

            try {
                size = Integer.parseInt(tmp[1].trim());
            } catch (NumberFormatException nfd) {
            }
        }

        // .. resolve bold / italic
        if (tmp.length > 2) {
            for (int i = 2; i < tmp.length; i++) {
                if ("bold".equalsIgnoreCase(tmp[i].trim())) {
                    bold = true;
                } else if ("italic".equalsIgnoreCase(tmp[i].trim())) {
                    italic = true;
                }
            }
        }

        FontData fd = new FontData();

        // .. font type
        fd.setName(fontName);

        // .. height
        if (size > 0) {
            fd.setHeight(size);
        }

        // .. style
        int style = SWT.None;

        if (bold) {
            style |= SWT.BOLD;
        }
        if (italic) {
            style |= SWT.ITALIC;
        }
        fd.setStyle(style);

        return fd;
    }

    private RGB toRgb(String hex) {
        assert ColorAndFontUtil.isHex(hex);
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new RGB(r, g, b);
    }
}
