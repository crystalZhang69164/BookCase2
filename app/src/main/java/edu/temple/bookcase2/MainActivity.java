package edu.temple.bookcase2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;



import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface,
        BookDetailsFragment.PlayButtonInterface, PlayerFragment.PlayerFragmentInterface
{

    ViewPagerFragment viewPagerFragment;
    BookListFragment bookListFragment;
    BookDetailsFragment bookDetailsFragment;
    String[] titles;
    ArrayList<Book> books;
    int booksArrayLength;
    PlayerFragment playerFragment;
    Fragment fragment;
    JSONArray jsonArray = new JSONArray();
    FrameLayout player;
    Button searchBtn;
    EditText editText;


    ViewPager pager;
    AudiobookService.MediaControlBinder binder;
    ServiceConnection serviceConnection;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = findViewById(R.id.playframe);

         fragment = getSupportFragmentManager().findFragmentById(R.id.playframe);
        if (!(fragment instanceof PlayerFragment)){
            playerFragment = new PlayerFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.playframe, playerFragment).commit();
        }
        else {
            playerFragment = (PlayerFragment) fragment;
            if (!playerFragment.stopped) {

            }
        }
        Intent intent = new Intent(this, AudiobookService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service){
                connected = true;
                binder = (AudiobookService.MediaControlBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0){
                connected = false;
                binder = null;
            }
        };

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        fragment = getSupportFragmentManager().findFragmentById(R.id.frame1);
        editText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread bookSearch = new Thread(){
                    @Override
                    public void run(){
                        bookSearchHandler.sendMessage(search(editText.getText().toString()));
                        //starts the thread
                    }
                };bookSearch.start();
            }
        });

        initialBookSearch();
    }



    Handler bookHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame1);
            JSONArray responseArray;
            MainActivity.this.books = books = new ArrayList<>();

            if (fragment instanceof ViewPagerFragment) {
                //gets books as an array list
                MainActivity.this.books = books = ((ViewPagerFragment) fragment).getBooksAsArrayList();
            }
            else if (fragment instanceof BookListFragment){
                MainActivity.this.books = books = ((BookListFragment) fragment).getBooksAsArrayList();
            }
            else {
                responseArray = (JSONArray) msg.obj;

                //sets books array length to the length of the response (json) array
                booksArrayLength = responseArray.length();
                //sets the titles array size to the length of the books array
                titles = new String[booksArrayLength];
                try {
                    for (int i = 0; i < booksArrayLength; i++) {
                        if (responseArray.getJSONObject(i).has("coverURL")) {

                        }
                        books.add(new Book(responseArray.getJSONObject(i)));

                        titles[i] = responseArray.getJSONObject(i).getString("title");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MainActivity.this.viewPagerFragment = ViewPagerFragment.newInstance(books);
            MainActivity.this.bookListFragment = BookListFragment.newInstance(books);
            MainActivity.this.bookDetailsFragment = BookDetailsFragment.newInstance(new Book(0,"","",0,0,""));


            //the view pager for portrait and the lists of book in landscape mode
            if (findViewById(R.id.frame2) == null) {
                if (fragment instanceof BookListFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame1, MainActivity.this.viewPagerFragment).commit();
            } else {
                if (fragment instanceof ViewPagerFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                fragment = getSupportFragmentManager().findFragmentById(R.id.frame2);
                if (fragment != null){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame1, MainActivity.this.bookListFragment).commit();
                getSupportFragmentManager().beginTransaction().add(R.id.frame2, MainActivity.this.bookDetailsFragment).commit();
            }
            return false;
        }
    });
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (!binder.isPlaying())
            unbindService(serviceConnection);
    }

    private void initialBookSearch()
    {
        Thread t = new Thread(){
            @Override
            public void run(){

                bookHandler.sendMessage(search(""));
            }
        };
        t.start();
    }

    Handler bookSearchHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            //json array
            JSONArray responseArray;
            responseArray = (JSONArray) msg.obj;
            booksArrayLength = responseArray.length();
            //sets the titles array to the size of book array length
            titles = new String[booksArrayLength];
            //clears the books
            books.clear();
            try {
                //traverse the array
                for (int i = 0; i < booksArrayLength; i++) {
                    if (responseArray.getJSONObject(i).has("coverURL")) {
                    }
                    books.add(new Book(responseArray.getJSONObject(i)));
                    titles[i] = responseArray.getJSONObject(i).getString("title");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame1);

            if (fragment instanceof BookListFragment)
                ((BookListFragment)fragment).updateBooks(books);
            if (fragment instanceof ViewPagerFragment)
                ((ViewPagerFragment)fragment).updateBooks(books);
            return false;
        }
    });
    @Override
    public void bookSelected(Book book) {
        MainActivity.this.bookDetailsFragment.changeBook(book);
    }

    private Message search(String searchTerm) {
        URL BookListURL;
        try {
            BookListURL = new URL(getResources().getString(R.string.bookSearchAPI)+ searchTerm);

            BufferedReader reader = new BufferedReader(new InputStreamReader(BookListURL.openStream()));

            String response = "", tempResponse;

            tempResponse = reader.readLine();
            while (tempResponse != null) {
                response = response + tempResponse;
                tempResponse = reader.readLine();
            }

            JSONArray bookArray = new JSONArray(response);
            Message msg = Message.obtain();
            msg.obj = bookArray;

            return msg;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //play btn click method
    @Override
    public void playButtonClicked(Book book) {
        binder.play(book.getId());
        player.setVisibility(View.VISIBLE);
        playerFragment.updatePlayer(book.getTitle());
        binder.setProgressHandler(playerFragment.progressHandler);
    }

    //moving the seek bar method
    @Override
    public void userMovedSeekBar(int progress) {
        if (connected)
            binder.seekTo(progress);
    }

    //pause btn click method
    @Override
    public void playPauseClicked() {

        binder.pause();
    }

    //stop btn click method
    @Override
    public void stopClicked() {
        binder.stop();
        //sets the player visibility to invisible
        player.setVisibility(View.GONE);
    }


}
