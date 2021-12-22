// Time-stamp: <26 Oct 2005 17:22 queinnec@enseeiht.fr>

import java.rmi.RemoteException;

/** Interface Individu (accessible à distance). */
public interface Individu extends java.rmi.Remote {
    String nom() throws RemoteException;
    int age() throws RemoteException;
    void feter_anniversaire () throws RemoteException;
}
