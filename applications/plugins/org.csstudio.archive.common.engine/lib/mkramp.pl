# Perl, create ramp... records

for ($i=0; $i<10000; ++$i)
{
	print("\n");
	print("record(calc, \"ramp$i\")\n");
	print("{\n");
   	print("    field(SCAN, \"2 second\")\n");
   	print("    field(CALC, \"A>9?0:A+1\")\n");
   	print("    field(INPA, \"ramp$i\")\n");
   	print("    field(EGU,  \"furlong\")\n");
   	print("    field(PREC, \"2\")\n");
	print("}\n");
}
