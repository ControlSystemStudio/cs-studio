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
from optparse import OptionParser

defaultDesc = "[Enter Feature Description here.]"
'''
since ElementTree does not allow easy access to the name space, simply setting it as a variable
'''
xmlns="{http://maven.apache.org/POM/4.0.0}"

def ifNoneReturnDefault(object, default):
    '''
    if the object is None or empty string then this function returns the default value
    '''
    if object == None and object != '':
        return default
    else:
        return object    
    
if __name__ == '__main__':
    repoDir = 'C:\git\cs-studio-organization-master\cs-studio'
    
    usage = 'usage: %prog -r C:\git\cs-studio'
    parser = OptionParser(usage=usage)
    parser.add_option('-r', '--repoDir', \
                      action='store', type='string', dest='repoDir', \
                      help='the repoDir')
    opts, args = parser.parse_args()
    repoDir = ifNoneReturnDefault(opts.repoDir, repoDir)
    
    incorrectFiles = []
    '''
    build a list of features from 
    1. core/core-features/pom.xml
    2. applications/applications-features/pom.xml
    '''
    features = []
    root = ET.parse(os.path.join(repoDir, 'core', 'features', 'pom.xml')).getroot()
    for module in root.iter(xmlns+'module'):        
        features.append(os.path.join(repoDir, 'core', 'features',module.text))
    root = ET.parse(os.path.join(repoDir, 'applications', 'features', 'pom.xml')).getroot()
    for module in root.iter(xmlns+'module'):        
        features.append(os.path.join(repoDir, 'applications', 'features', module.text))
        
    
    for feature in features:
        completefilename = os.path.join( feature, 'feature.xml' )
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
        