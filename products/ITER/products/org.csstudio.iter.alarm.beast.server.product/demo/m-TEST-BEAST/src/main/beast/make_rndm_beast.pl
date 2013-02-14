# Perl, create alarm configuration for 600 noise records
# perl make_rndm_beast.pl>myBeast.xml

print("<config name=\"demo\">\n");
print("    <component name=\"TEST-BST1\">\n");

for ($i=0; $i<600; ++$i)
{
     print("      <pv name=\"TEST-BST1:RNDMxB$i\">\n");
     print("          <description>RNDMxB$i Noise signal Out of Limit</description> <latching>true</latching><annunciating >true</annunciating>\n");
     print("          <guidance><title>Noise out of Limit</title><details>HIHI = 90; HIGH= 80; LOW = 20; LOLO=10</details></guidance>\n");
     print("          <guidance><title>Consequence of deviation</title><details>Too much noise</details></guidance>\n");
     print("          <guidance><title>Corrective action</title><details>Disconnect the signal</details></guidance>\n");
     print("          <guidance><title>Time for response</title><details>5 minutes</details></guidance>\n");
     print("          <display><title>Noise control OPI</title><details>/m-TEST-BEAST/src/main/boy/sdd/TEST-BST1.opi</details></display>\n");
     print("      </pv>\n");
}

print("    </component>\n");
print("</config>\n");
