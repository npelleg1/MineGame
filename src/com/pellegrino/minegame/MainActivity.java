package com.pellegrino.minegame;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	
	public int click_count = 0, mine_count = 0, placed_mines = 0, mines, neigh_count = 0;
	public int BlockDimensionX = 0, BlockDimensionY = 0;
	public MyButton minefield[][];
	private TableLayout mineField;
	private Button Check, Mark, MinesButton;
	private int BlockPadding = 1;
	private boolean hasLost, hasWon, hasStarted = false, instructionsDisplayed = false;
	private int marked_spaces = 0;
	public long timeWhenStopped = 0, elapsed;
	Chronometer clock;
	TextView TS;
	SharedPreferences sharedPref;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mineField = (TableLayout)findViewById(R.id.MineField);
        clock = (Chronometer)findViewById(R.id.chronometer);
        clock.setGravity(Gravity.CENTER);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
 //       Editor editor = sharedPref.edit();
 //       editor.putLong("easy", 0);
 //       editor.putLong("medium", 0);
 //       editor.putLong("hard", 0);
 //       editor.commit();
        TS = (TextView)findViewById(R.id.topScore);
        TS.setGravity(Gravity.CENTER);
        TS.setBackgroundColor(getResources().getColor(R.color.LightGreen));
        TS.setText("" + elapsedTimeToText(sharedPref.getLong("easy", 0)));
		Check = (Button)findViewById(R.id.check_button);
		Check.setBackgroundColor(Color.BLUE);
		Check.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				for (int i = 0; i < 8; i++){
					for(int j = 0; j < 5; j++){
						minefield[i][j].setClickOn(true);
						minefield[i][j].setMarkOn(false);
					}
				}
				Check.setBackgroundColor(Color.BLUE);
				Mark.setBackgroundColor(Color.GRAY);
			}
		});
		Mark = (Button)findViewById(R.id.mark_button);
		Mark.setBackgroundColor(Color.GRAY);
		Mark.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 5; j++){
						minefield[i][j].setClickOn(false);
						minefield[i][j].setMarkOn(true);
					}
				}
				Mark.setBackgroundColor(Color.BLUE);
				Check.setBackgroundColor(Color.GRAY);
			}
		});
		MinesButton = (Button)findViewById(R.id.Mines);
		MinesButton.setBackgroundColor(Color.GRAY);
		mines = 10;
		MinesButton.setText("10");
		MinesButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				if (click_count == 0 && MinesButton.getText().equals("10")){
					mines = 15;
					MinesButton.setText("15");
					TS.setTextColor(getResources().getColor(R.color.Black));
					TS.setBackgroundColor(getResources().getColor(R.color.Yellow));
					TS.setText(""+ elapsedTimeToText(sharedPref.getLong("medium", 0)));
				}
				else if (click_count == 0 && MinesButton.getText().equals("15")){
					mines = 20;
					MinesButton.setText("20");	
					TS.setTextColor(getResources().getColor(R.color.Black));
					TS.setText("" + elapsedTimeToText(sharedPref.getLong("hard", 0)));
					TS.setBackgroundColor(getResources().getColor(R.color.Crimson));
				}
				else if (click_count == 0 && MinesButton.getText().equals("20")){
					mines = 10;
					MinesButton.setText("10");
					TS.setTextColor(getResources().getColor(R.color.Black));
				    TS.setBackgroundColor(getResources().getColor(R.color.LightGreen));
				    TS.setText("" + elapsedTimeToText(sharedPref.getLong("easy", 0)));
				}
				else{
					mines = 10;
					MinesButton.setText("10");
					TS.setTextColor(getResources().getColor(R.color.Black));
				    TS.setBackgroundColor(getResources().getColor(R.color.LightGreen));
				    TS.setText("" + elapsedTimeToText(sharedPref.getLong("easy", 0)));
				}
				mineField.removeAllViews();
				click_count = 0; 
				mine_count = 0; 
				placed_mines = 0;  
				neigh_count = 0;
				marked_spaces = 0;
				hasStarted = false;
				timeWhenStopped = clock.getBase() - SystemClock.elapsedRealtime();
				clock.stop();
				clock.setBase(SystemClock.elapsedRealtime());
				timeWhenStopped = 0;
				Mark.setBackgroundColor(Color.GRAY);
				Check.setBackgroundColor(Color.BLUE);
				clock.setBackgroundColor(getResources().getColor(R.color.White));
				createMinefield();
				onWindowFocusChanged(false);
			}
		});
		createMinefield();
	}
	
	public void createMinefield(){
		minefield = new MyButton[8][5];
		hasLost = false;
		hasWon = false;
		int i, j;
		for (i = 0; i < 8; i++){
			for (j = 0; j < 5; j++){
				minefield[i][j] = new MyButton(this);
				minefield[i][j].setDefaults();
				final int currentRow = i;
				final int currentColumn = j;
				minefield[i][j].setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if(click_count == 0){
							Random rand = new Random();
							int rand1, rand2;
							while (placed_mines < mines){
								rand1 = rand.nextInt(8);
								rand2 = rand.nextInt(5);
								if (minefield[rand1][rand2].getHasMine() != true && !(currentRow == rand1 && currentColumn == rand2)){
									minefield[rand1][rand2].setHasMine(true);
									placed_mines++;
								}
							}
							for (int y = 0; y < 8; y++){
								for(int z = 0; z < 5; z++){
									neigh_count = 0;
									if(y-1 < 0 || y+1 > 7 || z-1 < 0 || z+1 > 4){
										if(y == 0 && z == 0){
											if(minefield[y+1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z+1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (y == 0 && z == 4){
											if(minefield[y+1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z-1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (y == 7 && z == 0){
											if(minefield[y][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z].getHasMine() == true)
												neigh_count += 1;
										}
										else if (y == 7 && z == 4){
											if(minefield[y][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z-1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (y == 0){
											if(minefield[y][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z+1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (z == 4){
											if(minefield[y-1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z-1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (y == 7) {
											if(minefield[y][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z-1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z+1].getHasMine() == true)
												neigh_count += 1;
										}
										else if (z == 0){
											if(minefield[y-1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y+1][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y][z+1].getHasMine() == true)
												neigh_count += 1;
											if(minefield[y-1][z+1].getHasMine() == true)
												neigh_count += 1;
										}
									}
									else{
										if (minefield[y-1][z-1].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y][z-1].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y+1][z-1].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y-1][z].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y+1][z].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y-1][z+1].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y][z+1].getHasMine() == true)
											neigh_count += 1;
										if (minefield[y+1][z+1].getHasMine() == true)
											neigh_count += 1;
									}
									if(minefield[y][z].getHasMine() != true)
										minefield[y][z].setN(neigh_count);
									else
										minefield[y][z].setN(10);
								}
							} 
							clock.setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
		                    clock.start();
						}
						if(hasLost == false && hasWon == false && minefield[currentRow][currentColumn].getHasBeenClicked() == false){
							LogicFunc(minefield[currentRow][currentColumn]);
							mine_count = 0;
							click_count = 0;
							for(int i = 0; i < 8; i++){
								for(int j = 0; j < 5; j++){
									if(minefield[i][j].getHasBeenClicked() == true){
										click_count++;
										if (minefield[i][j].getN() == 0){
											revealZeroes(i, j);
										}
									}
									if(minefield[i][j].getHasMine() == true && minefield[i][j].getHasBeenClicked() == true){
										for (int k = 0; k < 8; k++){
											for (int l = 0; l < 5; l++){
												if(minefield[k][l].getHasMine() == true){
													changeColorTo(Color.RED, minefield[k][l]);
												}
											}
										}
										timeWhenStopped = clock.getBase() - SystemClock.elapsedRealtime();
										clock.stop();
										TS.setBackgroundColor(getResources().getColor(R.color.Black));
										TS.setTextColor(getResources().getColor(R.color.Ivory));
										TS.setText("You Died After " + clock.getText() + " of Play");
									}
									else if(minefield[i][j].getHasMine() == true && minefield[i][j].getIsMarked() == true){
										mine_count++;
									}
									if(mine_count == mines && click_count == 40-mines){
										hasWon = true;
										for(int k = 0; k < 8; k++){
											for(int m = 0; m < 5; m++){
												if(minefield[k][m].getHasMine() == true && minefield[k][m].getIsMarked() == true){
													changeColorTo(Color.GREEN, minefield[k][m]);
												}
											}
										}
										timeWhenStopped = clock.getBase() - SystemClock.elapsedRealtime();
										clock.stop();
										boolean isBestScore = false;
										elapsed = calculateElapsedTime(clock);
										sharedPref = getPreferences(Context.MODE_PRIVATE);
										if((sharedPref.getLong("easy", 0) == 0 || sharedPref.getLong("easy", 0) > elapsed) && mines == 10){
					                    	bestScorePopup("easy");
					                    	isBestScore = true;
					                    }
										else if((sharedPref.getLong("medium", 0) == 0 || sharedPref.getLong("medium", 0) > elapsed) && mines == 15){
					                    	bestScorePopup("medium");
					                    	isBestScore = true;
					                    }
										else if((sharedPref.getLong("hard", 0) == 0 || sharedPref.getLong("hard", 0) > elapsed) && mines == 20){
					                    	bestScorePopup("hard");
					                    	isBestScore = true;
					                    }
										TS.setGravity(Gravity.CENTER);
										if(isBestScore){
											TS.setText("You won after " + clock.getText() + "! New Best!");
										}
										else{
											TS.setText("You won after " + clock.getText() + "!");
										}
										clock.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
										TS.setTextColor(getResources().getColor(R.color.Black));
									}
								}
							}
							MinesButton.setText(Integer.toString(mines-marked_spaces));
						}
					}
				});
			}
		}
	}
	public void showMinefield(int BlockDimensionX, int BlockDimensionY){
		mineField.removeAllViews();
		for (int row = 0; row < 8; row++)
		{
			TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(new LayoutParams((BlockDimensionX + 4 * BlockPadding) * 5, BlockDimensionY + 4 * BlockPadding));
			for (int column = 0; column < 5; column++)
			{
				minefield[row][column].setLayoutParams(new LayoutParams(BlockDimensionX + 4 * BlockPadding,  BlockDimensionY + 4 * BlockPadding)); 
				minefield[row][column].setPadding(BlockPadding, BlockPadding, BlockPadding, BlockPadding);
				changeColorTo(Color.BLACK, minefield[row][column]);
				tableRow.addView(minefield[row][column]);
			}
			mineField.addView(tableRow, new TableLayout.LayoutParams((BlockDimensionX + 4 * BlockPadding) * 5, BlockDimensionY + 4 * BlockPadding));  
		}
	}
	
	public void revealZeroes(int i, int j){
		if (i - 1 >= 0 && j - 1 >= 0) {
			if(minefield[i-1][j-1].getHasBeenClicked() == false){
				minefield[i-1][j-1].setHasBeenClicked(true);
				click_count++;
				changeColorTo(Color.GRAY, minefield[i-1][j-1]);
				if(minefield[i-1][j-1].getN() != 0 && minefield[i-1][j-1].getN() != 10){
					textColor(minefield[i-1][j-1].getN(), minefield[i-1][j-1]);
					minefield[i-1][j-1].setText(Integer.toString(minefield[i-1][j-1].getN()));
				}
				if(minefield[i-1][j-1].getN() == 0){
					revealZeroes(i-1, j-1);
				}
			}
		}
		if (i - 1 >= 0 && j + 1 <= 4){
			if(minefield[i-1][j+1].getHasBeenClicked() == false){
				minefield[i-1][j+1].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i-1][j+1]);
				if(minefield[i-1][j+1].getN() != 0 && minefield[i-1][j+1].getN() != 10){
					textColor(minefield[i-1][j+1].getN(), minefield[i-1][j+1]);
					minefield[i-1][j+1].setText(Integer.toString(minefield[i-1][j+1].getN()));
				}
				click_count++;
				if(minefield[i-1][j+1].getN() == 0){
					revealZeroes(i-1, j+1);
				}
			}
		}
		if (i - 1 >= 0){
			if(minefield[i-1][j].getHasBeenClicked() == false){
				minefield[i-1][j].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i-1][j]);
				if(minefield[i-1][j].getN()!= 0 && minefield[i-1][j].getN() != 10){
					textColor(minefield[i-1][j].getN(), minefield[i-1][j]);
					minefield[i-1][j].setText(Integer.toString(minefield[i-1][j].getN()));
				}
				click_count++;
				if(minefield[i-1][j].getN() == 0){
					revealZeroes(i-1, j);
				}
			}
		}
		if (i + 1 <= 7){
			if (minefield[i+1][j].getHasBeenClicked() == false ){
				minefield[i+1][j].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i+1][j]);
				if(minefield[i+1][j].getN() != 0 && minefield[i+1][j].getN() != 10){
					textColor(minefield[i+1][j].getN(), minefield[i+1][j]);
					minefield[i+1][j].setText(Integer.toString(minefield[i+1][j].getN()));
				}					
				click_count++;
				if(minefield[i+1][j].getN() == 0){
					revealZeroes(i+1, j);
				}
			}
		}
		if (i + 1 <= 7 && j - 1 >= 0){
			if (minefield[i+1][j-1].getHasBeenClicked() == false){
				minefield[i+1][j-1].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i+1][j-1]);
				if(minefield[i+1][j-1].getN() != 0 && minefield[i+1][j-1].getN() != 10){
					textColor(minefield[i+1][j-1].getN(), minefield[i+1][j-1]);
					minefield[i+1][j-1].setText(Integer.toString(minefield[i+1][j-1].getN()));
				}
				click_count++;
				if(minefield[i+1][j-1].getN() == 0){
					revealZeroes(i+1, j-1);
				}
			}
		}
		if (i + 1 <= 7 && j + 1 <= 4){
			if (minefield[i+1][j+1].getHasBeenClicked() == false){ 
				minefield[i+1][j+1].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i+1][j+1]);
				if(minefield[i+1][j+1].getN() != 0 && minefield[i+1][j+1].getN() != 10){
					textColor(minefield[i+1][j+1].getN(), minefield[i+1][j+1]);
					minefield[i+1][j+1].setText(Integer.toString(minefield[i+1][j+1].getN()));
				}
				click_count++;
				if(minefield[i+1][j+1].getN() == 0){
					revealZeroes(i+1, j+1);
				}
			}
		}
		if (j + 1 <= 4){
			if (minefield[i][j+1].getHasBeenClicked() == false){
				minefield[i][j+1].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i][j+1]);
				if(minefield[i][j+1].getN() != 0 && minefield[i][j+1].getN() != 10){
					textColor(minefield[i][j+1].getN(), minefield[i][j+1]);
					minefield[i][j+1].setText(Integer.toString(minefield[i][j+1].getN()));
				}
				click_count++;
				if(minefield[i][j+1].getN() == 0){
					revealZeroes(i, j+1);
				}
			}
		}
		if (j-1 >= 0){
			if (minefield[i][j-1].getHasBeenClicked() == false){
				minefield[i][j-1].setHasBeenClicked(true);
				changeColorTo(Color.GRAY, minefield[i][j-1]);
				if(minefield[i][j-1].getN() != 0 && minefield[i][j-1].getN() != 10){
					textColor(minefield[i][j-1].getN(), minefield[i][j-1]);
					minefield[i][j-1].setText(Integer.toString(minefield[i][j-1].getN()));
				}
				click_count++;
				if(minefield[i][j-1].getN() == 0){
					revealZeroes(i, j-1);
				}
			}
		}
	}
	
	public void LogicFunc(MyButton s){
		if(s.getClickOn() == true && s.getIsMarked() == false){
			s.setHasBeenClicked(true);
			if (s.getHasMine() == true){
				changeColorTo(Color.RED, s);
				hasLost = true;
			}
			else if(s.getN() == 0){
				changeColorTo(Color.GRAY, s);
			}
			else{
				changeColorTo(Color.GRAY, s);
				textColor(s.getN(), s);
				s.setText(Integer.toString(s.getN()));
			}
		}
		else if (s.getMarkOn() == true){
			if (s.getIsMarked() == false){
				s.setIsMarked(true);
				changeColorTo(Color.YELLOW, s);
				marked_spaces++;
			}	
			else if (s.getIsMarked() == true){
				s.setIsMarked(false);
				changeColorTo(Color.BLACK, s);
				marked_spaces--;
			}
		}
	}
	public void changeColorTo(int color, MyButton s){
		GradientDrawable drawable = new GradientDrawable();
		drawable.setStroke(1, Color.WHITE);
		drawable.setColor(color);
		s.setBackgroundDrawable(drawable);
	}
	
	public void textColor(int n, MyButton b){
		switch (n){
		case 1:
			b.setTextColor(getResources().getColor(R.color.Navy));
			break;
		case 2:
			b.setTextColor(getResources().getColor(R.color.GreenYellow));
			break;
		case 3:
			b.setTextColor(getResources().getColor(R.color.FireBrick));
			break;
		case 4:
			b.setTextColor(getResources().getColor(R.color.BlueViolet));
			break;
		case 5:
			b.setTextColor(getResources().getColor(R.color.Gold));
			break;
		case 6:
			b.setTextColor(getResources().getColor(R.color.LightSkyBlue));
			break;
		case 7:
			b.setTextColor(getResources().getColor(R.color.DarkOliveGreen));
			break;
		case 8:
			b.setTextColor(getResources().getColor(R.color.Pink));
			break;
		default:
			break;
		}	
	}
	
	public void bestScorePopup(String difficulty){
		Editor editor = sharedPref.edit();
		elapsed = calculateElapsedTime(clock);
        editor.putLong(difficulty, elapsed);
        editor.commit();
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (BlockDimensionX == 0 && BlockDimensionY == 0){
			BlockDimensionX = mineField.getWidth()/6; 
			BlockDimensionY = mineField.getHeight()/7;
		}
		if(hasStarted == false){
			showMinefield(BlockDimensionX, BlockDimensionY);
			hasStarted = true;
		}
		if(instructionsDisplayed == false && sharedPref.getLong("easy", 0) == 0 && sharedPref.getLong("medium", 0) == 0 && sharedPref.getLong("hard", 0) == 0){
			Check.setClickable(false);
			Mark.setClickable(false);
			MinesButton.setClickable(false);
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 5; j++){
					minefield[i][j].setClickable(false);
				}
			}
			LinearLayout viewGroup = (LinearLayout)findViewById(R.id.popup);
			LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
			final PopupWindow popup = new PopupWindow(this);
			popup.setContentView(layout);
			popup.setWidth(mineField.getWidth());
			popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
			Button close = (Button) layout.findViewById(R.id.close);
			close.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					popup.dismiss();
					for(int i = 0; i < 8; i++){
						for(int j = 0; j < 5; j++){
							minefield[i][j].setClickable(true);
						}
					}
					Check.setClickable(true);
					Mark.setClickable(true);
					MinesButton.setClickable(true);
				}
			});
			instructionsDisplayed = true;
		}
	}
	
	public void onPause(){
		super.onPause();
		timeWhenStopped = clock.getBase() - SystemClock.elapsedRealtime();
		clock.stop();
	}
	
	public void onResume(){
		super.onResume();
		if(click_count != 0 && hasWon == false && hasLost == false){
			clock.setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
			clock.start();
		}
	}
	
	public long calculateElapsedTime(Chronometer mChronometer) {
        long stoppedMilliseconds = 0;
        String chronoText = mChronometer.getText().toString();
        String array[] = chronoText.split(":");
        stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 + Integer.parseInt(array[1]) * 1000;
        return stoppedMilliseconds;
    }
	
	public String elapsedTimeToText(long score){
		String timeString = " ";
		if (score >= 60*1000){
			int minutes = (int)(score/(1000*60));
			int seconds = (int)(score%(1000*60))/1000;
			if (seconds < 10){
				timeString = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
			}
			else{
				timeString = Integer.toString(minutes) + ":" + Integer.toString(seconds);
			}
		}
		else if (score == 0){
			timeString = "Set a New Best!";
		}
		else{
			int seconds = (int)(score/1000);
			if(seconds < 10){
				timeString = "00:0" + Integer.toString(seconds);
			}
			else{
				timeString = "00:" + Integer.toString(seconds);
			}
		}
		return timeString;
	}
}
