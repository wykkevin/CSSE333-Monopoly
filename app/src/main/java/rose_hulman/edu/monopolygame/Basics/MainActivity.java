package rose_hulman.edu.monopolygame.Basics;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Stack;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.Game.GameViewFragment;
import rose_hulman.edu.monopolygame.GameRoom.PlayerInfoContent;
import rose_hulman.edu.monopolygame.GameRoom.PlayerInfoFragment;
import rose_hulman.edu.monopolygame.Lobby.GameInfoContent;
import rose_hulman.edu.monopolygame.Lobby.GameInfoFragment;
import rose_hulman.edu.monopolygame.R;
import rose_hulman.edu.monopolygame.WelcomePages.LoginFragment;
import rose_hulman.edu.monopolygame.WelcomePages.WelcomeFragment;

public class MainActivity extends AppCompatActivity implements GameInfoFragment.GameInfoFragmentListener, WelcomeFragment.WelcomeFragmentListener, LoginFragment.LoginFragmentListener, PlayerInfoFragment.OnPlayerInfoFragmentListener {

    private Stack<Fragment> mFragmentStack;
    private static String UserName;

    public static String getUserName() {
        return UserName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentStack = new Stack<>();
        Fragment loginFrag = LoginFragment.newInstance();
        mFragmentStack.push(loginFrag);
        replacefragment(loginFrag);
    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mFragmentStack.peek() instanceof PlayerInfoFragment || mFragmentStack.peek() instanceof GameViewFragment) {
//            (new QuitGameClass()).execute();
//        } else {
//            DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
//            dbService.closeConnection();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mFragmentStack.peek() instanceof PlayerInfoFragment || mFragmentStack.peek() instanceof GameViewFragment) {
//            (new QuitGameClass()).execute();
//        } else {
//            DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
//            dbService.closeConnection();
//        }
//    }

    public void selectCharacter(final Object[] input, final Boolean isCreate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Game");
        View view = getLayoutInflater().inflate(R.layout.select_character_dialog, null, false);
        builder.setView(view);
        final EditText CharacterName = (EditText) view.findViewById(R.id.dialog_characterName);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isCreate) {
                    input[5] = CharacterName.getText().toString();
                    (new AddGameClass()).execute((String[]) input);
                } else {
                    input[1] = CharacterName.getText().toString();
                    (new JoinGameClass()).execute(input);
                }
            }
        }).show();
    }


    @Override
    public void enterRoom(GameInfoContent.GameInfo item) {
        Object[] infoList = new Object[2];
        infoList[0] = item;
        selectCharacter(infoList, false);
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

        final String[] mpIndex = {""};
        final String[] initMoney = {""};
        final String[] maxTurn = {""};
        final String[] targetAmount = {""};

        Spinner MapIndex = (Spinner) view.findViewById(R.id.dialog_mapIndex);
        ArrayAdapter<CharSequence> MPadapter = ArrayAdapter.createFromResource(this,
                R.array.Map_IDs, android.R.layout.simple_spinner_item);
        MPadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MapIndex.setAdapter(MPadapter);


        MapIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mpIndex[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mpIndex[0] = (String) parent.getItemAtPosition(0);
            }
        });

        final Spinner InitMoney = (Spinner) view.findViewById(R.id.dialog_starting_money);
        ArrayAdapter<CharSequence> IMadapter = ArrayAdapter.createFromResource(this,
                R.array.Init_Money, android.R.layout.simple_spinner_item);
        IMadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        InitMoney.setAdapter(IMadapter);

        InitMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initMoney[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                initMoney[0] = (String) parent.getItemAtPosition(0);
            }
        });

        final Spinner maxTurns = (Spinner) view.findViewById(R.id.dialog_maxturns);
        ArrayAdapter<CharSequence> MTadapter = ArrayAdapter.createFromResource(this,
                R.array.Max_Turn, android.R.layout.simple_spinner_item);
        MTadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxTurns.setAdapter(MTadapter);

        maxTurns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxTurn[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                maxTurn[0] = (String) parent.getItemAtPosition(0);
            }
        });

        final Spinner TargetAmount = (Spinner) view.findViewById(R.id.dialog_TargetAmount);
        ArrayAdapter<CharSequence> TAadapter = ArrayAdapter.createFromResource(this,
                R.array.Target_Amount, android.R.layout.simple_spinner_item);
        TAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TargetAmount.setAdapter(TAadapter);

        TargetAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                targetAmount[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                targetAmount[0] = (String) parent.getItemAtPosition(0);
            }
        });


