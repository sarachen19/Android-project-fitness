package com.sarachen.androidappkeep.ui.login;

import com.sarachen.androidappkeep.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private LoggedInUser user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(LoggedInUser user) {
        this.user = user;
    }

    public LoggedInUser getUser() {
        return user;
    }

    String getDisplayName() {
        return user.getDisplayName();
    }
}