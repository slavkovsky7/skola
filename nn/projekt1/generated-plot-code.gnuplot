set xlabel "X1 axis" 
set ylabel "X2 axis" 
set zlabel "X3 axis" 
set title "3D surface from a function"
set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1
set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1
plot "generated-neg.dat" with points ls 1, "generated-pos.dat" with points ls 2 , 3.83842073065308*x + -1.1963284600960407 with lines ls 4
