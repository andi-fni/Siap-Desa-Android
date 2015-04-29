package com.andifni.qrreaderfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.software.shell.fab.ActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends ActionBarActivity implements MaterialTabListener {

    private ProgressDialog progressDialog;

    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[]={"Scan","Dokumen"};
    private int Numboftabs =2;
    private MaterialTabHost tabHost;
    private ActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
        }
        getSupportActionBar().setElevation(0);
//        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
//        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
//
//        // Assigning ViewPager View and setting the adapter
//        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setAdapter(adapter);
//
//        // Assiging the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.accent);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);

        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles,Numboftabs);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    FileFragment f = (FileFragment)adapter.getItem(1);
                    f.showActionButton();
                } else {
                    FileFragment f = (FileFragment)adapter.getItem(1);
                    f.hideActionButton();
                }
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        tabHost.addTab(
                tabHost.newTab()
                        .setTabListener(this).setText("Scan"));
        tabHost.addTab(
                tabHost.newTab()
                        .setTabListener(this).setText("Dokumen"));
//        actionButton = getFragmentManager().findFragmentById()

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startQRReaderActivity(View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, 1);
    }

    public void startQRReaderActivityFAB(View view) {
        FileFragment f = (FileFragment)adapter.getItem(1);
        f.hideActionButton();
        startQRReaderActivity(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            final String url = data.getStringExtra("URL");
            if (url.equals("NULL"))
            {
                //that means qr could not be identified or user pressed the back button
                //do nothing
            }
            else
            {
                String fileName = data.getStringExtra("FILENAME");
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Dokumen ditemukan! Silahkan unduh atau lihat dokumen " + fileName).setTitle("Berhasil Dipindai!");
                dialog.setPositiveButton("Unduh Dokumen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //unduh
                        if (isOnline()) {
                            new DownloadFileFromURL().execute(url);
                        } else {
                            errorMessage("Perangkat tidak terkoneksi Internet!");
                        }

                    }
                });
                dialog.setNeutralButton("Lihat Saja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //lihat
                        if (isOnline()) {
                            Intent inten = new Intent(MainActivity.this, WebViewPdf.class);
                            inten.putExtra("url", url);
                            startActivity(inten);
                        } else {
                            errorMessage("Perangkat tidak terkoneksi Internet!");
                        }

                    }
                });
                dialog.show();

            }
        }
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition(), true);
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    public void errorMessage(String s) {
        AlertDialog.Builder dialogError = new AlertDialog.Builder(this);
        dialogError.setMessage(s);
        dialogError.setTitle("Kesalahan");
        dialogError.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogError.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private String fileLocation;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Mengunduh File...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
//            OutputStream output = null;
//            InputStream input = null;
            try {
                if (!isExternalStorageWritable()) throw new Exception("Cannot Write");
                URL url = new URL(f_url[0]);

                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                conection.setInstanceFollowRedirects(false);
                //conection.setConnectTimeout(90 * 1000);
                URL secondURL = new URL(conection.getHeaderField("Location"));
                URLConnection connection2 = secondURL.openConnection();
                //connection2.setConnectTimeout(3*60*1000);
                connection2.connect();
                // getting file length
                int lenghtOfFile = connection2.getContentLength();
                String[] path = secondURL.toString().split("/");

                String fileName = path[path.length - 1];

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(secondURL.openStream(), 8192);

                File file = getStorageDir("Siap Desa");

                // Output stream to write file
                OutputStream output = new FileOutputStream(file.getAbsolutePath()+"/"+fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    Log.e("DOWNLOAD :", "" + total);
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                fileLocation = file.getAbsolutePath()+"/"+fileName;

            } catch (Exception e) {
//                Toast.makeText(MainActivity.this,
//                        "No Application Available to View PDF",
//                        Toast.LENGTH_SHORT).show();
//                try {
//                    output.close();
//                    input.close();
//                }
//                catch(Exception ex)
//                {
//
//                }
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

        public File getStorageDir(String name) {
            // Get the directory for the user's public pictures directory.
            File file = new File(Environment.getExternalStorageDirectory() + "/Documents/" + name);
            if (!file.mkdirs()) {
                Log.e("ERROR", "Directory not created");
            }
            return file;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            progressDialog.dismiss();
            progressDialog.setProgress(0);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(fileLocation);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Log.e("LOCATION", file.getName().toString());
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this,
                        "No Application Available to View PDF",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }

    }
}
