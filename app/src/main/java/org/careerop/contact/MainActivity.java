package org.careerop.contact;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd;
    final Context context = this;
    private ImageView imageView;
    private EditText etName;
    private EditText etPhone;
    private Button btnAddContact;
    private Bitmap bitmap;
    private Button btnChoose;
    private Button btnCancel;
    private ListView listView;
    private List<Contact> contactList;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);
        //compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5' this compile wile must add build.gradle

        // Create default option which wile be used for every
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config); // Do it application start
        //compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5' this compile wile must add build.gradle

        btnAdd = (Button) findViewById(R.id.btnAdd);
        listView = (ListView) findViewById(R.id.listView);


        //add contact to server database
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_contact_dialog);
                dialog.setTitle("Add Contact");

                etName = (EditText) dialog.findViewById(R.id.etName);
                etPhone = (EditText) dialog.findViewById(R.id.etPhone);
                btnChoose = (Button) dialog.findViewById(R.id.btnChoose);
                imageView = (ImageView) dialog.findViewById(R.id.imgImage);
                btnAddContact = (Button) dialog.findViewById(R.id.btnAddContact);
                btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

                btnChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //by calling this method can select image from gallery
                        showFileChooser();
                    }
                });

                btnAddContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //add contact to server
                        addContact();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //cancel the dialog
                        dialog.dismiss();
                        getData();
                    }
                });

                dialog.show();
            }
        });

        //Calling getdata method to show listview data
        getData();

    }

    private void getData() {
        StringRequest stringRequest = new StringRequest(Config.SHOW_ALL_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //for listview datashowing
    private void showJSON(String json) {
        ParseJSON pj = new ParseJSON(json);
        pj.jsonParser();
        contactList = ParseJSON.contactList;
        adapter = new ContactAdapter(getApplicationContext(), ParseJSON.contactList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String contactId = contactList.get(position).getId().toString();
                deleteContact(contactId);
                getData();
                return false;
            }
        });
    }

    private void deleteContact(String contactId) {
        final ProgressDialog loading = ProgressDialog.show(this, "Deleting...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.DELETE_CONTACT_URL + contactId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    //add contact to server
    private void addContact() {
        final ProgressDialog loading = ProgressDialog.show(this, "Adding...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADD_CONTACT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Dismissing the progress dialog
                loading.dismiss();
                //Showing toast message of the response
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Dismissing the progress dialog
                loading.dismiss();
                //Showing toast
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String image = getStringFromBitmap(bitmap);
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //adding parameter
                params.put(Config.NAME, name);
                params.put(Config.PHONE, phone);
                params.put(Config.IMAGE, image);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    //Convert Bitmat to image
    public String getStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //Choose image from gallery
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
