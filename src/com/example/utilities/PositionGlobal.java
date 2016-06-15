package com.example.utilities;

public class PositionGlobal {

	
	private static PositionGlobal mPositionGlobal;
	public int position = -1;
	public int FLAG = 0;
	
	private PositionGlobal(){
		
	}
	
	public static PositionGlobal getInstance(){
		
		if(mPositionGlobal == null){
			mPositionGlobal = new PositionGlobal();
		}
		return mPositionGlobal;
	}
	





}
