close all;
clear;

%% Simulation parameters
% On décrit ci-après les paramètres généraux de la simulation

%modulation parameters
M = 4; %Modulation order 
Nframe = 10000;
Nfft=1024;
Nfftu = 512;
Ncp=8;
Ns=Nframe*(Nfft+Ncp);
N= log2(M)*Nframe*Nfft;
Nsamples = 100;

%Channel Parameters
Eb_N0_dB = 5; % Eb/N0 values
%Multipath channel parameters
hc1=[1 -0.9];
hc2=[0.227,0.46,0.688,0.46,0.227];
Lc1=length(hc1);%Channel length
Lc2=length(hc2);
H1=fft(hc1,Nfft);
H2=fft(hc2,Nfft);
%Preallocations
bits1 = randi([0 1],N/2,1);
bits2 = randi([0 1],N/2,1);
s1 = qammod(bits1,M,'InputType','bit');
s2 = qammod(bits2,M,'InputType','bit');
sigs1 = var(s1);
sigs2 = var(s2);

smat1 = reshape(s1,Nfftu,Nframe);
smat2 = reshape(s2,Nfftu,Nframe);

x1 = [fft(smat1);zeros(Nfftu,Nframe)];
x2 = [zeros(Nfftu,Nframe);fft(smat2)];
x1 = ifft(x1);
x2 = ifft(x2);

x1cp = [x1(end-Ncp+1:end,:);x1];
x2cp = [x2(end-Ncp+1:end,:);x2];
z1 = reshape(x1cp,1,Ns);
z2 = reshape(x2cp,1,Ns);
z1 = filter(hc1,1,z1);
z2 = filter(hc2,1,z2);
z = z1 + z2;

sig2b=10^(-Eb_N0_dB/10);
n = sqrt(sig2b/2)*randn(1,Ns)+1j*sqrt(sig2b/2)*randn(1,Ns);
y = z + n;
y = reshape(y,Nfft+Ncp,Nframe);
y = y(Ncp+1:end,:);

Y = fft(y);

%Y1 = [Y(1:Nfftu,:);zeros(Nfftu,Nframe)];
%Y2 = [zeros(Nfftu,Nframe);Y(Nfftu+1:end,:)];
W1 = conj(H1)./(abs(H1).^2 + sig2b/sigs1);
W2 = conj(H2)./(abs(H2).^2 + sig2b/sigs2);
Y1 = diag(W1(1:Nfftu))*Y(1:Nfftu,:);
Y2 = diag(W2(Nfftu+1:end))*Y(Nfftu+1:end,:);
S1 = ifft(Y1);
S2 = ifft(Y2);

xhat1 = qamdemod(S1(:),M,'outputType','bit');
xhat2 = qamdemod(S2(:),M,'outputType','bit');

BER1 = size(find([bits1(:)- xhat1(:)]),1)/length(bits1);
BER2 = size(find([bits2(:)- xhat2(:)]),1)/length(bits2);

figure;
plot(abs(z1(1:Nsamples)),'r')
hold on
plot(abs(z2(1:Nsamples)),'b')
legend("User 1","User 2")
xlabel("Samples (time)")
ylabel("Magnitude")
title("SC-FDMA signal")
