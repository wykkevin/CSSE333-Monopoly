package rose_hulman.edu.monopolygame.GameRoom;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import rose_hulman.edu.monopolygame.Basics.MainActivity;
import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;

/**
 * Created by Hao Yang on 1/30/2018.
 */

public class StartGameService {
    private PlayerInfoFragment mPlayerInfoFragment;
    private static DatabaseConnectionService mDBService = DatabaseConnectionService.getInstance("", "");

    public StartGameService(PlayerInfoFragment mPlayerInfoFragment) {
        this.mPlayerInfoFragment = mPlayerInfoFragment;
        (new GameStarterClass()).execute();
    }

    class GameStarterClass extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... commands) {
            String userName = MainActivity.getUserName();
            Connection con = mDBService.getConnection();
            String query = "Select Status from [User] Where UserName = ? ";
            try {
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt("Status");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer) {
                case 0:
                    (new GameStarterClass()).execute();
                    break;
                case 1:
                    mPlayerInfoFragment.enterGame();
                    return;
            }
        }
    }
}
