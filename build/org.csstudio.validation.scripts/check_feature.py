'''
Created on Sep 17, 2014

A simple script which scans all feature.xml files under the directory it was invoked from to ensure 
1. that none of them explicitly define JRE container
2. All features have a provider
3. All features have a description or description URL
4. No features have required plugins, tycho should take care of dependency resolution.  

@author: Kunal Shroff
'''

import os.path
import sys
import xml.etree.ElementTree as ET

defaultDesc = "[Enter Feature Description here.]"

incorrectFiles = []
for dirpath, dirnames, filenames in os.walk("."):
    for completefilename in [ os.path.join(dirpath, f) for f in filenames if f == "feature.xml" ]:
        tree = ET.parse(completefilename)
        if tree.findall("requires"):
            incorrectFiles.append(completefilename + " REASON: The feature file should not have the requires node, you can ensure this by removing all the dependencies defined in the feature")
        root = tree.getroot()
        if "provider-name" not in root.attrib:
            incorrectFiles.append(completefilename + " REASON: The feature file should have a provider")
        descElem = root.find("description")
        if descElem is not None:
            if defaultDesc in descElem.text and "url" not in descElem.attrib:
                incorrectFiles.append(completefilename + " REASON: No feature description")
        else:
            incorrectFiles.append(completefilename + " REASON: No feature description")
if len(incorrectFiles) != 0:
    print 'Following incorrectly configured feature.xml files are committed to repository.'    
    for f in incorrectFiles:
        print f
    sys.exit(-1)

