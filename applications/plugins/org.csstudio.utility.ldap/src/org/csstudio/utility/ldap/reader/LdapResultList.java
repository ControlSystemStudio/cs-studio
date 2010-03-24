/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.Messages;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceResultList;


public class LdapResultList extends NameSpaceResultList {
    
    private List<String> _result = Collections.emptyList();
    private Set<SearchResult> _answerSet = Collections.emptySet();
    
    
    public LdapResultList() {
        // Empty
    }
    
    public List<String> getAnswer() {
        final List<String> tmp = new ArrayList<String>(_result);
        _result.clear(); // TODO (bknerr) : wtf ?
        return tmp;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getResultList()
     */
    @Override
    public List<ControlSystemItem> getCSIResultList() {
        final List<ControlSystemItem> tmpList = new ArrayList<ControlSystemItem>();
        if(_result == null) {
            return null;
        }
        
        for (final String row : _result) { // TODO (bknerr) : encapsulate LDAP answer parsing !
            String cleanList = row;
            // Delete "-Chars that add from LDAP-Reader when the result contains special character
            if(cleanList.startsWith("\"")){ //$NON-NLS-1$
                if(cleanList.endsWith("\"")) {
                    cleanList = cleanList.substring(1,cleanList.length()-1);
                } else {
                    cleanList = cleanList.substring(1);
                }
            }
            final String[] token = cleanList.split("[,=]"); //$NON-NLS-1$
            if(token.length<2) {
                if(!token[0].equals("no entry found")){
                    Activator.logError(Messages.getString("CSSView.Error1")+row+"'");//$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
                
            }
            
            //            if(token[0].compareTo("eren")==0){ //$NON-NLS-1$
            //                tmpList.add(new ProcessVariable(token[1], cleanList));
            //            }
            //            else{
            tmpList.add(new ControlSystemItem(token[1], cleanList));
            //            }
        }
        //_result = new ArrayList<String>(); // TODO (bknerr) : wtf ?
        _result.clear();
        return tmpList;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getNew()
     */
    @Override
    public NameSpaceResultList getNew() {
        //return new LdapResultList(_observer);
        return new LdapResultList();
    }
    
    @Override
    public void notifyView() {
        setChanged();
        notifyObservers();
    }
    
    // TODO (bknerr) : replace all accesses to this List<String by the Set<SearchResult> (answerSet)
    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#setResultList(java.util.ArrayList)
     */
    @Override
    public void setResultList(final List<String> resultList) {
        _result = new ArrayList<String>(resultList);
        notifyView();
    }
    /**
     * @param list
     * @param answerSet
     */
    public void setResultList(final List<String> list, final Set<SearchResult> answerSet) {
        setAnswerSet(answerSet);
        setResultList(list);
    }
    
    
    private void setAnswerSet(final Set<SearchResult> answerSet) {
        _answerSet = answerSet;
    }
    public Set<SearchResult> getAnswerSet() {
        return _answerSet;
    }
    
    
}
