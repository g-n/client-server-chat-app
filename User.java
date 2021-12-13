import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    String name;
    Socket client;
    PrintWriter out;
    public static final Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Creates a user with a specific username, client, and printwriter
     * @param name the username
     * @param client the client socket connection
     * @param out the output buffer to the server
     * @throws IOException
     */
    public User(String name, Socket client, PrintWriter out) throws IOException {
        this.name = name;
        this.client = client;
        this.out = out;
    }

    /**
     * writes a message to the output buffer
     * @param message
     * @throws IOException
     */
    public void send(String message) throws  IOException {
        out.println(message);
    }
    void log(String message){
        logger.log(Level.INFO, name + "@ " + client.getInetAddress() + ":" + client.getPort() +" " + message);
    }

    /**
     * sends a MSG command to the server
     * @param message
     */
    public void msg(String message) {
        try {
            send("MSG " + name + " " + message);
        } catch (IOException e){
            log("msg error");
        }
    }

    /**
     * sends an EXIT command to the server
     */
    public void exit() {
        try {
            send("EXIT " + name);
        } catch (IOException e){
            log("exit error");
        }
    }

    /**
     * sends the login status to the server
     * @param ls
     * @throws IOException
     */
    public void send(LoginStatus ls) throws  IOException {
        out.println(ls.getMessage());
    }

    /**
     * sends a generic message to the server without a command
     * @param message
     * @throws IOException
     */
    public void send(int message) throws  IOException {
        this.send(String.valueOf(message));
    }
    @Override
    public boolean equals(Object other){
        return this.name.equals(((User) other).name);
    }

}
