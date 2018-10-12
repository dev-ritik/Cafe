package io.github.dev_ritik.cafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmResults;

public class ClientActivity extends AppCompatActivity {
    public DatabaseReference mDatabaseReference;
    Realm realm;
    RealmResults<Client> results;
//    private ArrayList<Client> clientList = new ArrayList<>();
//    private ClientsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
//        Log.i("point ca27", "on create");

        realm = Realm.getDefaultInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("transactions");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        mAdapter = new ClientsAdapter(clientList);
        results = realm.where(Client.class).findAll();

//        interpolateRecycleView();


        ClientsAdapter clientsAdapter = new ClientsAdapter(this, results, realm, mDatabaseReference);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(clientsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("point ca58", "on destroy");
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
