package rose_hulman.edu.monopolygame;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Stack;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.Game.GameMapFragment;
import rose_hulman.edu.monopolygame.Lobby.GameInfoContent;
import rose_hulman.edu.monopolygame.Lobby.GameInfoFragment;
import rose_hulman.edu.monopolygame.Lobby.GameMainFragment;
import rose_hulman.edu.monopolygame.Lobby.LoginFragment;
import rose_hulman.edu.monopolygame.Lobby.PlayerInfoFragment;
import rose_hulman.edu.monopolygame.Lobby.WelcomeFragment;

public class MainActivity extends AppCompatActivity implements GameInfoFragment.GameInfoFragmentListener, WelcomeFragment.WelcomeFragmentListener, LoginFragment.LoginFragmentListener, PlayerInfoFragment.OnPlayerInfoFragmentListener {

    private Stack<Fragment> mFragmentStack;
    private String UserName;

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
    protected void onDestroy() {
        super.onDestroy();

        if (mFragmentStack.peek() instanceof PlayerInfoFragment || mFragmentStack.peek() instanceof GameMainFragment) {
            (new QuitGameClass()).execute();
        } else {
            DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
            dbService.closeConnection();
        }
    }


    @Override
    public void enterRoom(GameInfoContent.GameInfo item) {
        GameInfoContent.GameInfo[] infoList = new GameInfoContent.GameInfo[1];
        infoList[0] = item;
        (new JoinGameClass()).execute(infoList);
    }

    public void enterGameRoom(GameInfoContent.GameInfo item) {
        mFragmentStack.push(PlayerInfoFragment.newInstance(item));
        replacefragment(mFragmentStack.peek());
    }

