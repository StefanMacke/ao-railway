package com.example;

import net.aokv.railway.result.Result;

public class User {

    String name;

    public User(String name) {
        this.name = name;
    }

    public Boolean isCorrectPassword(Password oldPassword) {
        return true;
    }

    public Result<?, String> changePassword(Password newPassword) {
        return Result.withValue(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