//        final EditText MapIndex = (EditText) view.findViewById(R.id.dialog_mapIndex);
//        final EditText InitMoney = (EditText) view.findViewById(R.id.dialog_starting_money);
//        final EditText maxTurns = (EditText) view.findViewById(R.id.dialog_maxturns);
//        final EditText TargetAmount = (EditText) view.findViewById(R.id.dialog_TargetAmount);
        final EditText GameName = (EditText) view.findViewById(R.id.dialog_GameName);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] input = new String[7];
                input[0] = mpIndex[0];
                input[1] = UserName;
                input[2] = initMoney[0];
                input[3] = maxTurn[0];
                input[4] = targetAmount[0];
                input[6] = GameName.getText().toString();
                selectCharacter(input, true);
            }
        }).show();
    }


    @Override
    public void StartGame(GameInfoContent.GameInfo gameInfo) {
        GameInfoContent.GameInfo[] infoList = new GameInfoContent.GameInfo[1];
        infoList[0] = gameInfo;
        (new OrderSetterClass()).execute(infoList);
    }

    @Override
    public void enterGame(GameInfoContent.GameInfo gameInfo) {
        GameViewFragment mFrag = GameViewFragment.newInstance(gameInfo);
        mFragmentStack.pop();
        mFragmentStack.push(mFrag);
        replacefragment(mFrag);
    }

    class StartGameClass extends AsyncTask<GameInfoContent.GameInfo, Void, Boolean> {
        private GameInfoContent.GameInfo gameInfo;

        @Override
        protected Boolean doInBackground(GameInfoContent.GameInfo... gameInfos) {
            try {
                gameInfo = gameInfos[0];
                Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
                CallableStatement cs;
                cs = con.prepareCall("{call Start_Game(?)}");
                cs.setInt(1, gameInfo.gameid);
                cs.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    class OrderSetterClass extends AsyncTask<GameInfoContent.GameInfo, Void, Void> {
        private GameInfoContent.GameInfo[] gameInfo;

        @Override
        protected Void doInBackground(GameInfoContent.GameInfo... gameInfos) {
            this.gameInfo = gameInfos;
            Iterator<PlayerInfoContent.PlayerInfo> playerInfoIterator = PlayerInfoFragment.getIterator();
            Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
            String query = "Update Character SET [Order] = ? Where UserID = ? ";
            try {
                int count = 0;
                while (playerInfoIterator.hasNext()) {
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, count);
                    stmt.setInt(2, playerInfoIterator.next().id);
                    stmt.executeUpdate();
                    count++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            (new StartGameClass()).execute(gameInfo);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mFragmentStack.pop();
            replacefragment(mFragmentStack.peek());
        }
    }


    class AddGameClass extends AsyncTask<String, Void, Boolean> {
        GameInfoContent.GameInfo info;

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
                cs.setString(7, input[5]);
                cs.setString(8, input[6]);
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
                stmt.setString(1, input[6]);
                rs = stmt.executeQuery();
                rs.next();
                gameid = rs.getInt(1);
                info = new GameInfoContent.GameInfo(gameid, Integer.valueOf(input[0]), Integer.valueOf(input[2]), Integer.valueOf(input[3]), Integer.valueOf(input[4]), input[6], input[5]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            enterGameRoom(info);
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

    class JoinGameClass extends AsyncTask<Object, Void, Boolean> {
        GameInfoContent.GameInfo info;

        @Override
        protected Boolean doInBackground(Object... input) {
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
                info = (GameInfoContent.GameInfo) input[0];
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, info.gameName);
                cs.setInt(3, userID);
                cs.setString(4, (String) input[1]);
                cs.executeUpdate();
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
            if (mFragmentStack.peek() instanceof PlayerInfoFragment) {
                (new LeaveGameClass()).execute();
            } else if (mFragmentStack.peek() instanceof GameViewFragment) {
                ((GameViewFragment) mFragmentStack.peek()).exitGame();
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
