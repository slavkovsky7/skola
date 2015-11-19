function ret = slerp(q1, q2, t)
	q1=q1/norm(q1)
	q2=q2/norm(q2)
	d = dot(q1, q2)
	a = round(acos( d ) * 100000)/100000
	if a == 0
	  ret = q1
	else
	  l_t_a=(1-t)*a
	  t_a=t*a
	  
	  
	  
	  sinq0=sin((1-t)*a)/sin(a)
	  sinq1=sin(t*a)/sin(a)

	  p1=sinq0*q1 
	  p2=sinq1*q2
	  ret = p1 + p2; 

	  norm(ret)
	end
end


q00=[1,2,3,pi/2];
q01=[0,2,8,pi/4];
q02=[5,5,5,pi/2];
q03=[5,5,5,pi/2];
t=1/3;

%1 krok 
q10=slerp(q00,q01,t+1)
printf("----------------\n")
q11=slerp(q01,q02,t)
printf("----------------\n")
q12=slerp(q02,q03,t-1)
printf("----------------\n")
q20=slerp(q10,q11,(t+1)/2)
printf("----------------\n")
q21=slerp(q11,q12,t/2)
printf("----------------\n")
result=slerp(q20,q21,t)

norm(result)

%q10=round(q10*1000)/1000
%nq10=q10/norm(q10)


%2 krok
%printf('------------------\n')
%q11=slerp(q01,q02,t);
%q11=round(q11*1000)/1000