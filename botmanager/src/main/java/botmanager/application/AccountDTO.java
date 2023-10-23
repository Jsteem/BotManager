package botmanager.application;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    String proxyIp;
    String proxyPort;
    String proxyUser;
    String proxyPassword;


    String login;
    String password;
    Boolean isMember;
    String startScript;

}
