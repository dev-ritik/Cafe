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

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ClientsAdapter
        extends RealmRecyclerViewAdapter<Client, ClientsAdapter.ClientViewHolder> {

    Realm realm1;

    public ClientsAdapter(Context context, OrderedRealmCollection<Client> clientList, Realm realm) {
        super(context, clientList, true);
//        this.realm = realm;
        realm1 = Realm.getDefaultInstance();


    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = getData().get(position);
        ClientViewHolder clientViewHolder = (ClientViewHolder) holder;
        clientViewHolder.loadItem(client);
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.client_data, parent, false);

        return new ClientViewHolder(view);

//        ClientViewHolder itemViewHolder = new ClientViewHolder(view);
//        return itemViewHolder;
    }

    private void deleteFromDatabase(final String id) {
        realm1.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Client client = bgRealm.where(Client.class).equalTo("checkInTime", id).findFirst();
                if (client != null) {
                    client.deleteFromRealm();
                }
//                Log.i("point 116", "Deleting " + id);
//
//                RealmResults<Client> result = realm1.where(Client.class).equalTo("checkInTime", id).findAll();
//                Log.i("point 119", "Deleting " + result);
//                result.deleteAllFromRealm();

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.


                Log.i("database", "Delete ok");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.i("database", error.getMessage());
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
                    deleteFromDatabase(client.getCheckInTime());
                }
            });
        }
    }
}