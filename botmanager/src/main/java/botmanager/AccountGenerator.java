package botmanager;

import botmanager.domain.Account;
import botmanager.domain.AvailableName;
import botmanager.persistence.AccountRepository;
import botmanager.persistence.AvailableNamesRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Optional;

@Component
public class AccountGenerator {

    @Autowired
    private AccountRepository accountRepository;


    @PostConstruct
    public void generateAndSaveAccounts() throws IOException {
        InputStream inputStream = new ClassPathResource("static/accounts.txt").getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] accountInfo = line.trim().split("-");
            if (accountInfo.length == 2) {
                String login = accountInfo[0].trim();
                String password = accountInfo[1].trim();

                if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {

                    Optional<Account> a = accountRepository.findByLogin(login);
                    if (a.isEmpty()) {
                        System.out.println("login: " + login + " password: " + password);
                        Account account = new Account();

                        account.setLogin(login);
                        account.setPassword(password);
                        account.setCreated(new Timestamp(System.currentTimeMillis()));
                        accountRepository.save(account);

                    }

                }
            }
        }
    }


}