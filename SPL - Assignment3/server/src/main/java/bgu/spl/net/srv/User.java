package bgu.spl.net.srv;

public class User {
    private Integer connectionId=-1;
    private String passcode;

    private String login; //username

    private boolean connected;
    private String accept_version;
    public String serverHost;




    public User(String login, String passcode, boolean connected) {
        this.login = login;
        this.passcode = passcode;
        this.connected = connected;
        this.accept_version = "1.2";
        this.serverHost = "stomp.cs.bgu.ac.il";
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getLogin(){
        return login;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getAccept_version() {
        return accept_version;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setLogin(String userName) {
        this.login = userName;
    }

    public void setAccept_version(String accept_version) {
        this.accept_version = accept_version;
    }

    public void setHost(String serverHost) {
        this.serverHost = serverHost;
    }
}