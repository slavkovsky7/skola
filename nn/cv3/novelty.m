function [W] = novelty(X)

  n = size(X,1)
  N = size(X,2)
  if n == N
    Xplus = inv(X);
  elseif n < N
    Xplus = X' * inv(X*X');
  else
    Xplus = inv( transpose(X)*X) * transpose(X); 
  end
  
  XXplus=  X*Xplus;
  W = eye( size(XXplus,1) ) - XXplus;