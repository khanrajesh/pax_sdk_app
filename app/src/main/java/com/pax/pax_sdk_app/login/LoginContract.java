package com.pax.pax_sdk_app.login;

public class LoginContract {
    public interface View {
        void fetchedV1Response(boolean status, String response);
    }

    public interface UserInteraction{
        void getV1Response(String base_url);
    }
}
