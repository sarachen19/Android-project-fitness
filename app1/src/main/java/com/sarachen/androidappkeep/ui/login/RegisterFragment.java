package com.sarachen.androidappkeep.ui.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sarachen.androidappkeep.model.LoggedInUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_register, container, false);

        final EditText usernameEditText = view.findViewById(R.id.register_username);
        final EditText passwordEditText = view.findViewById(R.id.register_password);
        final EditText emailEditText = view.findViewById(R.id.register_email);
        final Button registerButton = view.findViewById(R.id.register_btn);
        final  Button registerBackButton = view.findViewById(R.id.register_back_btn);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginOrRegisterResult>() {
            @Override
            public void onChanged(@Nullable LoginOrRegisterResult loginOrRegisterResult){
                if (loginOrRegisterResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginOrRegisterResult.getError() != null) {
                    showRegisterFailed(loginOrRegisterResult.getError());
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

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            DatabaseReference users = Database.DB.child("users");
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LoggedInUser user = DatabaseHelper.register(snapshot, username, password, email);
                    loginViewModel.loginOrRegister(user);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        registerBackButton.setOnClickListener((v) -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void updateUiWithUser(LoggedInUserView success) {
        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Register Succeed", Toast.LENGTH_LONG).show();
        new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {
            getActivity().getSupportFragmentManager().popBackStack();
        }, 3000);

    }

    private void showRegisterFailed(Integer errorString) {
        Toast.makeText(getContext(), "Username already exist", Toast.LENGTH_LONG).show();
    }
}