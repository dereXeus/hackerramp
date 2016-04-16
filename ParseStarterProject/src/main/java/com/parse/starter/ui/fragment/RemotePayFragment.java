package com.parse.starter.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.parse.Notification;
import com.parse.starter.parse.Transaction;
import com.parse.starter.parse.User;
import com.parse.starter.ui.activity.MenuActivity;
import com.parse.starter.ui.activity.RemotePaymentActivity;
import com.parse.starter.util.CurrentUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemotePayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemotePayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemotePayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AMOUNT = "amount";
    private static final String DESCRIPTION = "description";

    // TODO: Rename and change types of parameters
    private String amount;
    private String description;

    Spinner spinner;

    private OnFragmentInteractionListener mListener;

    public RemotePayFragment() {
        // Required empty public constructor
    }

    public static RemotePayFragment newInstance(String amount, String description) {
        RemotePayFragment fragment = new RemotePayFragment();
        Bundle args = new Bundle();
        args.putString(AMOUNT, amount);
        args.putString(DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("Remote Pay Fragment"," Size of set " + getArguments().keySet().size());
            amount = getArguments().getString(AMOUNT);
            description = getArguments().getString(DESCRIPTION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_remote_pay, container, false);

        spinner = (Spinner)v.findViewById(R.id.users);

        Log.d("Remote Pay Fragment","Size of set " + getArguments().keySet().size());
        Log.d("Remote Pay Fragment","Amount : " + getArguments().getString(AMOUNT));
        Log.d("Remote Pay Fragment","Description : " + getArguments().getString(DESCRIPTION));

        TextView tv_mct_nmm = (TextView)v.findViewById(R.id.tv_mcht_nm);
        tv_mct_nmm.setText(description);
        TextView tv_amt =  (TextView)v.findViewById(R.id.tv_amnt);
        tv_amt.setText(amount+" Rs");

        populateDialog(spinner);

        Button payButton = (Button) v.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayBtnClick(v);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void populateDialog(final Spinner spinner){
        final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this.getActivity(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                // Here we can configure a ParseQuery to our heart's desire.
                ParseQuery query = new ParseQuery("User");
                query.whereNotEqualTo("phone", CurrentUser.getInstance().getUser().getPhone());
                return query;
            }
        }) {
            @Override
            public View getItemView(ParseObject object, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.custom_spinner, null);
                }
                User usr = (User) object;
                TextView nameView = (TextView) v.findViewById(R.id.Name);
                nameView.setText(usr.getName() + "    (" + usr.getPhone() + ")");
                return v;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }
        };

        spinner.setAdapter(adapter);

    }

    public void onPayBtnClick(View view){
        try {
            Notification notification = new Notification();
            notification.setAmount(amount);
            notification.setMerchantName(description);
            notification.setForwardToUser((User) spinner.getSelectedItem());
            notification.setRequestUser(CurrentUser.getInstance().getUser());
            notification.setStatus("U");notification.setPaymentType("R");
            notification.save();
            new RemotePaymentTask(this.getActivity()).execute("");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class RemotePaymentTask extends AsyncTask<String, Void, String> {
        ParseQuery<Notification> notificationParseQuery;
        Activity activity;
        String status;

        RemotePaymentTask(Activity activity){
            this.activity = activity;
            this.notificationParseQuery = ParseQuery.getQuery("Notification");
            this.notificationParseQuery.whereEqualTo("request_user", CurrentUser.getInstance().getUser());
        }

        private String getPaymentStatus(){
            try {
                List<Notification> notifications = notificationParseQuery.find();
                return notifications.get(0).getStatus();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "U";
        }

        @Override
        protected String doInBackground(String... params) {
            int trials = 0;
            while(true){
                try {
                    if(trials>200){
                        break;
                    }
                    String status = getPaymentStatus();
                    if("Paid".equals(status)){
                        return  "Payment Succesfull .. ";
                    }else if("C".equals(status)){
                        return  "Payment Cancelled  by " + ((User)spinner.getSelectedItem()).getName() + "  .. ";
                    }
                    Thread.sleep(1000);
                    trials = trials + 1;
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    break;
                }
            }
            return "Payment Request Timeout .. ";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
            try {
                List<Notification> clearNotifications = notificationParseQuery.find();
                for (Notification clearNotification:clearNotifications){
                    clearNotification.delete();
                    clearNotification.save();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Button payButton = (Button)activity.findViewById(R.id.payButton);
            payButton.setEnabled(true);
            if("Payment Succesfull .. ".equals(result)){

                Transaction transaction = new Transaction();
                transaction.setAmount((int)Double.parseDouble(amount));
                transaction.setDescription(description);
                transaction.setTo((User) spinner.getSelectedItem());
                transaction.setFrom(CurrentUser.getInstance().getUser());
                transaction.add();

            }
        }

        @Override
        protected void onPreExecute() {
            Button payButton = (Button) activity.findViewById(R.id.payButton);
            payButton.setEnabled(false);
        }

    }
}
