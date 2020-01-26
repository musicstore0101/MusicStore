package app.music.musicstore.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import app.music.musicstore.MainActivity;
import app.music.musicstore.R;
import app.music.musicstore.data.model.DBUserAdapter;
import app.music.musicstore.data.model.LoggedInUser;

import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiDownloadPath;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public final int PERMISSION_EXTERNAL_STORAGE_WRITE = 1;
    public final int PERMISSION_EXTERNAL_STORAGE_READ = 2;
    public final int PERMISSION_INTERNET = 3;

    EditText usernameEditText;
    EditText passwordEditText;
    EditText displaynameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PrintStream o = new PrintStream(new File(g_mohammadRafiDownloadPath+"A.txt"));
            System.setOut(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        displaynameEditText = findViewById(R.id.Displayname);


        if (!hasAllRequiredPermissions()) {
            getAllPermissionsFromUser();
        } else {
            System.out.println("Shantanu coming inside permissions");
            LoggedInUser user = null;
            user = getLoggedInUser();
            if (user != null && user.getUserId() != "") {
                System.out.println("@@@@@@@@ shantanu user exists after permission");
                usernameEditText.setText(user.getUserId());
                passwordEditText.setText(user.getPassword());
                displaynameEditText.setText(user.getDisplayName());
                loginViewModel.login(user.getUserId(), user.getPassword(), user.getDisplayName());
            }
        }

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {

                showLoginResult(loginResult);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            displaynameEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                /*
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        displaynameEditText.getText().toString());
                */

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String displayname = displaynameEditText.getText().toString();
                doRemoteLogin(username, password, displayname);
            }
        });
    }

    public void showLoginResult(LoginResult loginResult) {
        if (loginResult == null) {
            return;
        }
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.GONE);
        if (loginResult.getError() != null) {
            showLoginFailed(loginResult.getError());
        }
        if (loginResult.getSuccess() != null) {
            updateUiWithUser(loginResult.getSuccess());
        }

        if(loginResult.getSuccess() != null) {
            setResult(Activity.RESULT_OK);

            try {
                DBUserAdapter dbUser = new DBUserAdapter(LoginActivity.this);
                dbUser.open();
                // shantanu to delete later
                System.out.println("@@@@@@@ Shantanu adding user, userid = " + usernameEditText.getText().toString()
                        + " displayname = " + displaynameEditText.getText().toString());
                dbUser.AddUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        displaynameEditText.getText().toString());
                dbUser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Complete and destroy login activity once successful
            finish();

            //shantanu
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);

            System.out.println("^^^^^^^^^^^^^^ shantanu before changing activity");
            LoginActivity.this.startActivity(myIntent);
        }

        else
        {
            DBUserAdapter dbUser = new DBUserAdapter(LoginActivity.this);
            dbUser.open();
            dbUser.removeAll();
            dbUser.close();

            Intent myIntent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }
    }

    private LoggedInUser doRemoteLogin(String username, String password, String displayname) {
        System.out.println("^^^^^^^^^^^^ shantanu coming here : " + username + " : " + password);
        LoggedInUser user = null;
        String parameters = username + "!" + password + "!" + displayname;

        System.out.println("^^^^^^^^^^^^ shantanu coming here : " + parameters);

        new AuthenticateUser(loginViewModel).execute(parameters);

        System.out.println("@@@@ shantanu just before returning");
        return user;
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = "Welcome " + model.getDisplayName() + "!";
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        DBUserAdapter dbUser = new DBUserAdapter(LoginActivity.this);
        dbUser.open();
        user = dbUser.Login();
        dbUser.close();
        return user;
    }

    private void getAllPermissionsFromUser() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for write permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_WRITE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for read permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_READ);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for internet permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
        }
    }

    private boolean hasAllRequiredPermissions() {
        System.out.println("!!!!!!! shantanu " + new Exception().getStackTrace()[1].getMethodName());
        boolean status = false;
        boolean status1 = false, status2 = false, status3 = false;

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            System.out.println("Shantanu has write permissions");
            status1 |= true;
        } else {
            status1 |= false;
            System.out.println("Shantanu has no write permissions");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu has read permissions");
            status2 |= true;
        } else {
            status2 |= false;
            System.out.println("Shantanu has no read permissions");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            status3 |= true;
            System.out.println("Shantanu has internet permissions");
        } else {
            status3 |= false;
            System.out.println("Shantanu has no internet permissions");
        }

        //Toast.makeText(MainActivity.this, "Write permissions: "+status1, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, "Read permissions: "+status2, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, "Internet permissions: "+status3, Toast.LENGTH_SHORT).show();

        status = status1 & status2 & status3;
        //Toast.makeText(MainActivity.this, "All permissions: "+status, Toast.LENGTH_SHORT).show();

        return status;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("Shantanu coming inside permissions");
                    /*
                    LoggedInUser user = getLoggedInUser();
                    if (user != null) {
                        System.out.println("@@@@@@@@ shantanu user exists after permission");
                        usernameEditText.setText(user.getUserId());
                        passwordEditText.setText(user.getPassword());
                        displaynameEditText.setText(user.getDisplayName());
                        loginViewModel.login(user.getUserId(), user.getPassword(), user.getDisplayName());
                    }
                     */
                } else {

                }
                break;
            }
            case PERMISSION_EXTERNAL_STORAGE_READ: {

            }
            break;
            case PERMISSION_INTERNET: {

            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
