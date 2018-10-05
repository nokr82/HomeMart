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

public class UpDelActivity extends AppCompatActivity {
    EditText edTitle2, edPrice2, edCate2, edIntro2, edSize2;
    Button btDel,btModi;

    int itemNum;
    String strNum ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_del);
        setTitle("동우몰 수정화면");

        edIntro2 = findViewById(R.id.edIntro2);
        edPrice2 = findViewById(R.id.edPrice2);
        edTitle2 = findViewById(R.id.edTitle2);
        edCate2 = findViewById(R.id.edCate2);
        edSize2 = findViewById(R.id.edSize2);
        btDel = findViewById(R.id.btDel);
        btModi = findViewById(R.id.btModi);

        Intent upit = getIntent();
        String str = upit.getStringExtra("TI");
        edTitle2.setText(str);
        str=upit.getStringExtra("CA");
        edCate2.setText(str);
        str=upit.getStringExtra("SI");
        edSize2.setText(str);
        //숫자열로받아오는부분
        int inum=upit.getIntExtra("PR",0);
        edPrice2.setText(String.valueOf(inum));
        str=upit.getStringExtra("IN");
        edIntro2.setText(str);

        itemNum = upit.getIntExtra("POS",0);
        strNum = String.valueOf(itemNum);


        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUpDelTask task = new HttpUpDelTask();
                task.execute(strNum);
                finish();

            }
        });
        btModi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUpDelTask task = new HttpUpDelTask();
                String title = edTitle2.getText().toString();
                String cate = edCate2.getText().toString();
                String size = edSize2.getText().toString();
                String price = edPrice2.getText().toString();
                String intro = edIntro2.getText().toString();
                //스트링스 배열로 넘어간다 sendMsg로 보내준다
                task.execute("수정",strNum,title,cate,size,price,intro);
                finish();

            }
        });

    }//on


    class HttpUpDelTask extends AsyncTask<String,Void,String>{
        String address;
        String sendMsg,reciveMsg;
        ProgressDialog updlg = new ProgressDialog(UpDelActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/MartServer/";
            updlg.setMessage("진행중!!~");
            updlg.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updlg.dismiss();

            //receiveMsg에 성공이면 토스트 출력, 액티비티 종료
            //실패면 토스트 출력
            if (reciveMsg.equals("성공")){
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            if(strings[0].equals("수정"))
                address += "MartUpdate.jsp";
            else
                address += "MartDelete.jsp";
            try {
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //수정용 sendMsg와 삭제용 sendMsg 구분
                if (strings[0].equals("수정")) {
                    sendMsg = "NO=" + strings[1] + "&TITLE=" + strings[2] + "&CATE=" + strings[3] +
                            "&SIZE=" + strings[4] + "&PRICE=" + strings[5] + "&INTRO=" + strings[6];

                }else {
                    sendMsg="NO="+strings[0];
                }
                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode()==conn.HTTP_OK){
                    //들어온 데이터 xml을 파싱하여 처리
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");

                    parser.setInput(isr);

                    int eventType = parser.getEventType();

                    String tag =null;
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        if (eventType==XmlPullParser.START_TAG) {
                            tag = parser.getName();
                            if (tag.equals("STATE")){
                                reciveMsg = parser.nextText();
                            }
                        }
                        eventType = parser.next();
                    }

                }else{
                    reciveMsg="전송실패";
                }


            }catch (Exception e){
                reciveMsg="전송실패";
                e.printStackTrace();
            }




            return reciveMsg;


        }
    }






}//class
