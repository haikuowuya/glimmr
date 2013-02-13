package com.bourke.glimmr.fragments.home;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;

import com.bourke.glimmr.common.Constants;
import com.bourke.glimmr.fragments.base.PhotoGridFragment;
import com.bourke.glimmr.tasks.LoadPhotostreamTask;

import com.googlecode.flickrjandroid.photos.Photo;
import android.widget.GridView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

public class PhotoStreamGridFragment extends PhotoGridFragment {

    private static final String TAG = "Glimmr/PhotoStreamGridFragment";

    public static final String KEY_NEWEST_PHOTOSTREAM_PHOTO_ID =
        "glimmr_newest_photostream_photo_id";

    protected LoadPhotostreamTask mTask;

    public static PhotoStreamGridFragment newInstance() {
        PhotoStreamGridFragment newFragment = new PhotoStreamGridFragment();
        return newFragment;
    }

    public static PhotoStreamGridFragment newInstance(
            boolean retainInstance, int gridChoiceMode) {
        PhotoStreamGridFragment newFragment = new PhotoStreamGridFragment();
        newFragment.mRetainInstance = retainInstance;
        newFragment.mGridChoiceMode = gridChoiceMode;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return mLayout;
    }

    @Override
    protected boolean shouldRetainInstance() {
        return mRetainInstance;
    }

    @Override
    protected int getGridChoiceMode() {
        return mGridChoiceMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Set to false to disable overlay */
        mShowDetailsOverlay = true;
    }

    /**
     * Once the parent binds the adapter it will trigger cacheInBackground
     * for us as it will be empty when first bound.  So we don't need to
     * override startTask().
     */
    @Override
    protected boolean cacheInBackground() {
        startTask(mPage++);
        return mMorePages;
    }

    private void startTask(int page) {
        super.startTask();
        mActivity.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
        mTask = new LoadPhotostreamTask(this, mActivity.getUser(), page);
        mTask.execute(mOAuth);
    }

    @Override
    public String getNewestPhotoId() {
        SharedPreferences prefs = mActivity.getSharedPreferences(Constants
                .PREFS_NAME, Context.MODE_PRIVATE);
        String newestId = prefs.getString(KEY_NEWEST_PHOTOSTREAM_PHOTO_ID,
                null);
        return newestId;
    }

    @Override
    public void storeNewestPhotoId(Photo photo) {
        SharedPreferences prefs = mActivity.getSharedPreferences(Constants
                .PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NEWEST_PHOTOSTREAM_PHOTO_ID, photo.getId());
        editor.commit();
        if (Constants.DEBUG)
            Log.d(getLogTag(), "Updated most recent photostream photo id to " +
                photo.getId());
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
}
