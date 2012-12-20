
print <<END
<pvtable version="1.0">
    <tolerance>0.0010</tolerance>
    <pvlist>
END
;

for ($i=1; $i<1000; ++$i)
{
    print <<END
        <pv> <name>test$i</name> </pv>
END
;
}

print <<END
    </pvlist>
</pvtable>
END
;
