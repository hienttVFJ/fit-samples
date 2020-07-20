package com.google.android.gms.fit.samples.stepcounterkotlin;

import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

/**
 * Created by Tran The Hien on 20,July,2020
 */
public class Constant {
    public static final Scope SCOPE_ACTIVITY_READ_WRITE = Fitness.SCOPE_ACTIVITY_READ_WRITE;
    public static final Scope SCOPE_BODY_READ_WRITE = Fitness.SCOPE_BODY_READ_WRITE;
    public static final Scope USER_PROFILE_SCOPE = new Scope("https://www.googleapis.com/auth/userinfo.profile");
    public static final Scope USER_EMAIL_SCOPE = new Scope("https://www.googleapis.com/auth/userinfo.email");
    public static final Scope[] TOTAL_SCOPES = new Scope[]{
            SCOPE_ACTIVITY_READ_WRITE, SCOPE_BODY_READ_WRITE, USER_PROFILE_SCOPE, USER_EMAIL_SCOPE
    };
}
