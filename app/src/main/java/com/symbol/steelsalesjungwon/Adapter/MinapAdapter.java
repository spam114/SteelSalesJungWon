package com.symbol.steelsalesjungwon.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.Minap;
import com.symbol.steelsalesjungwon.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MinapAdapter extends ArrayAdapter<Minap> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    TextView txtPart;
    TextView txtMinapQty;
    TextView txtMinapWeight;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private ArrayList<Minap> listViewItemList = new ArrayList<Minap>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<Minap> filteredItemList;
    Filter listFilter;

    public MinapAdapter(Context context, int layoutResourceID, ArrayList data) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.data = data;
        this.layoutRsourceId = layoutResourceID;
        this.listViewItemList = data;
        this.filteredItemList = listViewItemList;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final Minap item = (Minap) filteredItemList.get(position);
        if (item != null) {
            //row.setTag(item);
            txtPart = row.findViewById(R.id.txtPart);
            txtMinapQty = row.findViewById(R.id.txtMinapQty);
            txtMinapWeight = row.findViewById(R.id.txtMinapWeight);
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String strQty = myFormatter.format(Double.parseDouble(item.NotDeliveryQty));
            String strWeight = myFormatter.format(Double.parseDouble(item.NotDeliveryWeight));

            txtPart.setText(item.PartName + "(" + item.PartSpecName + ")");
            txtMinapQty.setText(strQty);
            txtMinapWeight.setText(strWeight);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("세부항목")
                            .setMessage("거래처: " + item.CustomerName + "\n" +
                                    "주문번호: " + item.SaleOrderNo + "\n" +
                                    "품명: " + item.PartName + "\n" +
                                    "규격: " + item.PartSpecName + "\n" +
                                    "미납수량: " + strQty+" EA" + "\n" +
                                    "미납중량: " + strWeight +" KG"+ "\n" +
                                    "비고1: " + item.Remark1
                            )
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                }
            });

            if (item.SaleOrderNo.equals("소계")) {
                row.setClickable(false);
                row.setBackgroundColor(Color.parseColor("#FFF0F8FF"));
                txtPart.setText(item.CustomerName + " 소계");
            } else {
                row.setClickable(true);
                row.setBackgroundColor(Color.TRANSPARENT);
            }

            if (item.SaleOrderNo.equals("명칭")) {
                row.setClickable(false);
                txtPart.setText(item.CustomerName);
                txtMinapQty.setText("");
                txtMinapWeight.setText("");
            }

        }
        return row;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Minap getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity) context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity) context, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) context, message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }

    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

    @Override
    public void HideKeyBoard(Context context) {

    }

    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 10000);
        progressON("Loading...", handler);
    }


    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter;
    }


    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            } else {
                ArrayList<Minap> itemList = new ArrayList<Minap>();

                for (Minap item : listViewItemList) {
                    if (constraint.toString().equals("전체")) {
                        itemList.add(item);
                    } else {
                        if (item.PartName.toUpperCase().equals(constraint.toString().toUpperCase())) {
                       /* if (item.PartName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase()))*/
                            itemList.add(item);
                        }
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<Minap>) results.values;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
