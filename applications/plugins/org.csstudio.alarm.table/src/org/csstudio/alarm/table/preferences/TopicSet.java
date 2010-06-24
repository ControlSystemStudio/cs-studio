/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Font;

/**
 * Data for one topicSet defined in the preferences.
 *
 * @author jhatje
 *
 */
public class TopicSet {

    private boolean _defaultTopic = false;

    private String _name = "not set";

    private List<String> _topics = null;

    private boolean _popUp = false;

    private boolean _startUp = false;

    private Font _font = null;

    public TopicSet(@Nonnull final String defTopic,
                    final String listOfTopics,
                    final String name,
                    final String popUp,
                    final String startUp,
                    final String font) {
        if (defTopic.equals("default")) {
            _defaultTopic = true;
        }
        if (listOfTopics != null) {
            _topics = new ArrayList<String>();
            String[] topics = listOfTopics.split(",");
            for (String topic : topics) {
                _topics.add(topic);
            }
        }
        if (name != null) {
            _name = name;
        }
        if (popUp != null) {
            _popUp = Boolean.parseBoolean(popUp);
        }
        if (startUp != null) {
            _startUp = Boolean.parseBoolean(startUp);
        }
        if (font != null) {
            String[] fontItems = font.split(",");
            try {
                _font = CustomMediaFactory.getInstance().getFont(fontItems[0],
                                                                 Integer.parseInt(fontItems[2]),
                                                                 Integer.parseInt(fontItems[1]));
            } catch (Exception e) {
                CentralLogger.getInstance().error(this, "error creating font");
            }
        }
    }

    public boolean isDefaultTopic() {
        return _defaultTopic;
    }

    public void setDefaultTopic(final boolean defaultTopic) {
        this._defaultTopic = defaultTopic;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public List<String> getTopics() {
        return _topics;
    }

    public void setTopics(final List<String> topics) {
        this._topics = topics;
    }

    public boolean isPopUp() {
        return _popUp;
    }

    public void setPopUp(final boolean popUp) {
        this._popUp = popUp;
    }

    public boolean isStartUp() {
        return _startUp;
    }

    public void setStartUp(final boolean startUp) {
        this._startUp = startUp;
    }

    public Font getFont() {
        return _font;
    }

    public void setFont(final Font font) {
        this._font = font;
    }

}
