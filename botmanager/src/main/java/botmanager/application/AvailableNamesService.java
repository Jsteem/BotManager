package botmanager.application;

import botmanager.domain.AvailableName;
import botmanager.persistence.AvailableNamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AvailableNamesService {

    @Autowired
    AvailableNamesRepository availableNamesRepository;


    public Response<?> getFirstAvailableName() {

        AvailableName availableName = availableNamesRepository.findFirstByOrderByName();

        if (availableName != null) {

            availableNamesRepository.delete(availableName);

            Response<String> response = new Response(Response.ResponseStatus.SUCCESS, "");
            response.object = availableName.getName();
            return response;
        } else {
            return new Response(Response.ResponseStatus.FAIL, "No available name");
        }
    }

}
