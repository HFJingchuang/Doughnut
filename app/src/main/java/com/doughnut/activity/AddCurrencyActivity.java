package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;


public class AddCurrencyActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;
    private EditText mEdtSearch;
    private RecyclerView mRecyclerView;
    private AddCurrencyAdapter mAdapter;
    private List<Currency> currencys;
    private List<String> selectTokens; //替换成币别的实体类，并且里面增加boolen字段，记录是否选中
    private String mCurrentWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
        currencys = new ArrayList<>();
        selectTokens = new ArrayList<>();
        mCurrentWallet = WalletSp.getInstance(this, "").getCurrentWallet();
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitle("添加币种");
        mTitleBar.setTitleBarClickListener(this);

        mEdtSearch = findViewById(R.id.edit_search);

        final SpannableString ss = new SpannableString(getString(R.string.tv_token_search));
        Drawable d = getResources().getDrawable(R.drawable.ic_search);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mEdtSearch.setHint(ss);

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 6 || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) && event != null) {
//                    // 先隐藏键盘
//                    ((InputMethodManager) edit_barcode.getContext()
//                            .getSystemService(Context.INPUT_METHOD_SERVICE))
//                            .hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),
//                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        String keyword = mEdtSearch.getText().toString().trim();
                        if (keyword.equals("")) {
                            Toast.makeText(AddCurrencyActivity.this, "请输入查询值", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        getRecord();
                        return true;
                    }
                }
                return false;
            }
        });
        getRecord();
        mAdapter = new AddCurrencyAdapter();
        mRecyclerView = findViewById(R.id.view_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(this, 10));
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 获取记录
     */
    private void getRecord() {
        currencys = new ArrayList<>();
        // 本地保存tokens
        String fileName = getPackageName() + "_tokens";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString("tokens", "");
        Map<String, String> tokenMap = JSON.parseObject(tokens, Map.class);
        for (Map.Entry<String, String> entry : tokenMap.entrySet()) {
            Currency currency = new Currency();
            currency.setName(entry.getKey());
            currency.setImage(Util.getTokenIcon(entry.getKey()));
            currencys.add(currency);
        }
        Collections.sort(currencys);
    }

    @Override
    public void onLeftClick(View v) {
        saveSelectToken();
        finish();
    }


    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onMiddleClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startActivity(Context from) {
        Intent intent = new Intent(from, AddCurrencyActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
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
        selectList.addAll(selectTokens);

        // 去掉先前隐藏的币种
        if (hideList.size() > 0 && selectList.size() > 0) {
            for (int i = selectList.size() - 1; i >= 0; i--) {
                String token = selectList.get(i);
                if (hideList.contains(token)) {
                    hideList.remove(token);
                    selectList.remove(token);
                }
            }
        }
        editor.putString("select", selectList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        editor.putString("hide", hideList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        editor.apply();
    }
}