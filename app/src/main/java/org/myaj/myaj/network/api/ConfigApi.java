package org.myaj.myaj.network.api;

import org.myaj.myaj.network.entity.config.Config;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Stardust on 2017/10/26.
 */

public interface ConfigApi {

    @GET("/api/config")
    Observable<Config> getConfig();

}
