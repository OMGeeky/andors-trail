package com.gpl.rpg.AndorsTrail.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Simply instantiate this class, implement abstract methods in an anonymous type, and tada, your Button is a Spinner! 
 */
public abstract class SpinnerEmulator {
	
	private Button spinnerButton;
	private Dialog spinnerDialog = null;
	private ListView choicesList;
	private Context context;
	
	public SpinnerEmulator(Button b, int arrayResId, int promptResId) {
		spinnerButton = b;
		context=b.getContext();
		initializeSpinnerEmulation(arrayResId, promptResId);
	}
	

	public SpinnerEmulator(View v, int buttonId, int arrayResId, int promptResId) {
		spinnerButton = (Button) v.findViewById(buttonId);
		context=v.getContext();
		initializeSpinnerEmulation(arrayResId, promptResId);
	}
	
	public void initializeSpinnerEmulation(final int arrayResId, final int promptResId) {
		choicesList = new ListView(context);//(Spinner) v.findViewById(R.id.inventorylist_category_filters);
		choicesList.setBackgroundResource(android.R.color.transparent);
		ArrayAdapter<CharSequence> skillCategoryFilterAdapter = ArrayAdapter.createFromResource(context, arrayResId, android.R.layout.simple_list_item_1);
		choicesList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		choicesList.setAdapter(skillCategoryFilterAdapter);
		choicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (getValue() == position) {
					spinnerDialog.dismiss();
					return;
				}
				setValue(position);
				spinnerButton.setText(context.getResources().getStringArray(arrayResId)[position]);
				spinnerDialog.dismiss();
				selectionChanged(position);
			}
		});
		choicesList.setSelection(getValue());
		
		spinnerButton.setText(context.getResources().getStringArray(arrayResId)[getValue()]);
		spinnerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spinnerDialog == null) {
					spinnerDialog = CustomDialogFactory.createDialog(context, context.getString(promptResId), null, null, choicesList, false);
				}
				CustomDialogFactory.show(spinnerDialog);
			}
		});
		
		
	}
	
	public abstract int getValue();
	public abstract void setValue(int value);
	public abstract void selectionChanged(int value);

}
