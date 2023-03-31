package com.symbol.steelsalesjungwon.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Adapter.AvailablePartAdapter;
import com.symbol.steelsalesjungwon.Object.SaleOrder;
import com.symbol.steelsalesjungwon.Object.Stock;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//가용재고가 표기되는 품목선택 액티비티
public class SearchAvailablePartActivity extends BaseActivity {
    ArrayList<Stock> stockArrayList;
    ArrayList<String> partNameDic;//품명 검색을 위한 리스트
    ArrayList<String> partSpecNameDic;//규격명 검색을 위한 리스트
    CharSequence[] partNameSequences;
    //CharSequence[] partSpecNameSequences;
    TextView txtBadge;
    AvailablePartAdapter availablePartAdapter;
    ListView listview;
    TextView txtPartName;
    TextView txtPartSpecName;
    TextView txtContent;
    Filter filter;//검색 필터
    int selectedIndex = 0;
    String locationNo;
    String locationName;
    String customerCode;
    String customerName;
    RelativeLayout badge_layout;
    String saleOrderNo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_available_part);
        stockArrayList=new ArrayList<>();
        listview = findViewById(R.id.listview);
        txtPartName = findViewById(R.id.txtPartName);
        txtPartSpecName = findViewById(R.id.txtPartSpecName);
        txtContent = findViewById(R.id.txtContent);
        txtBadge = findViewById(R.id.txtBadge);
        locationNo = getIntent().getStringExtra("locationNo");
        locationName = getIntent().getStringExtra("locationName");
        customerCode = getIntent().getStringExtra("customerCode");
        customerName = getIntent().getStringExtra("customerName");
        txtContent.setText(customerName+" ("+locationName+")");
        badge_layout = findViewById(R.id.badge_layout);
        saleOrderNo="";

        txtBadge.setVisibility(View.INVISIBLE);
        txtBadge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("0"))
                    txtBadge.setVisibility(View.VISIBLE);
                else
                    txtBadge.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPartName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialAlertDialogBuilder(SearchAvailablePartActivity.this)
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
                                filter = availablePartAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                                filter.filter(partNameSequences[selectedIndex]);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //listview.setFilterText("SPP BPE");
                                filter = availablePartAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                                filter.filter(partNameSequences[selectedIndex]);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        badge_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<SaleOrder> saleOrderArrayList=new ArrayList<>();

                for(int i=0;i<stockArrayList.size();i++){
                    Stock stock=stockArrayList.get(i);
                    SaleOrder saleOrder= new SaleOrder();
                    if(stock.checked){
                        saleOrder.isDBSaved=false;
                        saleOrder.saleOrderNo="";
                        saleOrder.index=-1;
                        saleOrder.partCode=stock.PartCode;
                        saleOrder.partName=stock.PartName;
                        saleOrder.partSpec=stock.PartSpec;
                        saleOrder.partSpecName=stock.PartSpecName;
                        saleOrder.marketPrice=stock.MarketPrice;
                        saleOrder.isChanged=true;
                        saleOrder.logicalWeight = Double.parseDouble(stock.Weight);
                        saleOrder.stockQty = Double.parseDouble(stock.Qty);
                        saleOrderArrayList.add(saleOrder);
                    }
                }

                if(saleOrderArrayList.size()==0){
                    Toast.makeText(SearchAvailablePartActivity.this, "품목을 하나 이상 선택하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SearchAvailablePartActivity.this, SaleOrderActivity.class);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("locationNo",locationNo);
                intent.putExtra("saleOrderNo",saleOrderNo);
                intent.putExtra("saleOrderArrayList", saleOrderArrayList);
                //intent.putExtra()
                startActivityResult.launch(intent);
                finish();
            }
        });

        getAvailableStock();

    }

    public void getAvailableStock() {
        String url = getString(R.string.service_address) + "getAvailableStock";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        GetAvailableStock gsod = new GetAvailableStock(url, values);
        gsod.execute();
    }

    public class GetAvailableStock extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetAvailableStock(String url, ContentValues values) {
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
                Stock stock;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                stockArrayList = new ArrayList<>();
                partNameDic = new ArrayList<>();
                partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SearchAvailablePartActivity.this, ErrorCheck, 2);
                        return;
                    }
                    stock = new Stock();
                    stock.PartCode = child.getString("PartCode");
                    stock.PartName = child.getString("PartName");
                    stock.PartSpec = child.getString("PartSpec");
                    stock.PartSpecName = child.getString("PartSpecName");
                    stock.Qty = child.getString("Qty");
                    stock.MarketPrice = child.getString("MarketPrice");
                    stock.Weight = child.getString("Weight");
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

                availablePartAdapter = new AvailablePartAdapter
                        (SearchAvailablePartActivity.this, R.layout.listview_available_part_row, stockArrayList, txtBadge);
                listview.setAdapter(availablePartAdapter);

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

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        saleOrderNo=result.getData().getStringExtra("saleOrderNo");
                        //Toast.makeText(SearchAvailablePartActivity.this, test, Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
