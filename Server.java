import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Response implements Runnable {
    public static final Log logger = new Log();

    static int BUFFER_SIZE = 4096;
    Socket client;
    BufferedReader in;
    PrintWriter out;
    UserList userlist;
    User user;


    public Response(Socket client, UserList userlist) throws IOException {
        this.client = client;
        this.userlist = userlist;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
    }

    private void log(String msg) {
        if (user != null){
            logger.log(client,user.name + " " + msg);
        }

    }

    boolean f(String line) throws IOException {
        StringTokenizer st = new StringTokenizer(line, " ", false);
        Command c = Parser.parseCommand(st);
        switch (c) {
            case LOGIN: {
                log("user login");
                if (user == null){
                    User newuser = new User(Parser.parseUser(st), this.client, this.out);
                    LoginStatus ls = userlist.login(newuser);
                    newuser.send(ls);
                    if (ls == LoginStatus.Success){
                        user = newuser;
                        log("login successful");
                        userlist.join(user);
                    } else{
                        log("login fail");
                    }
                }
                userlist.broacastUserList();

                break;
            }
            case EXIT: {
                log("user exit");
                User otheruser = new User(Parser.parseUser(st), this.client, this.out);
                if (user != null){
                    if (user.equals(otheruser)){
                        userlist.exit(otheruser);
                        userlist.broacastUserList();
                        return true;
                    }
                }
                break;
            }
            case MSG: {
                log("user msg");
                User otheruser = new User(Parser.parseUser(st), this.client, this.out);
                String message = Parser.parseRemaining(st);
//                use;
                if (user.equals(otheruser)){
                    userlist.broadcast("MSG " + otheruser.name + " " + message);
                }
                break;
            }
            case PRIVMSG: {
                break;
            }
            case USERLIST: {
                log("userlist");
                User otheruser = new User(Parser.parseUser(st), this.client, this.out);
                if(user.equals(otheruser)){
                    user.send(userlist.userList());
                }
            }
//            case HEARTBEAT: {
//                log("heartbeat");
//            }
        }
        return false;

    }

    void close() {
        try {
            log("closing");
            if (user != null)
                userlist.exit(user);
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (client != null)
                client.close();
            log("closed");
        } catch (IOException e) {
            log("closing exception");
            e.printStackTrace();
        }
    }

    /**
     * Runs the thread and parses the request from the client socket and returns a response
     */
    public void run() {
        try {
            while (true) {
                String line = in.readLine();
                System.out.println("line: " + line);
                if (line == null) {
                    log("null closing");
                    break;
                }
                if (f(line)){
                    break;
                }
            }
            close();
        } catch (IOException e) {
            log("io exception run: " + e.getMessage());
            close();
        }
        log("done");
    }
}

class UserList {
    public static final Log logger = new Log();

    static final List<User> userlist = Collections.synchronizedList(new ArrayList<>());

    UserList() {
    }

    boolean contains(User other) {
        for (User cur : userlist) {
            if (cur.client.isClosed()) {
                userlist.remove(cur);
                continue;
            }
            if (cur.equals(other)) {
                return true;
            }
        }
        return false;
    }
    boolean remove(User other) {
        for (User cur : userlist) {
            if (cur.client.isClosed()) {
                userlist.remove(cur);
                continue;
            }
            if (cur.equals(other)) {
                userlist.remove(cur);
                return true;
            }
        }
        return false;
    }
    void join(User user) {
        userlist.add(user);
        broadcast("LOGIN " + user.name);
        userList();
    }

    String userList(){
        StringBuilder sb = new StringBuilder();
        sb.append("USERLIST ");
        for (User u : userlist) {
            sb.append(u.name);
            sb.append(" ");
        }
        return sb.toString();
    }

    void broacastUserList(){
        broadcast(userList());
    }


    LoginStatus login(User user) {
        LoginStatus ls;
        if (contains(user)) {
            ls = LoginStatus.Failure;
        } else {
            ls = LoginStatus.Success;

        }
        return ls;
    }

    LoginStatus exit(User user) {
        LoginStatus ls;
        if (remove(user)) {
            ls = LoginStatus.Success;
            broadcast("EXIT " + user.name);
            broacastUserList();
        } else {
            ls = LoginStatus.Failure;
        }
        return ls;
    }

    void broadcast(String message) {
        for (User user : userlist) {
            try {
                user.send(message);
            } catch (IOException e) {
                logger.log(user, "BROADCAST FAIL" + message);
            }
        }
    }
}

public class Server {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        UserList ul = new UserList();
        try {
            int port = 63546;
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Running on port " + port);
            while (true) {
                Socket client = ss.accept();
                pool.submit(new Response(client, ul));
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }
}
