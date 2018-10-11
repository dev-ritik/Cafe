package io.github.dev_ritik.cafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.zxing.Result;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static int ACCOUNT_ACTIVITY_REQUEST_CODE = 1;
    ProfileTracker profileTracker;
    ImageView accountButton;
    String userId;
    String userName;
    Realm realm;
    String[] data;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();


        RealmResults<Client> clients = realm.where(Client.class).findAll();
        for (Client client : clients) {
            Toast.makeText(this, client.getId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, client.getId(), Toast.LENGTH_SHORT).show();
        }


        accountButton = (ImageView) findViewById(R.id.account_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        else {

            // set click listener on account button
            accountButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivityForResult(intent, ACCOUNT_ACTIVITY_REQUEST_CODE);
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

            // show profile pic on account button

            if (AccessToken.getCurrentAccessToken() != null) {
                // If there is an access token then Login Button was used
                // Check if the profile has already been fetched
                Profile currentProfile = Profile.getCurrentProfile();
                if (currentProfile != null) {
                    displayProfilePic(currentProfile);
                    this.userId = currentProfile.getId();
                    this.userName = currentProfile.getFirstName() + " " + currentProfile.getMiddleName() + " " + currentProfile.getLastName();
                } else {
                    // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                    Profile.fetchProfileForCurrentAccessToken();
                }
            } else {
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
                        Log.i("point m123", toastMessage);
                    }
                });
            }


            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void
                onDecoded(@NonNull final Result result) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                            data = result.getText().split("@");
                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });

        }
    }


    private void realmAddition(final String userId, final String userName, final String checkInTime) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Client client = bgRealm.createObject(Client.class);
                client.setId(userId);
                client.setName(userName);
                client.setCheckInTime(checkInTime);

                //add new client to database
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                yourPlacesArrayList.add(client);

                // Transaction was a success.
                Toast.makeText(MainActivity.this, "Successfully Stored", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // if the user logged out in AccountActivity, show the login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unregister the profile tracker receiver
        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
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

    public void checkIn(View view) {
        Log.i("point m247", Arrays.toString(data));
        if (data != null && data.length == 3 && data[0] != null && data[1] != null && data[2] != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Client> clients = realm.where(Client.class).equalTo("id", data[0]).findAll();
                    Log.i("point m253", clients + "" + data[0]);
                    if (clients == null || clients.size() == 0) {
                        realmAddition(data[0], data[1], data[2]);
                        Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();
                    } else if (clients.get(clients.size() - 1).getCheckOutTime() != null) {
                        realmAddition(data[0], data[1], data[2]);
                        Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Already checked in", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else
            Toast.makeText(this, "no data found", Toast.LENGTH_SHORT).show();
    }

    public void checkOut(View view) {
        if (data != null && data.length == 3 && data[0] != null && data[1] != null && data[2] != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Client> clients = realm.where(Client.class).equalTo("id", data[0]).findAll();
                    Log.i("point m261", clients + "" + data[0]);
                    if (clients == null || clients.size() == 0) {
                        Toast.makeText(MainActivity.this, "new user!! Please checkin", Toast.LENGTH_SHORT).show();
                    } else if (clients.get(clients.size() - 1).getCheckOutTime() == null) {
                        Toast.makeText(MainActivity.this, "Checked-out", Toast.LENGTH_SHORT).show();
                        clients.get(clients.size() - 1).setCheckOutTime(data[2]);
                    } else {
                        Toast.makeText(MainActivity.this, "Already checked out!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else
            Toast.makeText(this, "no data found", Toast.LENGTH_SHORT).show();
    }

    public void clients(View view) {
        startActivity(new Intent(this, ClientActivity.class));
    }

}

