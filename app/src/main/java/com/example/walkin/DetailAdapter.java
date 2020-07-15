package com.example.walkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.walkin.models.ImageModel;
import com.example.walkin.models.PartialVisitorResponseModel;
import com.example.walkin.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{
    private List<PartialVisitorResponseModel> listdata = new ArrayList<>();
    Context context;
    // RecyclerView recyclerView;
    public DetailAdapter(Context context) {
        this.context = context;
    }

    public void setListdata(List<PartialVisitorResponseModel> listdata) {
        this.listdata = listdata;
        notifyDataSetChanged();
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
        final PartialVisitorResponseModel detailListData = listdata.get(position);
        holder.tvName.setText(detailListData.getName());
        holder.tvObjective.setText(context.getString(R.string.contact_s, detailListData.getDepartment()));
        holder.tvIn.setText(Util.Companion.toDateFormat(detailListData.getCheckin_time()));
        if (detailListData.getCheckout_time().isEmpty()) {
            holder.tvOut.setText(context.getString(R.string.stay_in));
            holder.btnReprint.setVisibility(View.VISIBLE);
        } else {
            holder.btnReprint.setVisibility(View.GONE);
            holder.tvOut.setText(Util.Companion.toDateFormat(detailListData.getCheckout_time()));
        }

        holder.imageView1.setImageResource(0);
        holder.imageView2.setImageResource(0);
        for (ImageModel imageModel : detailListData.getImages()) {
            String type = imageModel.getType();
            if ("4".equals(type)) {
                Glide.with(context) //1
                        .load(imageModel.getUrl())
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .skipMemoryCache(true) //2
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                        .into(holder.imageView1);
            } else if ("2".equals(type)) {
                Glide.with(context) //1
                        .load(imageModel.getUrl())
                        .placeholder(R.drawable.ic_car)
                        .error(R.drawable.ic_car)
                        .skipMemoryCache(true) //2
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                        .into(holder.imageView2);
            }
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+ detailListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView1,imageView2;
        public TextView tvName, tvObjective, tvIn, tvOut;
        public RelativeLayout relativeLayout;
        public Button btnReprint;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView1 = (ImageView) itemView.findViewById(R.id.iv_1);
            this.imageView2 = (ImageView) itemView.findViewById(R.id.iv_2);
            this.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tvObjective = (TextView) itemView.findViewById(R.id.tv_objective);
            this.tvIn = (TextView) itemView.findViewById(R.id.tv_in);
            this.tvOut = (TextView) itemView.findViewById(R.id.tv_out);
            this.btnReprint = (Button) itemView.findViewById(R.id.btnReprint);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}