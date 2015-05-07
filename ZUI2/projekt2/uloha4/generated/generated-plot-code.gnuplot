set xlabel "X axis" 
set ylabel "Y axis" 
plot "generated/generated-e3_smoothed.dat" with lines ls 1, "generated/generated-e3_forecast.dat" with lines ls 2, "generated/generated-original.dat" with lines ls 3
