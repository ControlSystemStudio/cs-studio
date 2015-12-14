#!/usr/bin/python2

# NOTE: This script requires location of the Eclipse executable
# to be specified by the environment variable ECLIPSE_BIN.

import sys, os, os.path, re, time, subprocess
from StringIO import StringIO

DESCRIPTION="List the Installable Units contained in the specified P2 repository."

if "ECLIPSE_BIN" not in os.environ:
    sys.stderr.write("Eclipse application not found, set ECLIPSE_BIN environment variable\n")
    sys.exit(1)

eclipse = os.environ["ECLIPSE_BIN"]

if not os.path.isfile(eclipse):
    sys.stderr.write("Eclipse application not found, executable not found: " + eclipse + "\n");
    sys.exit(1)


def p2list(repo, query=None):

    iu_pattern = re.compile(r"{id=(.*),version=(.*)}")

    cmd_args = [ eclipse, "-nosplash", "-application", "org.eclipse.equinox.p2.director", "-repository", repo, "-lf", '{id=${id},version=${version}}', "-list" ]

    if query is not None:
        cmd_args.append(query)

    sys.stderr.write("Querying repository: " + repo + ": ")
    start = time.time()
    try:
        output = subprocess.check_output(cmd_args)
    except subprocess.CalledProcessError as e:
        sys.stderr.write("Error (%s)\n" % (e.returncode,))
        sys.stderr.write(e.output)
        sys.exit(1)

    sys.stderr.write("Done (%.2fs)\n" % ((time.time() - start),))

    ius = []
    for line in StringIO(output):
        result = iu_pattern.match(line)
        if result is not None:
            ius.append(result.group(1,2))

    return ius


if __name__ == '__main__':
    from argparse import ArgumentParser
    parser = ArgumentParser(description=DESCRIPTION)
    parser.add_argument("repository", help="Eclipse P2 repository (http: or file: URL)" )
    parser.add_argument("query", nargs='?', help="Optional P2 query expression (e.g. 'Q:group')")

    args = parser.parse_args(sys.argv[1:])

    ius = p2list(args.repository, args.query)
    for iu in ius:
        sys.stdout.write(iu[0]+'/'+iu[1]+'\n')
