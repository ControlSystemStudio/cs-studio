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
package org.csstudio.sds.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.GuideModel;
import org.csstudio.sds.model.RulerModel;
import org.csstudio.sds.ui.internal.commands.CreateGuideCommand;
import org.csstudio.sds.ui.internal.commands.DeleteGuideCommand;
import org.csstudio.sds.ui.internal.commands.MoveGuideCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * The RulerProvider for the SDS-Display.
 * @author Kai Meyer
 */
public final class SDSRulerProvider extends RulerProvider {

    /**
     * A PropertyChangeListener for rulers.
     */
    private PropertyChangeListener _rulerListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(RulerModel.PROPERTY_CHILDREN_CHANGED)) {
                GuideModel guide = (GuideModel)evt.getNewValue();
                if (getGuides().contains(guide)) {
                    guide.addPropertyChangeListener(_guideListener);
                } else {
                    guide.removePropertyChangeListener(_guideListener);
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
    private PropertyChangeListener _guideListener = new PropertyChangeListener() {

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
    private RulerModel _ruler;

    /**
     * Constructor.
     * @param ruler
     *             The RulerModel fore this provider
     */
    public SDSRulerProvider(final RulerModel ruler) {
        _ruler = ruler;
        _ruler.addPropertyChangeListener(_rulerListener);
        List guides = getGuides();
        for (int i = 0; i < guides.size(); i++) {
            ((GuideModel)guides.get(i)).addPropertyChangeListener(_guideListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getRuler() {
        return _ruler;
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
        return new CreateGuideCommand(_ruler, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getDeleteGuideCommand(final Object guide) {
        return new DeleteGuideCommand((GuideModel) guide, _ruler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getGuidePositions() {
        List guides = getGuides();
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
    public List getGuides() {
        return _ruler.getGuides();
    }
}
