package app.music.musicstore.data;

import app.music.musicstore.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password, String displayname) {
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser user =
                    new LoggedInUser(username,
                            displayname);
            user.setPassword(password);

            // set the user in in-memory DB

            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
