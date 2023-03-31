package com.symbol.steelsalesjungwon;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.symbol.steelsalesjungwon.Activity.MinapActivity;
import com.symbol.steelsalesjungwon.Activity.ProductInOutActivity;
import com.symbol.steelsalesjungwon.Activity.SearchAvailablePartActivity;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Fragment.FragmentSaleOrder;
import com.symbol.steelsalesjungwon.Fragment.FragmentViewCollection;
import com.symbol.steelsalesjungwon.Fragment.FragmentViewSaleOrder;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.Users;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity2 extends FragmentActivity implements BaseActivityInterface {
    TabLayout tabs;
    FragmentSaleOrder fragmentSaleOrder;
    FragmentViewSaleOrder fragmentViewSaleOrder;
    FragmentViewCollection fragmentViewCollection;
    //FragmentTest fragmentTest;
    boolean testFlag = false;
    ImageView imageView5;

    com.symbol.steelsalesjungwon.BackPressControl backpressed;
    TabLayout.Tab firstTab;
    TabLayout.Tab secondTab;
    TabLayout.Tab thirdTab;
    TabLayout.Tab fourthTab;
    TabLayout.Tab fifthTab;

    LinearLayout layoutRefresh;
    LinearLayout layoutTotal;
    LinearLayout layoutWeight;
    FrameLayout container;
    TextView textView7;
    int currentTab;

    String noticeData;
    SharedPreferences noticePref;//공지 유무를 저장
    TextView txtTotalAmount;
    TextView txtTotalWeight;
    TextView txtLabelWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //startProgress();
        backpressed = new com.symbol.steelsalesjungwon.BackPressControl(this);
        fragmentSaleOrder = new FragmentSaleOrder(this);
        txtTotalAmount=findViewById(R.id.txtTotalAmount);
        txtTotalWeight=findViewById(R.id.txtTotalWeight);
        txtLabelWeight=findViewById(R.id.txtLabelWeight);
        layoutWeight=findViewById(R.id.layoutWeight);
        fragmentViewSaleOrder = new FragmentViewSaleOrder(this, txtTotalAmount, txtTotalWeight);
        fragmentViewCollection = new FragmentViewCollection(this, txtTotalAmount);

        noticePref=getSharedPreferences("NoticePref",MODE_PRIVATE);

        boolean viewNotice=true;
        viewNotice=noticePref.getBoolean("viewNotice",true);

        if(viewNotice==true){
            getNoticeData();
        }

 /*       this.stockOutDetailArrayList = new ArrayList<>();
        this.scanDataArrayList = new ArrayList<>();*/
        //this.productionInfoArrayList = new ArrayList<>();

        //fragmentTest = new FragmentTest();
        /* fragment3 = new Fragment3();*/
        tabs = findViewById(R.id.tabs);
        imageView5 = findViewById(R.id.imageView5);
        textView7 = findViewById(R.id.textView7);//테스트용
        layoutRefresh = findViewById(R.id.layoutRefresh);
        layoutRefresh.setVisibility(View.INVISIBLE);
        layoutTotal=findViewById(R.id.layoutTotal);
        layoutTotal.setVisibility(View.GONE);
        container=findViewById(R.id.container);

        LinearLayout.LayoutParams params
                = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.weight = (float)7.9;
        container.setLayoutParams(params);



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        layoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(currentTab==1){
                    String fromDate = fragmentViewSaleOrder.tyear + "-" + (fragmentViewSaleOrder.tmonth + 1) + "-" + fragmentViewSaleOrder.tdate;
                    fragmentViewSaleOrder.getViewSaleOrderData(fromDate);
                    Toast.makeText(MainActivity2.this, "갱신되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(currentTab==2){
                    fragmentViewCollection.getCollectionData();
                }
            }
        });
        /*if(Users.authorityList.contains(0)){//관리자 권한이 있다면,
            textView7.setText(" 재고조사(클릭)");
        }

        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Users.authorityList.contains(0)){//관리자 권한이 있다면,
                    Intent i = new Intent(getBaseContext(), ActivityInventorySurvey.class);
                    startActivity(i);
                }
            }
        });*/

        //테스트용
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(MainActivity2.this);
                //alertBuilder.setIcon(R.drawable.ic_launcher);
                //alertBuilder.setTitle(partName + "(" + partSpecName + ")");
                // List Adapter 생성
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity2.this,
                        android.R.layout.simple_list_item_1);

                try {
                    String content = "\n어플리케이션: " + (String) getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getPackageManager().getPackageInfo(getPackageName(), 0).packageName, PackageManager.GET_UNINSTALLED_PACKAGES)) + "\n" +
                            "버전: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "\n" +
                            "사용자번호: " + Users.PhoneNumber + "\n" +
                            "사용자명: " + Users.UserName + "\n" +
                            "부서: " + Users.DeptName + "\n" +
                            "권한: ";
                    for (int i = 0; i < Users.authorityNameList.size(); i++) {
                        content += Users.authorityNameList.get(i).toString() + ", ";
                    }
                    content = content.substring(0, content.length() - 2);

                    adapter.add(content);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                // 버튼 생성
                alertBuilder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                // Adapter 셋팅
                alertBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
                alertBuilder.show();


            }
        });

        firstTab = tabs.newTab().setText("주문").setIcon(R.drawable.outline_local_grocery_store_black_48);
        secondTab = tabs.newTab().setText("주문현황").setIcon(R.drawable.outline_assignment_black_48);
        thirdTab = tabs.newTab().setText("미수금").setIcon(R.drawable.outline_payments_black_48);
        fourthTab = tabs.newTab().setText("가용재고").setIcon(R.drawable.outline_inventory_black_48);
        fifthTab=tabs.newTab().setText("미납현황").setIcon(R.drawable.outline_inventory_2_black_48);
        tabs.addTab(firstTab);
        tabs.addTab(secondTab);
        tabs.addTab(thirdTab);
        tabs.addTab(fourthTab);
        tabs.addTab(fifthTab);
