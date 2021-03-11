package com.sarachen.androidappkeep.ui.play;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarachen.androidappkeep.R;
import com.rozdoum.socialcomponents.utils.GlideApp;
import com.sarachen.androidappkeep.model.Exercise;
import com.sarachen.androidappkeep.ui.MainActivity0;
import com.sarachen.androidappkeep.ui.viewCourse.ViewCourseFragment;

import org.parceler.Parcels;

public class PauseCourseFragment extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Bundle bundle;
    private Exercise exercise;
    private ImageView pauseImageView;
    private TextView pauseTextView;
    private ConstraintLayout constraintLayoutViewDetail;
    private Button continueBtn, exitBtn;
    private ShowExerciseDetailInterface mMyInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pause_course, container, false);

        //set pauseImageView, pauseTextView, constraintLayoutViewDetail, buttons
        pauseImageView = (ImageView)view.findViewById(R.id.exercise_detail_pause_imageView);
        pauseTextView = (TextView)view.findViewById(R.id.exercise_detail_pause_imageView_group_text);
        constraintLayoutViewDetail = (ConstraintLayout)view.findViewById(R.id.exercise_detail_pause_imageView_group);
        continueBtn = (Button)view.findViewById(R.id.exercise_detail_play_btn);
        exitBtn = (Button)view.findViewById(R.id.exercise_detail_exit_btn);
        // get exercise
        bundle = getArguments();
        exercise = Parcels.unwrap(bundle.getParcelable("exercise_parcel"));
        //get image ref and download into image view using GlideUI for Firebase storage
        StorageReference imageStorageRef = storage.getReferenceFromUrl(exercise.getImageUrl().trim());
        GlideApp
                .with(getContext())
                .load(imageStorageRef)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(pauseImageView);
        //setup exercise title
        pauseTextView.setText(exercise.getName());
        setListenerForViewDetail();
        return view;
    }

    public void setListenerForViewDetail() {
        //set listener for continue and exit button
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //set listener for text detail
        constraintLayoutViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyInterface.showExerciseDetail(bundle);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayCourseActivity) {
            mMyInterface = (PauseCourseFragment.ShowExerciseDetailInterface) context;
        } else {
            throw new ClassCastException(context + " must implement interface ShowExerciseDetail interface");
        }
    }

    @Override
    public void onDetach() {
        mMyInterface = null;
        super.onDetach();
    }

    public interface ShowExerciseDetailInterface {
        void showExerciseDetail(Bundle bundle);
    }
}