import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

class AuthError extends Exception {
    AuthError(String msg) {
        super(msg);
    }
}

public class Parser {
    public static final Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Parses the first token in the message and maps to a command
     *
     * @param tokenizer
     * @return Command enum
     * @throws IOException
     */
    static Command parseCommand(StringTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasMoreTokens()) {
            log("no command");
            throw new IOException();
        }
        String token = tokenizer.nextToken();
        token = token.toUpperCase();
        switch (token) {
            case "LOGIN":
//                login(remaining);
                return Command.LOGIN;
            case "EXIT":
                return Command.EXIT;
            case "MSG":
                return Command.MSG;
            case "PRIVMSG":
                return Command.PRIVMSG;
            case "USERLIST":
                return Command.USERLIST;
            case "JOIN":
                return Command.JOIN;
            case "STATUS":
                return Command.STATUS;
            case "HEARTBEAT":
                return Command.HEARTBEAT;
            default:
                throw new IllegalStateException("Unknown Command: " + token);
        }
    }

    static void log(String msg) {
        logger.log(Level.INFO, msg);
    }

    /**
     * Parses a username from the tokenizer
     *
     * @param tokenizer
     * @return
     * @throws IOException
     */
    static String parseUser(StringTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasMoreTokens()) {
            log("no user token " + tokenizer.nextToken(""));
            throw new IOException();
        }
        String token = tokenizer.nextToken();
        token = token.toUpperCase();
        return token;
    }

    /**
     * checks if the login status was successful
     *
     * @param msg
     * @return
     * @throws AuthError
     */
    static boolean checkStatus(String msg) throws AuthError {
        if (msg.equals(LoginStatus.Success.getMessage())) {
            return true;
        } else if (msg.equals(LoginStatus.Failure.getMessage())) {
            return false;
        }
        throw new AuthError("status not matched");
    }

    static LoginStatus parseStatus(StringTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasMoreTokens()) {
            log("no status code");
            throw new IOException();
        }
        String token = tokenizer.nextToken();
        token = token.toUpperCase();
        if (token.equals(LoginStatus.Success.getCode())) {
            return LoginStatus.Success;
        } else if (token.equals(LoginStatus.Failure.getCode())) {
            return LoginStatus.Failure;
        }
        throw new IOException("login status parse fail");
    }

    /**
     * Parses the remaining part of the message
     *
     * @param tokenizer
     * @return a string containing the rest of the message
     * @throws IOException
     */
    static String parseRemaining(StringTokenizer tokenizer) throws IOException {
        return tokenizer.nextToken("").trim();
    }
}