/*
        tabs2=findViewById(R.id.tabs2);
        final TabLayout.Tab topTab;
        topTab = tabs2.newTab().setText("코일입고").setIcon(R.drawable.outline_build_white_24dp);
        final TabLayout.Tab topTab2;
        topTab2 = tabs2.newTab().setText("테스트").setIcon(R.drawable.outline_build_white_24dp);
        tabs2.addTab(topTab);
        tabs2.addTab(topTab2);*/

        //firstTab.view.setClickable(false);
        //secondTab.view.setClickable(false);
        //thirdTab.view.setClickable(false);


        /*else if (Users.authorityList.contains(2)) {//2:출고권한만 가졌을시
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentStockOut).commit();//첫실행 fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
            secondTab.setIcon(R.drawable.outline_build_black_48dp);
            thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
            tabs.selectTab(thirdTab);
        }*/
        firstTab.view.setVisibility(View.GONE);
        secondTab.view.setVisibility(View.GONE);
        thirdTab.view.setVisibility(View.GONE);
        fourthTab.view.setVisibility(View.GONE);
        fifthTab.view.setVisibility(View.GONE);

        if (Users.authorityList.contains(2)) {//대리점 권한이 있으면, Third, Fourth탭 안보이게
            layoutRefresh.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            params.weight = (float)7.1;
            container.setLayoutParams(params);
            firstTab.setIcon(R.drawable.outline_local_grocery_store_black_48);//주문관리
            secondTab.setIcon(R.drawable.baseline_assignment_black_48);//주문현황
            tabs.selectTab(secondTab);
            currentTab=1;
            firstTab.view.setVisibility(View.VISIBLE);
            secondTab.view.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentViewSaleOrder).commit();//첫실행 fragment
        } else if (Users.authorityList.contains(0) || Users.authorityList.contains(1)) {
            firstTab.setIcon(R.drawable.baseline_local_grocery_store_black_48);//주문관리
            secondTab.setIcon(R.drawable.outline_assignment_black_48);//주문현황
            thirdTab.setIcon(R.drawable.outline_payments_black_48);//미수금
            fourthTab.setIcon(R.drawable.outline_inventory_black_48);//수불현황
            fifthTab.setIcon(R.drawable.outline_inventory_2_black_48);//
            tabs.selectTab(firstTab);
            currentTab=0;
            firstTab.view.setVisibility(View.VISIBLE);
            secondTab.view.setVisibility(View.VISIBLE);
            thirdTab.view.setVisibility(View.VISIBLE);
            fourthTab.view.setVisibility(View.VISIBLE);
            fifthTab.view.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentSaleOrder).commit();//첫실행 fragment
        }
        /*tabs.addTab(tabs.newTab().setText("출하"));*/

       /* tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        });*/

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                currentTab=position;
                Fragment selected = null;
                if (position == 0) {
                    if (Users.authorityList.contains(2)) {
                        //대리점 권한이 있으면, 주문관리 누르면 바로 연결된 업체,현장 주문서 작성
                        if(Users.CustomerCode.equals("")){//대리점권한이 있는데, AppUsers에 CustomerCode가 들어있지 않다면 메세지
                            Toast.makeText(MainActivity2.this, "연결된 거래처 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        secondTab.setIcon(R.drawable.baseline_assignment_black_48);
                        layoutRefresh.setVisibility(View.VISIBLE);
                        layoutTotal.setVisibility(View.VISIBLE);
                        //txtTotalWeight.setVisibility(View.VISIBLE);
                        //txtLabelWeight.setVisibility(View.VISIBLE);
                        layoutWeight.setVisibility(View.VISIBLE);
                        layoutTotal.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                        params.weight = (float)7.1;
                        container.setLayoutParams(params);
                        secondTab.select();
                        getLocationNoByCustomerCode();
                    } else {
                        selected = fragmentSaleOrder;
                        firstTab.setIcon(R.drawable.baseline_local_grocery_store_black_48);
                        layoutRefresh.setVisibility(View.INVISIBLE);
                        layoutTotal.setVisibility(View.GONE);
                        params.weight = (float)7.9;
                        container.setLayoutParams(params);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
                    }
                } else if (position == 1) {//주문현황
                    selected = fragmentViewSaleOrder;
                    secondTab.setIcon(R.drawable.baseline_assignment_black_48);
                    layoutRefresh.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.VISIBLE);
                    //txtTotalWeight.setVisibility(View.VISIBLE);
                    //txtLabelWeight.setVisibility(View.VISIBLE);
                    layoutWeight.setVisibility(View.VISIBLE);
                    layoutTotal.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    params.weight = (float)7.1;
                    container.setLayoutParams(params);
                    if (Users.authorityList.contains(2)) {
                        //대리점 권한이 있으면, 본인 업체만 조회
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
                    }
                } else if (position == 2) {
                    selected = fragmentViewCollection;
                    thirdTab.setIcon(R.drawable.baseline_payments_black_48);
                    layoutRefresh.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.VISIBLE);
                    //txtTotalWeight.setVisibility(View.GONE);
                    //txtLabelWeight.setVisibility(View.GONE);
                    layoutWeight.setVisibility(View.GONE);
                    layoutTotal.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                    params.weight = (float)7.1;
                    container.setLayoutParams(params);
                    //대리점 권한이 있으면, 주문관리 누르면 바로 연결된 업체,현장 주문서 작성
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
                }

                else if (position == 3) {
                    Intent intent = new Intent(MainActivity2.this, ProductInOutActivity.class);
                    startActivity(intent);
                }

                else if (position == 4) {
                    Intent intent = new Intent(MainActivity2.this, MinapActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    firstTab.setIcon(R.drawable.outline_local_grocery_store_black_48);
                } else if (position == 1) {
                    secondTab.setIcon(R.drawable.outline_assignment_black_48);
                } else if (position == 2) {
                    thirdTab.setIcon(R.drawable.outline_payments_black_48);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                currentTab=position;
                Fragment selected = null;
                if (position == 0) {
                    return;
                } else if (position == 1) {
                    return;
                } else if (position == 2) {
                    return;
                }

                else if (position == 3) {
                    Intent intent = new Intent(MainActivity2.this, ProductInOutActivity.class);
                    startActivity(intent);
                }
                else if (position == 4) {
                    Intent intent = new Intent(MainActivity2.this, MinapActivity.class);
                    startActivity(intent);
                }
            }
        });

        //progressOFF2();
    }

    public void getNoticeData() {
        String url = getString(R.string.service_address) + "getNoticeData";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        //values.put("SearchString", this.edtSearch.getText().toString());
        GetNoticeData gsod = new GetNoticeData(url, values);
        gsod.execute();
    }

    public class GetNoticeData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetNoticeData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity2.this, ErrorCheck, 2);
                        return;
                    }
                    noticeData = child.getString("AppRemark");
                }
                viewNotice();
            } catch (Exception e) {

                e.printStackTrace();

            } finally {
            }
        }
    }

    private void viewNotice(){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_notice, null);
        AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        TextView tvTitle=dialogView.findViewById(R.id.tvTitle);
        try {
            tvTitle.setText("변경사항(version "+getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName+")");
        } catch (PackageManager.NameNotFoundException e) {
            tvTitle.setText("변경사항");
        }
        TextView tvContent=dialogView.findViewById(R.id.tvContent);
        tvContent.setText(noticeData);
        final AlertDialog dialog = buider.create();
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
        //Dialog 보이기
        dialog.show();
        Button btnOK=dialogView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chkNoView=dialogView.findViewById(R.id.chkNoView);

                if(chkNoView.isChecked()){
                    SharedPreferences.Editor editor = noticePref.edit();
                    editor.putBoolean("viewNotice", false);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
    }

    private void getLocationNoByCustomerCode() {
        String url = getString(R.string.service_address) + "getLocationNoByCustomerCode";
        ContentValues values = new ContentValues();
        values.put("CustomerCode", Users.CustomerCode);
        //values.put("CustomerCode", saleOrderNo);
        GetLocationNoByCustomerCode gsod = new GetLocationNoByCustomerCode(url, values);
        gsod.execute();
    }

    public class GetLocationNoByCustomerCode extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetLocationNoByCustomerCode(String url, ContentValues values) {
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

                String CustomerCode="";
                String CustomerName="";
                String LocationNo="";
                String LocationName="";
                //String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity2.this, ErrorCheck, 2);
                        return;
                    }
                    CustomerCode = child.getString("CustomerCode");
                    CustomerName = child.getString("CustomerName");
                    LocationNo = child.getString("LocationNo");
                    LocationName = child.getString("LocationName");
                }

                Intent i = new Intent(MainActivity2.this, SearchAvailablePartActivity.class);
                i.putExtra("locationNo", LocationNo);
                i.putExtra("locationName", LocationName);
                i.putExtra("customerCode", CustomerCode);
                i.putExtra("customerName", CustomerName);
                startActivityResult.launch(i);
                //sdf

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

    public ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    String fromDate = fragmentViewSaleOrder.tyear + "-" + (fragmentViewSaleOrder.tmonth + 1) + "-" + fragmentViewSaleOrder.tdate;
                    fragmentViewSaleOrder.getViewSaleOrderData(fromDate);
                }
            });

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON(this, message, handler);
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

        backpressed.onBackPressed();
    }
}
