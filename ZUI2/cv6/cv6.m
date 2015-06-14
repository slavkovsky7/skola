load data;

function [theta] = regression(x,y)
	theta=x'*x\(x'*y);
end

function c = autocorelation(yy)
	c =zeros(1,size(yy,1));
	
	yy_avg = mean(yy);
	N = size(c,2);


	%c(:,1) = (1/N)*sum( ( yy - yy_avg).^2 );

	for h = 1 : N
		s = 0;
		for l = 1 : N - h
			s = s + ( yy(l,:) - yy_avg )* ( yy(l+h,:) - yy_avg); 
		end
		c(:,h) = (1/N)*s;
	end
end

function coef =  trojka(stationar) 
	X = zeros(4, size(stationar,1) - 4);
	Y = zeros(size(stationar,1) - 4, 1);
	
	for i = 1 : 100 - 4
		X(:,i) = stationar(i:i+3);
		Y(i,:) = stationar(i+4);
	end
	size(X)
	size(Y)
	coef = reg(X',Y)
endfunction


Y = data';
X = (1 : size(data,2))';
theta = regression(X,Y);

size(theta)

YY = X*theta;

stationar = Y-YY;
c = autocorelation(stationar);

trojka(stationar)


hold all;
plot(Y);
plot(YY);
plot(Y-YY);
hold off;
figure;


hold all;
plot(data);
plot(c);
%plot(c/c(:,1));