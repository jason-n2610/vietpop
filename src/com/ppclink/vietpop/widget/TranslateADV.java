/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ppclink.vietpop.widget;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class TranslateADV extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterY;
    private Camera mCamera;

    public TranslateADV(float fromDegrees, float toDegrees,
            float centerX, float centerY) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterY = centerY;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
        final float centerY = mCenterY;
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();
        camera.save();
        camera.translate(0, degrees, 0);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(0, -centerY);
        matrix.postTranslate(0, centerY);
    }
}
