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

public class MyWatcher2 implements TextWatcher {
//할인율 변경
    private TextInputEditText edit;
    private TextInputEditText edtOrderQty;
    private TextView txtOrderPrice;
    private TextView txtOrderAmount;
    private SaleOrderAdapter saleOrderAdapter;
    private TextView txtTotal;
    private ArrayList data;
    private SaleOrder item;
    private View row;
    TextView txtWeight;
    private TextView txtTotalWeight;
    public MyWatcher2(TextInputEditText edit, TextInputEditText edtOrderQty, TextView txtOrderPrice, TextView txtOrderAmount,
                      SaleOrderAdapter saleOrderAdapter, TextView txtTotal, ArrayList data, View row, TextView txtWeight, TextView txtTotalWeight) {
        this.edit = edit;
        this.edtOrderQty = edtOrderQty;
        this.txtOrderPrice = txtOrderPrice;
        this.txtOrderAmount = txtOrderAmount;
        this.saleOrderAdapter = saleOrderAdapter;
        this.txtTotal = txtTotal;
        this.data = data;
        this.row=row;
        this.txtWeight=txtWeight;
        this.txtTotalWeight=txtTotalWeight;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Log.d("TAG", "onTextChanged: " + s);
        if(start==1 && before==0 && count==1) {
            item.directPrice = 0;
            item.initState=false;
        }
        this.item = (SaleOrder) edit.getTag();

        if (item != null) {
            String tempString=s.toString();
            if(tempString.equals("-"))
                tempString="0";

            String tempString2=item.discountRate;
            if(tempString2.equals(""))
                tempString2="0";

            if(!tempString.equals(tempString2)){
                //row.setBackgroundColor(Color.YELLOW);
                item.isChanged=true;
            }
            item.discountRate = tempString;
        }
    }


    @Override
    public void afterTextChanged(Editable s) {
       //String test=s.toString();
        if(s.length()>=1){
            char schar = s.toString().charAt(s.length()-1);
            String test=String.valueOf(schar);
            if (test.equals("."))
                return;
        }

        DecimalFormat myFormatter = new DecimalFormat("###,###");

        int orderQty = 0;//주문수량
        double discountRate = 0;//할인율
        double orderPrice = 0;//주문단가
        double orderAmount = 0;//주문금액
        double calcPrice=0;

        if(item.directPrice!=0)
            calcPrice = item.directPrice;
        else
            calcPrice=Double.parseDouble(item.marketPrice);

        switch(edtOrderQty.getText().toString()) {
            case "":
            case "-":
                break;
            default:
                orderQty = Integer.parseInt(edtOrderQty.getText().toString());
        }

        if (!s.toString().equals("") && !s.toString().equals("-")) {
            discountRate = Double.parseDouble(s.toString());
        }

        if(!item.initState)
            orderPrice = Math.round(discountRate * calcPrice / 100 + calcPrice);
        else
            orderPrice = Double.parseDouble(item.orderPrice);

        orderAmount = orderQty * (int) orderPrice;

        String strOrderPrice = myFormatter.format((int) orderPrice);
        txtOrderPrice.setText(strOrderPrice);

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

        item.Weight=item.logicalWeight*orderQty;

        double totalAmount = 0;
        double totalWeight = 0;
        for (int i = 0; i < saleOrderAdapter.getCount(); i++) {
            SaleOrder item = (SaleOrder) data.get(i);
            totalAmount += item.orderAmount;
            totalWeight += item.Weight;
        }

        if(item.isChanged)
            row.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
        else
            row.setBackgroundColor(Color.TRANSPARENT);

        String strTotalPrice = myFormatter.format((int) totalAmount);
        String strTotalWeight = myFormatter.format((int) totalWeight);
        txtTotal.setText("합계: " + strTotalPrice + " 원");
        txtTotalWeight.setText("중량: " + strTotalWeight + " KG");
        //notifyDataSetChanged();
    }
}