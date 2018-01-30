package rose_hulman.edu.monopolygame.Lobby;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import rose_hulman.edu.monopolygame.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPlayerInfoFragmentListener}
 * interface.
 */
public class PlayerInfoFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_GameInfo = "game-info";
    private OnPlayerInfoFragmentListener mListener;
    private GameInfoContent.GameInfo mGameInfo;
    private MyPlayerInfoRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerInfoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlayerInfoFragment newInstance(GameInfoContent.GameInfo gameInfo) {
        PlayerInfoFragment fragment = new PlayerInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GameInfo, gameInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mGameInfo = getArguments().getParcelable(ARG_GameInfo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playerinfo_list, container, false);
        setHasOptionsMenu(true);
        view.setBackgroundResource(R.color.background);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyPlayerInfoRecyclerViewAdapter(PlayerInfoContent.ITEMS, mListener);
            PlayerInfoContent.setGameID(mGameInfo.gameid);
            PlayerInfoContent.setAdapter(mAdapter);
            recyclerView.setAdapter(mAdapter);
            PlayerInfoContent.reloadPlayer();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.room_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start_name:
                mListener.StartGame(mGameInfo);
                break;
            case R.id.action_game_refresh:
                PlayerInfoContent.reloadPlayer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayerInfoFragmentListener) {
            mListener = (OnPlayerInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayerInfoFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPlayerInfoFragmentListener {
        void StartGame(GameInfoContent.GameInfo item);
    }
}
