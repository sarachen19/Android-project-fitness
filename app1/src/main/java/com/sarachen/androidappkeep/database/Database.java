package com.sarachen.androidappkeep.database;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Database {
    public static final DatabaseReference DB;

    static {
        DB = FirebaseDatabase.getInstance().getReference();
    }
}
