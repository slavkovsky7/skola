function [] = display_image(V,r,c)

figure;
%clr;
colormap(gray);

[velk,N] = size(V);
velk = ceil(sqrt(N));
for i = 1:N
	subplot(velk,velk,i);
	imagesc(reshape(V(:,i),r,c),[0,255]);
	axis('off')
	title(['face ' num2str(i)]);
end

