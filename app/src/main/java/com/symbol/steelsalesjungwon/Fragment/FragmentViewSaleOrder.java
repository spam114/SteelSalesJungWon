package com.symbol.steelsalesjungwon.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.symbol.steelsalesjungwon.Adapter.SaleOrderViewAdapter;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.SaleOrderView;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentViewSaleOrder extends Fragment implements BaseActivityInterface {
    Context context;
    //TextInputEditText edtSearch;
    ListView listview;
    ArrayList<SaleOrderView> saleOrderViewArrayList;
    SaleOrderViewAdapter saleOrderViewAdapter;
    TextView txtFromDate;
    Spinner spinnerDept;

    public int tyear;
    public int tmonth;
    public int tdate;

    TextView txtTotalAmount;
    TextView txtTotalWeight;

    public FragmentViewSaleOrder() {

    }

    public FragmentViewSaleOrder(Context context, TextView txtTotalAmount, TextView txtTotalWeight) {
        this.context = context;
        this.txtTotalAmount=txtTotalAmount;
        this.txtTotalWeight=txtTotalWeight;
    }

    private final int REQUEST_STOCKOUT = 1;

    //ArrayList<StockOutDetail> stockOutDetailArrayList;
    //ArrayList<StockOutDetail> scanDataArrayList;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout2, container, false);
        this.txtFromDate = rootView.findViewById(R.id.txtFromDate);
        final Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);

        this.txtFromDate.setText("[ "+tyear + "-" + (tmonth + 1) + "-" + tdate+" ]");
        this.txtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(tyear, tmonth, tdate);
            }
        });

        this.listview = rootView.findViewById(R.id.listview);
        this.spinnerDept=rootView.findViewById(R.id.spinnerDept);

        ArrayList<String> deptArrayList=new ArrayList<>();
        int index=0;
        for(int i=0;i<Users.deptArrayList.size();i++){
            deptArrayList.add(Users.deptArrayList.get(i).deptName);
            if(Users.DeptCode.equals(Users.deptArrayList.get(i).deptCode)){
                index = Users.deptArrayList.get(i).index;
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, deptArrayList);

        spinnerDept.setAdapter(adapter);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        spinnerDept.setSelection(index);
        spinnerDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*String locationNo = locationMap.get(position).LocationNo;
                getCoilStock(locationNo, "");*/
                if(view==null){
                    if(position==0){
                        String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                        getViewSaleOrderData(fromDate);
                    }
                    return;
                }
                String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                getViewSaleOrderData(fromDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;//부서선택시, 주석처리
        getViewSaleOrderData(fromDate);//부서선택시, 주석처리

        return rootView;
    }

    private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtFromDate.setText("[ "+year + "-" + (monthOfYear + 1) + "-" + dayOfMonth+" ]");
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                                String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                                //DATA가져오기
                                getViewSaleOrderData(fromDate);

                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }

    public void getViewSaleOrderData(String fromDate) {
        String url = getString(R.string.service_address) + "getViewSaleOrderData";
        ContentValues values = new ContentValues();
        values.put("FromDate", fromDate);
        values.put("BusinessClassCode", Users.BusinessClassCode);

        int position=spinnerDept.getSelectedItemPosition();
        String deptCode="-1";

        /*for(int i=0;i<Users.deptArrayList.size();i++){
            if(Users.deptArrayList.get(i).index==position)
                deptCode=Users.deptArrayList.get(i).deptCode;
        }*/

        values.put("DeptCode", "-1");//전체로 고정

        if (Users.authorityList.contains(2))//대리점 권한이 있으면, 본인이 작성한 주문서만 VIEW
            values.put("UserID", Users.UserID);
        else
            values.put("UserID", "");//전체 주문서 VIEW



        GetViewSaleOrderData gsod = new GetViewSaleOrderData(url, values);
        gsod.execute();
    }

    public class GetViewSaleOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetViewSaleOrderData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
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
                SaleOrderView saleOrderView;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                saleOrderViewArrayList = new ArrayList<>();
                String totalAmount="";
                String totalWeight="";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck, 2);
                        return;
                    }
                    if(!child.getString("SaleOrderNo").equals("Total")){
                        saleOrderView = new SaleOrderView();
                        saleOrderView.SaleOrderNo = child.getString("SaleOrderNo");
                        saleOrderView.LocationNo = child.getString("LocationNo");
                        saleOrderView.LocationName = child.getString("LocationName");
                        saleOrderView.CustomerCode = child.getString("CustomerCode");
                        saleOrderView.CustomerName = child.getString("CustomerName");
                        saleOrderView.EmployeeName = child.getString("EmployeeName");
                        saleOrderView.EmployeeName2 = child.getString("EmployeeName2");
                        saleOrderView.State = child.getString("State");
                        saleOrderView.UserCode = child.getString("UserID");
                        saleOrderView.Amount = child.getString("Amount");
                        saleOrderView.Weight = child.getString("Weight");
                        saleOrderViewArrayList.add(saleOrderView);
                    }
                    else{
                        DecimalFormat myFormatter = new DecimalFormat("###,###");

                        totalAmount = child.getString("Amount");
                        totalWeight = child.getString("Weight");

                        String strAmount = myFormatter.format(Double.parseDouble(totalAmount));
                        String strWeight = myFormatter.format(Double.parseDouble(totalWeight));

                        txtTotalAmount.setText(strAmount+" 원");
                        txtTotalWeight.setText(strWeight+" KG");
                    }
                }

                String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                saleOrderViewAdapter = new SaleOrderViewAdapter
                        (context, R.layout.listview_view_saleorder_row,saleOrderViewArrayList, FragmentViewSaleOrder.this, fromDate);
                listview.setAdapter(saleOrderViewAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    /**
     * 버튼 클릭
     */
    public void mOnClick(View v) {

        switch (v.getId()) {

           /* case R.id.imvPrint:
                String _url=getString(R.string.service_address) + "setPrintOrderData";
                ContentValues _values = new ContentValues();
                _values.put("PCCode", "201");
                _values.put("PrintDivision", "2");
                _values.put("PintNo", "KP-2012260050");
                _values.put("InsertId", Users.PhoneNumber);
                SetPrintOrderData sod = new SetPrintOrderData(_url, _values);
                sod.execute();


                break;

            case R.id.btnTest:
                String url=getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", "E3-2102230002");
                GetStockOutDetailAndScanData gsod = new GetStockOutDetailAndScanData(url, values);
                gsod.execute();
                break;*/
        }
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity) getContext(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity) getContext(), message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) getContext(), message, handler);
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
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }
}

