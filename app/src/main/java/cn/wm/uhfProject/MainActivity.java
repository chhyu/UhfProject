package cn.wm.uhfProject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rscja.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

import cn.com.example.rfid.driver.RfidDriver;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect, btnInventory;
    private RecyclerView rcvEpcList;
    private RfidDriver driver;
    private EpcListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.handleRfidPower(this, true);
        registerReceover();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.handleRfidPower(this, false);
        unregisterReceiver(mRfidKeyReceiver);
    }

    private List<String> epcList = new ArrayList<>();

    private void initData() {
        driver = new RfidDriver();
    }

    private void registerReceover() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.eastaeon.rfidkey.KEY_DOWN");
        filter.addAction("com.eastaeon.rfidkey.KEY_UP");
        registerReceiver(mRfidKeyReceiver, filter);
    }

    private void initView() {
        btnConnect = findViewById(R.id.btnConnect);
        btnInventory = findViewById(R.id.btnInventory);
        rcvEpcList = findViewById(R.id.rcvEpcList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcvEpcList.setLayoutManager(layoutManager);
        adapter = new EpcListAdapter(this, epcList);
        rcvEpcList.setAdapter(adapter);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doConnect();
            }
        });
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrStopInventory();
            }
        });
    }

    private void startOrStopInventory() {
        if (driver != null) {
            if (loopFlag) {
                driver.stopRead();
                btnInventory.setText("Inventory");
            } else {
                epcList.clear();
                driver.readMore();
                btnInventory.setText("stop");
                new TagThread().start();
            }
            loopFlag = !loopFlag;
        }
    }


    private void doConnect() {
        if (driver != null) {
            int status = driver.initRFID(Utils.getCOMPort());
            if (status == -1000) {
                Toast.makeText(MainActivity.this, "设备连接失败", Toast.LENGTH_SHORT).show();
                btnConnect.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
                btnConnect.setEnabled(false);
            }
        }
    }

    private boolean loopFlag = false;

    class TagThread extends Thread {

        public void run() {
            while (loopFlag) {
                Log.e("lmsg", "GetBufData start");
                String strEpc = driver.GetBufData();
                Log.e("lmsg", "GetBufData: " + strEpc);
                if (!StringUtility.isEmpty(strEpc)) {
                    Message msg = handler.obtainMessage();
                    msg.obj = strEpc;
                    msg.what = 123;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123) {
                epcList.add((String) msg.obj);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private BroadcastReceiver mRfidKeyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keycode", 0);
            String action = intent.getAction();//
            if (action.equals("com.eastaeon.rfidkey.KEY_DOWN")) {
                driver.readMore();
                loopFlag = true;
                new TagThread().start();
            } else if (action.equals("com.eastaeon.rfidkey.KEY_UP")) {
                loopFlag = false;
                driver.stopRead();
            }
        }
    };
}