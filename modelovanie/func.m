1;
function c = cova(X,Y)
	n = max(size(X))
	xStred = (X - mean(X))
	yStred = (Y - mean(Y))
	vynasobene = xStred.*yStred
	suma = sum(vynasobene)
	c = suma/(n-1)
end

function box(objBox, arg)
	d1 = objBox.d1*objBox.l1;
	d2 = objBox.d2*objBox.l2;
	d3 = objBox.d3*objBox.l3;
	face(objBox.c+d1, d2, d3, arg);
	face(objBox.c-d1, d2, d3, arg);
	face(objBox.c+d2, d1, d3, arg);
	face(objBox.c-d2, d1, d3, arg);
	face(objBox.c+d3, d1, d2, arg);
	face(objBox.c-d3, d1, d2, arg);
end

function face(c, d1, d2, arg)
	v = [c+d1+d2, c-d1+d2, c-d1-d2, c+d1-d2, c+d1+d2];
	plot3(v(1,:), v(2,:), v(3,:), arg);
end

function object(A, arg)
	A = A(:, [1,2,3,4,1,3,2,4]);
	plot3(A(1,:), A(2,:), A(3,:), arg);
end

function axis(objBox, m, arg)
	e1 = [objBox.c, objBox.c+objBox.d1*m];
	e2 = [objBox.c, objBox.c+objBox.d2*m];
	e3 = [objBox.c, objBox.c+objBox.d3*m];
	plot3(e1(1,:), e1(2,:), e1(3,:), arg);
	plot3(e2(1,:), e2(2,:), e2(3,:), arg);
	plot3(e3(1,:), e3(2,:), e3(3,:), arg);
end

function sepPlane(v, c, m, arg)
	d1 = cross(v, [1;0;0]);
	d1 = d1/norm(d1)*m/2;
	d2 = cross(v, d1);
	d2 = d2/norm(d2)*m/2;
	face(c, d1, d2, arg);
end

% a = 1
% b = -329.1697
% c = 6875.62
% d = -37043.1

% p = -b/(3*a)
% q = p^3 + (b*c-3*a*d)/(6*a^2)
% r = c/(3*a)

% deter = q^2 + (r - p^2)^3