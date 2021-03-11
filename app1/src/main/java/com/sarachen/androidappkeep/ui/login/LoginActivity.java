package com.sarachen.androidappkeep.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.database.Database;
import com.sarachen.androidappkeep.database.DatabaseHelper;
import com.sarachen.androidappkeep.model.Manager;
import com.sarachen.androidappkeep.model.LoggedInUser;
import com.sarachen.androidappkeep.ui.MainActivity0;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.login_username);
        final EditText passwordEditText = findViewById(R.id.login_password);
        final Button loginButton = findViewById(R.id.login_btn);
        final Button registerButton = findViewById(R.id.login_register_btn);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginOrRegisterResult>() {
            @Override
            public void onChanged(@Nullable LoginOrRegisterResult loginOrRegisterResult) {
                if (loginOrRegisterResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginOrRegisterResult.getError() != null) {
                    showLoginFailed(loginOrRegisterResult.getError());
                }
                if (loginOrRegisterResult.getSuccess() != null) {
                    updateUiWithUser(loginOrRegisterResult.getSuccess());
                }

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            DatabaseReference users = Database.DB.child("users");
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LoggedInUser user = DatabaseHelper.getUser(snapshot, username, password);
                    loginViewModel.loginOrRegister(user);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        registerButton.setOnClickListener(v -> {
            RegisterFragment registerFragment = new RegisterFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.login_main_frame, registerFragment, "registerFragment")
                    .addToBackStack(null)
                    .commit();
        });
    }


    private void updateUiWithUser(LoggedInUserView model) {
        // TODO : initiate successful logged in experience
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        Manager.user = model.getUser();

        SharedPreferences preferences = getSharedPreferences("com.username.app", Context.MODE_PRIVATE);
        preferences.edit().putString("sessionUsername", Manager.user.getUserId()).commit();

        new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {
            setResult(Activity.RESULT_OK);
            //Complete and destroy login activity once successful
            finish();
            //start new activity
            Intent intent = new Intent(this, MainActivity0.class);
            startActivity(intent);
        }, 3000);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}