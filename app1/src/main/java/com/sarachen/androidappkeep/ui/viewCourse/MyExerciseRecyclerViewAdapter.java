package com.sarachen.androidappkeep.ui.viewCourse;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarachen.androidappkeep.R;
import com.rozdoum.socialcomponents.utils.GlideApp;
import com.sarachen.androidappkeep.model.Exercise;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Exercise}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyExerciseRecyclerViewAdapter extends RecyclerView.Adapter<MyExerciseRecyclerViewAdapter.ViewHolder> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageStorageRef;
    private final List<Exercise> mValues;

    public MyExerciseRecyclerViewAdapter(List<Exercise> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String title = holder.mItem.getId() + " " + holder.mItem.getName();
        holder.mTitleView.setText(title);
        //get image ref and download into image view using GlideUI for Firebase storage
        imageStorageRef = storage.getReferenceFromUrl(holder.mItem.getImageUrl().trim());
        GlideApp
                .with(holder.itemView.getContext())
                .load(imageStorageRef)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public Exercise mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.fragment_exercise_title);
            mImageView = (ImageView) view.findViewById(R.id.fragment_exercise_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}