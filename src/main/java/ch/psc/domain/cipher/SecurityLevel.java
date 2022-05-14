package ch.psc.domain.cipher;

/**
 * This enum shall represent the quality of a cipher algorithm.
 * Currently, this enum is only used in test but not actually presented in the GUI
 * TODO Define the use of this enum in the GUI and implement it
 *
 * @author Tristan, Lorenz
 */
public enum SecurityLevel {

    none(0),
    low(1),
    medium(2),
    high(3);

    private final int level;

    SecurityLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
