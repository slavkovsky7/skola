function [W] = analytical(X)
  n = size(X,1)
  N = size(X,2)
  if n == N
    Xplus = inv(X);
  elseif n < N
    Xplus = X' * inv(X*X');
  else
    Xplus = inv( transpose(X)*X) * transpose(X); 
  end
  
  W = X*Xplus;