package youtubedownloadhelper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.list.YoutubeListFragment;
import youtubedownloadhelper.media.MediaPlayerFragment;
import youtubedownloadhelper.media.PlayService;
import youtubedownloadhelper.utils.AndroidUtils;
import youtubedownloadhelper.utils.NotifyManager;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy");
        MainActivity.this.stopService(new Intent(MainActivity.this, PlayService.class));
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.toExit)
                    .setNegativeButton(R.string
                            .Alert_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.stopService(new Intent(MainActivity.this, PlayService.class));
                            NotifyManager.getInstance().cancelMediaNotification(MainActivity.this);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.cancel,null)
                    .create().show();
        } else {
            getFragmentManager().popBackStack();
        }
    }

}
