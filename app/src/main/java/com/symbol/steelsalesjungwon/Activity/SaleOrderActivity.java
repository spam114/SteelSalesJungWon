package com.symbol.steelsalesjungwon.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsalesjungwon.Adapter.SaleOrderAdapter;
import com.symbol.steelsalesjungwon.BackPressControl;
import com.symbol.steelsalesjungwon.Object.SaleOrder;
import com.symbol.steelsalesjungwon.Object.Users;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;
import com.symbol.steelsalesjungwon.dialog.SearchAvailablePartDialog;
import com.symbol.steelsalesjungwon.dialog.SearchByKeyinDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//주문서를 작성,수정,확정 하는 액티비티
public class SaleOrderActivity extends BaseActivity {

    ArrayList<SaleOrder> saleOrderArrayList;
    ListView listview;
    TextInputEditText edtRemark;
    TextInputEditText edtRemark2;
    BackPressControl backpressed;
    String saleOrderNo = "";
    String customerCode = "";
    String locationNo = "";
    SaleOrderAdapter saleOrderAdapter;
    TextView txtTotal;
    TextView txtTotalWeight;
    TextView txtSaleOrderNo;
    Button btnSave;
    Button btnConfirm;
    MaterialButtonToggleGroup toggleOrder;
    ImageView imvRefresh;
    String fixDivision = "N";
    LinearLayout layoutTop;
    Spinner spinnerSaleDivisionCode;
    String deptCode = "-1";

    //FragmentViewSaleOrder fragmentViewSaleOrder;