    @Override
    public void createNewGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Game");
        View view = getLayoutInflater().inflate(R.layout.create_game_dialog, null, false);
        builder.setView(view);
        final EditText MapIndex = (EditText) view.findViewById(R.id.dialog_mapIndex);
        final EditText InitMoney = (EditText) view.findViewById(R.id.dialog_starting_money);
        final EditText maxTurns = (EditText) view.findViewById(R.id.dialog_maxturns);
        final EditText TargetAmount = (EditText) view.findViewById(R.id.dialog_TargetAmount);
        final EditText GameName = (EditText) view.findViewById(R.id.dialog_GameName);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] input = new String[6];
                input[0] = MapIndex.getText().toString();
                input[1] = UserName;
                input[2] = InitMoney.getText().toString();
                input[3] = maxTurns.getText().toString();
                input[4] = TargetAmount.getText().toString();
                input[5] = GameName.getText().toString();
                (new AddGameClass()).execute(input);
            }
        }).show();
    }


    @Override
    public void StartGame(GameInfoContent.GameInfo gameInfo) {
        GameInfoContent.GameInfo[] infoList = new GameInfoContent.GameInfo[1];
        infoList[0] = gameInfo;
        (new StartGameClass()).execute(infoList);
    }

    class StartGameClass extends AsyncTask<GameInfoContent.GameInfo, Void, Boolean> {
        @Override
        protected Boolean doInBackground(GameInfoContent.GameInfo... gameInfos) {
            try {
                GameInfoContent.GameInfo info = gameInfos[0];
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{call Start_Game(?)}");
                cs.setInt(1, info.gameid);
                cs.executeUpdate();
                createGame(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class LeaveGameClass extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{call leave_game(?)}");
                String queue = "Select ID from [User] where UserName = ?";
                PreparedStatement stmt = con.prepareStatement(queue);
                stmt.setString(1, UserName);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int userID = rs.getInt(1);
                cs.setInt(1, userID);
                cs.executeUpdate();
                mFragmentStack.pop();
                replacefragment(mFragmentStack.peek());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void createGame(GameInfoContent.GameInfo gameInfo) {
        GameMapFragment.newInstance(gameInfo);
        //TODO: Create map based on GameInfo
    }


    class AddGameClass extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... input) {
            try {
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{?=call Add_Game(?,?,?,?,?,?,?)}");
                String queue = "Select ID from [User] where UserName = ?";
                PreparedStatement stmt = con.prepareStatement(queue);
                stmt.setString(1, input[1]);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                input[1] = String.valueOf(rs.getInt(1));
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setInt(2, Integer.valueOf(input[0]));
                cs.setInt(3, Integer.valueOf(input[1]));
                cs.setDouble(4, Double.valueOf(input[2]));
                cs.setInt(5, Integer.valueOf(input[3]));
                cs.setInt(6, Integer.valueOf(input[4]));
                cs.setString(7, "23");//TODO: Deal with this later
                cs.setString(8, input[5]);
                cs.executeUpdate();
                int returnValue = cs.getInt(1);
                System.out.println(returnValue);
//                switch (returnValue) {
//                    case 1:
//                        return false;
//                }
                int gameid = 0;
                queue = "Select GameID from [Game] where GameName = ?";
                stmt = con.prepareStatement(queue);
                stmt.setString(1, input[5]);
                rs = stmt.executeQuery();
                rs.next();
                gameid = rs.getInt(1);
                GameInfoContent.GameInfo gameInfo = new GameInfoContent.GameInfo(gameid, Integer.valueOf(input[0]), Integer.valueOf(input[2]), Integer.valueOf(input[3]), Integer.valueOf(input[4]), input[5]);
                enterGameRoom(gameInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//        protected void onPostExecute(Boolean result) {
//            if (result) {
//                loginSucceeded();
//            } else {
//                mListener.showErrorMessage(false);
//            }
//        }


    @Override
    public void enterLobby() {
        mFragmentStack.push(GameInfoFragment.newInstance());
        replacefragment(mFragmentStack.peek());
    }

    class JoinGameClass extends AsyncTask<GameInfoContent.GameInfo, Void, Boolean> {
        GameInfoContent.GameInfo info;

        @Override
        protected Boolean doInBackground(GameInfoContent.GameInfo... infos) {
            try {
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{?=call Join_Game(?,?,?)}");

                String queue = "Select ID from [User] where UserName = ?";
                PreparedStatement stmt = con.prepareStatement(queue);
                stmt.setString(1, UserName);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int userID = rs.getInt(1);

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, infos[0].gameName);
                cs.setInt(3, userID);
                cs.setString(4, "GOUDAN");

                cs.executeUpdate();
                info = infos[0];
                int returnValue = cs.getInt(1);
                return (returnValue == 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                enterGameRoom(info);
            } else {
                showErrorMessage("Fail to join the game");
            }
        }
    }


    @Override
    public void openScoreboard() {
        //Todo: Show Scoreboard
    }


    @Override
    public void signOut() {
        mFragmentStack.pop();
        replacefragment(mFragmentStack.peek());
    }

    @Override
    public void onBackPressed() {
        if (mFragmentStack.size() > 1) {
            if (mFragmentStack.peek() instanceof PlayerInfoFragment || mFragmentStack.peek() instanceof GameMainFragment) {
                (new LeaveGameClass()).execute();
            } else {
                mFragmentStack.pop();
                replacefragment(mFragmentStack.peek());
            }
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
    public void switchToWelcomePage(String userID) {
        UserName = userID;
        mFragmentStack.push(WelcomeFragment.newInstance());
        replacefragment(mFragmentStack.peek());
    }

    @Override
    public void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    class QuitGameClass extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{call leave_game(?)}");
                String queue = "Select ID from [User] where UserName = ?";
                PreparedStatement stmt = con.prepareStatement(queue);
                stmt.setString(1, UserName);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int userID = rs.getInt(1);
                cs.setInt(1, userID);
                cs.executeUpdate();
                mFragmentStack.pop();
                replacefragment(mFragmentStack.peek());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
            dbService.closeConnection();
        }
    }
}
