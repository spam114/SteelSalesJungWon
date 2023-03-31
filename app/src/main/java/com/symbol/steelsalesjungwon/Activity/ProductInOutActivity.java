package com.symbol.steelsalesjungwon.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Adapter.ProductInOutAdapter;
import com.symbol.steelsalesjungwon.Object.Stock;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//가용재고가 표기되는 품목선택 액티비티
public class ProductInOutActivity extends BaseActivity {

    ArrayList<String> partNameDic;//품명 검색을 위한 리스트
    ArrayList<String> partSpecNameDic;//규격명 검색을 위한 리스트
    CharSequence[] partNameSequences;
    Filter filter;//검색 필터
    ArrayList<Stock> stockArrayList;
    //CharSequence[] partSpecNameSequences;
    ProductInOutAdapter productInOutAdapter;
    ListView listview;
    TextView txtContent;
    int selectedIndex = 0;
    TextView txtPartName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_out);
        txtPartName = findViewById(R.id.txtPartName);
        listview = findViewById(R.id.listview);
        txtContent = findViewById(R.id.txtContent);
        txtContent.setText("가용재고");

        /*listview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
               // txtContent.setText("수불현황");
                if(i7!=0)
                    progressOFF2();
            }
        });*/

        txtPartName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialAlertDialogBuilder(ProductInOutActivity.this)
                        .setTitle("품명을 선택하세요")
                        .setSingleChoiceItems(partNameSequences, selectedIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedIndex = which;
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                //listview.setFilterText("SPP BPE");
                                filter = productInOutAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                                filter.filter(partNameSequences[selectedIndex]);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //listview.setFilterText("SPP BPE");
                                filter = productInOutAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                                filter.filter(partNameSequences[selectedIndex]);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        getProductInOutData();
    }

    public void getProductInOutData() {
        String url = getString(R.string.service_address) + "GetProductInOutData";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        GetProductInOutData gsod = new GetProductInOutData(url, values);
        gsod.execute();
    }

    public class GetProductInOutData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetProductInOutData(String url, ContentValues values) {
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

                stockArrayList = new ArrayList<>();
                partNameDic = new ArrayList<>();
                partSpecNameDic = new ArrayList<>();

                Stock stock;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                stockArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ProductInOutActivity.this, ErrorCheck, 2);
                        return;
                    }
                    stock = new Stock();
                    stock.PartCode = child.getString("PartCode");
                    stock.PartName = child.getString("PartName");
                    stock.PartSpec = child.getString("PartSpec");
                    stock.PartSpecName = child.getString("PartSpecName");
                    stock.Qty = child.getString("Qty");//가용재고
                    stock.OutQty = child.getString("OutQty");
                    stock.OutQtySeoul = child.getString("OutQtySeoul");
                    stock.OutQtyPusan = child.getString("OutQtyPusan");
                    stock.Minap = child.getString("Minap");
                    stock.MinapSeoul = child.getString("MinapSeoul");
                    stock.MinapPusan = child.getString("MinapPusan");
                    stockArrayList.add(stock);

                    if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);
                }

                partNameSequences = new CharSequence[partNameDic.size() + 1];
                partNameSequences[0] = "전체";
                for (int i = 1; i < partNameDic.size() + 1; i++) {
                    partNameSequences[i] = partNameDic.get(i - 1);
                }


                productInOutAdapter = new ProductInOutAdapter
                        (ProductInOutActivity.this, R.layout.listview_product_in_out_row, stockArrayList);
                listview.setAdapter(productInOutAdapter);

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
