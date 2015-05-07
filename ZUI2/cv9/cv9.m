
function [maxval, imax, jmax ] = argmax(m)
	imax = 1;
	jmax = 1;
	for i = 1 : size(m, 1)
		for j = 1 : size(m, 2)
			if m(i,j) > m(imax, jmax)
				imax = i;
				jmax = j;
			end
		end
	end
	maxval = m(imax, jmax);
end

function [X,finalP] = viterbi(obs, states, start_p, trans_p, emit_p)
	V = [];
	V2 = [];
	for j = 1 : size(states,2)
		V(1,j) = start_p(j)*emit_p(j, obs(1) );
		V2(1,j) = j;
	end

	for i = 2 : size(obs,2)
		for j = 1 : size(states, 2)
			V(i,j) = 0;
			for k = 1 : size(states,2)
				val = V(i-1,k)*trans_p(k,j)*emit_p(j, obs(i) );
				if (val > V(i,j) )
					V(i,j) = val;
					V2(i,j) = k;
				end
			end
		end
	end

	
	X = zeros(size(obs,2),1);
	Z = zeros(size(obs,2),1);
	
	[val, index ] = max(V(end,:));
	Z(end) = index;
	X(end) = Z(end);

	for i = size(V2, 1) : -1 : 2
		Z(i-1) = V2(i, Z(i));
		X(i-1) = Z(i-1);
	end
	
	V

	X
	finalP = val
end


states = {'Healthy', 'Fever'};
observations = {'normal', 'cold', 'dizzy'};

% T(N,N)=transition probability matrix
trans_p =  [[0.8, 0.2];
     		[0.3, 0.7]];
% E(N,M)=Emission matrix
emit_p = [[0.6, 0.3, 0.1];
     	  [0.1, 0.2, 0.7]];
% pi=initial probability matrix
start_p = [0.6, 0.4];

% O=Given observation sequence labelled in numerics
% 1 = 1st observation type, 2 = 2nd observation type,..
obs = [1, 2, 2, 3, 1, 1];
%obs = [3, 3, 1, 3, 3, 2, 2];
%Pr(obs)  path   Pr(path)
viterbi(obs,states,start_p,trans_p,emit_p);