'''
Created on Oct 3, 2014

The goal is to scan the features and plugins in the core and applications to ensure
1. plugins are not present in duplicate features

@author: Kunal Shroff
'''
import os.path
import xml.etree.ElementTree as ET
import string
from xml.dom import minidom
import sys
from optparse import OptionParser


def readFeatures(repoDir):
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
        for completefilename in [ os.path.join(dirpath, f) for f in filenames if f.endswith("feature.xml")]:
            xmldoc = minidom.parse(completefilename)
            id = ''
            for feature in xmldoc.getElementsByTagName('feature'):
                id = feature._attrs[u'id'].value
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
    
    usage = 'usage: %prog -r C:\git\cs-studio -p C:\git\cs-studio\products\NSLS2 -b C:\git\cs-studio\build'
    parser = OptionParser(usage=usage)
    parser.add_option('-r', '--repoDir', \
                      action='store', type='string', dest='repoDir', \
                      help='the repoDir')
    opts, args = parser.parse_args()
    repoDir = ifNoneReturnDefault(opts.repoDir, repoDir)
    
    '''
    since ElementTree does not allow easy access to the name space, simply setting it as a variable
    '''
    xmlns="{http://maven.apache.org/POM/4.0.0}"
    
    issues = []
    
    '''all the features in the core repository'''
    allFeatures = readFeatures(repoDir)
    
    coreDir = os.path.join(repoDir, 'core')
    appDir = os.path.join(repoDir, 'applications')
    
    '''
    Core Validation:
    read the features/pom.xml for the list of core features
    '''
    root = ET.parse(os.path.join(coreDir, 'features', 'pom.xml')).getroot()
    
    '''the features defined in the pom'''
    coreFeatures = []
    for module in root.iter(xmlns + 'module'):
        featureName = module.text        
        coreFeatures.append(module.text)    
        
    '''read the plugins/pom.xml for the list of core features'''
    root = ET.parse(os.path.join(coreDir, 'plugins', 'pom.xml')).getroot()
    corePlugins = []
    for module in root.iter(xmlns + 'module'):
        corePlugins.append(module.text)
    
    '''
    Check 
    1. All the features defined in the features/pom.xml are present in the core/features
    2. Check that the included plugins are not duplicated
    '''
        
    uniquePlugins = set()
    duplicatePlugins = set()
    
    for feature in coreFeatures:
        if feature not in allFeatures:
            print 'Could not find Core feature : ' + feature
            issues.append('Could not find Core feature : ' + feature)
        else:
            duplicatePlugins.update((set(uniquePlugins) & set(allFeatures[feature]['plugins'])))
            uniquePlugins.update(set(uniquePlugins) ^ set(allFeatures[feature]['plugins']))
    
    duplicatePluginMap = {} 
    for d in duplicatePlugins:
        duplicatePluginMap[d] = []
        for f in allFeatures:
            if d in allFeatures[f]['plugins']:
                duplicatePluginMap[d].append(f)
        issues.append('Duplicate inclusion of plugin: ' + d + ' in features: ' + str(duplicatePluginMap[d]))
        print 'Duplicate inclusion of plugin: ' + d + ' in features: ' + str(duplicatePluginMap[d])
       
    '''
    Applications Validation:
    read the features/pom.xml for the list of application features
    '''
    root = ET.parse(os.path.join(appDir, 'features', 'pom.xml')).getroot()
    '''all the features in the applications repository'''
    allAppFeatures = readFeatures(os.path.join(appDir, 'features'))
    '''the features defined in the pom'''
    appFeatures = []
    for module in root.iter(xmlns + 'module'):
        featureName = module.text        
        appFeatures.append(module.text)    
        
    '''read the plugins/pom.xml for the list of core features'''
    root = ET.parse(os.path.join(appDir, 'plugins', 'pom.xml')).getroot()
    appPlugins = []
    for module in root.iter(xmlns + 'module'):
        appPlugins.append(module.text)
    
    '''
    Check 
    1. All the features defined in the features/pom.xml are present in the applications/features
    2. Check that the included plugins are not duplicated
    '''
        
    uniquePlugins = set()
    duplicatePlugins = set()
    
    for feature in appFeatures:
        if feature not in allFeatures:
            print 'Could not find Application feature : ' + feature
            issues.append('Could not find Application feature : ' + feature)
        else:
            duplicatePlugins.update((set(uniquePlugins) & set(allFeatures[feature]['plugins'])))
            uniquePlugins.update(set(uniquePlugins) ^ set(allFeatures[feature]['plugins']))
    
    duplicatePluginMap = {} 
    for d in duplicatePlugins:
        duplicatePluginMap[d] = []
        for f in allFeatures:
            if d in allFeatures[f]['plugins']:
                duplicatePluginMap[d].append(f)
        issues.append('Duplicate inclusion of plugin: ' + d + ' in features: ' + str(duplicatePluginMap[d]))
        print 'Duplicate inclusion of plugin: ' + d + ' in features: ' + str(duplicatePluginMap[d])
    if len(issues) != 0:
        sys.exit(-1)
    