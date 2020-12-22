package gr.uom.api_app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ContextClass implements Parcelable {

    Context context;

    ContextClass(Context context){
        this.context = context;
    }

    protected ContextClass(Parcel in) {
    }

    public static final Creator<ContextClass> CREATOR = new Creator<ContextClass>() {
        @Override
        public ContextClass createFromParcel(Parcel in) {
            return new ContextClass(in);
        }

        @Override
        public ContextClass[] newArray(int size) {
            return new ContextClass[size];
        }
    };

    public Context getContext() {
        return context;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
