package com.example.walkin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private MyListData[] listdata;
    boolean status;
    // RecyclerView recyclerView;
    public MyListAdapter(MyListData[] listdata,boolean statusCode) {
        this.listdata = listdata;
        this.status = statusCode;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata[position];
        holder.textView.setText(listdata[position].getDescription());
        holder.imageView1.setImageResource(listdata[position].getImgId());
        holder.imageView2.setImageResource(listdata[position].getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
        if (status == true){
            holder.btnReprint.setVisibility(View.GONE);
        }else{
            holder.btnReprint.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView1,imageView2;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public Button btnReprint;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView1 = (ImageView) itemView.findViewById(R.id.imageView1);
            this.imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            this.textView = (TextView) itemView.findViewById(R.id.textView1);
            this.btnReprint = (Button) itemView.findViewById(R.id.btnReprint);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}