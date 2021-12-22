// Time-stamp: <26 Oct 2005 17:21 queinnec@enseeiht.fr>
import java.rmi.RemoteException;

/** Implantation accessible à distance de l'interface Individu. */
public class IndividuImpl extends java.rmi.server.UnicastRemoteObject implements Individu {
    private String nom;
    private int age;

    public IndividuImpl(String nom, int age) throws RemoteException {
        this.nom = nom;
        this.age = age;
    }
    
    public String nom()
    {
        System.out.println ("IndividuImpl.nom");
        return nom;
    }
    
    public int age()
    {
        System.out.println ("IndividuImpl.age");
        return age;
    }
    
    public void feter_anniversaire ()
    {
        System.out.println ("IndividuImpl.feter_anniversaire");
        System.out.println ("Bon anniversaire "+nom);
        age++;
    }
}
