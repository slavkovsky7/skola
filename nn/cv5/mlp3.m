
function [h, y, h_net] = forward_pass(x, w_hid, w_out)
    h_net = [w_hid * x];
    h = [logsig(h_net);-1];
    y = w_out * h;
endfunction

%nacitanie dat
clear
load train_set
load test_set

n_input = 1+1;
n_out = 1;

n_train = size(train_set,2);
n_test = size(test_set,2);

%vygeneruj vahy, nastav parametre
alpha = 0.1;   %  <--- uprav
mih = 0.65;
mio = 0.55;
n_hid = 10;     %  <--- uprav

w_hid = rand(n_hid,n_input);
w_out = rand(n_out,n_hid+1);


errors = [];
E = 1;
ep = 0

time = cputime;


w_hid
w_out


while ( ep < 1000 )
  	E = 0;
   	ep++;
   	train_set = train_set(:,randperm(n_train));
  	for t=1:n_train

  		x = train_set(:,t);
  		x(end) = -1; %bias
  		[h, y, h_net] = forward_pass(x, w_hid, w_out);


  		target = train_set(n_input,t);
  		e = (1/2)*(target - y )^2;
  		%trenovanie
  		delta_out = (target - y);
      %d(2) = target - h(3)
  	  %w(2)	= w(2) + alpha*d(2)*h(2)
  		w_out = w_out + alpha*(delta_out*h'); %+ mio*dw_out;
  		w_out_unbias = w_out(:, 1:end-1);
  		h_unbias = h(1:end - 1);

      %d(1) = unbias(w(2))'*d(2).*( unbias(h(2)).*(1- unbias(h(2)) )
  		delta_hid = (w_out_unbias'*delta_out).*( h_unbias .* ( 1 - h_unbias) );
  		w_hid = w_hid + alpha*delta_hid*x' ;
      %w(1) = w(1) + alpha*d(1)*h(1)
  		%  <--- dopln
  	   	E += e;

     	end


   	%%%%%%%%%%%%%%%%%%%%%%%
   	if mod(ep, 10) == 0
	   	ep
	   	E
	   	time = cputime - time
	   	time = cputime;
   	end
end



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% testovanie
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
close all;
load full_set;

E = 0;
for j=1:n_test    
    %prechod
       x = test_set(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       [h, y, h_net] = forward_pass(x, w_hid, w_out);
       
   	   % vyratanie targetu
   	   target = test_set(n_input,j);
       
    % spocitava pocet spravne urcenych vzoriek
    E = E + (target-y)^2;
end
   
E = sqrt(E / n_test);

min_data = min(data(2,:)');
max_data = max(data(2,:)');
E_test = E / (max_data - min_data);

E=0;
%data = (sortrows(data'))';
for j=1:size(data,2)
        
       %prechod
       x = data(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       h = [logsig(w_hid * x); -1];
       y = w_out * h;
       
       plot_data(j,1) = y;
       plot_data(j,2) = data(2,j);
       
       E = E + (data(2,j)-y)^2;
end
E = sqrt(E / n_test);
E_overal = E / (max_data - min_data)   

figure;
plot(plot_data)