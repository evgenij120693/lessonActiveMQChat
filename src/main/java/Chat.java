import org.apache.activemq.ActiveMQConnectionFactory;

public class Chat
        implements javax.jms.MessageListener
{
    private static final String APP_TOPIC = "jms.samples.chat";
    private static final String DEFAULT_BROKER_NAME = "tcp://10.240.17.94:61616";
    private static final String DEFAULT_PASSWORD = "password";

    private javax.jms.Connection connect = null;
    private javax.jms.Session pubSession = null;
    private javax.jms.Session subSession = null;
    private javax.jms.MessageProducer publisher = null;

    /** Create JMS client for publishing and subscribing to messages. */
    private void chatter( String broker, String username, String password)
    {
        // Create a connection.
        try
        {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connect = factory.createConnection ();
            pubSession = connect.createSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
            subSession = connect.createSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
        }
        catch (javax.jms.JMSException jmse)
        {
            System.err.println("error: Cannot connect to Broker - " + broker);
            jmse.printStackTrace();
            System.exit(1);
        }

        // Create Publisher and Subscriber to 'chat' topics
        try
        {
            javax.jms.Topic topic = pubSession.createTopic (APP_TOPIC);
            javax.jms.MessageConsumer subscriber = subSession.createConsumer(topic);
            subscriber.setMessageListener(this);
            publisher = pubSession.createProducer(topic);
            // Now that setup is complete, start the Connection
            connect.start();
        }
        catch (javax.jms.JMSException jmse)
        {
            jmse.printStackTrace();
        }

        try
        {
            // Read all standard input and send it as a message.
            java.io.BufferedReader stdin =
                    new java.io.BufferedReader( new java.io.InputStreamReader( System.in ) );
            System.out.println ("\nChat application:\n"
                    + "=================\n"
                    + "The application user " + username + " connects to the broker at " + DEFAULT_BROKER_NAME + ".\n"
                    + "The application will publish messages to the " + APP_TOPIC + " topic.\n"
                    + "The application also subscribes to that topic to consume any messages published there.\n\n"
                    + "Type some text, and then press Enter to publish it as a TextMesssage from " + username + ".\n");
            while ( true )
            {
                String s = stdin.readLine();

                if ( s == null )
                    exit();
                else if ( s.length() > 0 )
                {
                    javax.jms.TextMessage msg = pubSession.createTextMessage();
                    msg.setText( username + ": " + s );
                    publisher.send( msg );
                }
            }
        }
        catch ( java.io.IOException ioe )
        {
            ioe.printStackTrace();
        }
        catch ( javax.jms.JMSException jmse )
        {
            jmse.printStackTrace();
        }
    }

    /**
     * Handle the message
     * (as specified in the javax.jms.MessageListener interface).
     */
    public void onMessage( javax.jms.Message aMessage)
    {
        try
        {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (javax.jms.TextMessage) aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try
            {
                String string = textMessage.getText();
                System.out.println( string );
            }
            catch (javax.jms.JMSException jmse)
            {
                jmse.printStackTrace();
            }
        }
        catch (java.lang.RuntimeException rte)
        {
            rte.printStackTrace();
        }
    }

    /** Cleanup resources and then exit. */
    private void exit()
    {
        try
        {
            connect.close();
        }
        catch (javax.jms.JMSException jmse)
        {
            jmse.printStackTrace();
        }

        System.exit(0);
    }

    //
    // NOTE: the remainder of this sample deals with reading arguments
    // and does not utilize any JMS classes or code.
    //

    /** Main program entry point. */
    public static void main(String argv[]) {



        // Start the JMS client for the "chat".
        Chat chat = new Chat();
        chat.chatter ("chat", "admin", "admin");

    }



}