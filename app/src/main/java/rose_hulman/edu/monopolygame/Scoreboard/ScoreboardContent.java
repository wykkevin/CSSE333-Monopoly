package rose_hulman.edu.monopolygame.Scoreboard;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;

public class ScoreboardContent {

    /**
     * An array of sample (dummy) items.
     */
//    public static final List<GamerStatistics> ITEMS = new ArrayList<GamerStatistics>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    private static MyUsersRecyclerViewAdapter mAdapter;


    public ScoreboardContent(MyUsersRecyclerViewAdapter myUsersRecyclerViewAdapter) {
        mAdapter = myUsersRecyclerViewAdapter;
        mAdapter.clear();
        Log.d("USERSTAT", "SET ADAPTER");
        (new ScoreboardContent.LoadItemClass()).execute();
    }

    static class LoadItemClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("USERSTAT", "TRYING TO PULL STUFF");
            Connection con = DatabaseConnectionService.getInstance("", "").getConnection();
            String query = "Select * from PlayerRecord";
            Statement stmt = null;
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Log.d("USERSTAT", "STAT FOR: " + rs.getString(1));
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    int win = rs.getInt(3);
                    int lose = rs.getInt(4);
                    int total = rs.getInt(5);
                    double rate = rs.getInt(6);
                    GamerStatistics stat = new GamerStatistics(name, win, total, rate);
                    addItem(stat);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private static void addItem(GamerStatistics item) {
        mAdapter.addItem(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class GamerStatistics {
        public final String name;
        public final int win;
        public final int total;
        public final double winRate;

        public GamerStatistics(String UserName, int Wins, int Total, double winRate) {
            this.name = UserName;
            this.win = Wins;
            this.total = Total;
            this.winRate = winRate;
        }
    }
}
