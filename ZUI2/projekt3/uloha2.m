#!/usr/bin/octave


function [F,P] = filter(X,E,pi,obs)
	F = [];
	P = [];

	filtration = pi; 
	%F(end+1,:) = filtration;
	for t = 1: size(obs,2)
		prediction = X*filtration;
		P(end+1,:) = prediction';

		filtration = E(:,obs(t)).*prediction;
		filtration = filtration/sum(filtration);
		F(end+1,:) = filtration';
	end
end

function ret = predict_one_step(X,E,pi,obs)
	[F,P] = filter(X,E,pi,obs);
	ret = F(end,:)*X;
end

function ret = compute_recursive(X,E,obs,k)
	t = size(obs,2);
	if k >= t
		ret = 1;
	else
		ret = X*E(:,obs(k));
		rek = compute_recursive(X,E,obs,k+1);
		ret = ret.*rek;
	end
end

function ret = smooth(X,E,pi,obs,k)
	rek = compute_recursive(X,E,obs,k);
	[F,P] = filter(X,E,pi,obs);
	ret = rek'.*F(k,:); 
	ret = ret / sum(ret);
end

% 1 - daznik, 2 nie daznik
obs = [1,1];

pi = [0.5;0.5];

X = [[0.7, 0.3]; ... % if it is raining today, there's 70% chance it will rain tomorrow
    [0.3, 0.7]];     % it is sunny today

% E(N,M)=Emission matrix
E = [[0.9, 0.1]; ... % raining, boss will most likely have umbrella (90%)
    [0.2, 0.8]];     % sunny, stil chance of umbrella



[F,P] = filter(X,E,pi,obs)

predict = predict_one_step(X,E,pi,obs)

smooth = smooth(X,E,pi,obs,1)
