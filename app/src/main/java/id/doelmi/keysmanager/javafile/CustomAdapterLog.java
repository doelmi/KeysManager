package id.doelmi.keysmanager.javafile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import id.doelmi.keysmanager.R;

/**
 * Created by abdul on 02/07/2017.
 */

public class CustomAdapterLog extends RecyclerView.Adapter<CustomAdapterLog.MyViewHolder> {
    ArrayList<CustomPOJO> list_members = new ArrayList<>();

    private final LayoutInflater inflater;
    View view;

    MyViewHolder holder;

    private Context context;

    public CustomAdapterLog(Context context) {
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.custom_row_log, parent, false);

        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CustomPOJO list_items = list_members.get(position);
        if (position == 0){
            holder.garis.setVisibility(View.GONE);
        }
        holder.aktivitas.setText(list_items.getName());
        holder.waktu.setText(list_items.getContent());

        String aktivitas = list_items.getName();

        if (aktivitas.contains("mengambil")){
            holder.imageStatus.setImageResource(R.drawable.keluar_round);
        }else if (aktivitas.contains("menambah")){
            holder.imageStatus.setImageResource(R.drawable.tambah_round);
        }else if (aktivitas.contains("menghapus")){
            holder.imageStatus.setImageResource(R.drawable.delete_round);
        }else if (aktivitas.contains("membatalkan")){
            holder.imageStatus.setImageResource(R.drawable.no_arsip_round);
        }else if (aktivitas.contains("mengembalikan")){
            holder.imageStatus.setImageResource(R.drawable.masuk_round);
        }else if (aktivitas.contains("memperbarui")){
            holder.imageStatus.setImageResource(R.drawable.edit_round);
        }else if (aktivitas.contains("mengarsipkan")){
            holder.imageStatus.setImageResource(R.drawable.arsip_round);
        }

    }

    public void setListContent(ArrayList<CustomPOJO> list_members) {
        this.list_members = list_members;

        notifyItemRangeChanged(0, list_members.size());
    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView aktivitas, waktu;
        ImageView imageStatus;
        View garis;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            aktivitas = (TextView) itemView.findViewById(R.id.aktivitas);
            waktu = (TextView) itemView.findViewById(R.id.waktu);
            imageStatus = (ImageView) itemView.findViewById(R.id.imageStatus);
            garis = itemView.findViewById(R.id.view9);
        }

        @Override
        public void onClick(View v) {
        }

    }

    public void removeAt(int position) {
        list_members.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, list_members.size());
    }
}
