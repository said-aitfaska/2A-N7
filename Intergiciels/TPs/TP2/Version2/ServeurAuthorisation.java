package TPSocket2018V2;


// imports pour la communication udp entre serveur http et serveur d'autorisation
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.util.Random;
import java.net.*;
public class ServeurAuthorisation  {

	final static int port = 8532;
	static int cpt = 0;
	 static Random rand = new Random();
	 static int accept = 1|2;

	public static void main(String[] args){


		try {

			// TODO Création de la connexion côté serveur, en spécifiant un port d'écoute
			
			DatagramSocket sock = new DatagramSocket(8082);
			while(true){
		  		// taille de notre buffer qui contiendra les donnes //
		  		byte[] buffer_donnes = new byte[cpt];
		  		DatagramPacket packet = new DatagramPacket(buffer_donnes,cpt);

				// TODO il faut vérifier si le client est autorisé ou pas une fois la
				// demande d'autoprisation est reçu
				// le packet doit contenir une demande d'autoprisation (auth_request) pour
				// un client
		  		sock.receive(packet);
				
				//nous récupérons le contenu du packet recçu et nous l'affichons
				String str = new String(packet.getData());
				print("Reçu de la part de " + packet.getAddress()
						+ " sur le port " + packet.getPort() + " : ");

				//println(str);
				// Autorisation de la part du serveur // 
				int k = rand.nextInt(accept);
				String AUTH ;
				if (k == 2){     // on accepte la reception si k = 2  SIMPLE CASE 
					AUTH = "OK";
				}else {
					AUTH = "KO";
				}
				
				//On réinitialise la taille du datagramme, pour les futures réceptions
				packet.setLength(buffer_donnes.length);

				//et nous allons répondre à notre client, donc même principe
				// retourner l'autrisation (ok) ou le rejet (ko)
				byte[] buffer2;

				// TODO structuration et emission de la réponse
				buffer2 = AUTH.getBytes();
				DatagramPacket packet2 = new DatagramPacket(buffer2,buffer2.length);
				packet2.setLength(buffer2.length);
				sock.send(packet2);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param str
	 */
	public static void print(String str){
		System.out.print(str);
	}
	/**
	 * @param str
	 */
	public static void println(String str){
		System.err.println(str);
	}



}