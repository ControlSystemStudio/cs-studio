package edu.msu.nscl.olog.api;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Levels (collection) object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman
 * @Deprecated 
 */
@XmlRootElement(name = "levels")
@Deprecated public class XmlLevels {
    private Collection<XmlLevel> levels = new ArrayList<XmlLevel>();

    /** Creates a new instance of XmlLevels. */
    public XmlLevels() {
    }

    /** Creates a new instance of XmlLevels with one initial level.
     * @param level initial element
     */
    public XmlLevels(XmlLevel level) {
        levels.add(level);
    }

    /**
     * Returns a collection of XmlLevel.
     *
     * @return a collection of XmlLevel
     */
    @XmlElement(name = "level")
    public Collection<XmlLevel> getLevels() {
        return levels;
    }

    /**
     * Sets the collection of levels.
     *
     * @param items new level collection
     */
    public void setLevels(Collection<XmlLevel> items) {
        this.levels = items;
    }

    /**
     * Adds a property to the level collection.
     *
     * @param item the XmlLevel to add
     */
    public void addXmlLevel(XmlLevel item) {
        this.levels.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlLog to create the string representation for
     * @return string representation
     */
    public static String toLog(XmlLevels data) {
        if (data.getLevels().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (XmlLevel p : data.getLevels()) {
                s.append(XmlLevel.toLog(p) + ",");
            }
            s.delete(s.length()-1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
