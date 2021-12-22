// Time-stamp: <03 mai 2012 09:04 queinnec@enseeiht.fr>

/** Implantation sérialisable de l'interface Individu. */
public class IndividuImpl implements Individu, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private int age;

    public IndividuImpl(String nom, int age) {
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
