
function ma_data = movingAverage(data, n)
   ma_data  = data;
   for  i = 1 + n : size(data,2);
      ma_data (1,i) = (1/n)*sum(data(1,i-n:i));
   end
endfunction


function exp_data = exponential_smoothing(data,alpha)
   exp_data = data;
   for  i = 2 : size(data,2);
      exp_data (1,i) = alpha*data(1,i) + (1 - alpha)*exp_data(1, i - 1);
   end
endfunction

function dexp_data = double_exponential_smoothing(data,alpha, beta)
   b = zeros(1, size(data,2));
   s = zeros(1, size(data,2) );
   
   
   s(1,1) = data(1,1);
   s(1,2) = data(1,2);
   
   b(1,2) = data(1,2) - data(1,1);
   
   for  i = 3 : size(data,2);
      s(1,i) = alpha*data(1,i) + (1 - alpha)*( s(1, i - 1) + b(1,i-1) );
      b(1,i) = beta*( s(1,i) - s(1,i-1)) + (1 - beta)*b(1,i-1);
   end 
   
   dexp_data = s;
endfunction

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

load data.mat;

%plot(ma_data);
%figure;

ma_data = movingAverage(data,100);
%exp_data= exponential_smoothing(data,0.005);
%dexp_data = double_exponential_smoothing(data,0.01, 0.01);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%










%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%tretia uloha

final_data = exponential_smoothing(data,0.01);
final_data2 = movingAverage(final_data ,50);

figure;
hold all;
plot(data);
plot(final_data);
plot(final_data2);





%plot(data);
%plot(exp_data);
%plot(ma_data);
%plot(dexp_data);