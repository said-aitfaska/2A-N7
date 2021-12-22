// Time-stamp: <07 mar 2013 15:00 queinnec@enseeiht.fr>
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.rmi.server.UnicastRemoteObject;

/* HIDE STUDENT */
class CallbackCreation extends UnicastRemoteObject implements CallbackOnCreation {
    public CallbackCreation() throws RemoteException { }
    public void eventCreated(Individu i) {
        try {
            System.out.println("Création: "+i.nom());
        } catch (Exception e) {
            System.err.println("eventCreated: exception: "+e);
        }
    }
}
/* END STUDENT */

/** Interface textuelle pour manipuler un Carnet accessible à distance.
 */
public class ClientTxt {

    interface Action {
        void executer(StringTokenizer st) throws Exception;
    }

    private static Carnet carnet;
    
    public static void main (String args[]) throws Exception {
        String registryhost;
        if (args.length >= 1)
          registryhost = args[0];
        else
          registryhost = "localhost:1099";

        //  Connexion au serveur de noms (obtention d'un handle)
        carnet = (Carnet) Naming.lookup("rmi://"+registryhost+"/MonCarnet");

        // Actions de l'interface utilisateur
        Map<String,Action> actions = new HashMap<String,Action>();

        /* Action de la commande 'peupler' */
        Action peupler = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    carnet.inserer (new IndividuImpl ("aaa", 10));
                    carnet.inserer (new IndividuImpl ("bbb", 18));
                    carnet.inserer (new IndividuImpl ("ccc", 12));
                    carnet.inserer (new IndividuImpl ("ddd", 20));
                    carnet.inserer (new IndividuImpl ("eee", 30));
                }};
        actions.put("p",peupler);
        actions.put("peupler",peupler);
        
        /* Action de la commande 'insérer nom age' */
        Action inserer = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    if (st.countTokens() < 2) {
                        System.out.println ("inserer nom age");
                        return;
                    }
                    String nom = st.nextToken();
                    int age = Integer.parseInt (st.nextToken());
                    /* BEGIN STUDENT */
                    carnet.inserer (new IndividuImpl(nom,age));
                    /* END STUDENT */
                }};
        actions.put("i",inserer);
        actions.put("insérer",inserer);
        actions.put("inserer",inserer);
        
        /* Action de la commande 'chercher nom' */
        Action chercher = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    if (st.countTokens() < 1) {
                        System.out.println ("chercher nom");
                        return;
                    }
                    String nom = st.nextToken();
                    /* BEGIN STUDENT */
                    Individu i = carnet.chercher (nom);
                    System.out.println ("Trouvé "+i.nom() + " age="+i.age());
                    /* END STUDENT */
                }};
        actions.put("c",chercher);
        actions.put("chercher",chercher);
        
        /* Action de la commande 'get num' */
        Action get = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    if (st.countTokens() < 1) {
                        System.out.println ("get numéro");
                        return;
                    }
                    int ind = Integer.parseInt (st.nextToken());
                    /* BEGIN STUDENT */
                    Individu i = carnet.get (ind);
                    System.out.println ("Trouvé n°"+ind+" : "+i.nom() + " age="+i.age());
                    /* END STUDENT */
                }};
        actions.put("g",get);
        actions.put("get",get);
        
        /* Action de la commande 'getall' */
        Action getall = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    /* BEGIN STUDENT */
                    Individu[] all = carnet.getAll();
                    for (int i = 0; i < all.length; i++) {
                        System.out.println ("N°"+i+" : "+all[i].nom()+" age="+all[i].age());
                    }
                    /* END STUDENT */
                }};
        actions.put("a",getall);
        actions.put("getall",getall);

        /* Action de la commande 'feter nom' */
        Action feter = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    if (st.countTokens() < 1) {
                        System.out.println ("Feter nom");
                        return;
                    }
                    String nom = st.nextToken();
                    /* BEGIN STUDENT */
                    Individu i = carnet.chercher (nom);
                    System.out.println ("Trouvé "+i.nom() + " age="+i.age());
                    i.feter_anniversaire();
                    /* END STUDENT */
                }};
        actions.put("f",feter);
        actions.put("fêter",feter);
        actions.put("feter",feter);

        /* Action de la commande 's'abonner' */
        Action callback = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    /* BEGIN STUDENT */
                    CallbackOnCreation cb = new CallbackCreation();
                    carnet.addCallbackOnCreation(cb);
                    /* END STUDENT */
                }};
        actions.put("cb", callback);
        actions.put("callback", callback);

        /* Action de la commande 'quitter' */
        Action quitter = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    System.out.println ("bye");
                    System.exit(0);
                }};
        actions.put("q",quitter);
        actions.put("quitter",quitter);

        /* Action de la commande 'help' */
        final Map<String,Action> actions2 = actions;
        Action help = new Action() {
                public void executer(StringTokenizer st) throws Exception {
                    System.out.println(actions2.keySet());
                }};
        actions.put("h",help);
        actions.put("help",help);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println ("[P]eupler [I]insérer [C]hercher [G]et get[A]ll [F]êter [CB]callback [Q]uitter");
            String ligne = stdin.readLine();
            StringTokenizer st = new StringTokenizer(ligne);
            if (st.countTokens() == 0) continue;
            Action a = actions.get(st.nextToken().toLowerCase());
            if (a == null) {
                System.out.println ("Try again");
            } else {
                try {
                    a.executer(st);
                } catch (Exception e) {
                    System.out.println (e);
                }
            }
        }
    }

}
