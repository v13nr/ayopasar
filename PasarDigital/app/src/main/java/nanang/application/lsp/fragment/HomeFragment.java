package nanang.application.lsp.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import nanang.application.lsp.iad.MainActivity;
import nanang.application.lsp.iad.R;
import nanang.application.lsp.libs.CommonUtilities;
import nanang.application.lsp.model.user;

import nanang.application.lsp.customfonts.MyTextView;

public class HomeFragment extends Fragment {

    public static WebView webView;
    public static RelativeLayout load_masking;
    WebSettings webSettings;


    MyTextView btn_no, btn_yes;

    Dialog dialog_informasi;
    MyTextView btn_ok;
    MyTextView text_title;
    MyTextView text_message;

    Dialog dialog_pilih_gambar;
    MyTextView from_camera, from_galery;

    String mImageCapturePath;
    String id_selected_aset;
    public static String action_add;

    Dialog dialog_delete_aset;
    MyTextView btn_delete_aset_no, btn_delete_aset_yes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_html, container, false);

        webView = (WebView) rootView.findViewById(R.id.webView);
        load_masking = (RelativeLayout) rootView.findViewById(R.id.loadmasking);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebChromeClient(new MyWebViewClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebChromeClient(new MyWebViewClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        loadDetail();
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100) {

            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private void loadDetail() {
        user data_user = CommonUtilities.getLoginUser(getActivity().getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
        webView.loadUrl("file:///android_asset/html/home.html?nama_desa="+CommonUtilities.toTitleCase(data_user.getNama().toLowerCase()));
    }

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        @JavascriptInterface
        public void gotoLogout() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).openDialogLogout();
                }
            });
        }

        @JavascriptInterface
        public void gotoKirimFoto() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).addAset();
                }
            });
        }

        @JavascriptInterface
        public void gotoLokal() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(9);
                }
            });
        }

        @JavascriptInterface
        public void gotoManageAset() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(5);
                }
            });
        }

        @JavascriptInterface
        public void gotoPengaturan() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(7);
                }
            });
        }

        @JavascriptInterface
        public void gotoEditDesa() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(10);
                }
            });
        }


        @JavascriptInterface
        public void gotoTentang() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(8);
                }
            });
        }


        @JavascriptInterface
        public void gotoPinjaman() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    ((MainActivity) getActivity()).displayView(12);
                }
            });

        }

    }
}
