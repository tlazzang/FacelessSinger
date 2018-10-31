package com.facelesssinger.shim.homesinger.Fragment;


import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Listener.OnRecordFileAddedListener;
import com.facelesssinger.shim.homesinger.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    private FloatingActionButton btn_record;
    private Chronometer mChronometer;
    private boolean isRecording;

    private String outputFile;
    private MediaRecorder myAudioRecorder;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        btn_record = (FloatingActionButton)view.findViewById(R.id.btnRecord);
        mChronometer = (Chronometer)view.findViewById(R.id.chronometer);
        isRecording = false;

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording){
                    startRecord();
                }
                else {
                    stopRecord();
                }
            }
        });
        return view;
    }

    public void startRecord(){
        isRecording = true;
        btn_record.setImageResource(R.drawable.stop_white);

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FacelessSinger/");
        if(!dir.exists()){ //FacelessSinger폴더 존재 안하면 생성
            dir.mkdir();
        }
        int numOfFile = dir.listFiles().length + 1;
        String fileName = "[얼가]" + numOfFile + "번째 녹음파일";

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FacelessSinger/"+ fileName +".mp3";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            Toast.makeText(getActivity(), "녹음 시작", Toast.LENGTH_SHORT).show();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord(){
        isRecording = false;
        btn_record.setImageResource(R.drawable.mic_white);

        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        myAudioRecorder.stop();

        myAudioRecorder.release();
        myAudioRecorder = null;
        Toast.makeText(getActivity(), "녹음 완료", Toast.LENGTH_SHORT).show();
    }
}
