function [rts, info] = rootsOfCubic(a,b,c,d)
%Tyler Brabham
%Math 128A, Numerical Analysis
%
%cubic polynomial ax^3+bx^2+cx+d
%

info = 'empty for initialization'; %#ok<NASGU>

%defining variables
maxIter = 10000;
tol = 10^-13;
%%%%%%%%%%%%%%%%%%

%%%%FORMULAE
if d==0
    Q1 = @(a,b,c) (-b+sqrt(b^2-4*a*c))/(2*a);
    Q2 = @(a,b,c) (-b-sqrt(b^2-4*a*c))/(2*a);
else
    Q1 = @(a,b,c) 2*c/(-b-sqrt(b^2-4*a*c));
    Q2 = @(a,b,c) 2*c/(-b+sqrt(b^2-4*a*c));
end
%%%%%%%%%%%%%%%%%

for i = 195:-1:10
    if (abs(a)>10^i) && (abs(b)>10^i) && (abs(c)>10^i) && (abs(d)>10^i)
        a = (10^-i)*a;
        b = (10^-i)*b;
        c = (10^-i)*c;
        d = (10^-i)*d;
        break
    elseif (abs(a)<10^-i) && (abs(b)<10^-i) && (abs(c)<10^-i) && (abs(d)<10^-i)
        a = (10^i)*a;
        b = (10^i)*b;
        c = (10^i)*c;
        d = (10^i)*d;
        break
    end
end

%Here do the case when it is constant function...
if (a==0 && b==0 && c==0)
    if d==0
        rts = inf;
        info = 'There are an infinite number of Roots';
    else
        rts = [];
        info = 'There are no Roots';
    end
    
%here do the case when it is linear...
elseif (a==0 && b==0)
    info = 'There is one real root for this linear function';
    rts = linearRootFinder(c,d);
    
%here do the case when it is quadratic...
elseif (a==0)
    info = 'There are two Roots, possibly complex.';
    rts = zeros(2,1);
    RootGuess = zeros(2,1);
    RootGuess(1) = Q1(b,c,d);
    RootGuess(2) = Q2(b,c,d);
    
    
    %%%%Generates a guess for what the two Roots are using the quadratic
    %%%%formula in the case where neither root is complex
    if isreal(RootGuess(1)) && isreal(RootGuess(2))
        if RootGuess(1)==inf || isnan(RootGuess(1))
            RootGuess(1) = 10^15;
        end
        
        %%%%Now it calls Newton's method on the first rootguess to improve the
        %%%%guess
        rts(1) = quadraticRootFinder(b,c,d,RootGuess(1),maxIter,tol);
        
        
        %%%%Now it uses polynomial divison to get a linear function, then
        %%%%it calls newtonsMethod again to make sure the root has the
        %%%%proper error
        
        [a1,a2,a3] = polynomialDivision(0,b,c,d,rts(1)); %#ok<ASGLU>
        rts(2) = linearRootFinder(a2,a3);
        rts(2) = quadraticRootFinder(b,c,d,rts(2),maxIter,tol);
        info = [info, ' Both Roots are real in this case'];
        
        %%%%Now both Roots are complex. The only useful scheme is fixed
        %%%%point iteration using a square root.
    else
        rts(1) =  complexNewton(a,b,c,d,RootGuess(1),maxIter,tol);
        rts(2) = conj(rts(1));
    end
    
    %here do the case when it is cubic...
else
    info = 'There are three Roots. One is real, the other two might be complex';
    rts = zeros(3,1);
    
    %the first root should be real, while the other two may be complex.
    
    
    RootGuess = rootGuesser(a,b,c,d);
    
    %%%%This bit guarantees that the first guess of RootGuess is a real
    %%%%number and the next two are the complex Roots, if any. Otherwise,
    %%%%it just keeps them in the same order.
    hmm = 2;
    didIt = false;
    for i = 1:3
        if isreal(RootGuess(i)) && didIt==false
            rts(1) = complexNewton(a,b,c,d,RootGuess(i),maxIter,tol);
            didIt = true;
        else
            rts(hmm) = RootGuess(i);
            hmm = hmm+1;
            if hmm==4 && i==3
                break
            end
        end
    end
    
    
    %%%%So now it it guaranteed that the first root is real.
    rts(1) = complexNewton(a,b,c,d,rts(1),maxIter,tol);
    [e,f,g] = polynomialDivision(a,b,c,d,rts(1));
    rts(2) = Q1(e,f,g);
    rts(3) = Q2(e,f,g);
    rts(2) = complexNewton(a,b,c,d,rts(2),maxIter,tol);
    if ~isreal(rts(2))
        rts(3) = conj(rts(2));
    else
        rts(3) = complexNewton(a,b,c,d,rts(3),maxIter,tol);
    end
    
    
