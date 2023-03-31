package com.symbol.steelsalesjungwon.Activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.symbol.steelsalesjungwon.Adapter.MinapAdapter;
import com.symbol.steelsalesjungwon.Object.Minap;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

//가용재고가 표기되는 품목선택 액티비티
public class MinapActivity extends BaseActivity {

    ArrayList<String> partNameDic;//품명 검색을 위한 리스트
    ArrayList<String> partSpecNameDic;//규격명 검색을 위한 리스트
    CharSequence[] partNameSequences;
    Filter filter;//검색 필터
    ArrayList<Minap> minapList;
    //CharSequence[] partSpecNameSequences;
    MinapAdapter minapAdapter;
    ListView listview;
    //TextView txtContent;
    int selectedIndex = 0;
    TextView txtPartName;
    Spinner spinnerDept;
    TextView txtFromDate;
    TextView txtToDate;
    TextView txtTotalQty;
    TextView txtTotalWeight;

    public int fyear;
    public int fmonth;
    public int fdate;

    public int tyear;
    public int tmonth;
    public int tdate;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minap);
        txtPartName = findViewById(R.id.txtPartName);
        listview = findViewById(R.id.listview);
        //txtContent = findViewById(R.id.txtContent);
        spinnerDept = findViewById(R.id.spinnerDept);
        //txtContent.setText("미납현황");
        txtFromDate=findViewById(R.id.txtFromDate);
        txtToDate=findViewById(R.id.txtToDate);
        txtTotalQty=findViewById(R.id.txtTotalQty);
        txtTotalWeight=findViewById(R.id.txtTotalWeight);

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        final Calendar calendar2 = Calendar.getInstance();
        fyear = calendar.get(Calendar.YEAR);
        fmonth = calendar.get(Calendar.MONTH);
        fdate = calendar.get(Calendar.DATE);

        tyear = calendar2.get(Calendar.YEAR);
        tmonth = calendar2.get(Calendar.MONTH);
        tdate = calendar2.get(Calendar.DATE);

        txtFromDate.setText(fyear + "." + (fmonth + 1) + "." + fdate);
        txtToDate.setText(+tyear + "." + (tmonth + 1) + "." + tdate);

        txtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(fyear, fmonth, fdate);
            }
        });

        txtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker2(tyear, tmonth, tdate);
            }
        });

        ArrayList<String> deptArrayList = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < Users.deptArrayList.size(); i++) {
            deptArrayList.add(Users.deptArrayList.get(i).deptName);
            if (Users.DeptCode.equals(Users.deptArrayList.get(i).deptCode)) {
                index = Users.deptArrayList.get(i).index;
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, deptArrayList);

        spinnerDept.setAdapter(adapter);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        spinnerDept.setSelection(index);
        spinnerDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null){
                    if(position==0){
                        String fromDate=fyear+"-"+(fmonth+1)+"-"+fdate;
                        String toDate = tyear+"-"+(tmonth+1)+"-"+tdate;
                        getMinapData(fromDate, toDate);
                    }
                    return;
                }
                String fromDate=fyear+"-"+(fmonth+1)+"-"+fdate;
                String toDate = tyear+"-"+(tmonth+1)+"-"+tdate;
                getMinapData(fromDate, toDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String fromDate=fyear+"-"+(fmonth+1)+"-"+fdate;//부서선택시, 주석처리
        String toDate = tyear+"-"+(tmonth+1)+"-"+tdate;//부서선택시, 주석처리
        getMinapData(fromDate, toDate);//부서선택시, 주석처리
    }

    private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtFromDate.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                                fyear=year;
                                fmonth=monthOfYear;
                                fdate=dayOfMonth;
                                String fromDate=fyear+"-"+(fmonth+1)+"-"+fdate;
                                String toDate = tyear+"-"+(tmonth+1)+"-"+tdate;
                                //DATA가져오기
                                getMinapData(fromDate, toDate);

                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }

    private void showDateTimePicker2(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtToDate.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                                String fromDate=fyear+"-"+(fmonth+1)+"-"+fdate;
                                String toDate = tyear+"-"+(tmonth+1)+"-"+tdate;
                                //DATA가져오기
                                getMinapData(fromDate, toDate);

                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }

    public void getMinapData(String fromDate, String toDate) {
        String url = getString(R.string.service_address) + "getMinapData";
        ContentValues values = new ContentValues();

        values.put("BusinessClassCode", Users.BusinessClassCode);
        values.put("FromDate", fromDate);
        values.put("ToDate", toDate);

        int position=spinnerDept.getSelectedItemPosition();
        String deptCode="-1";

        /*for(int i=0;i<Users.deptArrayList.size();i++){
            if(Users.deptArrayList.get(i).index==position)
                deptCode=Users.deptArrayList.get(i).deptCode;
        }*/
        values.put("DeptCode", deptCode);
        GetMinapData gsod = new GetMinapData(url, values);
        gsod.execute();
    }

    public class GetMinapData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetMinapData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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

                minapList = new ArrayList<>();
                partNameDic = new ArrayList<>();
                partSpecNameDic = new ArrayList<>();
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                Minap minap;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String totalQty="0";
                String totalWeight="0";
                minapList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MinapActivity.this, ErrorCheck, 2);
                        return;
                    }
                    minap = new Minap();
                    minap.CustomerCode = child.getString("CustomerCode");
                    minap.CustomerName = child.getString("CustomerName");
                    minap.SaleOrderNo = child.getString("SaleOrderNo");
                    minap.OrderSerialNo = child.getString("OrderSerialNo");
                    minap.PartCode = child.getString("PartCode");
                    minap.PartName = child.getString("PartName");
                    minap.PartSpec = child.getString("PartSpec");
                    minap.PartSpecName = child.getString("PartSpecName");
                    minap.Remark1 = child.getString("Remark1");
                    minap.NotDeliveryQty = child.getString("NotDeliveryQty");
                    minap.NotDeliveryWeight = child.getString("NotDeliveryWeight");

                    if(minap.CustomerCode.equals("합계")){
                        totalQty=minap.NotDeliveryQty;
                        totalWeight=minap.NotDeliveryWeight;
                    }
                    else{
                        minapList.add(minap);
                    }



                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }
                String strQty = myFormatter.format(Double.parseDouble(totalQty));
                String strWeight = myFormatter.format(Double.parseDouble(totalWeight));
                txtTotalQty.setText(strQty);
                txtTotalWeight.setText(strWeight);
                /*partNameSequences = new CharSequence[partNameDic.size() + 1];
                partNameSequences[0] = "전체";
                for (int i = 1; i < partNameDic.size() + 1; i++) {
                    partNameSequences[i] = partNameDic.get(i - 1);
                }*/


                minapAdapter = new MinapAdapter
                        (MinapActivity.this, R.layout.listview_minap_row, minapList);
                listview.setAdapter(minapAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                //Log.i("순서확인", "미납/재고종료");
                progressOFF2(this.getClass().getName());
            }
        }
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

 /*   ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        saleOrderNo=result.getData().getStringExtra("saleOrderNo");
                        //Toast.makeText(SearchAvailablePartActivity.this, test, Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
}
