package com.sarachen.androidappkeep.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginOrRegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    LoginOrRegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginOrRegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}