    //1.앞쪽에서 선택한 데이터를 가져온다.
    //2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
    //1번과 2번 데이터를 합쳐서
    //화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saleorder);
        backpressed = new BackPressControl(this);
        listview = findViewById(R.id.listview);
        edtRemark = findViewById(R.id.edtRemark);
        edtRemark2 = findViewById(R.id.edtRemark2);
        spinnerSaleDivisionCode = findViewById(R.id.spinnerSaleDivisionCode);
        saleOrderNo = getIntent().getStringExtra("saleOrderNo");
        customerCode = getIntent().getStringExtra("customerCode");
        locationNo = getIntent().getStringExtra("locationNo");
        saleOrderArrayList = (ArrayList<SaleOrder>) getIntent().getSerializableExtra("saleOrderArrayList");
        txtTotal = findViewById(R.id.txtTotal);
        txtTotalWeight = findViewById(R.id.txtTotalWeight);
        txtSaleOrderNo = findViewById(R.id.txtSaleOrderNo);
        txtSaleOrderNo.setText("주문번호");
        //txtTotal.setText("합계: 0 원");
        toggleOrder = findViewById(R.id.toggleOrder);
        btnSave = findViewById(R.id.btnSave);
        btnConfirm = findViewById(R.id.btnConfirm);
        imvRefresh = findViewById(R.id.imvRefresh);
        layoutTop = findViewById(R.id.layoutTop);
        ArrayList<String> saleDivisionCodeArrayList = new ArrayList<>();
        saleDivisionCodeArrayList.add("판매");
        saleDivisionCodeArrayList.add("임대");
        txtSaleOrderNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!saleOrderNo.equals("")) {
                    spinnerSaleDivisionCode.setEnabled(false);//주문타입은 수정 불가능
                }
            }
        });

        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, saleDivisionCodeArrayList);

        spinnerSaleDivisionCode.setAdapter(adapter);
        if(Users.BusinessClassCode.equals("7"))
            spinnerSaleDivisionCode.setSelection(0);
        else if(Users.BusinessClassCode.equals("1"))
            spinnerSaleDivisionCode.setSelection(1);
        else
            spinnerSaleDivisionCode.setSelection(0);

        spinnerSaleDivisionCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    if (position == 0) {//판매

                    } else {//임대

                    }
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (saleOrderNo.equals("")) {
            fixDivision = "N";
            btnConfirm.setEnabled(false);
            toggleOrder.uncheck(R.id.btnSave);
            toggleOrder.uncheck(R.id.btnConfirm);
            btnConfirm.setText("확정");
        } else {
            btnConfirm.setEnabled(true);
        }

        imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(SaleOrderActivity.this);
                materialAlertDialogBuilder.setTitle("품목 추가");
                CharSequence[] sequences = new CharSequence[2];
                sequences[0] = "목록에서 찾기";
                sequences[1] = "검색하여 찾기";
                materialAlertDialogBuilder.setItems(sequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//목록에서 찾기
                            FragmentManager fm = getSupportFragmentManager();
                            SearchAvailablePartDialog custom = new SearchAvailablePartDialog(saleOrderArrayList);
                            custom.show(fm, "");
                        } else if (which == 1) {//검색하여 찾기

                            FragmentManager fm = getSupportFragmentManager();
                            SearchByKeyinDialog custom = new SearchByKeyinDialog(saleOrderArrayList);
                            custom.show(fm, "");
                        }
                    }
                });
                materialAlertDialogBuilder.setCancelable(true);
                materialAlertDialogBuilder.show();
            }
        });

        //주문서 저장
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saleOrderNo.equals("")) {//최초 주문 상태
                    new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                            .setTitle("주문서 작성")
                            .setMessage("총금액: " + txtTotal.getText().toString().replace("합계: ", "") + "\n" + "주문서를 작성하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SaveSalesOrderData(false);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                            if (saleOrderNo.equals("")) {
                                toggleOrder.uncheck(R.id.btnSave);
                            } else {
                                toggleOrder.check(R.id.btnSave);
                            }
                        }
                    }).show();
                } else {//주문 이미 한번이라도 작성한상태
                    new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                            .setTitle("주문서 수정")
                            .setMessage("총금액: " + txtTotal.getText().toString().replace("합계: ", "") + "\n" + "주문서를 수정하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SaveSalesOrderData(false);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                            if (saleOrderNo.equals("")) {
                                toggleOrder.uncheck(R.id.btnSave);
                            } else {
                                toggleOrder.check(R.id.btnSave);
                            }

                        }
                    }).show();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrCancelSaleOrder();
            }
        });

        getSaleOrderDetail();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void confirmOrCancelSaleOrder() {
        String cString = "";
        String cTitle = "";
        String cType = "";
        if (fixDivision.equals("Y")) {//확정취소를 한다.
            cTitle = "주문서 확정취소";
            cString = "확정 취소 하시겠습니까";
            cType = "취소";
        } else {//확정을 한다.
            cTitle = "출고의뢰(출고부서 선택)";
            cString = "출고의뢰 하시겠습니까?";
            cType = "확정";
        }
        String finalCType = cType;
        final CharSequence[] deptSequences= new CharSequence[Users.stockOutdeptArrayList.size()];
        for(int i=0;i<Users.stockOutdeptArrayList.size();i++){
            deptSequences[i] = Users.stockOutdeptArrayList.get(i).deptName;
        }
        final int[] checkedNo = {0};
        if(Users.BusinessClassCode.equals("7")){
            checkedNo[0] =2;
        }
        else if(Users.BusinessClassCode.equals("1")){
            checkedNo[0] =1;
        }

        if (finalCType.equals("취소")) {//취소
            new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                    .setTitle(cTitle)
                    .setMessage("주문번호: " + txtSaleOrderNo.getText().toString() + "\n" + cString)
                    .setCancelable(false)
                    .setPositiveButton
                            ("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelSaleOrder();
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                    if (fixDivision.equals("N")) {
                        toggleOrder.uncheck(R.id.btnConfirm);
                        btnConfirm.setText("확정");
                    } else {
                        toggleOrder.check(R.id.btnConfirm);
                        btnConfirm.setText("확정취소");
                    }
                }
            }).show();
        } else {//확정
            new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                    .setTitle(cTitle)
                    //.setMessage("주문번호: " + txtSaleOrderNo.getText().toString() + "\n" + cString)
                    .setCancelable(false)
                    .setSingleChoiceItems(deptSequences, checkedNo[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedNo[0] =which;
                        }
                    })
                    .setPositiveButton
                            ("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deptCode = Users.stockOutdeptArrayList.get(checkedNo[0]).deptCode;
                                    SaveSalesOrderData(true);
                                }
                            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                    if (fixDivision.equals("N")) {
                        toggleOrder.uncheck(R.id.btnConfirm);
                        btnConfirm.setText("확정");
                    } else {
                        toggleOrder.check(R.id.btnConfirm);
                        btnConfirm.setText("확정취소");
                    }
                }
            }).show();
        }
    }

    private void confirmSaleOrder() {
        String url = getString(R.string.service_address) + "confirmSaleOrder";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.UserID);
        values.put("ProductDBName", getString(R.string.product_dbname));
        values.put("SaleOrderNo", saleOrderNo);
        values.put("DeptCode", deptCode);
        ConfirmOrCancelSaleOrder gsod = new ConfirmOrCancelSaleOrder(url, values);
        gsod.execute();
    }

    private void cancelSaleOrder() {
        String url = getString(R.string.service_address) + "cancelSaleOrder";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.UserID);
        values.put("ProductDBName", getString(R.string.product_dbname));
        values.put("SaleOrderNo", saleOrderNo);
        ConfirmOrCancelSaleOrder gsod = new ConfirmOrCancelSaleOrder(url, values);
        gsod.execute();

    }

    public class ConfirmOrCancelSaleOrder extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        ConfirmOrCancelSaleOrder(String url, ContentValues values) {
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
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");

                        if (child.getString("FixDivision").equals("N")) {//"이미 취소된 전표 처리입니다" 에러시 버튼 처리
                            toggleOrder.uncheck(R.id.btnConfirm);
                            btnConfirm.setText("확정");
                            btnSave.setEnabled(true);
                            setEnableFalseOrTrueEditText(true);
                        } else {
                            toggleOrder.check(R.id.btnConfirm);
                            btnConfirm.setText("확정취소");
                            btnSave.setEnabled(false);
                            setEnableFalseOrTrueEditText(false);
                        }

                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }
                    fixDivision = child.getString("FixDivision");
                    /*saleOrder = new SaleOrder();
                    saleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty= child.getString("OrderQty");
                    saleOrder.orderPrice= child.getString("OrderPrice");
                    saleOrder.standardPrice= child.getString("StandardPrice");
                    saleOrderArrayList.add(saleOrder);*/
                }
                if (fixDivision.equals("Y")) {
                    toggleOrder.check(R.id.btnConfirm);
                    btnConfirm.setText("확정취소");
                    btnSave.setEnabled(false);
                    setEnableFalseOrTrueEditText(false);
                    Toast.makeText(SaleOrderActivity.this, "확정 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    toggleOrder.uncheck(R.id.btnConfirm);
                    btnConfirm.setText("확정");
                    btnSave.setEnabled(true);
                    setEnableFalseOrTrueEditText(true);
                    Toast.makeText(SaleOrderActivity.this, "확정 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

    private void setEnableFalseOrTrueEditText(boolean state) {
        /*try {
            edtRemark.setEnabled(state);
            edtRemark2.setEnabled(state);
            setEnable(listview, state);
        } catch (Exception ete) {
            //Toast.makeText(SaleOrderActivity.this, ete.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        edtRemark.setEnabled(state);
        edtRemark2.setEnabled(state);
        imvRefresh.setEnabled(state);
        for (int i = 0; i < listview.getCount(); i++) {
            try {
                SaleOrder soa = (SaleOrder) listview.getItemAtPosition(i);
                soa.isEnabled = state;
            } catch (Exception ete) {
                Toast.makeText(SaleOrderActivity.this, ete.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        saleOrderAdapter.notifyDataSetChanged();
    }

    public static void setEnable(View view, boolean tOrf) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setEnable(viewGroup.getChildAt(i), tOrf);
                }
            }
            String test = "";
            test = view.getClass().getName();
            if (test.equals("com.symbol.steelsalesjungwon.BackPressEditText"))
                view.setEnabled(tOrf);
        }
    }

        /*for(int i=0; i<listview.getCount();i++){
            try {

            }
            catch (Exception ete){
                Toast.makeText(SaleOrderActivity.this,ete.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }*/


    private void SaveSalesOrderData(boolean confirmFlag) {//confirmFlag=true 끝나고 확정까지
        String url = getString(R.string.service_address) + "saveSalesOrderData";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.UserID);
        values.put("SaleOrderNo", saleOrderNo);
        values.put("CustomerCode", customerCode);
        values.put("LocationNo", locationNo);
        values.put("BusinessClassCode", Users.BusinessClassCode);
        values.put("OutBusinessClassCode", Users.BusinessClassCode);
        values.put("Remark1", edtRemark.getText().toString());
        values.put("Remark2", edtRemark2.getText().toString());
        String saleDivisionCode = "";
        if (spinnerSaleDivisionCode.getSelectedItemPosition() == 0) {
            saleDivisionCode = "S";
        } else {
            saleDivisionCode = "R";
        }
        values.put("SaleDivisionCode", saleDivisionCode);

        for (int i = 0; i < saleOrderArrayList.size(); i++) {
            if (saleOrderArrayList.get(i).orderQty.equals("")) {
                Toast.makeText(SaleOrderActivity.this, "수량을 확인하시기 바랍니다.\n" +
                        "(" + saleOrderArrayList.get(i).partName + ", " + saleOrderArrayList.get(i).partSpecName + ")", Toast.LENGTH_SHORT).show();

                ChangeButtonState();
                return;
            }
        }

        SaveSalesOrderData gsod = new SaveSalesOrderData(url, values, saleOrderArrayList, confirmFlag);
        gsod.execute();
    }


    public class SaveSalesOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        ArrayList<SaleOrder> list;
        boolean confirmFlag;

        SaveSalesOrderData(String url, ContentValues values, ArrayList<SaleOrder> list, boolean confirmFlag) {
            this.url = url;
            this.values = values;
            this.list = list;
            this.confirmFlag = confirmFlag;
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
            result = requestHttpURLConnection.request2(url, values, list);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }

                    tempSaleOrderNo = child.getString("SaleOrderNo");
                    fixDivision = child.getString("FixDivision");
                    /*saleOrder = new SaleOrder();
                    saleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty= child.getString("OrderQty");
                    saleOrder.orderPrice= child.getString("OrderPrice");
                    saleOrder.standardPrice= child.getString("StandardPrice");
                    saleOrderArrayList.add(saleOrder);*/
                }
                if (!tempSaleOrderNo.equals("")) {
                    saleOrderNo = tempSaleOrderNo;
                    txtSaleOrderNo.setText(saleOrderNo);
                    if (!confirmFlag)
                        Toast.makeText(SaleOrderActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();
                    btnSave.setText("저장");
                    btnConfirm.setEnabled(true);
                    toggleOrder.check(R.id.btnSave);
                }
                if (fixDivision.equals("Y")) {
                    toggleOrder.check(R.id.btnConfirm);
                    btnConfirm.setText("확정취소");
                } else {
                    toggleOrder.uncheck(R.id.btnConfirm);
                    btnConfirm.setText("확정");
                }
                //확정 까지한다
                if (confirmFlag) {
                    confirmSaleOrder();
                }

                //저장한 데이터 색상 투명하게 변경
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).isChanged = false;
                }
                txtTotal.setBackgroundColor(Color.TRANSPARENT);
                txtTotalWeight.setBackgroundColor(Color.TRANSPARENT);

                if (!confirmFlag) {
                    saleOrderAdapter.notifyDataSetChanged();
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

    /**
     * 주문서의 상태에 따라서 버튼의 상태를 바꾼다.
     */
    private void ChangeButtonState() {
        if (!saleOrderNo.equals("")) {
            txtSaleOrderNo.setText(saleOrderNo);
            btnSave.setText("저장");
            toggleOrder.check(R.id.btnSave);
            btnConfirm.setEnabled(true);

            if (fixDivision.equals("Y")) {
                toggleOrder.check(R.id.btnConfirm);
                btnConfirm.setText("확정취소");
                toggleOrder.check(R.id.btnSave);
                btnSave.setEnabled(false);
                setEnableFalseOrTrueEditText(false);
            } else {
                toggleOrder.uncheck(R.id.btnConfirm);
                btnConfirm.setText("확정");
                setEnableFalseOrTrueEditText(true);
            }
        } else {
            btnSave.setText("주문");
            toggleOrder.uncheck(R.id.btnSave);
        }

    }

    /**
     * 목록에서 찾기로 추가된 품목들 처리
     *
     * @param addedSaleOrderArrayList
     */
    public void addSaleOrderListByContents(ArrayList<SaleOrder> addedSaleOrderArrayList) {
        for (int i = 0; i < addedSaleOrderArrayList.size(); i++) {
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.partCode = addedSaleOrderArrayList.get(i).partCode;
            saleOrder.partName = addedSaleOrderArrayList.get(i).partName;
            saleOrder.partSpec = addedSaleOrderArrayList.get(i).partSpec;
            saleOrder.partSpecName = addedSaleOrderArrayList.get(i).partSpecName;
            saleOrder.marketPrice = addedSaleOrderArrayList.get(i).marketPrice;
            saleOrder.orderQty = addedSaleOrderArrayList.get(i).orderQty;
            saleOrder.orderPrice = addedSaleOrderArrayList.get(i).orderPrice;
            saleOrder.standardPrice = addedSaleOrderArrayList.get(i).standardPrice;
            saleOrder.isChanged = true;
            saleOrder.logicalWeight = addedSaleOrderArrayList.get(i).logicalWeight;
            saleOrder.stockQty = addedSaleOrderArrayList.get(i).stockQty;
            saleOrderArrayList.add(saleOrder);
        }
        saleOrderAdapter.notifyDataSetChanged();
    }

    private void getSaleOrderDetail() {
        String url = getString(R.string.service_address) + "getSaleOrderDetail";
        ContentValues values = new ContentValues();
        values.put("SaleOrderNo", saleOrderNo);
        values.put("BusinessClassCode", Users.BusinessClassCode);
        GetSaleOrderDetail gsod = new GetSaleOrderDetail(url, values);
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
    public void onBackPressed() {

        boolean changeFlag = false;
        boolean changeFlag2 = false;//항목 하나 삭제시 변경
        ColorDrawable cd = (ColorDrawable) txtTotal.getBackground();

        if (cd != null) {
            int test = cd.getColor();
            Toast.makeText(SaleOrderActivity.this, Integer.toString(test), Toast.LENGTH_SHORT);
            if (test != 0) {
                changeFlag2 = true;
            }
        }

        for (int i = 0; i < saleOrderArrayList.size(); i++) {
            if (saleOrderArrayList.get(i).isChanged)
                changeFlag = true;
        }

        if (changeFlag || changeFlag2) {//변경된 데이터가 존재
            //주문하지 않은 데이터는 삭제합니다. 안내 메세지
            new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                    .setTitle("뒤로 가기")
                    .setMessage("저장하지 않은 데이터는 삭제 됩니다.\n뒤로 가시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton
                            ("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                            /*    Intent intent=new Intent();
                                intent.putExtra("saleOrderNo",saleOrderNo);
                                setResult(Activity.RESULT_OK, intent);*/
                                    goBack();
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
        } else {
            finish();
        }
    }

    public void goBack() { // 종료
        //String fromDate=fragmentViewSaleOrder.tyear+"-"+(fragmentViewSaleOrder.tmonth+1)+"-"+fragmentViewSaleOrder.tdate;
        //fragmentViewSaleOrder.getViewSaleOrderData(fromDate);

        finish();
    }

    public class GetSaleOrderDetail extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetSaleOrderDetail(String url, ContentValues values) {
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
            double initTotalAmt = 0;
            double initTotalWeight = 0;
            try {
                SaleOrder saleOrder;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String Remark1 = "";
                String SaleDivisionCode = "";
                String Remark2 = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                //double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }
                    saleOrder = new SaleOrder();
                    saleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty = child.getString("OrderQty");
                    saleOrder.orderPrice = child.getString("OrderPrice");
                    saleOrder.standardPrice = child.getString("StandardPrice");
                    saleOrder.discountRate = child.getString("DiscountRate");
                    saleOrder.logicalWeight = Double.parseDouble(child.getString("LogicalWeight"));
                    saleOrder.orderAmount = Double.parseDouble(child.getString("OrderAmount"));
                    saleOrder.stockQty = Double.parseDouble(child.getString("StockQty"));
                    saleOrder.Weight = Double.parseDouble(saleOrder.orderQty) * saleOrder.logicalWeight;
                    saleOrder.initState = true;

                    Remark1 = child.getString("Remark1");
                    Remark2 = child.getString("Remark2");
                    SaleDivisionCode = child.getString("SaleDivisionCode");
                    fixDivision = child.getString("FixDivision");

                    initTotalAmt += Double.parseDouble(child.getString("OrderAmount"));
                    initTotalWeight += Double.parseDouble(saleOrder.orderQty) * saleOrder.logicalWeight;

                    saleOrderArrayList.add(saleOrder);

                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }
                saleOrderAdapter = new SaleOrderAdapter
                        (SaleOrderActivity.this, R.layout.listview_saleorder_row, saleOrderArrayList, listview, edtRemark, edtRemark2, spinnerSaleDivisionCode, txtTotal, layoutTop, saleOrderNo, txtTotalWeight);
                listview.setAdapter(saleOrderAdapter);
                txtSaleOrderNo.setText(saleOrderNo);
                if (fixDivision.equals("Y")) {//확정
                    toggleOrder.check(R.id.btnConfirm);
                    btnConfirm.setText("확정취소");
                    toggleOrder.check(R.id.btnSave);
                    btnSave.setEnabled(false);
                    setEnableFalseOrTrueEditText(false);
                } else {
                    toggleOrder.uncheck(R.id.btnConfirm);
                    btnConfirm.setText("확정");
                    if (!saleOrderNo.equals("")) {
                        toggleOrder.check(R.id.btnSave);
                    }
                    setEnableFalseOrTrueEditText(true);
                }


                txtTotal.setText("합계: " + Double.toString(initTotalAmt) + " 원");
                txtTotalWeight.setText("중량: " + ((int) initTotalWeight) + " KG");
                edtRemark.setText(Remark1);
                edtRemark2.setText(Remark2);

                if(!SaleDivisionCode.equals("")){
                    if (SaleDivisionCode.equals("R"))
                        spinnerSaleDivisionCode.setSelection(1);
                    else
                        spinnerSaleDivisionCode.setSelection(0);
                }
                //txtTotal.setText("Why");

              /*  partNameSequences = new CharSequence[partNameDic.size() + 1];
                partNameSequences[0] = "전체";
                for (int i = 1; i < partNameDic.size() + 1; i++) {
                    partNameSequences[i] = partNameDic.get(i - 1);
                }

               *//* partSpecNameSequences = new CharSequence[partSpecNameDic.size()+1];
                partSpecNameSequences[0] ="전체";
                for (int i = 1; i < partSpecNameDic.size()+1; i++) {
                    partSpecNameSequences[i] = partSpecNameDic.get(i-1);
                }*//*

                availablePartAdapter = new AvailablePartAdapter
                        (SearchAvailablePartActivity.this, R.layout.listview_available_part, stockArrayList, txtBadge);*/


            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

}
