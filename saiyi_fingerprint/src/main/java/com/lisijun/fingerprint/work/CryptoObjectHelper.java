package com.lisijun.fingerprint.work;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * 描述：指纹密匙创建帮助类
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 15:39
 */

public class CryptoObjectHelper {
    // This can be key name you want. Should be unique for the app.
    private static final String KEY_NAME = "com.lisijun.android.fingerprint_authentication_key";

    // We always use this keystore on Android.
    private static final String KEYSTORE_NAME = "AndroidKeyStore";

    // Should be no need to change these values.
    private static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KEY_ALGORITHM + "/" + BLOCK_MODE + "/" + ENCRYPTION_PADDING;
    private final KeyStore _keystore;

    public CryptoObjectHelper() throws Exception {
        _keystore = KeyStore.getInstance(KEYSTORE_NAME);
        _keystore.load(null);
    }

    /**
     * 创建密匙对象
     * @return 返回密匙实例
     * @throws Exception 抛出异常
     */
    public FingerprintManagerCompat.CryptoObject buildCryptoObject() throws Exception {
        Cipher cipher = createCipher(true);
        return new FingerprintManagerCompat.CryptoObject(cipher);
    }

    /**
     * 创建密码
     * @param retry 是否重试
     * @return 返回密码
     * @throws Exception 抛出异常
     */
    public Cipher createCipher(boolean retry) throws Exception {
        Key key = getKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch(KeyPermanentlyInvalidatedException e) {
            _keystore.deleteEntry(KEY_NAME);
            if(retry) {
                createCipher(false);
            } else {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        return cipher;
    }

    /**
     * 获取密匙Key
     * @return 返回Key
     * @throws Exception 抛出异常
     */
    public Key getKey() throws Exception {
        Key secretKey;
        if(!_keystore.isKeyEntry(KEY_NAME)) {
            createKey();
        }
        secretKey = _keystore.getKey(KEY_NAME, null);
        return secretKey;
    }

    /**
     * 创建密匙Key
     * @throws Exception 抛出异常
     */
    public void createKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}
