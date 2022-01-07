%Nombre de porteuse
N = 16;
%nb_nbits
nb_bits = 16000;
% Ts 
Ts = 1; 
%generation des bits aleatorement  
bits=randi([0,1],1,nb_bits);
% Mapping en symboles complexe
symboles =  2*bits-1;
% Surechantillonage
mat_symboles =reshape(symboles,N,length(symboles)/N);
%matrice sortie de la ifft
signal_entree = ifft(mat_symboles);
%matrice  sortie de la fft
signal_sortie = fft(signal_entree);
%remise en ligne
sig_sortie = reshape(signal_sortie,1,length(symboles));
%decision symboles
symboles_decides = sign(sig_sortie);
%Demapping_2
demmaping = (symboles_decides +1 )/2;
%Calcul de TEB 
TEB = length(find(bits ~= demmaping))/nb_bits;
% DSP signal   
    DSP = periodogram(sig_sortie);
    figure();
    plot(fftshift(DSP));
    xlabel('Fréquences en Hz');
    ylabel('S_{x}(f)');
    title('Figure : DSP signal toutes les porteuses');
    
%% Avec une seule porteuse et les autres a zero

mat_symboles1 =  [mat_symboles(1,:); zeros(N-1,nb_bits/N)];
signal_entree1 = ifft(mat_symboles1);
%sortie de ifft
signal_sortie1 = fft(signal_entree1);
%remise en ligne
sig_sortie1 = reshape(signal_sortie1,1,length(symboles));
%decision symboles
symboles_decides1 = sign(sig_sortie1);
%Demapping_2
demmaping1 = (symboles_decides1 +1 )/2;
%Calcul de TEB 
TEB1 = length(find(bits ~= demmaping1))/nb_bits;
% DSP signal avec une seule porteuse   
    DSP1 = periodogram(sig_sortie1);
    figure();
    plot(fftshift(DSP1));
    xlabel('Fréquences en Hz');
    ylabel('S_{x}(f)');
    title('Figure : DSP signal avec une porteuse ');

%% Avec deux porteuses utilisées et les autres a zero

mat_symboles2 =  [mat_symboles(1:2,:); zeros(N-2,nb_bits/N)];
signal_entree2 = ifft(mat_symboles2);
%sortie de ifft
signal_sortie2 = fft(signal_entree2);
%remise en ligne
sig_sortie2 = reshape(signal_sortie2,1,length(symboles));
%decision symboles
symboles_decides2 = sign(sig_sortie2);
%Demapping_2
demmaping2 = (symboles_decides2 + 1 )/2;
%Calcul de TEB 
TEB2 = length(find(bits ~= demmaping2))/nb_bits;
% DSP signal avec deux  porteuses   
    DSP2 = periodogram(sig_sortie2);
    figure();
    plot(fftshift(DSP2));
    xlabel('Fréquences en Hz');
    ylabel('S_{x}(f)');
    title('Figure : DSP signal avec deux porteuse ');
    
%% Avec huit porteuses utilisées et les autres a zero 

mat_symboles3 = [zeros(4,nb_bits/N);mat_symboles(5:12,:); zeros(4,nb_bits/N)];
signal_entree3 = ifft(mat_symboles3);
%sortie de ifft
signal_sortie3 = fft(signal_entree3);
%remise en ligne
sig_sortie3 = reshape(signal_sortie3,1,length(symboles));
%decision symboles
symboles_decides3 = sign(sig_sortie3);
%Demapping_2
demmaping3 = (symboles_decides3 + 1 )/2;
TEB3 = length(find(bits ~= demmaping3))/nb_bits;
% DSP signal avec deux  porteuses   
    DSP3 = periodogram(sig_sortie3);
    figure();
    plot(fftshift(DSP3));
    xlabel('Fréquences en Hz');
    ylabel('S_{x}(f)');
    title('Figure : DSP signal avec huit porteuse ');
    
    
%% Chaine avec canal multitrajet 

