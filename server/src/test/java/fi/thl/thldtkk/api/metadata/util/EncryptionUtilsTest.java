package fi.thl.thldtkk.api.metadata.util;

import static com.google.common.base.Charsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.security.GeneralSecurityException;
import java.util.Base64;
import org.junit.Test;

public class EncryptionUtilsTest {

  private String exampleSecretKey = Base64.getEncoder()
      .encodeToString("examplesecretkey".getBytes(UTF_8));

  @Test
  public void shouldEncryptAndDecryptString() throws GeneralSecurityException {
    String input = "This is a test";

    String[] encryptedMsgAndIv = EncryptionUtils.encrypt(input, exampleSecretKey);

    assertNotEquals(input, encryptedMsgAndIv[0]);

    String decryptedMsg = EncryptionUtils
        .decrypt(encryptedMsgAndIv[0], encryptedMsgAndIv[1], exampleSecretKey);

    assertEquals(input, decryptedMsg);
  }

}
