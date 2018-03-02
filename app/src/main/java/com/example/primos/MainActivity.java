package com.example.primos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private EditText inputField, resultField;
    private Button primecheckbutton;
    private MyAsyncTask myAsyncTask;
//    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = (EditText) findViewById(R.id.inputField);
        resultField = (EditText) findViewById(R.id.resultField);
        primecheckbutton = (Button) findViewById(R.id.primecheckbutton);


    }

    public void triggerPrimecheck(View v){
        if (myAsyncTask == null || myAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
//            isRunning = true;
            Log.d(TAG, "Tread " + Thread.currentThread().getId() + ": Comienza triggerPrimecheck");
            long parameter = Long.parseLong(inputField.getText().toString());
            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute(parameter);
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck termina");
        } else if (myAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
//            isRunning = false;
            Log.d(TAG, "Cancelando test" + Thread.currentThread().getId());
            myAsyncTask.cancel(true);
        }
        Log.d(TAG, "Estado de triggerPrimecheckFinal " + myAsyncTask.getStatus().toString());

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
//            isRunning = false;
            Log.d(TAG, "Cancelando test" + Thread.currentThread().getId());
            myAsyncTask.cancel(true);
        }
        Log.d(TAG, "Estado de onPause() " + myAsyncTask.getStatus().toString());
    }

    private class MyAsyncTask extends AsyncTask<Long, Double, Boolean>{

        @Override
        protected void onPreExecute(){
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": onPreExecute()");
            resultField.setText("");
            primecheckbutton.setText("CANCELAR");
            Log.d(TAG, "Estado de onPreExecute() " + myAsyncTask.getStatus().toString());

        }

        @Override
        protected Boolean doInBackground(Long... n){
            if(isCancelled()){
                return null;
            }


            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": Comienza doInBackground");
            long numComprobar = n[0];
            if(numComprobar < 2 || numComprobar % 2 == 0)
                return false;
            double limite = Math.sqrt(numComprobar) + 0.0001;
            double progreso = 0;
            for(long factor = 3; factor < limite && !isCancelled(); factor += 2){
                if(numComprobar % factor == 0){
                    return false;
                }
                if (factor > limite * progreso / 100){
                    publishProgress(progreso / 100);
                    progreso += 5;
                }
            }
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": Finaliza doInBackground");
            Log.d(TAG, "Estado de doInBackground " + myAsyncTask.getStatus().toString());
            return true;
        }

        @Override
        protected void onProgressUpdate(Double... progress){
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": onProgressUpdate()");
            resultField.setText(String.format("%.1f%% completed", progress[0]*100));
            Log.d(TAG, "Estado de onProgressUpdate() " + myAsyncTask.getStatus().toString());

        }

        @Override
        protected void onPostExecute(Boolean isPrime){
            if(!isCancelled()){
                Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": onPostExecute()");
                resultField.setText(isPrime + "");
                primecheckbutton.setText("¿ES PRIMO?");
            } else{
                resultField.setText("Proceso cancelado");
                primecheckbutton.setText("¿ES PRIMO?");
            }
            Log.d(TAG, "Estado de onPostExecute() " + myAsyncTask.getStatus().toString());

        }

        @Override
        protected  void onCancelled(){
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + ": onCancelled");
            super.onCancelled();
            resultField.setText("Proceso cancelado");
            primecheckbutton.setText("¿ES PRIMO?");
            Log.d(TAG, "Estado de onCancelled() " + myAsyncTask.getStatus().toString());
        }




    }



}
