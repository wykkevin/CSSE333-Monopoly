package rose_hulman.edu.monopolygame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.Lobby.GameInfoContent;
import rose_hulman.edu.monopolygame.Lobby.GameInfoFragment;
import rose_hulman.edu.monopolygame.Lobby.LoginFragment;
import rose_hulman.edu.monopolygame.Lobby.WelcomeFragment;

public class MainActivity extends AppCompatActivity implements GameInfoFragment.GameInfoFragmentListener, WelcomeFragment.WelcomeFragmentListener, LoginFragment.LoginFragmentListener {

    private Stack<Fragment> mFragmentStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentStack = new Stack<>();
        Fragment loginFrag = LoginFragment.newInstance();
        mFragmentStack.push(loginFrag);
        replacefragment(loginFrag);
    }



    @Override
    public void onGameInfoFragmentInteraction(GameInfoContent.GameInfo item) {
        //TODO: Switch to game room
    }

    @Override
    public void enterLobby() {
        //Todo: Show lobby
    }

    @Override
    public void openScoreboard() {
        //Todo: Show Scoreboard
    }

    @Override
    public void signOut() {
        DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
        dbService.closeConnection();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentStack.size() > 1) {
            mFragmentStack.pop();
            replacefragment(mFragmentStack.peek());
        } else {
            super.onBackPressed();
        }
    }

    public void replacefragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void switchToWelcomePage() {
        mFragmentStack.push(WelcomeFragment.newInstance());
        replacefragment(mFragmentStack.peek());
    }

    @Override
    public void showErrorMessage(boolean isRegister) {
        String message;
        if (isRegister) {
            message = "Registration Failed!";
        } else {
            message = "Login Failed!";
        }
        new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}
