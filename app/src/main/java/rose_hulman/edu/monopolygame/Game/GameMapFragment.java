package rose_hulman.edu.monopolygame.Game;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.Lobby.GameInfoContent;
import rose_hulman.edu.monopolygame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameMapFragmentListener} interface
 * to handle interaction events.
 * Use the {@link GameMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameMapFragment extends Fragment {
    private DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("", "");
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GAMEINFO = "Game_INFO_ARG";

    private GameInfoContent.GameInfo mGameInfo;

    private GameMapFragmentListener mListener;

    public GameMapFragment() {
        // Required empty public constructor
    }

    public static GameMapFragment newInstance(GameInfoContent.GameInfo gameInfo) {
        GameMapFragment fragment = new GameMapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GAMEINFO, gameInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameInfo = getArguments().getParcelable(ARG_GAMEINFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game_map, container, false);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameMapFragmentListener) {
            mListener = (GameMapFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameRoomFragmentListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface GameMapFragmentListener {
        void onFragmentInteraction(Uri uri);
    }
}
