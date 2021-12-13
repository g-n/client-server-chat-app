import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client {
    static int BUFFER_SIZE = 4096;

    /**
     * runs a chat client on port 63546
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ChatScreen cs;
        String username;
        String line;
        try (Socket sock = new Socket("127.0.0.1", 63546);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true)) {
            username = JOptionPane.showInputDialog("Enter username");
            out.println("LOGIN " + username.trim());

            while (true) {
                line = in.readLine();
                if (line == null) {
                    System.out.println("null err");
                    return;
                }
                try {

                    if (Parser.checkStatus(line)) {
                        break;
                    } else {
                        JOptionPane.showMessageDialog(frame, username + " already logged in");
                        username = JOptionPane.showInputDialog("Enter username");
                        out.println("LOGIN " + username.trim());
                    }

                } catch (AuthError e) {
                    System.out.println("ignore: " + line);
                }
            }
            User user = new User(username, sock, out);

            cs = new ChatScreen(user);
            while (true) {
                line = in.readLine();
                System.out.println(line);
                if (line == null) {
                    break;
                }
                if (line.length() == 0) {
                    break;
                }
                StringTokenizer st = new StringTokenizer(line);
                Command c = Parser.parseCommand(st);
                if (c == Command.HEARTBEAT) {
                    System.out.println("heartbeat");
                } else {
                    switch (c) {
                        case LOGIN: {
                            String otheruser = Parser.parseUser(st);
                            cs.notifyMessage(otheruser, "has logged in");
                            break;
                        }
                        case EXIT: {
                            String otheruser = Parser.parseUser(st);
                            cs.notifyMessage(otheruser, "has exited");
                            if (username.equals(otheruser)) {
                                in.close();
                                out.close();
                                sock.close();
                                return;
                            }
                            break;
                        }
                        case MSG: {
                            String otheruser = Parser.parseUser(st);
                            String msg = Parser.parseRemaining(st);
                            cs.addMessage(otheruser, msg);
                            break;
                        }
                        case PRIVMSG: {
                            break;
                        }
                        case USERLIST: {
                            System.out.println("userlist");
                            String msg = Parser.parseRemaining(st);
                            String[] ulist = msg.split(" ");
                            cs.addUser(ulist);
                            break;
                        }
                        case STATUS:
                            break;
                    }

                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}