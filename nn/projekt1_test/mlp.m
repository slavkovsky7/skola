%nacitanie dat

function [h,y] = forward_pass(x, w)
  x = x(1:end-1);
  h{1} = x;
  h_net{1} = x; 
  %DEBUG printf("...FW Pass begin..............\n")
  for i = 2 : size(w,2) + 1
      net = w{i-1} * [h{i-1};-1];
      h{i} = logsig(net);
      y = h{i};
      %DEBUG net_ = net'
      %DEBUG hi_ = h{i}'
  end
  %DEBUG printf("...FW Pass end..............\n")
endfunction

% =======================
clear
%load iris

data = load('-ascii','2d.trn.dat');

data = data';

[n_input,n_data] = size(data);
n_out = max(data(n_input,:));
%data = data(:,randperm(n_data));

% rozdel data na trenovaci a na testovaci set
train_set = data(:,1:end);
test_set = data(:,700:end);

n_train = size(train_set,2);
n_test = size(test_set,2);

%vygeneruj vahy, nastav parametre
alpha = 0.1;   %  <--- uprav
layers = [ n_input - 1, 8 , n_out ];      %  <--- uprav
w = [];

for i = 2 : size(layers,2)
  w{i - 1} = rand( layers(i) ,layers(i-1) + 1);
  dw{i - 1} = zeros(size(w{i-1}));
end


w

errors = [];
E = 1;
ep = 0;
while (E > 0.9 && ep < 10000 )
%for ep = 1 : 100
   ep++;
   E = 0;
   % trenovanie
   train_set = train_set(:,randperm(n_train));
   for t=1:n_train
        %prechod
        x = train_set(:,t);
        

        %DEBUG printf("======================================\n")
        [h, y] = forward_pass( x, w );

        target = zeros(n_out,1);
        target(train_set(n_input,t)) = 1;
        %y = y > 0.5;
        e = (1/2)*sum( (target - (y > 0.5) ).^2 );

        %DEBUG e_ = e
        %DEBUG x_ = x'
        %DEBUG target_ = target'
        %DEBUG y'
        
        %%%%%%%%%%%%%%%%%%%%%%%%%%
        %DEBUG  printf("...BW Pass begin..............\n")
        if e > 0
          for i = size(w,2): -1 : 1
            %DEBUG printf("...l\n")
            der = (h{i+1}.*(1 - h{i+1}));
            %DEBUG der'
            if i == size(w,2)
              delta{i} = (target - y).*der;
            else
              w_unbias = w{i+1}(:,1:end-1);
              delta{i} = (w_unbias'*delta{i+1}).*der;  

            end
            %DEBUG delta_i = delta{i}'

            tmp = alpha*delta{i}*([h{i};-1]');%; + m(i)*dw{i};  
           %DEBUG  tmp_ = tmp
            w{i} = w{i} + tmp;
           %DEBUG  w_i = w{i}
          end
        end
        %DEBUG printf("...BW Pass end..............\n")
        %DEBUG w
        %%%%%%%%%%%%%%%%%%%%%%%%%%
        E += e;

   end
   % testovanie

   %errors = [errors , E];
   
   if mod(ep, 10) == 0
    E
    ep
   end
end

