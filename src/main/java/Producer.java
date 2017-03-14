import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Evgenij on 14.03.2017.
 */
public class Producer implements Runnable   {
    public void run() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection myConnection = null;
        try {
            myConnection = factory.createConnection();
            myConnection.start();
            Session session = myConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("Dest");
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);//не будет кеширования
            TextMessage textMessage = session.createTextMessage("Hello world");
            producer.send(textMessage);
            session.close();
            myConnection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
