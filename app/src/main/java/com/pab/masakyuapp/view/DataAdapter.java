package com.pab.masakyuapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.DataModel;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<DataModel> dataList;
    private Context context;
    private OnItemClickCallback onItemClickCallback;

    public DataAdapter(List<DataModel> dataList, Context context, OnItemClickCallback onItemClickCallback) {
        this.dataList = dataList;
        this.context = context;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_judul_resep, tvPorsi, tv_waktu_masak, tv_id, tv_sender;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_judul_resep = itemView.findViewById(R.id.tv_judul_resep);
            tvPorsi = itemView.findViewById(R.id.tv_porsi);
            tv_waktu_masak = itemView.findViewById(R.id.tv_pengirim);
            tv_sender = itemView.findViewById(R.id.tv_sender);

            itemView.setOnClickListener(this);
        }

        public void bind(DataModel data) {
            tv_id.setText(String.valueOf(data.getId()));
            tv_judul_resep.setText(data.getNamaResep());
            tvPorsi.setText("Porsi : "+ String.valueOf(data.getPorsi()));
            tv_waktu_masak.setText("Waktu : "+ data.getWaktuMasak());
            tv_sender.setText("@"+ data.getUsername());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickCallback != null) {
                onItemClickCallback.onItemClicked(dataList.get(position));
            }
        }
    }
    public void updateData(List<DataModel> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }


    public interface OnItemClickCallback {
        void onItemClicked(DataModel data);
    }
}
