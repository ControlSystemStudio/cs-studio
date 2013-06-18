/**
 * 
 */
package org.csstudio.opibuilder.samples;

/**
 * Interface for the extension point to contribute sample screens to the BOY
 * examples that can be install in CS-Studio
 * 
 * The given sample screens will be installed in a directory under Examples
 * 
 * @author shroffk
 * 
 */
public interface SampleSet {

    /**
     * Return the directory of the sample set
     * 
     * @return
     */
    public String getDirectory();

}
