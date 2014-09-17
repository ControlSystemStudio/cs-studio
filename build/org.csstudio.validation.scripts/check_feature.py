'''
Created on Sep 17, 2014

A simple script which scans all feature.xml files under the directory it was invoked from to ensure that none of them explicitly define JRE container  

@author: Kunal Shroff
'''

import os.path
import re
import sys
import xml.etree.ElementTree as ET

incorrectFiles = []
for dirpath, dirnames, filenames in os.walk("."):
    for completefilename in [ os.path.join(dirpath, f) for f in filenames if f == "feature.xml" ]:
        tree = ET.parse(completefilename)
        if tree.findall("requires"):
            incorrectFiles.append(completefilename)
if len(incorrectFiles) != 0:
    print 'Following incorrectly configured feature.xml files are committed to repository. \
    The feature file should not have the requires node, you can ensure this by removing all the dependencies defined in the feature'
    for f in incorrectFiles:
        print f
    sys.exit(-1)

