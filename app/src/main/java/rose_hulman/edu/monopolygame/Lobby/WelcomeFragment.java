package rose_hulman.edu.monopolygame.Lobby;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    private static final DatabaseConnectionService dbService = DatabaseConnectionService.getInstance("","");
    private WelcomeFragmentListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getView();
        Button mEnterLobbyButton = (Button) view.findViewById(R.id.enter_lobby_button);
        mEnterLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.enterLobby();
            }
        });
        Button mSignOutButton = (Button) view.findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.signOut();
            }
        });
        Button mScoreBoardButton = (Button) view.findViewById(R.id.check_scoreboard);
        mScoreBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openScoreboard();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WelcomeFragmentListener) {
            mListener = (WelcomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WelcomeFragmentListener");
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
    public interface WelcomeFragmentListener {
        void enterLobby();

        void openScoreboard();

        void signOut();
    }
}
