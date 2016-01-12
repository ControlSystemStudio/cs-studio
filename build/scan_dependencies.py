'''
Created on Apr 24, 2013

@author: shroffk
'''
import os.path
import re
import traceback
from xml.dom import minidom
import sys
from optparse import OptionParser
from ConfigParser import SafeConfigParser
from tempfile import NamedTemporaryFile, mkstemp
import mmap
'''
This Script

1. automates the process of creating the plugin.list and feature.list files needed by the build.

2. It tries to evaluate the dependencies as defined in the MANIFEST.MF files for the plugins and checks
   if these dependencies are resolved/included in the features of the product
'''

def createDependencyMap(repoDir):
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
                        dependencyDetails = dependency.split(';')
                        details = {}
                        for detail in dependencyDetails:
                            matchVersion = re.match('bundle-version="(.*)"', detail.strip())
                            if matchVersion:
                                details['version'] = matchVersion.group(1)
                            matchResolution = re.match('resolution:=(.*)', detail.strip())
                            if matchResolution:
                                details['resolution'] = matchResolution.group(1)
                        '''
                        Ignore optional dependencies, if resolution is not specified it is assumed to be required
                        '''
                        if 'resolution' not in details.keys() or details['resolution'] != 'optional':
                            dependencies.append(dependency.split(';')[0].strip())                                                                                                                  
            dependencyStack[name] = {'name':name, 'file':completefilename, 'dependencies':dependencies}
    return dependencyStack

def createDependantPluginMap(dependencyMap):
    '''
    {id:[, ,]}
    pluginId and list of dependent plugins
    This is sort of like a reserve map of the manifest map
    '''
    d = {}
    for id in dependencyMap.keys():
        d[id] = [id2 for id2 in dependencyMap.keys() if id in dependencyMap[id2]['dependencies']]
    return d    

def getAllDependencies(pluginId, dependencyMap):
    '''
    Given a pluginId, return a complete list of plugin dependencies
    '''
    l = []
    if pluginId in dependencyMap.keys():
        l.append(pluginId)
        if dependencyMap[pluginId]['dependencies']:
            for id in dependencyMap[pluginId]['dependencies']:
                l = l + getAllDependencies(id)
    return l
                
    
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

class FakeSecHead(object):
    def __init__(self, fp):
        self.fp = fp
        self.sechead = '[FakeSection]\n'
    def readline(self):
        if self.sechead:
            try: return self.sechead
            finally: self.sechead = None
        else:
            return self.fp.readline()

def check(filetmp, search):
        datafile = file(filetmp)
        found = False #this isn't really necessary 
        for line in datafile:
            if search in line:
                #found = True #not necessary 
                return line
        return '' #because you finished the search without finding anything

    
