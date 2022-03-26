package ch.psc.domain.cipher;

public enum SecurityLevel {
  
  none(0),
  low(1),
  medium(2),
  high(3);
  
  private final int level;
  
  private SecurityLevel(int level) {
    this.level = level;
  }
  
  public int getLevel() {
    return level;
  }
}
