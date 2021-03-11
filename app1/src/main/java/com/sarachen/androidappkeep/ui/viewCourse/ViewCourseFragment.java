package com.sarachen.androidappkeep.ui.viewCourse;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.database.Database;
import com.sarachen.androidappkeep.database.DatabaseHelper;
import com.sarachen.androidappkeep.model.Course;
import com.sarachen.androidappkeep.model.LoggedInUser;
import com.sarachen.androidappkeep.ui.MainActivity0;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ViewCourseFragment extends Fragment {
    private Bundle bundle;//course, user, isEnrolled
    private TextView titleView, detailView;
    private ImageView imageView, playCourseImage;
    private ViewCourseToPlayCourse mMyInterface;
    private Button playOrEnterBtn;
    public ViewCourseFragment() {
    }

    // TODO: Customize parameter initialization

    public static ViewCourseFragment newInstance(int columnCount) {

        ViewCourseFragment fragment = new ViewCourseFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // get course, user
        bundle = getArguments();
        Course course = Parcels.unwrap(bundle.getParcelable("course_parcel"));
        LoggedInUser user = Parcels.unwrap(bundle.getParcelable("user_parcel"));
        int userId = Integer.parseInt(user.getUserId());
        Set<Integer> courseIdsByUser = user.getCourses();

        View view = inflater.inflate(R.layout.fragment_course, container, false);
        titleView = (TextView)view.findViewById(R.id.course_exercise_courseTitle);
        detailView = view.findViewById(R.id.course_exercise_courseDetail);
        imageView = view.findViewById(R.id.course_exercise_image);
        playCourseImage = (ImageView)view.findViewById(R.id.course_play_img);
        playOrEnterBtn = (Button)view.findViewById(R.id.course_exercise_button);

        //set playOrEnterBtn text and onclick event
        setBtn(userId, course, courseIdsByUser);

        //add exercise fragment-------------------------------------------------------
        ExerciseFragment exerciseFragment = new ExerciseFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.exercises_frame, exerciseFragment, "exerciseFragment")
                .commit();
        //pass bundle(course and userid) to fragment
        exerciseFragment.setArguments(getArguments());

        //update view course detail
        showCourseData(course);

        addClickListener(view);
        return view;
    }
    public void addClickListener(View view) {
        //play course when click play image
        playCourseImage.setOnClickListener(v -> mMyInterface.playCourse(getArguments()));
        //navigate back
        view.findViewById(R.id.course_back_btn).setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack("viewCourseFg", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
    }

    private void setBtn(int userId, Course course, Set<Integer> courseIdsByUser) {
        if (bundle.getBoolean("isEnrolled")) {
            playOrEnterBtn.setText("Start");
            playOrEnterBtn.setOnClickListener((v) -> mMyInterface.playCourse(getArguments()));
        }
        else {
            playOrEnterBtn.setText("Add Course");
            //-------------------add course into user's course and update db----------------------
            playOrEnterBtn.setOnClickListener((v) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
                builder.setMessage("Add this course?")
                        .setTitle("Add")
                        .setPositiveButton("OK", (dialog, id) -> {
                            DatabaseHelper.addCourse(userId, course.getId(), courseIdsByUser);
                            bundle.putBoolean("isEnrolled", true);
                            getActivity().runOnUiThread(() -> {
                                bundle.putBoolean("isEnrolled", true);
                                playOrEnterBtn.setText("Start");
                                setBtn(userId, course, courseIdsByUser);
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> {
                            // User cancelled the dialog
                        });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            });
        }
    }
    public void showCourseData(Course course) {
        titleView.setText(course.getTitle());
        detailView.setText(course.getDetail());
        Glide
                .with(getActivity())
                .load(course.getImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity0) {
            mMyInterface = (ViewCourseToPlayCourse) context;
        } else {
            throw new ClassCastException(context + " must implement interface ShowPlayCourseFragment");
        }
    }

    @Override
    public void onDetach() {
        mMyInterface = null;
        super.onDetach();
    }

    public interface ViewCourseToPlayCourse {
        void playCourse(Bundle bundle);
    }
}