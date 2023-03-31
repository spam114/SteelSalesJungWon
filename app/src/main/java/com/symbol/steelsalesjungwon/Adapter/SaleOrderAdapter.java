package com.symbol.steelsalesjungwon.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.BackPressEditText;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.MyWatcher;
import com.symbol.steelsalesjungwon.MyWatcher2;
import com.symbol.steelsalesjungwon.MyWatcher3;
import com.symbol.steelsalesjungwon.Object.SaleOrder;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SaleOrderAdapter extends ArrayAdapter<SaleOrder> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    ListView listView;

    EditText edtRemark;
    EditText edtRemark2;
    TextView txtTotal;
    TextView txtTotalWeight;
    BackPressEditText edtOrderQty;
    BackPressEditText edtDiscountRate;
    TextView txtOrderPrice;
    TextView txtOrderAmount;
    LinearLayout layoutTop;
    String saleOrderNo;
    TextView txtWeight;
    TextView txtStockQty;
    Spinner spinnerSaleDivisionCode;


   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    //Filter listFilter;
    int checkedQty = 0;


    public SaleOrderAdapter(Context context, int layoutResourceID, ArrayList data, ListView listView,
                            EditText edtRemark, EditText edtRemark2, Spinner spinnerSaleDivisionCode, TextView txtTotal, LinearLayout layoutTop, String saleOrderNo, TextView txtTotalWeight) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.listView = listView;
        this.edtRemark = edtRemark;
        this.edtRemark2 = edtRemark2;
        this.spinnerSaleDivisionCode = spinnerSaleDivisionCode;
        this.txtTotal = txtTotal;
        this.txtTotalWeight = txtTotalWeight;
        this.layoutTop = layoutTop;
        this.saleOrderNo = saleOrderNo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);

            txtWeight = row.findViewById(R.id.txtWeight);
            txtStockQty = row.findViewById(R.id.txtStockQty);
            txtOrderPrice = row.findViewById(R.id.txtOrderPrice);
            txtOrderAmount = row.findViewById(R.id.txtOrderAmount);
            edtOrderQty = (BackPressEditText) row.findViewById(R.id.edtOrderQty);
            edtDiscountRate = (BackPressEditText) row.findViewById(R.id.edtDiscountRate);
            edtOrderQty.addTextChangedListener(new MyWatcher(edtOrderQty, edtDiscountRate, txtOrderPrice, txtOrderAmount, this, txtTotal, data, row, txtWeight, txtTotalWeight));
            //edtOrderQty.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            edtDiscountRate.addTextChangedListener(new MyWatcher2(edtDiscountRate, edtOrderQty, txtOrderPrice, txtOrderAmount, this, txtTotal, data, row, txtWeight, txtTotalWeight));
            txtOrderPrice.addTextChangedListener(new MyWatcher3(edtOrderQty, txtOrderPrice, txtOrderAmount, this, txtTotal, data, row, txtWeight));
            //edtDiscountRate.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        SaleOrder item = (SaleOrder) data.get(position);
        if (item != null) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            //row.setTag(item);
            //ImageView imvRemove;
            //imvRemove= row.findViewById(R.id.imvRemove);

            TextView txtPartName = row.findViewById(R.id.txtPartName);
            txtWeight = row.findViewById(R.id.txtWeight);
            txtStockQty = row.findViewById(R.id.txtStockQty);
            txtOrderPrice = row.findViewById(R.id.txtOrderPrice);

            txtOrderAmount = row.findViewById(R.id.txtOrderAmount);
            String strOrderAmount = myFormatter.format(item.orderAmount);
            txtOrderAmount.setText(strOrderAmount);

            txtPartName.setText(item.partName + "\n" + item.partSpecName);
            //txtWeight.setText(Double.toString(item.Weight));
            txtStockQty.setText("");//가용재고

            TextView txtStockQty = row.findViewById(R.id.txtStockQty);
            String strStockQty = myFormatter.format(item.stockQty);
            txtStockQty.setText(strStockQty);

            edtOrderQty = row.findViewById(R.id.edtOrderQty);
            edtDiscountRate = row.findViewById(R.id.edtDiscountRate);

            edtDiscountRate.setTag(item);
            edtOrderQty.setTag(item);
            txtOrderPrice.setTag(item);

            //edtOrderQty.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //edtDiscountRate.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            edtOrderQty.setText(item.orderQty);

            edtOrderQty.setOnBackPressListener(new BackPressEditText.OnBackPressListener() {
                @Override
                public void onBackPress() {
                    layoutTop.requestFocus();
                }
            });

            edtOrderQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) { // IME_ACTION_SEARCH , IME_ACTION_GO

                        layoutTop.requestFocus();
                        HideKeyBoard(context);
                    }
                    return false;
                }
            });


            edtDiscountRate.setOnBackPressListener(new BackPressEditText.OnBackPressListener() {
                @Override
                public void onBackPress() {
                    layoutTop.requestFocus();
                }
            });

            edtDiscountRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) { // IME_ACTION_SEARCH , IME_ACTION_GO
                        layoutTop.requestFocus();
                        HideKeyBoard(context);
                    }
                    return false;
                }
            });

            edtDiscountRate.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    item.directPrice = 0;
                    item.initState = false;
                    return false;
                }
            });

            if (item.discountRate.equals("") || item.discountRate.equals("0"))
                edtDiscountRate.setText("-");
            else
                edtDiscountRate.setText(item.discountRate);


            if (!item.orderPrice.equals("")) {
                double tempPrice = Double.parseDouble(item.orderPrice);
                String strOrderPrice = myFormatter.format((int) tempPrice);
                txtOrderPrice.setText(strOrderPrice);
            }

            // edtDiscountRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,1)});

            /*edtDiscountRate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){//focus 들어왔을때
                        int test=edtDiscountRate.length();
                        edtDiscountRate.setSelection(edtDiscountRate.getText().length(),edtDiscountRate.getText().length());
                    }
                }
            });*/

            if (item.isEnabled) {
                edtOrderQty.setEnabled(true);
                edtDiscountRate.setEnabled(true);
                txtOrderPrice.setEnabled(true);
                row.setEnabled(true);
            } else {
                edtOrderQty.setEnabled(false);
                edtDiscountRate.setEnabled(false);
                txtOrderPrice.setEnabled(false);
                row.setEnabled(false);
            }

            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("주문항목 삭제")
                            .setMessage("품목: " + item.partName + "\n" +
                                    "규격: " + item.partSpecName + "\n" + "주문항목을 삭제하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            data.remove(position);
                                            txtTotal.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
                                            txtTotalWeight.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
                                            if (data.size() == 0) {
                                                if (saleOrderNo.equals("")) {//SaleOrderActivity 종료
                                                    ((Activity) context).finish();
                                                } else {//SaleOrderActivity 종료 + DB Delete 로직 실행
                                                    deleteSalesOrderData();
                                                }
                                            }

                                            notifyDataSetChanged();
                                            Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return false;
                }
            });

            View finalRow = row;
            txtOrderPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtOrderPrice = finalRow.findViewById(R.id.txtOrderPrice);
                    edtDiscountRate = finalRow.findViewById(R.id.edtDiscountRate);
                    //
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View dialogView = inflater.inflate(R.layout.dialog_direct_price, null);
                    AlertDialog.Builder buider = new AlertDialog.Builder(context); //AlertDialog.Builder 객체 생성
                    //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                    buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                    final AlertDialog dialog = buider.create();
                    //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                    dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                    //Dialog 보이기
                    dialog.show();
                    TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
                    tvTitle.setText(item.partName + "(" + item.partSpecName + ")");
                    Button btnOK = dialogView.findViewById(R.id.btnOK);
                    Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                    BackPressEditText edtOrderPrice = dialogView.findViewById(R.id.edtOrderPrice);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double tempPrice = Double.parseDouble(edtOrderPrice.getText().toString());

                            if(tempPrice==0){
                                Toast.makeText(context, "단가는 0보다 커야 합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //item.orderPrice = edtOrderPrice.getText().toString();
                            item.directPrice = Double.parseDouble(edtOrderPrice.getText().toString());
                            item.discountRate = "0";
                            String strOrderPrice = myFormatter.format((int) tempPrice);
                            txtOrderPrice.setText(strOrderPrice);
                            //notifyDataSetChanged();
                            edtDiscountRate.setText("-");
                            finalRow.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
                            item.isChanged = true;
                            dialog.dismiss();
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtOrderQty.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                //edtOrderQty.clearFocus();
                edtDiscountRate.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                //edtDiscountRate.clearFocus();
                edtRemark.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                edtRemark2.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                //edtRemark.clearFocus();
                spinnerSaleDivisionCode.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                //edtRemark2.clearFocus();

                layoutTop.requestFocus();
                HideKeyBoard(context);
                return false;
            }
        });
        return row;
    }

    private void deleteSalesOrderData() {
        String url = context.getString(R.string.service_address) + "deleteSalesOrderData";
        ContentValues values = new ContentValues();
        values.put("UserID", "");
        values.put("SaleOrderNo", saleOrderNo);
        values.put("KMURL", context.getString(R.string.KMURL));
        values.put("SDBN", context.getString(R.string.SDBN));
        DeleteSalesOrderData gsod = new DeleteSalesOrderData(url, values);
        gsod.execute();
    }

    public class DeleteSalesOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteSalesOrderData(String url, ContentValues values) {
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
                //String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck, 2);
                        return;
                    }

                    //tempSaleOrderNo = child.getString("SaleOrderNo");
                }
                ((Activity) context).finish();

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

