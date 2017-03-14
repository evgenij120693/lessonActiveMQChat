/**
 * Created by Evgenij on 14.03.2017.
 */

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Chat2 {
    private Connection conn = null;
    private Session sessionPub = null;
    private Session sessionSub = null;
    private Destination destination = null;
    private Topic topic = null;
    private MessageProducer producer = null;
    private MessageConsumer consumer = null;

    public void chatter(String broker, String username) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(broker);
        try {
            conn = factory.createConnection();
            conn.start();
            sessionPub = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            sessionSub = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);


            destination = sessionPub.createTopic("Dest");
            consumer = sessionSub.createConsumer(destination);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        String mess = ((TextMessage) message).getText();
                        System.out.println(mess);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            producer = sessionPub.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            TextMessage joinedToChatMessage = sessionPub.createTextMessage(username + " joined chat");
            producer.send(joinedToChatMessage);
            String message;
            while (true) {
                message = br.readLine();

                if (message == null) {
                    exit();
                }
                TextMessage textMessage = sessionPub.createTextMessage(username + " : " + message);
                producer.send(textMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }

    private void exit() {
        try {
            sessionPub.close();
            conn.close();
        } catch (javax.jms.JMSException jmse) {
            jmse.printStackTrace();
        }

        System.exit(0);
    }
}



