package com.sarachen.androidappkeep.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.database.Database;
import com.sarachen.androidappkeep.database.DatabaseHelper;
import com.sarachen.androidappkeep.model.Manager;
import com.sarachen.androidappkeep.model.Course;
import com.sarachen.androidappkeep.model.LoggedInUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private static DatabaseReference db = Database.DB;
    private List<Course> coursesByUser = new ArrayList<>();
    private LoggedInUser user = Manager.user;
    private HomeFragment.CourseOnClickListener listener;
    private HomeCourseRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeFragment newInstance(int columnCount) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home_course_list, container, false);

        //update coursesByUser
        DatabaseReference coursesDf = db.child("courses");
        coursesDf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesByUser = DatabaseHelper.getCoursesByUserId(dataSnapshot, user.getCourses());

                // Set the adapter
                Context context = null;
                RecyclerView recyclerView = null;
                if (view instanceof RecyclerView) {
                    recyclerView = (RecyclerView) view;
                    context = view.getContext();
                }
                else {
                    recyclerView = (RecyclerView) view.findViewById(R.id.home_recycler_view_course_list);
                    context = recyclerView.getContext();
                }
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                adapter = new HomeCourseRecyclerViewAdapter(coursesByUser, listener, user.getCourses());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("chenxirui", error.getMessage());
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (HomeFragment.CourseOnClickListener) context;
    }
    /*
     * CourseOnClickListener interface
     */
    public interface CourseOnClickListener {
        void onCLickCourse(Course course, boolean isEnrolled, Set<Integer> courseIdsByUser);
    }
}