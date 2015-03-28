load_images;

display_image(X,r,c);

X1 = X(:,[1 2 3 4 5 6]);

% analytical
W = analytical(X1);


C = [];
for i = 1:9
  C(:,i)= corrupt_line(X(:,i),r,c,20,30);
endfor

R = W*C;

display_image(R,r,c);

%gen inv

W = gen_inv(W, X(:,7) );
W = gen_inv(W, X(:,8) );
W = gen_inv(W, X(:,9) );


R = W*C;

display_image(R,r,c);

% novelty

W = novelty(X);

R = W*C;


for i = 1:9
  R(:,i)= X(:,i) - C(:,i);
endfor

display_image(R,r,c);