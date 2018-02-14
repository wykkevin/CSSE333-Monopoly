package rose_hulman.edu.monopolygame.Lobby;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

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
public class GameInfoContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<GameInfo> ITEMS = new ArrayList<GameInfo>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, GameInfo> ITEM_MAP = new HashMap<Integer, GameInfo>();

    private static final DatabaseConnectionService mDBService = DatabaseConnectionService.getInstance("", "");

    private static MyGameInfoRecyclerViewAdapter mAdapter = null;

    public static void reloadGame() {
        ITEM_MAP.clear();
        ITEMS.clear();
        (new LoadItemClass()).execute();
    }

    public static void setAdapter(MyGameInfoRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    static class LoadItemClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Connection con = mDBService.getConnection();
            String query = "Select * from Game";
            Statement stmt = null;
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    int gameid = rs.getInt(1);
                    int mapIndex = rs.getInt(2);
                    int initMoney = rs.getInt(3);
                    int MaxTurns = rs.getInt(4);
                    int TargetAmount = rs.getInt(5);
                    String gameName = rs.getString(6);
                    GameInfo info = new GameInfo(gameid, mapIndex, initMoney, MaxTurns, TargetAmount, gameName, "");
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

    private static void addItem(GameInfo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.gameid, item);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class GameInfo implements Parcelable {
        public final int gameid;
        public final int mapIndex;
        public final int initMoney;
        public final int MaxTurns;
        public final int TargetAmount;
        public final String gameName;
        public String characterName;

        public GameInfo(int gameid, int mapIndex, int initMoney, int maxTurns, int targetAmount, String gameName, String characterName) {
            this.gameid = gameid;
            this.mapIndex = mapIndex;
            this.initMoney = initMoney;
            MaxTurns = maxTurns;
            TargetAmount = targetAmount;
            this.gameName = gameName;
            this.characterName = characterName;
        }

        protected GameInfo(Parcel in) {
            gameid = in.readInt();
            mapIndex = in.readInt();
            initMoney = in.readInt();
            MaxTurns = in.readInt();
            TargetAmount = in.readInt();
            gameName = in.readString();
            characterName = in.readString();
        }

        public static final Creator<GameInfo> CREATOR = new Creator<GameInfo>() {
            @Override
            public GameInfo createFromParcel(Parcel in) {
                return new GameInfo(in);
            }

            @Override
            public GameInfo[] newArray(int size) {
                return new GameInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(gameid);
            dest.writeInt(mapIndex);
            dest.writeInt(initMoney);
            dest.writeInt(MaxTurns);
            dest.writeInt(TargetAmount);
            dest.writeString(gameName);
            dest.writeString(characterName);
        }

        public void setCharacterName(String name) {
            this.characterName = characterName;
        }

    }
}
