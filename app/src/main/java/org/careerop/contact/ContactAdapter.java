package org.careerop.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Juyel on 17/05/09.
 */

public class ContactAdapter extends BaseAdapter{
    private Context context;
    private List<Contact> contactList;
    private LayoutInflater inflater;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_row,null);
        }
        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
        ImageView contactImage = (ImageView)convertView.findViewById(R.id.listImage);
        TextView txtName = (TextView)convertView.findViewById(R.id.txtName);
        TextView txtPhone = (TextView)convertView.findViewById(R.id.txtPhone);

        //Adding prograessbar
        //For load image
        ImageLoader.getInstance().displayImage(contactList.get(position).getImage(), contactImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(view.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(view.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(view.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(view.GONE);
            }
        });

        txtName.setText(contactList.get(position).getName().toString());
        txtPhone.setText(contactList.get(position).getPhone().toString());


        return convertView;
    }
}
