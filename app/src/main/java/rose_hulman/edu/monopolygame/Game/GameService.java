package rose_hulman.edu.monopolygame.Game;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public GameService(GameViewFragment gameViewFragment) {
        (new GameHandlerClass()).execute(new String[0]);
        this.mGameViewFragment = gameViewFragment;
    }

    class GameHandlerClass extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... commands) {
            Connection con = mDBService.getConnection();
            String[] input = commands;
            String query = "Select * from Where";
            Statement stmt = null;
            try {

                //TODO: Set up logic for purchase, walk, etc
                switch (input.length) {
                    case 0:
                        //TODO: Pull Log from Database
                        break;
                    case 1:
                        //TODO: Roll Dice
                        break;
                    case 2:
                        //TODO: Perform action based on Prompt
                        break;
                }
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                String temp = parseLog(rs.getString(1));
                if (temp.equals(log)) {
                    update = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pressedExit) {
                mGameViewFragment.exitGame();
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
                    (new GameHandlerClass()).execute("rolldice");
                } else {
                    (new GameHandlerClass()).execute();
                }
            } else if (isWaitForDecision) {
                switch (decision) {
                    case 0:
                        (new GameHandlerClass()).execute();
                        break;
                    case 1:
                        decision = 0;
                        isWaitForDecision = false;
                        (new GameHandlerClass()).execute("confirm", prompt);
                        break;
                    case 2:
                        decision = 0;
                        isWaitForDecision = false;
                        (new GameHandlerClass()).execute("reject", prompt);
                        break;
                }
            } else {
                (new GameHandlerClass()).execute();
            }
        }
    }

    private String parseLog(String input) {
        String[] toParse = input.split(" ");
        String display = "";
        for (int i = 0; i < toParse.length; i++) {
            switch (toParse[i]) {
                case "-player":
                    break;
            }
        }
        //TODO: Set up log parsing logic
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
}
