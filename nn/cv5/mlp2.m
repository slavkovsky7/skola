
function [h, y, h_net] = forward_pass(x, w_hid, w_out)
    h_net = [w_hid * x];
    h = [logsig(h_net);-1];
    y = w_out * h;
endfunction

function [yy] = logsig_der(net)
	yy = logsig(net);
	for ix=1:size(net,1)
	    for iy=1:size(net,2)
	        yy(ix,iy) = yy(ix,iy)*(1 - yy(ix,iy) );
	    end
	end
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

dw_hid = zeros(n_hid,n_input);
dw_out = zeros(n_out,n_hid+1);

errors = [];
E = 1;
ep = 0
while ( ep < 30000 )
  	E = 0;
   	ep++;
   	train_set = train_set(:,randperm(n_train));
	for t=1:n_train

		x = train_set(:,t);
		x(end) = -1; %bias
		[h, y, h_net] = forward_pass(x, w_hid, w_out);


		target = train_set(n_input,t);
		e = (target - y )^2;
		%trenovanie
		delta_out = (target - y);

	   	
		w_out = w_out + alpha*(delta_out*h') + mio*dw_out;
		%dw_out = alpha*(delta_out*h');

		w_out_unbias = w_out(:, 1:end-1);
		delta_hid = (w_out_unbias'*delta_out).*logsig_der(h_net);
		w_hid = w_hid + alpha*delta_hid*x' ;
		%dw_hid = alpha*delta_hid*x' + mih*dw_hid;
		%  <--- dopln
	   	E += e;

   	end


   	%%%%%%%%%%%%%%%%%%%%%%%
   	if mod(ep, 10) == 0
	   	ep
	   	E
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