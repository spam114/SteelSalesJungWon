package com.symbol.steelsalesjungwon.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsalesjungwon.Adapter.CustomerCollectionAdapter;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.HangulUtils;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.CollectionData;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentViewCollection extends Fragment implements BaseActivityInterface {
    Context context;
    TextInputEditText edtSearch;
    ListView listview;
    ArrayList<CollectionData> collectionDataArrayList;
    CustomerCollectionAdapter customerCollectionAdapter;
    LinearLayout flayout;
    Spinner spinnerDept;
    Button btnViewData;
    Button btnStock;
    TextView txtTotalAmount;

    public int tyear;
    public int tmonth;
    public int tdate;

    public FragmentViewCollection() {

    }

    public FragmentViewCollection(Context context, TextView txtTotalAmount) {
        this.context = context;
        this.txtTotalAmount=txtTotalAmount;
    }

    private final int REQUEST_STOCKOUT = 1;
    TextView txtState;

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

    public void setChangeListData(String searchKeyword) {
        if (searchKeyword != null) {
            if (searchKeyword.length() == 0) {//검색 데이터 없을시
                //setLoadListData(oriList);
            } else {
                ArrayList<CollectionData> temp = new ArrayList<>();
                for (CollectionData i : collectionDataArrayList) {
                    boolean isAdd = false;
                    String iniName = HangulUtils.getHangulInitialSound(i.CustomerName, searchKeyword);
                    if (iniName.indexOf(searchKeyword) >= 0) {
                        isAdd = true;
                    }
                    if (isAdd) {
                        temp.add(i);
                    }
                }
                setSearchData(temp);
            }
        } else {
            //setLoadListData(oriList);
        }
    }

    private void setSearchData(ArrayList<CollectionData> list) {
        //lay_noData.setVisibility(View.GONE);
        // tv_noSearch.setVisibility(View.GONE);
        if (list.size() == 0) {

        } else {
            int position = spinnerDept.getSelectedItemPosition();
            String deptCode = "-1";
            for (int i = 0; i < Users.deptArrayList.size(); i++) {
                if (Users.deptArrayList.get(i).index == position)
                    deptCode = Users.deptArrayList.get(i).deptCode;
            }

            customerCollectionAdapter = new CustomerCollectionAdapter
                    (context, R.layout.listview_customercollection_row, list, "미수금현황", deptCode);
            listview.setAdapter(customerCollectionAdapter);
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            double totalAmt=0;
            for(int i=0;i<list.size();i++){
                totalAmt+=Double.parseDouble(list.get(i).UnCollectionAmt);
            }
            String strTotalAmount = myFormatter.format(totalAmt);
            txtTotalAmount.setText(strTotalAmount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout3, container, false);
        this.txtState = rootView.findViewById(R.id.txtState);
        this.edtSearch = rootView.findViewById(R.id.edtSearch);
        flayout = rootView.findViewById(R.id.flayout);

        final Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);

        this.spinnerDept = rootView.findViewById(R.id.spinnerDept);
        ArrayList<String> deptArrayList = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < Users.deptArrayList.size(); i++) {
            deptArrayList.add(Users.deptArrayList.get(i).deptName);
            if (Users.DeptCode.equals(Users.deptArrayList.get(i).deptCode)) {
                index = Users.deptArrayList.get(i).index;
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
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
                        getCollectionData();
                    }
                    return;
                }
                getCollectionData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if(hasFocus){
                    edtSearch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                }
                else{
                    edtSearch.setGravity(Gravity.CENTER_HORIZONTAL);
                }*/
                edtSearch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
        });

        this.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && before == 0 && count == 0)
                    return;
                if (s.toString().equals("")) {
                    getCollectionData();
                }
                setChangeListData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {//확인버튼
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) { // IME_ACTION_SEARCH , IME_ACTION_GO
                    //getCustomerLocation();
                }
                return false;
            }
        });


        this.listview = rootView.findViewById(R.id.listview);
        this.listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtSearch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                //edtSearch.clearFocus();
                flayout.requestFocus();
                HideKeyBoard(context);
                return false;
            }
        });
        //this.edtScan.setText("2105110001");
        //this.stockOutDetailArrayList = new ArrayList<>();
        // this.scanDataArrayList = new ArrayList<>();
       /* try {
            txtVersion.setText("version "+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        getCollectionData();//부서선택시, 주석처리

        return rootView;
    }


    public void getCollectionData() {
        this.txtTotalAmount.setText("");
        collectionDataArrayList = new ArrayList<>();

        int position = spinnerDept.getSelectedItemPosition();
        String deptCode = "-1";
        /*for (int i = 0; i < Users.deptArrayList.size(); i++) {
            if (Users.deptArrayList.get(i).index == position)
                deptCode = Users.deptArrayList.get(i).deptCode;
        }*/

        customerCollectionAdapter = new CustomerCollectionAdapter
                (context, R.layout.listview_customercollection_row, collectionDataArrayList, "미수금현황", deptCode);
        customerCollectionAdapter.notifyDataSetChanged();
        listview.setAdapter(customerCollectionAdapter);
        String url = getString(R.string.service_address) + "getCollectionData";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        values.put("DeptCode", "-1");//전체로 고정

        GetCollectionData gsod = new GetCollectionData(url, values);
        gsod.execute();
    }

    public class GetCollectionData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCollectionData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.i("순서확인", "미수금현황시작");
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
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                CollectionData collectionData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                collectionDataArrayList = new ArrayList<>();

                int position = spinnerDept.getSelectedItemPosition();
                String deptCode = "-1";
                for (int i = 0; i < Users.deptArrayList.size(); i++) {
                    if (Users.deptArrayList.get(i).index == position)
                        deptCode = Users.deptArrayList.get(i).deptCode;
                }
                double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck, 2);
                        return;
                    }
                    collectionData = new CollectionData();
                    collectionData.CustomerCode = child.getString("CustomerCode");
                    collectionData.CustomerName = child.getString("CustomerName");
                    collectionData.UnCollectionAmt = child.getString("UnCollectionAmt");
                    totalAmt+=Double.parseDouble(collectionData.UnCollectionAmt);
                    collectionDataArrayList.add(collectionData);
                }
                customerCollectionAdapter = new CustomerCollectionAdapter
                        (context, R.layout.listview_customercollection_row, collectionDataArrayList, "미수금현황", deptCode);
                listview.setAdapter(customerCollectionAdapter);
                String strTotalAmount = myFormatter.format(totalAmt);
                txtTotalAmount.setText(strTotalAmount);
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                //Log.i("순서확인", "미수금현황종료");
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
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }
}

