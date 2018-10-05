package com.hdu.homemart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserMartListActivity extends AppCompatActivity {

    Spinner SP2;
    ListView list2;
    //리스트아이템
    TextView txTitle,txPrice,txCate,txSize;
    ImageView img;

    ArrayList<Mydata> arrdata2= new ArrayList<>();


    String[] strtitle = {"상의","하의","신발","정장"};

    ArrayAdapter<String> arrayAdapter;

    MyAdapter2 mad2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_list);
        setTitle("동우몰 상품리스트");

        list2 = findViewById(R.id.list);
        SP2 = findViewById(R.id.SP);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,strtitle);

        SP2.setAdapter(arrayAdapter);





        mad2= new MyAdapter2(this);
        list2.setAdapter(mad2);

        SP2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //리스트수정/삭제버튼처리
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent uoit  = new Intent(UserMartListActivity.this,UserOrderActivity.class);
                //리스트뷰에서 선택한 항목의 DB번호를 구하여 UpdateActivity로 넘김
                int pos  = arrdata2.get(position).mNo;
                String ti = arrdata2.get(position).mtitle;
                String ca = arrdata2.get(position).mCate;
                String si = arrdata2.get(position).mSize;
                int pr = arrdata2.get(position).mPrice;
                String in = arrdata2.get(position).mIntro;
                //수정데이터 넘기기
                uoit.putExtra("POS",pos);
                uoit.putExtra("TI",ti);
                uoit.putExtra("CA",ca);
                uoit.putExtra("SI",si);
                uoit.putExtra("PR",pr);
                uoit.putExtra("IN",in);

                startActivity(uoit);

            }
        });


    }//온

    @Override
    protected void onResume() {
        super.onResume();
        HttpListTask2 task = new HttpListTask2();
        task.execute("마트목록");
    }


    class MyAdapter2 extends BaseAdapter{
        Context con;
        MyAdapter2(Context c){
            con=c;
        }

        @Override
        public int getCount() {
            return arrdata2.size();
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

                convertView = lif.inflate(R.layout.list_item,parent,false);
            }
            txTitle = convertView.findViewById(R.id.txTitle);
            txPrice = convertView.findViewById(R.id.txPrice);
            txCate = convertView.findViewById(R.id.txCate);
            txSize = convertView.findViewById(R.id.txSize);
            img = convertView.findViewById(R.id.img);

            Mydata md = arrdata2.get(position);
            //상품이미지 변경!!!!!
            if (md.mNo==1){
                img.setImageResource(R.drawable.dome);
            }else if (md.mNo==2){
                img.setImageResource(R.drawable.conver);
            }else if (md.mNo==3){
                img.setImageResource(R.drawable.suit);
            }else if (md.mNo==4){
                img.setImageResource(R.drawable.top);
            }else if (md.mNo==5){
                img.setImageResource(R.drawable.shirt);
            }else if (md.mNo==6){
                img.setImageResource(R.drawable.shorts);
            }else {
                img.setImageResource(R.drawable.ready);
            }

            txSize.setText(md.mSize);
            txTitle.setText(md.mtitle);
            txCate.setText(md.mCate);
            txPrice.setText(String.valueOf(md.mPrice)+"원");

            return convertView;
        }
    }

    class HttpListTask2 extends AsyncTask<String,Void,String>{
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog listdlg = new ProgressDialog(UserMartListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/MartServer/MartList.jsp";

            listdlg.setMessage("상품 준비중!~~");
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
                    Mydata tdb=null;
                    arrdata2.clear();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    //BookData 객체 생성
                                    tdb = new Mydata();
                                }else if (tag.equals("NO")){
                                    String no = parser.nextText();
                                    //숫자 문자열로 전환
                                    tdb.mNo = Integer.parseInt(no);
                                }else if (tag.equals("TITLE")){
                                    tdb.mtitle = parser.nextText();
                                }else if (tag.equals("CATE")){
                                    tdb.mCate = parser.nextText();
                                }else if (tag.equals("SIZE")){
                                    tdb.mSize = parser.nextText();
                                }else if (tag.equals("PRICE")){
                                    String price = parser.nextText();
                                    tdb.mPrice = Integer.parseInt(price);
                                }else if(tag.equals("INTRO")){
                                    tdb.mIntro = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    arrdata2.add(tdb);
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





}//메인
