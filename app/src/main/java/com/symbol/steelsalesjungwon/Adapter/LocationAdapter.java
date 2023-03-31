package com.symbol.steelsalesjungwon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.steelsalesjungwon.Activity.SearchAvailablePartActivity;
import com.symbol.steelsalesjungwon.Object.Location;
import com.symbol.steelsalesjungwon.R;
import com.symbol.steelsalesjungwon.databinding.RowLocationBinding;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    /* Context context;
     LinearLayout layoutTop;
     String contractNo;
     String fromDate;*/
    private ArrayList<Location> items;
    Context context;
    DialogFragment dialogFragment;

    public LocationAdapter(ArrayList<Location> items, Context context, DialogFragment dialogFragment) {
        this.context=context;
        this.items = items;
        this.dialogFragment=dialogFragment;
    }


   /* public LocationAdapter(Context context, LinearLayout layoutTop, String contractNo, String fromDate) {
        super();
        this.context = context;
        this.layoutTop = layoutTop;
        this.contractNo = contractNo;
        this.fromDate = fromDate;
    }*/

    public void updateAdapter(ArrayList<Location> location) {
        items.clear();
        items.addAll(location);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RowLocationBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_location, viewGroup, false);
        return new LocationAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Location item = items.get(position);
        viewHolder.setItem(item, position); //왜오류
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //보통은 ViewHolder를 Static 으로 쓴다.
    //범용성을 위해서, 나는 제거함
    class ViewHolder extends RecyclerView.ViewHolder {
        RowLocationBinding binding;
        //View row;

        public ViewHolder(RowLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(Location item, int position) {
            binding.tvDong.setText(item.LocationName);
            binding.layoutRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SearchAvailablePartActivity.class);
                    i.putExtra("locationNo", item.LocationNo);
                    i.putExtra("locationName", item.LocationName);
                    i.putExtra("customerCode", item.CustomerCode);
                    i.putExtra("customerName", item.CustomerName);
                    context.startActivity(i);
                    dialogFragment.dismiss();
                }
            });
            //binding.tvConstructionEmployee.setText(item.CollectEmployee);
            /*//textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressDate);
            binding.tvExProgressFloor.setText(item.ExProgressFloor);
            //.setText(((Dong) data.get(position)).ProgressDate);
            binding.edtProgressFloor.setTag(item);
            binding.edtProgressFloor.setText(item.ProgressFloor);
            String exProgressDate = item.ExProgressDate;
            String progressDate = item.ProgressDate;


            if (!exProgressDate.equals(""))
                binding.tvExYearMonth.setText(exProgressDate.substring(0, 4) + "\n" + exProgressDate.substring(5));
            else
                binding.tvExYearMonth.setText("");
            binding.tvExProgressFloor.setText(item.ExProgressFloor);
            if (!progressDate.equals(""))
                binding.tvYearMonth.setText(progressDate.substring(0, 4) + "\n" + progressDate.substring(5));
            else
                binding.tvYearMonth.setText("");*/

            /*binding.edtProgressFloor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        notifyItemChanged(position);
                        //edtProgressFloor.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                        //layoutTop.requestFocus();
                        //notifyDataSetChanged();
                        //HideKeyBoard(context);
                    }
                }
            });*/
        }
    }

    public void addItem(Location item) {
        items.add(item);
    }

    public void setItems(ArrayList<Location> items) {
        this.items = items;
    }

    public Location getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Location item) {
        items.set(position, item);
    }


    /*public void deleteDongProgressFloorReturn(String dong, Dong item) {
        String url = context.getString(R.string.service_address) + "deleteDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        DeleteDongProgressFloorReturn gsod = new DeleteDongProgressFloorReturn(url, values, item);
        gsod.execute();
    }

    public void setDongProgressFloorReturn(String dong, TextView v, Dong item, String progressFloor) {
        String url = context.getString(R.string.service_address) + "setDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        values.put("ProgressFloor", progressFloor);
        SetDongProgressFloorReturn gsod = new SetDongProgressFloorReturn(url, values, v, item, fromDate);
        gsod.execute();
    }

    public class DeleteDongProgressFloorReturn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        Dong item;

        DeleteDongProgressFloorReturn(String url, ContentValues values, Dong item) {
            this.url = url;
            this.values = values;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                layoutTop.requestFocus();
                item.ProgressFloor = "";
                item.ProgressDate = "";
                notifyDataSetChanged();
                HideKeyBoard(context);
                Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    public class SetDongProgressFloorReturn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String fromDate;
        TextView v;
        Dong item;

        SetDongProgressFloorReturn(String url, ContentValues values, TextView v, Dong item, String fromDate) {
            this.url = url;
            this.values = values;
            this.fromDate = fromDate;
            this.v = v;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                layoutTop.requestFocus();
                item.ProgressFloor = v.getText().toString();
                item.ProgressDate = this.fromDate;
                notifyDataSetChanged();
                HideKeyBoard(context);
                Toast.makeText(context, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }*/

    /*private void startProgress() {
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
    public void progressON() {

    }

    @Override
    public void progressON(String message) {

    }

    @Override
    public void progressON(String message, Handler handler) {

    }

    @Override
    public void progressOFF(String className) {

    }

    @Override
    public void progressOFF2(String className) {

    }

    @Override
    public void progressOFF() {

    }

    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }*/
}

