package botmanager;

import botmanager.domain.Account;
import botmanager.persistence.AccountRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Optional;

@Component
public class AccountGeneratorProxy {

    @Autowired
    private AccountRepository accountRepository;


    @PostConstruct
    public void generateAndSaveAccounts() throws IOException {
        InputStream inputStream = new ClassPathResource("static/accounts_proxy.txt").getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] accountInfo = line.trim().split(";");
            if (accountInfo.length == 5) {
                String login = accountInfo[0].trim();
                String password = accountInfo[1].trim();
                String proxyUsername = accountInfo[2].trim();
                String proxyPassword = accountInfo[3].trim();
                String proxyIpPort = accountInfo[4].trim(); // Combined field containing IP and port

                if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {

                    Optional<Account> a = accountRepository.findByLogin(login);
                    if (a.isEmpty()) {
                        System.out.println("login: " + login + " password: " + password);
                        Account account = new Account();

                        account.setLogin(login);
                        account.setPassword(password);
                        account.setProxyUser(proxyUsername);
                        account.setProxyPassword(proxyPassword);
                        account.setCreated(new Timestamp(System.currentTimeMillis()));

                        // Extract IP and port from the proxyIpPort field
                        String[] ipPort = proxyIpPort.split(":");
                        if (ipPort.length == 2) {
                            account.setProxyIp(ipPort[0]);
                            account.setProxyPort(ipPort[1]);
                        }

                        accountRepository.save(account);
                    }
                }
            }
        }
    }
}

