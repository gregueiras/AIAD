package market;

import agents.OurAgent;
import java.io.Serializable;

public class Company implements Serializable {

  private InvestmentType type;
  private String name;
  private boolean closed;
  private Integer price;
  private OurAgent currentOwner;
  private boolean doubleValue;
  private int actualBid; //Might not be needed, its here just in case

  public Company(String name, Integer price, InvestmentType type, boolean doubleValue) {
    this.type = type;
    this.name = name;
    this.price = price;
    this.closed = true;
    this.doubleValue = doubleValue;
  }

  public Company(Company c){
    this.type = c.type;
    this.name = c.name;
    this.price = c.price;
    this.closed = c.closed;
    this.doubleValue = c.doubleValue;
  }



  public InvestmentType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public boolean isDoubleValue() {
    return doubleValue;
  }

  public void setType(InvestmentType type) {
    this.type = type;
  }
}
