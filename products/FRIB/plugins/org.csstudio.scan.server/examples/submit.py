from httplib import *
import xml.etree.ElementTree as ET

scan="""<?xml version="1.0" encoding="UTF-8"?>
<commands>
  <comment><text>Example scan submitted via REST interface</text></comment>
</commands>
"""

headers = {
    "Content-type": "text/xml",
    "Accept": "text/xml"
}

server = HTTPConnection("localhost", 4812)
server.request('POST', '/submit/demo', scan, headers)

response = server.getresponse()
print response.status, response.reason, response.getheaders()
data = response.read()
print data
server.close()

info = ET.fromstring(data)
if info.tag != 'id':
    raise "Missing scan ID"
scan_id = int(info.text)

print "Submitted scan with ID ", scan_id
