package com.example.models;



import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class MUsicModels implements Parcelable {
	
	public long SongId;
	public String SongName;
	public String Image;
	public long SongLength;
	public String Artist;
	public String AlbumID;
	public String Uri;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public String toString(){
		
		StringBuffer lBuffer = new StringBuffer();
		lBuffer.append("===========Music==========");
		lBuffer.append("\n SongId::::"+SongId);
		lBuffer.append("\n SongName::::"+SongName);
		lBuffer.append("\n SongLength::::"+SongLength);
		lBuffer.append("\n Artist::::"+Artist);
		lBuffer.append("\n AlbumID::::"+AlbumID);
		lBuffer.append("\n Uri::::"+Uri);
		return lBuffer.toString();
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(SongId);
		dest.writeString(SongName);
		dest.writeString(Image);
		dest.writeLong(SongLength);
		dest.writeString(Artist);
		dest.writeString(AlbumID);
		dest.writeString(Uri);
		
		
	}

	public MUsicModels(){}
	
	private MUsicModels(Parcel in) {
		SongId = in.readLong();
		SongName  = in.readString();
		Image  = in.readString();
		SongLength = in.readLong();
		Artist = in.readString();
		AlbumID = in.readString();
		Uri = in.readString();
		
    }
	
	
	public static final Parcelable.Creator<MUsicModels> CREATOR = new Parcelable.Creator<MUsicModels>() {
        public MUsicModels createFromParcel(Parcel in) {
            return new MUsicModels(in);
        }

        public MUsicModels[] newArray(int size) {
            return new MUsicModels[size];
        }
    };

    public static class OrderByTextAZ implements Comparator<MUsicModels> {

		@Override
		public int compare(MUsicModels o1, MUsicModels o2) {
			String title1 = o1.SongName;
			String title2 = o2.SongName;
	
			int k = title1.toLowerCase().compareTo(title2.toLowerCase());
			return k;
		}
	}
	
}
