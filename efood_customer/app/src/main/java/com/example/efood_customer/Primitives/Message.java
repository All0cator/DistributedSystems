package com.example.efood_customer.Primitives;

import java.io.Serializable;

public class Message implements Serializable {
    public MessageType type;
    public Object payload;
}
