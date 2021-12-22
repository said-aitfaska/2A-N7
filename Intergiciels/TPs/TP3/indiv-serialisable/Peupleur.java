// Time-stamp: <02 mai 2012 10:33 queinnec@enseeiht.fr>
import java.rmi.Naming;

/** Programme peuplant le carnet avec des Individus.
 *  Si les Individus sont des objets accessibles à distance (Remote), le
 *  programme ne doit pas se terminer. Sinon il peut (les objets ont
 *  été sérialisés).
 */
public class Peupleur {

    private static Carnet carnet;
    
    public static void main (String args[]) throws Exception {
        String registryhost;
        if (args.length >= 1)
          registryhost = args[0];
        else
          registryhost = "localhost:1099";

        //  Connexion au serveur de noms (obtention d'un handle)
        carnet = (Carnet) Naming.lookup("rmi://"+registryhost+"/MonCarnet");

        carnet.inserer (new IndividuImpl ("aaa", 10));
        carnet.inserer (new IndividuImpl ("bbb", 18));
        carnet.inserer (new IndividuImpl ("ccc", 12));
        carnet.inserer (new IndividuImpl ("ddd", 20));
        carnet.inserer (new IndividuImpl ("eee", 30));

        System.out.println ("Peupleur a peuplé.");
    }
}
