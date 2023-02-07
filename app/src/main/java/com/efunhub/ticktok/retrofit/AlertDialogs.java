package com.efunhub.ticktok.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class AlertDialogs {

	private ProgressDialog mDialog;
	private static AlertDialogs mInstance;

	public static AlertDialogs getInstance() {
		//TODO Get Single Instance

		if (mInstance == null) {
			mInstance = new AlertDialogs();
		}
		return mInstance;
	}

	public void onShowProgressDialog(Activity activity,
			boolean isShow) {
		// TODO Show ProgressDialog

		try {
			if (isShow) {
    		//	Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/Ubuntu-R.ttf");
		//		SpannableStringBuilder spannableSB = new SpannableStringBuilder("Loading...");
		//		spannableSB.setSpan(font, 0, spannableSB.length(), spannableSB.SPAN_EXCLUSIVE_EXCLUSIVE);
				//spannableSB.setSpan(new CustomTypefaceSpan("fonts/Roboto-Regular.ttf", font), 0, spannableSB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				//loadDialog = ProgressDialog.show(this, null, spannableSB, true, false);

				mDialog=ProgressDialog.show(activity,"","Please Wait",true);
			mDialog.show();
			} else {
				if (mDialog.isShowing())
					mDialog.dismiss();
			}
		} catch (Exception e) {

		}
	}



   public void onShowToastNotification(Activity activity,String msg){

	  Toast ltoast=Toast.makeText(activity,msg,Toast.LENGTH_LONG);
	  // ltoast.setGravity(Gravity.BOTTOM,0,0);
	   ltoast.show();
   }



	public void onHideKeyBoard(Activity mActivity) {
		final InputMethodManager imm = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);


			imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);


	}

/*	public static class DatePickerFragment extends DialogFragment
			implements DatePickerDialog.OnDateSetListener {
        TextView txt_Date;
		public DatePickerFragment(TextView txt) {
		      txt_Date=txt;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog lDialog=new DatePickerDialog(getActivity(), this, year, month, day);
			lDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

			return lDialog;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			String lDay = (day<10)?"0"+day: ""+day;

           txt_Date.setText(""+lDay+"-"+(++month)+"-"+year);
		}
	}

	public  boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}
*/
}
