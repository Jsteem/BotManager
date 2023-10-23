package botmanager.api;


import botmanager.application.AvailableNamesService;
import botmanager.application.Response;
import botmanager.domain.AvailableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/names")
public class AvailableNamesController {
    @Autowired
    private AvailableNamesService availableNamesService;

    @GetMapping("/first")
    public ResponseEntity<?> getFirstAvailableName() {
        Response response = availableNamesService.getFirstAvailableName();

        if(response.status == Response.ResponseStatus.SUCCESS){
            return ResponseEntity.status(HttpStatus.OK).body(response.object);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.message);


    }
    @PostMapping("/create/{id}")
    public ResponseEntity<?> createAccount(@PathVariable long id, @PathVariable String chosenName){
        //todo: delete the name as soon as its used by the get request?
        //todo: this function should just assign the name to the account
        return null;

    }

}
