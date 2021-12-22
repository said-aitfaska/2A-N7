
import javax.jms.*;
import javax.naming.*;

public class HelloWorld {

    public static ConnectionFactory connectionFactory;
    public static Connection        connection;
    public static Session           sessionP;
    public static Session           sessionS;
    public static MessageProducer   producer;
    public static MessageConsumer   consumer;
    public static Destination       destination;

    public static void main(String argv[]) {

        try {
            InitialContext ic = new InitialContext ();

            connectionFactory = (ConnectionFactory)ic.lookup("ConnFactory");
            destination = (Destination)ic.lookup("MonTopic");

            System.out.println("Bound to ConnFactory and MonTopic");

            connection = connectionFactory.createConnection();
            connection.start();

            System.out.println("Created connection");

            System.out.println("Creating sessions: not transacted, auto ack");
            sessionP = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            sessionS = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

            producer = sessionP.createProducer(destination);
            consumer = sessionS.createConsumer(destination);

            consumer.setMessageListener(new MsgListener());

            System.out.println("Ready");

            TextMessage textmsg = sessionP.createTextMessage();
            textmsg.setText("Hello World !!!");
            producer.send(textmsg);

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    static class MsgListener implements MessageListener {
        public void onMessage(Message msg)  {
            try {
                TextMessage textmsg = (TextMessage)msg;
                System.out.println("I have received : " + textmsg.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
