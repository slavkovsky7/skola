%nacitanie dat

function [yy] = logsig_der(net)
yy = logsig(net);
for ix=1:size(net,1)
    for iy=1:size(net,2)
        yy(ix,iy) = yy(ix,iy)*(1 - yy(ix,iy) );
    end
end
endfunction

function [h, y, h_net, y_net] = forward_pass(x, w_hid, w_out)
    h_net = [w_hid * x];
    h = [logsig(h_net);-1];

    y_net = w_out * h;
    y = logsig(y_net);     
endfunction
% =======================
clear
load iris

[n_input,n_data] = size(data);
n_out = max(data(n_input,:));
data = data(:,randperm(n_data));

% rozdel data na trenovaci a na testovaci set
train_set = data(:,1:120);
test_set = data(:,121:150);

n_train = size(train_set,2);
n_test = size(test_set,2);

%vygeneruj vahy, nastav parametre
alpha =  0.1; % DOPLN
n_hid =  8;


%riadky vo w_hid sa priraduju hk, cize 6x5
w_hid = rand(n_hid, n_input); % DOPLN
w_out = rand(n_out, n_hid+1); % DOPLN

w_hid
w_out
size(w_out)

quit

errors = [];
E = 1;
ep = 0;
while (E > 0.9 && ep < 2000 )
%for ep = 1 : 100
   ep++;
   E = 0;
   % trenovanie
   train_set = train_set(:,randperm(n_train));
   for t=1:n_train
        %prechod
        x = train_set(:,t);
        x(end) = -1; %bias

        %================forward pass================
		    [h, y, h_net, y_net] = forward_pass(x, w_hid, w_out);
        
        % vyratanie targetu
        target = zeros(n_out,1);
        target(train_set(n_input,t)) = 1;

        %vyrataj chybu na vrstvach
        e = (1/2)*sum( (target - y ).^2 );
          
        %================backward pass================
        if e > 0
          delta_out = (target - y).*logsig_der(y_net);
          w_out = w_out + alpha*(delta_out*h');
           
          w_out_unbias = w_out(:, 1:end-1);
		      delta_hid = (w_out_unbias'*delta_out).*logsig_der(h_net);
		      w_hid = w_hid + alpha*delta_hid*x';
        end
       E += e;
   end
   % testovanie

   errors = [errors , E];
   
   E
   ep
end
figure;
plot(errors);

printf("============================\n");

%finalna klasifikacia
ok = 0;
for t=1:n_test
 x = test_set(:,t);
 x( size(x,1),:) = -1; %bias

 
 target = zeros(n_out,1);
 target(test_set(n_input,t)) = 1;
 [h, y, h_net, y_net] = forward_pass(x, w_hid, w_out);

 y = y > 0.5;
 if sum(y == target) >= 3 
   ok++;
 end
end

accuracy = ok / n_test

