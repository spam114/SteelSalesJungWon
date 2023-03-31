package com.symbol.steelsalesjungwon.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.Stock;
import com.symbol.steelsalesjungwon.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

//가용재고가 표기되는 품목선택 리스트뷰를 구성할때 사용하는 어뎁터
public class ProductInOutAdapter extends ArrayAdapter<Stock> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    TextView txtPart;
    TextView txtPartSpec;
    TextView txtQty;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private ArrayList<Stock> listViewItemList = new ArrayList<Stock>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<Stock> filteredItemList;
    Filter listFilter;

    public ProductInOutAdapter(Context context, int layoutResourceID, ArrayList data) {
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

        final Stock item = (Stock) filteredItemList.get(position);
        if (item != null) {
            //row.setTag(item);
            txtPart = row.findViewById(R.id.txtPart);
            txtPartSpec = row.findViewById(R.id.txtPartSpec);
            txtQty = row.findViewById(R.id.txtQty);

            DecimalFormat myFormatter = new DecimalFormat("###,###");

            String strQty = myFormatter.format(Double.parseDouble(item.Qty));

            txtPart.setText(item.PartName);
            txtPartSpec.setText(item.PartSpecName);
            txtQty.setText(strQty);


            /*row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        checkedQty--;
                    }
                    else {
                        checkBox.setChecked(true);
                        checkedQty++;
                    }

                    //txtBadge.setText(Integer.toString(checkedQty));
                    //boolean newState=!item.checked;
                    //item.checked=newState;
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState=!item.checked;
                    item.checked=newState;
                }
            });
            checkBox.setChecked(item.checked);*/
        }
        return row;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Stock getItem(int position) {
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
                ArrayList<Stock> itemList = new ArrayList<Stock>();

                for (Stock item : listViewItemList) {
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
            filteredItemList = (ArrayList<Stock>) results.values;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}

