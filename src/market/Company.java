package market;

import agents.AgentInvestor;
import agents.OurAgent;
import jade.core.AID;

import java.io.Serializable;

public class Company implements Serializable {

  private InvestmentType type;
  private String name;
  private boolean closed;
  private Integer price; //price for which it was sold
  private AID currentOwner; // investor who bought it
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
    this.currentOwner = c.getCurrentOwner();
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

  @Override
  public String toString() {
    return "Company{" +
        "type=" + type +
        ", name='" + name + '\'' +
        ", price=" + price + ", owner=" + currentOwner +
        '}';
  }

  public AID getCurrentOwner() {
    return currentOwner;
  }

  public void setCurrentOwner(AID currentOwner) {
    this.currentOwner = currentOwner;
  }
}
