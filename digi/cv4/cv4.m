signal = [1 2 3 4 5 6 7 8 0 0 0 0 0 0 0 0];
dft=abs(fft(signal));

hold all;
subplot(2,1,1);
plot(signal);
subplot(2,1,2);
plot(dft);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function ret = sinusoid(fs, f, phase, n)
	ret=zeros(1,n);
	for i=1 : n
		ret(1,i) =  sin(phase + ((2*pi*f)/fs)*i);
	end
end


figure;
hold all;

function dvojka(sinus, plotpos, new_size)
	dft=abs(fft(sinus));
	%new_size=size(sinus,2)/2;
	dft=dft(1,1:end/2);
	dft_l=size(dft,2);
	x=[0.0:new_size/dft_l:new_size-0.0001];
	subplot(3,2,plotpos);
	plot(sinus);
	subplot(3,2,plotpos+1);
	stem(x,dft);
end

sinus1 = sinusoid(100, 10, 0, 100);
dvojka(sinus1, 1, 50);

sinus2=[zeros(1,20),sinus1,zeros(1,20)];
dvojka(sinus2, 3, 50);

sinus3=sinus1(1, 1:end-3);
dvojka(sinus3, 5, 47);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 3
figure;
hold all;
sig = sinusoid(1000, 50, 0, 1000) + sinusoid(1000, 120, 0, 1000);
sig2 = sig + randn(1000,1)*sqrt(3);
furier = abs(fft(sig2));
furier = furier(1:500);


subplot(1,3,1);
plot(sig);
subplot(1,3,2);
plot(sig2);
subplot(1,3,3);
plot(furier);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure;
a=(5*2*pi/1000);
b=5*2*pi;
signal = square(0:a:b,0.5);
furi = abs(fft(signal));
subplot(1,2,1);
plot(signal);
subplot(1,2,2);
plot(0:1000, furi);

