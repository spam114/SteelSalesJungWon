package com.symbol.steelsalesjungwon;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsalesjungwon.Adapter.SaleOrderAdapter;
import com.symbol.steelsalesjungwon.Object.SaleOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyWatcher3 implements TextWatcher {
    private TextInputEditText edtOrderQty;
    private TextView txtOrderPrice;
    private TextView txtOrderAmount;
    private SaleOrderAdapter saleOrderAdapter;
    private TextView txtTotal;
    private ArrayList data;
    private SaleOrder item;
    private View row;
    TextView txtWeight;
    public MyWatcher3( TextInputEditText edtOrderQty, TextView txtOrderPrice, TextView txtOrderAmount,
                      SaleOrderAdapter saleOrderAdapter, TextView txtTotal, ArrayList data, View row, TextView txtWeight) {
        this.edtOrderQty = edtOrderQty;
        this.txtOrderPrice = txtOrderPrice;
        this.txtOrderAmount = txtOrderAmount;
        this.saleOrderAdapter = saleOrderAdapter;
        this.txtTotal = txtTotal;
        this.data = data;
        this.row=row;
        this.txtWeight=txtWeight;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Log.d("TAG", "onTextChanged: " + s);
        String tempString = s.toString().replace(",","");
        this.item = (SaleOrder) txtOrderPrice.getTag();

        if (item != null) {
            item.orderPrice = tempString;
        }
    }


    @Override
    public void afterTextChanged(Editable s) {
        //String test=s.toString();
        String tempString = s.toString().replace(",","");
        DecimalFormat myFormatter = new DecimalFormat("###,###");

        int orderQty = 0;//주문수량
        double orderPrice = 0;//주문단가
        double orderAmount = 0;//주문금액


        switch(edtOrderQty.getText().toString()) {
            case "":
            case "-":
                break;
            default:
                orderQty = Integer.parseInt(edtOrderQty.getText().toString());
        }
        orderPrice = Double.parseDouble(tempString);
        orderAmount = orderQty * (int) orderPrice;

        if(orderPrice!=0){
            String strOrderAmount = myFormatter.format(orderAmount);
            txtOrderAmount.setText(strOrderAmount);
            item.orderAmount=orderAmount;
        }

        item.orderPrice=Integer.toString((int)orderPrice);
               /* item.orderQty=Integer.toString(orderQty);
                item.orderPrice=Integer.toString((int)orderPrice);
                item.orderAmount=orderAmount;
                item.discountRate=Integer.toString((int)discountRate);*/

        double totalAmount = 0;
        for (int i = 0; i < saleOrderAdapter.getCount(); i++) {
            SaleOrder item = (SaleOrder) data.get(i);
            totalAmount += item.orderAmount;
        }

        if(item.isChanged)
            row.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
        else
            row.setBackgroundColor(Color.TRANSPARENT);

        String strTotalPrice = myFormatter.format((int) totalAmount);
        txtTotal.setText("합계: " + strTotalPrice + " 원");
        //notifyDataSetChanged();
    }
}