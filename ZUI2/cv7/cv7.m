


% HMM Forward algoritm
clear all;




function p=forward_alg(o,a,e,pi)
	n_states=length(a(1,:));
	n_input=length(o);
	for i=1:n_states        %it is initilization
	    f(1,i)=e(i,o(1))*pi(i);
	end
	for t=1:(n_input-1)      %recurssion
	    for j=1:n_states
	        s=0;
	        for i=1:n_states
	            s=s+a(i,j)*f(t,i);
	        end
	        f(t+1,j)=s*e(j,o(t+1));
	    end
	end
	s = sum( f(end,:) )
	f(end,:) = f(end,:) ./ s; 
	
	f(end,:)
end



%%%%%%%%%%%%%%%%%%%%%%%%%%
% T(N,N)=transition probability matrix
T = [[0.7, 0.3]; ... % if it is raining today, there's 70% chance it will rain tomorrow
    [0.3, 0.7]];     % it is sunny today
% E(N,M)=Emission matrix
E = [[0.9, 0.1]; ... % raining, boss will most likely have umbrella (90%)
    [0.2, 0.8]];     % sunny, stil chance of umbrella
% pi=initial probability matrix
pi = [0.5;0.5];

% O=Given observation sequence labelled in numerics
% 1=True, 2=False
%o = [2;2;2;2;2;2;2;2;1];
o = [1;2 ]
forward_alg(o,T,E,pi);
% Output
% P=probability of the last state given a sequence of observations


% don't forget to normalize
