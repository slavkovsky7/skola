%load data
load SOM_data

[n_input,n_data] = size(data);


w = rand(5,5,n_input)*3;

alpha = 0.1;
lambdai = 1;
lambdaf = 1.5;

function ret =  euclidian(u,v)
	s = sum((u - v).^2);
	ret =sqrt(s);
end

function ret = h(ix,istar,lam)
	exponent = ( euclidian(ix,istar)^2 ) / ( lam^2 );
	ret = exp(-exponent);
end


lambdas = zeros(100,1);
for i = 1 : 100
	lambdas(i) =(lambdai*( lambdaf / lambdai ))^(i / 100);
end


for ep=1:100
   data = data(:,randperm(n_data));
   for j=1:n_data
      x = data(:,j,:); 
      %NAJDI VITAZA
      minDist = 10000000;
      for a = 1 : size(w,1)
      	for b = 1 : size(w,2)
      		W = reshape(w(a,b,:), n_input, 1);
      		weights_point = W;
      		if euclidian(x,weights_point) < minDist
      			minDist = euclidian(x,weights_point);
      			istar = [a;b];
      		end 
      	end
      end


	  for a = 1 : size(w,1)
      	for b = 1 : size(w,2)
      		W = reshape(w(a,b,:), n_input, 1);
      		dw = alpha*h(istar, [a;b], lambdas(ep) )*(x - W);
	      	for c = 1 : size(w,3)
	      		
	      		w(a,b,c) = w(a,b,c) + dw(c,:); 
	      	end
      	end
      end

      %NAJDI VITAZA
      %TODO:: uprav lambdu a a
   end 
   ep
end


%zobrazenie
close all
plot3(data(1,:),data(2,:),data(3,:),'o')
grid
hold on

for i=1:size(w,1)
        plot3(w(i,:,1),w(i,:,2),w(i,:,3),'r')
end

for i=1:size(w,2)
        plot3(w(:,i,1),w(:,i,2),w(:,i,3),'r')
end
hold off


%komponenty
figure;
grid off
velk = ceil(sqrt(n_input));
for i=1:n_input
    subplot(velk,velk,i);
    imagesc(w(:,:,i),[-10,10])
end

