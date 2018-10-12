package io.github.dev_ritik.cafe;

//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.util.List;
//
//public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.MyViewHolder> {
//    private List<Client> clientsList;
//
//    ClientsAdapter(List<Client> clientsList) {
//        this.clientsList = clientsList;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.client_data, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Client client = clientsList.get(position);
//        holder.userId.setText(client.getId());
//        holder.userName.setText(client.getName());
//        holder.checkInTime.setText(client.getCheckInTime());
//        holder.checkOutTime.setText(client.getCheckOutTime());
//    }
//
//    @Override
//    public int getItemCount() {
//        return clientsList.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView userId, userName, checkInTime, checkOutTime;
//        Button delete;
//
//        MyViewHolder(View view) {
//            super(view);
//            userId = (TextView) view.findViewById(R.id.userId);
//            userName = (TextView) view.findViewById(R.id.userName);
//            checkInTime = (TextView) view.findViewById(R.id.checkInTime);
//            checkOutTime = (TextView) view.findViewById(R.id.checkOutTime);
//            delete = view.findViewById(R.id.delete);
//            delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
//
//        }
//    }
//}

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ClientsAdapter
        extends RealmRecyclerViewAdapter<Client, ClientsAdapter.ClientViewHolder> {

    Realm realm1;
    DatabaseReference root;

    public ClientsAdapter(Context context, OrderedRealmCollection<Client> clientList, Realm realm, DatabaseReference root) {
        super(context, clientList, true);
        realm1 = Realm.getDefaultInstance();
        this.root = root;

    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = getData().get(position);
        for (Client c : getData()
                ) {
            Log.i("point 95", c.getCheckInTime());

        }
        Log.i("point 98", "done");
        ClientViewHolder clientViewHolder = (ClientViewHolder) holder;
        clientViewHolder.loadItem(client);
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.client_data, parent, false);

        return new ClientViewHolder(view);

    }

    private void deleteFromDatabase(final String id, final String checkInTime) {
        realm1.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Client client = bgRealm.where(Client.class).equalTo("checkInTime", checkInTime).findFirst();
                if (client != null) {
                    client.deleteFromRealm();
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                notifyDataSetChanged();

                Log.i("database", "Delete ok");
                deleteFromFireBase(id, checkInTime);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.i("database", error.getMessage());
            }
        });

    }

    private void deleteFromFireBase(final String id, final String checkInTime) {
        Query query = root.child(id).orderByChild("checkInTime").equalTo(checkInTime);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot != null && snapshot.getKey() != null) {
                        root.child(id).child(snapshot.getKey()).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(, "Failed to upload", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public class ClientViewHolder extends RecyclerView.ViewHolder {
        public TextView userId, userName, checkInTime, checkOutTime;
        Button delete;

        ClientViewHolder(View view) {
            super(view);
            userId = (TextView) view.findViewById(R.id.userId);
            userName = (TextView) view.findViewById(R.id.userName);
            checkInTime = (TextView) view.findViewById(R.id.checkInTime);
            checkOutTime = (TextView) view.findViewById(R.id.checkOutTime);
            delete = view.findViewById(R.id.delete);


        }

        void loadItem(final Client client) {
            userName.setText(client.getName());
            userId.setText(client.getId());
            checkInTime.setText(client.getCheckInTime());
            checkOutTime.setText(client.getCheckOutTime());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFromDatabase(client.getId(), client.getCheckInTime());
                }
            });
        }
    }
}