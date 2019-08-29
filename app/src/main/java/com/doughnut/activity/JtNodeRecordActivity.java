package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.dialog.NodeCubtomDialog;
import com.doughnut.utils.FileUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WConstant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JtNodeRecordActivity extends BaseActivity implements
        TitleBar.TitleBarClickListener {

    final private BigDecimal PING_QUICK = new BigDecimal("60");
    final private BigDecimal PING_LOW = new BigDecimal("100");

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewCustom;
    private Button mBtnCustom;
    private NodeRecordAdapter mAdapter;
    private NodeRecordCustomAdapter mAdapterCustom;
    private GsonUtil publicNodes = new GsonUtil("{}");
    private List<String> publicNodesCustom = new ArrayList<>();
    private int mSelectedItem = -1;
    private int mSelectedCustomItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jtnode_record);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void onLeftClick(View view) {
        String url;
        String ping;
        if (mSelectedItem != -1) {
            NodeRecordAdapter.VH vh = (NodeRecordAdapter.VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
            url = vh.mTvNodeUrl.getText().toString();
            ping = vh.mTvNodePing.getText().toString();
        } else {
            NodeRecordCustomAdapter.VH vh = (NodeRecordCustomAdapter.VH) mRecyclerViewCustom.findViewHolderForLayoutPosition(mSelectedCustomItem);
            url = vh.mTvNodeUrl.getText().toString();
            ping = vh.mTvNodePing.getText().toString();
        }
        // ping值没有时，忽略
        ping = ping.replace("ms", "");
        if (!TextUtils.isEmpty(ping) && !TextUtils.equals(ping, "---")) {
            JtServer.getInstance(this).changeServer(url);
            String fileName = getPackageName() + WConstant.SP_SERVER;
            SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("nodeUrl", url);
            editor.apply();
        }
        finish();
    }

    @Override
    public void onRightClick(View view) {
    }

    @Override
    public void onMiddleClick(View view) {
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitle("节点设置");
        mTitleBar.setTitleBarClickListener(this);

        mBtnCustom = findViewById(R.id.btn_custom);
        mBtnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NodeCubtomDialog(JtNodeRecordActivity.this, new NodeCubtomDialog.onConfirmOrderListener() {
                    @Override
                    public void onConfirmOrder() {
                        cancelDefaultNode();
                        mSelectedCustomItem = 0;
                        getCustomNode();
                    }
                }).show();
            }
        });
        mAdapter = new NodeRecordAdapter();
        mRecyclerView = findViewById(R.id.view_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        mSmartRefreshLayout.autoRefresh();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mSelectedItem = -1;
                refreshlayout.finishRefresh();
                mAdapter.notifyDataSetChanged();
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                mSelectedItem = -1;
                refreshlayout.finishLoadMore();
                mAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerViewCustom = findViewById(R.id.view_recycler_custom);
        mRecyclerViewCustom.setLayoutManager(new LinearLayoutManager(this));
        mAdapterCustom = new NodeRecordCustomAdapter();
        mRecyclerViewCustom.setAdapter(mAdapterCustom);

        getPublicNode();
        getCustomNode();
    }

    public static void startJtNodeRecordActivity(Context context) {
        Intent intent = new Intent(context, JtNodeRecordActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    class NodeRecordAdapter extends RecyclerView.Adapter<NodeRecordAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            RelativeLayout mLayoutItem;
            TextView mTvNodeUrl;
            TextView mTvNodeName;
            TextView mTvNodePing;
            ImageView mImgLoad;
            RadioButton mRadioSelected;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mTvNodeUrl = itemView.findViewById(R.id.tv_node_url);
                mTvNodeName = itemView.findViewById(R.id.tv_node_name);
                mTvNodePing = itemView.findViewById(R.id.tv_ping);
                mImgLoad = itemView.findViewById(R.id.img_ping);
                mRadioSelected = itemView.findViewById(R.id.radio_selected);
                mRadioSelected.setClickable(false);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelCustomNode();
                        VH vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                        if (getAdapterPosition() != mSelectedItem && vh != null) {
                            vh.mRadioSelected.setChecked(false);
                            mSelectedItem = getAdapterPosition();
                            vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                            vh.mRadioSelected.setChecked(true);
                        } else {
                            if (mSelectedItem != -1) {
                                notifyItemChanged(mSelectedItem);
                            }
                            mSelectedItem = getAdapterPosition();
                            vh = (VH) mRecyclerView.findViewHolderForLayoutPosition(getAdapterPosition());
                            vh.mRadioSelected.setChecked(true);
                        }
                    }
                });
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_node, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (publicNodes == null || publicNodes.getLength() == 0) {
                return;
            }
            GsonUtil item = publicNodes.getObject(position);
            holder.mTvNodeName.setText(item.getString("name", ""));
            String url = item.getString("node", "");
            holder.mTvNodeUrl.setText(url);
            holder.mLayoutItem.setClickable(true);
            if (TextUtils.equals(holder.mTvNodeUrl.getText().toString(), JtServer.getInstance(JtNodeRecordActivity.this).getServer()) && mSelectedItem == -1 && mSelectedCustomItem == -1) {
                mSelectedItem = position;
                holder.mRadioSelected.setChecked(true);
            } else {
                holder.mRadioSelected.setChecked(false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] ws = url.replace("ws://", "").replace("wss://", "").split(":");
                    if (ws.length != 2) {
                        return;
                    }
                    String host = ws[0];
                    String port = ws[1];
                    try {
                        ArrayList<Integer> prots = PortScan.onAddress(host).setMethodTCP().setPort(Integer.valueOf(port)).doScan();
                        if (prots != null && prots.size() == 1) {
                            Ping.onAddress(host).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                                @Override
                                public void onResult(PingResult pingResult) {
                                }

                                @Override
                                public void onFinished(PingStats pingStats) {
                                    AppConfig.postOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String ping = String.format("%.2f", pingStats.getAverageTimeTaken());
                                            holder.mTvNodePing.setText(ping + "ms");
                                            BigDecimal pingBig = new BigDecimal(ping);
                                            if (pingBig.compareTo(PING_QUICK) == -1) {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_quick));
                                            } else if (pingBig.compareTo(PING_LOW) == -1) {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_normal));
                                            } else {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                                            }
                                            holder.mTvNodePing.setVisibility(View.VISIBLE);
                                            holder.mImgLoad.setVisibility(View.GONE);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                }
                            });
                        } else {
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mLayoutItem.setClickable(false);
                                    holder.mTvNodePing.setText("---");
                                    holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                                    holder.mTvNodePing.setVisibility(View.VISIBLE);
                                    holder.mImgLoad.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return publicNodes.getLength();
        }
    }

    class NodeRecordCustomAdapter extends RecyclerView.Adapter<NodeRecordCustomAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            RelativeLayout mLayoutItem;
            TextView mTvNodeUrl;
            TextView mTvNodeName;
            TextView mTvNodePing;
            ImageView mImgLoad;
            RadioButton mRadioSelected;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mTvNodeUrl = itemView.findViewById(R.id.tv_node_url);
                mTvNodeName = itemView.findViewById(R.id.tv_node_name);
                mTvNodePing = itemView.findViewById(R.id.tv_ping);
                mImgLoad = itemView.findViewById(R.id.img_ping);
                mRadioSelected = itemView.findViewById(R.id.radio_selected);
                mRadioSelected.setClickable(false);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDefaultNode();
                        VH vh = (VH) mRecyclerViewCustom.findViewHolderForLayoutPosition(mSelectedCustomItem);
                        if (getAdapterPosition() != mSelectedCustomItem && vh != null) {
                            vh.mRadioSelected.setChecked(false);
                            mSelectedCustomItem = getAdapterPosition();
                            vh = (VH) mRecyclerViewCustom.findViewHolderForLayoutPosition(mSelectedCustomItem);
                            vh.mRadioSelected.setChecked(true);
                        } else {
                            if (mSelectedCustomItem != -1) {
                                notifyItemChanged(mSelectedCustomItem);
                            }
                            mSelectedCustomItem = getAdapterPosition();
                            vh = (VH) mRecyclerViewCustom.findViewHolderForLayoutPosition(mSelectedCustomItem);
                            vh.mRadioSelected.setChecked(true);
                        }
                    }
                });
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_node, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (publicNodesCustom == null || publicNodesCustom.size() == 0) {
                return;
            }
            int last = publicNodesCustom.size() - 1;
            // 倒序显示
            String item = publicNodesCustom.get(last - position);
            holder.mTvNodeUrl.setText(item);
            if (mSelectedItem == -1 && mSelectedCustomItem == position) {
                holder.mRadioSelected.setChecked(true);
            } else {
                holder.mRadioSelected.setChecked(false);
            }
            holder.mLayoutItem.setClickable(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] ws = item.replace("ws://", "").replace("wss://", "").split(":");
                    if (ws.length != 2) {
                        return;
                    }
                    String host = ws[0];
                    String port = ws[1];
                    try {
                        ArrayList<Integer> prots = PortScan.onAddress(host).setMethodTCP().setPort(Integer.valueOf(port)).doScan();
                        if (prots != null && prots.size() == 1) {
                            Ping.onAddress(host).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                                @Override
                                public void onResult(PingResult pingResult) {
                                }

                                @Override
                                public void onFinished(PingStats pingStats) {
                                    AppConfig.postOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String ping = String.format("%.2f", pingStats.getAverageTimeTaken());
                                            holder.mTvNodePing.setText(ping + "ms");
                                            BigDecimal pingBig = new BigDecimal(ping);
                                            if (pingBig.compareTo(PING_QUICK) == -1) {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_quick));
                                            } else if (pingBig.compareTo(PING_LOW) == -1) {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_normal));
                                            } else {
                                                holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                                            }
                                            holder.mTvNodePing.setVisibility(View.VISIBLE);
                                            holder.mImgLoad.setVisibility(View.GONE);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                }
                            });
                        } else {
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mLayoutItem.setClickable(false);
                                    holder.mTvNodePing.setText("---");
                                    holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                                    holder.mTvNodePing.setVisibility(View.VISIBLE);
                                    holder.mImgLoad.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return publicNodesCustom.size();
        }
    }

    // 获取默认节点
    private void getPublicNode() {
        publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
        if (mAdapter != null) {
            cancelCustomNode();
            mAdapter.notifyDataSetChanged();
        }
    }

    // 获取自定义节点
    private void getCustomNode() {
        publicNodesCustom.clear();
        String fileName = JtNodeRecordActivity.this.getPackageName() + "_customNode";
        SharedPreferences sharedPreferences = JtNodeRecordActivity.this.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String nodes = sharedPreferences.getString("nodes", "");
        List<String> nodeList;
        if (!TextUtils.isEmpty(nodes)) {
            if (nodes.contains(",")) {
                List<String> arrList = Arrays.asList(nodes.split(","));
                nodeList = new ArrayList(arrList);
            } else {
                nodeList = new ArrayList();
                nodeList.add(nodes);
            }
            publicNodesCustom.addAll(nodeList);
            if (mAdapterCustom != null) {
                cancelDefaultNode();
                mAdapterCustom.notifyDataSetChanged();
            }
        }
    }

    // 去消默认节点选中
    private void cancelDefaultNode() {
        NodeRecordAdapter.VH vh = (NodeRecordAdapter.VH) mRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
        if (vh != null) {
            vh.mRadioSelected.setChecked(false);
            mSelectedItem = -1;
        }
    }

    // 去消自定义节点选中
    private void cancelCustomNode() {
        NodeRecordCustomAdapter.VH vh = (NodeRecordCustomAdapter.VH) mRecyclerViewCustom.findViewHolderForLayoutPosition(mSelectedCustomItem);
        if (vh != null) {
            vh.mRadioSelected.setChecked(false);
            mSelectedCustomItem = -1;
        }
    }
}
