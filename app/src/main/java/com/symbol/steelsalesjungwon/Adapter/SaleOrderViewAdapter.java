package com.symbol.steelsalesjungwon.Adapter;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Activity.SaleOrderActivity;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Fragment.FragmentViewSaleOrder;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.MainActivity2;
import com.symbol.steelsalesjungwon.Object.SaleOrder;
import com.symbol.steelsalesjungwon.Object.SaleOrderView;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SaleOrderViewAdapter extends ArrayAdapter<SaleOrderView> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    FragmentViewSaleOrder fragmentViewSaleOrder;
    String fromDate;
   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)


    public SaleOrderViewAdapter(Context context, int layoutResourceID, ArrayList data, FragmentViewSaleOrder fragmentViewSaleOrder, String fromDate) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.fragmentViewSaleOrder=fragmentViewSaleOrder;
        this.fromDate=fromDate;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final SaleOrderView item = (SaleOrderView) data.get(position);
        if (item != null) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            TextView txtSaleOrderNo = (TextView) row.findViewById(R.id.txtSaleOrderNo);
            TextView txtCustomerName = (TextView) row.findViewById(R.id.txtCustomerName);
            TextView txtEmplyoeeName = (TextView) row.findViewById(R.id.txtEmployeeName);
            TextView txtEmplyoeeName2 = (TextView) row.findViewById(R.id.txtEmployeeName2);
            TextView txtState = (TextView) row.findViewById(R.id.txtState);
            TextView txtAmount = row.findViewById(R.id.txtAmount);
            txtSaleOrderNo.setText(item.SaleOrderNo);
            txtCustomerName.setText(item.CustomerName + "(" + item.LocationName + ")");
            txtEmplyoeeName.setText(item.EmployeeName);
            txtEmplyoeeName2.setText(item.EmployeeName2);
            txtState.setText(item.State);
            if (item.State.equals("주문작성")) {
                txtAmount.setVisibility(View.GONE);
                txtState.setTextColor(Color.BLACK);
            } else if (item.State.equals("주문확정")) {
                txtAmount.setVisibility(View.VISIBLE);
                txtState.setTextColor(Color.parseColor("#FF006400"));
                //txtAmount.setTextColor(Color.parseColor("#FF006400"));
            } else if (item.State.equals("배차완료")) {
                txtAmount.setVisibility(View.VISIBLE);
                txtState.setTextColor(Color.parseColor("#FFA52A2A"));
                //txtAmount.setTextColor(Color.parseColor("#FFA52A2A"));
            } else {//출고완료
                txtAmount.setVisibility(View.VISIBLE);
                txtState.setTextColor(Color.BLUE);
                //txtAmount.setTextColor(Color.BLUE);
            }

            String strAmount = myFormatter.format(Double.parseDouble(item.Amount));
            String strWeight = myFormatter.format(Double.parseDouble(item.Weight));

            txtAmount.setText(strAmount+" ("+strWeight+")");


           /* TextView txtPartSpecName = (TextView) row.findViewById(R.id.txtPartSpecName);
            txtPartSpecName.setText(((Location) data.get(position)).PartSpecName);

            TextView txtQty = (TextView) row.findViewById(R.id.txtQty);
            txtQty.setText(String.format("%,d", Integer.parseInt(((Stock) data.get(position)).Qty)));*/

           /* if ((item.PartCode + "-" + item.PartSpec).equals(lastPart)) {//마지막 변경된 행 강조표시
                textViewPartName.setBackgroundColor(Color.YELLOW);
                textViewPartSpecName.setBackgroundColor(Color.YELLOW);
                layoutQty.setBackgroundColor(Color.YELLOW);
                this.lastPosition = position;
            }
            else{
                textViewPartName.setBackgroundColor(Color.TRANSPARENT);
                textViewPartSpecName.setBackgroundColor(Color.TRANSPARENT);
                layoutQty.setBackgroundColor(Color.TRANSPARENT);
            }*/

            ArrayList<SaleOrder> saleOrderArrayList = new ArrayList<>();

            Intent i = new Intent(getContext(), SaleOrderActivity.class);
            i.putExtra("customerCode", item.CustomerCode);
            i.putExtra("locationNo", item.LocationNo);
            i.putExtra("saleOrderNo", item.SaleOrderNo);
            i.putExtra("saleOrderArrayList", saleOrderArrayList);
            row.setOnClickListener(v -> {
                ((MainActivity2) (context)).startActivityResult.launch(i);
            });


            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                   /* if(!item.UserCode.equals(Users.UserID)){
                        return false;
                    }*/

                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("주문서 삭제")
                            .setMessage("주문번호: " + item.SaleOrderNo + "\n" +
                                    "거래처: " + item.CustomerName + "\n" + "주문서를 삭제하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteSalesOrderData(item.SaleOrderNo, position);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return false;
                }
            });


        }
        return row;
    }

    private void deleteSalesOrderData(String saleOrderNo, int position) {
        String url = context.getString(R.string.service_address) + "deleteSalesOrderData";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.UserID);
        values.put("SaleOrderNo", saleOrderNo);
        values.put("KMURL", context.getString(R.string.KMURL));
        values.put("SDBN", context.getString(R.string.SDBN));
        DeleteSalesOrderData gsod = new DeleteSalesOrderData(url, values, position);
        gsod.execute();
    }

    public class DeleteSalesOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        int position;

        DeleteSalesOrderData(String url, ContentValues values, int position) {
            this.url = url;
            this.values = values;
            this.position=position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                   /* if (!child.getString("FixDivision").equals("Y")) {//확정된 주문이어서 삭제 불가능시, 화면 Refresh
                        ErrorCheck = child.getString("ErrorCheck");
                        showErrorDialog(context, ErrorCheck, 2);
                        fragmentViewSaleOrder.getViewSaleOrderData(fromDate);
                        return;
                    }*/

                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        showErrorDialog(context, ErrorCheck, 2);
                        fragmentViewSaleOrder.getViewSaleOrderData(fromDate);
                        return;
                    }

                    //tempSaleOrderNo = child.getString("SaleOrderNo");
                }

                data.remove(position);
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
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

