package com.example.walkin.cyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
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
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.printer.PrinterParams;
import com.example.walkin.R;
import com.example.walkin.cyp.models.ImageModel;
import com.example.walkin.cyp.models.PartialVisitorResponseModel;
import com.example.walkin.cyp.models.SignatureModel;
import com.example.walkin.cyp.models.VisitorResponseModel;
import com.example.walkin.cyp.models.WalkInErrorModel;
import com.example.walkin.cyp.utils.NetworkUtil;
import com.example.walkin.cyp.utils.PreferenceUtils;
import com.example.walkin.cyp.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{
    private List<PartialVisitorResponseModel> listdata = new ArrayList<>();
    Context context;
    private AidlPrinter printDev = null;

    // RecyclerView recyclerView;
    public DetailAdapter(Context context) {
        this.context = context;
    }

    public void setListdata(List<PartialVisitorResponseModel> listdata) {
        this.listdata = listdata;
        notifyDataSetChanged();
    }

    public void setPrinter(AidlPrinter printDev) {
        this.printDev = printDev;
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
            holder.btnReprint.setTag(detailListData.getContact_code());
            holder.btnReprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String code = view.getTag().toString();
                    search(code);
                }
            });
        } else {
            holder.btnReprint.setVisibility(View.GONE);
            holder.tvOut.setText(Util.Companion.toDateFormat(detailListData.getCheckout_time()));
        }

        holder.imageView1.setImageResource(0);
        holder.imageView2.setImageResource(0);
        for (ImageModel imageModel : detailListData.getImages()) {
            String type = imageModel.getType();
            if ("1".equals(type) && !imageModel.getUrl().isEmpty()) {
                    Glide.with(context) //1
                        .load(imageModel.getUrl())
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .skipMemoryCache(true) //2
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                        .into(holder.imageView1);
            } else if ("2".equals(type) && !imageModel.getUrl().isEmpty()) {
                Glide.with(context) //1
                        .load(imageModel.getUrl())
                        .placeholder(R.drawable.ic_car)
                        .error(R.drawable.ic_car)
                        .skipMemoryCache(true) //2
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                        .into(holder.imageView2);
            } else if ("4".equals(type) && !imageModel.getUrl().isEmpty()) {
                Glide.with(context) //1
                        .load(imageModel.getUrl())
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .skipMemoryCache(true) //2
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                        .into(holder.imageView1);
            }
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+ detailListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void search(final String code) {
        NetworkUtil.Companion.searchByOrder(code, new NetworkUtil.Companion.NetworkLisener<VisitorResponseModel>() {
            @Override
            public void onResponse(VisitorResponseModel response) {
                print(response);
            }

            @Override
            public void onError(@NotNull WalkInErrorModel errorModel) {
                Log.e("error", errorModel.getMsg());
            }

            @Override
            public void onExpired() {
                search(code);
            }
        }, VisitorResponseModel.class);
    }

    private void print(VisitorResponseModel data) {
        try {
            Bitmap bitmap = Util.Companion.createImageFromQRCode(data.getContact_code());
            List<SignatureModel> signature = PreferenceUtils.getSignature();

            List<PrinterParams> textList = new ArrayList<PrinterParams>();
            PrinterParams printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.CENTER);
            printerParams.setDataType(PrinterParams.DATATYPE.IMAGE);
            printerParams.setLineHeight(200);
            printerParams.setBitmap(PreferenceUtils.getBitmapLogo());
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\n\nบริษัท : " + PreferenceUtils.getCompanyName().replace(" ", " "));
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nชื่อ-นามสกุล : " + data.name().replace(" ", " "));
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nเลขบัตรประขาชน : " + data.getIdcard());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nทะเบียนรถ : " + data.getVehicle_id());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nจากบริษัท : " + data.getFrom());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nผู้ที่ขอพบ : " + data.getPerson_contact());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nต่อต่อแผนก : " + data.getDepartment());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nวัตถุประสงค์ : " + data.getObjective_type().replace(" ", " "));
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nอุณหภูมิ : " + data.getTemperature());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.LEFT);
            printerParams.setTextSize(24);
            printerParams.setText("\nเวลาเข้า : " + data.getCheckin_time());
            printerParams.setBold(true);
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.CENTER);
            printerParams.setDataType(PrinterParams.DATATYPE.IMAGE);
            printerParams.setLineHeight(200);
            printerParams.setBitmap(bitmap);
            textList.add(printerParams);
            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.CENTER);
            printerParams.setTextSize(24);
            printerParams.setText(data.getContact_code());
            textList.add(printerParams);

            for (int i = 0;i<signature.size();i++){
                printerParams = new PrinterParams();
                printerParams.setAlign(PrinterParams.ALIGN.CENTER);
                printerParams.setTextSize(24);
                printerParams.setText("\n\n\n____________________________");
                textList.add(printerParams);
                printerParams = new PrinterParams();
                printerParams.setAlign(PrinterParams.ALIGN.CENTER);
                printerParams.setTextSize(24);
                printerParams.setText("\n" + signature.get(i).getname());
                textList.add(printerParams);
            }

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.CENTER);
            printerParams.setTextSize(24);
            printerParams.setText("\n\n" + PreferenceUtils.getCompanyNote());
            textList.add(printerParams);

            printerParams = new PrinterParams();
            printerParams.setAlign(PrinterParams.ALIGN.CENTER);
            printerParams.setTextSize(24);
            printerParams.setText("\n\n\n\n\n");
            textList.add(printerParams);

            printDev.printDatas(textList, new AidlPrinterStateChangeListener.Stub() {
                @Override
                public void onPrintFinish() throws RemoteException {
                    Log.e("panya", "onPrintFinish");
                }

                @Override
                public void onPrintError(int i) throws RemoteException {
                    Log.e("panya", "onPrintError");
                }

                @Override
                public void onPrintOutOfPaper() throws RemoteException {
                    Log.e("panya", "onPrintOutOfPaper");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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