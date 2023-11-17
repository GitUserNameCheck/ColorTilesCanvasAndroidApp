package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.util.Random;

public class TilesView extends View {

    class Pair{
        float x, y;
        public Pair(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class MyTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int i = integers[0];
            int j = integers[1];
            int delay = integers[2];
            try {
                changeTileColor(i, j);
                publishProgress();
                Thread.sleep(delay);
                int step = 1;
                for (int k = 0; k < 3; k++) {
                    changeTileColor(i+step,j);
                    changeTileColor(i-step,j);
                    changeTileColor(i, j+step);
                    changeTileColor(i, j-step);
                    publishProgress();
                    Thread.sleep(delay);
                    step += 1;
                }
            } catch (InterruptedException e) { }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            invalidate();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if(darkCounter == 16 || darkCounter == 0){
                Toast toast = Toast.makeText(getContext(), "You've won!",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    int[][] tiles = new int[4][4];
    Pair[][] tilesCoordinates = new Pair[4][4];

    int darkColor = Color.GRAY;
    int brightColor = Color.YELLOW;

    int darkCounter;

    float squareWidth, squareHeight;

    int width, height; // ширина и высота канвы

    public TilesView(Context context) {
        super(context);
    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 1) заполнить массив tiles случайными цветами
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            for(int j =0; j< 4; j++){
                boolean number = rnd.nextBoolean();
                if(number){
                    tiles[i][j] = darkColor;
                    darkCounter += 1;
                }
                else{
                    tiles[i][j] = brightColor;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float margin = 15f;
        width = canvas.getWidth();
        height = canvas.getHeight();
        squareWidth = (width - margin * 5) / 4;
        squareHeight = (height - margin * 5) / 4;
        // 2) отрисовка плиток
        // задать цвет можно, используя кисть
        Paint p = new Paint();
        float x_start = 0f, y_start = 0f;
        for (int i = 0; i < 4; i++) {
            y_start += 15f;
            for(int j =0; j< 4; j++){
                p.setColor(tiles[i][j]);
                x_start += 15f;
                canvas.drawRect(x_start, y_start, x_start + squareWidth,  y_start + squareHeight, p);
                tilesCoordinates[i][j] = new Pair(x_start, y_start);
                x_start += squareWidth;
            }
            y_start += squareHeight;
            x_start = 0f;
        }
    }

    public void changeTileColor(int i, int j){
        if(0 <= i && i<=3 && 0 <= j && j<=3) {
            Paint p = new Paint();
            if (tiles[i][j] == darkColor) {
                tiles[i][j] = brightColor;
                darkCounter -= 1;
            } else {
                tiles[i][j] = darkColor;
                darkCounter += 1;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3) получить координаты касания
        float x = event.getX();
        float y = event.getY();
        // 4) определить тип события
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            // палец коснулся экрана
            // 5) определить, какой из плиток коснулись
            // изменить её цвет на противоположный
            for (int i = 0; i < 4; i++) {
                for(int j =0; j< 4; j++){
                    if(tilesCoordinates[i][j].x < x && x < squareWidth + tilesCoordinates[i][j].x &&
                            tilesCoordinates[i][j].y < y && y < squareWidth + tilesCoordinates[i][j].y){
                        MyTask task = new MyTask();
                        task.execute(i, j, 80);
                    }
                }
            }
        }
        // 6) проверить, не выиграли ли вы (все плитки одного цвета)

//        invalidate(); // заставляет экран перерисоваться
        return true;
    }
}