clear
load pdata 

alpha=0.001;
n = size(data,1);
m = n - 1 ;

train_data = data;
%varv = var(data');
%for i=1 : size(varv,2)
%  train_data(i,:) = (1/varv(i))*train_data(i,:);
%end


W = rand(m,n);

R = rand(m,m);
U = tril(R,-1)


for ep=1:500
  for t=1:size(data,2)
    x = train_data(:,t);
    y = zeros(m,1);
    

    
    
    y = W*x;
    y = y + U*y;
        
  

    
    %OJA RULE
    for i=1:n
      delta_wi = alpha*y*(x(i) - W(:,i)'*y);
      W(:,i) = W(:,i) + delta_wi;
      %W(:,i) = W(:,i) / norm(W(:,i));
      %delta_wi = alpha*y*x(i);
      %wi = W(:,i) + delta_wi;
      %W(:,i) = wi / norm(wi);
      
    end

    for i=1:m
      W(i,:) = W(i,:) / norm(W(i,:));
    end
    %W = W / norm(W);
    %norm(W)
    %quit
    %W

    %y = y / norm(y);
    for i=1:m
      for k=1:m
          U(i,k) = U(i,k)-alpha*y(i)*( y(k) + y(i)* U(i,k) ); 
      end
    end
    

    
    
    U = tril(U,-1);


  end


  ep
  W

end


W
U

W(1,:)
norm(W(1,:))


hold on

p1 = [-0.6;0.6] * W(1,:);
p2 = [-0.6;0.6] * W(2,:);

plot3(data(1,:),data(2,:),data(3,:),'o',p1(:,1),p1(:,2),p1(:,3),'b',p2(:,1),p2(:,2),p2(:,3),'g')
grid
axis([-0.6,0.6,-0.6,0.6,-0.6,0.6])