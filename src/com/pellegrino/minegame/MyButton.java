//project MineGame
package com.pellegrino.minegame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button{
	
	private boolean hm, im, hbc, Mo, Co;
	private int n;
	
	public MyButton(Context context){
		super(context);
	}
	
	public MyButton(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public MyButton(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	public void setDefaults() {
		this.hm = false;
		this.im = false;
		this.hbc = false;
		this.n = 0;
		this.Mo = false;
		this.Co = true;
		setBackgroundColor(getResources().getColor(android.R.color.black));
	}
	
	public boolean getHasMine(){
		return hm;
	}
	
	public void setHasMine(boolean b){
		hm = b;
	}
		
	public boolean getIsMarked(){
		return im;
	}
	
	public void setIsMarked(boolean b){
		im = b;
	}
	
	public boolean getHasBeenClicked(){
		return hbc;
	}
	
	public void setHasBeenClicked(boolean b){
		hbc = b; 
	}
	
	public boolean getMarkOn(){
		return Mo;
	}
	
	public void setMarkOn(boolean b){
		Mo = b;
	}
	
	public boolean getClickOn(){
		return Co;
	}
	
	public void setClickOn(boolean b){
		Co = b;
	}
	
	public int getN(){
		return n;
	}
	
	public void setN(int i){
		n = i;
	}
}
