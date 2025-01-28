package com.r7b7.tutorial.redis.model;

import java.io.Serializable;

public record User(String id, String name, String email) implements Serializable{

}
