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
    private int endturn = 0;


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
                CallableStatement cs;
                cs = mDBService.getConnection().prepareCall("{call Import_place_from_layout(?,?)}");
                cs.setInt(1, mGameViewFragment.getMapID());
                cs.setInt(2, mGameViewFragment.getGameID());
                cs.executeUpdate();
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
                        update = false;
                        int forwardStep = (int) (Math.random() * 6) + 1;
                        CallableStatement cs;
                        cs = con.prepareCall("{call Go_Forward(?,?,?)}");
                        cs.setInt(1, forwardStep);
                        cs.setInt(2, mGameViewFragment.getGameID());
                        cs.setInt(3, userID);
                        cs.executeUpdate();
                        break;
                    case 2:
                        update = false;
                        if (commands[0].equals("confirm")) {
                            cs = parsePrompt(commands[1]);
                            cs.executeUpdate();
                            endturn = 1;
                        } else {
                            endturn = 2;
                        }
                        break;
                    case 3:
                        if (input[0].equals("1")) {
                            stmt = con.createStatement();
                            logResultSet = stmt.executeQuery(getLogQuery);
                            logResultSet.next();
                            received = logResultSet.getString(1);
                            temp = parseLog(received);
                            if (!temp.equals(log)) {
                                update = true;
                                log = temp;
                            } else {
                                update = false;
                            }
                        }
                        cs = con.prepareCall("{call Change_Order(?)}");
                        cs.setInt(1, mGameViewFragment.getGameID());
                        cs.executeUpdate();
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
                update = false;
            }

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
                        String[] input = new String[2];
                        input[0] = "confirm";
                        input[1] = prompt;
                        (new GameHandlerClass()).execute(input);
                        break;
                    case 2:
                        decision = 0;
                        isWaitForDecision = false;
                        mGameViewFragment.setRejectButtonStatus(isWaitForDecision);
                        mGameViewFragment.setConfirmButtonStatus(isWaitForDecision);
                        input = new String[2];
                        input[0] = "reject";
                        input[1] = prompt;
                        (new GameHandlerClass()).execute(input);
                        break;
                }
            } else if (endturn != 0) {
                String[] input = new String[3];
                input[0] = String.valueOf(endturn);
                endturn = 0;
                (new GameHandlerClass()).execute(input);
            } else {
                (new GameHandlerClass()).execute(new String[0]);
            }
            mGameViewFragment.setRollDiceButtonStatus(isWaitForRollDice);
            mGameViewFragment.setRejectButtonStatus(isWaitForDecision);
            mGameViewFragment.setConfirmButtonStatus(isWaitForDecision);
        }
    }

    private CallableStatement parsePrompt(String prompt) {
        String[] toParse = prompt.split(" ");
        String cmd = toParse[0];
        String pid = toParse[1];
        CallableStatement cs = null;
        try {
            switch (cmd) {
                case "-purchase":
                    cs = mDBService.getConnection().prepareCall("{call Purchase_Land(?,?,?)}");
                    break;
                case "-upgrade":
                    cs = mDBService.getConnection().prepareCall("{call Upgrade_Building(?,?,?)}");
                    break;
                case "-pay":
                    cs = mDBService.getConnection().prepareCall("{call Pay(?,?,?)}");
                    break;
            }
            cs.setInt(1, userID);
            cs.setInt(2, mGameViewFragment.getGameID());
            cs.setInt(3, Integer.parseInt(pid));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cs;
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
                    String posPrice = toParse[i];
                    i++;
                    String upgradeCost = toParse[i];
                    i++;
                    String curlevel = toParse[i];
                    i++;
                    String maxlevel = toParse[i];
                    i++;
                    String currentMoney = toParse[i];
                    i++;
                    String type = toParse[i];
                    i++;
                    String userid = toParse[i];
                    String startposname = placeIDMap.get(Integer.valueOf(startposid));
                    String endposname = placeIDMap.get(Integer.valueOf(endposid));
                    display = activePlayerName + " started from " + startposname + ", walked " + steps + " steps and arrived at " + endposname + ".\n" + "This player currently has " + currentMoney + " kang. \n";
                    if (Integer.valueOf(userid) == userID) {
                        prompt = "";
                        if (placeOwner.equals(activePlayerName)) {
                            this.isWaitForDecision = true;
                            if (!curlevel.equals(maxlevel)) {
                                display += "Do you want to upgrade " + endposname + " from level " + curlevel + " to level " + (Integer.valueOf(curlevel) + 1) + "buy paying " + upgradeCost + " kang?\n";
                                prompt += "-upgrade ";
                            }
                        } else if (placeOwner.equals("NULL")) {
                            this.isWaitForDecision = true;
                            display += "Do you want to purchase " + endposname + " by paying " + posPrice + " kang?\n";
                            prompt += "-purchase ";
                        } else {
                            prompt += "-pay ";
                            this.isWaitForDecision = true;
                            decision = 1;
                        }
                        prompt += endposid;
                    } else {
                        isWaitForDecision = false;
                    }
                    break;
                case "-W":
                    i++;
                    String winnerName = toParse[i];
                    display = winnerName + " wins the game! \n";
                    break;
                case "-L":
                    i++;
                    String loserName = toParse[i];
                    display = loserName + " loses the game! \n";
                    break;
                case "-Pay":
                    i++;
                    String payername = toParse[i];
                    i++;
                    String ownername = toParse[i];
                    i++;
                    String price = toParse[i];
                    i++;
                    String payafter = toParse[i];
                    i++;
                    String ownafter = toParse[i];
                    i++;
                    display = payername + " paid " + ownername + " " + price + " kang.\n";
                    display += payername + " now has " + payafter + " kang.\n";
                    display += ownername + " now has " + ownafter + " kang.\n";
                    break;
                case "-Upgrade":
                    i++;
                    String charname = toParse[i];
                    i++;
                    String pid = toParse[i];
                    i++;
                    price = toParse[i];
                    i++;
                    String moneyafter = toParse[i];
                    i++;
                    String levelafter = toParse[i];
                    display = charname + " paid " + price + " kang and upgraded " + placeIDMap.get(Integer.valueOf(pid)) + " to " + levelafter + ".\n";
                    display += charname + " now has " + moneyafter + " kang.\n";
                    break;
                case "-Purchase":
                    i++;
                    charname = toParse[i];
                    i++;
                    pid = toParse[i];
                    i++;
                    price = toParse[i];
                    i++;
                    moneyafter = toParse[i];
                    display = charname + " paid " + price + " kang and purchased " + placeIDMap.get(Integer.valueOf(pid)) + ".\n";
                    display += charname + " now has " + moneyafter + " kang.\n";
                    break;
                case "-Card":
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
