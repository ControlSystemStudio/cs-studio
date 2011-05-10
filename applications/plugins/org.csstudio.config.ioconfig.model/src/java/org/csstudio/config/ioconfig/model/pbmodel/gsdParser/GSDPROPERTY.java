package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

public class GSDPROPERTY {
    
    /**
     * Search for property string in Profibus-DP GSD file and return property value<br>
     * Author: Torsten Böckmann <br>
     * Date: 31. January 2006<br>
     * Last changed: 16. February 2006<br>
     * Reason of changed: ............................................. <br>
     * Copyrights: DESY -Hamburg- by group MKS2<br>
     * (c) 2006 * State: untested prototype<br>
     * Inputs: Profibus DP GSD file as string<br>
     * Property name of profibus parameter as string <br>
     * Output: Property value as string<br>
     * Error: Error tag: "Property not found" as string
     */
 // TODO (hrickens) [03.05.2011]: Raus damit!
    public String selectPropertyValue(String inputStr, String propertyName) {
        int startIndex = 0;
        int endIndex = 0;
        int lineCounter = 0; // Beginning of the first character in the
        // property line
        int slashPosition; // 
        int done = 0; // Flag for ending whiles
        int propertyFound = 0; // Flag for found property string
        int stepbackcounter = 0;// Search for begin of lines
        int commentIndex = 0; // Position of a comment line
        
        String propertyValue = "";
        String selectedStr;
        String PropertyName = "";
        String rawPropertyValue = "";
        char testChar; // Test flag for beginning of property name
        
        // Select property name string
        while ( (done != 1) && inputStr != null && (startIndex < inputStr.length())) {
            startIndex = inputStr.indexOf(propertyName, startIndex);
            // Is end of property name end by space or "="
            // operator
            if (startIndex != -1) {
                // Check in the front of property name the beginning
                // search for "Revision" and found
                // the first tag where "Revision" is included
                // (e.g. GSD_Revision)
                testChar = inputStr.charAt(startIndex - 1);
                if (testChar < 33) {
                    endIndex = inputStr.indexOf("=", startIndex);
                    if (endIndex == -1) {
                        endIndex = inputStr.indexOf(" ", startIndex);
                    }
                    // Is property name equal found string
                    PropertyName = inputStr.substring(startIndex, endIndex).trim();
                    if (PropertyName.compareTo(propertyName) == 0) {
                        // Find the begin of a line
                        stepbackcounter = startIndex;
                        while ( (stepbackcounter != 0) && (inputStr.charAt(stepbackcounter) != 10)) {
                            stepbackcounter--;
                        }
                        commentIndex = inputStr.indexOf(";", stepbackcounter);
                        if ( (commentIndex == -1) || (commentIndex > startIndex)) {
                            // Property Tag isn't deactivated by comment letter
                            done = 1;
                            propertyFound = 1;
                            lineCounter = startIndex;
                        } else {
                            // Found Property Tag is deactived by comment letter
                            startIndex++;
                        }
                    } else {
                        startIndex++;
                    }
                } else {
                    startIndex++;
                }
            } else {
                done = 1;
            }
            
        }
        
        // Is property name found
        if (propertyFound == 1) {
            startIndex = endIndex;
            endIndex = inputStr.indexOf("\n", startIndex);
            selectedStr = inputStr.substring(startIndex, endIndex);
            
            // Find position of character "="
            startIndex = selectedStr.indexOf("=");
            // Delete character "="
            endIndex = selectedStr.length();
            selectedStr = selectedStr.substring( (startIndex + 1), endIndex);
            endIndex = selectedStr.length();
            
            // Delete whitespaces
            selectedStr = selectedStr.trim();
            
            // Delete comments
            startIndex = selectedStr.indexOf(";");
            if (startIndex != -1) {
                
                selectedStr = selectedStr.substring(0, startIndex);
            }
            // Search for backslash character at line end
            slashPosition = selectedStr.indexOf("\\");
            
            if (slashPosition != -1) {
                selectedStr = selectedStr.substring(0, slashPosition);
                rawPropertyValue = selectedStr.trim();
                done = 0;
                // If a backslash at the end of the line read the next line
                while (done != 1) {
                    startIndex = inputStr.indexOf("\n", lineCounter);
                    startIndex++;
                    lineCounter = startIndex;
                    endIndex = inputStr.indexOf("\n", startIndex);
                    selectedStr = inputStr.substring(startIndex, endIndex);
                    
                    // Delete whitespaces
                    selectedStr = selectedStr.trim();
                    
                    // Search for comments
                    startIndex = selectedStr.indexOf(";");
                    // If comments deleten then
                    if (startIndex != -1) {
                        selectedStr = selectedStr.substring(0, startIndex);
                    }
                    // Search for another backslash
                    slashPosition = selectedStr.indexOf("\\");
                    // If backslash is found delete then
                    if (slashPosition != -1) {
                        selectedStr = selectedStr.substring(0, slashPosition);
                        rawPropertyValue = rawPropertyValue.concat(selectedStr.trim());
                    } else {
                        // If not backslash found read no more line
                        rawPropertyValue = rawPropertyValue.concat(selectedStr.trim());
                        done = 1;
                    }
                    
                    propertyValue = rawPropertyValue;
                    testChar = propertyValue.charAt(0);
                    // Is the first charakter """
                    if (testChar == 34) {
                        // Replace the """
                        propertyValue = propertyValue.substring(1, propertyValue.length() - 1);
                    }
                    startIndex = propertyValue.indexOf('"');
                    // Is the last charakter """
                    if (startIndex != -1) {
                        // Replace the end of the line
                        propertyValue = propertyValue.substring(0, startIndex);
                    }
                    if (propertyValue.indexOf("\n") != -1) {
                        propertyValue = propertyValue.substring(0, propertyValue.length() - 2);
                        propertyValue = propertyValue.trim();
                    } else {
                        // Delete whitespaces
                        propertyValue = propertyValue.trim();
                    }
                }
            } else {
                propertyValue = selectedStr;
                testChar = propertyValue.charAt(0);
                // Is the first charakter """
                if (testChar == 34) {
                    // Replace the """
                    propertyValue = propertyValue.substring(1, propertyValue.length() - 1);
                }
                
                startIndex = propertyValue.indexOf('"');
                // Is the last charakter """
                if (startIndex != -1) {
                    // Replace the end of the line
                    propertyValue = propertyValue.substring(0, startIndex);
                }
                // Delete whitespaces
                propertyValue = propertyValue.trim();
            }
        } else {
            // Property name is not found in the GSD file
            propertyValue = "Property not found";
        }
        
        return propertyValue;
    }
    
}
