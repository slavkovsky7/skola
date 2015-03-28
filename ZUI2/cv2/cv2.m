function [Psurvive,Pclass,Pgender,Page] = train(train_data)
  total = size(train_data,1);
  
  prezili = sum(train_data(:,4) == 1);
  neprezili = sum(train_data(:,4) == 0);
  
  dosp = sum(train_data(:,2) == 1);
  deti = sum(train_data(:,2) == 0);
  
  c0      = sum(train_data(:,1) == 0 );
  c1      = sum(train_data(:,1) == 1 );
  c2      = sum(train_data(:,1) == 2 );
  c3      = sum(train_data(:,1) == 3 );
  
  muzi  = sum(train_data(:,3) == 1);
  zeny = sum(train_data(:,3) == 0);
  
  %printf("================================\n");
  
  c0Prezili 	 = sum(train_data(:,1) == 0  & train_data(:,4) == 1);
  c1Prezili      = sum(train_data(:,1) == 1  & train_data(:,4) == 1); 
  c2Prezili      = sum(train_data(:,1) == 2  & train_data(:,4) == 1);
  c3Prezili      = sum(train_data(:,1) == 3  & train_data(:,4) == 1) ;
  
  c0Neprezili    = sum(train_data(:,1) == 0  & train_data(:,4) == 0);
  c1Neprezili    = sum(train_data(:,1) == 1  & train_data(:,4) == 0) ;
  c2Neprezili    = sum(train_data(:,1) == 2  & train_data(:,4) == 0);
  c3Neprezili    = sum(train_data(:,1) == 3  & train_data(:,4) == 0) ;
 
  %printf("================================\n");
  
  dospPrezili    = sum(train_data(:,2) == 1 & train_data(:,4) == 1) ;
  detiPrezili    = sum(train_data(:,2) == 0 & train_data(:,4) == 1) ;
  
  dospNeprezili  = sum(train_data(:,2) == 1  & train_data(:,4) == 0) ;
  detiNeprezili  = sum(train_data(:,2) == 0  & train_data(:,4) == 0) ;
  
  %printf("================================\n");
  
  muziPrezili    = sum(train_data(:,3) == 1 & train_data(:,4) == 1);
  zenyPrezili    = sum(train_data(:,3) == 0 & train_data(:,4) == 1);
  
  muziNeprezili    = sum(train_data(:,3) == 1  & train_data(:,4) == 0);
  zenyNeprezili    = sum(train_data(:,3) == 0  & train_data(:,4) == 0);
  
  %printf("================================\n")
  
  Pclass = [];
  Pgender= [];
  Page   = [];
  Psurvive  = [];

  
  Pclass(1,1)  = c0Prezili/ prezili;
  Pclass(2,1)  = c1Prezili/ prezili;
  Pclass(3,1)  = c2Prezili/ prezili;
  Pclass(4,1)  = c3Prezili/ prezili;
   
  Pclass(1,2)  = c0Neprezili/ neprezili;
  Pclass(2,2)  = c1Neprezili/ neprezili;
  Pclass(3,2)  = c2Neprezili/ neprezili;
  Pclass(4,2)  = c3Neprezili/ neprezili;
  
  
  Pgender(1,1) = zenyPrezili/ prezili;
  Pgender(2,1) = muziPrezili/ prezili;
  
  Pgender(1,2) = zenyNeprezili/ neprezili;
  Pgender(2,2) = muziNeprezili/ neprezili;
  
  Page(1,1) = detiPrezili / prezili;
  Page(2,1) = dospPrezili / prezili;
  
  Page(1,2) = detiNeprezili / neprezili;
  Page(2,2) = dospNeprezili / neprezili;
  
  Psurvive(1,1) = neprezili / total;
  Psurvive(1,2) = prezili / total; 
 
endfunction


function ret = classify(sample, Plabel, Pclass, Pgender, Page )
  ret = 1;
  
    
  %printf("====================\n");
  %sample
  
  class  = sample(1,1) + 1;
  age    = sample(1,2) + 1;
  gender = sample(1,3) + 1;

  labelSize = size(Plabel,2);
  
  max = -1;
  for l=1:labelSize
    P = Plabel(1,l)*Pclass(class,l)*Pgender(gender,l)*Page(age,l);
    if P > max;
      max = P;
      ret = l - 1;
    end
  end
  %ret
endfunction


function [train_set, test_set] = splitData(data)
  n_data = size(data,1);
  data = data( randperm(n_data), :);
  n = size(data,1);
  s = floor(8*(n/10));
  train_set=data(1:s,:);
  test_set = data(s:n,:);
endfunction

function accuracy = testAccuracy(test_set, Psurvive, Pclass, Pgender, Page)
  n = size(test_set,1);
  ok = 0;
  for i=1:n
    sample = test_set(i,:);
    ret = classify(sample, Psurvive, Pclass, Pgender, Page );
    if ( sample(1,4) == ret ) 
      ok++;
    end
  end
  accuracy = ok / n;
endfunction


total = 0
for i=1:1
  data = load ( "-ascii", "titanic.txt");
  %[train_set, test_set] = splitData(data);
  [Psurvive,Pclass,Pgender,Page] = train(data);
  total += testAccuracy(data, Psurvive, Pclass, Pgender, Page );
  average = total / i
end


Pgender
Pclass
Page
Psurvive