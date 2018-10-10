package io.github.dev_ritik.cafe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.MyViewHolder> {
    private List<Client> clientsList;

    ClientsAdapter(List<Client> clientsList) {
        this.clientsList = clientsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Client client = clientsList.get(position);
        holder.userId.setText(client.getId());
        holder.userName.setText(client.getName());
        holder.checkInTime.setText(client.getCheckInTime());
        holder.checkOutTime.setText(client.getCheckOutTime());
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userId, userName, checkInTime, checkOutTime;

         MyViewHolder(View view) {
            super(view);
            userId = (TextView) view.findViewById(R.id.userId);
            userName = (TextView) view.findViewById(R.id.userName);
            checkInTime = (TextView) view.findViewById(R.id.checkInTime);
            checkOutTime = (TextView) view.findViewById(R.id.checkOutTime);
        }
    }
}