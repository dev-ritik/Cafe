package io.github.dev_ritik.cafe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {
    private List<Transaction> transactionsList;

    TransactionsAdapter(List<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transaction transaction = transactionsList.get(position);
        holder.checkInTime.setText(transaction.getCheckInTime());
        holder.checkOutTime.setText(transaction.getCheckOutTime());
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView checkInTime, checkOutTime;
        Button delete;

        MyViewHolder(View view) {
            super(view);
            checkInTime = (TextView) view.findViewById(R.id.checkInTime);
            checkOutTime = (TextView) view.findViewById(R.id.checkOutTime);
        }
    }
}