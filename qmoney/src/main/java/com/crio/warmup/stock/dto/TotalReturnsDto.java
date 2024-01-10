
package com.crio.warmup.stock.dto;

// -----implements Comparable<TotalReturnsDto>
public class TotalReturnsDto implements Comparable<TotalReturnsDto>{

  private String symbol;
  private Double closingPrice;

  public TotalReturnsDto(String symbol, Double closingPrice) {
    this.symbol = symbol;
    this.closingPrice = closingPrice;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Double getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(Double closingPrice) {
    this.closingPrice = closingPrice;
  }
//-----------------------------------------------------
  @Override
  public int compareTo(TotalReturnsDto trd){
    return (this.getClosingPrice() < trd.getClosingPrice()?-1 : (this.getClosingPrice()==trd.getClosingPrice()?0:1));
  }
  //-----------------------------------------
}
