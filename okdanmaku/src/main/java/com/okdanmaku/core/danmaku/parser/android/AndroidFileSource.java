package com.okdanmaku.core.danmaku.parser.android;

import android.net.Uri;

import com.okdanmaku.core.danmaku.parser.IDataSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yangya on 2019-11-19.
 */
public class AndroidFileSource implements IDataSource {

    private static final String SCHEME_HTTP = "http";

    private static final String SCHEME_HTTPS = "https";

    private static final String SCHEME_FILE = "file";

    public InputStream mInputStream;

    public AndroidFileSource(String filepath) {
        fillStreamFromFile(new File(filepath));
    }

    public AndroidFileSource(Uri uri) {
        fillStreamFromUri(uri);
    }

    public AndroidFileSource(File file) {
        fillStreamFromFile(file);
    }

    private void fillStreamFromFile(File file) {
        try {
            mInputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fillStreamFromUri(Uri uri) {
        if (uri == null || uri.getPath() == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (SCHEME_HTTP.equalsIgnoreCase(scheme) || SCHEME_HTTPS.equalsIgnoreCase(scheme)) {
            fillStreamFromHttpFile(uri);
        } else if (SCHEME_FILE.equalsIgnoreCase(scheme)) {
            fillStreamFromFile(new File(uri.getPath()));
        }
    }

    private void fillStreamFromHttpFile(Uri uri) {
        try {
            URL url = new URL(uri.getPath());
            url.openConnection();
            mInputStream = new BufferedInputStream(url.openStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void release() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mInputStream = null;
            }
        }
    }
}
