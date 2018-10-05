package com.hdu.homemart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderActivity extends AppCompatActivity {

    ImageView img3;
    TextView txSize3,txTitle3,txPrice3,txPay;
    EditText edAddress,edOrder;
    Spinner spAmount;
    Button btBuy;

    String[] Amount={"1","2","3","4","5","6","7","8","9","10"};

    ArrayAdapter<String> arrayAdapter;

    int itemNum;
    int itemPri;
    String strNum ;

    String strAmount=Amount[0];

    int pos;

    String orderAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitle("동우몰 주문화면");


        edOrder = findViewById(R.id.edOrder);
        img3 = findViewById(R.id.img3);
        txTitle3 = findViewById(R.id.txTitle2);
        txSize3 = findViewById(R.id.txSize2);
        txPrice3 = findViewById(R.id.txPrice2);
        spAmount = findViewById(R.id.spAmount);
        txPay = findViewById(R.id.txPay);
        edAddress = findViewById(R.id.edAddress);
        btBuy = findViewById(R.id.btBuy);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Amount);



        spAmount.setAdapter(arrayAdapter);

        spAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position + 1;

                int paycal = pos * itemPri;

                txPay.setText(String.valueOf(paycal));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpOrderTask task = new HttpOrderTask();
                String size = txSize3.getText().toString();
                String title = txTitle3.getText().toString();
                //String size = edSize.getText().toString();
                String amount = String.valueOf(pos);

                String pay = txPay.getText().toString();
                String address = edAddress.getText().toString();
                String order = edOrder.getText().toString();
                //sendMsg = "SIZE=" + strings[0] + "&TITLE=" + strings[1] + "&AMOUNT=" + strings[2] + "&PAY=" + strings[3]
                  //      + "&ADDRESS=" + strings[4]+"&ORDER="+strings[5];
                task.execute(size,title,amount,pay,address,order);

            }
        });





        Intent oit = getIntent();
        String str = oit.getStringExtra("TI");
        txTitle3.setText(str);
        str=oit.getStringExtra("SI");
        txSize3.setText(str);
        //숫자열로받아오는부분
        itemPri=oit.getIntExtra("PR", 0);
        txPrice3.setText(String.valueOf(itemPri));

        itemNum = oit.getIntExtra("POS",0);
        strNum = String.valueOf(itemNum);


        //주문상품이미지
        if (itemNum==1){
            img3.setImageResource(R.drawable.dome);
        }else if (itemNum==2){
            img3.setImageResource(R.drawable.conver);
        }else if (itemNum==3){
            img3.setImageResource(R.drawable.suit);
        }else if (itemNum==4){
            img3.setImageResource(R.drawable.top);
        }else if (itemNum==5){
            img3.setImageResource(R.drawable.shirt);
        }else if (itemNum==6){
            img3.setImageResource(R.drawable.shorts);
        }

    }//on

    class HttpOrderTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(OrderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/MartServer/MartOrder.jsp";
            dlg.setMessage("구매중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            //reciveMsg에 들어오는 내용에 따라서 실패시 토스트만띄움 성공시는 토스트 띄우고 액티비티 종료

            if (reciveMsg.equals("추가성공")) {
                Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();
                Intent it = new Intent(OrderActivity.this,DeliveryListActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "SIZE=" + strings[0] + "&TITLE=" + strings[1] + "&AMOUNT=" + strings[2] + "&PAY=" + strings[3]
                        + "&ADDRESS=" + strings[4]+"&ORDER="+strings[5];
                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    //들어온 데이터 xml을 파싱하여 처리
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    parser.setInput(isr);

                    int eventType = parser.getEventType();

                    String tag = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            tag = parser.getName();
                            if (tag.equals("STATE")) {
                                reciveMsg = parser.nextText();
                            }
                        }
                        eventType = parser.next();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return reciveMsg;
        }
    }



}//class
