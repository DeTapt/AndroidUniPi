package com.androidunipi.model;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class CustomDialog {
	
	public CustomDialog(){}
	
	public void ChangeDialogUI(AlertDialog alert){
		
	    //title color imba hack :P
	    int textViewId = alert.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
		TextView tv = (TextView) alert.findViewById(textViewId);
		tv.setTextColor(Color.parseColor("#125688"));
		//divider color imba hack :P
		int dividerId = alert.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = alert.findViewById(dividerId);
		divider.setBackgroundColor(Color.parseColor("#125688"));
		
	}

}