end

end %ends main function

%%%%%%%%%%%%%%%%%%%%%%%
% Polynomial Division %
%%%%%%%%%%%%%%%%%%%%%%%
function [e,f,g] = polynomialDivision(a,b,c,d,realRoot)
%takes in a cubic polynomial and then uses long division to determine
%the coefficients for the factored quadratic polynomial. This will allow
%one to determine the complex Roots after determining the first real root.

%%%%PRODUCES A QUADRATIC FUNCTION FROM A CUBIC
if (a~=0)
    if abs(a*realRoot^3)>abs(d)
        e = a;
        g = -d/realRoot;
        f = (g-c)/realRoot;    
    else
        e = a;
        f = b+realRoot*a;
        g = c+realRoot*(f);
    end  
    %%%%PRODUCES A LINEAR FUNCTION FROM A QUADRATIC
elseif (b~=0)
    e = 0;
    f = b;
    g = (c+b*realRoot);
end

end %ends subfunction polynomialDivision

%%%%%%%%%%%%%%%%%%%
% Newton's Method %
%%%%%%%%%%%%%%%%%%%
function [Root] = newtonsMethod(a,b,c,d,guess,iterations,tol)

%a,b,c,d are the cubic coefficients. Use this to produce f and df for
%newton's method. guess is the initial guess for Newton's method
%iterations is the max iterations before newton's method fails

f = [a,b,c,d];
df = [3*a,2*b,c];
RootError = abs(polyval(f,guess));
Root = guess;

previousRoot = inf;

i = 0;
while ((RootError>tol || sqrt(abs(previousRoot-Root))>tol) && i<iterations)
    previousRoot = Root;
    Root = Root-polyval(f,Root)/polyval(df,Root);
    RootError = abs(polyval(f,Root));
    i = i+1;
end

end %ends subfunction newtonsMethod

function [out] = complexNewton(a,b,c,d,guess,maxIter,tol)

f = [a,b,c,d];
df = [3*a,2*b,c];
RootError = sqrt(abs(polyval(f,guess)*conj(polyval(f,guess))));
Root = guess;

i = 0;
while ((RootError>tol) && i<maxIter)
    Root = Root-polyval(f,Root)/polyval(df,Root);
    RootError = sqrt(abs(polyval(f,Root)*conj(polyval(f,Root))));
    i = i+1;
end

out = Root;

end %ends subfunction complexNewton

%%%%%%%%%%%%%%%%%%%%%%
% Basic Root Finders %
%%%%%%%%%%%%%%%%%%%%%%
function [out] = linearRootFinder(a,b)
%ax+b

%one and only one root
out = (-b)/a;

end %ends subfunction linearRootFinder

function [out] = quadraticRootFinder(b,c,d,guess,iterations,tol)

out = newtonsMethod(0,b,c,d,guess,iterations,tol);

end %ends subfunction quadraticRootFinder

function [rootGuess] = rootGuesser(a,b,c,d)
tol = 10^-10;

rootGuess = zeros(3,1);

Q = sqrt((2*b^3-9*a*b*c+27*d*a^2)^2-4*(b^2-3*a*c)^3);
C = (.5*(Q+2*b^3-9*a*b*c+27*d*a^2))^(1/3);

rootGuess(1) = (-b/(3*a)) - (C/(3*a))-(b^2-3*a*c)/(3*a*C);
rootGuess(2) = (-b/(3*a))+ C*(1+1i*sqrt(3))/(6*a)+(1-1i*sqrt(3))*(b^2-3*a*c)/(6*a*C);
rootGuess(3) = (-b/(3*a))+ C*(1-1i*sqrt(3))/(6*a)+(1+1i*sqrt(3))*(b^2-3*a*c)/(6*a*C);

for i = 1:3
    if abs(imag(rootGuess(i)))<tol
        rootGuess(i) = real(rootGuess(i));
    end
end

end %ends sunfunction rootGuesser