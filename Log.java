import java.io.IOException;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    public static final Logger logger = Logger.getLogger(Response.class.getName());

    public Log() {
        try {
            FileHandler file = new FileHandler("log.txt");
            LogFormat formatter = new LogFormat();
            file.setFormatter(formatter);
            logger.addHandler(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String msg) {
        logger.log(Level.INFO, msg);
    }

    public void log(Socket client, String msg) {

        logger.log(Level.INFO, client.getInetAddress() + ":" + client.getPort() + "\t" + msg);
    }

    public void log(User user, String msg) {
        logger.log(Level.INFO, user.client.getInetAddress() + ":" + user.client.getPort() + "\t" +
                user.name + "\t" +
                msg);
    }
}
