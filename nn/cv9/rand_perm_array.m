function [out] = rand_perm_array(data)
n_data = size(data,2);
r = randperm(n_data);

for j=1:n_data
    out{j} = data{r(j)};
end