package rose_hulman.edu.monopolygame.Lobby;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rose_hulman.edu.monopolygame.Lobby.GameInfoContent.GameInfo;
import rose_hulman.edu.monopolygame.Lobby.GameInfoFragment.GameInfoFragmentListener;
import rose_hulman.edu.monopolygame.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GameInfo} and makes a call to the
 * specified {@link GameInfoFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGameInfoRecyclerViewAdapter extends RecyclerView.Adapter<MyGameInfoRecyclerViewAdapter.ViewHolder> {

    private final List<GameInfo> mValues;
    private final GameInfoFragmentListener mListener;

    public MyGameInfoRecyclerViewAdapter(List<GameInfo> items, GameInfoFragment.GameInfoFragmentListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gameinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onGameInfoFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public GameInfo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
