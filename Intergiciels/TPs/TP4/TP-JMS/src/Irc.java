import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.jms.*;
import javax.naming.*;

public class Irc {
    public static TextArea              text;
    public static TextField             data;
    public static Frame                 frame;

    public static Vector<String> users = new Vector<String>();
    public static String myName;

    public static ConnectionFactory connectionFactory;
    public static Connection        connection;
    public static Session           sessionP;
    public static Session           sessionS;
    public static MessageProducer   producer;
    public static MessageConsumer   consumer;
    public static Destination       destination;

    public static void main(String argv[]) {

        if (argv.length != 1) {
            System.out.println("java Irc <name>");
            return;
        }
        myName = argv[0];

        // creation of the GUI
        frame=new Frame();
        frame.setLayout(new FlowLayout());

        text=new TextArea(10,60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);

        data=new TextField(60);
        frame.add(data);

        Button write_button = new Button("write");
        write_button.addActionListener(new writeListener());
        frame.add(write_button);

        Button connect_button = new Button("connect");
        connect_button.addActionListener(new connectListener());
        frame.add(connect_button);

        Button who_button = new Button("who");
        who_button.addActionListener(new whoListener());
        frame.add(who_button);

        Button leave_button = new Button("leave");
        leave_button.addActionListener(new leaveListener());
        frame.add(leave_button);

        frame.setSize(550,300);
        text.setBackground(Color.black);
        frame.setVisible(true);
    }

    /* allow to print something in the window */
    public static void print(String msg) {
        try {
            text.append(msg+"\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class readListener implements MessageListener {
    public void onMessage(Message msg)  {
        try {
            StreamMessage smsg = (StreamMessage)msg;
            String cmd = smsg.readString();
            
            if (cmd.equals("say")) {
            	Irc.print(smsg.readString());
            } else if (cmd.equals("enter")) {
            	Irc.print("*** Entering: " + smsg.readString());
                StreamMessage m = Irc.sessionP.createStreamMessage();
                m.writeString("iamhere");
                m.writeString(Irc.myName);
                Irc.producer.send(m);
            } else if (cmd.equals("iamhere")) {
                String u = smsg.readString();
                if (!Irc.users.contains(u))
                  Irc.users.add(u);
            } else if (cmd.equals("leave")) {
            	Irc.print("**** Bye from: " + smsg.readString());
                String u = smsg.readString();
                Irc.users.remove(u);
            } else {
            	Irc.print("**** Not understood: " + cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
        

// action invoked when the "write" button is clicked
class writeListener implements ActionListener {
    public void actionPerformed (ActionEvent ae) {
        try {
            StreamMessage m = Irc.sessionP.createStreamMessage();
            m.writeString("say");
            m.writeString(Irc.myName+" says "+Irc.data.getText());
            Irc.producer.send(m);
            Irc.data.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// action invoked when the "connect" button is clicked
class connectListener implements ActionListener {
    public void actionPerformed (ActionEvent ae) {
        try {

            InitialContext ic = new InitialContext ();

            Irc.connectionFactory = (ConnectionFactory)ic.lookup("ConnFactory");
            Irc.destination = (Destination)ic.lookup("MonTopic");

            System.out.println("Bound to ConnFactory and MonTopic");

            Irc.connection = Irc.connectionFactory.createConnection();
            Irc.connection.start();

            System.out.println("Created connection");

            System.out.println("Creating sessions: not transacted, auto ack");
            Irc.sessionP = Irc.connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Irc.sessionS = Irc.connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

            Irc.producer = Irc.sessionP.createProducer(Irc.destination);
            Irc.consumer = Irc.sessionS.createConsumer(Irc.destination);

            Irc.consumer.setMessageListener(new readListener());

            System.out.println("Ready");

            StreamMessage m = Irc.sessionP.createStreamMessage();
            m.writeString("enter");
            m.writeString(Irc.myName);
            Irc.producer.send(m);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// action invoked when the "who" button is clicked
class whoListener implements ActionListener {
    public void actionPerformed (ActionEvent ae) {
        try {
            String res = "";
            for (String e : Irc.users)
              res += " "+e;
            Irc.print(res);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// action invoked when the "leave" button is clicked
class leaveListener implements ActionListener {
    public void actionPerformed (ActionEvent ae) {
        try {
            StreamMessage m = Irc.sessionP.createStreamMessage();
            m.writeString("leave");
            m.writeString(Irc.myName);
            Irc.producer.send(m);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
    

