function [Int,fcnt,info] = adaptiveSimpsons(fun,a,b,tol)
%uses adapative simpson quadrature method numerical integrate the function
%handle fun. tol is the error.

%level is the maximum number of recursions.
level = 10;

%info is useless
info = 'blank';

%total number of times that the function fun has to be evaluated for the
%entire process of integration.

h = (b-a)/2;
f = [fun(a),fun(a+h),fun(b)];
fcnt1 = 3;
Int1 = (h/3)*(f(1)+4*f(2)+f(3));

[Int,fcnt,info] = adaptiveSimpsons(fun,a,b,h,Int1,f,tol,level);
fcnt = fcnt1+fcnt;

end %ends main function

function [Int,fcnt,info] = adaptiveSimpsons(fun,a,b,h,prevInt,prevf,tol,level)
%actually does all the work for the main function, but has level as an
%input so it can be implemented recursively.

%prevInt is the previous integral which led to this call of
%adaptiveSimpsons, and prevf is all the previous function evaluations.
level = level-1;
fcnt = 0;
info = 'blank for now';
Int1 = prevInt;

if level>0
    newf = [fun(a+(h/2)), fun(a+(3*h/2))];
    fcnt = 2;
    Int2 = (h/6)*(prevf(1)+4*newf(1)+prevf(2));
    Int3 = (h/6)*(prevf(2)+4*newf(2)+prevf(3));
    
    if abs(Int1-Int2-Int3)>=15*tol
        prevf2 = [prevf(1) newf(1) prevf(2)];
        prevf3 = [prevf(2) newf(2) prevf(3)];
        [Int2,fcnt2,info2] = adaptiveSimpsons(fun,a,a+h,h/2,Int2,prevf2,tol,level);
        [Int3,fcnt3,info3] = adaptiveSimpsons(fun,a+h,b,h/2,Int3,prevf3,tol,level);
        Int = Int2+Int3;
        fcnt = fcnt+fcnt2+fcnt3;
    else
        Int = Int1;
    end
else
    Int = Int1;
end

end %ends subfunction adaptiveSimpsons.
