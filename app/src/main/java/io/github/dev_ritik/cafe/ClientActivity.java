package io.github.dev_ritik.cafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ClientActivity extends AppCompatActivity {
    Realm realm;

    private ArrayList<Client> clientList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClientsAdapter mAdapter;

    private ClientsAdapter clientsAdapter;
    RealmResults<Client> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        realm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mAdapter = new ClientsAdapter(clientList);
        results = realm.where(Client.class).findAll();

//        interpolateRecycleView();



        clientsAdapter = new ClientsAdapter(this,results,realm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(clientsAdapter);


//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
    }

    private void interpolateRecycleView() {
        RealmResults<Client> clients = realm.where(Client.class).findAll();

        // Use an iterator to add all
        realm.beginTransaction();
        Log.i("point ca45", clients.size() + "");

        for (Client client : clients) {
            Log.i("point ca455", "here" + client);
            clientList.add(client);
            Log.i("point ca51", clientList.size() + "");
            mAdapter.notifyDataSetChanged();
        }

        realm.commitTransaction();
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
