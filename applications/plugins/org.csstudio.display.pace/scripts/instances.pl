#!/usr/bin/perl
#
# Set arrays S/N to all the known systems/instances
# of the LLRF, NUM contains the number.

open (MYFILE, '>>instances.txt');
print MYFILE "<instances>\n";



push @S, "RFQ";
push @N, 1;

for ($i=1; $i<=4; ++$i)
{
        push @S, "MEBT";
        push @N, $i;
}

$ss = scalar(@S);
for ($i=0; $i<$ss; $i++)
{
   print MYFILE "   <instance>\n";
   print MYFILE "      <name>";
   $si=$S[$i];
   print MYFILE "$si ";
   $ni=$N[$i];
   print MYFILE "$ni";
   print MYFILE "</name>\n";
   print MYFILE "      <macros>S=$si,N=$ni</macros>\n";
   print MYFILE "   </instance>\n";
}

@S=();
$#S=-1;
@N=();
$#N=-1;

for ($i=1; $i<=6; ++$i)
{
        push @S, "DTL";
        push @N, $i;
}

$ss = scalar(@S);
for ($i=0; $i<$ss; $i++)
{
   print MYFILE "   <instance>\n";
   print MYFILE "      <name>";
   $si=$S[$i];
   print MYFILE "$si ";
   $ni=$N[$i];
   print MYFILE "$ni";
   print MYFILE "</name>\n";
   print MYFILE "      <macros>S=$si,N=$ni</macros>\n";
   print MYFILE "   </instance>\n";
}

@S=();
$#S=-1;
@N=();
$#N=-1;

for ($i=1; $i<=4; ++$i)
{
        push @S, "CCL";
        push @N, $i;
}

$ss = scalar(@S);
for ($i=0; $i<$ss; $i++)
{
   print MYFILE "   <instance>\n";
   print MYFILE "      <name>";
   $si=$S[$i];
   print MYFILE "$si ";
   $ni=$N[$i];
   print MYFILE "$ni";
   print MYFILE "</name>\n";
   print MYFILE "      <macros>S=$si,N=$ni</macros>\n";
   print MYFILE "   </instance>\n";
}

@S=();
$#S=-1;
@N=();
$#N=-1;

for ($i=1; $i<=11; ++$i)
{
        push @S, "SCL";
        push @N, sprintf("%02da", $i);
        push @S, "SCL";
        push @N, sprintf("%02db", $i);
        push @S, "SCL";
        push @N, sprintf("%02dc", $i);
}

$ss = scalar(@S);
for ($i=0; $i<$ss; $i++)
{
   print MYFILE "   <instance>\n";
   print MYFILE "      <name>";
   $si=$S[$i];
   print MYFILE "$si ";
   $ni=$N[$i];
   print MYFILE "$ni";
   print MYFILE "</name>\n";
   print MYFILE "      <macros>S=$si,N=$ni</macros>\n";
   print MYFILE "   </instance>\n";
}

@S=();
$#S=-1;
@N=();
$#N=-1;

for ($i=12; $i<=23; ++$i)
{
        push @S, "SCL";
        push @N, sprintf("%02da", $i);
        push @S, "SCL";
        push @N, sprintf("%02db", $i);
        push @S, "SCL";
        push @N, sprintf("%02dc", $i);
        push @S, "SCL";
        push @N, sprintf("%02dd", $i);
}

$ss = scalar(@S);
for ($i=0; $i<$ss; $i++)
{
   print MYFILE "   <instance>\n";
   print MYFILE "      <name>";
   $si=$S[$i];
   print MYFILE "$si ";
   $ni=$N[$i];
   print MYFILE "$ni";
   print MYFILE "</name>\n";
   print MYFILE "      <macros>S=$si,N=$ni</macros>\n";
   print MYFILE "   </instance>\n";
}
print MYFILE "</instances>\n";
$NUM=$#S + 1;
close (MYFILE); 
