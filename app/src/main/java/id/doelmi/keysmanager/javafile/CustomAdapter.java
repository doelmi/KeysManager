package id.doelmi.keysmanager.javafile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import id.doelmi.keysmanager.R;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<CustomPOJO> list_members = new ArrayList<>();

    private final LayoutInflater inflater;
    private View view;

    private MyViewHolder holder;

    private Context context;

    public CustomAdapter(Context context) {
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.custom_row, parent, false);

        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CustomPOJO list_items = list_members.get(position);
        if (position == 0) {
            holder.garis.setVisibility(View.GONE);
        }
        holder.username.setText(list_items.getName());
        holder.content.setText(list_items.getContent());
        holder.time.setText(list_items.getTime());

        String uri_ = list_items.getGambar();
        String path = list_items.getPath();

        try {
            if (uri_ != null && uri_.contains(".jpg")) {
                try {
                    File f = new File(path, uri_);
                    Bitmap b = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(new FileInputStream(f)), 128, 128, true);
                    holder.kunciImage.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (uri_ != null && !uri_.equals("0") && !uri_.contains(".jpg")) {
                holder.kunciImage.setImageResource(Integer.parseInt(uri_));
            } else {
                holder.kunciImage.setImageResource(R.drawable.ic_launcher);
            }
        } catch (Exception e) {
            holder.kunciImage.setImageResource(R.drawable.ic_launcher);
        }

        if (list_items.getTime().equalsIgnoreCase("Ada")) {
            holder.time.setTextColor(Color.GREEN);
        } else {
            holder.time.setTextColor(Color.RED);
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
        TextView username, content, time;
        CircleImageView kunciImage;
        View garis;

        private MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            username = (TextView) itemView.findViewById(R.id.username);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            kunciImage = (CircleImageView) itemView.findViewById(R.id.kunciImage);
            garis = itemView.findViewById(R.id.view9);
        }

        @Override
        public void onClick(View v) {
        }

    }
}
