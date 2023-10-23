package botmanager;


import botmanager.domain.Account;
import botmanager.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaytimeResetScheduler {

    @Autowired
    private AccountRepository accountRepository;

    //sec min hour day month
    @Scheduled(cron = "0 0 0 * * *")
    public void resetPlayTimeInHours() {
        System.out.println("resetting");
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            account.setPlayTimeInHours(0.0);
            accountRepository.save(account);
        }
    }
}
