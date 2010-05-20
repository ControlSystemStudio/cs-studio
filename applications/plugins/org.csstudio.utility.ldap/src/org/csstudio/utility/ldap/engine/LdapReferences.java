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
import java.util.List;
import java.util.Vector;

import org.csstudio.platform.logging.CentralLogger;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 10.05.2010
 */
public class LdapReferences {

    private Hashtable<String, Entry> _ldapEntries = new Hashtable<String, Entry>();

    public LdapReferences() {
        // EMPTY
    }

    public static class Entry {

        private GregorianCalendar _timeCreated = null;
        private GregorianCalendar _lastTimeUsed = null;
        private List<String> _namesInNamespace = null;

        public Entry ( final List<String> namesInNamespace) {
            //
            // initialize timer
            //
            setTimeCreated( new GregorianCalendar());
            setNamesInNamespace(namesInNamespace);

        }


        public GregorianCalendar getLastTimeUsed() {
            return _lastTimeUsed;
        }
        public void setLastTimeUsed(final GregorianCalendar lastTimeUsed) {
            this._lastTimeUsed = lastTimeUsed;
        }
        public List<String> getNamesInNamespace() {
            return _namesInNamespace;
        }
        public void setNamesInNamespace(final List<String> namesInNamespace) {
            this._namesInNamespace = namesInNamespace;
        }
        public void replaceNamesInNamespace(final Vector<String> namesInNamespace) {
            this._namesInNamespace = namesInNamespace;
        }
        public GregorianCalendar getTimeCreated() {
            return _timeCreated;
        }
        public void setTimeCreated(final GregorianCalendar timeCreated) {
            this._timeCreated = timeCreated;
        }

    }

    public Hashtable<String, Entry> getLdapEntries() {
        return _ldapEntries;
    }

    public void setLdapEntries(final Hashtable<String, Entry> entries) {
        this._ldapEntries = entries;
    }

    public void newLdapEntry (final String channelName,
                              final List<String> namesInNamespace) {
        //
        // insert new entry
        //
        final Entry newEntry = new Entry ( namesInNamespace);
        this._ldapEntries.put( channelName, newEntry);
    }

    public Entry getEntry ( final String channelName) {
        //
        // find and return entry in hashtable
        //
        return this._ldapEntries.get( channelName);
    }

    public void changeLdapEntry ( final String channelName, final Vector<String> namesInNamespace) {
        //
        // insert new entry
        //
        if ( hasEntry( channelName)) {
            final Entry actualEntry =  this._ldapEntries.get( channelName);
            actualEntry.replaceNamesInNamespace(namesInNamespace);
            this._ldapEntries.remove(channelName);
            this._ldapEntries.put( channelName, actualEntry);
        } else {
            CentralLogger.getInstance().warn( this, "no entry for: " + channelName + " in ldapEntries hashTable");
        }

    }

    public boolean hasEntry ( final String channelName) {
        //
        // find ldap entry by searchig for the channel name
        //
        return _ldapEntries.containsKey( channelName);
    }

    public void clearAll(){
        _ldapEntries.clear();
    }

    public void clear(final String channelName){
        _ldapEntries.remove(channelName);
    }

}
