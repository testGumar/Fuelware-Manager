package com.fuelware.app.fw_manager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuelware.app.fw_manager.R;
import com.fuelware.app.fw_manager.appconst.AppConst;
import com.fuelware.app.fw_manager.models.BatchReport;
import com.fuelware.app.fw_manager.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashierReportAdapter extends RecyclerView.Adapter<CashierReportAdapter.MyViewHolder> {

    private List<BatchReport> records = new ArrayList<>();
    private List<BatchReport> origialRecords ;
    private Context mContext;

    public CashierReportAdapter(List<BatchReport> records, Context mContext) {
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
    public CashierReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cashier_report, parent, false);

        return new CashierReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashierReportAdapter.MyViewHolder holder, int position) {

        position = holder.getAdapterPosition();
        holder.tvSerailNumber.setText((position+1)+"");
        final BatchReport model = records.get(position);
        holder.tvBatchID.setText(model.batch_number.trim());
        holder.tvCashierName.setText(model.user.data.first_name+" "+model.user.data.last_name);

        holder.tvInTime.setText(MyUtils.dateToString(AppConst.SERVER_DATE_TIME_FORMAT, AppConst.APP_DATE_TIME_FORMAT, model.start_time));
        holder.tvOutTime.setText(MyUtils.dateToString(AppConst.SERVER_DATE_TIME_FORMAT, AppConst.APP_DATE_TIME_FORMAT, model.end_time));

        holder.tvEindent.setText(MyUtils.formatCurrency(model.produce.income.mode.e_indent));
        holder.tvMindent.setText(MyUtils.formatCurrency(model.produce.income.mode.manual_indent));

        double total = MyUtils.parseDouble(model.produce.income.mode.e_indent) +
                MyUtils.parseDouble(model.produce.income.mode.manual_indent);
        holder.tvTotal.setText(MyUtils.formatCurrency(total));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvSerailNumber)
        TextView tvSerailNumber;

        @BindView(R.id.tvBatchID)
        TextView tvBatchID;
        @BindView(R.id.tvCashierName)
        TextView tvCashierName;
        @BindView(R.id.tvInTime)
        TextView tvInTime;
        @BindView(R.id.tvOutTime)
        TextView tvOutTime;

        @BindView(R.id.tvEindent)
        TextView tvEindent;
        @BindView(R.id.tvMindent)
        TextView tvMindent;
        @BindView(R.id.tvTotal)
        TextView tvTotal;

        @BindView(R.id.linlayBindent)
        LinearLayout linlayBindent;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            linlayBindent.setVisibility(View.GONE);
        }
    }

    public void filter (String newText) {
        newText = newText.trim().toLowerCase();
        records.clear();
        if (newText.isEmpty()) {
            records.addAll(origialRecords);
        } else {
            for (BatchReport item : origialRecords) {
                if (item.user.data.first_name.toLowerCase().startsWith(newText)) {
                    records.add(item);
                } else if (item.user.data.last_name.toLowerCase().startsWith(newText)) {
                    records.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
