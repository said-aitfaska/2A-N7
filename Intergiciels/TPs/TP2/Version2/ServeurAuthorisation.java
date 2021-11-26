package TPSocket2018V2;


// imports pour la communication udp entre serveur http et serveur d'autorisation
import java.io.IOException;
import java.net.SocketException;




public class ServeurAuthorisation  {

	final static int port = 8532;
	static int cpt = 0;



	public static void main(String[] args){


		try {

			// TODO Création de la connexion côté serveur, en spécifiant un port d'écoute

			while(true){

				//TODO On s'occupe maintenant de l'objet paquet

				// TODO il faut vérifier si le client est autorisé ou pas une fois la
				// demande d'autoprisation est reçu
				// le packet doit contenir une demande d'autoprisation (auth_request) pour
				// un client



				// TODO reception


				//nous récupérons le contenu du packet recçu et nous l'affichons
				String str = new String(packet.getData());
				print("Reçu de la part de " + packet.getAddress()
						+ " sur le port " + packet.getPort() + " : ");

				println(str);


				//On réinitialise la taille du datagramme, pour les futures réceptions
				packet.setLength(buffer.length);




				//et nous allons répondre à notre client, donc même principe
				// retourner l'autrisation (ok) ou le rejet (ko)
				byte[] buffer2;

				// TODO structuration et emission de la réponse



				packet2.setLength(buffer2.length);
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