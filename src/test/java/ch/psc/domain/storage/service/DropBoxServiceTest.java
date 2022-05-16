package ch.psc.domain.storage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.IndividualSpaceAllocation;
import com.dropbox.core.v2.users.SpaceAllocation;
import com.dropbox.core.v2.users.SpaceUsage;

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
  void tearDown() {}

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
      when(dbxWebAuthMock.finishFromCode("code")).thenThrow(new DbxException(""));

      cut.finishFromCode(dbxWebAuthMock, "code");
      fail("Exception expected");

    } catch (Exception e) {
      assertEquals("Wrong code", e.getMessage());
    }
  }

  @Test
  void getAvailableStorageSpace() throws DbxException {
    cut = new DropBoxService(dbxClientV2Mock);

    DbxUserUsersRequests DbxUserUsersRequestsMock = mock(DbxUserUsersRequests.class);

    when(dbxClientV2Mock.users()).thenReturn(DbxUserUsersRequestsMock);

    when(DbxUserUsersRequestsMock.getSpaceUsage()).thenReturn(new SpaceUsage(1_000_000_000_000L,
        SpaceAllocation.individual(new IndividualSpaceAllocation(5_000_000_000_000L))));

    BigDecimal usedStorageSpace = cut.getUsedStorageSpace();

    assertEquals(1_000_000_000_000L, usedStorageSpace.longValue());

  }
}
