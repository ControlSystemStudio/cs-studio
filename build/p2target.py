#!/usr/bin/python2

# NOTE: This script requires location of the Eclipse executable
# to be specified by the environment variable ECLIPSE_BIN.

import sys, p2list

from argparse import FileType
from argparse import ArgumentParser

from xml.etree import ElementTree

DESCRIPTION="""Expand the specified target definition template as follows:

All 'location' elements must contains a valid 'repository' element with a
'location' attribute. If a 'location' element does NOT contain 'unit'
elements then the P2 repository is queried for all features using
the expression 'Q:group'. The 'location' element is then filled with 'unit'
elements for the latest version of each feature contained in the repository.

If a 'location' element contains one or more 'unit' elements then the P2
repository is queried for all installable units (IUs). The 'unit' elements
must specify an 'id' attribute, but the 'version' attribute is optional.
If the specified IU is found in the repository then the 'version' attribute
is either completed or verified.
"""

parser = ArgumentParser(description=DESCRIPTION)
parser.add_argument("template", type=FileType('r'), help="Location of target definition template")
parser.add_argument("target", type=FileType('w'), help="Destination of target definition (default: stdout)", nargs='?', default=sys.stdout)

args = parser.parse_args(sys.argv[1:])


tree = ElementTree.parse(args.template)
if tree is None:
    sys.stderr.write("Error parsing file: invalid XML file: " + str(template) + "\n")
    sys.exit(1)

locations = tree.getroot().find("locations")
if locations is None:
    sys.stderr.write("Error parsing file: missing 'locations' element\n")
    sys.exit(1)

for location in locations.findall("location"):
    repository = location.find("repository")
    if repository is None:
        sys.stderr.write("Error parsing file: missing 'repository' element\n")
        sys.exit(1)
    url = repository.get("location")
    if url is None:
        sys.stderr.write("Error parsing file: missing 'location' attribute\n")
        sys.exit(1)

    units = location.findall("unit")
    if len(units) == 0:
        elm = None
        for iu in p2list.p2list(url, "Q:group"):
            if elm is None or iu[0] != elm.get('id'):
                elm = ElementTree.Element("unit")
                elm.set('id', iu[0])
                elm.set('version', iu[1])
                elm.tail = '\n'
                location.append(elm)
            else:
                elm.set('id', iu[0])
                elm.set('version', iu[1])
    else:
        ius = p2list.p2list(url)
        for unit in units:
            id = unit.get('id')
            if id is None:
                sys.stderr.write("Error parsing file: 'unit' element missing 'id' attribute\n")
                sys.exit(1)
            ver = unit.get('version')
            if ver is None:
                location.remove(unit)
                elm = None
                for iu in ius:
                    if iu[0] == id:
                        if elm is None or iu[0] != elm.get('id'):
                            elm = ElementTree.Element("unit")
                            elm.set('id', iu[0])
                            elm.set('version', iu[1])
                            elm.tail = '\n'
                            location.append(elm)
                        else:
                            elm.set('id', iu[0])
                            elm.set('version', iu[1])
                        break
                else:
                    sys.stderr.write("Error verifying file: IU '%s' not found in repository\n" % (id,))
                    sys.exit(1)
            else:
                for iu in ius:
                    if iu[0] == id and iu[1] == ver:
                        break
                else:
                    sys.stderr.write("Error verifying file: IU '%s' with version '%s' not found in repository\n" % (id,ver))
                    sys.exit(1)


tree.write(args.target, encoding="UTF-8", xml_declaration=True)
