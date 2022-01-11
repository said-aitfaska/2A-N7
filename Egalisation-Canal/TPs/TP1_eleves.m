
% Script for computing the BER for BPSK/QPSK modulation in ISI Channels
% 
close all;
clear;

%% Simulation parameters
% On décrit ci-après les paramètres généraux de la simulation

%Frame length
M=4; %2:BPSK, 4: QPSK
N  = 1000000; % Number of transmitted bits or symbols
Es_N0_dB = [0:10]; % Eb/N0 values
%Multipath channel parameters
%hc=[1 0.8*exp(1i*pi/3) 0.3*exp(1i*pi/6) ];%0.1*exp(1i*pi/12)];%ISI channel
hc=[0.04, -0.05, 0.07, -0.21, -0.5, 0.72, 0.36, 0, 0.21, 0.03, 0.07];
%a=1.2;
%hc=[1 -a];
Lc=length(hc);%Channel length
ChannelDelay=0; %delay is equal to number of non causal taps

%Preallocations
nErr_zfinf=zeros(1,length(Es_N0_dB));
for ii = 1:length(Es_N0_dB)

   % BPSK symbol generations
%    bits = rand(1,N)>0.5; % generating 0,1 with equal probability
%    s = 1-2*bits; % BPSK modulation following: {0 -> +1; 1 -> -1} 
   
    % QPSK symbol generations
   bits = rand(2,N)>0.5; % generating 0,1 with equal probability
   s = 1/sqrt(2)*((1-2*bits(1,:))+1j*(1-2*bits(2,:))); % QPSK modulation following the BPSK rule for each quadatrure component: {0 -> +1; 1 -> -1} 
   sigs2=var(s);
   
   % Channel convolution: equivalent symbol based representation
   z = conv(hc,s);  
   
   %Generating noise
   sig2b=10^(-Es_N0_dB(ii)/10);
   %n = sqrt(sig2b)*randn(1,N+Lc-1); % white gaussian noise, BPSK Case
    n = sqrt(sig2b/2)*randn(1,N+Lc-1)+1j*sqrt(sig2b/2)*randn(1,N+Lc-1); % white gaussian noise, QPSK case
   
   % Adding Noise
   y = z + n; % additive white gaussian noise

   %% zero forcing equalization
   % We now study ZF equalization
   
   %Unconstrained ZF equalization, only if stable inverse filtering
   
   
   %%
   % 
   %  The unconstrained ZF equalizer, when existing is given by 
   % 
   % $w_{,\infty,zf}=\frac{1}{h(z)}$
   % 
   %%
   % 
   
    s_zf=filter(1,hc,y);%if stable causal filter is existing
    bhat_zf = zeros(2,length(bits));
    bhat_zf(1,:)= real(s_zf(1:N)) < 0;
    bhat_zf(2,:)= imag(s_zf(1:N)) < 0;
    nErr_zfinfdirectimp(1,ii) = size(find([bits(:)- bhat_zf(:)]),1);
    %Otherwise, to handle the non causal case
    Nzf=200;
    [r, p, k]=residuez(1, hc);
    [w_zfinf]=ComputeRI( Nzf, r, p, k );
    s_zf=conv(w_zfinf,y);
    bhat_zf = zeros(2,length(bits));
    bhat_zf(1,:)= real(s_zf(Nzf:N+Nzf-1)) < 0;
    bhat_zf(2,:)= imag(s_zf(Nzf:N+Nzf-1)) < 0;
    
    nErr_zfinf(1,ii) = size(find([bits(:)- bhat_zf(:)]),1);
    
    % MMSE
    deltac=zeros(1,2*Lc-1);
    deltac(Lc)=1;
    Nmmse = 200;
    [r,p,k] = residuez(fliplr(conj(hc)),(conv(hc,fliplr(conj(hc)))+(sig2b/sigs2)*deltac));
    [w_mmseinf]=ComputeRI( Nmmse, r, p, k );
    s_mmse=conv(w_mmseinf,y);
    bhat_mmse = zeros(2,length(bits));
    bhat_mmse(1,:)= real(s_mmse(Nmmse:N+Nmmse-1)) < 0;
    bhat_mmse(2,:)= imag(s_mmse(Nmmse:N+Nmmse-1)) < 0;
    nErr_mmseinf(1,ii) = size(find([bits(:)- bhat_mmse(:)]),1);

    % ZF contraint
    Nw = 100;
    H = toeplitz([hc(1) zeros(1,Nw-1)]',[hc, zeros(1,Nw-1)]);
    Ry = (conj(H)*H.');
    p = zeros(Nw+Lc-1,1);
    P = (H.'*inv(Ry)*conj(H));
    [alpha, dopt] = max(diag(abs(P)));
    p(dopt) = 1;
    Gamma = conj(H)*p;
    w_zf_ls = (inv(Ry)*Gamma).';

    sig_e_opt = sigs2-conj(w_zf_ls)*Gamma;
    bias = 1 - sig_e_opt/sigs2;
    shat = conv(w_zf_ls,y);
    shat = shat(dopt:end);

    bhat_zfls = zeros(2,length(bits));
    bhat_zfls(1,:) = real(shat(1:N)) < 0;
    bhat_zfls(2,:) = imag(shat(1:N)) < 0;
    nErr_zfls(1,ii) = size(find([bits(:)- bhat_zfls(:)]),1);
    bias_zf(1,ii) = bias;

    % MMSE
    Nw = 100;
    H = toeplitz([hc(1) zeros(1,Nw-1)]',[hc, zeros(1,Nw-1)]);
    Ry = sigs2*(conj(H)*H.')+sig2b*eye(Nw);
    p = zeros(Nw+Lc-1,1);
    P = 1/sigs2*(H.'*inv(Ry/sigs2))*conj(H);
    [alpha, dopt] = max(diag(abs(P)));
    p(dopt) = 1;
    Gamma = conj(H)*p;
    w_mmse_ls = (inv(Ry)*Gamma).';

    sig_e_opt = sigs2-conj(w_mmse_ls)*Gamma;
    bias = 1 - sig_e_opt/sigs2;
    shat = conv(w_mmse_ls,y);
    shat = shat(dopt:end);

    bhat_mmsels = zeros(2,length(bits));
    bhat_mmsels(1,:) = real(shat(1:N)) < 0;
    bhat_mmsels(2,:) = imag(shat(1:N)) < 0;
    nErr_mmsels(1,ii) = size(find([bits(:)- bhat_mmsels(:)]),1);
    bias_mmse(1,ii) = bias;

end
simBer_zfinfdirectimp = nErr_zfinfdirectimp/N/log2(M); % simulated ber
simBer_zfinf = nErr_zfinf/N/log2(M); % simulated ber

simBer_mmseinf = nErr_mmseinf/N/log2(M); % simulated ber
simBer_zfls = nErr_zfls/N/log2(M);
simBer_mmsels = nErr_mmsels/N/log2(M);

% plot

figure
semilogy(Es_N0_dB,simBer_zfinfdirectimp(1,:),'bs-','Linewidth',2);
hold on
semilogy(Es_N0_dB,simBer_zfinf(1,:),'rs-','Linewidth',2);
semilogy(Es_N0_dB,simBer_mmseinf(1,:),'gs-','Linewidth',2);
semilogy(Es_N0_dB,simBer_zfls(1,:),'ys-','Linewidth',2);
semilogy(Es_N0_dB,simBer_mmsels(1,:),'cs-','Linewidth',2);
axis([0 50 10^-6 0.5])
grid on
legend('sim-zf-inf/direct','sim-zf-inf/direct','sim-mmse-inf/direct','sim-zf-ls FIR','sim-mmse-ls FIR');
xlabel('E_s/N_0, dB');
ylabel('Bit Error Rate');
title('Bit error probability curve for QPSK in ISI with MMSE and ZF equalizers')

figure;
plot(Es_N0_dB,bias_mmse,"rs-");
axis([0 50 0 1])
grid on;
xlabel('E_s/N_0, dB');
ylabel('Bias');
title("Bias for QPSK with MMSE/FIR equalizer");

figure;
title('Impulse response (MMSE)');
stem(real(w_mmseinf))
hold on
stem(real(w_mmseinf),'r-')
ylabel('Amplitude');
xlabel('time index')

[h,w] = freqz(w_zf_ls);
figure;
plot(w/pi,20*log10(abs(h)))
ax = gca;
ax.YLim = [-100 20];
ax.XTick = 0:.5:2;
xlabel('Normalized Frequency (\times\pi rad/sample)')
ylabel('Magnitude (dB)')
title("ZF/FIR")

[h,w] = freqz(w_mmse_ls);
figure;
plot(w/pi,20*log10(abs(h)))
ax = gca;
ax.YLim = [-100 20];
ax.XTick = 0:.5:2;
xlabel('Normalized Frequency (\times\pi rad/sample)')
ylabel('Magnitude (dB)')
title("MMSE/FIR")

figure;
title('Impulse response (ZF)');
stem(real(w_zfinf))
hold on
stem(real(w_zfinf),'r-')
ylabel('Amplitude');
xlabel('time index')

figure;
title("Symboles");
scatter(real(s),imag(s),"r+");
hold on;
scatter(real(z),imag(z),"b+");
legend("Symboles émis","Symboles reçus (sans bruit)");

figure;
subplot(3,1,1);
fx = linspace(0,1,N);
sx = (abs(fft(s)).^2)/N;
plot(fx,sx);
title("DSP signal émis")
subplot(3,1,2);
fz = linspace(0,1,length(z));
sz = (abs(fft(z)).^2)/length(z);
plot(fz,sz);
title("DSP signal reçu (sans bruit)")
subplot(3,1,3);
sy = (abs(fft(y)).^2)/length(y);
plot(fz,sy);
title("DSP signal reçu (avec bruit)");



