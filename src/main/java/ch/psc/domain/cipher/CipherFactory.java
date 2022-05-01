package ch.psc.domain.cipher;

import ch.psc.exceptions.FatalImplementationException;

import java.lang.reflect.InvocationTargetException;

public class CipherFactory {


    /**
     * Creates a new Instance of the provided {@code type}.
     * <p>
     * Under the hood Reflection is used to
     * create new instances. Since {@link CipherAlgorithms} and {@link PscCipher} implementations
     * are both under our control, errors during reflection are collected under
     * {@link FatalImplementationException}s.
     *
     * @param type The type of {@link PscCipher}, which will be generated.
     * @return New Instance of the {@link PscCipher} implementation.
     * @throws FatalImplementationException If something during reflection goes wrong. Most likely a
     *                                      configuration error in {@link CipherAlgorithms}. Please check error message and
     *                                      parent throwable.
     */
    public static PscCipher createCipher(String type) throws FatalImplementationException {
        return createCipher(CipherAlgorithms.valueOf(type));
    }

    /**
     * Creates a new Instance of the provided {@code type}.
     * <p>
     * Under the hood Reflection is used to
     * create new instances. Since {@link CipherAlgorithms} and {@link PscCipher} implementations
     * are both under our control, errors during reflection are collected under
     * {@link FatalImplementationException}s.
     *
     * @param type The type of {@link PscCipher}, which will be generated.
     * @return New Instance of the {@link PscCipher} implementation.
     * @throws FatalImplementationException If something during reflection goes wrong. Most likely a
     *                                      configuration error in {@link CipherAlgorithms}. Please check error message and
     *                                      parent throwable.
     */
    public static PscCipher createCipher(CipherAlgorithms type) throws FatalImplementationException {

        try {
            return type.getCipherClass().getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalArgumentException e) {
            throw new FatalImplementationException("Each class inheriting from PscCipher must implement an empty constructor!", e);
        } catch (InvocationTargetException e) {
            throw new FatalImplementationException("Constructor of " + type.getCipherClass() + " threw an exception:", e);
        } catch (InstantiationException e) {
            throw new FatalImplementationException("Seems like the Enum '" + CipherAlgorithms.class + "is contains abstract classes. Please contact the PSC team!", e);
        } catch (SecurityException | IllegalAccessException e) {
            throw new FatalImplementationException("Access to the empty constructor of '" + type.getClass() + "' is denied!", e);
        }

    }
}
