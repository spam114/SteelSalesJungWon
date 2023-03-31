package com.symbol.steelsalesjungwon.Object;

import java.io.Serializable;

public class SaleOrder implements Serializable {

    public int index=0;
    public boolean isDBSaved=false;
    public String saleOrderNo="";
    public String partCode="";
    public String partName="";
    public String partSpec="";
    public String partSpecName="";
    public String orderQty="";
    public double orderAmount=0;
    public String discountRate="";
    public double directPrice=0;//직접입력 단가

    public boolean initState=false;//최초상태 true면 최초 불러온상태 DB에서

    public String orderPrice="";//할인율 적용하여, 결정된 주문 단가
    public String marketPrice="";//PartSpec의 시장 단가
    public String standardPrice="";//SaelsOrderDetail의 저장 당시의 시장 단가: DataSet에서 최초 불러올때만 이걸로 셋팅

    public String remark1="";
    public String remark2="";
    public boolean isEnabled=true;//버튼 활성화여부
    public boolean isChanged=false;//데이터 변경여부
    public double logicalWeight=0;//이론중량
    public double Weight=0;//중량
    public double stockQty;//가용재고

    public SaleOrder() {
        super();
    }
}
