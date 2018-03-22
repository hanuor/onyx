/*
 * Copyright (C) 2016 Hanuor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hanuor.onyx;

import android.content.Context;

import com.hanuor.onyx.hub.Tags;
import com.hanuor.onyx.hub.Utils.DisposableManager;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Onyx {
    volatile static Tags mTags =  null;
    public static Tags with(Context context){
        mTags = new Tags(context);
        mTags.setInstance(mTags);
        return mTags;
    }

    private static void buildClient(){
        final Disposable disposable = null;
        Single<Boolean> mPerformRequest = Single.fromCallable(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });
        mPerformRequest.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
