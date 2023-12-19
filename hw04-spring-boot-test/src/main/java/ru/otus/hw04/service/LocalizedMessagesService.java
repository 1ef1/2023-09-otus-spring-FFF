package ru.otus.hw04.service;

public interface LocalizedMessagesService {
    String getMessage(String code, Object ...args);
}
