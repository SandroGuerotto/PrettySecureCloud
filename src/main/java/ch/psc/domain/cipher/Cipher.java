package ch.psc.domain.cipher;

import java.util.List;
import java.util.concurrent.Future;
import ch.psc.domain.file.File;

public interface Cipher {
  
  public List<Future<File>> encrypt(Key key, List<File> files);
  
  public List<Future<File>> decrypt(Key key, List<File> files);
  
  public String getType();
  
  public SecurityLevel getSecurityLevel();
}
