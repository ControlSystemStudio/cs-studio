# Perl, create 600 random noise records
# perl make_rndm_db.pl>myDb.db

for ($i=0; $i<600; ++$i)
{
	     print("\n");
	     print("record(calc, \"TEST-BST1:RNDMxB$i\")\n");
	     print("{\n");
        	print("    field(ADEL, \"10\")\n");
        	print("    field(CALC, \"RNDM*100\")\n");
        	print("    field(DESC, \"Noise\")\n");
        	print("    field(EGU,  \"V\")\n");
        	print("    field(PREC, \"2\")\n");
        	print("    field(LOPR, \"0\")\n");
        	print("    field(HOPR, \"100\")\n");
        	print("    field(LOLO, \"10\")\n");
        	print("    field(LLSV, \"MAJOR\")\n");
        	print("    field(LOW, \"20\")\n");
        	print("    field(LSV, \"MINOR\")\n");
        	print("    field(HIGH, \"80\")\n");
        	print("    field(HSV, \"MINOR\")\n");
        	print("    field(HIHI, \"90\")\n");
        	print("    field(HHSV, \"MAJOR\")\n");
        	print("    field(SCAN, \".1 second\")\n");
	     print("}\n");
}

