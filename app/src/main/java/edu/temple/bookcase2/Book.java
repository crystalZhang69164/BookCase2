package edu.temple.bookcase2;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Book implements Parcelable {

    private int id;
    private String title;
    private String author;
    private int duration;
    private int published;
    private String coverURL;

    public Book(int id, String title, String author, int duration, int published, String coverURL) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.published = published;
        this.coverURL = coverURL;
    }


    public Book(JSONObject bookObject) throws JSONException {
        this(bookObject.getInt("book_id"), bookObject.getString("title")
                , bookObject.getString("author"), bookObject.getInt("duration")
                , bookObject.getInt("published"), bookObject.getString("cover_url"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", getId());
            jsonObject.put("title", getTitle());
            jsonObject.put("author", getAuthor());
            jsonObject.put("duration", getDuration());
            jsonObject.put("published", getPublished());
            jsonObject.put("coverURL", getCoverURL());
            if (jsonObject.has("coverURL")) {
                Log.d("BOOK", ""+ jsonObject.getString("coverURL"));
            }
            return jsonObject;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.duration);
        dest.writeInt(this.published);
        dest.writeString(this.coverURL);
    }

    protected Book(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.author = in.readString();
        this.duration = in.readInt();
        this.published = in.readInt();
        this.coverURL = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
