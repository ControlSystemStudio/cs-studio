package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

public class GSDPROPERTY {

    public String SelectPropertyValue(String inputStr, String propertyName) {
        /***********************************************************************
         * Search for property string in Profibus-DP GSD file and return *
         * property value *
         * **********************************************************************
         * Author: Torsten Böckmann * Date: 31. January 2006 * Last changed: 16.
         * February 2006 * Reason of changed:
         * ............................................. *
         * ............................................. * Copyrights: DESY
         * -Hamburg- by group MKS2 * (c) 2006 * State: untested prototype *
         * *********************************************************************
         * Inputs: Profibus DP GSD file as string * Property name of profibus
         * parameter as string * * Output: Property value as string * * Error:
         * Error tag: "Property not found" as string *
         **********************************************************************/

        int startIndex;
        int endIndex;
        int lineCounter;    // Beginning of the first character in the
        // property line
        int slashPosition;  // 
        int done;           // Flag for ending whiles
        int PropertyFound;  // Flag for found property string
        int stepbackcounter;// Search for begin of lines
        int commentIndex;   // Position of a comment line

        String propertyValue;
        String selectedStr;
        String PropertyName;
        String rawPropertyValue;
        char testChar;      // Test flag for beginning of property name

        PropertyFound = 0;
        propertyValue = "";
        rawPropertyValue = "";
        startIndex = 0;
        done = 0;
        PropertyName = "";
        propertyValue = "";
        startIndex = 0;
        endIndex = 0;
        lineCounter = 0;
        stepbackcounter = 0;
        commentIndex = 0;

        // Select property name string
        while ((done != 1) && inputStr!=null &&(startIndex < inputStr.length())) {
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
                        while ((stepbackcounter != 0)
                                && (inputStr.charAt(stepbackcounter) != 10)) {
                            stepbackcounter--;
                        }
                        commentIndex = inputStr.indexOf(";", stepbackcounter);
                        if ((commentIndex == -1) || (commentIndex > startIndex)) {
                            // Property Tag isn't deactivated by comment letter
                            done = 1;
                            PropertyFound = 1;
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
        if (PropertyFound == 1) {
            startIndex = endIndex;
            endIndex = inputStr.indexOf("\n", startIndex);
            selectedStr = inputStr.substring(startIndex, endIndex);

            // Find position of character "="
            startIndex = selectedStr.indexOf("=");
            // Delete character "="
            endIndex = selectedStr.length();
            selectedStr = selectedStr.substring((startIndex + 1), endIndex);
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
                        propertyValue = propertyValue.substring(1,
                                propertyValue.length() - 1);
                    }
                    startIndex = propertyValue.indexOf('"');
                    // Is the last charakter """
                    if (startIndex != -1) {
                        // Replace the end of the line
                        propertyValue = propertyValue.substring(0, startIndex);
                    }
                    if (propertyValue.indexOf("\n") != -1) {
                        propertyValue = propertyValue.substring(0,
                                propertyValue.length() - 2);
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
                    propertyValue = propertyValue.substring(1, propertyValue
                            .length() - 1);
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
