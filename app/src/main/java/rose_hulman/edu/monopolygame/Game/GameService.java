package rose_hulman.edu.monopolygame.Game;

import android.os.AsyncTask;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import rose_hulman.edu.monopolygame.Basics.MainActivity;
import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;

/**
 * Created by Hao Yang on 1/29/2018.
 */

public class GameService implements GameViewFragment.GameMapFragmentListener {
    private static DatabaseConnectionService mDBService = DatabaseConnectionService.getInstance("", "");
    private GameViewFragment mGameViewFragment;
    private String log = "";
    private boolean update = false;
    private boolean pressedRollDice = false;
    //    private boolean isCurrentPlayer = false;
    private boolean isWaitForRollDice = false;
    private boolean isWaitForDecision = false;
    private int decision = 0;
    private boolean pressedExit = false;
    private String prompt = "";
    private int userID;

    private HashMap<Integer, String> placeIDMap = new HashMap<>();

    public GameService(GameViewFragment gameViewFragment) {
        (new ConstructorClass()).execute();
        this.mGameViewFragment = gameViewFragment;
    }

    class ConstructorClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int mapIndex = mGameViewFragment.getMapID();
            String getMapQuery = "Select LayoutID, placeName from Layout Where MapID = " + mapIndex;
            try {
                Statement stmt = mDBService.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(getMapQuery);
                while (rs.next()) {
                    placeIDMap.put(rs.getInt(1), rs.getString(2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            (new GameHandlerClass()).execute(new String[0]);
        }
    }


    class GameHandlerClass extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... commands) {
            Connection con = mDBService.getConnection();
            String[] input = commands;
            String getLogQuery = "Select [log] from Game Where GameID = " + mGameViewFragment.getGameID();
            try {
                String getUserNameQuery = "Select ID from [User] where UserName = ?";
                PreparedStatement getUsername = con.prepareStatement(getUserNameQuery);
                getUsername.setString(1, MainActivity.getUserName());
                ResultSet rs = getUsername.executeQuery();
                rs.next();
                userID = rs.getInt(1);
                //TODO: Set up logic for purchase, walk, etc
                switch (input.length) {
                    case 0:
                        Statement stmt = con.createStatement();
                        ResultSet logResultSet = stmt.executeQuery(getLogQuery);
                        logResultSet.next();
                        String received = logResultSet.getString(1);
                        String temp = parseLog(received);
                        if (!temp.equals(log)) {
                            update = true;
                            log = temp;
                        } else {
                            update = false;
                        }
                        String setReceivedQuery = "Update Character SET Received = 1 Where UserID = ? ";
                        PreparedStatement setReceived = con.prepareStatement(setReceivedQuery);
                        setReceived.setInt(1, userID);
                        setReceived.executeUpdate();
                        break;
                    case 1:
                        int forwardStep = (int) (Math.random() * 6) + 1;
                        CallableStatement cs;
                        cs = con.prepareCall("{call Go_Forward(?,?,?)}");
                        cs.setInt(1, forwardStep);
                        cs.setInt(2, mGameViewFragment.getGameID());
                        cs.setInt(3, userID);
                        cs.executeUpdate();
                        break;
                    case 2:
                        //TODO: Perform action based on Prompt
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pressedExit) {
                return;
            }
            if (update) {
                mGameViewFragment.updateText(log);
            }
            mGameViewFragment.setRollDiceButtonStatus(isWaitForRollDice);
            mGameViewFragment.setRejectButtonStatus(isWaitForDecision);
            mGameViewFragment.setConfirmButtonStatus(isWaitForDecision);

            if (isWaitForRollDice) {
                if (pressedRollDice) {
                    pressedRollDice = false;
                    isWaitForRollDice = false;
                    mGameViewFragment.setRollDiceButtonStatus(isWaitForRollDice);
                    (new GameHandlerClass()).execute("rolldice");
                } else {
                    (new GameHandlerClass()).execute(new String[0]);
                }
            } else if (isWaitForDecision) {
                switch (decision) {
                    case 0:
                        (new GameHandlerClass()).execute(new String[0]);
                        break;
                    case 1:
                        decision = 0;
                        isWaitForDecision = false;
                        mGameViewFragment.setRejectButtonStatus(isWaitForDecision);
                        mGameViewFragment.setConfirmButtonStatus(isWaitForDecision);
                        (new GameHandlerClass()).execute("confirm", prompt);
                        break;
                    case 2:
                        decision = 0;
                        isWaitForDecision = false;
                        mGameViewFragment.setRejectButtonStatus(isWaitForDecision);
                        mGameViewFragment.setConfirmButtonStatus(isWaitForDecision);
                        (new GameHandlerClass()).execute("reject", prompt);
                        break;
                }
            } else {
                (new GameHandlerClass()).execute(new String[0]);
            }
        }
    }

    private String parseLog(String input) {
        String[] toParse = input.split(" ");
        String display = "";
        for (int i = 0; i < toParse.length; i++) {
            switch (toParse[i]) {
                case "-empty":
                    break;
                case "-player":
                    break;
                case "-T":
                    i++;
                    String turnNumber = toParse[i];
                    i++;
                    String activePlayerName = toParse[i];
                    i++;
                    String UID = toParse[i];
                    if (Integer.valueOf(UID) == userID) {
                        this.isWaitForRollDice = true;
                    }
                    display = "Turn " + turnNumber + ": Character " + activePlayerName + "'s move.\n";
                    break;
                case "-Dice":
                    i++;
                    activePlayerName = toParse[i];
                    i++;
                    String steps = toParse[i];
                    i++;
                    String startposid = toParse[i];
                    i++;
                    String endposid = toParse[i];
                    i++;
                    String placeOwner = toParse[i];
                    i++;
                    String startposname = placeIDMap.get(Integer.valueOf(startposid));
                    String endposname = placeIDMap.get(Integer.valueOf(endposid));
                    display = activePlayerName + " started from " + startposname + ", walked " + steps + " steps and arrived at " + endposname + "\n";
                    break;
            }
        }
        return display;
    }


    @Override
    public void rolldice() {
        this.pressedRollDice = true;
    }

    @Override
    public void confirm() {
        this.decision = 1;
    }

    @Override
    public void reject() {
        this.decision = 2;
    }

    @Override
    public void leaveGame() {
        this.pressedExit = true;
    }

}
