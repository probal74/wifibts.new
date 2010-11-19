package com.polandro.wifibts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class TextEntryActivity extends Activity {
	   private EditText et;
	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);

	        setContentView(R.layout.activity_text_entry);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	        // title
	        try {
	            String s = getIntent().getExtras().getString("title");
	            if (s.length() > 0) {
	                this.setTitle(s);
	            }
	        } catch (Exception e) {
	        }
	        // value

	        try {
	            et = (EditText) findViewById(R.id.txtValue);
	            et.setText(getIntent().getExtras().getString("value"));
	        } catch (Exception e) {
	        }
	        // button
	        ((Button) findViewById(R.id.btnDone)).setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                executeDone();
	            }
	        });
	}
	   
	    @Override
	    public void onBackPressed() {
	        executeDone();
	        super.onBackPressed();
	    }
	    	    
	    private void executeDone() {
	        Intent resultIntent = new Intent();
	        resultIntent.putExtra("value", TextEntryActivity.this.et.getText().toString());
	        resultIntent.putExtra("_id", getIntent().getExtras().getString("_id"));
	        setResult(Activity.RESULT_OK, resultIntent);
	        finish();
	    }

}
