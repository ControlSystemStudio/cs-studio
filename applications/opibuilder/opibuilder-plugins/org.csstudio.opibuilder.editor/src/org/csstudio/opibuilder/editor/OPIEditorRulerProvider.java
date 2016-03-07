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

package org.csstudio.opibuilder.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.CreateGuideCommand;
import org.csstudio.opibuilder.commands.DeleteGuideCommand;
import org.csstudio.opibuilder.commands.MoveGuideCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * The RulerProvider for the OPI Editor.
 * @author Kai Meyer(original author), Xihui Chen (since import from SDS 2009/9)
 */
public final class OPIEditorRulerProvider extends RulerProvider {

    /**
     * A PropertyChangeListener for rulers.
     */
    private PropertyChangeListener rulerListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(RulerModel.PROPERTY_CHILDREN_CHANGED)) {
                GuideModel guide = (GuideModel)evt.getNewValue();
                if (getGuides().contains(guide)) {
                    guide.addPropertyChangeListener(guideListener);
                } else {
                    guide.removePropertyChangeListener(guideListener);
                }
                for (int i = 0; i < listeners.size(); i++) {
                    ((RulerChangeListener)listeners.get(i))
                            .notifyGuideReparented(guide);
                }
            }
        }
    };

    /**
     * A PropertyChangeListener for guides.
     */
    private PropertyChangeListener guideListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(GuideModel.PROPERTY_CHILDREN_CHANGED)) {
                for (int i = 0; i < listeners.size(); i++) {
                    ((RulerChangeListener)listeners.get(i))
                            .notifyPartAttachmentChanged(evt.getNewValue(), evt.getSource());
                }
            } else {
                for (int i = 0; i < listeners.size(); i++) {
                    ((RulerChangeListener)listeners.get(i))
                            .notifyGuideMoved(evt.getSource());
                }
            }
        }
    };

    /**
     * The Model for the rulers.
     */
    private RulerModel ruler;

    /**
     * Constructor.
     * @param ruler
     *             The RulerModel fore this provider
     */
    public OPIEditorRulerProvider(final RulerModel ruler) {
        this.ruler = ruler;
        this.ruler.addPropertyChangeListener(rulerListener);
        List<GuideModel> guides = getGuides();
        for (int i = 0; i < guides.size(); i++) {
            ((GuideModel)guides.get(i)).addPropertyChangeListener(guideListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getRuler() {
        return ruler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnit() {
        return RulerProvider.UNIT_PIXELS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractWidgetModel> getAttachedModelObjects(final Object guide) {
        return new ArrayList<AbstractWidgetModel>(((GuideModel)guide).getAttachedModels());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getMoveGuideCommand(final Object guide, final int pDelta) {
        return new MoveGuideCommand((GuideModel)guide, pDelta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getCreateGuideCommand(final int position) {
        return new CreateGuideCommand(ruler, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getDeleteGuideCommand(final Object guide) {
        return new DeleteGuideCommand((GuideModel) guide, ruler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getGuidePositions() {
        List<GuideModel> guides = getGuides();
        int[] result = new int[guides.size()];
        for (int i = 0; i < guides.size(); i++) {
            result[i] = ((GuideModel)guides.get(i)).getPosition();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGuidePosition(final Object guide) {
        return ((GuideModel)guide).getPosition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GuideModel> getGuides() {
        return ruler.getGuides();
    }
}
