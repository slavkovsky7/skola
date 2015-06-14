%load data
load data

%data su v stlpcoch, posledny riadok je kategoria 1 / 0

function ret =  euclidian(u,v)
	s = sum((u - v).^2);
	ret =sqrt(s);
end

function [W] = analytical(X,Y)
  n = size(X,1)
  N = size(X,2)
  if n == N
    Xplus = inv(X);
  elseif n < N
    Xplus = X' * inv(X*X');
  else
    Xplus = inv( transpose(X)*X) * transpose(X); 
  end
  
  W = Y*Xplus;
end

[n_input,n_data] = size(data);

%hidden unit centers
alpha=0.1;
n_hidden = 4;
v = rand(n_input-1,n_hidden);

%linear weights
%...

% Stage 1 - samoorganizacia
%zmente si to na while, resp inak nahodne vyberajte (nechal som to tu, keby
%ste to potrebovali)
%for ep=1:100
%   data = data(:,randperm(n_data));
%   for j=1:n_data
%       x = data(:,j);
%       x = x(1:end-1);

%       distances = zeros(n_hidden,1);
%       for i=1:n_hidden
%       		distances(i) = euclidian(x, v(:,i) );
%       end  
%       [m,mindex] = min(distances);


%	   dist = x - v(:,mindex);       
%       v(:,mindex) = v(:,mindex) + alpha*( dist );
%   end 
%end

%transformacia X-iek


sigma=2;
q=1;
w = zeros(n_hidden, n_data);
for ep=1:1
  for j=1:n_data
    x = data(:,j);
    x = x(1:end-1);
    
    xx = zeros(n_hidden,1);
    for i=1:n_hidden
	xx(i) = exp( -(q*euclidian(x, v(:,i))) / (sigma^2) );
    end

    w(:,j) = xx;
    
    
    c=1;
    for i=1:size(v,2)
      if euclidian(x,v(:,i)) < euclidian(x,v(:,c))
	c = i;
      end
    end
    v(:,c) = v(:,c) + alpha*( x - v(:,c));
    
  end
  ep
end


w = analytical(w, data(end,:) );

% Testovanie
data_t = data;
error = 0;
for j=1:n_data
   

   x = data(:,j);
   x = x(1:end-1);
   

   % sem doplnte prechod sietou
   h = zeros(n_hidden,1);
   for i=1:n_hidden
      h(i) = exp( - ( euclidian(x, v(:,i))^2) / (sigma^2) );
   end
    

    y = w*h; %zmente na vystup
    % pre >0.5 povazuje za kategoriu 1
    
    data_t(4,j) = data(4,j) + (y>0.5) *2;
    if (data_t(4,j) == 1) || (data_t(4,j) == 2)
        error = error + 1;
    end
end
error = error/n_data


%zobrazenie
close all
figure;
hold all;
A = data_t(:,find(data_t(4,:) == 0)); % cat 0 spravne
B = data_t(:,find(data_t(4,:) == 1)); % cat 1 nespravne
C = data_t(:,find(data_t(4,:) == 2)); % cat 0 nespravne
D = data_t(:,find(data_t(4,:) == 3)); % cat 1 spravne


plot3(A(1,:),A(2,:),A(3,:),'gx')
plot3(B(1,:),B(2,:),B(3,:),'mx')

if size(C,2) > 0
  plot3(C(1,:),C(2,:),C(3,:),'rx')
end

if size(C,2) > 0
  plot3(D(1,:),D(2,:),D(3,:),'bx')
end

plot3(v(1,:),v(2,:),v(3,:),'ko');


