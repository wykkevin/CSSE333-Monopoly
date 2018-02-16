package rose_hulman.edu.monopolygame.Scoreboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rose_hulman.edu.monopolygame.R;
import rose_hulman.edu.monopolygame.Scoreboard.ScoreboardContent.GamerStatistics;

public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> {

    private final List<GamerStatistics> mValues = new ArrayList<>();

    public MyUsersRecyclerViewAdapter() {
        new ScoreboardContent(this);
    }

    public void addItem(GamerStatistics item) {
        mValues.add(item);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_userstat, parent, false);
        view.setBackgroundResource(R.color.background);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText("Name: " + holder.mItem.name);
        holder.mWinView.setText("Win: " + holder.mItem.win);
        holder.mWinRateView.setText("Rate: " + String.valueOf(holder.mItem.winRate) + "%");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void clear() {
        mValues.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mWinView;
        public final TextView mWinRateView;
        //        public final TextView mTotalView;
        public GamerStatistics mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.userstat_name);
            mWinView = (TextView) view.findViewById(R.id.userstat_win);
            mWinRateView = (TextView) view.findViewById(R.id.userstat_rate);
//            mTotalView = (TextView) view.findViewById(R.id.userstat_total);
        }


    }
}
