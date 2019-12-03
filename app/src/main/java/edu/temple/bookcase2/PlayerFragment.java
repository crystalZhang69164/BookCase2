package edu.temple.bookcase2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.temple.audiobookplayer.AudiobookService;


public class PlayerFragment extends Fragment {

    Context parent;
    String title;
    int progress;
    SeekBar seekBar;
    TextView textView;
    Button pausePlayButton;
    boolean paused;
    boolean stopped;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerFragmentInterface)
            parent = context;
        else
            throw new RuntimeException("Please implement PlayerFragment's interface");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        //reference to the buttons and other controls
        seekBar = view.findViewById(R.id.seekBar);
        textView = view.findViewById(R.id.playerTitleTextView);
        pausePlayButton = view.findViewById(R.id.pauseplay);
        title = "";
        progress = 0;
        //sets the seek bar progress to 0
        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress change
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                paused = true;
                ((PlayerFragmentInterface) parent).playPauseClicked();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((PlayerFragmentInterface) parent).userMovedSeekBar(seekBar.getProgress());
                ((PlayerFragmentInterface) parent).playPauseClicked();
                paused = false;
            }
        });

        (view.findViewById(R.id.stop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayerFragmentInterface) parent).stopClicked();
                stopped = true;
            }
        });

        //the pause play button onclick event
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayerFragmentInterface) parent).playPauseClicked();
                if (!paused){
                    pausePlayButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    paused = true;
                }
                else if (paused){
                    pausePlayButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    paused = false;
                }
            }
        });
        return view;
    }


    final Handler progressHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            //gets the progress for the seek bar
            AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) msg.obj;
            if (!paused)
                progress = bookProgress.getProgress();
            seekBar.setProgress(progress);
            return true;
        }
    });

    public void updatePlayer (String title){
        this.title = title;
        //sets the text of the text view to the currently playing book title
        textView.setText("Now Playing: " + title);
        paused = false;
        stopped = false;
    }

    interface PlayerFragmentInterface{
        void userMovedSeekBar(int progress);
        void playPauseClicked();
        void stopClicked();
    }
}

