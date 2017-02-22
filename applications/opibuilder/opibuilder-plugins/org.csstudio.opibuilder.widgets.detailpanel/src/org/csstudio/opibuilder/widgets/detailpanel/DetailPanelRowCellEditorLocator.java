package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

// This class specifies the location of the in place edit box during direct editing
// of a row name.  The method is copied from LabelCellEditorLocator but essentially
// provides the location of the row figure's name label widget.

public class DetailPanelRowCellEditorLocator implements CellEditorLocator {

    private IFigure labelFigure;

    public DetailPanelRowCellEditorLocator(IFigure figure) {
        labelFigure = figure;
    }

    @Override
    public void relocate(CellEditor celleditor) {
        Text text = (Text)celleditor.getControl();
        if(OPIBuilderPlugin.isRAP()) {
            text.moveAbove(null);
        }
        Rectangle rect = labelFigure.getClientArea();
        labelFigure.translateToAbsolute(rect);
        org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
        rect.translate(trim.x, trim.y);
        rect.width += trim.width;
        rect.height += trim.height;
        int fontHeight = FigureUtilities.getTextExtents("H", labelFigure.getFont()).height; //$NON-NLS-1$
        if(fontHeight>rect.height){
            rect.height=fontHeight;
        }
        text.setBounds(rect.x, rect.y, rect.width, rect.height);
    }

}
