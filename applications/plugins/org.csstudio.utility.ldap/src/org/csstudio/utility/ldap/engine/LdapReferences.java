/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
	
	public void clearAll(){
	    ldapEntries.clear();
	}
	
	public void clear(String channelName){
	    ldapEntries.remove(channelName);
	}

}
