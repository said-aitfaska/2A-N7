// Time-stamp: <10 avr 2012 09:37 queinnec@enseeiht.fr>
import java.rmi.RemoteException;

/** Interface d'un Carnet accessible à distance, contenant des Individu(s).
  * Pas de fioritures. */
public interface Carnet extends java.rmi.Remote {
    /** Insère l'Individu dans le carnet. */
    public void inserer (Individu x) throws RemoteException;

    /** Renvoie un Individu dont le nom correspond à <code>nom</code>,
     * ou lève l'exception <code>IndividuInexistant</code> s'il n'y en a pas. */
    public Individu chercher (String nom) throws RemoteException, IndividuInexistant;

    /** Renvoie le <code>n</code>-ième Individu du carnet,
     * ou lève <code>IndividuInexistant</code> s'il n'y en a pas. */
    public Individu get (int n) throws RemoteException, IndividuInexistant;

    /** Renvoie l'ensemble des Individu(s) contenus dans le carnet, sous
     * la forme d'un tableau. */
    public Individu[] getAll() throws RemoteException;

    /** Ajouter un callback pour être informé lors de la création d'un Individu. */
    public void addCallbackOnCreation(CallbackOnCreation cb) throws RemoteException;
    
    /** Enlever un callback. */
    public void removeCallbackOnCreation(CallbackOnCreation cb) throws RemoteException;
}
