import ch.psc.domain.storage.service.DropBoxService;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.IndividualSpaceAllocation;
import com.dropbox.core.v2.users.SpaceAllocation;
import com.dropbox.core.v2.users.SpaceUsage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sandro
 */
public class DropBoxServiceTest {

    private DropBoxService cut;
    @Mock
    private DbxWebAuth dbxWebAuthMock;
    @Mock
    private DbxClientV2 dbxClientV2Mock;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cut = new DropBoxService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void buildAuthRequest() {
        DbxWebAuth.Request act = cut.buildAuthRequest();
        assertNotNull(act);
    }

    @Test
    void finishFromCodeSuccessful() {
        try {
            when(dbxWebAuthMock.finishFromCode("code"))
                    .thenReturn(new DbxAuthFinish("token", 1L, "", "", "", "", ""));

            Map<String, String> code = cut.finishFromCode(dbxWebAuthMock, "code");
            assertEquals("token", code.get("access_token"));

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void finishFromCodeFailed() {
        try {
            when(dbxWebAuthMock.finishFromCode("code"))
                    .thenThrow(new DbxException(""));

            cut.finishFromCode(dbxWebAuthMock, "code");
            fail("Exception expected");

        } catch (Exception e) {
            assertEquals("Wrong code", e.getMessage());
        }
    }

    @Test
    void getDbxRequestConfig() {
        DbxRequestConfig act = cut.getDbxRequestConfig();
        assertEquals(DropBoxService.PRETTY_SECURE_CLOUD, act.getClientIdentifier());
        assertEquals("de-CH", act.getUserLocale());
    }

    @Test
    void getAvailableStorageSpace() throws DbxException {
        cut = new DropBoxService(dbxClientV2Mock);

        DbxUserUsersRequests DbxUserUsersRequestsMock = mock(DbxUserUsersRequests.class);

        when(dbxClientV2Mock.users())
                .thenReturn(DbxUserUsersRequestsMock);

        when(DbxUserUsersRequestsMock.getSpaceUsage())
                .thenReturn(new SpaceUsage(1000L, SpaceAllocation.individual(new IndividualSpaceAllocation(5000L))));

        double act = cut.getAvailableStorageSpace();

        assertEquals(4000, act);

    }
}
