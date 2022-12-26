package com.flowring.laleents.tools;

import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.phone.DefinedUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetUtils {

    private static final String TAG = NetUtils.class.getSimpleName();

    private static final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
        }
    };

    public static boolean isNetworkAvailable(Context ctx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "Exception...", e);
            return false;
        }
    }

    public static String POSTHttp(String url, JSONObject body, String header) {
        HttpURLConnection httpConn = null;
        try {
            URL mURL = new URL(url);
            httpConn = (HttpURLConnection) mURL.openConnection();
            httpConn.setRequestProperty("Content-Type", "application/json");//; charset=UTF-8");
            httpConn.setRequestProperty("Accept", "application/json");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpConn.setRequestProperty("Authorization", header);
            }
            httpConn.setRequestMethod("POST");
            httpConn.setReadTimeout(30000);
            httpConn.setConnectTimeout(30000);
            httpConn.setDoOutput(true);

            if (body != null) {
                String jsonString = body.toString();
                OutputStreamWriter outWrite = new OutputStreamWriter(httpConn.getOutputStream());
                outWrite.write(jsonString);
                outWrite.flush();
                outWrite.close();
            }

            int responseCode = httpConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POST:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                return "responseCode = " + responseCode + ", errorMsg = " + errMsg;
                //throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return null;
    }

    public static String POST(String url, JSONObject body, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", "application/json");//; charset=UTF-8");
            httpsConn.setRequestProperty("Accept", "application/json");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("POST");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            if (body != null) {
                String jsonString = body.toString();
                OutputStreamWriter outWrite = new OutputStreamWriter(httpsConn.getOutputStream());
                outWrite.write(jsonString);
                outWrite.flush();
                outWrite.close();
            }

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POST:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                return "responseCode = " + responseCode + ", errorMsg = " + errMsg;
                //throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String PUT(String url, JSONObject body, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", "application/json");//; charset=UTF-8");
            httpsConn.setRequestProperty("Accept", "application/json");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("PUT");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            if (body != null) {
                String jsonString = body.toString();
                OutputStreamWriter outWrite = new OutputStreamWriter(httpsConn.getOutputStream());
                outWrite.write(jsonString);
                outWrite.flush();
                outWrite.close();
            }

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "PUT:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                return "responseCode = " + responseCode + ", errorMsg = " + errMsg;
                //throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String POSTMedia(String url, File file, String header) {
        try {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            String mimeType = "image/jpeg";
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }

//            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
//                    .addPart(
//                            Headers.of("Content-Disposition", "form-data; name=\"sticker\"; filename=\"" + file.getName() + "\""),
//                            RequestBody.create(MediaType.parse(mimeType), file)).build();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//
//            OkHttpClient client = new OkHttpClient();
//            Response response = client.newCall(request).execute();
//
//            if (response.code() == 200) {
//                InputStream is = response.body().byteStream();
//                String res = getStringFromInputStream(is);
//                is.close();
//                return res;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

//            if (httpsConn != null) {
//                httpsConn.disconnect();
//            }
        }
        return null;
    }

    public static String POSTMedia(String url, Bitmap bitmap, String header) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();

//            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
//                    .addPart(
//                            Headers.of("Content-Disposition", "form-data; name=\"images\"; filename=\"background.jpg\""),
//                            RequestBody.create(MediaType.parse("image/jpeg"), bytes)).build();
//
//            Request request = new Request.Builder()
//                    .addHeader("Authorization", header)
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//
//            OkHttpClient client = new OkHttpClient();
//            Response response = client.newCall(request).execute();
//
//            if (response.code() == 200) {
//                InputStream is = response.body().byteStream();
//                String res = getStringFromInputStream(is);
//                is.close();
//                return res;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

