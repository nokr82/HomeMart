package com.hdu.homemart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeliveryListActivity extends AppCompatActivity {

    ListView list2;

    //리스트아이템
    ImageView img4;
    TextView txBuyer,txBuySize,txBuyPrice,txAddress,txAmount,txProduct;

    ArrayList<DeliveryData> arrData =new ArrayList<>();



    MyAdapter2 mad2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);
        setTitle("동우몰 구매리스트");

        list2 = findViewById(R.id.list2);

        mad2 = new MyAdapter2(this);


        list2.setAdapter(mad2);










    }//on
    @Override
    protected void onResume() {
        super.onResume();
        HttpOrderListTask task = new HttpOrderListTask();
        task.execute("구매목록");
    }

    class MyAdapter2 extends BaseAdapter {
        Context con;
        MyAdapter2(Context c){
            con=c;
        }

        @Override
        public int getCount() {
            return arrData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){

                LayoutInflater lif = LayoutInflater.from(con);

                convertView = lif.inflate(R.layout.deliverly_item,parent,false);
            }
            //TextView txBuyer,txBuySize,txBuyPrice,txAddress,txAmount;
            txBuyer = convertView.findViewById(R.id.txBuyer);
            txBuySize = convertView.findViewById(R.id.txBuySize);
            txBuyPrice = convertView.findViewById(R.id.txBuyPrice);
            txAddress = convertView.findViewById(R.id.txAddress);
            txAmount = convertView.findViewById(R.id.txAmount);
            txProduct=convertView.findViewById(R.id.txProduct);
            img4 = convertView.findViewById(R.id.img4);

            DeliveryData dd = arrData.get(position);
            //상품이미지 변경!!!!!
            if (dd.dSize.equals("해골잠바")){
                img4.setImageResource(R.drawable.dome);
            }else if (dd.dSize.equals("컨버스척테일러")){
                img4.setImageResource(R.drawable.conver);
            }else if (dd.dSize.equals("파란정장")){
                img4.setImageResource(R.drawable.suit);
            }else if (dd.dSize.equals("커버낫반팔티")){
                img4.setImageResource(R.drawable.top);
            }else if (dd.dSize.equals("공유셔츠")){
                img4.setImageResource(R.drawable.shirt);
            }else if (dd.dSize.equals("반바지")){
                img4.setImageResource(R.drawable.shorts);
            }else {
                img4.setImageResource(R.drawable.ready);
            }

            txBuyer.setText("사이즈: "+dd.dOrder);
            txBuySize.setText("상품명: "+dd.dSize);
            txBuyPrice.setText("가격: "+String.valueOf(dd.dPrice)+"원");
            txAddress.setText("수량: "+dd.dAddress);
            txAmount.setText("배송지: "+dd.dAmount);
            txProduct.setText("구매자: "+dd.dProduct);




            return convertView;
        }
    }

    class HttpOrderListTask extends AsyncTask<String,Void,String> {
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog listdlg = new ProgressDialog(DeliveryListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/MartServer/OrderList.jsp";

            listdlg.setMessage("구매목록 준비중!~~");
            listdlg.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mad2.notifyDataSetChanged();
            listdlg.dismiss();

            if (reciveMsg != null){
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg="MSG="+strings[0];

                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode()==conn.HTTP_OK){
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");
                    parser.setInput(isr);

                    int eventType = parser.getEventType();
                    String tag = null;
                    //DB에서 가져오는 상품 정보저장
                    DeliveryData tdb=null;
                    arrData.clear();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                   
                                    tdb = new DeliveryData();
                                }else if (tag.equals("NO")){
                                    String no = parser.nextText();
                                    //숫자 문자열로 전환
                                    tdb.dNo = Integer.parseInt(no);
                                }else if (tag.equals("ORDER")){
                                    tdb.dOrder = parser.nextText();
                                }else if (tag.equals("SIZE")){
                                    tdb.dSize = parser.nextText();
                                }else if (tag.equals("ADDRESS")){
                                    tdb.dAddress = parser.nextText();
                                }else if (tag.equals("PRICE")){
                                    String price = parser.nextText();
                                    tdb.dPrice = Integer.parseInt(price);
                                }else if(tag.equals("AMOUNT")){
                                    tdb.dAmount = parser.nextText();
                                }else if(tag.equals("PRODUCT")){
                                    tdb.dProduct = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    arrData.add(tdb);
                                }
                                break;



                        }

                        eventType = parser.next();


                    }
                }



            }catch (Exception e){
                e.printStackTrace();
            }






            return reciveMsg;
        }
    }


}//class
