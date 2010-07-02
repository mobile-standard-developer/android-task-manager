/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.jpsan.es;

import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class TaskEdit extends Activity {

	private TextView mDateDisplay;

	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private int mDate;

	Date d = new Date();

	private int mYear = d.getYear();
	private int mMonth = d.getMonth();
	private int mDay = d.getDay();
	
	

	private TasksDbAdapter mDbHelper;

	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new TasksDbAdapter(this);
		
		mDbHelper.open();
		setContentView(R.layout.note_edit);

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mDateDisplay = (TextView) findViewById(R.id.selectedDate);

		Button confirmButton = (Button) findViewById(R.id.confirm);
		Button cancelButton = (Button) findViewById(R.id.cancel);
		Button pickUpDate = (Button) findViewById(R.id.pickADate);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(TasksDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(TasksDbAdapter.KEY_ROWID)
					: null;
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}

		});

		// add a click listener to the button
		pickUpDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
				// finish();
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:

			DatePickerDialog dp = new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
			dp.updateDate(2010, d.getMonth(), d.getDay());
			
			return dp;
		}
		return null;
	}

	private void populateFields() {
		if (mRowId != null) {
			Cursor note = mDbHelper.fetchNote(mRowId);
			startManagingCursor(note);
			mTitleText.setText(note.getString(note
					.getColumnIndexOrThrow(TasksDbAdapter.KEY_TITLE)));
			mBodyText.setText(note.getString(note
					.getColumnIndexOrThrow(TasksDbAdapter.KEY_BODY)));
			mDate = note.getInt(note
					.getColumnIndexOrThrow(TasksDbAdapter.KEY_DATE_INTEGER));
			mDateDisplay.setText(note.getString(note
					.getColumnIndexOrThrow(TasksDbAdapter.KEY_DATE)));
		}
	}

	// updates the date we display in the TextView
	private void updateDisplay() {
		mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("/").append(mDay).append("/")
				.append(mYear).append(" "));

		String formDate = mYear + "";
		String temp = "";
		if (mMonth + 1 <= 9) {
			formDate = formDate + "0";
			
		}
		formDate = formDate + (mMonth + 1);
		
		if (mDay <= 9) {
			formDate = formDate + "0";
			
		}
		
		formDate = formDate + mDay;
		
		temp = formDate.substring(6)+"/"+formDate.substring(4,6)+"/"+formDate.substring(0,4);
		
		mDateDisplay.setText(temp);
		mDate = Integer.parseInt(formDate);
		
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(TasksDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		int date_i = mDate;
		String date = mDateDisplay.getText().toString();

		if (notInvalidDataParsed(title, body, date)) {
			if (mRowId == null) {
				long id = mDbHelper.createNote(title, body, date_i, date);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				mDbHelper.updateNote(mRowId, title, body, date_i, date);
			}
		}

	}


	private boolean notInvalidDataParsed(String title, String body, String date) {

		if (title == null || title.equals("") || title.startsWith(" ")) {
			return false;
		}
		
		if (date == null || date.equals("") || date.startsWith(" ")) {
			return false;
		}
		return true;

	}

}
