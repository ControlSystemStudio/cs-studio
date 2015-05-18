package de.desy.language.snl.diagram.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;

import de.desy.language.snl.parser.nodes.StateSetNode;

public class StateSetFigure extends Label {

    private final StateSetNode _state;

    public StateSetFigure(final StateSetNode state) {
        _state = state;
        this.setText(_state.getSourceIdentifier());
        this.setTextAlignment(PositionConstants.TOP);
        final Label toolTipLabel = new Label();
        toolTipLabel.setText(_state.getContent());
        toolTipLabel.setBackgroundColor(ColorConstants.tooltipBackground);
        this.setToolTip(toolTipLabel);
        this.setBackgroundColor(ColorConstants.lightGray);
        final LineBorder lineBorder = new LineBorder();
        lineBorder.setColor(ColorConstants.black);
        this.setBorder(lineBorder);
        this.setLayoutManager(new XYLayout());
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    protected boolean useLocalCoordinates() {
//        return true;
//    }

}
