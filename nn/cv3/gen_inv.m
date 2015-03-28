function [W] = gen_inv(W,x)
  z = x - W*x;
  W = W + (z * transpose(z)) / (norm(z)^2);
