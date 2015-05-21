function [data] = load_images(N)

x = 10;

fprintf(1,'Loading images as matrices...');
data = zeros(x*x,N);

for i = 1:N
	Z = (imread(['img' num2str(i) '.bmp'])  );
	Zn = reshape(Z(:,:,1),x*x,1);
    data(:,i) = Zn;
end

data = (data / 255 * 2 ) -1;


% clean unused variables
clear Z;
clear i;
