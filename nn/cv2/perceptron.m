%load data, mozne - and, or, hard
load hard

[n_data,n_input] = size(data);


data

%1. vygeneruj náhodné váhy, vyber alpha
w = rand(1,n_input); % <- doplnit
%w = [0.5036086362, 0.0106706634, 0.7890591838];
alpha = 0.1; % <- doplnit

E = 1;
ep=0
while (E > 0.1)
   %2. nastav celkovu chybu epochy na 0
   E = 0;
   % nahodne pomiesaj
   data = data(randperm(n_data),:);
   for j=1:n_data
       %3.vyber x, vypocitaj y
       x = data(j,:); 
       x(1,3) = -1;
       net = x*w';
       
       y = 1/ ( 1 + exp(-net) ) ;
       % <- doplnit
       d = data(j,3);
       e = (1/2)*( d - y )^2;
       E = E + e;
       %4. vyrataj chybu
       if e > 0
	  w = w + alpha*(d - y)*( y / (1 - y) )*x;
       end
       % <- doplnit
       
       %5.uprav vahy prepojeni
       
       % <- doplnit

   end 
   ep++;
   sleep(1)
   % vizualizacia

      figure;
      plotdots(data,w);

end


plotdots(data,w);
