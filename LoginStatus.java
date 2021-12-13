public enum LoginStatus {
    Failure("<0>"),
    Success("<1>");
    private final String msg;

    LoginStatus(String msg) {
        this.msg = msg;
    }

    String getMessage() {
        return "STATUS " + msg;
    }

    String getCode() {
        return msg;
    }


}
