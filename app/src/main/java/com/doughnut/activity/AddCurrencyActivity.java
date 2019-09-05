package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jtblk.client.bean.Marker;
import com.doughnut.R;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.doughnut.utils.currency;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;
import java.util.List;


public class AddCurrencyActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener{

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;
    private EditText edit_check;
    private RecyclerView mRecyclerView;
    private AddCurrencyAdapter mAdapter;
    private Marker marker;
    private List<currency> currencys; //替换成币别的实体类，并且里面增加boolen字段，记录是否选中

    private LinearLayout mLayoutEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
        currencys=new ArrayList<>();
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

        mLayoutEmpty = findViewById(R.id.layout_no_transfer);
        edit_check= findViewById(R.id.edit_check);

        edit_check.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 6 || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) && event != null) {
//                    // 先隐藏键盘
//                    ((InputMethodManager) edit_barcode.getContext()
//                            .getSystemService(Context.INPUT_METHOD_SERVICE))
//                            .hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),
//                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if( event.getAction()==KeyEvent.ACTION_DOWN)
                    {
                        String keyword= edit_check.getText().toString().trim();
                        if (keyword.equals(""))
                        {
                            Toast.makeText(AddCurrencyActivity.this,"请输入查询值",Toast.LENGTH_LONG).show();
                            return  false;
                        }
                        getRecord();
                        return  true;
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
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        mSmartRefreshLayout.autoRefresh();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getRecord();
                if (currencys == null || currencys.isEmpty()) {
                    mLayoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    mLayoutEmpty.setVisibility(View.GONE);
                }
                refreshlayout.finishRefresh();
                mAdapter.notifyDataSetChanged();
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (marker == null) {
                    refreshlayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                getRecordMore();
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onClick(View view) {

    }

    /**
     * 获取更多
     */
    private  void getRecordMore() {


    }

    /**
     * 获取记录
     */
    private  void getRecord() {
        currencys=new ArrayList<>();
        String keyword= edit_check.getText().toString().trim();
        WalletManager.getInstance(this).getAllTokens();
        String fileName = this.getPackageName() + "_tokens";
        List<currency> bbs=new ArrayList<>();
        currency bb=   new currency();
        bb.image=R.mipmap.ic_launcher;
        bb.name="SWTC";
        currency bb1=   new currency();
        bb1.image=R.mipmap.ic_launcher;
        bb1.name="SWTC1";
        currency bb2=   new currency();
        bb2.image=R.mipmap.ic_launcher;
        bb2.name="SWTC2";
        currency bb3=   new currency();
        bb3.image=R.mipmap.ic_launcher;
        bb3.name="SWTC3";
        currency bb4=   new currency();
        bb4.image=R.mipmap.ic_launcher;
        bb4.name="SWTC4";
        currency bb5=   new currency();
        bb5.image=R.mipmap.ic_launcher;
        bb5.name="SWTC5";
        currency bb6=   new currency();
        bb6.image=R.mipmap.ic_launcher;
        bb6.name="SWTC6";
        currency bb7=   new currency();
        bb7.image=R.mipmap.ic_launcher;
        bb7.name="SWTC7";
        currency bb8=   new currency();
        bb8.image=R.mipmap.ic_launcher;
        bb8.name="SWTC8";
        currency bb9=   new currency();
        bb9.image=R.mipmap.ic_launcher;
        bb9.name="SWTC9";

        currency bb10=   new currency();
        bb10.image=R.mipmap.ic_launcher;
        bb10.name="SWTC10";
        currency bb11=   new currency();
        bb11.image=R.mipmap.ic_launcher;
        bb11.name="SWTC11";
        currency bb12=   new currency();
        bb12.image=R.mipmap.ic_launcher;
        bb12.name="SWTC12";
        currency bb13=   new currency();
        bb13.image=R.mipmap.ic_launcher;
        bb13.name="SWTC13";

        currency bb14=   new currency();
        bb14.image=R.mipmap.ic_launcher;
        bb14.name="SWTC14";
        currency bb15=   new currency();
        bb15.image=R.mipmap.ic_launcher;
        bb15.name="SWTC15";
        currency bb16=   new currency();
        bb16.image=R.mipmap.ic_launcher;
        bb16.name="SWTC16";
        currency bb17=   new currency();
        bb17.image=R.mipmap.ic_launcher;
        bb17.name="SWTC17";
        bbs.add(bb);
        bbs.add(bb1);
        bbs.add(bb2);
        bbs.add(bb3);
        bbs.add(bb4);
        bbs.add(bb5);
        bbs.add(bb6);
        bbs.add(bb7);
        bbs.add(bb8);
        bbs.add(bb9);
        bbs.add(bb10);
        bbs.add(bb11);
        bbs.add(bb12);
        bbs.add(bb13);
        bbs.add(bb14);
        bbs.add(bb15);
        bbs.add(bb16);
        bbs.add(bb17);
        currencys.addAll(bbs);

        //使用Java8中的lambda表达式过滤
        //currencys = bbs.stream().filter((currency s)->s.name.contains(keyword)).collect(Collectors.toList());



    }
    @Override
    public void onLeftClick(View v) {
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
        String currentWallet = WalletSp.getInstance(this, "").getCurrentWallet();
//        mTitleBar.setTitle(WalletSp.getInstance(this, currentWallet).getName());
        if (mAdapter != null) {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    public static void startActivity(Context from) {
        Intent intent = new Intent(from, AddCurrencyActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerView.getChildAdapterPosition(
                    mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerView.getAdapter().getItemCount() - 1) {
                return mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1)
                        .getBottom() <= mRecyclerView.getBottom();
            }
        } catch (Throwable e) {
        }
        return false;
    }
    class AddCurrencyAdapter extends RecyclerView.Adapter<AddCurrencyAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            LinearLayout mLayoutItem;
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            CheckBox tv_check;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
//                    mLayoutItem.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            currency tr = currencys.get(getAdapterPosition());
//                            TransactionDetailsActivity.startTransactionDetailActivity(AddCurrencyActivity.this, tr);
//                        }
//                    });
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                tv_check = itemView.findViewById(R.id.tv_check);
                /**
                 * checkbox 选择事件
                 */
                tv_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked)
                        {
                            currencys.get(getAdapterPosition()).ischeck=true;

                        }
                        else
                        {
                            currencys.get(getAdapterPosition()).ischeck=false;

                        }
                    }
                });

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
            currency tr = currencys.get(position);
            //赋值
            holder.mImgIcon.setImageResource(R.drawable.ic_transfer_send);
            holder.mTvTransactionAddress.setText(tr.name);
            holder.tv_check.setChecked(tr.ischeck);

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


}