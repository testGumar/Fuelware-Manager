package com.fuelware.app.fw_manager.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuelware.app.fw_manager.BuildConfig;
import com.fuelware.app.fw_manager.appconst.Const;
import com.fuelware.app.fw_manager.R;
import com.fuelware.app.fw_manager.activities.receipts.EditReceiptActivity;
import com.fuelware.app.fw_manager.activities.receipts.ReceiptDetailActivity;
import com.fuelware.app.fw_manager.activities.receipts.ReceiptListActivity;
import com.fuelware.app.fw_manager.models.ReceiptModel;
import com.fuelware.app.fw_manager.network.MLog;
import com.fuelware.app.fw_manager.network.RxBus;
import com.fuelware.app.fw_manager.utils.MyUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListAdapter.MyViewHolder> {
    private List<ReceiptModel> records = new ArrayList<>();
    private List<ReceiptModel> origialRecords ;
    private Context mContext;

    public ReceiptListAdapter(List<ReceiptModel> records, Context mContext) {
        this.origialRecords = records;
        this.mContext = mContext;
        this.records.addAll(records);
    }

    public void refresh () {
        records.clear();
        records.addAll(origialRecords);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cash_receipt, parent, false);

        return new ReceiptListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptListAdapter.MyViewHolder holder, int position) {

        position = holder.getAdapterPosition();
        holder.tvSerailNumber.setText((position+1)+"");

        final ReceiptModel model = records.get(position);
        holder.tvBusinessName.setText(model.getBusiness());
        holder.tvReceiptNo.setText(model.getReceipt_number());
        holder.tvAmount.setText(MyUtils.formatCurrency(model.getAmount()));

        String receiptType = ((Activity)mContext).getIntent().getStringExtra(Const.RECEIPT_TYPE);
        if (receiptType.equals(Const.RECEIPT_CREDIT)
                || receiptType.equals(Const.RECEIPT_DEBIT)
                || receiptType.equals(Const.RECEIPT_LOYALTY)) {

            holder.imgDelete.setVisibility(View.GONE);
            holder.imgEdit.setVisibility(View.VISIBLE);
        } else {
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgEdit.setVisibility(View.GONE);
        }

        holder.tvGenerate.setOnClickListener(v -> {
            AndPermission.with(v.getContext())
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .onGranted(permissions -> {
                        MyUtils.downloadPDF(v.getContext(), BuildConfig.BASE_URL_2 + "print/manager-receipt/"+model.getToken());
                    })
                    .onDenied(permissions -> {
                        MLog.showToast(v.getContext(), "Read/Write External Storage permission denied!");
                    })
                    .start();

        });

        holder.imgDelete.setOnClickListener(v -> {
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_cofirm_action);
            TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            tvTitle.setText("Confirm Delete");
            tvMessage.setText("You will not be able to recover once deleted?");
            TextView btnNo = dialog.findViewById(R.id.btnNo);
            TextView btnYes = dialog.findViewById(R.id.btnYes);
            dialog.setCancelable(true);
            btnNo.setOnClickListener(w -> dialog.dismiss());
            btnYes.setOnClickListener(w -> {
                dialog.dismiss();
                ((ReceiptListActivity)mContext).deleteReceipt(model, holder.getAdapterPosition(), dialog);
            });
            dialog.show();
        });

        holder.imgEdit.setOnClickListener(v -> {
            v.getContext().startActivity(new Intent(v.getContext(), EditReceiptActivity.class)
                    .putExtra("CASH_RECEIPT_MODEL", model)
                    .putExtra(RxBus.POSITION, holder.getAdapterPosition())
                    .putExtra(Const.RECEIPT_TYPE, receiptType)
            );
        });

        holder.itemView.setOnClickListener(v -> {
            v.getContext().startActivity(new Intent(v.getContext(), ReceiptDetailActivity.class)
                    .putExtra("CASH_RECEIPT_MODEL", model)
                    .putExtra(Const.RECEIPT_TYPE, ((Activity)mContext).getIntent().getStringExtra(Const.RECEIPT_TYPE))
            );
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusinessName, tvReceiptNo, tvAmount, tvSerailNumber, tvGenerate;
        ImageView imgDelete, imgEdit;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSerailNumber = itemView.findViewById(R.id.tvSerailNumber);
            this.tvBusinessName = itemView.findViewById(R.id.etBusinessName);
            this.tvReceiptNo = itemView.findViewById(R.id.tvReceiptNo);
            this.tvAmount = itemView.findViewById(R.id.tvAmount);
            this.imgDelete = itemView.findViewById(R.id.imgDelete);
            this.imgEdit = itemView.findViewById(R.id.imgEdit);
            this.tvGenerate = itemView.findViewById(R.id.tvGenerate);

        }
    }

    public void filter (String newText) {
        newText = newText.trim().toLowerCase();
        records.clear();
        if (newText.isEmpty()) {
            records.addAll(origialRecords);
        } else {
            /*for (ReceiptModel item : origialRecords) {
                if (item.getVehicle_number().toLowerCase().startsWith(newText)) {
                    records.add(item);
                } else if (item.getIndent_number().toLowerCase().startsWith(newText)) {
                    records.add(item);
                }
            }*/
        }
        notifyDataSetChanged();
    }
}
