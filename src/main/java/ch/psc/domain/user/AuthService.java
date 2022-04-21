package ch.psc.domain.user;

/**
 * @author Sandro
 */
public interface AuthService {

    User authenticate(String email, String pwd);
    User signup();

    User update(User user);



}
