package ch.psc.domain.cipher;

import ch.psc.domain.file.File;

import java.util.List;
import java.util.concurrent.Future;

public interface Cipher {
  
  public List<Future<File>> encrypt(Key key, List<File> files);
  
  public List<Future<File>> decrypt(Key key, List<File> files);
  
  public String getType();
  
  public SecurityLevel getSecurityLevel();
}
