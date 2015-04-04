set terminal png size 1024,768
set output 'generated/output/curve.png'
set xlabel "epoch" 
set ylabel "error" 
set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 0.5
plot "/home/martin/workspace/skola/nn/projekt2/generated/generated-train-errors.dat" with lines ls 1, "/home/martin/workspace/skola/nn/projekt2/generated/generated-valid-errors.dat" with lines ls 2
