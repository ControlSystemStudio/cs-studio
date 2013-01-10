

for ($i=1; $i<2000; ++$i)
{
    print <<END
record(calc, "test$i")
{
    field(SCAN, ".1 second")
    field(CALC, "A<20?A+RNDM:0")
    field(INPA, "test$i")
}
END
;
}
