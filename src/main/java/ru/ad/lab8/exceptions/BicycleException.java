package ru.ad.lab8.exceptions;

public class BicycleException extends RuntimeException {
  public BicycleException(String msg) {
    super(msg);
  }

  public BicycleException(Integer id) {
    super("Entity with id = " + id + " not found");
  }

}