//            if (httpsConn != null) {
//                httpsConn.disconnect();
//            }
        }
        return null;
    }

    public static String POSTMedia(String url, String mimeType, Bitmap bitmap, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", mimeType);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("POST");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (mimeType.equals("image/png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            }
            byte[] bytes = stream.toByteArray();
            OutputStream out = httpsConn.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POSTMedia:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                throw new NetworkErrorException("response status is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String POSTAudio(String url, String filePath, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", "audio/mpeg");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("POST");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            File file = new File(filePath);
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes); //read file into bytes[]
            fis.close();

            OutputStream out = httpsConn.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POSTAudio:" + url + "\n ERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                throw new NetworkErrorException("response status is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String POSTFile(Context context, String url, Uri uri, int size, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            ContentResolver cr = context.getContentResolver();
            String mimeType = cr.getType(uri);

            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", mimeType);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("POST");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            InputStream inputStream = cr.openInputStream(uri);
            DataOutputStream dos = new DataOutputStream(httpsConn.getOutputStream());
            byte[] b = new byte[size];
            int i = 0;
            while ((i = inputStream.read(b)) != -1) {
                dos.write(b, 0, b.length);
            }
            dos.flush();
            dos.close();
            inputStream.close();

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POSTFile:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                return null;//throw new NetworkErrorException("response status is "+responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "POSTFile:" + url + "\nERROR = " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String POSTToken(String url, String body, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
            httpsConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestMethod("POST");
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            if (body != null) {
                OutputStreamWriter outWrite = new OutputStreamWriter(httpsConn.getOutputStream());
                outWrite.write(body);
                outWrite.flush();
                outWrite.close();
            }

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, "POST:" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                return "responseCode = " + responseCode + ", errorMsg = " + errMsg;
                //throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String GETTest(String url, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpURLConnection) mURL.openConnection();
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setRequestMethod("GET");
            httpsConn.setConnectTimeout(30000);
            httpsConn.setReadTimeout(30000);
            httpsConn.connect();
            int responseCode = httpsConn.getResponseCode();
            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                default:
                    Log.e(TAG, "GET:" + url + "\n ERROR: responseCode = " + responseCode + ", errorMsg = " + getStringFromInputStream(httpsConn.getErrorStream()));
                    return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String GET(String url, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setRequestMethod("GET");
            httpsConn.setConnectTimeout(30000);
            httpsConn.setReadTimeout(30000);
            httpsConn.connect();
            int responseCode = httpsConn.getResponseCode();
            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                default:
                    Log.e(TAG, "GET:" + url + "\n ERROR: responseCode = " + responseCode + ", errorMsg = " + getStringFromInputStream(httpsConn.getErrorStream()));
                    return "";
            }

        } catch (Exception e) {
            Log.e(TAG, "GET:" + url + "\n exception" + e);
            e.printStackTrace();
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static String OfficialGET(String url, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setRequestMethod("GET");
//            httpsConn.setConnectTimeout(3000000);
//            httpsConn.setReadTimeout(3000000);
            httpsConn.connect();
            int responseCode = httpsConn.getResponseCode();
            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                default:
                    Log.e(TAG, "GET:" + url + "\n ERROR: responseCode = " + responseCode + ", errorMsg = " + getStringFromInputStream(httpsConn.getErrorStream()));
                    return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "GET:" + e);
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    public static Bitmap GETBitmap(String url, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }

            InputStream is = httpsConn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    public static Bitmap GETBitmapAndSave(Context context, String url, String header, String fileName) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }

            InputStream is = httpsConn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            File targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile != null && targetFile.exists())
                targetFile.delete();
            Bitmap.CompressFormat format = (fileName.contains(".png")) ?
                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            FileOutputStream fileOS = new FileOutputStream(targetFile);
            bitmap.compress(format, 70, fileOS);

            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            if (bitmap.sameAs(emptyBitmap)) {
                targetFile.delete();
            }

            is.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(context.getResources(),
                    fileName.isEmpty() ? R.drawable.default_group : R.drawable.default_person);
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    public static Bitmap GETUserBitmapAndCache(Context context, String url, String header, String fileName) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }

            InputStream is = httpsConn.getInputStream();
            Bitmap bitmap = decodeToBitmap(context, is, fileName, 90, 70);
            is.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    public static Bitmap GETStickerAndSave(Context context, String url, String header, String fileName) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }

            InputStream is = httpsConn.getInputStream();
            Bitmap bitmap = decodeToBitmap(context, is, fileName, 144, 100);
            is.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(context.getResources(),
                    fileName.isEmpty() ? R.drawable.default_group : R.drawable.default_person);
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    private static Bitmap decodeToBitmap(Context context, InputStream is, String fileName, int dp, int quality) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, len);
            }
            byte[] imageData = baos.toByteArray();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

            int size = (int) CommonUtils.convertDpToPixel(dp, context);
            options.inSampleSize = com.flowring.laleents.tools.FileUtils.calculateInSampleSize(options, size, size);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

            File targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
            Bitmap.CompressFormat format = (fileName.contains(".png")) ?
                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            FileOutputStream fileOS = new FileOutputStream(targetFile);
            bitmap.compress(format, quality, fileOS);

            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            if (bitmap.sameAs(emptyBitmap)) {
                targetFile.delete();
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap[] GETBitmap(String[] url, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        List<Bitmap> bmpList = new ArrayList<>();
        try {
            for (String sURL : url) {
                bmpList.add(GETBitmap(sURL, header));
            }
            Bitmap[] bmps = new Bitmap[bmpList.size()];
            return bmpList.toArray(bmps);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    public static String GETAudioAndSave(Context context, String url, String header, String fileName) {
        String filePath = com.flowring.laleents.tools.FileUtils.getApplicationFolder(context, DefinedUtils.FOLDER_AUDIO) + "/" + fileName;
        return GETFile(url, filePath, 4 * 1024);
    }

    public static String GETVideoAndSave(Context context, String url, String header, String fileName, int fileSize) {
        String filePath = com.flowring.laleents.tools.FileUtils.getApplicationFolder(context, DefinedUtils.FOLDER_VIDEO) + "/" + fileName;
        return GETFile(url, filePath, fileSize);
    }

    public static String GETFileAndSave(Context context, String url, String header, String fileName, int fileSize) {
        String filePath = com.flowring.laleents.tools.FileUtils.getApplicationFolder(context, DefinedUtils.FOLDER_FILES) + "/" + fileName;
        return GETFile(url, filePath, fileSize);
    }

    private static String GETFile(String url, String filePath, int fileSize) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);

            InputStream is = httpsConn.getInputStream();
            File targetFile = new File(filePath);
            OutputStream output = new FileOutputStream(targetFile);
            byte[] buffer = new byte[fileSize];
            int read;
            while ((read = is.read(buffer)) != -1)
                output.write(buffer, 0, read);

            output.flush();
            is.close();
            return filePath;
        } catch (Exception e) {
            //InputStream is = httpsConn.getErrorStream();
            //String error = is.toString();
            e.printStackTrace();
            return "";
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    public static String GETStickerZip(Context context, String url, int productId, String header) {
        try {
            String zipPath = String.format("%s/%d.zip", context.getCacheDir(), productId);
            String filePath = String.format("%s/laleSticker%d", com.flowring.laleents.tools.FileUtils.getApplicationFolder(context, DefinedUtils.FOLDER_STICKER), productId);
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            Response response = client.newCall(request).execute();
//            if (!response.isSuccessful()) {
//                throw new IOException("Failed to download file: " + response);
//            }
//            FileOutputStream fos = new FileOutputStream(zipPath);
//            fos.write(response.body().bytes());
//            fos.close();

            File stickerFile = new File(filePath);
            com.flowring.laleents.tools.FileUtils.unzipSticker(context, new File(zipPath), stickerFile, String.valueOf(productId));
            if (stickerFile.exists()) {
                FileUtils.saveProductStickerId(context, String.valueOf(productId), true);
            }

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String API(String url, String method, JSONObject body, String header) {
        SSLSocketFactory ssf = trustAllHosts().getSocketFactory();
        HttpsURLConnection httpsConn = null;
        try {
            URL mURL = new URL(url);
            httpsConn = (HttpsURLConnection) mURL.openConnection();
            httpsConn.setSSLSocketFactory(ssf);
            httpsConn.setHostnameVerifier(hostnameVerifier);
            httpsConn.setRequestProperty("Content-Type", "application/json");//; charset=UTF-8");
            httpsConn.setRequestProperty("Accept", "application/json");
            if (!header.isEmpty() && !header.equals("Bearer ")) {
                httpsConn.setRequestProperty("Authorization", header);
            }
            httpsConn.setRequestMethod(method);
            httpsConn.setReadTimeout(30000);
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoOutput(true);

            if (body != null) {
                String jsonString = body.toString();
                OutputStreamWriter outWrite = new OutputStreamWriter(httpsConn.getOutputStream());
                outWrite.write(jsonString);
                outWrite.flush();
                outWrite.close();
            }

            int responseCode = httpsConn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpsConn.getInputStream();
                String response = getStringFromInputStream(is);
                is.close();
                return response;
            } else {
                InputStream is = httpsConn.getErrorStream();
                String errMsg = getStringFromInputStream(is);
                is.close();
                Log.e(TAG, method + ":" + url + "\nERROR: responseCode = " + responseCode + ", errorMsg = " + errMsg);
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        return null;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }

    private static SSLContext trustAllHosts() {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (chain == null || chain.length == 0)
                    throw new IllegalArgumentException("Certificate is null or empty");
                if (authType == null || authType.length() == 0)
                    throw new IllegalArgumentException("Authtype is null or empty");
                if (!authType.equalsIgnoreCase("ECDHE_RSA") &&
                        !authType.equalsIgnoreCase("ECDHE_ECDSA") &&
                        !authType.equalsIgnoreCase("RSA") &&
                        !authType.equalsIgnoreCase("ECDSA") &&
                        !authType.equalsIgnoreCase("GENERIC"))
                    throw new CertificateException("Certificate is not trust, authType:" + authType);
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate is not valid or trusted");
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            return sc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
