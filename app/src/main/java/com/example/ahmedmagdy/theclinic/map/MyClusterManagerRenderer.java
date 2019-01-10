package com.example.ahmedmagdy.theclinic.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ahmedmagdy.theclinic.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.squareup.picasso.Picasso.Priority.HIGH;


public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private static final String TAG = "MyClusterManagerRenderer";
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private Context context;
    private ClusterManager mClusterManager;

    public MyClusterManagerRenderer(Context context, GoogleMap map,
                                    ClusterManager<ClusterMarker> clusterManager) {

        super(context, map, clusterManager);
        this.context = context;
        this.mClusterManager = clusterManager;

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.image_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarker item, final MarkerOptions markerOptions) {
        if (item.getIconPicUrl() != null) {
            final String pUrl = item.getIconPicUrl();
            Uri selectedImageUri = Uri.parse(item.getIconPicUrl());
            //System.out.println(TAG+"selectedImageUri.getPath()"+selectedImageUri.getPath() );
            //imageView.setImageURI(selectedImageUri);
            // imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //Glide.with(context).load(pUrl).into(imageView);

            /* Picasso.get().load(pUrl).placeholder(R.drawable.cartman_cop).priority(HIGH).into(imageView);*/

            /*for( Marker m : mClusterManager.getMarkerCollection().getMarkers()){
                    Picasso.get().load(pUrl).placeholder(R.drawable.cartman_cop)
                            .centerCrop().fit().priority(HIGH).into(
                    imageView, new MarkerCallback(m,pUrl,imageView));
            }*/

            /*Picasso.get()
                    .load(pUrl).fit()
                    .placeholder(R.drawable.cartman_cop).priority(HIGH)
                    .into(imageView);*/

            /*
            final ImageView img = new ImageView(context);
            Picasso.get()
                    .load(pUrl).placeholder(R.drawable.cartman_cop).priority(HIGH)
                    .into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setBackgroundDrawable(img.getDrawable());
                        }

                        @Override
                        public void onError(Exception e) {

                        }

                    });*/

            Picasso.get().load(pUrl).fit().centerCrop()
                    .placeholder(R.drawable.cartman_cop).priority(HIGH)
                    .into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "onSuccess");
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                }

            });




        } else {
            imageView.setImageResource(item.getIconPic());
        }


        Bitmap icon = iconGenerator.makeIcon();

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle()).snippet(item.getSnippet());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }


    ///////////////

   /* @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        imageView.setImageResource(item.getIconPic());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }
*/
    ///////////////




}
