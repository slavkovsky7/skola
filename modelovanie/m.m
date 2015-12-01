1;
source('func.m');

% a
display('a)');
A = [[0;0;10], [0;5;0], [5;0;0], [-5;-5;0]]
B = [[50;50;50], [30;32;25], [25;30;30], [35;30;30]]
K = cov(B')

% b
display('b)');
[vektory, lambda] = eig(K);
lambda1 = lambda(9)
lambda2 = lambda(5)
lambda3 = lambda(1)
d1 = -vektory(:, 3)
d2 = -vektory(:, 2)
d3 = -vektory(:, 1)

% c
display('c)');
xMax = max(A(1,:));
xMin = min(A(1,:));
yMax = max(A(2,:));
yMin = min(A(2,:));
zMax = max(A(3,:));
zMin = min(A(3,:));
Abox.d1 = [1; 0; 0];
Abox.d2 = [0; 1; 0];
Abox.d3 = [0; 0; 1];
Abox.l1 = (xMax-xMin)/2;
Abox.l2 = (yMax-yMin)/2;
Abox.l3 = (zMax-zMin)/2;
Abox.c = [(xMax+xMin)/2; (yMax+yMin)/2; (zMax+zMin)/2];
%Abox.c = [0; 0; 2.5];
Abox

B
D = [d1'; d2'; d3']
Btransformed = D*B
xMax = max(Btransformed(1,:))
xMin = min(Btransformed(1,:))
yMax = max(Btransformed(2,:))
yMin = min(Btransformed(2,:))
zMax = max(Btransformed(3,:))
zMin = min(Btransformed(3,:))
Bbox.d1 = d1;
Bbox.d2 = d2;
Bbox.d3 = d3;
Bbox.l1 = (xMax-xMin)/2;
Bbox.l2 = (yMax-yMin)/2;
Bbox.l3 = (zMax-zMin)/2;
cTransformed = [(xMax+xMin)/2; (yMax+yMin)/2; (zMax+zMin)/2]
% cTransformed = [33;35.25;33.75];
D'
Bbox.c = (cTransformed'*D)';
Bbox

% d

function s = cross2(u,v)
	s = zeros(size(u));
	s(1) = u(2)*v(3) - u(3)*v(2); 
	s(2) = u(3)*v(1) - u(1)*v(3);
	s(3) = u(1)*v(2) - u(2)*v(1);

	printf('[%f*%f - %f*%f;\n%f*%f - %f*%f;\n%f*%f - %f*%f\n] ', u(2),v(3),u(3),v(2),u(3),v(1),u(1),v(3),u(1),v(2),u(2),v(1) )
end

display('d)');
d1a = Abox.d1
d2a = Abox.d2
d3a = Abox.d3
d1b = Bbox.d1
d2b = Bbox.d2
d3b = Bbox.d3

d1aXd1b = cross2(Abox.d1, Bbox.d1)
d1aXd2b = cross2(Abox.d1, Bbox.d2)
d1aXd3b = cross2(Abox.d1, Bbox.d3)
d2aXd1b = cross2(Abox.d2, Bbox.d1)
d2aXd2b = cross2(Abox.d2, Bbox.d2)
d2aXd3b = cross2(Abox.d2, Bbox.d3)
d3aXd1b = cross2(Abox.d3, Bbox.d1)
d3aXd2b = cross2(Abox.d3, Bbox.d2)
d3aXd3b = cross2(Abox.d3, Bbox.d3)

% e
display('e)');
rAB = Bbox.c - Abox.c
v1 = d1b
v2 = d1aXd2b;
v3 = d2aXd3b;
sAB1 = abs(dot(v1, rAB))
sAB2 = abs(dot(v2, rAB))
sAB3 = abs(dot(v3, rAB))

% f
function print_f() 
end

display('f)');

hA1 = abs(dot(v1, d1a*Abox.l1)) + abs(dot(v1, d2a*Abox.l2)) + abs(dot(v1, d3a*Abox.l3))
hA2 = abs(dot(v2, d1a*Abox.l1)) + abs(dot(v2, d2a*Abox.l2)) + abs(dot(v2, d3a*Abox.l3))
hA3 = abs(dot(v3, d1a*Abox.l1)) + abs(dot(v3, d2a*Abox.l2)) + abs(dot(v3, d3a*Abox.l3))
hB1 = abs(dot(v1, d1b*Bbox.l1)) + abs(dot(v1, d2b*Bbox.l2)) + abs(dot(v1, d3b*Bbox.l3))
hB2 = abs(dot(v2, d1b*Bbox.l1)) + abs(dot(v2, d2b*Bbox.l2)) + abs(dot(v2, d3b*Bbox.l3))
hB3 = abs(dot(v3, d1b*Bbox.l1)) + abs(dot(v3, d2b*Bbox.l2)) + abs(dot(v3, d3b*Bbox.l3))


% g
display('g)');
display('test 1:');
sAB1 < hA1+hB1
display('test 2:');
sAB2 < hA2+hB2
display('test 3:');
sAB3 < hA3+hB3










% plot
figure;
hold on;
grid on;
e.c=[0;0;0]; e.d1=[1;0;0]; e.d2=[0;1;0]; e.d3=[0;0;1];
% axis(e, 25, 'o-g')
object(A, '.-b');
object(B, '.-b');
axis(Abox, 15, 'o-g');
axis(Bbox, 15, 'o-g');
box(Abox, '.-r');
box(Bbox, '.-r');
vc = (2*Abox.c+Bbox.c)/3;
% sepPlane(d1b, vc, 40, '.-.b');
% sepPlane(d1aXd2b, vc, 50, '.--b');
% sepPlane(d2aXd3b, vc, 60, '.-b');