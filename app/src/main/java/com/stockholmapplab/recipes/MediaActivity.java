package com.stockholmapplab.recipes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.R;

public class MediaActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_media);
		initActionBar();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_media));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    default:
	    	finish();
	        return super.onOptionsItemSelected(item);
	    }
	}

	public void twitter(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://twitter.com/susanneshealth")));
	}

	public void facebook(View v) {
		startActivity(new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://www.facebook.com/pages/Susannes-Health/462291797211667?ref=hl")));
	}

	public void web(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://susanneshealth.com")));
	}

	public void google(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://plus.google.com/104370522982374875132")));
	}

	public void webSal(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://www.stockholmapplab.com")));
	}

	public void twitterSal(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://twitter.com/StockholmAppLab")));
	}

	public void facebookSal(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://www.facebook.com/52health?ref=hl")));
	}

	public void more(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
	}

	public void contact(View v) {
		startActivity(new Intent(MediaActivity.this, ContactUsActivity.class));
	}

	public void about(View v) {
		startActivity(new Intent(MediaActivity.this, AboutActivity.class));
	}

	public void back(View v) {
		finish();
	}
}
