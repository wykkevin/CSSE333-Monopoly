package rose_hulman.edu.monopolygame.Lobby;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlayerInfoContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<PlayerInfo> ITEMS = new ArrayList<PlayerInfo>();
    private static int gameID = -1;
    private static MyPlayerInfoRecyclerViewAdapter mAdapter;
    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, PlayerInfo> ITEM_MAP = new HashMap<Integer, PlayerInfo>();
    private static final DatabaseConnectionService mDBService = DatabaseConnectionService.getInstance("", "");

    public static void reloadPlayer() {
        if (gameID == -1) {
            return;
        }
        ITEM_MAP.clear();
        ITEMS.clear();
        (new LoadItemClass()).execute();
    }

    public static void setGameID(int gid){
        gameID = gid;
    }

    public static void setAdapter(MyPlayerInfoRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    static class LoadItemClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (gameID == -1) {
                return null;
            }
            Connection con = mDBService.getConnection();
            try {
                Statement stmt = con.createStatement();
                String query = "Select * from [User] where GameID = " + gameID;
                Log.d("PICPIC", query);
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Log.d("PICPIC", "SELECTED");
                    int userid = rs.getInt(1);
                    String userName = rs.getString(4);
                    PlayerInfo info = new PlayerInfo(userid, userName);
                    addItem(info);
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

    private static void addItem(PlayerInfo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class PlayerInfo {
        public final int id;
        public final String UserName;

        public PlayerInfo(int id, String userName) {
            this.id = id;
            UserName = userName;
        }

    }
}
