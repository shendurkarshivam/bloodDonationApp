package com.example.blooddonationapp.Service;

import com.example.blooddonationapp.Notifications.MyResponse;
import com.example.blooddonationapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA43YBHIc:APA91bHYZEzN0DUp3wavfKPc7NtynxW4qnqE6laJRJ8MP2ZjOVzQxtVyIwGJ7Gss5BjFImEO44bza4susvrUNwKSEYLfysnyOhIpmmq_MGRs3gjlGG8hzeF6W5OYqTWnT3hjvkNEpnsM"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
