
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.websuite.dao;

import java.util.Vector;

/**
 * @author mmoeller
 * @version 1.0
 * @since 04.07.2012
 */
public class HowToSearchWords {
    
    private Vector<String> keywords;
    
    public HowToSearchWords(String searchString) {
        keywords = new Vector<String>();
        if (searchString != null) {
            String line = searchString.trim();
            if (!line.isEmpty()) {
                String[] parts = line.split(" ");
                for (String s : parts) {
                    if (s != null) {
                        if (!s.trim().isEmpty()) {
                            keywords.add(s.trim());
                        }
                    }
                }
            }
        }
    }
    
    public boolean hasKeywords() {
        return !keywords.isEmpty();
    }
    
    public int size() {
        return keywords.size();
    }
    
    public String getKeyword(int n) {
        if ((n < 0) || (n >= keywords.size())) {
            throw new ArrayIndexOutOfBoundsException("Index " + n + " is invalid.");
        }
        return keywords.get(n);
    }
    
    public String getWhereClause(boolean orMode) {
        StringBuffer result = new StringBuffer();
        if (!keywords.isEmpty()) {
            result.append(" WHERE ");
            for (int i = 0;i < keywords.size();i++) {
                result.append("DESCLONG LIKE ? ");
                if (i < (keywords.size() - 1)) {
                    result.append(orMode ? "OR " : "AND ");
                }
            }
        }
        return result.toString();
    }
}
