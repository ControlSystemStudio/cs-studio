#!/bin/bash
MANIFESTLIST=`find .. | grep MANIFEST.MF | grep -v "/build/" | sort`

#echo "<html>"
#echo "<body>"
#echo "<table>"
echo "<html>
<head>
<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />
<title>CSS - Plugin list</title>
<style type=\"text/css\">
table {
border-width: 1px;
border-spacing: 2px;
border-style: outset;
border-color: gray;
border-collapse: collapse;
background-color: white;
}
table th {
border-width: 1px;
padding: 3px;
border-style: inset;
border-color: gray;
background-color: white;
-moz-border-radius: ;
}
table td {
border-width: 1px;
padding: 3px;
border-style: inset;
border-color: gray;
background-color: white;
-moz-border-radius: ;
}
</style>
</head>
<body>
<h1>Plugin list</h1>
<table>
<tr>
  <th>Name</th>
  <th>Version</th>
  <th>Maintainer</th>
  <th>Info</th>
  <th>Manifest location</th>
</tr>"


for MANIFEST in $MANIFESTLIST
do
  echo "  <tr>"
  echo "  <td>" `cat $MANIFEST | grep Bundle-Name | cut -d: -f2- | sed "s/</\&lt;/g" | sed "s/>/\&gt;/g"` "</td>"
  echo "  <td>" `cat $MANIFEST | grep Bundle-Version | cut -d: -f2- | sed "s/</\&lt;/g" | sed "s/>/\&gt;/g"` "</td>"
  echo "  <td>" `cat $MANIFEST | grep Bundle-Vendor | cut -d: -f2- | sed "s/</\&lt;/g" | sed "s/>/\&gt;/g"` "</td>"
  echo "  <td>" `cat $MANIFEST | grep Bundle-Description | cut -d: -f2- | sed "s/</\&lt;/g" | sed "s/>/\&gt;/g"` "</td>"
  echo "  <td>" $MANIFEST "</td>"
  echo "  </tr>"
done
echo "</table>"

if [ -n "$BUILD_TAG" ]
then
  echo "<p>Rebuilt at every commit by Jenkins (" $BUILD_TAG " -  " $BUILD_ID  ")</p>"
fi
echo "<p>The information is taken by parsing each plugin's MANIFEST.MF, using properties Bundle-Name, Bundle-Version, Bundle-Vendor, Bundle-Description</p>"

echo "</body>"
echo "</html>"

#find .. | grep MANIFEST.MF | sort | xargs -i cat {} | grep Bundle-Name | cut -d: -f2-
#find .. | grep MANIFEST.MF | sort | xargs -i cat {} | grep Bundle-Version | cut -d: -f2-
#find .. | grep MANIFEST.MF | sort | xargs -i cat {} | grep Bundle-Vendor | cut -d: -f2-
#find .. | grep MANIFEST.MF | sort | xargs -i cat {} | grep Bundle-Description | cut -d: -f2-

