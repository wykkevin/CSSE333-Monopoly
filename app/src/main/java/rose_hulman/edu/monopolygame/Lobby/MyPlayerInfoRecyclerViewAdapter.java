package rose_hulman.edu.monopolygame.Lobby;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rose_hulman.edu.monopolygame.Lobby.PlayerInfoContent.PlayerInfo;
import rose_hulman.edu.monopolygame.Lobby.PlayerInfoFragment.OnPlayerInfoFragmentListener;
import rose_hulman.edu.monopolygame.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlayerInfo} and makes a call to the
 * specified {@link OnPlayerInfoFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlayerInfoRecyclerViewAdapter extends RecyclerView.Adapter<MyPlayerInfoRecyclerViewAdapter.ViewHolder> {

    private final List<PlayerInfo> mValues;
    private final OnPlayerInfoFragmentListener mListener;

    public MyPlayerInfoRecyclerViewAdapter(List<PlayerInfo> items, OnPlayerInfoFragmentListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playerinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("Player " + position + ": " + mValues.get(position).UserName);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public PlayerInfo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.username);
        }
    }
}
