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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsalesjungwon.Adapter.CustomerLocationAdapter;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.HangulUtils;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.Location;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSaleOrder extends Fragment implements BaseActivityInterface {
    Context context;
    TextInputEditText edtSearch;
    ListView listview;
    ArrayList<Location> locationArrayList;
    CustomerLocationAdapter customerLocationAdapter;
    LinearLayout flayout;

    Button btnViewData;
    Button btnStock;

    public FragmentSaleOrder() {

    }

    public FragmentSaleOrder(Context context) {
        this.context = context;
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
        if(searchKeyword != null) {
            if (searchKeyword.length() == 0) {//검색 데이터 없을시
                //setLoadListData(oriList);
            } else {
                ArrayList<Location> temp = new ArrayList<>();
                for(Location i : Users.locationArrayList) {
                    boolean isAdd = false;
                    String iniName = HangulUtils.getHangulInitialSound(i.CustomerName, searchKeyword);
                    if (iniName.indexOf(searchKeyword) >= 0) {
                        isAdd = true;
                    }
                    if(isAdd) {
                        temp.add(i);
                    }
                }
                setSearchData(temp);
            }
        } else {
            //setLoadListData(oriList);
        }
    }

    private void setSearchData(ArrayList<Location> list) {
        //lay_noData.setVisibility(View.GONE);
       // tv_noSearch.setVisibility(View.GONE);
        if(list.size() == 0) {

        } else {
            //rv.setVisibility(View.VISIBLE);
            //customerLocationAdapter.swap(list);


            customerLocationAdapter= new CustomerLocationAdapter
                    (context, R.layout.listview_customerlocation_row, list,"주문관리");
            listview.setAdapter(customerLocationAdapter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout1, container, false);
        this.txtState = rootView.findViewById(R.id.txtState);
        this.edtSearch = rootView.findViewById(R.id.edtSearch);
        this.flayout =  rootView.findViewById(R.id.flayout);

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
                //getCustomerLocationBySearch();
                if(s.toString().equals("")){
                    if(start!=0 && before !=0 && count!=0)
                        getCustomerLocation(false);
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

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO
                    //getCustomerLocation();
                }
                return false;
            }
        });


        this.listview=rootView.findViewById(R.id.listview);
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
        getCustomerLocation(true);

        return rootView;
    }


    public void getCustomerLocation(boolean initStart) {
        String url = getString(R.string.service_address) + "getCustomerLocation";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        GetCustomerLocation gsod = new GetCustomerLocation(url, values, initStart);
        gsod.execute();
    }

    public class GetCustomerLocation extends AsyncTask<Void, Void, String> {
        boolean initStart;//최초실행
        String url;
        ContentValues values;

        GetCustomerLocation(String url, ContentValues values, boolean initStart) {
            this.url = url;
            this.values = values;
            this.initStart = initStart;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(initStart)
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
                Location location;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                locationArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    location = new Location();
                    location.LocationNo=child.getString("LocationNo");
                    location.LocationName=child.getString("LocationName");
                    location.CustomerCode=child.getString("CustomerCode");
                    location.CustomerName=child.getString("CustomerName");
                    locationArrayList.add(location);
                }
                customerLocationAdapter= new CustomerLocationAdapter
                        (context, R.layout.listview_customerlocation_row, locationArrayList,"주문관리");
                listview.setAdapter(customerLocationAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if(initStart)
                    progressOFF2("");
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
        ApplicationClass.getInstance().progressON((Activity)getContext(), message, handler);
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
        ApplicationClass.getInstance().HideKeyBoard((Activity)context);
    }

    public class SetPrintOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetPrintOrderData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등의 행위
            //startProgress();
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                    ErrorCheck = child.getString("ErrorCheck");
                    //Toast.makeText(getContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(context, ErrorCheck, 2);
                    return;
                }
                //Toast.makeText(getContext(), "출력이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                showErrorDialog(context, "출력이 완료 되었습니다.", 1);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //progressOFF2();
            }


        }
    }
}

