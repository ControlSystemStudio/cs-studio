use English;

open(F, "sh check.sh |") or die;
while (<F>)
{
    # print;
    chomp;
    if (m/Description\s+(.*)/)
    {
        $engine = $1;
    }
    if (m/Channels\s+(.*)/)
    {
        $engine{$engine}{channels} = $1;
        $engine{$engine}{disconnected} = 0;
    }
    if (m/Disconnected\s+(.*)/)
    {
        $engine{$engine}{disconnected} = $1;
    }
    if (m/Write Period\s+(.*)/)
    {
        die "Wrong write period $1\n" unless $1 == 30;
    }
    if (m/Write Count\s+(.*)/)
    {
        $engine{$engine}{written} = $1;
    }
    if (m/Write Duration\s+(.*)/)
    {
        if ($1 <= 0.0)
        {
            $engine{$engine}{time} = 0.001;
        }
        else
        {
            $engine{$engine}{time} = $1;
        }
    }
    #     Memory         13.5 MB of 63.6 MB used (21.2 %)
    if (m/Memory\s+(.* MB) of (.*) MB used/)
    {
        $engine{$engine}{used_mem} = $1;
        $engine{$engine}{total_mem} = $2;
    }
}

$channels = 0;
$disconnected = 0;
$written = 0;
$time = 0;

printf("%-20s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\n", "Engine", "Channels", "Disconnected", "Values", "Val/sec", "Write Time [s]", "Write/sec", "Used Mem", "Total Mem", "Mem %");
foreach $engine ( sort keys %engine )
{
    $channels += $engine{$engine}{channels};
    $disconnected += $engine{$engine}{disconnected};
    $written  += $engine{$engine}{written};
    $time     += $engine{$engine}{time};
    printf("%-20s\t%10d\t%10d\t%10d\t%10.1f\t%10.1f\t%10.1f\t%10.1f MB\t%10.1f MB\t%5.1f %%\n",
           $engine,
           $engine{$engine}{channels},
           $engine{$engine}{disconnected},
           $engine{$engine}{written},
           $engine{$engine}{written} / 30.0,
           $engine{$engine}{time},
           $engine{$engine}{written}  / $engine{$engine}{time},
           $engine{$engine}{used_mem},
           $engine{$engine}{total_mem},
           100.0*$engine{$engine}{used_mem} / $engine{$engine}{total_mem});
}
printf("%-20s\t%10d\t%10d\t%10d\t%10.1f\t%10.1f\t%10.1f\n",
       "Total:",
       $channels,
       $disconnected,
       $written,
       $written / 30.0,
       $time,
       $written / $time);
