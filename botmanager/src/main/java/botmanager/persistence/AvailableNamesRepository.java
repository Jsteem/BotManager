package botmanager.persistence;

import botmanager.domain.AvailableName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AvailableNamesRepository extends JpaRepository<AvailableName, String> {

    AvailableName findFirstByOrderByName();
}
