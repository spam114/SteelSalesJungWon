package com.symbol.steelsalesjungwon.dialog;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.symbol.steelsalesjungwon.Adapter.LocationAdapter;
import com.symbol.steelsalesjungwon.Application.ApplicationClass;
import com.symbol.steelsalesjungwon.Interface.BaseActivityInterface;
import com.symbol.steelsalesjungwon.Object.Location;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.RequestHttpURLConnection;
import com.symbol.steelsalesjungwon.databinding.DialogLocationBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationDialog extends DialogFragment implements BaseActivityInterface {

    ArrayList<Location> locationArrayList;
    DialogLocationBinding binding;
    LocationAdapter adapter;
    //AvailablePartAdapter availablePartAdapter;
    //ListView listview;
    //TextView txtPartName;
    //TextView txtPartSpecName;
    //RecyclerView recyclerView;
    int selectedIndex = 0;
    String locationNo;
    String locationName;
    String customerCode;
    String customerName;
    Context context;
    //Button btnSave;
    //Button btnCancel;

    public LocationDialog(Context context, String customerCode, String customerName) {
        this.context=context;
        this.customerCode = customerCode;
        this.customerName = customerName;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //startProgress();
        View view = inflater.inflate(R.layout.dialog_location, container, true);
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //binding = DataBindingUtil.setContentView((Activity) context, R.layout.dialog_location);
        adapter=new LocationAdapter(new ArrayList<>(), context, this);
        binding = DataBindingUtil.bind(view);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.txtCustomerName.setText(customerName);

        //getDialog().setTitle("Sample");
        //txtContent.setText("거래처: " + customerName);


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i = new Intent(getContext(), SearchAvailablePartActivity.class);
                i.putExtra("locationNo", item.LocationNo);
                i.putExtra("locationName", item.LocationName);
                i.putExtra("customerCode", item.CustomerCode);
                i.putExtra("customerName", item.CustomerName);
                startActivity(i);*/
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        getLocationByCustomerCode();
        return view;
    }


    public void getLocationByCustomerCode() {
        String url = getString(R.string.service_address) + "getLocationByCustomerCode";
        ContentValues values = new ContentValues();
        values.put("CustomerCode", customerCode);
        GetLocationByCustomerCode gsod = new GetLocationByCustomerCode(url, values);
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
        ApplicationClass.getInstance().progressON((Activity) getContext(), message, handler);
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

    public class GetLocationByCustomerCode extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetLocationByCustomerCode(String url, ContentValues values) {
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
                Location location;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                locationArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(getContext(), ErrorCheck, 2);
                        return;
                    }
                    location = new Location();
                    location.CustomerCode = child.getString("CustomerCode");
                    location.CustomerName = child.getString("CustomerName");
                    location.LocationNo = child.getString("LocationNo");
                    location.LocationName = child.getString("LocationName");
                    locationArrayList.add(location);
                }
                adapter.updateAdapter(locationArrayList);
                binding.recyclerView.setAdapter(adapter);

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