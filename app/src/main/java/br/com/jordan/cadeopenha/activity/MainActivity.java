package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.jordan.cadeopenha.AsyncTaskListenerAutenticarAPI;
import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.task.AutenticarAPITask;


public class MainActivity extends Activity implements AsyncTaskListenerAutenticarAPI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutenticarAPITask task = new AutenticarAPITask(this, this);
        task.execute();
    }


    @Override
    public void onTaskCompleteAutenticarAPI(String result) {
        Toast.makeText(this, "FOI", Toast.LENGTH_LONG).show();
    }
}
