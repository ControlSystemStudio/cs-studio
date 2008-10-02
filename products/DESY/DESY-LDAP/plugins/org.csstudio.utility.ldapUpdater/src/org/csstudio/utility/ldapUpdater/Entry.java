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
 * 
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
