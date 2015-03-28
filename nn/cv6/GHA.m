clear
load GHA_data

[dim,count] = size(data)


for i = 1 : size(data,1)
	sum(mean(data(i,:)))
end

w = rand(2,3); % <--- uprav
alpha = 0.1; % <--- uprav

w

for ep=1:100
   data = data(:,randperm(count));

   for t=1:count
       x = data(:,t);
       y = w*x;

       deltaw =  w;
       for i = 1 : size(w, 1)
       		for j = 1 : size(w, 2)
       			s =  y(1:i)'* w(1:i,j);
       			deltaw(i,j) = y(i,1) * ( x(j,1) - s) ;
       		end
       end

       w = w + alpha*deltaw;
       %deltaw = y()
	% <--- uprav

   end 
end

W = w;
W

%zobrazenie dat + hlavnych komponentov
p1 = [-0.6;0.6] * W(1,:);
p2 = [-0.6;0.6] * W(2,:);

plot3(data(1,:),data(2,:),data(3,:),'o',p1(:,1),p1(:,2),p1(:,3),'b',p2(:,1),p2(:,2),p2(:,3),'g')
grid
axis([-0.6,0.6,-0.6,0.6,-0.6,0.6])