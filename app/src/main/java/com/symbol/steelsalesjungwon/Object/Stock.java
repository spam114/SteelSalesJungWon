package com.symbol.steelsalesjungwon.Object;

import java.io.Serializable;

public class Stock implements Serializable {

    public String PartCode = "";
    public String PartName = "";
    public String PartSpec="";
    public String PartSpecName="";
    public String Qty="";
    public String MarketPrice="";//시장단가
    public String Size1 = "";//두께
    public String Size2 = "";//폭
    public String Weight="";//중량
    public boolean checked=false;

    public String OutQty="";//출고수량
    public String OutQtySeoul="";//출고수량(서울)
    public String OutQtyPusan="";//출고수량(부산)
    public String Minap="";//미납수량
    public String MinapSeoul="";//미납수량(서울)
    public String MinapPusan="";//미납수량(부산)

    public Stock() {
        super();
    }
}
