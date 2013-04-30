'''
Created on Apr 24, 2013

@author: shroffk
'''
import os.path
import re
from xml.dom import minidom
import sys
from optparse import OptionParser
'''
This Script

1. automates the process of creating the plugin.list and feature.list files needed by the build.

2. It tries to evaluate the dependencies as defined in the MANIFEST.MF files for the plugins and checks
   if these dependencies are resolved/included in the features of the product
'''

def readManifests(repoDir):
    '''
    Read the manifest file create the dependency tree
    It will get all the Manifest files in the repoDir and create tree
    of the dependencies of each plugins.
      
    {id: {name:'',
         file:'complete file path',
         dependencies:[,,]
         }
    }
    '''
    dependencyStack = {}
    for dirpath, dirnames, filenames in os.walk(os.path.normpath(repoDir)):
        for completefilename in [ os.path.join(dirpath, f) for f in filenames if f.endswith(".MF")]:
            name = ''
            dependencies = []
            fileString = open(completefilename, 'r').read().replace(',\n', ',')
            for line in fileString.split('\n'):
                m = re.search('Bundle-SymbolicName: (.*)', line)
                if m:
                    name = m.group(1).split(';')[0].strip()                  
                m2 = re.match('Require-Bundle: (.*)', line)
                if m2:
                    for dependency in m2.group(1).split(','):
                        dependencies.append(dependency.split(';')[0].strip())
                        dependencyDetails = dependency.split(';')
                        for detail in dependencyDetails:
                            matchVersion = re.match('bundle-version="(.*)"', detail.strip())
                            if matchVersion:
                                version = matchVersion.group(1)
                            matchResolution = re.match('resolution:=(.*)', detail.strip())
                            if matchResolution:
                                resolution = matchResolution.group(1)                                                                                                
            dependencyStack[name] = {'name':name, 'file':completefilename, 'dependencies':dependencies}
    return dependencyStack

def dependantPluginMap(dependencyMap):
    '''
    {id:[, ,]}
    pluginId and list of dependent plugins
    This is sort of like a reserve map of the manifest map
    '''
    d = {}
    for id in dependencyMap.keys():
        d[id] = [id2 for id2 in dependencyMap.keys() if id in dependencyMap[id2]['dependencies']]
    return d    

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
        
def getFeatureList(featureId, featureMap):
    '''
    Return a list with the featureId and the Id of all the included features
    '''
    l = []
    if featureId in featureMap.keys():
        l.append(featureId)
        if featureMap[featureId]['includes']:
            for includedFeatureId in featureMap[featureId]['includes']:
                l = l + getFeatureList(featureId=includedFeatureId, featureMap=featureMap)
    return l

def getPluginList(featureId, featureMap):
    '''
    Returns a list of all the plugins that are part of this feature and all the included features
    '''
    l = []
    if featureId in featureMap.keys():
        l = l + featureMap[featureId]['plugins']
        if featureMap[featureId]['includes']:
            for includedFeatureId in featureMap[featureId]['includes']:
                l = l + getPluginList(featureId=includedFeatureId, featureMap=featureMap)
    return l

def ifNoneReturnDefault(object, default):
    '''
    if the object is None or empty string then this function returns the default value
    '''
    if object == None and object != '':
        return default
    else:
        return object
    
if __name__ == '__main__':
    repoDir = 'C:\git\cs-studio'
    productFile = 'C:\git\cs-studio\products\NSLS2\plugins\org.csstudio.nsls2.product\css-nsls2.product'
    
    usage = 'usage: %prog -r /git/cs-studio -p /git/cs-studio/products/NSLS2/plugins/org.csstudio.nsls2.product/css-nsls2.product'
    parser = OptionParser(usage=usage)
    parser.add_option('-r', '--repoDir', \
                      action='store', type='string', dest='repoDir', \
                      help='the repoDir')
    parser.add_option('-p', '--productFile', \
                      action='store', type='string', dest='productFile', \
                      help='the productFile')
    opts, args = parser.parse_args()
    repoDir = ifNoneReturnDefault(opts.repoDir, repoDir)
    productFile = ifNoneReturnDefault(opts.productFile, productFile)
    '''
    TODO: need to include the plugin on the product and explicitly include the dependencies defined there.
    TODO: handle optional dependencies
    '''
    try:
        dependencyMap = readManifests(repoDir=repoDir)
        featureMap = readFeatures(repoDir=repoDir)
    
        '''
        Get started from a project, get the direct dependencies from the manifest
        META-INF/MANIFEST.MF
    
        Get the product file and get the features needs from 
        *.product
        '''
        
        features = []
        plugins = []
        xmldoc = minidom.parse(productFile)
        for features in xmldoc.getElementsByTagName('features'):
            reqFeatures = []
            for feature in features.getElementsByTagName('feature'):
                reqFeatures.append(feature._attrs[u'id'].value)
        for plugins in xmldoc.getElementsByTagName('plugins'):
            reqPlugins = []
            for plugin in plugins.getElementsByTagName('plugin'):
                reqPlugins.append(plugin._attrs[u'id'].value)
             
        '''
        Since our products are feature based products we will use the features to make the two lists
        1. feature.list 
        2. plugin.list
        '''        
        featureList = []
        for featureId in reqFeatures:
            featureList = featureList + getFeatureList(featureId=featureId, featureMap=featureMap)
        featureList = list(set(featureList))
        featureList.sort()
        '''this is feature.list'''
        print featureList           
        '''
        The plugin list represents the included plugins the ones that are part of features
        We do need to add the additional plugin defined in the product.xml one 
        '''
        pluginList = []
        for featureId in reqFeatures:
            pluginList = pluginList + getPluginList(featureId=featureId, featureMap=featureMap)
        pluginList = list(set(pluginList))
        pluginList.sort()
        '''this is the exhaustive plugin.list'''
#        print pluginList
        '''this is the plugin.list consisting of source plugins from the cs-studio repo'''
        sourcePluginList = [ plugin for plugin in pluginList if plugin in dependencyMap.keys() ]
        print len(sourcePluginList), sourcePluginList
                
        '''
        Now we try to resolve the required plugins
        META-INF/MANIFEST.MF
        we use the pluginList         
        '''
        manifestDependencyList = []
        for plugin in pluginList:
            if plugin in  dependencyMap.keys():
                manifestDependencyList.append(plugin)
                manifestDependencyList = manifestDependencyList + dependencyMap[plugin]['dependencies']
        manifestDependencyList = list(set(manifestDependencyList))
        manifestDependencyList.sort()
        print len(manifestDependencyList), manifestDependencyList
                
        dependentPlugins = dependantPluginMap(dependencyMap=dependencyMap)
        print dependentPlugins
        for missingDependency in [ plugin for plugin in manifestDependencyList if plugin not in sourcePluginList ]:
            print 'Missing dependency: ' + missingDependency
            if missingDependency in dependentPlugins.keys():
                print 'Required by: '                
                print str(dependentPlugins.get(missingDependency)).strip('[]')
    except:
        print 'Exception'
    pass
