package ch.psc.presentation.controller.register;

import java.util.List;

public interface RegisterFlow {
    List<Object> getData();
    boolean isValid();
}
