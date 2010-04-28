package org.csstudio.alarm.table.preferences;

import java.util.ArrayList;
import java.util.List;

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
    
    public TopicSet(final String defTopic,
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
