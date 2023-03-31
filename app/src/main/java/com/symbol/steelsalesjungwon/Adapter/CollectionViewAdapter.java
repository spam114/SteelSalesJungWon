package com.symbol.steelsalesjungwon.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.CollectionData;
import com.symbol.steelsalesjungwon.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

//가용재고가 표기되는 품목선택 리스트뷰를 구성할때 사용하는 어뎁터
public class CollectionViewAdapter extends ArrayAdapter<CollectionData> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    TextView txtSaleYYMM;
    //TextView txtLastMonthResultAmt;
    TextView txtSaleAmt;
    TextView txtCollectionAmt;
    TextView txtUnCollectionAmt;

   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    //private ArrayList<Stock> listViewItemList = new ArrayList<Stock>() ;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    int checkedQty=0;


    public CollectionViewAdapter(Context context, int layoutResourceID, ArrayList data) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.data=data;
        this.layoutRsourceId = layoutResourceID;
        //this.listViewItemList = data;
        //this.filteredItemList=listViewItemList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final CollectionData item = (CollectionData) data.get(position);
        if (item != null) {
            //row.setTag(item);
            txtSaleYYMM=row.findViewById(R.id.txtSaleYYMM);
            //txtLastMonthResultAmt=row.findViewById(R.id.txtLastMonthResultAmt);
            txtSaleAmt=row.findViewById(R.id.txtSaleAmt);
            txtCollectionAmt=row.findViewById(R.id.txtCollectionAmt);
            txtUnCollectionAmt=row.findViewById(R.id.txtUnCollectionAmt);

            txtSaleYYMM.setText(item.SaleYYMM);

            DecimalFormat myFormatter = new DecimalFormat("###,###");
            //String lastMonthResultAmt = myFormatter.format(Double.parseDouble(item.LastMonthResultAmt));
            String saleAmt = myFormatter.format(Double.parseDouble(item.SaleAmt));
            String collectionAmt = myFormatter.format(Double.parseDouble(item.CollectionAmt));
            String unCollectionAmt = myFormatter.format(Double.parseDouble(item.UnCollectionAmt));

            //txtLastMonthResultAmt.setText(lastMonthResultAmt);
            txtSaleAmt.setText(saleAmt);
            txtCollectionAmt.setText(collectionAmt);
            txtUnCollectionAmt.setText(unCollectionAmt);

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
        return position ;
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity)context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)context, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity)context, message, handler);
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
}

