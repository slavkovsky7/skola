%nacitanie dat
clear
load data


n_in = 3;
n_out = 1;
n_data = size(data,2);

%vygeneruj vahy, nastav parametre
alpha =  0.1; % DOPLN
n_hid =  5; % DOPLN


w_inp = rand( n_hid, n_in + 1);
w_hid = rand( n_hid, n_hid + 1 ); % DOPLN
w_out = rand( n_out, n_hid + 1 ); % DOPLN


errors = [];

for ep = 1 : 100
   % trenovanie
   E = 0;
   data = rand_perm_array(data);

   epochErrorCount = 0;
   for t=1:n_data
       seq = data{t};
       target = seq(1);
       seq = seq(1:end-1);
	   

       %forward pass
	   h = {}; 
	   for i = 1 : size(seq,1)
	   	xi = zeros(n_in,1);
	   	xi(seq(i)+1) = 1;
	   	xi = [ xi; -1 ];
	   	if i < 2
	   		h{i} = logsig(w_inp*xi);
	   	else
	   		h{i} = logsig(w_inp*xi + w_hid*[h{i-1};-1]);
	   	end	
	   end

	   y = logsig(w_out*[h{end};-1] );
	   cy = y > 0.5;

	   
	   if (cy == target)
		   %backward pass
		   delta = {};
		   for i = size(seq, 1) + 1 : -1 : 1
				if ( i > size(seq,1) )
					delta{i} = y*(1 - y)*(target - y);
				else
					der = h{i}.*( 1 - h{i});
					if ( i == size(seq,1) )
						w_unbias = w_out(:,1:end-1);
						delta{i} = der.*(w_unbias*delta{i+1})';
					else

						w_unbias = w_hid(:,1:end-1);
						delta{i} = der.*(w_unbias*delta{i+1});
					end
				end 
		   end	  

		   delta
		   quit

		   %update vah
		   w_out = w_out + alpha*delta{end}*[ h{end};-1]';
		   delta_h = zeros(size(w_hid));
		   for i = 1 : size(h,2) - 1
		   		newDelta_h = delta{i+1}*[h{i};-1]';
		   		delta_h = delta_h + newDelta_h;
		   end
		   delta_h = alpha*delta_h;
		   w_hid = w_hid + delta_h;

		   w_in
		   delta_input = zeros(size(w_inp));
		   for i = 1 : size(seq,1)
		   	delta{i}
		   	seq(i)
		   	delta_input
		   	quit
		      delta_input = delta_input + delta{i}*seq(i);
		   end
		   w_inp = w_inp + delta_input;

		   quit
	   else
	   		epochErrorCount = epochErrorCount + 1 ;
       end  
   end
   
   epoch
   epochErrorCount

   errors = [errors, E];
end
figure;
plot(errors);
