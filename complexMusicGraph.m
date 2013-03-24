function [] = complexMusicGraph(Zin, fcn, n)
%Zin is a complex number of the form x+iy. The output of
%complexMusicFunction is another complex number. The functional calls
%itself repeatedly.

%The function plays the input value as a musical note, where the angle of
%Zin represents the pitch (0 is 440 Hz, 2*pi is 880 Hz), and the magnitude
%is the amplitude of the wave. If the amplitude is greater than 1000, than
%the amplitude just is set to 1000. Then fcn is applied to Zin, producing a
%new complex number. The function complexMusicGraph() is then called
%recursively to produce contiued sounds. Param n is the number of
%iterations to be implemented.

%Right now, the circle which corresponds to pitches is just uniformly
%broken apart. Eventually, it will need to be broken apart into distinct
%pitches, which is not uniform.

%Some good start values.
%complexMusicGraph(-5-i,@(x) log(x).*cos(x).*exp(x),1000)
%complexMusicGraph(-2+2i,@(x) log(x.^2).*sin(x).*exp(x),1000)
%complexMusicGraph(-5-i,@(x) log(x.^2).*sin(x).*exp(x),1000)
%complexMusicGraph(1-i*pi/2,@(x) log(x.^2).*sin(x).*exp(x),1000)
%complexMusicGraph(-5-i,@(x) log(x).*acos(x).*exp(x),1000)

startValue = Zin;

A = 440; %in hertz
C = 523.25;

T = .25/2; %period
step = .000125;
t = (0:step:T);
w = zeros(n,length(t));

notes = zeros(n,2);
for i = 1:n
    oldZin = Zin;
    [x,y] = producePitchMag(Zin,C);
    notes(i,1) = x;
    notes(i,2) = y;
    Zin = fcn(Zin);
    if (mod(real(Zin)/real(oldZin),10)==0) && (mod(imag(Zin)/imag(oldZin),10)==0)
        Zin = startValue;
    end
end

for i = 1:n
    for j = 1:length(t)
        w(i,j) = notes(i,2)*sin(2*pi*notes(i,1)*t(j));
    end
end


for i = 1:n
    sound(w(i,:));
end

end %ends main function

function [pitch,mag] = producePitchMag(Zin,C)

theta = angle(Zin);
if isnan(theta)
    theta = 1;
end

mag = sqrt(imag(Zin)^2+real(Zin)^2);
if (mag>1000 || isnan(mag))
    mag = 1000;
end

pitch = C*(1+theta/(2*pi));

% %%A MINOR
% if pitch<493.88
%     pitch = 440;
% elseif pitch<523.25
%     pitch = 493.88;
% elseif pitch<587.33
%     pitch = 523.25;
% elseif pitch<659.26
%     pitch = 587.33;
% elseif pitch<698.46
%     pitch = 659.26;
% elseif pitch<783.99
%     pitch = 698.26;
% else
%     pitch = 783.99;
% end

%C MAJOR
% if pitch<587.33
%     pitch = 523.25;
% elseif pitch<659.26
%     pitch = 587.33;
% elseif pitch<698.46
%     pitch = 659.26;
% elseif pitch<783.99
%     pitch = 698.26;
% elseif pitch<880
%     pitch = 783.99;
% elseif pitch<987.77
%     pitch = 880;
% elseif pitch<1046.50
%     pitch = 987.77;
% else
%     pitch = 1046.5;
% end

% %MINOR PENTATONIC
if pitch<523.25
    pitch = 440;
elseif pitch<587.33
    pitch = 523.25;
elseif pitch<659.26
    pitch = 587.33;
elseif pitch<783.99
    pitch = 659.26;
else
    pitch = 783.99;
end

end %ends subfunction producePitchMag
