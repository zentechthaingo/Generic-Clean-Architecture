package com.zeyad.cleanarchitecture.presentation.screens.firebaze;

//public class FirebazeActivity extends AppCompatActivity {
//
//    private AdView mAdView;
//    private InterstitialAd mInterstitialAd;
//    private Button mLoadInterstitialButton;
//
//    public static Intent getCallingIntent(Context context) {
//        return new Intent(context, FirebazeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_firebaze);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
////        FirebaseCrash.log("Activity created");
//        // messaging
//        if (getIntent().getExtras() != null)
//            for (String key : getIntent().getExtras().keySet()) {
//                String value = getIntent().getExtras().getString(key);
//                Log.d("TAG", "Key: " + key + " Value: " + value);
//            }
//        FirebaseMessaging.getInstance().subscribeToTopic("news");
//        Log.d("TAG", "Subscribed to news topic");
//        Log.d("TAG", "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
//        // end messaging
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//        // AdMob ad unit IDs are not currently stored inside the google-services.json file.
//        // Developers using AdMob can store them as custom values in a string resource file or
//        // simply use constants. Note that the ad units used here are configured to return only test
//        // ads, and should not be used outside this sample.
//
//        // Create an InterstitialAd object. This same object can be re-used whenever you want to
//        // show an interstitial.
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
//
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                requestNewInterstitial();
//                beginSecondActivity();
//            }
//        });
//
//        mLoadInterstitialButton = (Button) findViewById(R.id.load_interstitial_button);
//        mLoadInterstitialButton.setOnClickListener((View.OnClickListener) v -> {
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//            } else {
//                beginSecondActivity();
//            }
//        });
//    }
//
//    /**
//     * Load a new interstitial ad asynchronously.
//     */
//    // [START request_new_interstitial]
//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//
//        mInterstitialAd.loadAd(adRequest);
//    }
//    // [END request_new_interstitial]
//
//    private void beginSecondActivity() {
////        Intent intent = new Intent(this, SecondActivity.class);
////        startActivity(intent);
//    }
//
//    // [START add_lifecycle_methods]
//
//    /**
//     * Called when leaving the activity
//     */
//    @Override
//    public void onPause() {
//        if (mAdView != null) {
//            mAdView.pause();
//        }
//        super.onPause();
//    }
//
//    /**
//     * Called when returning to the activity
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
//        if (!mInterstitialAd.isLoaded()) {
//            requestNewInterstitial();
//        }
//    }
//
//    /**
//     * Called before the activity is destroyed
//     */
//    @Override
//    public void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
//        super.onDestroy();
//    }
//}