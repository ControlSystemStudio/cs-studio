/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.bnl.channelfinder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 *  TODO: either add/remove on this class or on the collection
 *
 * @author rlange
 */

@XmlRootElement(name = "channels")
class XmlChannels {
    private Collection<XmlChannel> items = new ArrayList<XmlChannel>();
  
    /** Creates a new instance of XmlChannels */
    public XmlChannels() {
    }

    /**
     * Returns a collection of XmlChannel.
     *
     * @return a collection of XmlChannel
     */
    @XmlElement(name = "channel")
    public Collection<XmlChannel> getChannels() {
        return items;
    }

    public void setChannels(Collection<XmlChannel> items) {
        this.items = items;
    }

    /**
     * Adds a channel to the channel collection.
     *
     * @param item the XmlChannel to add
     */
    public void addChannel(XmlChannel item) {
        this.items.add(item);
    }

	public boolean containsKey(String name) {
		// TODO Auto-generated method stub
		Iterator<XmlChannel> itr = this.items.iterator();
		while(itr.hasNext()){
			if(itr.next().getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public Collection<String> getChannelNames(){
		Collection<String> list = new ArrayList<String>();
		for (XmlChannel channel : items) {
			list.add(channel.getName());
		}
		return list;
	}

}
