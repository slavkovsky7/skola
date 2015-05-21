% ULOHA: jednokrokova predikcia dat

%nacitanie dat
clear
load data

n_in = 1;
n_out = 1;
n_data = size(data,2);


data

%vygeneruj vahy, nastav parametre
alpha =  0.1; % DOPLN
n_reservoair = 10 ; % DOPLN
percent_of_connections = 4; %DOPLN
spectral_radius = 5; %DOPLN

w_in = rand( 10 , 10 ); %DOPLN
w_hid = rand( 10 , 10); % DOPLN

% preried prepojenia
% uprav spektralny radius prepojeni, asi sa bude hodit funkcia eigs(w,1);

errors = [];

%zber dat
for i = 1 : n_data
   
end

% vyrataj w_out pomocou pseudoinverznej matice z nazbieranych dat


% vizualizuj originalne data a predikovane a vyrataj stvorec chyb
plot(data)