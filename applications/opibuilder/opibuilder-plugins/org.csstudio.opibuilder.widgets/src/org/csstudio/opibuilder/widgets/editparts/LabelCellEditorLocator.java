/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

/**The locator of label cell editor.
 * @author Xihui Chen
 *
 */
public class LabelCellEditorLocator
        implements CellEditorLocator
    {

        private IFigure labelFigure;

        public LabelCellEditorLocator(IFigure figure) {
            setLabel(figure);
        }

        @Override
        public void relocate(CellEditor celleditor) {
            Text text = (Text)celleditor.getControl();
            if(OPIBuilderPlugin.isRAP())
                text.moveAbove(null);
            Rectangle rect = labelFigure.getClientArea();
            labelFigure.translateToAbsolute(rect);
            org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
            rect.translate(trim.x, trim.y);
            rect.width += trim.width;
            rect.height += trim.height;
            int fontHeight = FigureUtilities.getTextExtents("H", labelFigure.getFont()).height; //$NON-NLS-1$
            if(fontHeight>rect.height)
                rect.height=fontHeight;
            text.setBounds(rect.x, rect.y, rect.width, rect.height);
        }

        /**
         * Returns the stickyNote figure.
         */
        protected IFigure getLabel() {
            return labelFigure;
        }

        /**
         * Sets the Sticky note figure.
         * @param stickyNote The stickyNote to set
         */
        protected void setLabel(IFigure stickyNote) {
            this.labelFigure = stickyNote;
        }


    }
