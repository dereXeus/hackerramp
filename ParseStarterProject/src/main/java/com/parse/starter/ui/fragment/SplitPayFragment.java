package com.parse.starter.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.parse.Notification;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SplitPayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SplitPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplitPayFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AMOUNT = "100";
    private static final String DESCRIPTION = "Nike Shoes";

    private String amount;
    private String description;

    ArrayList<User> splitUsers = new ArrayList<User>();
    ArrayAdapter<User> splitAdapter;
    ListView splitListView;
    Spinner spinner;
    Button payButton;

    private OnFragmentInteractionListener mListener;

    public SplitPayFragment() {
        // Required empty public constructor
    }

    public static SplitPayFragment newInstance(String amount, String description) {
        SplitPayFragment fragment = new SplitPayFragment();
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
            amount = getArguments().getString(AMOUNT);
            description = getArguments().getString(DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_split_pay, container, false);

        ArrayList<User> contactList = getContactList();

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

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        splitAdapter = new ArrayAdapter<User>(this.getActivity(), 0, splitUsers) {
            @Override
            public View getView(int position, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.custom_spinner, null);
                }
                User usr = getItem(position);
                TextView nameView = (TextView) v.findViewById(R.id.Name);
                nameView.setText(usr.getName() + "    (" + usr.getPhone() + ")");
                return v;
            }

        };

        ListView splitListView = (ListView) v.findViewById(R.id.split_list);
        splitListView.setAdapter(splitAdapter);

        splitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                User tmp = splitUsers.get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(SplitPayFragment.this.getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to remove " + tmp.getName() + " ?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        splitUsers.remove(positionToRemove);
                        splitAdapter.notifyDataSetChanged();
                    }
                });
                adb.show();
            }
        });

        this.spinner  = (Spinner) v.findViewById(R.id.spinner);
        payButton = (Button) v.findViewById(R.id.split);

        Button addButton = (Button) v.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSplitUser(v);
            }
        });

        payButton = (Button) v.findViewById(R.id.split);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitAmount(v);
            }
        });

        TextView tv_mct_nmm = (TextView)v.findViewById(R.id.tv_mcht_nm);
        tv_mct_nmm.setText(this.description);
        TextView tv_amt =  (TextView)v.findViewById(R.id.tv_amnt);
        tv_amt.setText(this.amount + " Rs");

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

    public ArrayList<User> getContactList() {
        ArrayList<User> list = new ArrayList<User>();

        ContentResolver cr = this.getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                .replaceAll("-", "").replaceAll(" ", "");

                        User tmp = new User();
                        tmp.setName(name);
                        tmp.setPhone(phoneNo);
                        list.add(tmp);
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }

        }

        return list;
    }

    public void addSplitUser(View view) {
        User curr = (User)spinner.getSelectedItem();

        if (!splitUsers.contains(curr)) {
            splitUsers.add(curr);
            splitAdapter.notifyDataSetChanged();
            spinner.setSelection(0);
        }
    }

    public void splitAmount(View view) {
        if (splitUsers.size() == 0) {
            Toast.makeText(this.getActivity(), "Please add people to split", Toast.LENGTH_LONG);
            return;
        }
        double totalAmount = Double.parseDouble(amount);
        double indAmount = totalAmount/splitUsers.size();
        String mcht_nm = description;

        for(User a:splitUsers){
            Notification tmp = new Notification();
            tmp.setForwardToUser(a);
            tmp.setAmount(indAmount + "");
            tmp.setMerchantName(mcht_nm);
            tmp.setRequestUser(CurrentUser.getInstance().getUser());
            tmp.setStatus("U"); tmp.setPaymentType("S");
            try {
                tmp.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        (new SplitPaymentTask()).execute();

    }

    private class SplitPaymentTask extends AsyncTask<String, Void, Integer> {
        ParseQuery<Notification> notificationParseQuery;
        String status;
        String merchant_card;
        int amount_share;


        SplitPaymentTask(){
            notificationParseQuery = ParseQuery.getQuery("Notification");
            notificationParseQuery.whereEqualTo("request_user", CurrentUser.getInstance().getUser());
            this.notificationParseQuery.whereEqualTo("pay_type","S");
        }

        private List<Notification> getPaymentStatus(){
            try {
                List<Notification> notifications = notificationParseQuery.find();
                return notifications;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* write fund transfer code here for the */
        public void makePayment(){
            /*double totalAmount = Double.parseDouble(amount);
            double indAmount = totalAmount/splitUsers.size();
            String mcht_nm = merchantName;

            for(User user: splitUsers){
                Transaction transaction = new Transaction();
                transaction.setFrom(CurrentUser.getInstance().getUser());
                transaction.setTo(user);
                transaction.setAmount((int)indAmount);
                transaction.setDescription(merchantName);
                transaction.add();
            }*/
        }

        @Override
        protected Integer doInBackground(String... params) {
            int trials = 1;
            while(true){
                try {
                    Log.d("SplitPay Fragment","Trial No. " + trials);
                    if(trials >= 200){
                        break;
                    }
                    List<Notification> status = getPaymentStatus();
                    if(checkPaymentApproved(status)){
                        makePayment();
                        return  0;
                    }
                    Thread.sleep(100);
                    trials = trials + 1;
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    break;
                }
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            String toastString ;
            if(result==0){
                toastString = "Payment Successfull.. ";
            }else{
                toastString = "Payment Request Cancelled.. ";
            }
            Toast.makeText(SplitPayFragment.this.getActivity(), toastString, Toast.LENGTH_LONG).show();

            try {
                List<Notification> clearNotifications = notificationParseQuery.find();
                for (Notification clearNotification:clearNotifications){
                    clearNotification.delete();
                    clearNotification.saveEventually();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            payButton.setEnabled(true);
        }

        @Override
        protected void onPreExecute() {
            payButton.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private boolean checkPaymentApproved(List<Notification> all){
        boolean approved=true;
        for(Notification  a:all){
            Log.d("SplitPay Fragment","Notification : " + a.getRequestUser().getName()
                    + " " + a.getForwardToUser().getName() + " " + a.getAmount() + " " + a.getStatus());
            if (a.getStatus().compareTo("P") != 0) {
                approved = false;
            }
        }
        return approved;
    }

    private int changeState(List<Notification> all){
        try {
            for(Notification  a:all) {
                a.setStatus("A");
                a.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 1;
    }
}
