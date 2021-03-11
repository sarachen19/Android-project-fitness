package com.sarachen.androidappkeep.ui.play;

import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.model.Exercise;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

public class ExerciseDetailFragment extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Bundle bundle;
    private Exercise exercise;
    private SimpleExoPlayer player;
    private StyledPlayerView playerView;
    private StorageReference videoStorageRef;
    private TextView titleView, stepsView, breatheView, movementFeelingView, commonMistakesView;
    private LinearLayout imageGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        //setContentView(R.layout.fragment_exercise_detail);
        // get exercise
        bundle = getArguments();
        exercise = Parcels.unwrap(bundle.getParcelable("exercise_parcel"));
        //setup all views
        player = new SimpleExoPlayer.Builder(getContext()).build();
        playerView = (StyledPlayerView)view.findViewById(R.id.exercise_detail2_player_view);
        titleView = (TextView) view.findViewById(R.id.exercise_detail2_title);
        stepsView = (TextView) view.findViewById(R.id.exercise_detail2_steps);
        breatheView = (TextView) view.findViewById(R.id.exercise_detail2_breathe);
        movementFeelingView = (TextView) view.findViewById(R.id.exercise_detail2_movementFeeling);
        commonMistakesView = (TextView) view.findViewById(R.id.exercise_detail2_commonMistakes);
        imageGroup = (LinearLayout)view.findViewById(R.id.exercise_detail2_imagesGroup);

        putContent();
        addOnclickListener(view);
        buildPlayer();
        return view;
    }

    private void putContent(){
        titleView.setText(exercise.getName());
        stepsView.setText(getStringFromList(exercise.getSteps()));
        breatheView.setText(getStringFromList(exercise.getBreathes()));
        movementFeelingView.setText(getStringFromList(exercise.getMovementFeelinigs()));
        commonMistakesView.setText(getStringFromList(exercise.getCommonMistakes()));
        putMovementFeelingPics();
    }
    private String getStringFromList(List<String> list) {
        String output = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1)
                output += "\u2726 " + list.get(i) + "\n";
            else output += "\u2726 " + list.get(i) + "\n\n";
        }
        return output;
    }
    private void putMovementFeelingPics() {
        List<String> urls = exercise.getMoveFeelingPictures();
        for (String url : urls) {
            ImageView imgView = new ImageView(getContext());
            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e("===========================Picasso Error=========================", exception.getMessage());
                }
            });
            Picasso pic = builder.build();
            pic.load(url.trim()).into(imgView);
            imageGroup.addView(imgView);
        }
    }
    private void buildPlayer() {
        playerView.setPlayer(player);
        String path = exercise.getVideoUrl();
        videoStorageRef = storage.getReferenceFromUrl(path.trim());
        videoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                MediaItem item = MediaItem.fromUri(uri);
                player.addMediaItem(item);
            }
        });
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare();
        player.seekTo(0);
        player.play();
    }
    private void addOnclickListener(View view) {
        Button btn = (Button)view.findViewById(R.id.exercise_detail_back_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}