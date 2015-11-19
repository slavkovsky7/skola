function ret = sinusoid(fs, f, phase, n)
	ret=zeros(1,n);
	for i=1 : n
		ret(1,i) =  sin(phase + ((2*pi*f)/fs)*i);
	end
end


sinus = sinusoid(4000, 10, 0.5*pi, 800);
plot(sinus);
figure;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

n=800;
sinus1 = sinusoid(16000, 144, 0, n);
sinus2 = sinusoid(16000, 56, 0, n);

sinus3=sinus1+sinus2;

csinus3=zeros(1, 10 );
for i=1 : size(csinus3,2)
	csinus3(1,i)=sinus3(i*80);
end

hold all;
plot(sinus1);
plot(sinus2);
plot(sinus3);
plot([0:80:800](1:end-1),csinus3);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function ret = chirp(fss, fe, deltatime, ampl, fs)
	pv = fs * deltatime;
	wstart = 2*pi*fss;
	wend = 2*pi*fe;

	beta = (wend - wstart) / (2*deltatime);

	for i = 1:pv
		t = (i/fs);
		ret(i) = ampl * cos(wstart*t + beta*t*t);
	end
end

figure;
ch = chirp(100, 6000, 0.9, 3, 16000);
plot(1:(0.9 * 16000), ch)


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function [x, y] = stvorka(in)
  s = size(in, 2);
  x = zeros(s)
  up = 1
  for i = 1:s
    x(i) = in(i) * up;
    if in(i) == 1
      up = up * -1;
    end
  end
end
% 4

figure;
res = stvorka([0 1 1 0 1 0 1 1 1 0 0 0 1]);
plot(res)


