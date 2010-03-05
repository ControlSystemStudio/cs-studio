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

package org.csstudio.utility.ldapUpdater;

import java.util.ArrayList;

/**
 * @author valett
 *
 */
public class Entry {
    /**
     * 
     */
    private ArrayList<String> _prompts =new ArrayList<String>();
    /**
     * 
     */
    private ArrayList<ArrayList<String>>_entryList=new ArrayList<ArrayList<String>>();
/**
 * @param prompts contains the prompt definition line
 * is usee 
 */ 
    public final void setPrompts(final String[] prompts){
        _prompts.clear();
        for (String string : prompts) {
            _prompts.add(string);
        }
    }
/**
 * 
 * @param entry gets the contents of a data line
 */ 
    public final void addEntry(final String[] entry){
        ArrayList<String> entryList = new ArrayList<String>();
        for (String string : entry) {
            entryList.add(string);
        }
        _entryList.add(entryList);
    }
/**
 * 
 * @param prompt is a string
 * @return is the index of the prompt inside of the definition line 
 */
    public final int getPrompt(final String prompt) {
        return _prompts.indexOf(prompt);
    }
/**
 * 
 * @return is the Arraylist
 */
    // TODO : irgendwas
    public final ArrayList<ArrayList<String>> getEntries() {
        return _entryList;
    }
    
/**
 * 
 * @param index bla
 * @return bla
 */
    public final String getPromptString (final int index) {
        return _prompts.get(index);
    }
}
