// Time-stamp: <10 avr 2012 14:43 queinnec@enseeiht.fr>
import java.rmi.RemoteException;

interface CallbackOnCreation extends java.rmi.Remote {

    /** Un individu a été créé. */
    public void eventCreated(Individu i) throws RemoteException;
}
