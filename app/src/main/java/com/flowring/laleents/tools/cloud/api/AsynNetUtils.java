package com.flowring.laleents.tools.cloud.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.FileUtils;
import com.flowring.laleents.tools.NetUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class AsynNetUtils {

    public static void OfficialGET(final Context context, final String url, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (context == null) {
                    return;
                }
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");
                final String response = NetUtils.OfficialGET(https + url, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                        Looper.loop();
                    }
                });
            }
        }).start();
    }

    public static void GET(final Context context, final String url, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.GET(https + url, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void POST(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.POST(https + url, body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void POSTMedia(final Context context, final String url, final Bitmap body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.POSTMedia(https + url, body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void POSTFormData(final Context context, final String url, final File body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.POSTMedia(https + url, body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void GETTest(final Context context, final String url, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = "http://192.168.3.214";
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.GETTest(https + url, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void POSTTest(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = "https://192.168.5.223:80";
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.POST(https + url, body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void PUT(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.API(https + url, "PUT", body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void DELETE(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.API(https + url, "DELETE", body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void SignupPOST(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");

                final String response = NetUtils.POST(https + url, body, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void GETBitmap(final Context context, final String url, final BitmapCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final Bitmap response = NetUtils.GETBitmap(url, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void GETSticker(final Context context, final String url, final String fileName, final BitmapCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final Bitmap response = NetUtils.GETStickerAndSave(context, https + url, header, fileName);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void GETBitmapArray(final Context context, final String[] urls, final BitmapArrayCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final Bitmap[] bitmaps = NetUtils.GETBitmap(urls, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(bitmaps);
                    }
                });
            }
        }).start();
    }

    public static void GETStickerZip(final Context context, final String url, final int productId, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");

                final String response = NetUtils.GETStickerZip(context, https + url, productId, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixGET(final Context context, final String url, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");

                final String response = NetUtils.GET(https + url, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixGETBitmap(final Context context, final String avatar_id, final String userID, final BitmapCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (avatar_id == null || avatar_id.isEmpty()) {
                    final Bitmap response = BitmapFactory.decodeResource(context.getResources(),
                            userID.isEmpty() ? R.drawable.default_group : R.drawable.default_person);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                } else {
                    final Bitmap response;
                    String sURL = avatar_id;
                    if (avatar_id.startsWith("mxc://")) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                        sURL = pref.getString(CommonUtils.PREF_MATRIX_URL, "") +
                                "/_matrix/media/r0/download/" + avatar_id.replace("mxc://", "");
                    }
                    String fileName = FileUtils.getUrlFileName(avatar_id, ".jpg");
                    response = NetUtils.GETUserBitmapAndCache(context, sURL, "", fileName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                }
            }
        }).start();
    }

    public static void MatrixGETAudio(final Context context, final String url, final String fileName, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String https = url;
                if (!url.contains("/_matrix/media/r0/download/")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    https = pref.getString(CommonUtils.PREF_MATRIX_URL, "") +
                            "/_matrix/media/r0/download/" + url;
                }
                final String response = NetUtils.GETAudioAndSave(context, https, "", fileName);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }


    public static void GETVideoAndSave(final Context context, final String url, final String fileName, final int fileSize, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String https = url;

                final String response = NetUtils.GETVideoAndSave(context, https, "", fileName, fileSize);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixGETFile(final Context context, final String url, final String fileName, final int fileSize, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String https = url;
                if (!url.contains("/_matrix/media/r0/download/")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    https = pref.getString(CommonUtils.PREF_MATRIX_URL, "") +
                            "/_matrix/media/r0/download/" + url;
                }
                final String response = NetUtils.GETFileAndSave(context, https, "", fileName, fileSize);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixPUT(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");

                final String response = NetUtils.API(https + url, "PUT", body, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixPOST(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");

                final String response = NetUtils.POST(https + url, body, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixPOSTMedia(final Context context, final String url, final String mimeType, final Bitmap body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");
                final String response = NetUtils.POSTMedia(https + url, mimeType, body, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixPOSTAudio(final Context context, final String url, final String filePath, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");
                final String response = NetUtils.POSTAudio(https + url, filePath, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void MatrixPOSTFile(final Context context, final String url, final Uri uri, final int size, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_MATRIX_URL, "");
                final String response = NetUtils.POSTFile(context, https + url, uri, size, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void AfPOST(final Context context, final String url, final JSONObject body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String companyUrl = pref.getString(CommonUtils.PREF_COMPANY_URL, "");
                String https = DefinedUtils.workURL.substring(0, 31);
                if (companyUrl.contains("upn2")) {
                    https.replace("work", "upn2");
                }
                String header = "Bearer " + pref.getString(CommonUtils.PREF_AF_TOKEN, "");
                final String response = NetUtils.POST(https + url, body, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void AfPOSTToken(final Context context, final String url, final String domain, final String body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String companyUrl = pref.getString(CommonUtils.PREF_COMPANY_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_AF_TOKEN, "");
                try {
                    URL domainUrl = new URL(domain);
                    String https = "";
                    if (domain.contains("WebAgenda")) {
                        https = domain.split("WebAgenda")[0] + "WebAgenda/";
                    } else {
                        https = domainUrl.getProtocol() + "://" + domainUrl.getHost() + "/";
                    }
                    final String response = NetUtils.POSTToken(https + url, body, header);
                    boolean isResponseExist = response != null && !response.isEmpty();
                    try {
                        if (isResponseExist) {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject.put("url", domain);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResponse(jsonObject.toString());
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResponse(response);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(response);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void AfGET(final Context context, final String url, String afToken, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                String header = "Bearer " + afToken;
                final String response = NetUtils.GET(url, header);
                boolean isResponseExist = response != null && !response.isEmpty();
                try {
                    if (isResponseExist) {
                        JSONObject jsonObject = new JSONObject(response);
                        jsonObject.put("url", url);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(jsonObject.toString());
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(response);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                }
            }
        }).start();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void AfGETBitmap(final Context context, final String url, String afToken, final BitmapCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                String header = "Bearer " + afToken;
                final Bitmap response = NetUtils.GETBitmap(url, header);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void NewsPOST(final Context context, final String url, final String domain, final String body, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = NetUtils.POSTToken(domain + url, body, "");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void MemiaGet(final Context context, final String url, final String domain, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = NetUtils.GET(domain + url, "");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface Callback {
        void onResponse(String response);
    }

    public interface BitmapArrayCallback {
        void onResponse(Bitmap[] response);
    }

    public interface BitmapCallback {
        void onResponse(Bitmap response);
    }
}
