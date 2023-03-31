package com.symbol.steelsalesjungwon.Activity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;

import com.symbol.steelsalesjungwon.Adapter.CollectionViewAdapter;
import com.symbol.steelsalesjungwon.Object.CollectionData;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

//가용재고가 표기되는 품목선택 액티비티
public class CollectionViewActivity extends BaseActivity {
    String customerCode;
    String customerName;
    String deptCode;
    ArrayList<CollectionData> collectionDataArrayList;
    //CharSequence[] partSpecNameSequences;
    CollectionViewAdapter collectionViewAdapter;
    ListView listview;
    TextView txtContent;
    int selectedIndex = 0;
    TextView txtLastMonthResultAmt;
    TextView txtSaleAmt;
    TextView txtCollectionAmt;
    TextView txtUncollectionAmt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_view);
        customerCode = getIntent().getStringExtra("customerCode");
        customerName = getIntent().getStringExtra("customerName");
        deptCode = getIntent().getStringExtra("deptCode");
        listview = findViewById(R.id.listview);
        txtContent = findViewById(R.id.txtContent);
        txtContent.setText("원대장 [" + customerName+"]");
        this.txtLastMonthResultAmt=findViewById(R.id.txtLastMonthResultAmt);
        this.txtSaleAmt=findViewById(R.id.txtSaleAmt);
        this.txtCollectionAmt=findViewById(R.id.txtCollectionAmt);
        this.txtUncollectionAmt=findViewById(R.id.txtUncollectionAmt);
        getCollectionDataByCustomerCode();

    }

    public void getCollectionDataByCustomerCode() {
        String url = getString(R.string.service_address) + "getCollectionDataByCustomerCode";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        values.put("CustomerCode", customerCode);
        values.put("DeptCode", "-1");//전체로 고정
        GetCollectionDataByCustomerCode gsod = new GetCollectionDataByCustomerCode(url, values);
        gsod.execute();
    }

    public class GetCollectionDataByCustomerCode extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCollectionDataByCustomerCode(String url, ContentValues values) {
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
                CollectionData collectionData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                collectionDataArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(CollectionViewActivity.this, ErrorCheck, 2);
                        return;
                    }
                    collectionData = new CollectionData();
                    collectionData.LastMonthResultAmt = child.getString("LastMonthResultAmt");
                    collectionData.SaleAmt = child.getString("SaleAmt");
                    collectionData.CollectionAmt = child.getString("CollectionAmt");
                    collectionData.UnCollectionAmt = child.getString("UnCollectionAmt");
                    //collectionData.CustomerCode = child.getString("CustomerCode");
                    //collectionData.CustomerName = child.getString("CustomerName");
                    collectionData.SaleYYMM = child.getString("SaleYYMM");

                    if(!collectionData.SaleYYMM.equals("계")){
                        collectionDataArrayList.add(collectionData);
                    }
                    else{//합계
                        txtLastMonthResultAmt.setText(myFormatter.format(Double.parseDouble(collectionData.LastMonthResultAmt)));
                        txtSaleAmt.setText(myFormatter.format(Double.parseDouble(collectionData.SaleAmt)));
                        txtCollectionAmt.setText(myFormatter.format(Double.parseDouble(collectionData.CollectionAmt)));
                        txtUncollectionAmt.setText(myFormatter.format(Double.parseDouble(collectionData.UnCollectionAmt)));

                    }
                }

                collectionViewAdapter = new CollectionViewAdapter
                        (CollectionViewActivity.this, R.layout.listview_collection_row, collectionDataArrayList);
                listview.setAdapter(collectionViewAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
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
