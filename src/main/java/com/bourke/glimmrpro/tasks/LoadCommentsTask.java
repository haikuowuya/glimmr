package com.bourke.glimmrpro.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.bourke.glimmrpro.common.Constants;
import com.bourke.glimmrpro.common.FlickrHelper;
import com.bourke.glimmrpro.event.Events.ICommentsReadyListener;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.comments.Comment;

import java.util.Date;
import java.util.List;

@SuppressWarnings("EmptyMethod")
public class LoadCommentsTask
        extends AsyncTask<OAuth, Void, List<Comment>> {

    private static final String TAG = "Glimmr/LoadCommentsTask";

    private final ICommentsReadyListener mListener;
    private final Photo mPhoto;
    private Exception mException;

    public LoadCommentsTask(ICommentsReadyListener listener, Photo photo) {
        mListener = listener;
        mPhoto = photo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Comment> doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        if (oauth != null) {
            if (Constants.DEBUG) Log.d(TAG, "Authenticated call");
            OAuthToken token = oauth.getToken();
            try {
                Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
                        token.getOauthToken(), token.getOauthTokenSecret());
                Date minCommentDate = null;
                Date maxCommentDate = null;
                return f.getCommentsInterface().getList(mPhoto.getId(),
                        minCommentDate, maxCommentDate);
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }
        } else {
            if (Constants.DEBUG) Log.d(TAG, "Unauthenticated call");
            try {
                Date minCommentDate = null;
                Date maxCommentDate = null;
                return FlickrHelper.getInstance().getCommentsInterface()
                    .getList(mPhoto.getId(), minCommentDate, maxCommentDate);
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final List<Comment> result) {
        if (result == null) {
            if (Constants.DEBUG)
                Log.e(TAG, "Error fetching comments, result is null");
        }
        mListener.onCommentsReady(result, mException);
    }

    @Override
    protected void onCancelled(final List<Comment> result) {
        if (Constants.DEBUG)
            Log.d(TAG, "onCancelled");
    }
}