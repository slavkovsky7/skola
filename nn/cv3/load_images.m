clear all;

N = 9;
fprintf(1,'Loading images as matrices...');
Xv = cell(1,N);

for i = 1:N
	Z = load(['face' num2str(i) '.mat']);
	Xv{i} = double(Z.X);
end

fprintf(1,'done.\n');
[r,c] = size(Xv{1});
fprintf(1,'each image has size %dx%d\n',r,c); 

% Put into vector form
fprintf(1,'Converting images into vector form ...');
for i = 1:N
    Xv{i} = reshape(Xv{i},c*r,1);
end

fprintf(1,'done.\n');

% Design matrix
X = zeros(r*c,N);
for i = 1:N
	X(:,i) = Xv{i};
end

% clean unused variables
clear Z;
clear i;