% on trouve un PC de 12 ; on prend IG = 4 et avec formule de 25%
% reponse en frequance de canal de propagation
h = [ 0.227 0.46 0.688 0.460 0.227];
H_entree = fft(h,100);
%frequence = []
%f = linspace(0,N,length());
module_H = abs(H_entree);
argument_H = angle(H_entree);
figure();
subplot(2, 1 , 1)
plot(linspace(0, N, length(H_entree)), module_H,'red','LineWidth',2);
title('Reponse en frequence (module)');
xlabel('Porteuses (N)');
ylabel('H_{canal}(N)');
subplot(2, 1 , 2)
plot(linspace(0, N, length(argument_H)), argument_H,'red','LineWidth',2);
%plot(periodogram(argument_H));
title('Reponse en frequence (argument)');
xlabel('Porteuses (N)');
ylabel('arg(H_{canal}(f))');

%% passage du canal OFFDM par canal de propagation

%remise en ligne
sig_recu = reshape(signal_entree,1,length(symboles));
SignalRecu = filter(h,1,sig_recu);
% DSP signal sortie de canal
 DSP4 = periodogram(SignalRecu);
    figure();
    plot(fftshift(DSP4));
    xlabel('Fréquences en Hz');
    ylabel('S_{x}(f)');
    title('Figure 4: DSP signal sortie du canal  ')
% Comparaison DSP sortie canal & avant canal 
figure();
plot(fftshift(DSP4),'blue');
hold on;
plot(fftshift(DSP),'r');
xlabel('Fréquences en Hz');
ylabel('S_{x}(f)');
title('Figure 4: DSP sortie canal & avant canal  ');
legend('DSP apres canal','DSP avant canal');

% Visualtization des constellations

% 2 constellations loignees ( premiere et derniere) :
Signalrecufft = fft(reshape(SignalRecu,N,length(symboles)/N));
figure();
plot(Signalrecufft(3,:),'ored','LineWidth',2);
hold on;
plot(Signalrecufft(9,:),'oblue','LineWidth',2);
title('figure1 : Constellations avec 2 porteuses'); 
legend('Porteuse 3','Porteuse 9');

%Determination de TEB simulé

%remise en ligne
signalSortie = reshape(Signalrecufft,1,length(symboles));
%decision symboles
symboles_decides4 = sign(signalSortie);
%Demapping_4
demmaping4 = (symboles_decides4 + 1 )/2;
TEB4 = length(find(bits ~= demmaping4))/nb_bits;
% RQ : sans interval de garde on a plus de bruit i.e
      % interferences entre symboles car les porteuse 
       % se supersosent donc on perde de l'info donc TEB grand

%% %%%%%%%%%%%%%%%  Implantation avec Intervale de Garde %%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Introduction des zeros entre les symboles :
N1 = 12;
%nb_nbits
nb_bits_IG = 12000;
%generation des bits aleatorement  
bits_IG=randi([0,1],1,nb_bits_IG);
%Mapping en symboles complexe
symboles_IG =  2*bits_IG-1;
% Surechantillonage
mat_IG =reshape(symboles_IG,N1,length(symboles_IG)/N1);
%passage par la ifft
signal_entree_IG = ifft(mat_IG);
%passage par la fft
signal_sortie_IG = fft(signal_entree_IG);
%remise en ligne de la matrice
sig_sortie_IG = reshape(signal_sortie_IG,1,length(symboles_IG));
%decision symboles
symboles_decides_IG = sign(sig_sortie_IG);

%Construction de la matrice avec des zeros au debut
mat_avec_IG = [zeros(4,length(signal_entree_IG));signal_entree_IG];
signal_sortieCanal_IG = filter(h,1,reshape(mat_avec_IG, 1, []));
signal_sortieCanalIG_ligne = reshape(signal_sortieCanal_IG,N1 +4,[]);
Signal_sortie_IG = fft(signal_sortieCanalIG_ligne(5:end,:));

% Constellations avec Intervale de Garde : 
figure();
plot(Signal_sortie_IG(3,:),'ored','LineWidth',2);
hold on;
plot(Signal_sortie_IG(9,:),'oblue','LineWidth',2);
title('figure1 : Constellations 2 porteuses avec IG'); 
legend('Porteuse 3','Porteuse 9');

%Calcul du TEB

%remise en ligne de la matrice
signalSortie_IG = reshape(Signal_sortie_IG,1,length(symboles_IG));
%decision symboles
symboles_decides5 = sign(signalSortie_IG);
%Demapping
demmaping5 = (symboles_decides5 + 1 )/2;
TEB5 = length(find(bits_IG ~= demmaping5))/nb_bits_IG;

