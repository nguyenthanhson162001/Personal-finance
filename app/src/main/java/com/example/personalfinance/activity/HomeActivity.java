package com.example.personalfinance.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.R;
import com.example.personalfinance.adapter.DateAdapter;
import com.example.personalfinance.entity.DateOfMonth;
import com.example.personalfinance.entity.MonthOfYear;
import com.example.personalfinance.entity.Spending;
import com.example.personalfinance.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView txtTotalMoney;
    private TextView txtMonthInMoney;
    private TextView txtMonthOutMoney;
    private TextView txtMonthBefore;
    private TextView txtMonthAfter;
    private TextView txtMonthNow;
    private ImageView imgNext;
    private ImageView imgPrevious;
    private TextView txtMonthTotalMoney;
    private ListView listViewDate;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private User user;
    private MonthOfYear monthOfYear;
    private DateAdapter adapter;
    private int indexMonth = -1;
    private DecimalFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtTotalMoney = findViewById(R.id.txtTotalMoney);
        txtMonthInMoney = findViewById(R.id.txtMonthInMoney);
        txtMonthOutMoney = findViewById(R.id.txtMonthOutMoney);
        txtMonthAfter = findViewById(R.id.txtMonthAfter);
        txtMonthBefore = findViewById(R.id.txtMonthBefore);
        txtMonthNow = findViewById(R.id.txtMonthNow);
        txtMonthTotalMoney = findViewById(R.id.txtMonthTotalMoney);
        listViewDate  = findViewById(R.id.listviewDate);
        imgNext = findViewById(R.id.imageNext);
        imgPrevious = findViewById(R.id.imagePrevious);

        formatter = new DecimalFormat("###,###,###");


        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child("QvDrtYaWYOSiONP3u25ivw7Wp5a2");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadDataFromFirebase(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexMonth --;
                loadDataInMonth();
                if (indexMonth == 0){
                    imgPrevious.setEnabled(false);
                }
                imgNext.setEnabled(true);
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexMonth ++;
                loadDataInMonth();
                if (indexMonth == user.getMonthOfYears().size()-1){
                    imgNext.setEnabled(false);
                }
                imgPrevious.setEnabled(true);
            }
        });




    }

    private void loadDataFromFirebase(DataSnapshot snapshot){
       user = snapshot.getValue(User.class);
       indexMonth  = user.getMonthOfYears().size()-1;

       imgNext.setEnabled(false);


       if (indexMonth>=0){
           double total = 0;

           txtTotalMoney.setText(formatter.format(user.totalMoney()) + " đ");

           loadDataInMonth();
       }

    }

    private void loadDataInMonth() {

        if (indexMonth>0){
            imgPrevious.setEnabled(true);
            txtMonthBefore.setText(user.getMonthOfYears().get(indexMonth-1).getMonth()+ "/" + user.getMonthOfYears().get(indexMonth-1).getYear());
        }
        else {
            txtMonthBefore.setText("");
            imgPrevious.setEnabled(false);
        }

        if (indexMonth<user.getMonthOfYears().size()-1){
            txtMonthAfter.setText(user.getMonthOfYears().get(indexMonth+1).getMonth()+ "/" + user.getMonthOfYears().get(indexMonth+1).getYear());
        }else{
            imgNext.setEnabled(false);
            txtMonthAfter.setText("");
        }

        monthOfYear = user.getMonthOfYears().get(indexMonth);

        txtMonthNow.setText(monthOfYear.getMonth()+ "/" + monthOfYear.getYear());
        txtMonthInMoney.setText("+" +formatter.format(monthOfYear.totalInMoneyInMonth()) + " đ");
        txtMonthOutMoney.setText(formatter.format(monthOfYear.totalOutMoneyInMonth()) + " đ");
        double totalMonth = monthOfYear.totalInMoneyInMonth() + monthOfYear.totalOutMoneyInMonth();
        if (totalMonth>=0){
            txtMonthTotalMoney.setText("+" + formatter.format(totalMonth) + " đ");
        }else{
            txtMonthTotalMoney.setText(formatter.format(totalMonth) + " đ");
        }
        adapter = new DateAdapter(HomeActivity.this,R.layout.date_item,monthOfYear);
        listViewDate.setAdapter(adapter);
    }


}