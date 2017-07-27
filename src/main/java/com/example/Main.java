package com.example;

import net.aokv.railway.result.Result;

public class Main {

    static UserRepo userRepo;

    public static void main(String args[]) {
        userRepo = new UserRepo();
        Result<?, String> result = changePassword("User", new Password("oldSecret"), new Password("newSecret"));
        System.out.println(result);
    }

    public static Result<?, String> changePassword(
            String username, Password oldPassword, Password newPassword) {
        return Result.combine(
                Result.with(username, "Username cannot be empty"),
                Result.with(oldPassword, "Old password cannot be empty"),
                Result.with(newPassword, "New password cannot be empty"))
                .onSuccess(() -> userRepo.find(username))
                .ensure(user -> user.isCorrectPassword(oldPassword), "Invalid password")
                .onSuccess(user -> user.changePassword(newPassword))
                .onSuccess(user -> userRepo.update(user))
                .onFailure(() -> System.out.println("Password could not be changed"))
                .map(user -> user);
    }
}
