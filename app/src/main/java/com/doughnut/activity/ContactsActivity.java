package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doughnut.R;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.nostra13.universalimageloader.utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class ContactsActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private LinearLayout mLayoutNoWallet;
    private RecyclerView mLsWallet;
    private ContactsAdapter mAdapter;

    private LinkedList<GsonUtil> contactList = new LinkedList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onLeftClick(View view) {
        this.finish();
    }

    @Override
    public void onRightClick(View view) {
    }

    @Override
    public void onMiddleClick(View view) {

    }

    public static void startContactsActivity(Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.title_contact));

        mTitleBar.setTitleTextColor(R.color.black);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.setTitleBarClickListener(this);

        mLayoutNoWallet = findViewById(R.id.layout_no_wallet);
        mLsWallet = (RecyclerView) findViewById(R.id.ls_manager_wallet);
        mLsWallet.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactsAdapter();
        mLsWallet.setAdapter(mAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mLsWallet);
    }

    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            LinearLayout mLayoutItem;
            TextView mTvAmount;
            TextView mTvAddress;
            TextView mTvTime;
            TextView mTvToken;
            String address;

            public VH(View v) {
                super(v);
                mTvAmount = itemView.findViewById(R.id.tv_amount);
                mTvAddress = itemView.findViewById(R.id.tv_address);

                mTvTime = itemView.findViewById(R.id.tv_time);
                mTvToken = itemView.findViewById(R.id.tv_token);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TokenTransferActivity.startTokenTransferActivity(ContactsActivity.this, address, "");
                        finish();
                    }
                });
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_contact, false);
            return new VH(v);

        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (contactList == null) {
                return;
            }
            GsonUtil item = contactList.get(position);
            holder.address = item.getString("address", "");
            holder.mTvAddress.setText(holder.address);
            ViewUtil.EllipsisTextView(holder.mTvAddress);
            holder.mTvTime.setText(item.getString("time", ""));
            holder.mTvToken.setText(item.getString("token", ""));
            holder.mTvAmount.setText(item.getString("amount", ""));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }
    }

    private void getContacts() {
        // 本地保存tokens
        String fileName = getPackageName() + "_contacts";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String contacts = sharedPreferences.getString("contacts", "");
        Map<String, String> contactMap;
        if (!TextUtils.isEmpty(contacts)) {
            contactMap = JSON.parseObject(contacts, Map.class);
        } else {
            contactMap = new HashMap<>();
            mLayoutNoWallet.setVisibility(View.VISIBLE);
        }
        for (String value : contactMap.values()) {
            contactList.add(new GsonUtil(value));
        }
        Collections.sort(contactList, new Comparator<GsonUtil>() {
            @Override
            public int compare(GsonUtil o1, GsonUtil o2) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
                try {
                    Date d1 = formatter.parse(o1.getString("time", ""));
                    Date d2 = formatter.parse(o2.getString("time", ""));
                    return d2.compareTo(d1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        mAdapter.notifyDataSetChanged();
    }
}
