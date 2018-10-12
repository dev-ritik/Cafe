package io.github.dev_ritik.cafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.zxing.WriterException;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    public String userId = null;
    ProfileTracker profileTracker;
    ImageView accountButton;
    ImageView qrImage;
    String userName = "not provided";
    TextView qrCode;
    LinearLayout codeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("point ma6", "oncreare");

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        qrImage = findViewById(R.id.qrImage);
        qrCode = findViewById(R.id.qrCode);
        accountButton = findViewById(R.id.account_button);
        codeLayout = findViewById(R.id.codeLayout);

        // set click listener on account button
        accountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userId == null) {
                    Toast.makeText(MainActivity.this, "Fetching data, Please Wait", Toast.LENGTH_SHORT).show();
                    fetchProfile();
                } else
                    startActivity(new Intent(MainActivity.this, AccountActivity.class).putExtra("id", userId));
            }
        });

        // register a receiver for the onCurrentProfileChanged event
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfilePic(currentProfile);
                }
            }
        };


        fetchProfile();
    }

    private void fetchProfile() {

        if (AccessToken.getCurrentAccessToken() != null) {
            // If there is an access token then Login Button was used
            // Check if the profile has already been fetched
            Profile currentProfile = Profile.getCurrentProfile();
            Log.i("point ma81", "profile login " + currentProfile);
            if (currentProfile != null) {
                Log.i("point ma83", "profile login");
                displayProfilePic(currentProfile);
                this.userId = currentProfile.getId();
                this.userName = currentProfile.getFirstName() + " " + currentProfile.getMiddleName() + " " + currentProfile.getLastName();
            } else {
                Log.i("point ma88", "profile login");
                // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                Profile.fetchProfileForCurrentAccessToken();
                Log.i("point ma93", "profile fetch" + userId + " " + currentProfile);
            }
        } else {
            Log.i("point ma89", "accountkit login");
            // Otherwise, get Account Kit login information
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // get Account Kit ID
                    userId = account.getId();

//                        PhoneNumber phoneNumber = account.getPhoneNumber();
//                        if (account.getPhoneNumber() != null) {
//                            // if the phone number is available, display it
//                            String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
//                            info.setText(formattedPhoneNumber);
//                            infoLabel.setText(R.string.phone_label);
//                        } else {
//                            // if the email address is available, display it
//                            String emailString = account.getEmail();
//                            info.setText(emailString);
//                            infoLabel.setText(R.string.email_label);
//                        }

                }

                @Override
                public void onError(final AccountKitError error) {
                    String toastMessage = error.getErrorType().getMessage();
                    Log.i("point m06", toastMessage);
                }
            });
        }

    }

    private void generateQrCode(String message) {
        qrCode.setVisibility(View.GONE);
        QRGEncoder qrgEncoder = new QRGEncoder(message, null, QRGContents.Type.TEXT, 450);
        try {
            // Getting QR-Code as Bitmap
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            Log.i("point m86", e.toString());
            Toast.makeText(this, "something went wrong generating QR code", Toast.LENGTH_SHORT).show();
            qrCode.setText(message);
            qrCode.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("point ca58", "on destroy");
        // unregister the profile tracker receiver
        profileTracker.stopTracking();
    }

    private void displayProfilePic(Profile profile) {
        // helper method to load the profile pic in a circular imageview
        Uri uri = profile.getProfilePictureUri(28, 28);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.icon_profile_empty)
                .transform(transformation)
                .into(accountButton);
    }

    public String calculateTime() {
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString();

    }

    public void generateCode(View view) {
        if (userId == null) {
            Toast.makeText(this, "Please wait to load user data", Toast.LENGTH_SHORT).show();
        } else {
            generateQrCode(userId + "@" + userName + "@" + calculateTime());
            codeLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }
    }

    public void reGenerateCode(View view) {
        if (userId == null) {
            Toast.makeText(this, "Please wait to load user data", Toast.LENGTH_SHORT).show();
        } else {
            generateQrCode(userId + "@" + userName + "@" + calculateTime());
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("point ca64", "onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("point ca70", "onresume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("point ca76", "onpause");
    }
}

