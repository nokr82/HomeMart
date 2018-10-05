package com.hdu.homemart;

import android.app.ProgressDialog;
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

public class JoinActivity extends AppCompatActivity {

    EditText edID2,edPass2,edName;

    Button btOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setTitle("동우몰 회원가입창");

        edID2 = findViewById(R.id.edID2);
        edPass2 = findViewById(R.id.edPass2);
        edName = findViewById(R.id.edName);

        btOk = findViewById(R.id.btOk);


        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String id = edID2.getText().toString();
                String pass = edPass2.getText().toString();




                HttpJointask task = new HttpJointask();
                task.execute(name,id,pass);
            }
        });


    }



    class HttpJointask extends AsyncTask<String,Void,String>{

        String address;
        String sendMsg,reciveMsg;

        ProgressDialog dlg = new ProgressDialog(JoinActivity.this);




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address ="http://192.168.0.18:8080/MartServer/Join.jsp";
            dlg.setMessage("가입중!~");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();
            if (reciveMsg.equals("등록성공")){
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
                finish();
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

                sendMsg = "NAME="+strings[0]+"&ID="+strings[1]+"&PASS="+strings[2];

                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode()==conn.HTTP_OK){
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");
                    parser.setInput(isr);

                    int eventType = parser.getEventType();
                    String tag;

                    while (eventType != XmlPullParser.END_DOCUMENT){
                        if (eventType == XmlPullParser.START_TAG){
                            tag=parser.getName();
                            if (tag.equals("REG")){
                                reciveMsg=parser.nextText();
                            }
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




}
