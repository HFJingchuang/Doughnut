package com.doughnut.utils;

import android.os.Build;

import com.doughnut.wallet.ICallBack;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AESUtil {

    private final static String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private final static String AES = "AES";
    private final static String SHA1_PRNG = "SHA1PRNG";
    private final static int KEY_SIZE = 32;

    /*
     * 生成随机密钥
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1_PRNG);
            byte[] bytes_key = new byte[20];
            localSecureRandom.nextBytes(bytes_key);
            String str_key = parseByte2HexStr(bytes_key);
            return str_key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String encrypt(String key, String content) {
        try {
            SecretKeySpec secretKeySpec;
            if (Build.VERSION.SDK_INT >= 28) {
                secretKeySpec = deriveKeyInsecurely(key);
            } else {
                secretKeySpec = fixSmallVersion(key);
            }
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            return parseByte2HexStr(cipher.doFinal(byteContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密
     *
     * @param key
     * @param content
     * @param callBack
     */
    public static void encrypt(String key, String content, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String encrypt = encrypt(key, content);
                emitter.onNext(encrypt);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String encrypt) throws Exception {
                callBack.onResponse(encrypt);
            }
        });
    }

    private static String decrypt(String key, String encrypted) {
        try {
            SecretKeySpec secretKeySpec;
            if (Build.VERSION.SDK_INT >= 28) {
                secretKeySpec = deriveKeyInsecurely(key);
            } else {
                secretKeySpec = fixSmallVersion(key);
            }
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            String decrypted = new String(cipher.doFinal(parseHexStr2Byte(encrypted)));
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param key
     * @param encrypted
     * @param callBack
     */
    public static void decrypt(String key, String encrypted, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String decrypt = decrypt(key, encrypted);
                emitter.onNext(decrypt);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String decrypt) throws Exception {
                callBack.onResponse(decrypt);
            }
        });
    }

    private static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static SecretKeySpec fixSmallVersion(String key) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(AES);
            SecureRandom secureRandom;
            if (28 >= android.os.Build.VERSION.SDK_INT) {
                secureRandom = SecureRandom.getInstance(SHA1_PRNG, "Crypto");
            } else {
                secureRandom = SecureRandom.getInstance(SHA1_PRNG);
            }
            secureRandom.setSeed(key.getBytes());
            generator.init(128, secureRandom);
            byte[] enCodeFormat = generator.generateKey().getEncoded();
            return new SecretKeySpec(enCodeFormat, AES);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKeySpec deriveKeyInsecurely(String password) {
        byte[] passwordBytes = password.getBytes(StandardCharsets.US_ASCII);
        return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, KEY_SIZE), AES);
    }
}
