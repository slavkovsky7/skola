x = 0:0.1:100;


y=zeros(size(x));

for t = 2 : size(x,2)

	y(t)= sum( rand(1,t)*4.- 2 );
	%y(t)= 0.8*t + rand()*10 - 5;
	%y(t)= 0.5*y(t-1) + rand()*10 - 5;
end

split = 10;
meansY = zeros(size(x)/split);
varY = zeros(size(x)/split);
for t = 1 : ( size(x,2) - split )/split;
	%y(t)= sum( mod(rand(1,t), 5) );
	yy = y(t*split:(t+1)*split);
	meansY(t)= mean( yy );
	varY(t) = var(yy);
end


set (0, "defaultaxesfontname", "Helvetica") % this is the line to add BEFORE plotting
hold on;

%plot(y);
%plot(meansY);
plot(varY);
print("MyPNG.png", "-dpng")