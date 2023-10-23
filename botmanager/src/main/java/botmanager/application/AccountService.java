package botmanager.application;

import botmanager.domain.Account;
import botmanager.domain.Skills;
import botmanager.persistence.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.lang.reflect.Field;


@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;


    public Response<?> updateAccountWithDisplayName(String login, String displayName) {
        Optional<Account> optionalAccount = accountRepository.findByLogin(login);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setDisplayName(displayName);
            account.setFirstLoggedIn(new Timestamp(System.currentTimeMillis()));
            accountRepository.save(account);
            return new Response(Response.ResponseStatus.SUCCESS, "");
        } else {
            return new Response(Response.ResponseStatus.FAIL, "Could not update account displayName");
        }

    }
    public Response<?> updateAccountWithSkills(String login, Skills skills){
        Optional<Account> optionalAccount = accountRepository.findByLogin(login);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Skills oldSkills = account.getSkills();
            skills.setLastUpdated(new Timestamp(System.currentTimeMillis()));

            if(oldSkills == null){
                skills.setAccount(account);
                account.setSkills(skills);
            }
            else{
                try {
                    BeanUtils.copyProperties(skills, oldSkills, new String[]{"id"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            accountRepository.save(account);
            return new Response(Response.ResponseStatus.SUCCESS, "");
        } else {
            return new Response(Response.ResponseStatus.FAIL, "Could not update account skills");
        }
    }

    public Response<?> getAccountInfo(){
        //todo: algorithm must calculate what account is best suited to login to the instance
        //todo: the algorithm has to factor in: current playtime (resets every 24 hours)



        List<Account> accountList = accountRepository.findAll();
        if(accountList.isEmpty()){
            return new Response(Response.ResponseStatus.FAIL, "No suitable account found");
        }


        Account account = accountList.stream()
                .filter(a -> a.getLastOnline() == null || a.getLastOnline().before(new Timestamp(System.currentTimeMillis() - 2 * 60 * 1000)))
                .filter(a -> a.getIsBanned() == null || !a.getIsBanned())
                .sorted(Comparator.comparingInt((Account a) -> {
                    Integer daysLeft = a.getMembershipDaysLeft();
                    return Objects.isNull(daysLeft) ? Integer.MIN_VALUE : daysLeft;
                }).reversed()
                .thenComparing((Account a) -> a.getProxyIp() == null))
                .findFirst()
                .orElse(null);


//        accountList.stream()
//                .filter(a -> a.getLastOnline() == null || a.getLastOnline().before(new Timestamp(System.currentTimeMillis() - 2 * 60 * 1000)))
//                .filter(a -> a.getIsBanned() == null || !a.getIsBanned())
//                .sorted(Comparator.comparingInt((Account a) -> {
//                    Integer daysLeft = a.getMembershipDaysLeft();
//                    return Objects.isNull(daysLeft) ? Integer.MIN_VALUE : daysLeft;
//                }).reversed()
//                        .thenComparing((Account a) -> a.getProxyIp() == null)).forEach(a -> System.out.println(a.getLogin()));



        //note: if no accounts are found, i.e. all of them have playtime greater than 8, a 404 response is sent, no account logs in
        if(account == null){
            return new Response(Response.ResponseStatus.FAIL, "No suitable account found");
        }

        account.setLastOnline(new Timestamp(System.currentTimeMillis()));
        accountRepository.save(account);


        AccountDTO accountDTO = new AccountDTO();
        accountDTO.login = account.getLogin();
        accountDTO.password = account.getPassword();

        accountDTO.isMember = account.getMembershipDaysLeft() > 0;


        accountDTO.proxyUser = account.getProxyUser();
        accountDTO.proxyPassword = account.getProxyPassword();

        accountDTO.proxyIp = account.getProxyIp();
        accountDTO.proxyPort = account.getProxyPort();

        accountDTO.startScript = this.findScript(account);

        Response response = new Response(Response.ResponseStatus.SUCCESS, "");
        response.object = accountDTO;
        return response;

    }
    public Response<?> setValueOfField(String login, String field, Object value){
        Optional<Account> optionalAccount = accountRepository.findByLogin(login);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            try{
                System.out.println("field: " + field + " value: " + value.toString());
                Field declaredField = Account.class.getDeclaredField(field);
                declaredField.setAccessible(true);
                declaredField.set(account, value);
                accountRepository.save(account);
                return new Response(Response.ResponseStatus.SUCCESS, "Value was set correctly");
            }
            catch(Exception e){
                e.printStackTrace();
                return new Response(Response.ResponseStatus.FAIL, "Something went wrong setting the value");
            }


        }
        return new Response(Response.ResponseStatus.FAIL, "No account found with that login");
    }

    public Response<?> setHeartbeat(String login, int minutesToAdd){

        Optional<Account> optionalAccount = accountRepository.findByLogin(login);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setLastOnline(new Timestamp(System.currentTimeMillis()));


            double hoursToAdd = minutesToAdd / 60.0;
            //System.out.println("adding this many hours to the account: " + hoursToAdd);
            account.setPlayTimeInHours(account.getPlayTimeInHours() + hoursToAdd);

            account.setTotalPlayTimeInHours(account.getTotalPlayTimeInHours() + hoursToAdd);



            accountRepository.save(account);

            Response response = new Response(Response.ResponseStatus.SUCCESS, "Account updated");
            //todo: if playtime > 3 h, take h/2 break
            response.object = findScript(account);

            return response;
        }
        return new Response(Response.ResponseStatus.FAIL, "No account found with that login");

    }

    public String findScript(Account account){
        Skills skills = account.getSkills();

        if(account.getMembershipDaysLeft() > 0){
//            if(skills.getFletching() > 70){
                return "fletching";
//            }
//            else{
//                return "spades";
//            }

        }

        if(!account.getTutorialIslandComplete()){
            System.out.println("requesting tutorial island for login: " + account.getLogin());
            return "tutorialIsland";

        }
        else if(skills.getFiremaking() < 30){
            System.out.println("requesting firemaking for login: " + account.getLogin());
            return "firemakingTo30";
        }
        else if(skills.getFishing() < 30 || skills.getMining() < 31){
            System.out.println("requesting fishing for login: " + account.getLogin());
            return "fishingAndMiningTo30";
        }
        else if(skills.getAttack() < 30 || skills.getStrength() < 30 || skills.getDefence() < 30){
            System.out.println("requesting chicken fighter for login: " + account.getLogin());
            return "chickenFighter";
        }
        return "";
    }


}
