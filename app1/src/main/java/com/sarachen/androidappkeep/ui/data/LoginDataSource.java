package com.sarachen.androidappkeep.ui.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarachen.androidappkeep.database.Database;
import com.sarachen.androidappkeep.database.DatabaseHelper;
import com.sarachen.androidappkeep.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(LoggedInUser user) {
            // TODO: handle loggedInUser authentication
             if (user != null) {
                 Log.d("========success=====", "getUser: ====================" + user.getDisplayName());
                 return new Result.Success<>(user);
             }
             else {
                 Log.d("=======failed======", "getUser failed" );
                 return new Result.Error(new IOException("User does not exist"));
             }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}