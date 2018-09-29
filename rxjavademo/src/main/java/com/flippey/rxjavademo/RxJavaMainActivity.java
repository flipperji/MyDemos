package com.flippey.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxJavaMainActivity extends AppCompatActivity {

    public static String TAG = "RxJavaMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rxjavademo_activity_main);


        rxJavaFun1();
    }

    private void rxJavaFun1() {
        //创建被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "observable: subscribe方法" );
            }
        });

        //创建观察者
        Observer<Integer> observer = new Observer<Integer>(){

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "observer: onSubscribe  开始采用subscribe连接" );

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "observer: onNext  对onNext事件的int"+integer+"作出反应" );

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "observer: onError" );

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "observer: onComplete" );

            }
        };


        observable.subscribe(observer);
    }
}
