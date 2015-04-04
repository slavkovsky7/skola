set xlabel "X1 axis" 
set ylabel "X2 axis" 
set zlabel "X3 axis" 
set title "3D surface from a function"
set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1
set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1
splot "generated-neg.dat" with points ls 1, "generated-pos.dat" with points ls 2 , -0.8076261569857198*x + -1.85446766659592*y + 0.7931098588654132 with lines ls 4, -0.45275388832245395*x + -2.851851711340452*y + 2.3923421696335567 with lines ls 7
