package com.doughnut.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.jtblk.BIP44.AddressIndex;
import com.android.jtblk.BIP44.BIP44;
import com.android.jtblk.exceptions.CoinNotFindException;
import com.android.jtblk.exceptions.NonSupportException;
import com.doughnut.R;
import com.doughnut.activity.MainActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.PWDUtils;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;


public class MnemonicImpFragment extends BaseFragment implements View.OnClickListener {

    private RadioButton mRadioRead;
    private EditText mEdtWord1, mEdtWord2, mEdtWord3, mEdtWord4, mEdtWord5, mEdtWord6, mEdtWord7, mEdtWord8, mEdtWord9, mEdtWord10, mEdtWord11, mEdtWord12,
            mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm, mEdtPath;
    private TextView mTvPolicy, mTvErrPassword, mTvErrPasswordRep;
    private ImageView mImgShowPwd;
    private LinearLayout mTvShowPwd, mLayoutRead;
    private RelativeLayout mLayoutMode;
    private Button mBtnConfirm;
    private Switch mSwhMode, mSwhED25519;
    private boolean isED25519 = false;
    private static final String DEFAULTPAHT = "m/44'/315'/0'/0/0/";

    private boolean isErr;
    private TransformationMethod transformationMethod = new TransformationMethod() {
        @Override
        public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
            // TODO Auto-generated method stub

        }

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            // TODO Auto-generated method stub
            return new SubCharSequence(source);
        }
    };

    public static MnemonicImpFragment newInstance() {
        MnemonicImpFragment fragment = new MnemonicImpFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mnemonic_imp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isErr = false;
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBtnConfirm.setClickable(true);
        mEdtWord1.requestFocus();
        mEdtWord1.setSelection(mEdtWord1.getText().toString().length());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
                String mnemonics = getMnemonics();
                String walletName = mEdtWalletName.getText().toString();
                String walletPwd = mEdtWalletPwd.getText().toString();
                LoadDialog loadDialog = new LoadDialog(getContext(), getString(R.string.dialog_import));
                loadDialog.show();
                if (mSwhMode.isChecked()) {
                    // 导入钱包
                    String path = mEdtPath.getText().toString();
                    AddressIndex addressIndex;
                    try {
                        addressIndex = BIP44.parsePath(path);
                    } catch (NonSupportException e) {
                        mEdtPath.setText(DEFAULTPAHT);
                        new MsgDialog(getContext(), getString(R.string.path_err)).setIsHook(false).show();
                        return;
                    } catch (CoinNotFindException e) {
                        mEdtPath.setText(DEFAULTPAHT);
                        new MsgDialog(getContext(), getString(R.string.path_err)).setIsHook(false).show();
                        return;
                    }
                    WalletManager.getInstance(getContext()).importMnemonicsWithPath(mnemonics, walletPwd, walletName, addressIndex, isED25519, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            boolean isSuccess = (boolean) response;
                            loadDialog.dismiss();
                            if (isSuccess) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra(Constant.IMPORT_FLAG, true);
                                intent.putExtra(Constant.PAGE_INDEX, "0");
                                intent.putExtra(Constant.WALLET_NAME, walletName);
                                startActivity(intent);
                            } else {
                                new MsgDialog(getContext(), getString(R.string.dialog_import_fail)).setIsHook(false).show();
                            }
                        }
                    });
                } else {
                    // 导入钱包
                    WalletManager.getInstance(getContext()).importMnemonics(mnemonics, walletPwd, walletName, isED25519, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            boolean isSuccess = (boolean) response;
                            loadDialog.dismiss();
                            if (isSuccess) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra(Constant.IMPORT_FLAG, true);
                                intent.putExtra(Constant.PAGE_INDEX, "0");
                                intent.putExtra(Constant.WALLET_NAME, walletName);
                                startActivity(intent);
                            } else {
                                new MsgDialog(getContext(), getString(R.string.dialog_import_fail)).setIsHook(false).show();
                            }
                        }
                    });
                }
                break;
            // 勾选框
            case R.id.layout_read:
                mRadioRead.setChecked(!mRadioRead.isChecked());
                isImportWallet();
                break;
            // 跳转服务条款页面
            case R.id.tv_policy:
                gotoServiceTermPage();
                break;
            case R.id.show_pwd:
                mImgShowPwd.setSelected(!mImgShowPwd.isSelected());
                if (mImgShowPwd.isSelected()) {
                    mEdtWalletPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtWalletPwd.setTransformationMethod(transformationMethod);
                }
                mEdtWalletPwd.setSelection(mEdtWalletPwd.getText().length());
                break;
            case R.id.swh_ed25519:
                isED25519 = mSwhED25519.isChecked();
                break;
            case R.id.swh_mode:
                if (mSwhMode.isChecked()) {
                    mLayoutMode.setVisibility(View.VISIBLE);
                } else {
                    mLayoutMode.setVisibility(View.GONE);
                    mSwhED25519.setChecked(false);
                    isED25519 = mSwhED25519.isChecked();
                    mEdtPath.setText(DEFAULTPAHT);
                }
                break;
        }
    }


    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view) {
        mEdtWord1 = view.findViewById(R.id.word1);
        mEdtWord1.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord2.requestFocus();
                        } else if (before > 0 && count == 0) {
                            mEdtWord1.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord2 = view.findViewById(R.id.word2);
        mEdtWord2.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord3.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("2");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord2.getText().length() == 0) {
                    mEdtWord1.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord3 = view.findViewById(R.id.word3);
        mEdtWord3.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord4.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("3");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord3.getText().length() == 0) {
                    mEdtWord2.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord4 = view.findViewById(R.id.word4);
        mEdtWord4.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord5.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("4");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord4.getText().length() == 0) {
                    mEdtWord3.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord5 = view.findViewById(R.id.word5);
        mEdtWord5.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord6.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("5");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord5.getText().length() == 0) {
                    mEdtWord4.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord6 = view.findViewById(R.id.word6);
        mEdtWord6.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord7.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("6");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord6.getText().length() == 0) {
                    mEdtWord5.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord7 = view.findViewById(R.id.word7);
        mEdtWord7.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord8.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord7.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("7");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord7.getText().length() == 0) {
                    mEdtWord6.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord8 = view.findViewById(R.id.word8);
        mEdtWord8.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord9.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord8.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("8");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord8.getText().length() == 0) {
                    mEdtWord7.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord9 = view.findViewById(R.id.word9);
        mEdtWord9.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord10.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord9.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("9");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord9.getText().length() == 0) {
                    mEdtWord8.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord10 = view.findViewById(R.id.word10);
        mEdtWord10.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord11.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord10.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("10");
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord10.getText().length() == 0) {
                    mEdtWord9.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord11 = view.findViewById(R.id.word11);
        mEdtWord11.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before == 0 && count > 0) {
                            mEdtWord12.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord11.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println(KeyEvent.KEYCODE_DEL);
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord11.getText().length() == 0) {
                    mEdtWord10.requestFocus();
                    event = null;
                }
                return false;
            }
        });
        mEdtWord12 = view.findViewById(R.id.word12);
        mEdtWord12.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before > 0 && count == 0) {
                            mEdtWord11.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWord12.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                System.out.println("12");
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mEdtWord12.getText().length() == 0) {
                    mEdtWord11.requestFocus();
                    event = null;
                }
                return false;
            }
        });

        mEdtWalletName = view.findViewById(R.id.edt_wallet_name);
        mEdtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isImportWallet();
                }
            }
        });

        mEdtWalletPwd = view.findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwd.setTransformationMethod(transformationMethod);
        mTvErrPassword = view.findViewById(R.id.tv_pwd_tips);
        mEdtWalletPwd.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isErr = false;
                        mTvErrPassword.setText(getResources().getString(R.string.tv_pwd_tips));
                        isImportWallet();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWalletPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtWalletPwd.getText().toString();
                    if (!TextUtils.isEmpty(passWord)) {
                        boolean isValid = PWDUtils.verifyPasswordFormat(passWord);
                        if (!isValid) {
                            isErr = true;
                            mEdtWalletPwd.setText("");
                            mTvErrPassword.setText(AppConfig.getCurActivity().getResources().getString(R.string.tv_pwd_err));
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwd.requestFocus();
                                }
                            });
                        } else {
                            isErr = false;
                        }
                    }
                    isImportWallet();
                }
            }
        });

        mEdtWalletPwdConfirm = view.findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletPwdConfirm.setTransformationMethod(transformationMethod);
        mTvErrPasswordRep = view.findViewById(R.id.tv_pwd_rep_tips);
        mEdtWalletPwdConfirm.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (mTvErrPasswordRep.isShown()) {
                            mTvErrPasswordRep.setVisibility(View.GONE);
                        }
                        isImportWallet();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWalletPwdConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtWalletPwd.getText().toString();
                    String passwordConfim = mEdtWalletPwdConfirm.getText().toString();
                    if (!TextUtils.isEmpty(passwordConfim)) {
                        if (!TextUtils.isEmpty(passWord) && !TextUtils.equals(passwordConfim, passWord)) {
                            mEdtWalletPwdConfirm.setText("");
                            mTvErrPasswordRep.setText(AppConfig.getCurActivity().getString(R.string.dialog_content_passwords_unmatch));
                            mTvErrPasswordRep.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwdConfirm.requestFocus();
                                }
                            });
                        } else {
                            boolean isValid = PWDUtils.verifyPasswordFormat(passwordConfim);
                            if (!isValid) {
                                mEdtWalletPwdConfirm.setText("");
                                mTvErrPasswordRep.setText(AppConfig.getCurActivity().getResources().getString(R.string.tv_pwd_err));
                                mTvErrPasswordRep.setVisibility(View.VISIBLE);
                                AppConfig.postOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtWalletPwdConfirm.requestFocus();
                                    }
                                });
                            }

                        }
                    }
                    isImportWallet();
                }
            }
        });

        mRadioRead = view.findViewById(R.id.radio_read);
        mLayoutRead = view.findViewById(R.id.layout_read);
        mLayoutRead.setOnClickListener(this);

        mLayoutMode = view.findViewById(R.id.layout_mode);

        mTvPolicy = view.findViewById(R.id.tv_policy);
        mTvPolicy.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvPolicy.setOnClickListener(this);

        mTvShowPwd = view.findViewById(R.id.show_pwd);
        mTvShowPwd.setOnClickListener(this);
        mImgShowPwd = view.findViewById(R.id.img_show_pwd);

        mSwhED25519 = view.findViewById(R.id.swh_ed25519);
        mSwhED25519.setOnClickListener(this);

        mSwhMode = view.findViewById(R.id.swh_mode);
        mSwhMode.setOnClickListener(this);

        mEdtPath = view.findViewById(R.id.edt_path);
        mEdtPath.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!mSwhMode.isChecked()) {
                        return;
                    }
                    String path = mEdtPath.getText().toString();
                    try {
                        BIP44.parsePath(path);
                    } catch (NonSupportException e) {
                        new MsgDialog(getContext(), getString(R.string.path_err)).setIsHook(false).show();
                    } catch (CoinNotFindException e) {
                        new MsgDialog(getContext(), getString(R.string.path_err)).setIsHook(false).show();
                    }
                }
            }
        });

        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 判断导入按钮是受可点击
     */
    private void isImportWallet() {
        String walletName = mEdtWalletName.getText().toString();
        String passWord = mEdtWalletPwd.getText().toString();
        boolean isRead = mRadioRead.isChecked();

        if (isFullMne() && !TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && isRead) {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setClickable(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
    }

    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(getContext(), getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }

    private String getMnemonics() {
        return new StringBuilder().append(mEdtWord1.getText().toString()).append(" ").append(mEdtWord2.getText().toString()).append(" ").append(mEdtWord3.getText().toString()).append(" ").append(mEdtWord4.getText().toString()).append(" ")
                .append(mEdtWord5.getText().toString()).append(" ").append(mEdtWord6.getText().toString()).append(" ").append(mEdtWord7.getText().toString()).append(" ").append(mEdtWord8.getText().toString()).append(" ")
                .append(mEdtWord9.getText().toString()).append(" ").append(mEdtWord10.getText().toString()).append(" ").append(mEdtWord11.getText().toString()).append(" ").append(mEdtWord12.getText().toString()).toString();
    }

    private boolean isFullMne() {
        if (TextUtils.isEmpty(mEdtWord1.getText().toString()) || TextUtils.isEmpty(mEdtWord2.getText().toString()) || TextUtils.isEmpty(mEdtWord3.getText().toString()) || TextUtils.isEmpty(mEdtWord4.getText().toString())
                || TextUtils.isEmpty(mEdtWord5.getText().toString()) || TextUtils.isEmpty(mEdtWord6.getText().toString()) || TextUtils.isEmpty(mEdtWord7.getText().toString()) || TextUtils.isEmpty(mEdtWord8.getText().toString())
                || TextUtils.isEmpty(mEdtWord9.getText().toString()) || TextUtils.isEmpty(mEdtWord10.getText().toString()) || TextUtils.isEmpty(mEdtWord11.getText().toString()) || TextUtils.isEmpty(mEdtWord12.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }
}
