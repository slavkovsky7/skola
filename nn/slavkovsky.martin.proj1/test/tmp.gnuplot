set xlabel "X1 axis" 
set ylabel "X2 axis" 
set zlabel "X3 axis" 
set title "3D surface from a function"
set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1
set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1
splot "test/neg.dat" with points ls 1, "test/pos.dat" with points ls 2 , -4.993925203287049*x + -7.70271181709914*y + 12.963634210488562 with lines ls 4
