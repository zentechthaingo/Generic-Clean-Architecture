package com.zeyad.cleanarchetecturet.presentation.presenters;

import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

import com.zeyad.cleanarchetecturet.domain.User;
import com.zeyad.cleanarchetecturet.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchetecturet.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchetecturet.domain.interactor.DefaultSubscriber;
import com.zeyad.cleanarchetecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchetecturet.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchetecturet.presentation.view.UserDetailsView;
import com.zeyad.cleanarchetecturet.utilities.Constants;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 2/11/16 Finish and Test!
public class StreamingPresenter implements BasePresenter, RtspClient.Callback,
        Session.Callback, SurfaceHolder.Callback {

    private UserDetailsView streamingView;

    public void setView(@NonNull UserDetailsView view) {
        streamingView = view;

        mSurfaceView.getHolder().addCallback(this);
        // Initialize RTSP client
        initRtspClient();
    }

    @Override
    public void resume() {
        toggleStreaming();
    }

    @Override
    public void pause() {
        toggleStreaming();
    }

    @Override
    public void destroy() {
//        getUserDetailsUseCase.unsubscribe();
        //
        mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }

    /**
     * Initializes the presenter by start retrieving user details.
     */
    public void initialize(int userId) {
//        this.userId = userId;
        loadUserDetails();
    }

    /**
     * Loads user details.
     */
    private void loadUserDetails() {
        hideViewRetry();
        showViewLoading();
        getUserDetails();
    }

    private void showViewLoading() {
        streamingView.showLoading();
    }

    private void hideViewLoading() {
        streamingView.hideLoading();
    }

    private void showViewRetry() {
        streamingView.showRetry();
    }

    private void hideViewRetry() {
        streamingView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(streamingView.getContext(),
                errorBundle.getException());
        streamingView.showError(errorMessage);
    }

    private void showUserDetailsInView(User user) {
//        streamingView.renderUser(userModelDataMapper.transform(user));
    }

    private void getUserDetails() {
//        getUserDetailsUseCase.execute(new UserDetailsSubscriber());
    }

    private final class UserDetailsSubscriber extends DefaultSubscriber<User> {

        @Override
        public void onCompleted() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            showViewRetry();
        }

        @Override
        public void onNext(User user) {
            showUserDetailsInView(user);
        }
    }

    //--------------------------------------------------------------------------------------------//

    // log tag
    public final static String TAG = UserDetailsPresenter.class.getSimpleName();

    // surfaceview
    private static SurfaceView mSurfaceView;

    // Rtsp session
    private Session mSession;
    private static RtspClient mClient;

    private void initRtspClient() {
        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(AndroidApplication.getInstance().getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(8000, 16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView).setPreviewOrientation(0)
                .setCallback(this).build();
        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);
        mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
        String ip, port, path;
        // We parse the URI written in the Editext
        Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
        Matcher m = uri.matcher(Constants.STREAM_URL);
        m.find();
        ip = m.group(1);
        port = m.group(2);
        path = m.group(3);
        mClient.setCredentials(Constants.PUBLISHER_USERNAME,
                Constants.PUBLISHER_PASSWORD);
        mClient.setServerAddress(ip, Integer.parseInt(port));
        mClient.setStreamPath("/" + path);
    }

    private void toggleStreaming() {
        if (!mClient.isStreaming()) {
            // Start camera preview
            mSession.startPreview();
            // Start video stream
            mClient.startStream();
        } else {
            // already streaming, stop streaming
            // stop camera preview
            mSession.stopPreview();
            // stop streaming
            mClient.stopStream();
        }
    }

    @Override
    public void onBitrateUpdate(long bitrate) {

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                break;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            alertError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionStopped() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRtspUpdate(int message, Exception exception) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                alertError(exception.getMessage());
                exception.printStackTrace();
                break;
        }
    }

    // TODO: 2/11/16 Apply MVP!
    private void alertError(final String msg) {
//        showErrorMessage(new DefaultErrorBundle((Exception) e));
//        final String error = (msg == null) ? "Unknown error: " : msg;
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setMessage(error).setPositiveButton("Ok",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }
}