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
import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.Currency;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.RecyclerViewSpacesItemDecoration;
import com.doughnut.view.TitleBar;
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
    private LinkedList<String> selectTokens; //替换成币别的实体类，并且里面增加boolen字段，记录是否选中
    private String mCurrentWallet;
    private boolean mIsSingle = false;
    private int mSelectedItem = -1;

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
            if (mIsSingle) {
                if (mSelectedItem != -1) {
                    String token = currencys.get(mSelectedItem).getName();
                    TokenReceiveActivity.startTokenReceiveActivity(this, token);
                }
            } else {
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
        if (mIsSingle) {
            if (mSelectedItem != -1) {
                String token = currencys.get(mSelectedItem).getName();
                TokenReceiveActivity.startTokenReceiveActivity(this, token);
            }
        } else {
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
                    Matcher matcher = p.matcher(currency.getName().toUpperCase());
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
        Map<String, String> tokenMap = JSON.parseObject(tokens, Map.class);
        for (Map.Entry<String, String> entry : tokenMap.entrySet()) {
            Currency currency = new Currency();
            currency.setName(entry.getKey());
            currency.setImage(Util.getTokenIcon(entry.getKey()));
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
            CheckBox chk_select;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //单选
                        if (mIsSingle) {
                            VH vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                            int position = getAdapterPosition();
                            if (position == mSelectedItem) {
                                return;
                            } else if (position != mSelectedItem && vh != null) {
                                vh.chk_select.setChecked(false);
                                vh.mLayoutItem.setActivated(false);
                                mSelectedItem = position;
                                vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                                vh.chk_select.setChecked(true);
                                vh.mLayoutItem.setActivated(true);
                            } else {
                                if (mSelectedItem != -1) {
                                    notifyItemChanged(mSelectedItem);
                                }
                                mSelectedItem = position;
                                vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(position);
                                vh.chk_select.setChecked(true);
                                vh.mLayoutItem.setActivated(true);
                            }
                        } else {
                            chk_select.setChecked(!chk_select.isChecked());
                            Currency currency = currencys.get(getAdapterPosition());
                            if (chk_select.isChecked()) {
                                selectTokens.add(currency.getName());
                                currency.setSelect(true);
                            } else {
                                selectTokens.remove(currency.getName());
                                currency.setSelect(false);
                            }
                        }

                    }
                });
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTokenName = itemView.findViewById(R.id.tv_token_name);
                chk_select = itemView.findViewById(R.id.tv_check);
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
            holder.mTvTokenName.setText(tr.getName());
            ViewUtil.EllipsisTextView(holder.mTvTokenName);
            holder.chk_select.setChecked(tr.getIsSelect());
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