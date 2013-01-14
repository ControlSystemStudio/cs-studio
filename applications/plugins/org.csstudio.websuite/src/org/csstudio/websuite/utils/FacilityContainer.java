
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.websuite.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 05.12.2012
 */
public class FacilityContainer {
    
    private static final Logger LOG = LoggerFactory.getLogger(FacilityContainer.class);
    
    private HashMap<String, Facility> content;
    
    public FacilityContainer(String path) {
        
        content = new HashMap<String, Facility>();
        SAXBuilder builder = new SAXBuilder();
        
        if (path != null) {

            File xmlFile = new File(path);
            try {
                
                Document xmlDoc = builder.build(xmlFile);
                
                Element root = xmlDoc.getRootElement();
                
                Iterator<?> facilityIter = root.getChildren("facility").iterator();
                while (facilityIter.hasNext()) {
                    Element facility = (Element) facilityIter.next();
                    String name = facility.getAttribute("name").getValue();
                    Facility o = new Facility(name);
                    Iterator<?> iocIter = facility.getChildren("ioc").iterator();
                    while (iocIter.hasNext()) {
                        Element iocName = (Element) iocIter.next();
                        String value = iocName.getValue();
                        o.addIocName(new IocName(value));
                    }
                    content.put(name, o);
                }
                
            } catch (JDOMException e) {
                LOG.error("[*** JDOMException ***]: {}", e.getMessage());
            } catch (IOException e) {
                LOG.error("[*** IOException ***]: {}", e.getMessage());
            }
        }
    }
    
    public boolean containsFacility(String name) {
        return content.containsKey(name);
    }
    
    public Facility getFacility(String name) {
        return content.get(name);
    }
    
    public Facility[] getAllFacilities() {
        Facility[] facilities = new Facility[content.size()];
        Collection<Facility> values = content.values();
        facilities = values.toArray(facilities);
        return facilities;
    }
}
