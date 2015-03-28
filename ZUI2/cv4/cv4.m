close all

probs = [0.2 0.2 0.2 0.2 0.4;
         0.7 0.4 0.3 0.3 0.4;
         0.4 0.3 0.2 0.4 0.4;
         0.1 0.2 0.3 0.5 0.3;
         0.1 0.3 0.4 0.7 0.3];

%Rejection sampling     
accumulator = zeros(5,5);
for i=1:5
    for j=1:5
        for z=1:100
	    % rand < probs(i,j)
            accumulator(i,j) = accumulator(i,j) + (rand < probs(i,j));
        end
    end
end

accumulator
mesh(accumulator);
title('Rejection sampling');

%Metropolis algorithm

% doplnte

rand