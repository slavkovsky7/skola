function [OUT] = corrupt_line(IN,r,c,Row_Start,Row_Stop)
[velk,N] = size(IN);

OUT = IN;
for i=1:N
    POM = reshape(IN(:,i),r,c);
    POM(Row_Start:Row_Stop,:) = zeros(Row_Stop-Row_Start+1,c);
    OUT(:,i) = reshape(POM,r*c,1);
end
