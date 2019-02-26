package nanang.application.lsp.fragment;

import android.app.Fragment;
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

public class LspFragment extends Fragment {

    public static WebView webView;
    public static RelativeLayout load_masking;
    WebSettings webSettings;


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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
        webView.loadUrl("file:///android_asset/html/lsp/index.html");
    }

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        /*
        @JavascriptInterface
        public void gotoProsesLogin(final String user, final String pass) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).prosesLogin(user, pass);
                }
            });
        }

        @JavascriptInterface
        public void gotoLupaPassword() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(2);
                }
            });
        }
*/


        @JavascriptInterface
        public void gotoSaveAsetRegister(final String jenis_lapak, final String nama_bank, final String norek, final String npwp, final String nama_anggota, final String nik, final String hp, final String email, final String password, final String namapasar, final  String namalapak, final String alamat) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ((MainActivity) getActivity()).prosesRegister(jenis_lapak, nama_bank, norek, npwp, nama_anggota, nik, hp, email, password, namapasar, namalapak, alamat);

                    }
                });
            }

        @JavascriptInterface
        public void gotoLoadDataJnsLapak() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).loadDataAsetJnsLapak();
                }
            });
        }


        @JavascriptInterface
        public void goto_cari() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).goto_caripinjaman();
                }
            });

        }
        @JavascriptInterface
        public void goto_login() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(11);
                }
            });

        }


        @JavascriptInterface
        public  void kalkulator(final String jumlah, final String masa){
            //dipanggil disini
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).prosescalc(jumlah, masa);
                }
            });
        }

    }
}
