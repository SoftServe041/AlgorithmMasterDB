import com.cargohub.models.Location;
import com.cargohub.repository.LocationRepository;
import com.cargohub.service.impl.LocationServiceNeo4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

public class LocationServiceNeo4jImplTest {

    @InjectMocks
    LocationServiceNeo4j locationServiceNeo4j;

    @Mock
    LocationRepository locationRepository;

    Location subject;
    List<Location> t;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        subject = new Location();
        subject.setId(9999L);
    }

    @Test
    void delete() {
        when(locationRepository.getAllLocations()).thenReturn(t);

    }

}
