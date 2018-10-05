package com.hdu.homemart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class UserOrderActivity extends AppCompatActivity {
    TextView txTitle2, txPrice2, txCate2, txIntro2;
    ImageView img2;
    Spinner spSize;
    Button btOrder,btCancel;

    String[] strSize = {"M","L","XL"},shoesSize={"230","240","250","260","270","280"};


    ArrayAdapter<String> arrayAdapter,arrayAdapter2;


    int itemNum;
    String strNum ;
    String strProdSize = strSize[0];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        setTitle("동우몰 주문화면");

        img2 = findViewById(R.id.img2);
        txIntro2 = findViewById(R.id.txIntro2);
        txPrice2 = findViewById(R.id.txPrice2);
        txTitle2 = findViewById(R.id.txTitle2);
        txCate2 = findViewById(R.id.txCate2);
        spSize = findViewById(R.id.spSize);
        btOrder = findViewById(R.id.btOrder);
        btCancel = findViewById(R.id.btCancel);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,strSize);
        arrayAdapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,shoesSize);

        spSize.setAdapter(arrayAdapter);

        Intent uoit = getIntent();
        String str = uoit.getStringExtra("TI");
        txTitle2.setText(str);
        str=uoit.getStringExtra("CA");
        txCate2.setText(str);
        //str=uoit.getStringExtra("SI");
        //숫자열로받아오는부분
        final int inum=uoit.getIntExtra("PR",0);
        txPrice2.setText(String.valueOf(inum));
        str=uoit.getStringExtra("IN");
        txIntro2.setText(str);

        itemNum = uoit.getIntExtra("POS",0);
        strNum = String.valueOf(itemNum);

        itemNum = uoit.getIntExtra("POS",0);
        strNum = String.valueOf(itemNum);

        //주문상품이미지
        if (itemNum==1){
            img2.setImageResource(R.drawable.dome);
        }else if (itemNum==2){
            spSize.setAdapter(arrayAdapter2);
            img2.setImageResource(R.drawable.conver);
            strProdSize = shoesSize[0];
        }else if (itemNum==3){
            img2.setImageResource(R.drawable.suit);
        }else if (itemNum==4){
            img2.setImageResource(R.drawable.top);
        }else if (itemNum==5){
            img2.setImageResource(R.drawable.shirt);
        }else if (itemNum==6){
            img2.setImageResource(R.drawable.shorts);
        }

        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oit  = new Intent(UserOrderActivity.this,OrderActivity.class);
                //리스트뷰에서 선택한 항목의 DB번호를 구하여 UpdateActivity로 넘김
                int pos  = itemNum;
                String ti = txTitle2.getText().toString();
                //String si = spSize.onClick();
               //숫자열로된문장 인텐트로보내는법1
                String pr = txPrice2.getText().toString();
                int prnum = Integer.parseInt(pr);

                //수정데이터 넘기기
                oit.putExtra("POS",pos);
                oit.putExtra("TI",ti);

                //숫자열로된문장 인텐트로보내는법2
                oit.putExtra("PR",prnum);

                oit.putExtra("SI", strProdSize);


                startActivity(oit);

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(itemNum==2)
                    strProdSize = shoesSize[position];
                else
                    strProdSize = strSize[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }
}
