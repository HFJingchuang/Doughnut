package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.Currency;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.RecyclerViewSpacesItemDecoration;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletSp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddCurrencyActivity extends BaseActivity implements TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;
    private EditText mEdtSearch;
    private RecyclerView mRecyclerView;
    private AddCurrencyAdapter mAdapter;
    private LinkedList<Currency> currencys;
    private LinkedList<Currency> currencysCopy;
    private LinkedList<String> selectTokens;
    private String mCurrentWallet;
    private boolean mIsSingle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
        currencys = new LinkedList<>();
        currencysCopy = new LinkedList<>();
        selectTokens = new LinkedList<>();
        mCurrentWallet = WalletSp.getInstance(this, "").getCurrentWallet();
        if (getIntent() != null) {
            mIsSingle = getIntent().getBooleanExtra(Constant.IS_SINGLE, false);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mIsSingle) {
                saveSelectToken();
            }
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onLeftClick(View v) {
        if (!mIsSingle) {
            saveSelectToken();
        }
        finish();
    }


    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onMiddleClick(View v) {

    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitle(getResources().getString(R.string.title_add_currency));
        mTitleBar.setTitleBarClickListener(this);

        mEdtSearch = findViewById(R.id.edit_search);

        final SpannableString ss = new SpannableString(getString(R.string.tv_token_search));
        Drawable d = getResources().getDrawable(R.drawable.ic_search);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mEdtSearch.setHint(ss);
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currencys.clear();
                Pattern p = Pattern.compile(s.toString().toUpperCase());
                for (int i = 0; i < currencysCopy.size(); i++) {
                    Currency currency = currencysCopy.get(i);
                    String name = currency.getName().toUpperCase();
                    if (TextUtils.equals(WConstant.CURRENCY_CNY, name)) {
                        String issue = currency.getIssue();
                        if (TextUtils.equals(WConstant.CURRENCY_ISSUE, issue)) {
                            name = WConstant.CURRENCY_CNT;
                        }
                    }
                    Matcher matcher = p.matcher(name);
                    if (matcher.find()) {
                        currencys.add(currency);
                    }
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getRecord();
        mAdapter = new AddCurrencyAdapter();
        mRecyclerView = findViewById(R.id.view_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(this, 10));
    }

    /**
     * 获取记录
     */
    private void getRecord() {
        currencys = new LinkedList<>();
        // 本地保存tokens
        String fileName = getPackageName() + "_tokens";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString("tokens", "");
        if (TextUtils.isEmpty(tokens)) {
            return;
        }
        Map<String, Currency> tokenMap = JSON.parseObject(tokens, new TypeReference<Map<String, Currency>>() {
        });
        for (Map.Entry<String, Currency> entry : tokenMap.entrySet()) {
            Currency currency = entry.getValue();
            currency.setImage(Util.getTokenIcon(currency.getName()));
            currencys.add(currency);
        }
        Collections.sort(currencys);
        currencysCopy = (LinkedList<Currency>) currencys.clone();
    }

    public static void startActivity(Context context, boolean isSingle) {
        Intent intent = new Intent(context, AddCurrencyActivity.class);
        intent.putExtra(Constant.IS_SINGLE, isSingle);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    class AddCurrencyAdapter extends RecyclerView.Adapter<AddCurrencyAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            RelativeLayout mLayoutItem;
            ImageView mImgIcon;
            TextView mTvTokenName;
            TextView mTvTokenIssue;
            CheckBox chk_select;
            String key;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //单选
                        if (mIsSingle) {
                            TokenReceiveActivity.startTokenReceiveActivity(AddCurrencyActivity.this, key);
                        } else {
                            chk_select.setChecked(!chk_select.isChecked());
                            Currency currency = currencys.get(getAdapterPosition());
                            if (chk_select.isChecked()) {
                                selectTokens.add(key);
                                currency.setSelect(true);
                            } else {
                                selectTokens.remove(key);
                                currency.setSelect(false);
                            }
                        }

                    }
                });
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTokenName = itemView.findViewById(R.id.tv_token_name);
                mTvTokenIssue = itemView.findViewById(R.id.tv_token_issue);
                chk_select = itemView.findViewById(R.id.tv_check);
                if (mIsSingle) {
                    chk_select.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_currency, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (currencys == null) {
                return;
            }
            Currency tr = currencys.get(position);
            //赋值
            holder.mImgIcon.setImageResource(tr.getImage());
            String tokenName = tr.getName();
            String tokenIssue = tr.getIssue();
            holder.key = tokenName + "_" + tokenIssue;
            if (TextUtils.equals(WConstant.CURRENCY_CNY, tokenName) && TextUtils.equals(WConstant.CURRENCY_ISSUE, tokenIssue)) {
                tokenName = WConstant.CURRENCY_CNT;
            }
            holder.mTvTokenName.setText(tokenName);
            if (TextUtils.isEmpty(tokenIssue)) {
                holder.mTvTokenIssue.setVisibility(View.GONE);
            } else {
                holder.mTvTokenIssue.setText(tokenIssue);
                holder.mTvTokenIssue.setVisibility(View.VISIBLE);
            }
            ViewUtil.EllipsisTextView(holder.mTvTokenName);
            ViewUtil.EllipsisTextView(holder.mTvTokenIssue);
            if (!mIsSingle) {
                holder.chk_select.setChecked(tr.getIsSelect());
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (currencys != null) {
                return currencys.size();
            }
            return 0;
        }
    }

    /**
     * 保存选中的币种
     */
    private void saveSelectToken() {
        if (selectTokens.size() > 0) {
            String fileName = getPackageName() + Constant.SELECT_TOKEN + mCurrentWallet;
            SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String select = sharedPreferences.getString("select", "");
            String hide = sharedPreferences.getString("hide", "");
            List<String> selectList;
            List<String> hideList;
            if (TextUtils.isEmpty(select)) {
                selectList = new ArrayList();
            } else {
                if (select.contains(",")) {
                    List<String> arrList = Arrays.asList(select.split(","));
                    selectList = new ArrayList(arrList);
                } else {
                    selectList = new ArrayList();
                    selectList.add(select);
                }
            }

            if (TextUtils.isEmpty(hide)) {
                hideList = new ArrayList();
            } else {
                if (select.contains(",")) {
                    List<String> arrList = Arrays.asList(hide.split(","));
                    hideList = new ArrayList(arrList);
                } else {
                    hideList = new ArrayList();
                    hideList.add(hide);
                }
            }

            // 去掉先前隐藏的币种及重复币种不再添加显示
            for (int i = selectTokens.size() - 1; i >= 0; i--) {
                String token = selectTokens.get(i);
                if (hideList.contains(token)) {
                    hideList.remove(token);
                    selectTokens.remove(token);
                } else if (selectList.contains(token)) {
                    selectTokens.remove(token);
                }
            }
            selectList.addAll(selectTokens);
            editor.putString("select", selectList.toString().replace("[", "").replace("]", "").replace(" ", ""));
            editor.putString("hide", hideList.toString().replace("[", "").replace("]", "").replace(" ", ""));
            editor.apply();
        }
    }
}