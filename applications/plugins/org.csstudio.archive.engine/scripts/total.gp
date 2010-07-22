#!/usr/bin/gnuplot -persist
set xdata time
set timefmt x "logs/%Y-%m-%d_%H:%M:%S"
plot 'total.dat' using 1:5 title 'Values recvd/sec' with linespoints lt 1, 'total.dat' using 1:7 title 'Written/sec' with lines lt 3
pause -1
