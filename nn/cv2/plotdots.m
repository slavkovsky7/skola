function [] = plotdots(d,w)
x1 = d(d(:,3) == 1,1); 
y1 = d(d(:,3) == 1,2);

x2 = d(d(:,3) == 0,1); 
y2 = d(d(:,3) == 0,2);

y(1) = (w(3) ) / w(2);
y(2) = (w(3) - w(1) ) / w(2);

x = [0,1];

plot(x1,y1,'ro',x2,y2,'go',x,y,'b-')

%plot(x,y,'b-')