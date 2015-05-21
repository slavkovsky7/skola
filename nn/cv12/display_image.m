function [] = display_image(V)

figure;
%clr;
r = 10;
c = 10
V = (V +1) /2 *255;
colormap(gray);

[velk,N] = size(V);
velk = ceil(sqrt(N));
for i = 1:N
	subplot(velk,velk,i);
	imagesc(reshape(V(:,i),r,c),[0,255]);
	axis('off')
end

