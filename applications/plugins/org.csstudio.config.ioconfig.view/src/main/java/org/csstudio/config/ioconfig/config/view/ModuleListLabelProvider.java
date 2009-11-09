package org.csstudio.config.ioconfig.config.view;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.01.2009
 */
public class ModuleListLabelProvider extends LabelProvider implements ILabelProvider, IFontProvider,
    IColorProvider {
    /**
     * Font for Module that have an Input or Output. (Style is Normal)
     */
    private static Font _normal;
    /**
     * Font for Module that have an Input and Output. (Style is Bold)
     */
    private static Font _bold;
    /**
     * Font for Module without an Input or Output. (Style is Italic)
     */
    private static Font _italic;
    /**
     * The color for Modules without an Input or Output.
     */
    private static Color _gray;
    /**
     * The default font color.
     */
    private static final Color BLACK = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);
    /**
     * The color for a existing Module Prototype.
     */
    private static final Color YELLOW = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_YELLOW);;
    
    /**
     * The Table font height.
     */
    private static int _height;
    /**
     * The Table Font name.
     */
    private static String _name;
    private final GSDFile _file;

    /**
     * Default Constructor.
     * 
     * @param table
     *            the Table how use this LabelProvider.
     * @param file 
     */
    public ModuleListLabelProvider(final Table table, GSDFile file) {
        _file = file;
        if(_bold==null) {
            FontData fontData = table.getFont().getFontData()[0];
            _height = fontData.getHeight();
            _name = fontData.getName();
    
            _bold = CustomMediaFactory.getInstance().getFont(_name, _height, SWT.BOLD);
            _normal = CustomMediaFactory.getInstance().getFont(_name, _height, SWT.NORMAL);
            _italic = CustomMediaFactory.getInstance().getFont(_name, _height, SWT.ITALIC);
            
            _gray = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getText(final Object element) {
        if (element instanceof GsdModuleModel) {
            return ((GsdModuleModel) element).getModuleNumber() + " : " + element.toString();
        }
        return element.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final Font getFont(final Object element) {
        if (element instanceof GsdModuleModel) {
            GsdModuleModel gmm = (GsdModuleModel) element;
            SlaveCfgData slaveCfgData = new SlaveCfgData(gmm.getValue());
            boolean input = slaveCfgData.isInput();
            boolean output = slaveCfgData.isOutput();
            if (input && output) {
                return _bold;
            } else if (input || output) {
                return _normal;
            } else {
                return _italic;
            }
        } else {
            return _normal;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Color getBackground(final Object element) {
        if (element instanceof GsdModuleModel) {
            GsdModuleModel gmm = (GsdModuleModel) element;
            int selectedModuleNo = gmm.getModuleNumber();
            GSDModule module = _file.getGSDModule(selectedModuleNo);
            if (module != null) {
                return YELLOW;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final Color getForeground(final Object element) {
        if (element instanceof GsdModuleModel) {
            GsdModuleModel gmm = (GsdModuleModel) element;
            SlaveCfgData slaveCfgData = new SlaveCfgData(gmm.getValue());
            // int selectedModuleNo = gmm.getModuleNumber();
            // GSDModule module =
            // getGSDFile().getGSDModule(selectedModuleNo);
            // if(module!=null) {
            // return _blue;
            // }
            boolean input = slaveCfgData.isInput();
            boolean output = slaveCfgData.isOutput();
            if (!input && !output) {
                return _gray;
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
    public final void dispose() {
        super.dispose();
    }
}