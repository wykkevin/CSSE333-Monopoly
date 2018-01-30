package rose_hulman.edu.monopolygame.Game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rose_hulman.edu.monopolygame.Lobby.GameInfoContent;
import rose_hulman.edu.monopolygame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameMapFragmentListener} interface
 * to handle interaction events.
 * Use the {@link GameViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GAMEINFO = "Game_INFO_ARG";

    private GameInfoContent.GameInfo mGameInfo;

    private GameMapFragmentListener mListener;

    private Button rolldiceButton;
    private Button confirmButton;
    private Button rejectButton;
    private TextView mTextView;

    public GameViewFragment() {
        // Required empty public constructor
    }

    public static GameViewFragment newInstance(GameInfoContent.GameInfo gameInfo) {
        GameViewFragment fragment = new GameViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GAMEINFO, gameInfo);
        fragment.setArguments(args);
        return fragment;
    }


    public void updateText(String log) {
        mTextView.append(log);
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
        View view = inflater.inflate(R.layout.fragment_game_map, container, false);
        mListener = new GameService(this);
        mTextView = (TextView) view.findViewById(R.id.game_logview);
        rolldiceButton = (Button) view.findViewById(R.id.action_roll_dice);
        rolldiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.rolldice();
            }
        });
        confirmButton = (Button) view.findViewById(R.id.action_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.confirm();
            }
        });
        rejectButton = (Button) view.findViewById(R.id.action_reject);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reject();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void exitGame() {
        mListener.leaveGame();
    }

    public void setRollDiceButtonStatus(boolean isActivate) {
        this.rolldiceButton.setActivated(isActivate);
    }

    public void setConfirmButtonStatus(boolean isActivate) {
        this.confirmButton.setActivated(isActivate);
    }

    public void setRejectButtonStatus(boolean isActivate) {
        this.rejectButton.setActivated(isActivate);
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
        void rolldice();

        void confirm();

        void reject();

        void leaveGame();
    }
}
