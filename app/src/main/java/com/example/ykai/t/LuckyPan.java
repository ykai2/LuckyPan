package com.example.ykai.t;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.security.KeyRep;

/**
 * Created by ykai on 2015/7/21.
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    private Thread thread;
    private boolean isRunning;
    //奖项
    private String[] mStrs=new String []{"单反相机","IPAD","恭喜发财","IPHONE","妹子","恭喜发财"};
    //图片
    private int[]mImgs=new int[]{
            R.drawable.danfan,R.drawable.ipad,R.drawable.f015,
            R.drawable.iphone,R.drawable.meizi,R.drawable.f040
    };
    private int[] mColor=new int[]{
            0xffffc300,0xfff17e01,
            0xffffc300,0xfff17e01,
            0xffffc300,0xfff17e01
    };

    private int mItemCount =6;
    private Bitmap[] mImgsBitmap;

    private RectF mRange=new RectF();
    private int mRadius;
    private Paint mArcPaint;
    private Paint mTextPaint;

    private double mSpeed=0;

    private volatile int mStartAngle=0;

    private boolean isShouldEnd;

    private int mCenter;

    private int mPadding;
    private Bitmap mBgBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg2);
    private float mTextSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,17,getResources().getDisplayMetrics());





    public LuckyPan(Context context)
    {
        this(context, null);
    }

    public LuckyPan(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);


    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding=getPaddingLeft();

        mRadius=width-mPadding*2;
        mCenter=width/2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mArcPaint=new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mTextPaint=new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mRange=new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);

        mImgsBitmap=new Bitmap[mItemCount];

        for(int i=0;i<mItemCount;i++){
            mImgsBitmap[i]=BitmapFactory.decodeResource(getResources(),mImgs[i]);

        }


        isRunning=true;
        thread=new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning=false;

    }

    @Override
    public void run() {
        while (isRunning){
            long start =System.currentTimeMillis();
            draw();
            long end=System.currentTimeMillis();
            if(end-start<30){
                try{
                    Thread.sleep(30-(end-start));
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
        

    }

    private void draw() {
        try {
            mCanvas=mHolder.lockCanvas();
            if(mCanvas!=null)
            {
                drawBg();
                float tmpAngle=mStartAngle;
                float sweepAngle=360/mItemCount;
                for(int i=0;i<mItemCount;i++){
                    mArcPaint.setColor(mColor[i]);

                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    drawIcon(tmpAngle,mImgsBitmap[i]);
                    tmpAngle+=sweepAngle;
                }
                mStartAngle+=mSpeed;

                if(isShouldEnd){
                    mSpeed-=1;

                }
                if(mSpeed<=0){
                    mSpeed=0;
                    isShouldEnd=false;
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    public void luckyStart(){
        mSpeed=50;
        isShouldEnd=false;
    }
    public void lucksEnd(){
        isShouldEnd=true;
    }
    public boolean isStart(){
        return mSpeed!=0;
    }
    public boolean isShouldEnd(){
        return isShouldEnd;
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        int imgWidth=mRadius/8;
        float angle=(float)((tmpAngle+360/mItemCount/2)*Math.PI/180);
        int x=(int)(mCenter+mRadius/2/2*Math.cos(angle));
        int y=(int)(mCenter+mRadius/2/2*Math.sin(angle));

        RectF rectF=new RectF(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rectF,null);

    }

    private void drawText(float tmpAngle, float sweepAngle, String mStr) {

        Path path=new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);
        float textWidth=mTextPaint.measureText(mStr);
        int hOffset=(int)(mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset=mRadius/2/6;
        mCanvas.drawTextOnPath(mStr,path, hOffset,vOffset,mTextPaint);
    }

    private void drawBg() {
        mCanvas.drawColor(0xffffff);
        mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);
    }
}
