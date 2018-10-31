package com.facelesssinger.shim.homesinger.Fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Adapter.FileListAdapter;
import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.MySongActivity;
import com.facelesssinger.shim.homesinger.PathUtil;
import com.facelesssinger.shim.homesinger.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileListFragment extends Fragment{

    private static final int PICK_FILE_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private ArrayList<AudioFile> fileList;
    private FileListAdapter fileListAdapter;
    private Button btn_mySong;
    private Button btn_exploreFile;

    public FileListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fileList_recyclerView);
        btn_mySong = (Button) view.findViewById(R.id.fileList_btn_mySong);
        btn_exploreFile = (Button) view.findViewById(R.id.fileList_btn_exploreFile);
        fileList = new ArrayList<>();
        getAllFileInDevice();
        fileListAdapter = new FileListAdapter(fileList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileListAdapter);

        btn_mySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MySongActivity.class);
                startActivity(intent);
            }
        });

        btn_exploreFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); //set mime type as per requirement
                startActivityForResult(intent,PICK_FILE_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK){
            if(data.getData() != null) {
                Uri uri = data.getData();

                String filePath = null;
                try {
                    filePath = PathUtil.getPath(getActivity(), uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                if(filePath != null) {
                    File file = new File(filePath);

                    AudioFile audio = new AudioFile();
                    audio.setPath(file.getPath());
                    audio.setName(file.getName());
                    audio.setDateTime(getFileCreateDate(file));
                    audio.setDurationMills(getFileDuration(file));

                    PlayFragment playFragment = new PlayFragment().newInstance(audio);
                    FragmentTransaction transaction = ((FragmentActivity) getActivity())
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playFragment.show(transaction, "dialog_playback");
                }
                else{
                    Toast.makeText(getActivity(), "오디오 파일을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //PageAdapter가 전,후의 프래그먼트까지 보유하고있기때문에 녹음하기에서 녹음목록으로 갔을시에 업데이트가 안되는 문제위한 함수
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getAllFileInDevice();
            fileListAdapter.updateData(fileList);
        }
    }

    public void getAllFileInDevice(){
        fileList.clear();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FacelessSinger/");
        if(dir.exists()){
            for(File file : dir.listFiles()){
                if(file.getName().contains(".mp3")){
                    long durationMills = getFileDuration(file);
                    long dateTime = getFileCreateDate(file);
                    AudioFile audioFile = new AudioFile(file.getPath(), file.getName(), durationMills, dateTime);
                    fileList.add(audioFile);
                }
            }
        }

    }

    public long getFileDuration(File file){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(), Uri.fromFile(file));
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();
        long durationMills = Long.parseLong(duration);

        return durationMills;
    }

    public long getFileCreateDate(File file){
        long createDate = file.lastModified();

        return createDate;
    }

}