%% %%%%%%%%%%%%%%%%%%%%  Implantation avec prefix cyclique %%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Construction de la matrice avec prefix cyclique
mat_avec_PC = [signal_entree_IG(end-3:end,:);signal_entree_IG];
%mat_avec_PC(1:4,:)= signal_entree_IG(N1-3:end,:);
signal_sortieCanal_PC = filter(h,1,reshape(mat_avec_PC, 1, []));
signal_sortieCanalPC_ligne = reshape(signal_sortieCanal_PC,N1 +4,[]);
Signal_sortie_PC = fft(signal_sortieCanalPC_ligne(5:end,:));

%Tracage des constellations avec PC 
figure();
plot(Signal_sortie_PC(3,:),'*red','LineWidth',2);
hold on;
plot(Signal_sortie_PC(9,:),'*blue','LineWidth',2);
title('figure1 : 2 Constellations  porteuses avec PC');
legend('Porteuse 3','Porteuse 9');

%Calcul du TEB simulé
%remise en ligne
signalSortie_PC = reshape(Signal_sortie_PC,1,length(symboles_IG));
%decision symboles
symboles_decides6 = sign(signalSortie_PC);
%Demapping
demmaping6 = (symboles_decides6 + 1 )/2;
TEB6 = length(find(bits_IG ~= demmaping6))/nb_bits_IG;

%% %%%%%%%%%%%%%%%%%%%%  Implantation avec Egalisation %%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

H = fft(h,N1);
signal_EG =  Signal_sortie_PC./(H.');   
%%%%%%  ML EGALIZER , COMMENT THE LINE BELLOW PLEASE  %%%%%%%%%%%%%%%
%signal_EG =  Signal_sortie_PC.*(H');   

%Tracage des constellations 
figure();
plot(signal_EG(3,:),'ored','LineWidth',2);
hold on;
plot(signal_EG(9,:),'oblue','LineWidth',2);
title('figure1 : Constellations avec P.C & Egalisateur ML ');
legend('Porteuse 3','Porteuse 9');

%Calcul du TEB simulé avec egalisateur et P.C
%remise en ligne
signal_EG_ligne = reshape(signal_EG,1,length(symboles_IG));
%decision symboles
symboles_decides_EG = sign(signal_EG_ligne);
%Demapping
demmaping_EG = (symboles_decides_EG + 1 )/2;
TEB_EG = length(find(bits_IG ~= demmaping_EG))/nb_bits_IG;

%% %%%%%%%%%%%%%%%%%%%  Impacte erreur d'horloge %%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Construction de la matrice avec prefix cyclique surdimensionement (double donc 8)
mat_sortie_HR = [signal_entree_IG(end-7:end,:);signal_entree_IG];
signalSortieCanal_PC = filter(h,1,reshape(mat_sortie_HR, 1, []));
signalSortieCanalPC_ligne = reshape(signalSortieCanal_PC,N1 +8,[]);

%matrices des differents cas ( selon enonce)
SignalSortie_cas1 = fft(signalSortieCanalPC_ligne(3:14,:));
SignalSortie_cas2 = fft(signalSortieCanalPC_ligne(7:18,:));
SignalSortie_cas3 = fft([signalSortieCanalPC_ligne(11:end,:);signalSortieCanalPC_ligne(1:2,:)]);

% Egalisation 
Signal_EG_cas1 = SignalSortie_cas1./(H.');
Signal_EG_cas2 = SignalSortie_cas2./(H.');
Signal_EG_cas3 = SignalSortie_cas3 ./(H.');

%Tracage des constellations avec erreur d'horloge : cas1
figure();
plot(Signal_EG_cas1(3,:),'ored','LineWidth',2);
%hold on;
title('figure1 : 1ere cas problem synchronisation ');
legend('porteuse 3');

%Tracage des constellations avec erreur d'horloge : cas 2 
figure();
plot(Signal_EG_cas2(3,:),'ored','LineWidth',2);
%hold on;
title('figure1 : 2eme cas problem synchronisation ');
legend('porteuse 3');

%Tracage des constellations avec erreur d'horloge : cas 3
figure();
plot(Signal_EG_cas3(3,:),'ored','LineWidth',2);
%hold on;
title('figure1 : 3eme cas problem synchronisation ');
legend('porteuse 3');

%RQ : LES CAS DE synchroinisation ajout un angle supplementaire de rotation
% pour cela on a une rotation dans les cas 2 et 3
% si egalisation : on a une compensation de rotation 