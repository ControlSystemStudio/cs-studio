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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A topic set is an immutable object.
 *
 * Data for one topicSet defined in the preferences.
 *
 * @author jhatje
 *
 */
public final class TopicSet {
    
    private static final Logger LOG = LoggerFactory.getLogger(TopicSet.class);

    private boolean _defaultTopic = false;

    private String _name = "not set";

    private List<String> _topics = null;

    private boolean _popUp = false;

    private boolean _startUp = false;

    private Font _font = null;

    private boolean _retrieveInitialState = false;

    private TopicSet(@Nonnull final Builder topicSetBuilder) {

        _defaultTopic = topicSetBuilder._defaultTopic.equals("default");

        if (topicSetBuilder._topics != null) {
            _topics = new ArrayList<String>();
            String[] topics = topicSetBuilder._topics.split(",");
            for (String topic : topics) {
                _topics.add(topic);
            }
        }

        _name = topicSetBuilder._name;

        if (topicSetBuilder._popUp != null) {
            _popUp = Boolean.parseBoolean(topicSetBuilder._popUp);
        }
        if (topicSetBuilder._startUp != null) {
            _startUp = Boolean.parseBoolean(topicSetBuilder._startUp);
        }
        if (topicSetBuilder._font != null) {
            defineFont(topicSetBuilder);
        }
        if (topicSetBuilder._retrieveInitialState != null) {
            _retrieveInitialState  = Boolean.parseBoolean(topicSetBuilder._retrieveInitialState);
        }
    }

    private void defineFont(@Nonnull final Builder topicSetBuilder) {
        String[] fontItems = topicSetBuilder._font.split(",");
        try {
            _font = CustomMediaFactory.getInstance().getFont(fontItems[0],
                                                             Integer.parseInt(fontItems[2]),
                                                             Integer.parseInt(fontItems[1]));
        } catch (Exception e) {
            LOG.error("error creating font");
        }
    }

    public boolean isDefaultTopic() {
        return _defaultTopic;
    }

    @Nonnull
    public String getName() {
        return _name;
    }

    @CheckForNull
    public List<String> getTopics() {
        return _topics;
    }

    public boolean isPopUp() {
        return _popUp;
    }

    public boolean isStartUp() {
        return _startUp;
    }

    public boolean isRetrieveInitialState() {
        return _retrieveInitialState;
    }

    @CheckForNull
    public Font getFont() {
        return _font;
    }



    /**
     * Simple builder for the topic set
     */
    public static class Builder {
        private String _defaultTopic;
        private String _name;
        private String _topics;
        private String _popUp;
        private String _startUp;
        private String _font;
        private String _retrieveInitialState;

        public Builder() {
            _defaultTopic = "";
            _name = "not set";
        }

        @Nonnull
        public final Builder setDefaultTopic(@Nonnull final String defaultTopic) {
            _defaultTopic = defaultTopic;
            return this;
        }

        @Nonnull
        public final Builder setName(@Nonnull final String name) {
            _name = name;
            return this;
        }

        @Nonnull
        public final Builder setTopics(@Nonnull final String topics) {
            _topics = topics;
            return this;
        }

        @Nonnull
        public final Builder setPopUp(@Nonnull final String popUp) {
            _popUp = popUp;
            return this;
        }

        @Nonnull
        public final Builder setStartUp(@Nonnull final String startUp) {
            _startUp = startUp;
            return this;
        }

        @Nonnull
        public final Builder setFont(@Nonnull final String font) {
            _font = font;
            return this;
        }

        @Nonnull
        public final Builder setRetrieveInitialState(@Nonnull final String retrieveInitialState) {
            _retrieveInitialState = retrieveInitialState;
            return this;
        }

        @Nonnull
        public final TopicSet build() {
            return new TopicSet(this);
        }

    }
}
