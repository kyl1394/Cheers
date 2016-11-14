package com.victoryroad.cheers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by krohlfing on 11/4/2016.
 */

public class AddCustomDrinkDialog extends Dialog implements android.view.View.OnClickListener {
    public Activity activity;
    public Dialog dialog;
    public Button cancel, save;

    public AddCustomDrinkDialog(Activity activity) {
        super(activity);

        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_custom_drink);
        save = (Button) findViewById(R.id.save_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                activity.finish();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
