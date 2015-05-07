
% HMM Forward-backward algoritm
clear all;


function f=forward_pass(o,a,b,pi)
	n_states=length(a(1,:));
	n_observations=length(o);
	f=zeros(n_observations, n_states);
	for i=1:n_states        %it is initilization
	    f(1,i)=b(i,o(1))*pi(i);
	end
	for t=1:(n_observations-1)      %recurssion
	    for j=1:n_states
	        s=0;
	        for i=1:n_states
	            s=s+a(i,j)*f(t,i);
	        end
	        f(t+1,j)=s*b(j,o(t+1));
	    end
	end
	for i = 1 : size( f, 1 )
		s = sum( f(i,:) );
		f(i,:) = f(i,:) ./ s; 
	end
end

function bw=backward_pass(o,a,b)
	n_states=length(a(1,:));
	n_observations=length(o);
	bw=zeros(n_observations, n_states);
	for i=1 : n_states
		bw(end,i) = 1;
	end

	for t = n_observations - 1 : -1 : 1
		for i = 1 : n_states
			bw(t,i) = 0;
			for j = 1 : n_states
				bw(t,i) += bw(t+1, j) * a(i,j) * b(j, o(t+1) );
			end
		end 
	end

end



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5

% T(N,N)=transition probability matrix
T = [[0.7, 0.3]; ... % if it is raining today, there's 70% chance it will rain tomorrow
    [0.3, 0.7]];     % it is sunny today
% E(N,M)=Emission matrix
E = [[0.9, 0.1]; ... % raining, boss will most likely have umbrella (90%)
    [0.2, 0.8]];     % sunny, stil chance of umbrella
% pi=initial probability matrix
pi = [0.5, 0.5];

% O=Given observation sequence labelled in numerics
% 1=True, 2=False
o = [1;2;1;1;2];

% Output
% P=probability of the last state given a sequence of observations
n_states=length(T(1,:));
n_observations=length(o);

f = forward_pass(o,T,E, pi)
b = backward_pass(o,T,E)

%posterior
for i=1:n_observations
    
end

% don't forget to normalize
