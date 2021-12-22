import org.objectweb.joram.client.jms.admin.*;
import org.objectweb.joram.client.jms.*;
import org.objectweb.joram.client.jms.tcp.*;

public class CreateDestination {
  public static void main(String args[]) throws Exception {
    System.out.println();
    System.out.println("CreateDestination administration phase... ");

    // Connecting to JORAM server:
    AdminModule.connect("root", "root", 60);

    // Creating the JMS administered objects:        
    javax.jms.ConnectionFactory connFactory =
      TcpConnectionFactory.create("localhost", 16010);

    Destination destination = Topic.create(0);
    //Destination destination = Queue.create(0); // XXXX

    // Creating an access for user anonymous:
    User.create("anonymous", "anonymous", 0);

    // Setting free access to the destination:
    destination.setFreeReading();
    destination.setFreeWriting();

    // Binding objects in JNDI:
    javax.naming.Context jndiCtx = new javax.naming.InitialContext();
    jndiCtx.bind("ConnFactory", connFactory);
    jndiCtx.bind("MonTopic", destination);
    jndiCtx.close();
    
    AdminModule.disconnect();
    System.out.println("Admin closed.");
  }
}
