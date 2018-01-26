package rose_hulman.edu.monopolygame.DatabaseConnection;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Hao Yang on 1/23/2018.
 */

public class UserService {
    private static final Random RANDOM = new SecureRandom();
    private static final Base64.Encoder enc = Base64.getEncoder();
    private static final Base64.Decoder dec = Base64.getDecoder();
    private DatabaseConnectionService dbService = null;

    public UserService(DatabaseConnectionService dbService) {
        this.dbService = dbService;
    }

    public boolean login(String username, String password) {
        Log.d("LOGIN", "Username:"+username);
        Log.d("LOGIN", "Password:"+password);
        if (username.equals("") || password.equals("") || username == null || password == null) {
            return false;
        }
        try {
            Connection con = this.dbService.getConnection();
            Log.d("LOGIN", "CONNECTION is null?" + (con == null));
            String query = "select PasswordSalt, PasswordHash from [User] where Username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            Log.d("LOGIN", "Query set");
            stmt.setString(1, username);
            Log.d("LOGIN", stmt.toString());
            ResultSet rs = stmt.executeQuery();
            Log.d("LOGIN", "Query executed, result is null?" + (rs == null));
            byte[] pwSalt = null;
            rs.next();
            try {
                pwSalt = rs.getBytes("PasswordSalt");
            }catch (Exception e){
                Log.d("LOGIN",e.toString());
                Log.d("LOGIN", "FAIL TO GET salt");
            }
            String pwHash = rs.getString("PasswordHash");
            Log.d("LOGIN", pwHash);
            if (pwHash.equals(hashPassword(pwSalt, password))) {
                return true;
            } else {
                Log.d("LOGIN", "WRONG PW");
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (username.equals("") || password.equals("") || username == null || password == null) {
            return false;
        }
        byte[] salt = getNewSalt();
        String pwHash = hashPassword(salt, password);
        try {
            Connection con = this.dbService.getConnection();
            CallableStatement cs;
            cs = con.prepareCall("{?=call Register(?,?,?)}");
            //TODO: Change stored procedure
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, username);
            cs.setBytes(3, salt);
            cs.setString(4, pwHash);
            //TODO: Update set
            cs.executeUpdate();
            int returnValue = cs.getInt(1);
            System.out.println(returnValue);
            if (returnValue > 0) {
                //TODO: Display Error Message
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //TODO: Display Error Message
            return false;
        }
        return true;
    }

    public byte[] getNewSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public String getStringFromBytes(byte[] data) {
        return enc.encodeToString(data);
    }

    public String hashPassword(byte[] salt, String password) {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f;
        byte[] hash = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            //TODO: Display Error Message
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            //TODO: Display Error Message
            e.printStackTrace();
        }
        return getStringFromBytes(hash);
    }
}
