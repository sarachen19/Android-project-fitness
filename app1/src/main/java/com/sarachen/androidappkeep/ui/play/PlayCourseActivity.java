package com.sarachen.androidappkeep.ui.play;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarachen.androidappkeep.R;
import com.sarachen.androidappkeep.model.Course;
import com.sarachen.androidappkeep.model.Exercise;

import org.parceler.Parcels;

import java.util.concurrent.TimeUnit;


public class PlayCourseActivity extends AppCompatActivity implements PauseCourseFragment.ShowExerciseDetailInterface {
    FirebaseStorage storage ;
    StorageReference imageStorageRef;
    private Bundle bundle;//course and userId
    private Course course;
    private int userId;
    private SimpleExoPlayer player;
    private StyledPlayerView playerView;
    private ImageButton exoPlayPauseBtn;
    private TextView currentTimeRemain, currentDuration, currentIndex, currentTitle, seeNotesLink;
    private StorageReference videoStorageRef;
    private int exercisesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_course);
        storage = FirebaseStorage.getInstance();
        //set all views
        currentTimeRemain = (TextView) findViewById(R.id.exo_current_time_remain);
        currentDuration = (TextView) findViewById(R.id.exo_current_duration);
        currentIndex = (TextView) findViewById(R.id.exo_current_index);
        currentTitle = (TextView) findViewById(R.id.exo_current_title);
        seeNotesLink = (TextView) findViewById(R.id.exo_see_notes);
        // get course and userId
        bundle = getIntent().getExtras();
        course = Parcels.unwrap(bundle.getParcelable("course_parcel"));
        userId = bundle.getInt("userId");
        //build player
        buidPlayer();
        setOnClickListenerForPauseBtn();
        setTransitionListener();
        //update textviews
        updateTextViewsEachSecond();
        //set exercises count;
        exercisesCount = course.getExercises().size();
    }
    private void buidPlayer() {
        // setup player
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView = (StyledPlayerView)findViewById(R.id.player_view);
        exoPlayPauseBtn = findViewById(R.id.exo_play_pause_btn);
        // Attach player to the view.
        playerView.setPlayer(player);
        // Build the media items.
        for (final Exercise exercise : course.getExercises()) {
            String path = exercise.getVideoUrl();
            videoStorageRef = storage.getReferenceFromUrl(path.trim());
            videoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Log.d("chenxirui", String.valueOf(uri));
                    //set mediaId and uri
                    MediaItem.Builder builder = new MediaItem.Builder();
                    builder.setUri(uri);
                    builder.setMediaId(String.valueOf(exercise.getId()));
                    MediaItem item = builder.build();
                    player.addMediaItem(item);
                    //add interval
                    player.addMediaItem(MediaItem.fromUri("https://firebasestorage.googleapis.com/v0/b/quickstart-1592333099507.appspot.com/o/interval.mp4?alt=media&token=908ee9e8-04f2-46e6-b36e-c77170ae1047"));
                }
            });
        }
        player.addListener(new Player.EventListener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                player.setPlayWhenReady(true);
            }
        });
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.seekTo(0);
        player.play();
    }

    public void setOnClickListenerForPauseBtn() {
        exoPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                    MediaItem item = player.getCurrentMediaItem();
                    if (item.mediaId.charAt(0) != 'h') {
                        int index = Integer.parseInt(item.mediaId);

                        PauseCourseFragment pauseCourseFragment = new PauseCourseFragment();
                        Exercise exercise = course.getExercises().get(index);
                        Intent intent = new Intent(getApplicationContext(), PauseCourseFragment.class);
                        Bundle itemBundle = new Bundle();
                        itemBundle.putParcelable("exercise_parcel", Parcels.wrap(exercise));
                        pauseCourseFragment.setArguments(itemBundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.course_play_main_frame, pauseCourseFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
                else {
                    player.setPlayWhenReady(true);
                }
            }
        });
    }
    public void setTransitionListener() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if (player.getCurrentMediaItem().mediaId.charAt(0) != 'h') {
                    //show time bar for exercises video
                    ((DefaultTimeBar)findViewById(R.id.exo_progress)).setVisibility(View.VISIBLE);
                    ((ImageButton)findViewById(R.id.exo_play_pause_btn)).setVisibility(View.VISIBLE);
                    ((ImageButton)findViewById(R.id.exo_prev)).setVisibility(View.VISIBLE);
                    ((ImageButton)findViewById(R.id.exo_next)).setVisibility(View.VISIBLE);
                    seeNotesLink.setVisibility(View.VISIBLE);
                    //set texts
                    int currentPositionInExercisesList = player.getCurrentWindowIndex() / 2;
                    String indexString = (currentPositionInExercisesList + 1) + "/" + exercisesCount;
                    currentIndex.setText(indexString);
                    currentTitle.setText(course.getExercises().get(currentPositionInExercisesList).getName());
                }
                else {
                    //hide time bar for intervals
                    ((DefaultTimeBar)findViewById(R.id.exo_progress)).setVisibility(View.GONE);
                    ((ImageButton)findViewById(R.id.exo_play_pause_btn)).setVisibility(View.GONE);
                    ((ImageButton)findViewById(R.id.exo_prev)).setVisibility(View.GONE);
                    ((ImageButton)findViewById(R.id.exo_next)).setVisibility(View.GONE);
                    seeNotesLink.setVisibility(View.GONE);
                    //set texts to empty
                    currentIndex.setText(" ");
                    currentTitle.setText(" ");
                    currentTimeRemain.setText(" ");
                    currentDuration.setText(" ");
                }
            }
        });
    }

    private void updateTextViewsEachSecond() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MediaItem item = player.getCurrentMediaItem();
                            if (player.isPlaying() && item.mediaId.charAt(0) != 'h') {
                                int index = player.getCurrentWindowIndex();
                                Long duration = TimeUnit.MILLISECONDS.toSeconds(player.getDuration());
                                Long currentPositionMillis = TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition());
                                int countDown = (int) (duration - currentPositionMillis);
                                if (countDown < 10) currentTimeRemain.setText("0:0" + String.valueOf(countDown));
                                else currentTimeRemain.setText("0:" + String.valueOf(countDown));
                                currentDuration.setText(duration.toString() + "\"");
                            }
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void showExerciseDetail(Bundle bundle) {
        ExerciseDetailFragment exerciseDetailFragment = new ExerciseDetailFragment();
        exerciseDetailFragment.setArguments(bundle);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.course_play_main_frame, exerciseDetailFragment)
            .addToBackStack(null)
            .commit();
    }
}