/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.commands;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.actions.OPIWidgetsTransfer;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * A Command to clone the selected widgets.
 * @author Kai Meyer (original author), Xihui Chen (since import from SDS 2009/9)
 *
 */
public final class CloneCommand extends Command {
    /**
     * The list of {@link AbstractWidgetModel}.
     */
    private List<AbstractWidgetModel> _models;
    /**
     * The list of cloned {@link AbstractWidgetModel}.
     */
    private List<AbstractWidgetModel> _clonedWidgets;
    /**
     * The parent for the AbstractWidgetModels.
     */
    private AbstractContainerModel _parent;
    /**
     * The horizontal Guide.
     */
    private GuideModel _hGuide;
    /**
     * The vertical Guide.
     */
    private GuideModel _vGuide;
    /**
     * The horizontal alignment.
     */
    private int _hAlignment;
    /**
     * The vertical alignment.
     */
    private int _vAlignment;
    /**
     * The difference between the original location and the new location.
     */
    private Dimension _difference;
    /**
     * The internal {@link CompoundCommand}.
     */
    private CompoundCommand _compoundCommand;

    /**
     * Constructor.
     * @param parent
     *             The parent {@link DisplayModel} for the widgets
     */
    public CloneCommand(final AbstractContainerModel parent) {
        super("Clone Widgets");
        _models = new LinkedList<AbstractWidgetModel>();
        _parent = parent;
    }

    /**
     * Adds the given {@link AbstractWidgetModel} with the given {@link Rectangle} to this Command.
     * @param model
     *             The AbstractWidgetModel
     * @param newBounds
     *             The new bounds for the AbstractWidgetModel
     */
    public void addPart(final AbstractWidgetModel model, final Rectangle newBounds) {
        _models.add(model);
        _difference = this.calculateDifference(model, newBounds);
    }

    /**
     * Calculates the difference between the original location of the widget and the new location.
     * @param model
     *             The {@link AbstractWidgetModel}
     * @param newBounds
     *             The new bounds for the widget
     * @return Dimension
     *             The difference between the original location of the widget and the new location
     */
    private Dimension calculateDifference(final AbstractWidgetModel model, final Rectangle newBounds) {
        Dimension dim = newBounds.getLocation().getDifference(model.getLocation());
        return dim;
    }

    /**
     * Sets the given {@link GuideModel} for the given orientation.
     * @param guide
     *             The guide
     * @param alignment
     *             The alignment for the guide
     * @param isHorizontal
     *             The orientation of the guide
     */
    public void setGuide(final GuideModel guide, final int alignment, final boolean isHorizontal) {
        if (isHorizontal) {
            _hGuide = guide;
            _hAlignment = alignment;
        } else {
            _vGuide = guide;
            _vAlignment = alignment;
        }
    }

    /**
     * Returns a list with widget models that are currently stored on the
     * clipboard.
     *
     * @return a list with widget models or an empty list
     */
    @SuppressWarnings("unchecked")
    private List<AbstractWidgetModel> getWidgetsFromClipboard() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        List<AbstractWidgetModel> result = (List<AbstractWidgetModel>) clipboard
                .getContents(OPIWidgetsTransfer.getInstance());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        DisplayModel tempModel = new DisplayModel();

        for(AbstractWidgetModel widget : _models){
            tempModel.addChild(widget, false);
        }

        String xml = XMLUtil.widgetToXMLString(tempModel, false);
        clipboard.setContents(new Object[] { xml },
                new Transfer[] { OPIWidgetsTransfer.getInstance() });

        _clonedWidgets = getWidgetsFromClipboard();

        _compoundCommand = new CompoundCommand();
        int i=0;
        for (AbstractWidgetModel widgetModel : _clonedWidgets) {
            if (_difference!=null) {
                widgetModel.setLocation((widgetModel.getLocation().x+_difference.width),
                        (widgetModel.getLocation().y+_difference.height));
            } else {
                widgetModel.setLocation((widgetModel.getLocation().x+10),
                        (widgetModel.getLocation().y+10));
            }
            _compoundCommand.add(new WidgetCreateCommand(widgetModel, _parent,
                    new Rectangle(widgetModel.getLocation(), widgetModel.getSize()), (i++ == 0? false : true)));

            if (_hGuide != null) {
                ChangeGuideCommand hGuideCommand = new ChangeGuideCommand(widgetModel, true);
                hGuideCommand.setNewGuide(_hGuide, _hAlignment);
                _compoundCommand.add(hGuideCommand);
            }
            if (_vGuide != null) {
                ChangeGuideCommand vGuideCommand = new ChangeGuideCommand(widgetModel, false);
                vGuideCommand.setNewGuide(_vGuide, _vAlignment);
                _compoundCommand.add(vGuideCommand);
            }
        }
        _compoundCommand.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        _compoundCommand.redo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _compoundCommand.undo();
    }

}
