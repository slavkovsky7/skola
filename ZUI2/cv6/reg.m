function [pol] = reg (X,Y)
pom = (X'*X) \ X';
pol = pom * Y;
