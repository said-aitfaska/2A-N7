clear;
close all;

M = 4;
Ns = 50000;
N = log2(M)*Ns;
Es_N0_dB = (0:50);
BER_ml = zeros(1,length(Es_N0_dB));
Nml = 20;

const = qammod((0:M-1)',M);
tblen = 16;
nsamp = 1;
preabme = [];
postamble = [];

hc = [0.623; 0.489+0.234i; 0.398i; 0.21];
Lc = length(hc);

for i=1:length(Es_N0_dB)
    bits = randi([0 1],N,1);
    s = qammod(bits,M,'InputType','bit','UnitAveragePower',true);
    
    z = filter(hc,1,s);
    sig2b=10^(-Es_N0_dB(i)/10);
    n = sqrt(sig2b/2)*randn(1,N/2)+1j*sqrt(sig2b/2)*randn(1,N/2);
    y = z + n';
    
    s_ml = mlseeq(y,hc,const,tblen,'rst',nsamp,[],[]);
    xh = qamdemod(s_ml(:),M,'outputType','bit');
    BER_ml(i) = size(find([bits(:)- xh(:)]),1);
end

BER_ml = BER_ml/N;
figure
semilogy(Es_N0_dB,BER_ml,'bs-','Linewidth',2);
axis([0 50 10^-6 0.5])
grid on
legend('sim-ml');
xlabel('E_s/N_0, dB');
ylabel('Bit Error Rate');
title('Bit error probability curve for QPSK in ISI with MLSE equalizer')