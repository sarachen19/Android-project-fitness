package com.sarachen.androidappkeep.ui.discover;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.database.Database;
import com.sarachen.androidappkeep.database.DatabaseHelper;
import com.sarachen.androidappkeep.model.Manager;
import com.sarachen.androidappkeep.model.Course;
import com.sarachen.androidappkeep.model.Exercise;
import com.sarachen.androidappkeep.model.LoggedInUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DiscoverFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 2;
    DatabaseReference db = Database.DB;
    DatabaseReference coursesDf;
    private LoggedInUser user = Manager.user;
    private List<Course> courses;// all courses
    private DiscoverFragment.CourseOnClickListener listener;
    private DiscoverCourseRecyclerViewAdapter adapter;
    private VideoView videoView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoverFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DiscoverFragment newInstance(int columnCount) {
        DiscoverFragment fragment = new DiscoverFragment();
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

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    /*
     * get all courses data from database, and pass it to adapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_discover_course_list, container, false);
        videoView = view.findViewById(R.id.course_fragment_videoView);
        playVideo();

        //begin fetching data from firebase database
        courses = new ArrayList<>();
        coursesDf = db.child("courses");
        coursesDf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("chenxirui","inside ondatachange method");
                for (DataSnapshot courseDs : dataSnapshot.getChildren()) {
                    List<Exercise> exercises = DatabaseHelper.getExercises(courseDs);
                    Course course = DatabaseHelper.getCourse(courseDs, exercises);
                    courses.add(course);
                }
                // Set the adapter
                Context context = null;
                RecyclerView recyclerView = null;
                if (view instanceof RecyclerView) {
                    recyclerView = (RecyclerView) view;
                    context = view.getContext();
                }
                else {
                    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_course_list);
                    context = recyclerView.getContext();
                }
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(gridLayoutManager);
                }
                adapter = new DiscoverCourseRecyclerViewAdapter(courses, listener, user.getCourses());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("chenxirui", databaseError.getMessage());
            }
        });
        return view;
    }

    public void playVideo() {
        videoView.setVideoURI(Uri.parse("android.resource://" + "com.sarachen.androidappkeep" + "/" + R.raw.intro_android_app));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.start();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DiscoverFragment.CourseOnClickListener) context;
    }

    /*
     * CourseOnClickListener interface
     */
    public interface CourseOnClickListener {
        void onCLickCourse(Course course, boolean isEnrolled, Set<Integer> courseIdsByUser);
    }

}