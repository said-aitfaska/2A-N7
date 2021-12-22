// Time-stamp: <03 Nov 2005 13:56 queinnec@enseeiht.fr>

/** Interface Individu. */
public interface Individu {
    /** Renvoie le nom de l'individu. */
    String nom();

    /** Renvoie l'age actuel de l'individu. */
    int age();

    /** Incrémente l'age actuel de l'individu et affiche à l'écran un message. */
    void feter_anniversaire ();
}
