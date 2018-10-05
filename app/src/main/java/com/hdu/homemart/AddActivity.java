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

public class AddActivity extends AppCompatActivity {

    EditText edTitle, edPrice, edCate, edIntro, edSize;
    Button btAdd2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        edIntro = findViewById(R.id.edIntro);
        edPrice = findViewById(R.id.edPrice);
        edTitle = findViewById(R.id.edTitle);
        edCate = findViewById(R.id.edCate);
        edSize = findViewById(R.id.edSize);
        btAdd2 = findViewById(R.id.btAdd2);

        btAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAddTask task = new HttpAddTask();
                String title = edTitle.getText().toString();
                String cate = edCate.getText().toString();
                String size = edSize.getText().toString();
                String price = edPrice.getText().toString();
                String intro = edIntro.getText().toString();

                task.execute(title,cate,size,price,intro);


            }
        });
    }
        class HttpAddTask extends AsyncTask<String, Void, String> {
            String address;
            String sendMsg, reciveMsg;

            ProgressDialog dlg = new ProgressDialog(AddActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                address = "http://192.168.0.18:8080/MartServer/MartAdd.jsp";
                dlg.setMessage("진열중...");
                dlg.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dlg.dismiss();

                //reciveMsg에 들어오는 내용에 따라서 실패시 토스트만띄움 성공시는 토스트 띄우고 액티비티 종료

                if (reciveMsg.equals("추가성공")) {
                    Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();
                    finish();
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

                    sendMsg = "TITLE=" + strings[0] + "&CATE=" + strings[1] + "&SIZE=" + strings[2] + "&PRICE=" + strings[3]
                            + "&INTRO=" + strings[4];
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
    }



