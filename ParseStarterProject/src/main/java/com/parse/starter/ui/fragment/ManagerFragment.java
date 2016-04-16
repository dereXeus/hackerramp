package com.parse.starter.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.ui.activity.SplitPaymentActivity;
import com.parse.starter.ui.activity.ListTransactionActivity;
import com.parse.starter.ui.activity.RemotePaymentActivity;
import com.parse.starter.parse.Account;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;
import com.parse.starter.util.functions;

import java.util.ArrayList;
import java.util.List;

public  class ManagerFragment extends Fragment {
	private User curr = null;
	private String phone;
	private ParseQueryAdapter<ParseObject> adapter;
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		curr = CurrentUser.getInstance().getUser();

		 adapter = new ParseQueryAdapter<ParseObject>(getActivity(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
			public ParseQuery<ParseObject> create() {
				ParseQuery<ParseObject> givenTo = ParseQuery.getQuery("Account");
				givenTo.whereEqualTo("From", curr);

				ParseQuery<ParseObject> takenFrom = ParseQuery.getQuery("Account");
				takenFrom.whereEqualTo("To",curr);

				List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
				queries.add(givenTo);
				queries.add(takenFrom);

				ParseQuery mainQuery = ParseQuery.or(queries);

				return mainQuery;

			}
		})
		{
			@Override
			public View getItemView(ParseObject object, View v, ViewGroup parent) {
				if (v == null) {
					v = View.inflate(getContext(), R.layout.list_row, null);
				}
				Account acc = (Account) object;
				TextView descriptionView = (TextView) v.findViewById(R.id.description);
				TextView amountView = (TextView) v.findViewById(R.id.amount);

				amountView.setText("Rs. " + acc.getAmount());

				User giveTo = acc.getFrom();

				if(acc.getFrom().equals(curr)) {
					giveTo = acc.getTo();
				}

				descriptionView.setText(giveTo.getName());
				Bitmap photo = functions.openPhoto("+919739015858",getActivity());
				//Bitmap scaled = Bitmap.createScaledBitmap(photo,60,60,true);
				//imgView.setImageBitmap(scaled);

				return v;
			}

		};


	}

	public void payForward(View view) {
		Intent intent = new Intent(getActivity(),RemotePaymentActivity.class);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.main, container, false);
		ListView listView = (ListView) v.findViewById(R.id.list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Account account = (Account) adapter.getItem(position);
				Intent intent = new Intent(getActivity(), ListTransactionActivity.class);
				if (account.getFrom().equals(curr))
					intent.putExtra("ToPhone", account.getTo().getPhone());
				else intent.putExtra("ToPhone", account.getFrom().getPhone());
				startActivity(intent);
			}
		});

		return v;
	}

	public static ManagerFragment newInstance(){
		ManagerFragment fragment = new ManagerFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public ManagerFragment(){

	}
}
