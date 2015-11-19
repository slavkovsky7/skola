function ret  = bernoulli(p, N)
	ret = zeros(1, N);
	for i = 1 : size(ret,2)
		to_add=-1;
		if (rand > p)
			to_add=1;
		end
		if (i > 1)
			ret(1,i) = sum( ret(1,i-1) );  
		end
		ret(1,i) = ret(1,i) + to_add; 
	end
end

hold all;
b1=bernoulli(0.5, 100);
b2=bernoulli(0.25, 100);
plot(b1);
plot(b2);  

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function ret = gauss(mean, var, n)
	ret = zeros(1,n);
	for i = 1 : n
		ret(1,i)= rand * var + mean;
	end
end

g=gauss(2,9,16000);
figure
hold all;
plot(g);
plot(xcov(g))
%function dvojka(freq,time, )


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function ret = sinusoid(fs, f, phase, n)
	ret=zeros(1,n);
	for i=1 : n
		ret(1,i) =  sin(phase + ((2*pi*f)/fs)*i);
	end
end



figure
hold all;
s1=sinusoid(16000,100, 0, 16000);
plot(s1, 'b')
plot(xcorr(s1) , 'r')

s2=sinusoid(16000,300, 0, 16000);
s3=sinusoid(16000,500, 0, 16000);

news1=s1+s2+s3;

figure
hold all;
plot(news1)
plot(xcorr(news1))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
hist_x = zeros(1, 1000);
for i = 1:size(hist_x, 2)
  hist_x(i) = mean(ceil(rand(1, 10) * 5));
endfor
 
figure
hist(hist_x)