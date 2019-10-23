package market;

public class Company {

  private COMPANY_RISK type;
  private String name;
  private boolean closed;
  private Integer price;

  public COMPANY_RISK getType() {
    return type;
  }

  public void setType(COMPANY_RISK type) {
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

  public Company(COMPANY_RISK type, String name, Integer price) {
    this.type = type;
    this.name = name;
    this.price = price;
    this.closed = true;
  }
}
