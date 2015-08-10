package com.stockholmapplab.recipes;

import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddStep4Fragment extends DialogFragment {

	private View mRoot;
	public static EditText guideEdittxt;

	public static AddStep4Fragment newInstance() {
		AddStep4Fragment f = new AddStep4Fragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fr_add_recipe_4, container, false);
		guideEdittxt = (EditText) mRoot.findViewById(R.id.addGuideEdittxt);
		guideEdittxt.setText(PrefHelper.getString("AddGuide", ""));
		guideEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				PrefHelper.setString("AddGuide", guideEdittxt.getText()
						.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		// Set selection of EditText to the end
		int textLength = guideEdittxt.getText().length();
		guideEdittxt.setSelection(textLength, textLength);
		return mRoot;
	}

}