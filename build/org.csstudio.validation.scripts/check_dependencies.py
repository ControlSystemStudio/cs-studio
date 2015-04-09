'''
Created on Oct 3, 2014

The goal is to scan the features and plugins in the core and applications to ensure
1. plugins are not present in duplicate features

@author: Kunal Shroff
'''
import os.path
import xml.etree.ElementTree as ET
from xml.dom import minidom
import sys
from optparse import OptionParser

'''since ElementTree does not allow easy access to the name space, simply setting it as a variable'''
xmlns="{http://maven.apache.org/POM/4.0.0}"
    
def readFeatures(repoDir, includeRAP=False):
    '''
    Read the all feature.xml
    {id:
        {id:'feature.id',
        file:'complete file path'
        plugins:['plugin.ids']
        includes:['included.features']
        }
    }
    '''
    features = {}
    for dirpath, dirnames, filenames in os.walk(os.path.normpath(repoDir)):
        for completefilename in [ os.path.join(dirpath, f) for f in filenames if f.endswith("feature.xml") and 'products' not in dirpath]:
            xmldoc = minidom.parse(completefilename)
            id = ''
            for feature in xmldoc.getElementsByTagName('feature'):
                id = feature._attrs[u'id'].value
                if includeRAP:
                    plugins = []
                    for node in feature.getElementsByTagName('plugin'):
                        plugins.append(node._attrs[u'id'].value)
                    includes = []
                    for node in feature.getElementsByTagName('includes'):
                        includes.append(node._attrs[u'id'].value)
                    features[id] = {'id':id, 'file':completefilename, 'plugins':plugins, 'includes':includes}
                elif 'rap' not in id:
                    plugins = []
                    for node in feature.getElementsByTagName('plugin'):
                        plugins.append(node._attrs[u'id'].value)
                    includes = []
                    for node in feature.getElementsByTagName('includes'):
                        includes.append(node._attrs[u'id'].value)
                    features[id] = {'id':id, 'file':completefilename, 'plugins':plugins, 'includes':includes}
    return features   

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
    parser.add_option('-i', '--includeRAP', \
                      action='store_true', dest='includeRAP', default=False, \
                      help='include RAP features')
    opts, args = parser.parse_args()
    repoDir = ifNoneReturnDefault(opts.repoDir, repoDir)
    includeRAP = ifNoneReturnDefault(opts.includeRAP, False)
    
    issues = []
    
    '''all the features in the cs-studio repository'''
    allFeatures = readFeatures(repoDir, includeRAP)
    
    '''
    Check 
    1. Check that the included plugins are not duplicated
    '''
        
    uniquePlugins = set()
    duplicatePlugins = set()
    
    for feature in allFeatures:
        duplicatePlugins.update((set(uniquePlugins) & set(allFeatures[feature]['plugins'])))
        uniquePlugins.update(set(uniquePlugins) ^ set(allFeatures[feature]['plugins']))
    
    duplicatePluginMap = {} 
    for d in duplicatePlugins:
        duplicatePluginMap[d] = []
        for f in allFeatures:
            if d in allFeatures[f]['plugins']:
                duplicatePluginMap[d].append(f)
        issues.append('Duplicate inclusion of plugin: ' + d + ' in features: ' + str(duplicatePluginMap[d]))
    issues.sort()
    for issue in issues:
        print issue
    if len(issues) != 0:
        sys.exit(-1)
    