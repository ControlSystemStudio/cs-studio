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

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.Messages;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceResultList;
import org.csstudio.utility.namespace.utility.ProcessVariable;

public class ErgebnisListe extends NameSpaceResultList{

	private ArrayList<String> ergbnis = new ArrayList<String>();

	public ArrayList<String> getAnswer() {
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.addAll(ergbnis);
		ergbnis.clear();
//		setChanged();
		return tmp;
	}

//	public void setAnswer(ArrayList<String> ergbnis) {
//		this.ergbnis.addAll(ergbnis);
//		notifyObservers();
//
//	}

	public void notifyView() {
		setChanged();
		notifyObservers();
	}

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getResultList()
     */
    @Override
    public ArrayList<ControlSystemItem> getResultList() {
        ArrayList<ControlSystemItem> tmp = new ArrayList<ControlSystemItem>();
        if(ergbnis==null) return null;
        for (String row : ergbnis) {
            String saubereListe = row;
            // Delete "-Chars that add from LDAP-Reader when the result contains special character
            if(saubereListe.startsWith("\"")){ //$NON-NLS-1$
                if(saubereListe.endsWith("\"")) //$NON-NLS-1$
                    saubereListe = saubereListe.substring(1,saubereListe.length()-1);
                else
                    saubereListe = saubereListe.substring(1);
            }
            String[] token = saubereListe.split("[,=]"); //$NON-NLS-1$
            if(token.length<2) {Activator.logError(Messages.getString("CSSView.Error1")+row+"'");break;} //$NON-NLS-1$ //$NON-NLS-2$

            if(token[0].compareTo("eren")==0){ //$NON-NLS-1$
                tmp.add(new ProcessVariable(token[1], saubereListe));
            }
            else{
                tmp.add(new ControlSystemItem(token[1], saubereListe));
            }
        }
        return tmp;
   }

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#setResultList(java.util.ArrayList)
     */
    @Override
    public void setResultList(ArrayList<String> resultList) {
//        this.ergbnis.addAll(ergbnis);
    	ergbnis.addAll(resultList);
        notifyObservers();

    }

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#copy()
     */
    @Override
    public NameSpaceResultList copy() {
         ErgebnisListe e = new ErgebnisListe();
         e.setResultList(ergbnis);
         return e;
    }

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getNew()
     */
    @Override
    public NameSpaceResultList getNew() {
        return new ErgebnisListe();
    }

}
