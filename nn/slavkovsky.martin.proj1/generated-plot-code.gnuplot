set xlabel "X1 axis" 
set ylabel "X2 axis" 
set zlabel "X3 axis" 
set title "3D surface from a function"
set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1
set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1
plot "generated-neg.dat" with points ls 1, "generated-pos.dat" with points ls 2 , 4.198991065529206*x + -1.3605327207552005 with lines ls 4, -0.02354859253324317*x + 0.6754867348689215 with lines ls 7
