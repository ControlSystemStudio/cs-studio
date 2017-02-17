package org.csstudio.logbook.olog.property.fault;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Reads the fault configuration gets a list of Areas, Subsystems, Devices,
 * Groups
 *
 * @author Kunal Shroff
 *
 */
@XmlRootElement
public class FaultConfiguration {

    @XmlElement
    @XmlList
    private List<String> areas;

    @XmlElement
    @XmlList
    private List<String> subsystems;

    @XmlElement
    @XmlList
    private List<String> devices;

    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    private List<Group> groups;

    public List<String> getAreas() {
        return areas;
    }

    public List<String> getSubsystems() {
        return subsystems;
    }

    public List<String> getDevices() {
        return devices;
    }

    public List<Group> getGroups() {
        return groups;
    }

    static class Group {

        @XmlAttribute
        private String name;
        @XmlAttribute
        private String owner;
        @XmlAttribute
        private String contact;

        public Group() {

        }

        public Group(String name, String owner, String contact) {
            super();
            this.name = name;
            this.owner = owner;
            this.contact = contact;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public String getContact() {
            return contact;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((contact == null) ? 0 : contact.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((owner == null) ? 0 : owner.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Group other = (Group) obj;
            if (contact == null) {
                if (other.contact != null)
                    return false;
            } else if (!contact.equals(other.contact))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (owner == null) {
                if (other.owner != null)
                    return false;
            } else if (!owner.equals(other.owner))
                return false;
            return true;
        }

    }

}
