# Perl, create ramp... records

for ($i=0; $i<10000; ++$i)
{
	print("      <channel> <name>ramp$i</name> <period>2</period><monitor/>     </channel>\n");
}
