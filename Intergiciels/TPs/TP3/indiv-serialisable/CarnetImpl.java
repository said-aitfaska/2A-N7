// Time-stamp: <10 avr 2012 14:43 queinnec@enseeiht.fr>
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

/** Implémentation basique d'un Carnet accessible à distance.
 *  Utilise une List(e) pour ranger les Individu(s).
 */
public class CarnetImpl extends UnicastRemoteObject implements Carnet {
    
    private List<Individu> contenu = new ArrayList<Individu>();

    /* HIDE STUDENT */
    private Set<CallbackOnCreation> lesCallbacks = new HashSet<CallbackOnCreation>();
    /* END STUDENT */

    public CarnetImpl() throws RemoteException {}

    /* BEGIN STUDENT */
    public void inserer (Individu x) throws RemoteException
    {
        String nom = x.nom();
        System.out.println ("Carnet: inserer "+nom);
        contenu.add (x);

        Iterator<CallbackOnCreation> it = lesCallbacks.iterator();
        while (it.hasNext()) {
            CallbackOnCreation cb = it.next();
            try {
                cb.eventCreated(x);
            } catch (RemoteException e) {
                it.remove();
            }
        }
    }
    
    public Individu chercher (String nom) throws RemoteException, IndividuInexistant
    {
        for (Individu x : contenu) {
            if (x.nom().equals (nom)) {
                System.out.println ("Carnet: chercher "+nom+ ": trouve");
                return x;
            }
        }
        System.out.println ("Carnet: chercher "+nom+ ": pas trouve");
        throw new IndividuInexistant("Nom pas present : "+nom);
    }
    
    public Individu get (int n) throws IndividuInexistant
    {
        if ((n < 0) || (n >= contenu.size()))
          throw new IndividuInexistant("Index errone : "+n);
        return (contenu.get(n));
    }
    
    public Individu[] getAll()
    {
        return contenu.toArray(new Individu[0]);
    }

    /** Ajouter un callback pour être informé lors de la création d'un Individu. */
    public void addCallbackOnCreation(CallbackOnCreation cb)
    {
        lesCallbacks.add(cb);
    }
    
    /** Enlever un callback. */
    public void removeCallbackOnCreation(CallbackOnCreation cb)
    {
        lesCallbacks.remove(cb);
    }

    /* END STUDENT */
}
