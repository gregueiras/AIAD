package market;

import agents.OurAgent;

public class Company {

  private CompanyRisk type;
  private InvestmentType invType; // One of these might be redundant
  private String name;
  private boolean closed;
  private Integer price;
  private OurAgent currentOwner;
  private boolean doubleValue;
  private int actualBid; //Might not be needed, its here just in case

  public CompanyRisk getType() {
    return type;
  }

  public void setType(CompanyRisk type) {
    this.type = type;
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

  public Company(CompanyRisk type, String name, Integer price, InvestmentType invType, boolean doubleValue) {
    this.type = type;
    this.name = name;
    this.price = price;
    this.closed = true;
    this.invType = invType;
    this.doubleValue = doubleValue;
  }
}
