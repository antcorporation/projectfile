package com.example.bimvendpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class TripMachineEnterResult extends AppCompatActivity implements TripMachineProductInputAdapter.passData {

    private TripMachines tripMachines;
    private List<TripMachineProduct> tripMachineProduct = new ArrayList<>();
    private EditText editTextName,editTextType,editTextLocation,editTextCash,editTextComment,editTextCashInput,editTextCoinsInput,editTextBillsInput,editTextAdjustInput,editTextLastMeter,editTextCurrentMeter;
    private RecyclerView recyclerViewProducts;
    private TripMachineProductInputAdapter mAdapter;
    private double cashCollected;
    private Button buttonSave,buttonCancel;
    private String tripNumber;
    private String status;
    private int machineNumber;
    private sendMachineData sendMachineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_machine_enter_result);

        tripMachines = (TripMachines) getIntent().getExtras().getSerializable("item");
        tripNumber = (String) getIntent().getExtras().getSerializable("tripNumber");
        machineNumber = (int) getIntent().getExtras().getSerializable("machineNumber");
        status = (String) getIntent().getExtras().getSerializable("status");

        if(tripMachines.getTripMachineProducts()!= null){
            tripMachineProduct = tripMachines.getTripMachineProducts();

        }

        hideKeyboard();

        TripMachineEnterResult.this.setTitle("Machine Code - "+ tripMachines.getCode());

        editTextAdjustInput = findViewById(R.id.trip_machine_adjust_input);
        editTextName = findViewById(R.id.trip_machine_name);
        editTextType = findViewById(R.id.trip_machine_type);
        editTextLocation = findViewById(R.id.trip_machine_location);
        editTextCash = findViewById(R.id.trip_machine_collected_cash);
        editTextComment = findViewById(R.id.trip_machine_comments);
        editTextCashInput = findViewById(R.id.trip_machine_cash_input);
        editTextCoinsInput = findViewById(R.id.trip_machine_Coins_input);
        editTextBillsInput = findViewById(R.id.trip_machine_bills_input);
        editTextLastMeter = findViewById(R.id.trip_machine_lastMeter_input);
        editTextCurrentMeter = findViewById(R.id.trip_machine_currentMeter_input);

        buttonSave = findViewById(R.id.trip_button_add);
        buttonCancel = findViewById(R.id.trip_button_cancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryWriteData();
            }
        });

        init();

        if(status.equals("posted")){
            buttonSave.setVisibility(View.GONE);
            buttonCancel.setText("back");
        }

        TextWatcher updateEditTexts = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("text changed", "onTextChanged: ");
                double cashCollected;
                int coins, bills;

                try {
                    coins = Integer.parseInt(editTextCoinsInput.getText().toString());
                } catch (Exception e) {
                    coins = 0;
                }

                String str = editTextAdjustInput.getText().toString();
                double adjust = 0.0;
                if(str.matches("[+,-][0-9.]+")){

                    try {
                        adjust = Double.parseDouble(str.substring(1));
                        if(str.substring(0,1).equals("-")){
                            adjust = adjust * (-1.0);
                        }
                        Log.d("adjust value", "onTextChanged: adjust"+adjust);
                    } catch (Exception e) {
                        adjust = 0.0;
                    }
                }
                else{
                    editTextAdjustInput.setError("give a valid input");
                    editTextAdjustInput.requestFocus();
                }

                try {
                    bills = Integer.parseInt(editTextBillsInput.getText().toString());
                } catch (Exception e) {
                    bills = 0;
                }
                cashCollected = coins*0.25 + bills + adjust;

                editTextCashInput.setText(String.format("%.2f", cashCollected));
                Log.d("adjust", "onTextChanged: " + adjust);

                tripMachines.setBills(bills);
                if(adjust>0){
                    tripMachines.setAdjust("+"+adjust);
                }else{
                    tripMachines.setAdjust(String.valueOf(adjust));
                }
                tripMachines.setCashCollected(cashCollected);
                tripMachines.setCoins(coins);


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("after change", "afterTextChanged: " + editTextCashInput.getText().toString());
            }
        };



        editTextCoinsInput.addTextChangedListener(updateEditTexts);
        editTextBillsInput.addTextChangedListener(updateEditTexts);
        editTextAdjustInput.addTextChangedListener(updateEditTexts);
        editTextCurrentMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int lastMeter,currentMeter;

                try {
                    lastMeter = Integer.parseInt(editTextLastMeter.getText().toString());
                } catch (Exception e) {
                    lastMeter = 0;
                }

                currentMeter = lastMeter;
                String str = editTextCurrentMeter.getText().toString();
                if(str.matches("[0-9.]+")){

                    try {
                        currentMeter = Integer.parseInt(str);
                    } catch (Exception e) {
                        currentMeter = lastMeter;
                    }
                }
                else{
                    editTextCurrentMeter.setError("give a valid input");
                    editTextCurrentMeter.requestFocus();
                }

                if(currentMeter > lastMeter){
                    editTextCoinsInput.setText(String.valueOf(currentMeter-lastMeter));
                    tripMachines.setCurrentMeterReading(currentMeter);
                }else{
                    editTextCurrentMeter.setError("Meter value can't be less than last one");
                    editTextCurrentMeter.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


    }

    private void tryWriteData() {
        tripMachines.setComment(editTextComment.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("machine", tripMachines);
        intent.putExtra("machineNo", machineNumber);
        setResult(RESULT_OK, intent);
        finish();

    }

    public interface sendMachineData{
        void sendData(TripMachines machines,int index);
    }

    private void init() {
        editTextAdjustInput.setText(tripMachines.getAdjust());
        editTextName.setText(tripMachines.getName());
        editTextType.setText(tripMachines.getType());
        editTextLocation.setText(tripMachines.getLocation());
        editTextCash.setText(String.valueOf(tripMachines.getCashCollected()));
        editTextComment.setText(tripMachines.getComment());
        editTextCashInput.setText(String.valueOf(tripMachines.getCashCollected()));
        editTextCoinsInput.setText(String.valueOf(tripMachines.getCoins()));
        editTextBillsInput.setText(String.valueOf(tripMachines.getBills()));
        editTextLastMeter.setText(String.valueOf(tripMachines.getLastMeterReading()));
        editTextCurrentMeter.setText(String.valueOf(tripMachines.getCurrentMeterReading()));

        editTextLastMeter.setEnabled(false);

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerViewProducts = findViewById(R.id.trip_machine_product_input_recyclerView);
        Log.d("list size", "initializeRecyclerView: list size " + tripMachineProduct.size());

        mAdapter = new TripMachineProductInputAdapter(tripMachineProduct,this,this);
        RecyclerView.LayoutManager mLayoutmanager =new LinearLayoutManager(this);
        recyclerViewProducts.setLayoutManager(mLayoutmanager);
        recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewProducts.setAdapter(mAdapter);
    }


    @Override
    public void passData(TripMachineProduct tripMachineProduct, int index) {
        Log.d("pass the data ", "passData: "+tripMachineProduct.getNewCount()+" "+tripMachineProduct.getFilled()+" "+tripMachineProduct.getPresentCount()+" "+tripMachineProduct.getSoldOrWin());
        tripMachines.getTripMachineProducts().get(index).setNewCount(tripMachineProduct.getNewCount());
        tripMachines.getTripMachineProducts().get(index).setSoldOrWin(tripMachineProduct.getSoldOrWin());
        tripMachines.getTripMachineProducts().get(index).setFilled(tripMachineProduct.getFilled());
        tripMachines.getTripMachineProducts().get(index).setPresentCount(tripMachineProduct.getPresentCount());
    }
}
