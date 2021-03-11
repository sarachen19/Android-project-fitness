package com.sarachen.androidappkeep.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.sarachen.androidappkeep.ui.data.LoginRepository;
import com.sarachen.androidappkeep.ui.data.Result;
import com.sarachen.androidappkeep.model.LoggedInUser;
import com.sarachen.androidappkeep.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginOrRegisterResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginOrRegisterResult> getLoginResult() {
        return loginResult;
    }

    public void loginOrRegister(LoggedInUser user) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.loginOrRegister(user);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginOrRegisterResult(new LoggedInUserView(data)));
        } else {
            loginResult.setValue(new LoginOrRegisterResult(R.string.login_failed));
        }
    }


    public void loginDataChanged(String username, String password) {
        if (!isNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }


}