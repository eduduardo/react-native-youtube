package com.inprogress.reactnativeyoutube;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    // the name of the key that defines the youtube api key
    // this should be defined as <meta-data> at the <application> level of the manifest
    private static final String KEY_YOUTUBE_API = "youtube.API_KEY";

    private static final Pattern urlPattern = Pattern.compile("(((ftp|https?):\\/\\/)[\\-\\w@:%_\\+.~#?,&\\/\\/=]+)|((mailto:)?[_.\\w-]+@([\\w][\\w\\-]+\\.)+[a-zA-Z]{2,3})");

    // the name of the keys to be sent from the @ReactMethod
    public static final String KEY_VIDEO_ID = "videoId";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";

    private String videoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube);

        // get view resources from the rendered layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
        TextView titleView = (TextView) findViewById(R.id.titleTextView);
        TextView descriptionView = (TextView) findViewById(R.id.descriptionTextView);

        // get the api key from the application meta-data
        String apiKey = getApiKey();if (apiKey == null) {
            Toast.makeText(this, R.string.error_no_api_key, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // get the data from the bundle
        // the only required item is videoId
        // if a title and description are provided, set the text of their companion components
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_VIDEO_ID)) {
                videoId = extras.getString(KEY_VIDEO_ID);
            }

            if (extras.containsKey(KEY_TITLE)) {
                titleView.setText(extras.getString(KEY_TITLE));

                // check for a custom font definition
                int fontResourceId = getResources().getIdentifier("title_font_path", "string", getPackageName());

                if (fontResourceId > 0) {
                    Typeface font = Typeface.createFromAsset(getAssets(), getString(fontResourceId));
                    titleView.setTypeface(font);
                }
            }

            if (extras.containsKey(KEY_DESCRIPTION)) {
                descriptionView.setText(extras.getString(KEY_DESCRIPTION));
            }
        }

        if (videoId == null) {
            Toast.makeText(this, R.string.error_video_does_not_exist, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        playerView.initialize(apiKey, this);
    }

    // helper method to get the youtube api key
    // from the <application> <meta-data> in the manifest
    private String getApiKey () {
        String key = "";

        try {
            key = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString(KEY_YOUTUBE_API);
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return key;
    }

    // handle any interesting links in the description
    public void onDescriptionClick (View view) {
        CharSequence text = ((TextView) view).getText();
        Matcher urlMatcher = urlPattern.matcher(text);

        if (urlMatcher.find()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMatcher.group(1)));
            startActivity(intent);
        }
    }

    @Override
    public void onInitializationSuccess (YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean isRestored) {
        youTubePlayer.setPlayerStyle(PlayerStyle.MINIMAL);

        if (videoId != null) {
            if (isRestored) {
                youTubePlayer.play();
            } else {
                youTubePlayer.loadVideo(videoId);
            }
        }
    }

    @Override
    public void onInitializationFailure (YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, R.string.error_init_failure, Toast.LENGTH_LONG).show();
    }
}


