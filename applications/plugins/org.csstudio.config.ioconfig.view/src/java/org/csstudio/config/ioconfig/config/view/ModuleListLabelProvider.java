package org.csstudio.config.ioconfig.config.view;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgDataBuilder;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 07.01.2009
 */
public class ModuleListLabelProvider extends LabelProvider implements IFontProvider, IColorProvider {
    /**
     * Font for Module that have an Input or Output. (Style is Normal)
     */
    private static Font _NORMAL;
    /**
     * Font for Module that have an Input and Output. (Style is Bold)
     */
    private static Font _BOLD;
    /**
     * Font for Module without an Input or Output. (Style is Italic)
     */
    private static Font _ITALIC;
    /**
     * The color for Modules without an Input or Output.
     */
    private static Color _GRAY;
    /**
     * The default font color.
     */
    private static final Color BLACK = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);
    /**
     * The color for a existing Module Prototype.
     */
    private static final Color YELLOW = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_YELLOW);
    
    /**
     * The Table font height.
     */
    private static int _HEIGHT;
    /**
     * The Table Font name.
     */
    private static String _NAME;
    
    /**
     * Default Constructor.
     * 
     * @param table
     *            the Table how use this LabelProvider.
     * @param file
     */
    public ModuleListLabelProvider(@Nonnull final Table table) {
        final FontData fontData = table.getFont().getFontData()[0];
        if(_GRAY==null) {
            _HEIGHT = fontData.getHeight();
            _NAME = fontData.getName();
            _GRAY = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);
            _BOLD = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.BOLD);
            _NORMAL = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.NORMAL);
            _ITALIC = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.ITALIC);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void dispose() {
        super.dispose();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final Color getBackground(@Nullable final Object element) {
        if (element instanceof GsdModuleModel2) {
            final GsdModuleModel2 gmm = (GsdModuleModel2) element;
            final int selectedModuleNo = gmm.getModuleNumber();
            final GSDFileDBO gsdFileDBO = gmm.getParent().getGsdFileDBO();
            final GSDModuleDBO module = gsdFileDBO.getGSDModule(selectedModuleNo);
            if (module != null) {
                return YELLOW;
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Font getFont(@Nullable final Object element) {
        if (element instanceof GsdModuleModel2) {
            final GsdModuleModel2 gmm = (GsdModuleModel2) element;
            boolean input = false;
            boolean output = false;
            final List<Integer> values = gmm.getValue();
            final SlaveCfgDataBuilder slaveCfgDataFactory = new SlaveCfgDataBuilder(values);
            for (final SlaveCfgData slaveCfgData : slaveCfgDataFactory.getSlaveCfgDataList()) {
                input |= slaveCfgData.isInput();
                output |= slaveCfgData.isOutput();
            }
            if (input && output) {
                return _BOLD;
            } else if (input || output) {
                return _NORMAL;
            } else {
                return _ITALIC;
            }
        } else {
            return _NORMAL;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Color getForeground(@Nullable final Object element) {
        if (element instanceof GsdModuleModel2) {
            final GsdModuleModel2 gmm = (GsdModuleModel2) element;
            boolean input = false;
            boolean output = false;
            final List<Integer> values = gmm.getValue();
            final SlaveCfgDataBuilder slaveCfgDataFactory = new SlaveCfgDataBuilder(values);
            for (final SlaveCfgData slaveCfgData : slaveCfgDataFactory.getSlaveCfgDataList()) {
                input = slaveCfgData.isInput();
                output = slaveCfgData.isOutput();
            }
            if (!input && !output) {
                return _GRAY;
            } else {
                return BLACK;
            }
        } else {
            return BLACK;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String getText(@Nonnull final Object element) {
        if (element instanceof GsdModuleModel2) {
            return ((GsdModuleModel2) element).getModuleNumber() + " : " + element.toString();
        }
        return element.toString();
    }
}
