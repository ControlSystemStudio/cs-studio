package org.csstudio.utility.ldap.engine;

import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import org.csstudio.platform.logging.CentralLogger;

public class LdapReferences {
	
	public Hashtable<String, Entry> ldapEntries = null;
	
	public LdapReferences() {
		//
		// initialize hash table
		//
		ldapEntries = new Hashtable<String, Entry>();
	}
	
	public class Entry {
		
		private GregorianCalendar timeCreated = null;
		private GregorianCalendar lastTimeUsed = null;
		private Vector<String> namesInNamespace = null;
		
		public Entry ( Vector<String> namesInNamespace) {
			//
			// initialize timer
			//
			setTimeCreated( new GregorianCalendar());
			setNamesInNamespace(namesInNamespace);
			
		}
		
		
		public GregorianCalendar getLastTimeUsed() {
			return lastTimeUsed;
		}
		public void setLastTimeUsed(GregorianCalendar lastTimeUsed) {
			this.lastTimeUsed = lastTimeUsed;
		}
		public Vector<String> getNamesInNamespace() {
			return namesInNamespace;
		}
		public void setNamesInNamespace(Vector<String> namesInNamespace) {
			this.namesInNamespace = namesInNamespace;
		}
		public void replaceNamesInNamespace(Vector<String> namesInNamespace) {
			this.namesInNamespace = namesInNamespace;
		}
		public GregorianCalendar getTimeCreated() {
			return timeCreated;
		}
		public void setTimeCreated(GregorianCalendar timeCreated) {
			this.timeCreated = timeCreated;
		}
		
	}

	public Hashtable<String, Entry> getLdapEntries() {
		//
		// return hash table
		//
		return ldapEntries;
	}

	public void setLdapEntries(Hashtable<String, Entry> ldapEntries) {
		this.ldapEntries = ldapEntries;
	}
	
	public void newLdapEntry ( String channelName, Vector<String> namesInNamespace) {
		//
		// insert new entry
		//
		Entry newEntry = new Entry ( namesInNamespace);
		this.ldapEntries.put( channelName, newEntry);
	}
	
	public Entry getEntry ( String channelName) {
		//
		// find and return entry in hastable
		//
		return this.ldapEntries.get( channelName);
	}
	
	public void changeLdapEntry ( String channelName, Vector<String> namesInNamespace) {
		//
		// insert new entry
		//
		if ( hasEntry( channelName)) {
			Entry actualEntry =  (Entry)this.ldapEntries.get( channelName);
			actualEntry.replaceNamesInNamespace(namesInNamespace);
			this.ldapEntries.remove(channelName);
			this.ldapEntries.put( channelName, actualEntry);
		} else {
			CentralLogger.getInstance().warn( this, "no entry for: " + channelName + " in ldapEntries hashTable");
		}
		
	}
	
	public boolean hasEntry ( String channelName) {
		//
		// find ldap entry by searchig for the channel name
		//
		return ldapEntries.containsKey( channelName);
	}

}
