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


import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.namespacebrowser.tine.Activator;
import org.csstudio.utility.namespacebrowser.tine.Messages;
import org.csstudio.utility.tine.reader.TineReader;
import org.csstudio.utility.tine.reader.TineSearchResult;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2007
 */
public class TineNameSpace extends NameSpace {

    private TineSearchResult _searchResult = new TineSearchResult();
    
    private TineReader _reader = null;
    
    volatile boolean _cancelled = false;
    
    /**
     * Constructor.
     */
    public TineNameSpace() {
        // Empty
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void start() {
	    _cancelled = false;
        try {
            if (getSearchResult() instanceof TineSearchResult) {
                final TineSearchResult searchResult = (TineSearchResult) getSearchResult();

                _reader = new TineReader(getName(), getFilter(), searchResult);
                _reader.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(final IJobChangeEvent event) {
                        if (!_cancelled && event.getResult().isOK()) {
                            getSearchResult().notifyView();
                        }
                    }
                 });

                _reader.schedule();
            } else {
                // TODO: Was soll gemacht werden wenn das 'getNameSpaceResultList() instanceof ErgebnisListe' nicht stimmt.
                Activator.logError(Messages.getString("CSSView.exp.IAE.2")); //$NON-NLS-1$
            }
        }catch (final IllegalArgumentException e) {
            Activator.logException(Messages.getString("CSSView.exp.IAE.1"), e); //$NON-NLS-1$
        }

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
	    _cancelled = true;
	    _reader.cancel();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public TineSearchResult getSearchResult() {
        return _searchResult;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public NameSpace createNew() {
        return new TineNameSpace();
    }

}