if __name__ == '__main__':
    repoDir = 'C:\git\cs-studio'
        
    usage = 'usage: %prog -r C:\git\cs-studio -p C:\git\cs-studio\products\NSLS2 -b C:\git\cs-studio\build'
    parser = OptionParser(usage=usage)
    parser.add_option('-r', '--repoDir', \
                      action='store', type='string', dest='repoDir', \
                      help='the repoDir')
    parser.add_option('-b', '--buildDir', \
                      action='store', type='string', dest='buildDir', \
                      help='the buildProperties')
    parser.add_option('-p', '--productDir', \
                      action='store', type='string', dest='productDir', \
                      help='the productDir')
    parser.add_option('-c', '--confBuildDir', \
                      action='store', type='string', dest='confBuildDir', \
                      help='the build directory containing build.properties')
    opts, args = parser.parse_args()
    repoDir = ifNoneReturnDefault(opts.repoDir, repoDir)
    productDir = ifNoneReturnDefault(opts.productDir, 'C:\git\cs-studio\products\NSLS2')
    buildDir = ifNoneReturnDefault(opts.buildDir, 'C:\git\cs-studio\build')
    confBuildDir = ifNoneReturnDefault(opts.confBuildDir, productDir)

    '''
    find the build.properties
    '''  
    cf = SafeConfigParser()
    cf.readfp(FakeSecHead(open(os.path.join(confBuildDir, 'build.properties'), 'r')))

    topLevelElementType =  ''
    topLevelElementId =  ''
    if check(confBuildDir + '/build.properties', 'feature.xml') :
        topLevelElementType = 'feature'
        topLevelElementId = 'feature.xml'
       
    line = check(confBuildDir + '/build.properties', '.product')
    productFileName = ''
    if line :
        line = line.strip()
        if ',' in line : 
            line = line[:line.index(',')]
        productFileName = line
    
    if not str(productFileName).endswith('.product'):
        print 'Invalid product specified in build.properties'
        sys.exit(-1)
    '''
    Find the product file for the product specified in the build.properties
    '''
    for dirpath, dirnames, filenames in os.walk(os.path.normpath(productDir)):
        l = [ os.path.join(dirpath, f) for f in filenames if f == productFileName]
        if len(l) == 1:
           productFilePath = l[0]
        elif len(l) > 1:
            print 'Found more than one copy of ', productFileName, 'at:'
            for f in l:
               print f
    print 'Building:', productFilePath

    '''
    TODO: need to include the plugin on the product and explicitly include the dependencies defined there.
    TODO: handle optional dependencies
    '''
    try:
        dependencyMap = createDependencyMap(repoDir=repoDir)
        featureMap = readFeatures(repoDir=repoDir)
    
        '''
        Get started from a project, get the direct dependencies from the manifest
        META-INF/MANIFEST.MF
    
        Get the product file and get the features needs from 
        *.product
        '''
        reqFeatures = []
        reqPlugins = []
        if productFilePath:
            xmldoc = minidom.parse(productFilePath)
            for features in xmldoc.getElementsByTagName('features'):
                for feature in features.getElementsByTagName('feature'):
                    reqFeatures.append(feature._attrs[u'id'].value)
                    '''Add the top level element if it is a feature'''
            for plugins in xmldoc.getElementsByTagName('plugins'):
                for plugin in plugins.getElementsByTagName('plugin'):
                    reqPlugins.append(plugin._attrs[u'id'].value)
        
        if topLevelElementType == 'feature':
            reqFeatures.append(topLevelElementId)
            
        '''
        Since our products are feature based products we will use the features to make the two lists
        1. feature.list : all the features to be packaged with this product
        2. plugin.list : all the plugins to be packaged with this product
        '''        
        featureList = []
        for featureId in reqFeatures:
            featureList = featureList + getFeatureList(featureId=featureId, featureMap=featureMap)
        featureList = list(set(featureList))
        featureList.sort()
        '''this is feature.list'''
        print featureList     
        featureListFile = open(os.path.join(confBuildDir, 'features.list'), 'w')
        for feature in featureList:
            '''
            This additional step is to check id feature folder name is different from feature id
            '''
            featureDir = os.path.split(os.path.normpath(featureMap.get(feature)['file']))[0]
            featureDirName = os.path.split(featureDir)[1]
            if(feature != featureDirName):
                print 'Consider revising feature folder name', featureDirName, 'to match featureId:', feature 
            featureListFile.write("%s\n" % featureDirName)
        featureListFile.close()      
        '''
        The plugin list represents the plugins that are part of/included in the features
        TODO [optional] We do need to add the additional plugin defined in the product.xml 
        '''
        pluginList = []
        for featureId in reqFeatures:
            pluginList = pluginList + getPluginList(featureId=featureId, featureMap=featureMap)
        pluginList = list(set(pluginList))
        pluginList.sort()
        '''
        This is the exhaustive plugin.list
        It includes all the plugins included in any of the features(and included features) used by the product'''
#        print pluginList
        '''
        sourcePluginList:
        This is the plugin.list consisting of source plugins from the cs-studio repo
        '''
        sourcePluginList = [ plugin for plugin in pluginList if plugin in dependencyMap.keys() ]
        pluginListFile = open(os.path.join(confBuildDir, 'plugins.list'), 'w')
        for plugin in sourcePluginList:
            '''
            This additional step is to check id plugin folder name is different from plugin id
            '''
            pluginDirName = (os.path.normpath(dependencyMap.get(plugin)['file'])).split(os.sep)[-3]
            if(plugin != pluginDirName):
                print 'Consider revising plugin folder name', pluginDirName, 'to match pluginId:', plugin 
            pluginListFile.write("%s\n" % pluginDirName)
        pluginListFile.close()
        print len(sourcePluginList), sourcePluginList
                
        '''
        Now we try to resolve the plugins required by those listed in the pluginList 
        using the dependencies defined in the META-INF/MANIFEST.MF       
        
        We shall output the missing dependencies and also a list of plugins that need them.  
        '''
        manifestDependencyList = []
        for plugin in pluginList:
            if plugin in  dependencyMap.keys():
                manifestDependencyList.append(plugin)
                manifestDependencyList = manifestDependencyList + dependencyMap[plugin]['dependencies']
        manifestDependencyList = list(set(manifestDependencyList))
        manifestDependencyList.sort()
        '''
        Now we remove the plugins that from eclipse/deltapack
        '''
        reducedManifestDependencyList = [ plugin for plugin in manifestDependencyList if plugin in dependencyMap.keys() ]
        print len(reducedManifestDependencyList), reducedManifestDependencyList
                        
        dependentPlugins = createDependantPluginMap(dependencyMap=dependencyMap)
        for missingDependency in [ plugin for plugin in reducedManifestDependencyList if plugin not in sourcePluginList ]:
            print 'Missing dependency: ' + missingDependency
            if missingDependency in dependentPlugins.keys():
                print 'Required by: '                
                print str(dependentPlugins.get(missingDependency)).strip('[]')
    except:
        print 'Exception'
        traceback.print_exc()
        sys.exit(-1)   
    pass
