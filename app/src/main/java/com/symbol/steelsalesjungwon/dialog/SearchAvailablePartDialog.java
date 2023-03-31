package com.symbol.steelsalesjungwon.dialog;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Activity.SaleOrderActivity;
import com.symbol.steelsalesjungwon.Adapter.AvailablePartAdapter;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.SaleOrder;
import com.symbol.steelsalesjungwon.Object.Stock;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchAvailablePartDialog extends DialogFragment implements BaseActivityInterface {

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
    ArrayList<SaleOrder> exOrderArrayList;//주문서에 추가되어있는 품목들
    RelativeLayout badge_layout;
    String saleOrderNo;
    Button btnSave;
    Button btnCancel;

    public SearchAvailablePartDialog(ArrayList<SaleOrder> exOrderArrayList) {
        this.exOrderArrayList = exOrderArrayList;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAvailableStock();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //startProgress();
        View view = inflater.inflate(R.layout.dialog_search_available_part, container, false);
        //getDialog().setTitle("Sample");
        stockArrayList=new ArrayList<>();
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        listview = view.findViewById(R.id.listview);
        txtPartName = view.findViewById(R.id.txtPartName);
        txtPartSpecName = view.findViewById(R.id.txtPartSpecName);
        txtContent = view.findViewById(R.id.txtContent);
        txtBadge = view.findViewById(R.id.txtBadge);
        /* locationNo = getIntent().getStringExtra("locationNo");
        locationName = getIntent().getStringExtra("locationName");
        customerCode = getIntent().getStringExtra("customerCode");
        customerName = getIntent().getStringExtra("customerName");*/
        //txtContent.setText("거래처: " + customerName);
        badge_layout = view.findViewById(R.id.badge_layout);
        saleOrderNo = "";
        //txtContent.setText("거래처: " + customerName);

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


                new MaterialAlertDialogBuilder(getContext())
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
                addSaleOrderList();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSaleOrderList();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Button doneBtn = (Button) mView.findViewById(R.id.done_convert);
        return view;
    }

    public void addSaleOrderList() {
        ArrayList<SaleOrder> saleOrderArrayList = new ArrayList<>();

        for (int i = 0; i < stockArrayList.size(); i++) {


            Stock stock = stockArrayList.get(i);
            SaleOrder saleOrder = new SaleOrder();
            if (stock.checked) {
                saleOrder.isDBSaved = false;
                saleOrder.saleOrderNo = "";
                saleOrder.index = -1;
                saleOrder.partCode = stock.PartCode;
                saleOrder.partName = stock.PartName;
                saleOrder.partSpec = stock.PartSpec;
                saleOrder.partSpecName = stock.PartSpecName;
                saleOrder.marketPrice = stock.MarketPrice;
                saleOrder.logicalWeight = Double.parseDouble(stock.Weight);
                saleOrder.stockQty = Double.parseDouble(stock.Qty);
                saleOrderArrayList.add(saleOrder);
            }
        }
        for (int i=0;i<saleOrderArrayList.size();i++){
            for (int j=0;j<exOrderArrayList.size();j++){
                if(saleOrderArrayList.get(i).partCode.equals(exOrderArrayList.get(j).partCode) && saleOrderArrayList.get(i).partSpec.equals(exOrderArrayList.get(j).partSpec)){
                    Toast.makeText(getContext(), "주문서에 존재하는 품목, 규격을 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }

        ((SaleOrderActivity)getContext()).addSaleOrderListByContents(saleOrderArrayList);
        dismiss();
                /*Intent intent = new Intent(SearchAvailablePartActivity.this, SaleOrderActivity.class);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("locationNo",locationNo);
                intent.putExtra("saleOrderNo",saleOrderNo);
                intent.putExtra("saleOrderArrayList", saleOrderArrayList);
                //intent.putExtra()
                startActivityResult.launch(intent);
                finish();*/


    }


    public void getAvailableStock() {
        String url = getString(R.string.service_address) + "getAvailableStock";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", Users.BusinessClassCode);
        GetAvailableStock gsod = new GetAvailableStock(url, values);
        gsod.execute();
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
                        showErrorDialog(getContext(), ErrorCheck, 2);
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
                        (getContext(), R.layout.listview_available_part_row, stockArrayList, txtBadge);
                listview.setAdapter(availablePartAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    /*View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),"Test",Toast.LENGTH_LONG).show();
        }
    };*/

}