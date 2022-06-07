package com.gpl.rpg.AndorsTrail.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.view.CustomDialogFactory.CustomDialog;

public class CustomListPreference extends ListPreference {

	//Extensive constructor support
	public CustomListPreference(Context context) {
		super(context);
	}
	public CustomListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//	Min API 21
//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	public CustomListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
//		super(context, attrs, defStyleAttr);
//	}
//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	public CustomListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//	}

	CustomDialog d = null;
	int clickedEntryIndex = 0;

	@Override
	public Dialog getDialog() {
		if (d == null) createDialog();
		return d;
	}

	private void createDialog() {
		final ListView choicesList = new ListView(getContext());
		choicesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		choicesList.setBackgroundResource(android.R.color.transparent);
		ArrayAdapter<CharSequence> choicesAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_checkedlistitem_layout, getEntries());
		choicesList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		choicesList.setAdapter(choicesAdapter);
		choicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (getValue() != getEntryValues()[position]) {
					CustomListPreference.this.notifyChanged();
					clickedEntryIndex = position;
					CustomListPreference.this.onClick(d, DialogInterface.BUTTON_POSITIVE);
				} else {
					CustomListPreference.this.onClick(d, DialogInterface.BUTTON_NEGATIVE);
				}
				d.dismiss();
			}
		});
		choicesList.setItemChecked(getValueIndex(), true);
		
		d = CustomDialogFactory.createDialog(getContext(), getTitle().toString(), null, null, choicesList, false);
	}

	public int getValueIndex() {
		int selectedPosition;
		for (selectedPosition = getEntryValues().length - 1; selectedPosition >= 0; selectedPosition--) {
			if (getValue().equals(getEntryValues()[selectedPosition].toString())) break;
		}
		return selectedPosition;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		CharSequence[] entryValues = getEntryValues();
		if (positiveResult && clickedEntryIndex >= 0 && entryValues != null) {
			String value = entryValues[clickedEntryIndex].toString();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}

	@Override
	protected void showDialog(Bundle state) {
		getDialog().setOnDismissListener(this);
		getDialog().show();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		d = null;
		super.onDismiss(dialog);
	}
}
