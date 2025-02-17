package com.reactnativetiktok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.logging.Logger;

public class TikTokEntryActivity extends ReactActivity implements IApiEventHandler {

    private static Logger LOGGER = Logger.getLogger(TikTokEntryActivity.class.getName());

    TikTokOpenApi ttOpenApi;

    public void onReq(BaseReq baseReq) {}

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        TikTokOpenApi create = TikTokOpenApiFactory.create(this);
        this.ttOpenApi = create;
        create.handleIntent(getIntent(), this);
    }

    public void onResp(BaseResp baseResp) {
        LOGGER.info("Recieved response of type " + baseResp.getClass().getName());
        if (baseResp instanceof Authorization.Response) {
            Authorization.Response response = (Authorization.Response) baseResp;
            WritableMap params = Arguments.createMap();
            LOGGER.info("Auth response received with error code " + response.errorCode);
            LOGGER.info("Auth response received with auth code " + response.authCode);
            LOGGER.info("Auth response received with permissions " + response.grantedPermissions);
            LOGGER.info("Auth response received with state " + response.state);
            params.putInt("status", response.errorCode);
            params.putString("code", response.authCode);
            ReactContext ctx = getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onAuthCompleted", params);
            System.out.println("Auth completed");
        } else if (baseResp instanceof Share.Response) {
            Share.Response response = (Share.Response) baseResp;
            ReactContext ctx = getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onShareCompleted", response.subErrorCode);
        }
        finish();
    }

    public void onErrorIntent(Intent intent) {
        finish();
    }
}
