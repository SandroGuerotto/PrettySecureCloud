package ch.psc.domain.cipher;

import ch.psc.domain.file.PscFile;

import java.util.List;
import java.util.concurrent.Future;

public interface PscCipher {
  
  public List<Future<PscFile>> encrypt(Key key, List<PscFile> files);
  
  public List<Future<PscFile>> decrypt(Key key, List<PscFile> files);
  
  public String getType();
  
  public SecurityLevel getSecurityLevel();
}
