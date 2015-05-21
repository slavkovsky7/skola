function [OUT] = corrupt_random(IN,perc)
OUT = IN .* (((rand(size(IN)) > perc) *2) -1 );