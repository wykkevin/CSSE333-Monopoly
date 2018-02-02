package rose_hulman.edu.monopolygame.WelcomePages;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import rose_hulman.edu.monopolygame.DatabaseConnection.DatabaseConnectionService;
import rose_hulman.edu.monopolygame.Basics.Informations;
import rose_hulman.edu.monopolygame.DatabaseConnection.UserService;
import rose_hulman.edu.monopolygame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private LoginFragmentListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText mPasswordView;
    private EditText mUserView;
    private DatabaseConnectionService dbService;
    private UserService userService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void loginSucceeded(String userID) {
        mListener.switchToWelcomePage(userID);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUserView = (EditText) view.findViewById(R.id.username);
        dbService = DatabaseConnectionService.getInstance(Informations.serverName, Informations.databaseName);
        (new GetConnectionClass()).execute((String[]) null);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        Button mUserSignInButton = (Button) view.findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbService.getConnection() == null) {
                    Log.d("DBCONNECT", "Connection status: " + (dbService.getConnection() == null));
                    mListener.showErrorMessage("Login Failed!");
                    return;
                }
                String[] input = new String[2];
                input[0] = mUserView.getText().toString();
                input[1] = mPasswordView.getText().toString();
                (new LoginClass()).execute(input);
            }
        });
        Button mUserRegButton = (Button) view.findViewById(R.id.user_reg_button);
        mUserRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordValid(mPasswordView.getText().toString()) || dbService.getConnection() == null) {
                    Log.d("DBCONNECT", "Connection status: " + (dbService.getConnection() == null));
                    mListener.showErrorMessage("Registration Failed!");
                    return;
                }
                String[] input = new String[2];
                input[0] = mUserView.getText().toString();
                input[1] = mPasswordView.getText().toString();
                (new SignUpClass()).execute(input);
            }
        });
        return view;
    }


    class GetConnectionClass extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... input) {
            dbService.connect(Informations.serverUsername, Informations.serverPassword);
            if (dbService.getConnection() != null) {
                userService = new UserService(dbService);
                Log.d("DBCONNECT", "CONNECTED!");
            }
            return null;
        }
    }


    class LoginClass extends AsyncTask<String, Void, Boolean> {
        private String userID;

        @Override
        protected Boolean doInBackground(String... urlStrings) {
            userID = urlStrings[0];
            if (userService.login(urlStrings[0], urlStrings[1])) {
                return true;
            } else {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                loginSucceeded(userID);
            } else {
                mListener.showErrorMessage("Login Failed!");
            }
        }
    }


    class SignUpClass extends AsyncTask<String, Void, Boolean> {
        private String userID;

        @Override
        protected Boolean doInBackground(String... urlStrings) {
            userID = urlStrings[0];
            if (userService.register(urlStrings[0], urlStrings[1])) {
                return true;
            } else {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                loginSucceeded(userID);
            } else {
                mListener.showErrorMessage("Registration Failed!");
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            mListener = (LoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginFragmentListener");
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
    public interface LoginFragmentListener {
        void switchToWelcomePage(String userID);

        void showErrorMessage(String isRegister);
    }

}
