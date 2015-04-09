#!/bin/bash
FILE=$1
sed -i 's/vhigh/4/g' $FILE
sed -i 's/high/3/g' $FILE
sed -i 's/med/2/g' $FILE
sed -i 's/low/1/g' $FILE

sed -i 's/5more/5/g' $FILE
sed -i 's/more/5/g' $FILE

sed -i 's/big/3/g' $FILE
sed -i 's/med/2/g' $FILE
sed -i 's/small/1/g' $FILE

sed -i 's/unacc/1/g' $FILE
sed -i 's/acc/2/g' $FILE

sed -i 's/vgood/4/g' $FILE
sed -i 's/good/3/g' $FILE


