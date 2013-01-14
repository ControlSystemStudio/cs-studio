
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.application.weightrequest;

import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.application.weightrequest.data.ValueEvent;
import org.csstudio.application.weightrequest.data.ValueListener;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 01.12.2011
 */
public class WeightRequest extends Thread {
    
    private static Logger LOG = LoggerFactory.getLogger(WeightRequest.class);
    
    private ArrayList<ValueListener> listener;
    
    private WeightDisplayParser displayParser;
    
    private String httpUrl;
    
    private long refreshRate;
    
    private boolean shutdown;

    public WeightRequest(String url, long refresh) {
        listener = new ArrayList<ValueListener>();
        displayParser = new WeightDisplayParser("&nbsp;");
        httpUrl = url;
        refreshRate = refresh;
        shutdown = false;
    }

    @Override
    public void run() {
        
        Parser parser = null;

        while (!shutdown) {
        
            try {
                
                parser = new Parser(httpUrl);
                NodeList divList = parser.parse(new TagNameFilter("div"));
                
                if(divList.size() > 0) {
                    
                    Div divTag = (Div) divList.elementAt(0);
                    
                    NodeList pList = divTag.getChildren()
                            .extractAllNodesThatMatch(new TagNameFilter("p"));
                    if(pList.size() > 0) {
                        
                        ParagraphTag pTag = (ParagraphTag)pList.elementAt(0);
                        ParseResult pResult = displayParser.parse(pTag.getStringText().trim());
                        
                        Iterator<?> allListener = listener.iterator();
                        while(allListener.hasNext()) {
                            ValueListener o = (ValueListener) allListener.next();
                            try {
                                o.onValue(new ValueEvent(this, pResult.getValue(), pResult.isValid()));
                            } catch(Exception e) {
                                LOG.error("[*** Exception ***]: {}", e.getMessage());
                            }
                        }
                    }
                }
            } catch (ParserException pe) {
            
            LOG.error("[*** ParserException ***]: {}", pe.getMessage());
            ValueListener o;
            Iterator<?> allListener = listener.iterator(); 
            while(allListener.hasNext()) {
                o = (ValueListener) allListener.next();
                o.onValue(new ValueEvent(this, Double.valueOf(0.0D), false));
            }
        }

        synchronized (this) {
            try {
                wait(refreshRate);
            } catch (InterruptedException ie) {
                // Can be ignored
            }
        }
      }
    }

    public void addListener(ValueListener valueListener) {
        listener.add(valueListener);
    }

    public synchronized void setShutdown() {
        shutdown = true;
        notify();
    }
}
