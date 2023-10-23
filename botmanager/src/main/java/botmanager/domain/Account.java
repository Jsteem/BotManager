package botmanager.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="accounts")
public class Account {
    @Id
    @Column(name = "login")
    String login;
    String password;
    String displayName;

    Timestamp created;
    Timestamp firstLoggedIn;
    Timestamp lastOnline;

    Double playTimeInHours = 0.0;
    Double totalPlayTimeInHours = 0.0;

    Boolean tutorialIslandComplete = false;
    Boolean romeoAndJulietComplete = false;
    Boolean goblinDiplomacyComplete = false;


    Integer totalValueBank = 0;
    Integer cashStack = 0;

    String totalValueBankString;
    String cashStackString;


    Integer membershipDaysLeft = 0;

    Boolean isBanned = false;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    Skills skills;

    String proxyIp;
    String proxyPort;
    String proxyUser;
    String proxyPassword;

}
