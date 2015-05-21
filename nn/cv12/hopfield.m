%load data .. kolko obrazkov nacitat .. max 7
samples = 7;

data = load_images(samples);

%zobrazenie dat
%display_image(data);

[n_input,n_data] = size(data);

J = zeros(n_input, n_input);
for i=1:n_input
	for j=1:n_input
		if i ~= j
			for p=1:n_data
				x = data(:,p);
				J(i,j) = J(i,j) + x(i,1)*x(j,1);
			end
		end
	end
end

%TUTO VYRATAJ J

%spominanie
%x = corrupt_random(ones(100,1),0.5); % nahodny sum
for xi = 1 : samples
	x = corrupt_random(data(:,xi),0.05); % zasumeny obrazok
	last = ones(100,1);

	S = x;
	energies = [];
	r = [];
	ep=0
	%for t=1:50
	while all(x==last) == 0
	    r = [r,x];
	    last = x;
	    %memory
	    si_new = S;

	    
	    for i=1:size(S,1)
			%i = randi([1 size(S,1)],1,1);
	    	for j=1:size(S,1)
	    		si_new(i) = si_new(i) + J(i,j)*S(j);
	    	end
	    	si_new(i) = sign(si_new(i));

	    	%break
	    end
	    S = si_new;
	    %SEM DOPLN DOPREDNY PRECHOD

	    x = S;

	    ep = ep + 1
	    if ep > 1000
	    	break;
	    end

	    energy = 0;
	    for i=1:size(S,1)
	    	for j=1:size(S,1)
	    		energy = energy + J(i,j)*S(i)*S(j);
	    	end
	    end
	    energy = (-1/2)*energy;
	    energies(end+1) = energy;


	end
	display_image(r);
	%figure;
	plot(energies)
end