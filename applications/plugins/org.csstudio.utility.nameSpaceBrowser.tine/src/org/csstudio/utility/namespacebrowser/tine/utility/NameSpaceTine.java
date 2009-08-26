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
/*
 * $Id$
 */
package org.csstudio.utility.namespacebrowser.tine.utility;


import org.csstudio.utility.namespacebrowser.tine.Activator;
import org.csstudio.utility.namespacebrowser.tine.Messages;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.tine.reader.NameSpaceResultListTine;
import org.csstudio.utility.tine.reader.TineReader;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2007
 */
public class NameSpaceTine extends NameSpace {

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpace#start()
	 */
//	@Override
	public void start() {
        try{
            TineReader tineReader;
            if (getNameSpaceResultList() instanceof NameSpaceResultListTine) {
                NameSpaceResultListTine eListe = (NameSpaceResultListTine) getNameSpaceResultList();
                String tmp = getSelection();
//                if(tmp.endsWith("=*,")) //$NON-NLS-1$
//                    tineReader = new TineReader(getName(), getFilter(),SearchControls.SUBTREE_SCOPE, eListe);
//                else
//                    tineReader = new TineReader(getName(), eListe);
                tineReader = new TineReader(getName(),getFilter(),eListe);                
                tineReader.addJobChangeListener(new JobChangeAdapter() {
                    public void done(IJobChangeEvent event) {
                        if (event.getResult().isOK())
                            getNameSpaceResultList().notifyView();
                        }
                 });
               
                tineReader.schedule();
            }else{
                // TODO: Was soll gemacht werden wenn das 'getNameSpaceResultList() instanceof ErgebnisListe' nicht stimmt.
                Activator.logError(Messages.getString("CSSView.exp.IAE.2")); //$NON-NLS-1$
            }
        }catch (IllegalArgumentException e) {
            Activator.logException(Messages.getString("CSSView.exp.IAE.1"), e); //$NON-NLS-1$
        }

	}
}
