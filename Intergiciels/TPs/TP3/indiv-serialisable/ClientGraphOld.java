// Time-stamp: <21 mar 2014 11:51 queinnec@enseeiht.fr>
import java.rmi.Naming;
import java.rmi.RemoteException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Interface graphique pour manipuler un Carnet accessible à distance.
 * Peut fonctionner comme une application autonome :
 *   <code>java ClientGraph site_serveur</code>
 * ou comme une applet en utilisant <code>applet.html</code> (dans ce cas, le
 * service de noms doit être sur la même machine, ou utiliser le
   paramètre registry dans applet.html).
 */
public class ClientGraphOld extends java.applet.Applet {

    private static Carnet carnet;

    public static void main (String args[]) throws Exception { // pgm autonome
        String registryhost;
        if (args.length >= 1)
          registryhost = args[0];
        else
          registryhost = "localhost:1099";

        //  Connexion au serveur de noms (obtention d'un handle)
        carnet = (Carnet) Naming.lookup("rmi://"+registryhost+"/MonCarnet");

        JFrame f = new JFrame();
        
        f.setSize(750,200);
        f.setTitle("Menu");

        fillin(f);
    }

    public void init() {        // applet
        String registryhost = getParameter("registry");
        if (registryhost == null)
          registryhost = "localhost:1099";
        try {
            carnet = (Carnet) Naming.lookup("rmi://"+registryhost+"/MonCarnet");
        } catch (Exception e) {
            JTextArea msg = new JTextArea("Lookup of 'rmi://"+registryhost+"/MonCarnet' failed:"+e, 0, 50);
            msg.setLineWrap(true);
            msg.setWrapStyleWord(true);
            add(msg);
            return;
        }
        fillin (this);
    }

    private static void fillin (Container f)
    {
        f.setLayout(new GridLayout(0,6));
        
        /*****************************************************************/

        f.add(new JLabel("  Nom : "));
        final JTextField nom = new JTextField(10);
        f.add(nom);
        
        f.add(new JLabel("  Age : "));
        final JTextField age = new JTextField(10);
        f.add(age);
        
        JButton binserer = new JButton ("Insérer");
        f.add(binserer);
        binserer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String n; int a;
                    n = nom.getText();
                    try {
                        a = Integer.parseInt (age.getText());
                        carnet.inserer (new IndividuImpl (n,a));
                    } catch (NumberFormatException en) {
                        System.out.println ("bouh !");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }});

        f.add (new Label(""));

        /****************************************************************/
        
        f.add(new JLabel("  Nom/Num : "));
        final JTextField getind = new JTextField(10);
        f.add(getind);

        f.add(new JLabel("  Age : "));
        final JLabel getage = new JLabel("???");
        f.add(getage);

        JButton bget = new JButton ("Chercher/Get");
        f.add(bget);
        bget.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        Individu res;
                        String quoi = getind.getText();
                        try {
                            int num = Integer.parseInt (quoi);
                            res = carnet.get (num);
                        } catch (NumberFormatException en) {
                            res = carnet.chercher (quoi);
                        }
                        getind.setText (""+res.nom());
                        getage.setText (""+res.age());
                    } catch (IndividuInexistant ei) {
                        getage.setText ("!!!");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }});

        JButton bfeter = new JButton ("Fêter");
        f.add(bfeter);
        bfeter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        Individu res;
                        String quoi = getind.getText();
                        res = carnet.chercher (quoi);
                        res.feter_anniversaire();
                        getage.setText (""+res.age());
                    } catch (IndividuInexistant ei) {
                        getage.setText ("!!!");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }});

        /****************************************************************/

        f.add (new Label(""));

        JButton bpeupler = new JButton ("Peupler");
        f.add(bpeupler);
        bpeupler.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        carnet.inserer (new IndividuImpl ("aaa", 10));
                        carnet.inserer (new IndividuImpl ("bbb", 18));
                        carnet.inserer (new IndividuImpl ("ccc", 12));
                        carnet.inserer (new IndividuImpl ("ddd", 20));
                        carnet.inserer (new IndividuImpl ("eee", 30));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }});

        /********************************/
        f.add (new Label(""));
        f.add (new Label(""));
        /********************************/

        JButton bgetall = new JButton ("GetAll");
        f.add(bgetall);
        bgetall.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        Individu[] res = carnet.getAll();

                        final JFrame f2 = new JFrame();
                        f2.addWindowListener (new WindowAdapter() {
                                public void windowClosing(WindowEvent e) {
                                    f2.setVisible(false);
                                }});
                        f2.setSize(400,300);
                        f2.setTitle("GetAll");
                        f2.setLayout(new GridLayout(0,1));

                        for (Individu x : res) {
                            f2.add(new JLabel(x.nom() + "/" + x.age()));
                        }
                        f2.setVisible(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }});
        
        /*****************************************************************/

        if (f instanceof Window) {
            ((Window)f).addWindowListener (new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }});
        }
        f.setVisible(true);
    }

}
