package rose_hulman.edu.monopolygame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Stack;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.DatabaseConnection.Informations;
import rose_hulman.edu.monopolygame.DatabaseConnection.UserService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private Stack<Fragment> mFragmentStack;
    private EditText mPasswordView;
    private EditText  mUserView;
    private DatabaseConnectionService dbService;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.username);
        dbService = DatabaseConnectionService.getInstance(Informations.serverName,Informations.databaseName);
        mPasswordView = (EditText) findViewById(R.id.password);
        userService = new UserService(dbService);
        Button mUserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(mUserView.getText().toString(),mPasswordView.getText().toString());
            }
        });
        Button mUserRegButton = (Button) findViewById(R.id.user_reg_button);
        mUserRegButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordValid(mPasswordView.getText().toString())){
                    //TODO: Display error message
                }
                attemptRegister(mUserView.getText().toString(),mPasswordView.getText().toString());
            }
        });
        dbService.connect(Informations.serverUsername,Informations.serverPassword);//TODO: Handle Cases of failure to connect to DB
    }

    private void attemptLogin(String user, String pw) {
        if (userService.login(user, pw)) {
            loginSucceeded();
        }
    }

    private void attemptRegister(String user, String pw) {
        if (userService.register(user, pw)) {
            loginSucceeded();
        }
    }

    public static void loginSucceeded() {
        //TODO: Switch to main page

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }
}

