package com.hdu.homemart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText edID,edPass;
    Button btLogin,btJoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("동우몰 로그인창");

        edID = findViewById(R.id.edID);
        edPass = findViewById(R.id.edPass);
        btLogin = findViewById(R.id.btLogin);
        btJoin = findViewById(R.id.btJoin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edID.getText().toString();
                String pass = edPass.getText().toString();

                HttpLogintask task = new HttpLogintask();
                task.execute(id,pass);
            }
        });
        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =  new Intent(MainActivity.this,JoinActivity.class);
                startActivity(it);
            }
        });



    }

    class HttpLogintask extends AsyncTask<String,Void,String>{
        String address;
        String sendMsg,reciveMsg;
        String adminName;
        ProgressDialog dlg = new ProgressDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/MartServer/Login.jsp";
            dlg.setMessage("접속중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            //로그인 성공시 다음 화면 실행
            if (reciveMsg.equals("성공")){
                Toast.makeText(getApplicationContext(),adminName+"관리자님 반가워요!~",Toast.LENGTH_SHORT).show();

                //인텐트 생성 및 startActivity실행
                Intent it = new Intent(MainActivity.this,MartListActivity.class);

                startActivity(it);


            }else if (reciveMsg.equals("고객성공")){
                Toast.makeText(getApplicationContext(),adminName+"고객님 반가워요!~",Toast.LENGTH_SHORT).show();
                Intent it = new Intent(MainActivity.this,UserMartListActivity.class);
                startActivity(it);
            }
            else if (reciveMsg.equals("실패")) {
                Toast.makeText(getApplicationContext(), "패스워드오류", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "ID="+strings[0]+"&PASS="+strings[1];

                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK){
                    //들어온 데이터 xml을 파싱하여 처리
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");

                    parser.setInput(isr);

                    int eventType = parser.getEventType();

                    String tag;
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                //태그 이름 추출
                                tag = parser.getName();
                                if (tag.equals("LOGIN")){
                                    //태그 다음의 TEXT추출
                                    reciveMsg = parser.nextText();
                                }else if (tag.equals("NAME")){
                                    adminName = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }

                                break;
                        }
                        eventType = parser.next();
                    }//while
                }


            }catch (Exception e){
                e.printStackTrace();
            }


            return reciveMsg;
        }
    }
    }






