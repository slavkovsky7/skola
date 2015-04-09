#!/usr/bin/octave


%!!!!!!!!!!!!!!!!
% Upozornujem ze kod je v OCTAVE, matlab nemam a 
% stahovat ho z torrentov nebudem + nebola povinnost
% mat projekt v Matlabe. Nikde to nebolo specifikovane
%!!!!!!!!!!!!!!!!

% train   - trenovacie data
% classes - stlpcovy vektor [x1;x2;x3 .. ;xn] , cisla jednotlivych tried
% column  - index k pozicii hodnot vektora classes v datach
% labels - vysledna trieda
function P =  compute_probs(train_data, column, classes, labels)
	A_counts = zeros(labels);
	for i = 1 : size(labels)
		outclass = labels(i);
		A_counts(i) = sum(train_data(:,end) == outclass );
	end

	P = zeros( size(classes),4);
	for i = 1 :  size(classes)
		for j = 1 : size(labels)
			outclass = labels(j);
			P(i,j) = sum(train_data(:,column) == classes(i) & train_data(:,end) == outclass ) / A_counts(j);
		end
	end
end

% train   - trenovacie data
% labels - vysledna trieda
function P = compute_labels_probs(train_data,labels)
	P  = zeros(size(labels));
	for i = 1 : size(labels)
		P(i) = sum(train_data(:,end) == labels(i)) / size(train_data,1);
	end
end

% bayess    : pole matic s pravde
% P_labels 	: vystupne pravdepodobnosti
% sampe 	: v nasom pripade 1-6 vektor priznakov, stlpcovy
function result = classify(bayess, P_labels, sample, params )
	max = -1;
	for l = 1 : size(P_labels)
		P = P_labels(l);
		for ci = 1 : size(sample)
			% najdi index k pravdepodobnosti podla sample(c)
			param_index = 1;
			for j = 1 : size(params{ci})
				if params{ci}(j) == sample(ci)
					param_index = j;
					break;
				end
			end			

			P = P * bayess{ci}(param_index,l);
		end

		if ( P > max )
			result = l;
			max = P;
		end
	end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

data = load('-ascii','car.data');

train_data = data;

labels = [1;2;3;4];
params = { [1;2;3;4] , [1;2;3;4], [2;3;4;5] [2;4;5] , [1;2;3] , [1;2;3]} ;


P_labels = compute_labels_probs(train_data, labels);


P_BPrice  = compute_probs(train_data, 1, params{1}, labels );
P_MPrice  = compute_probs(train_data, 2, params{2}, labels );
P_Doors   = compute_probs(train_data, 3, params{3}, labels );
P_Capcity = compute_probs(train_data, 4, params{4}, labels );
P_Luggage = compute_probs(train_data, 5, params{5}, labels );
P_Safety  = compute_probs(train_data, 6, params{6}, labels );


Bayess = {P_BPrice; P_MPrice;P_Doors;P_Capcity;P_Luggage;P_Safety};


%%%%%%%% Accuracy testing %%%%%%%% 

% 				label(i)  :true label(i)  , false label(i)
%				label(i+1):true label(i+1), false label(i+1)
Confusion_matrix = zeros(size(labels),size(labels));

OK = 0;
N = size(data,1);

for i = 1 : N
	%printf("--------------\n");
	sample = train_data(i, 1 : end - 1 )';
	actual = train_data(i, end );
	predicted = classify(Bayess, P_labels, sample, params); 

	if ( actual == predicted )
		OK++;
	end


	Confusion_matrix(actual, predicted) += 1;
end


OK
Accuracy = OK / N
Confusion_matrix