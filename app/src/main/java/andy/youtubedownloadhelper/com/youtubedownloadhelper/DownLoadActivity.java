package andy.youtubedownloadhelper.com.youtubedownloadhelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by andy on 2015/3/8.
 */
public class DownLoadActivity extends Activity {

    TextView tv_title;
    ImageView iv_title;
    LinearLayout video_contain;
    Context context;
    Youtube curYoutube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        context = this;
        tv_title = (TextView) findViewById(R.id.textView);
        iv_title = (ImageView) findViewById(R.id.imageView);
        video_contain = (LinearLayout) findViewById(R.id.video_contain);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }


    public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

                new YoutubeloadPaser(this, new YoutubeloadPaser.CallBack(){


                    @Override
                    public void success(Youtube youtube) {
                        curYoutube = youtube;
                        tv_title.setText(curYoutube.getTitle());
                        displayImageUrl(iv_title,curYoutube.getThumbnail_url());
                        showDownloadList(curYoutube.getVideoList());
                    }

                    @Override
                    public void onfail(String Message) {
                        Toast.makeText(DownLoadActivity.this,Message,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).execute(sharedText);


        }
    }

    private void  displayImageUrl(final ImageView iv, final String imageUrl)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return ;
                }
            }
        }).start();

    }
    private void showDownloadList(List<Video> list){
        video_contain.removeAllViews();
        if(list==null||list.size()==0)
            return;
        for(int i=0;i<list.size();i++){
            video_contain.addView(getView(list.get(i)).convertView);
        }
    }
    public class ViewHolder{
        Button bt;
        ProgressBar progressBar;
        View convertView;
        Video video;
    }
    public ViewHolder getView(final Video video) {

        ViewHolder vh = new ViewHolder();

        vh.convertView = LayoutInflater.from(DownLoadActivity.this).inflate(R.layout.downloaditem,null);
        vh.bt = (Button) vh.convertView.findViewById(R.id.button);
        vh.progressBar = (ProgressBar)vh.convertView.findViewById(R.id.progressBar2);
        vh.video = video;
        vh.bt.setText(video.getType());
        vh.bt.setTag(vh);
        vh.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewHolder vh = (ViewHolder) v.getTag();
                final DownloadDialog downloadDialog =  new DownloadDialog(context, curYoutube.getTitle()+"."+vh.video.getVideoType());
                downloadDialog.setPositiveButton(R.string.alert_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DownloadTask(DownLoadActivity.this,vh).execute(vh.video.getUrl(),downloadDialog.getCurrentFilePath(),downloadDialog.getCurrentFileName());
                    }
                })
                        .setNegativeButton(R.string.alert_cancel,null).create().show();

            }
        });
        return vh;
    }


}
