package botmanager;

import botmanager.domain.AvailableName;
import botmanager.persistence.AvailableNamesRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class NameGenerator {

    @Autowired
    private AvailableNamesRepository namesRepository;


    @PostConstruct
    public void generateAndSaveNames() throws IOException {
        InputStream inputStream = new ClassPathResource("static/names.txt").getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String name = line.trim();
            if (!StringUtils.isEmpty(name)) {
                AvailableName availableName = new AvailableName();
                availableName.setName(name);
                namesRepository.save(availableName);
            }
        }

    }
}