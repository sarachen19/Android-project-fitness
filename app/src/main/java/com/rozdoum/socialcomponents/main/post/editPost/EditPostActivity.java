/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rozdoum.socialcomponents.main.post.editPost;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rozdoum.socialcomponents.R;
import com.rozdoum.socialcomponents.main.interactors.PostInteractor;
import com.rozdoum.socialcomponents.main.main.MainActivity;
import com.rozdoum.socialcomponents.main.post.BaseCreatePostActivity;
import com.rozdoum.socialcomponents.managers.PostManager;
import com.rozdoum.socialcomponents.model.Post;
import com.rozdoum.socialcomponents.utils.GlideApp;
import com.rozdoum.socialcomponents.utils.ImageUtil;

public class EditPostActivity extends BaseCreatePostActivity<EditPostView, EditPostPresenter> implements EditPostView {
    private static final String TAG = EditPostActivity.class.getSimpleName();
    public static final String POST_EXTRA_KEY = "EditPostActivity.POST_EXTRA_KEY";
    public static final int EDIT_POST_REQUEST = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Post post = (Post) getIntent().getSerializableExtra(POST_EXTRA_KEY);
        presenter.setPost(post);
        showProgress();
        fillUIFields(post);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.addCheckIsPostChangedListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.closeListeners();
    }

    @NonNull
    @Override
    public EditPostPresenter createPresenter() {
        if (presenter == null) {
            return new EditPostPresenter(this);
        }
        return presenter;
    }

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void fillUIFields(Post post) {
        titleEditText.setText(post.getTitle());
        descriptionEditText.setText(post.getDescription());
        loadPostDetailsImage(post.getImageTitle());
        hideProgress();
    }

    private void loadPostDetailsImage(String imageTitle) {
        PostManager.getInstance(this.getApplicationContext()).loadImageMediumSize(GlideApp.with(this),
                imageTitle,
                imageView,
                () -> progressBar.setVisibility(View.GONE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.save) {
            presenter.doSavePost(imageUri);
            return true;
        }
                return super.onOptionsItemSelected(item);
        }

}
