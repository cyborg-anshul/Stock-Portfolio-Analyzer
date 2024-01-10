
package com.crio.warmup.stock.portfolio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  private RestTemplate restTemplate;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
  LocalDate endDate){
//--
    ArrayList<AnnualizedReturn> ar =new ArrayList<>();
    if(portfolioTrades.size() <=0)
    {
      return null; 
    }

    for(PortfolioTrade trade :portfolioTrades){
      ar.add(getAnnualizedReturns(trade,endDate));
    }
    Comparator<AnnualizedReturn> SortByAnnReturn = getComparator();

    Collections.sort(ar,SortByAnnReturn);
    return ar;
  }

//------------
  public AnnualizedReturn getAnnualizedReturns(PortfolioTrade trade, LocalDate endLocalDate){
    String ticker =trade.getSymbol();
    LocalDate startLocalDate =trade.getPurchaseDate();

    if(startLocalDate.compareTo(endLocalDate)>=0){
      throw new RuntimeException();
    }

    //String url =buildUri(ticker,startLocalDate,endLocalDate);

    //RestTemplate restTemplate =new RestTemplate();
try{
    //TiingoCandle[] stockStartToEndDate = restTemplate.getForObject(url, TiingoCandle[].class);
    List<Candle> stockStartToEndDate =getStockQuote(ticker,startLocalDate,endLocalDate);
// Candle candle = new TiingoCandle();
  
      Candle stockStartDate =stockStartToEndDate.get(0);
      Candle stockLatest =stockStartToEndDate.get(stockStartToEndDate.size() -1);
      Double buyPrice =stockStartDate.getOpen();
      Double sellPrice =stockLatest.getClose();

      AnnualizedReturn annualizedReturn =calculateAnnualizedReturns(endLocalDate, trade, buyPrice, sellPrice);
      return annualizedReturn;
    
  } catch(JsonProcessingException e){
    System.out.println(e.getMessage());
    return new AnnualizedReturn(trade.getSymbol(), Double.NaN, Double.NaN);

  }
  }

public AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) 
      {
        double totalReturn=(sellPrice -buyPrice) / buyPrice;
        double numYears = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate) / 365.24;
        double annualizedReturns = Math.pow((1 + totalReturn), (1 / numYears))- 1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturns,totalReturn);
  }


  public static String getToken(){
    return "72448b5873086f23b132897edbc68390116d6da0" ;
  }

//-------------------------------------------------------------------------------------------------------
 private Comparator<AnnualizedReturn> getComparator() {
  return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
}

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonMappingException, JsonProcessingException
       {
    String api = buildUri(symbol, from , to);
   // return Arrays.asList(restTemplate.getForObject(api,TiingoCandle[].class));
   // 1. accept in String response = restTemplate.getForObject(api, String.class);
   // 2. object mapper declaration and registering java time module
   // 3. Candle[] candles= obm.ReadValue(response, TiingoCandle[].class);
   // 4. check for empty liss
   // 5. return Arrays.asList(Candles);
   String response = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
   ObjectMapper objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());
   Candle[] result = objectMapper.readValue(response, TiingoCandle[].class);
   if (result == null) {
     return new ArrayList<>();
   }
   return Arrays.asList(result);
 }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
      //return uriTemplate;
      String token =getToken();
      return "https://api.tiingo.com/tiingo/daily/" + symbol +
      "/prices?startDate=" + startDate + "&endDate=" + endDate + "&token=" + token;
  }
}
