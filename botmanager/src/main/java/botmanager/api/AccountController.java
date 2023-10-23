package botmanager.api;

import botmanager.application.AccountService;
import botmanager.application.Response;
import botmanager.domain.Skills;
import botmanager.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/{login}/displayName")
    public ResponseEntity<?> updateDisplayName(@PathVariable String login, @RequestBody String displayName) {
        Response response = accountService.updateAccountWithDisplayName(login, displayName);
        System.out.println("updating login and displayname: " + displayName);

        return getResponseEntity(response);

    }
    @PostMapping("/{login}/skills")
    public ResponseEntity<?> updateSkills(@PathVariable String login, @RequestBody Skills newSkills) {
        Response response = accountService.updateAccountWithSkills(login, newSkills);

        return getResponseEntity(response);
    }
    @GetMapping("/account")
    public ResponseEntity<?> getAccountInfo(){
        Response response = accountService.getAccountInfo();

        return getResponseEntity(response);
    }

    @PostMapping("/{login}/heartbeat")
    public ResponseEntity<?> setHeartBeat(@PathVariable String login, @RequestBody int minutesToAdd){
        Response response = accountService.setHeartbeat(login, minutesToAdd);
        return getResponseEntity(response);
    }

    @PostMapping("/{login}/setAccountValue")
    public ResponseEntity<?> setTutIsland(@PathVariable String login, @RequestBody Object[] fieldAndValue){
        Response response = accountService.setValueOfField(login, (String) fieldAndValue[0], fieldAndValue[1]);
        return getResponseEntity(response);
    }



    private ResponseEntity<?> getResponseEntity(Response response){
        if(response.status == Response.ResponseStatus.SUCCESS){
            return ResponseEntity.status(HttpStatus.OK).body(response.object);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.message);
    }
}
